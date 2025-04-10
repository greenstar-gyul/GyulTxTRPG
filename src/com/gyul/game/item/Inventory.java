package com.gyul.game.item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class Inventory {
    private List<Item> equipment;
    private Map<Integer, Integer> consumtion;
    private Map<Integer, Item> consInfo; // 소비아이템 코드에 해당하는 아이템이 뭔지
    private ItemDao iDao;

    public Inventory() {
        equipment = new ArrayList<>();
        consumtion = new HashMap<>();
        consInfo = new HashMap<>();
        iDao = new ItemDao();
    }

    public Inventory(String userID) {

    }

    public void addInventory(Item item) {
        if (item instanceof Weapon) {
            equipment.add(item);
        } else if (item instanceof Consume) {
            int key = item.getItemCode(); // 아이템 코드 기반 키
            consumtion.put(key, consumtion.getOrDefault(key, 0) + 1);
            consInfo.putIfAbsent(key, item);
        }
    }

    /**
     * 소비 아이템 전용 개수 차감 및 삭제
     * 
     * @param item 소비 아이템
     * 
     */
    public boolean removeItem(Item item) {
        if (item instanceof Consume) {
            consumtion.replace(item.getItemCode(), consumtion.get(item.getItemCode()) - 1);
            if (consumtion.get(item.getItemCode()) <= 0) {
                consumtion.remove(item.getItemCode());
                consInfo.remove(item.getItemCode());
            }
            
            return true;
        }
        
        return false;
    }

    // 장비 아이템 제거
    public void removeItem(int Idx) {
        equipment.remove(Idx);
    }

    /**
     * 현재 인벤토리의 장비 아이템들을 문자열 리스트로 반환
     */
    public List<String> getEquipList() {
        List<String> equipList = new ArrayList<>();
        
        for (Item item : equipment) {
            equipList.add(item.toString());
        }

        return equipList;
    }

    /**
     * 현재 인벤토리의 소모품들을 문자열 리스트로 반환
     */
    public List<String> getConsList() {
        List<String> consList = new ArrayList<>();
        Set<Entry<Integer, Integer>> cset = consumtion.entrySet();
        for (Entry<Integer, Integer> item : cset) {
            int itemKey = item.getKey();
            consList.add(consInfo.get(itemKey).toString() + " " + item.getValue());
        }

        return consList;
    }

    /**
     * 1. 인벤토리에 없는 장비 DB에서 제거 -> 있는 장비면 정보만 갱신
     * 2. DB에 없는 장비 추가
     * 3. 인벤토리에 없는 소모품 DB에서 제거 -> 있는 소모품이면 정보만 갱신
     * 4. DB에 없는 소모품 추가
     * @param userID
     */
    public boolean saveInventory(String userID) {
//        System.out.println("인벤토리 저장 시작");
        // 1. 인벤토리에 없는 장비 DB에서 제거 -> 있는 장비면 정보만 갱신
        List<Integer> equipCodes = iDao.getAllEquipCode(userID);
//        System.out.println("1단계");
        for (int code : equipCodes) {
//            System.out.println(code);
            Item item = findEquip(code);
            if (item != null) { // null이 아니란건 DB에 저장된 장비를 지금 가지고 있단 뜻 => 착용 여부만 변경
                if (!iDao.changeIsEquip(item))
                    return false;
            }
            else { // 없다는건 삭제돼야할 장비란뜻
                if (!iDao.removeEquip(code))
                    return false;
            }
//            System.out.println("완료");
        }

        // 2. DB에 없는 장비 추가
//        System.out.println("2단계");
        for (Item equip : equipment) {
            if (equip.hashCode() == 0) { // equip_code가 0인건 DB에 저장된게 아니란 뜻
                if (!iDao.insertEquip(userID, equip))
                    return false;
            }
        }

        // 3. 인벤토리에 없는 소모품 DB에서 제거 -> 있는 소모품이면 정보만 갱신
        List<Integer> itemCodes = iDao.getAllConsItemCode(userID);
//        System.out.println("3단계");
        for (int code : itemCodes) {
            Item item = findCons(code);
            if (item != null) {
                if (!iDao.updateConsumption(userID, item, consumtion.get(code))) {
//                    System.out.println("갱신 실패");
                    return false;
                }
                else {
//                    System.out.println("갱신 성공");
                }
            }
            else {
                if (!iDao.removeCons(userID, code)) {
//                    System.out.println("삭제 실패");
                    return false;
                }
                else {
//                    System.out.println("삭제 성공");
                }
            }
        }

        // 4. DB에 없는 소모품 추가
        Set<Entry<Integer, Item>> es = consInfo.entrySet();
//        System.out.println("4단계");
        for (Entry<Integer, Item> it : es) {
            int itemCode = it.getKey();
            Item item = it.getValue();
            if (item.hashCode() == 0) { // data_code가 0인건 DB에 저장된게 아니란 뜻
                if (!iDao.insertConsumption(userID, item, consumtion.get(itemCode))) {
                    return false;
                }
            }
        }
        
//        System.out.println("저장 성공");

        return true;
    }

    /**
     * 
     * DB에 저장된 장비가 지금 인벤토리에 있는지 여부
     * @param equipCode
     * 찾을 아이템 코드
     * @return
     * true : DB에 저장된 장비가 현재 인벤토리에도 있음
     * false : 없음
     */
    private Item findEquip(int equipCode) {
        for (Item item : equipment) {
            int itemECode = ((Weapon)item).getEquipCode();
            if (itemECode == equipCode)
                return item;
        }

        return null;
    }

    /**
     *  
     * DB에 저장된 소모품이 지금 인벤토리에 있는지 여부
     * @param itemCode
     * 찾을 소모품의 itemCode
     * @return
     * 있으면 Item, 없으면 null 반환
     */
    private Item findCons(int itemCode) {
        return consInfo.get(itemCode);
    }

    public void loadInventory(String userID) {
        equipment = iDao.loadAllEquipment(userID);
        consInfo = iDao.loadAllConsume(userID);
        consumtion = iDao.loadAllConsumeCnt(userID);
    }
    
    public boolean equipOrRelease(int itemIdx) {
        Weapon item = (Weapon) equipment.get(itemIdx);
        if (item.getIsEquipped()) {
            item.setIsEquipped(false);
            return false;
        }
        else {
            equipEquip(itemIdx);
            return true;
        }
    }
    
    private void equipEquip(int itemIdx) {
        Weapon weapon = (Weapon) equipment.get(itemIdx);
        for (Item item : equipment) {
            Weapon temp = (Weapon) item;
            if (temp.getIsEquipped()) {
                temp.setIsEquipped(false);
                break;
            }
        }
        weapon.setIsEquipped(true);
    }
    
    public Item getEquippedItem() {
        for (Item item : equipment) {
            Weapon temp = (Weapon) item;
            if (temp.getIsEquipped())
                return item;
        }
        return null;
    }
}

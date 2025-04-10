package com.gyul.game.item;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.gyul.game.MainApp;

public class ItemUtils {
    public static final String JSON_PATH = MainApp.JSON_PATH + "items.json";

    private static Map<String, Map<Integer, Item>> itemInfo;

    private static ItemUtils itemUtils = new ItemUtils();
    
    private static int[] equipCodesTable = { 1000, 1001, 1002, 1003, 1004 };
    private static int[] potionCodesTable = { 7001, 7002, 7003, 7004 };

    private ItemUtils() {
        Initialize();
    }

    public static ItemUtils getInstance() {
        return itemUtils;
    }

    private static void Initialize() {
        if (itemInfo == null) {
            try {
                Reader reader = new InputStreamReader(new FileInputStream(ItemUtils.JSON_PATH), "UTF-8");
                Type type = new TypeToken<Map<String, Map<String, JsonObject>>>() {}.getType();
                Gson gson = new Gson();
                Map<String, Map<String, JsonObject>> rawData = gson.fromJson(reader, type);

                itemInfo = new HashMap<>();
                for (Map.Entry<String, Map<String, JsonObject>> entry : rawData.entrySet()) {
                    String category = entry.getKey();
                    Map<Integer, Item> itemMap = new HashMap<>();

                    for (Map.Entry<String, JsonObject> itemEntry : entry.getValue().entrySet()) {
                        int code = Integer.parseInt(itemEntry.getKey());
                        JsonObject obj = itemEntry.getValue();
                        Item item = parseItem(category, obj);
                        itemMap.put(code, item);
                    }

                    itemInfo.put(category, itemMap);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static Item parseItem(String category, JsonObject obj) {
        int code = obj.get("code").getAsInt();
        String name = obj.get("name").getAsString();
        int buy = obj.get("buy").getAsInt();
        int sell = obj.get("sell").getAsInt();

        switch (category) {
            case "Weapon":
                int atk = obj.get("atk").getAsInt();
                int reqlv = obj.get("reqlv").getAsInt();
                return new Weapon(code, name, buy, sell, atk, reqlv);

            case "Potion":
                int healp = obj.get("healp").getAsInt();
                return new Consume(code, name, buy, sell, healp);

            default:
                throw new IllegalArgumentException("알 수 없는 아이템 타입: " + category);
        }
    }

    public static Item getItem(int code) {
        for (Map<Integer, Item> items : itemInfo.values()) {
            if (items.containsKey(code)) {
                return items.get(code);
            }
        }
        return null;
    }
    
    public static Item getItem(String name) {
        for (Map<Integer, Item> items : itemInfo.values()) {
            for (Item item : items.values()) {
                if (item.getName().equals(name)) {
                    return item;
                }
            }
        }
        return null;
    }

    public static Map<String, Map<Integer, Item>> getAllItems() {
        return itemInfo;
    }
    
    public static Weapon getRandomWeapon(String userClass) {
        int itemCode = 0;
        
        int re = (int) (Math.random() * 1000) + 1; // [1, 1001)
        
        if (re <= 780) {  // 78%
            itemCode = equipCodesTable[0];
        }
        else if (re <= 900) {  // 12%
            itemCode = equipCodesTable[1];
        }
        else if (re <= 970) {  // 7%
            itemCode = equipCodesTable[2];
        }
        else if (re <= 999) {  // 2.9%
            itemCode = equipCodesTable[3];
        }
        else { // 0.1%
            itemCode = equipCodesTable[4];
        }
        
        if (userClass.equals("궁수")) {
            itemCode += 1000;
        }
        
        Item item = ItemUtils.getItem(itemCode).clone();
        Weapon w = (Weapon) item;
        w.setIsEquipped(false);
        
        return w;
    }
    
    public static Item getRandomPotion() {
        int itemCode = 0;
        
        int re = (int) (Math.random() * 1000) + 1; // [1, 1001)
        
        if (re <= 350) {  // 35%
            itemCode = potionCodesTable[0];
        }
        else if (re <= 475) {  // 12.5%
            itemCode = potionCodesTable[1];
        }
        else if (re <= 525) {  // 5%
            itemCode = potionCodesTable[2];
        }
        else if (re <= 530){ // 0.5%
            itemCode = potionCodesTable[3];
        }
        else { // 47%
            return null;
        }
        
        Item item = ItemUtils.getItem(itemCode).clone();
        
        return item;
    }
}

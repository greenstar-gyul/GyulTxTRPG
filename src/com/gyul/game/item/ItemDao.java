package com.gyul.game.item;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gyul.game.dao.GyulDao;


public class ItemDao extends GyulDao {
    
    // TODO 장비 전용 공간

    /**
     * 인벤토리DB에 장비아이템 추가
     * 
     * @param userID
     * @param item
     * @return
     */
    public boolean insertEquip(String userID, Item item) {
        Connection conn = getConnect();
        String insertQ = "INSERT INTO inventory_tbl(data_code, user_id, item_code, item_cnt, equipped, equip_code)" //
                + "       VALUES                   (inventory_seq.nextval, ?, ?, 1, ?, ?)";

        int equipCode = getLastEquipCode();
        equipCode += 1;
            
        
        String equipped = ((Weapon) item).getIsEquipped() ? "Y" : "N";

        try {
            PreparedStatement psmt = conn.prepareStatement(insertQ);
            psmt.setString(1, userID);
            psmt.setInt(2, item.getItemCode());
            psmt.setString(3, equipped);
            psmt.setInt(4, equipCode);
            int r = psmt.executeUpdate();
            if (r > 0) {
                ((Weapon) item).setEquipCode(equipCode);
                item.setDataCode(getDataCode(equipCode));
                conn.close();
                return true;
            }
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    

    /**
     * 장비 아이템을 저장할 때 마지막 equip_code 가져오기
     * 
     * @return
     * 마지막 equip코드 반환
     * 저장된 장비가 없다면 -1 반환
     */
    public int getLastEquipCode() {
        String selectQ = "SELECT MAX(equip_code) AS code" //
                + "       FROM   inventory_tbl" //
                + "       WHERE  equip_code IS NOT NULL";
        try {
            Connection conn = getConnect();

            PreparedStatement psmt = conn.prepareStatement(selectQ);
            ResultSet rs = psmt.executeQuery();
            if (rs.next()) {
                int code = rs.getInt("code");
                conn.close();
                return code;
            }
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    /**
     * 장비의 데이터코드 가져오기(equip_code로 검색)
     * Only 장비아이템 !!!
     * @param equipCode
     * @return
     * 해당 장비아이템의 datacode
     * 조회실패 시 -1 반환
     */
    public int getDataCode(int equipCode) {
        String selectQ = "SELECT data_code" //
                + "       FROM   inventory_tbl" //
                + "       WHERE  equip_code = ?"; //
        try {
            Connection conn = getConnect();

            PreparedStatement psmt = conn.prepareStatement(selectQ);
            psmt.setInt(1, equipCode);
            ResultSet rs = psmt.executeQuery();
            if (rs.next()) {
                int dataCode = rs.getInt("data_code");
                conn.close();
                return dataCode;
            }
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    /**
     * 장비 아이템 DB에서 제거
     * 
     * @param item
     * @return
     */
    public Boolean removeEquip(int equipCode) {
        Connection conn = getConnect();
        String deleteQ = "DELETE inventory_tbl" //
                + "       WHERE  equip_code = ?";//

        try {
            PreparedStatement psmt = conn.prepareStatement(deleteQ);
            psmt.setInt(1, equipCode);
            int r = psmt.executeUpdate();
            if (r > 0) {
                conn.close();
                return true;
            }
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        
        return false;
    }

    /**
     * 현재 플레이어의 DB에 있는 모든 장비 리스트 가져오기
     * @param userID
     * @return
     */
    public List<Integer> getAllEquipCode(String userID) {
        List<Integer> itemList = new ArrayList<>();

        String selectQ = "SELECT equip_code" //
                + "       FROM   inventory_tbl" //
                + "       WHERE  user_id = LOWER(?)"
                + "         AND  equip_code IS NOT NULL";

        try {
            Connection conn = getConnect();

            PreparedStatement psmt = conn.prepareStatement(selectQ);
            psmt.setString(1, userID);
            ResultSet rs = psmt.executeQuery();
            while (rs.next()) {
                itemList.add(rs.getInt("equip_code"));
//                System.out.println("equip_code : " + rs.getInt("equip_code"));
            }
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return itemList;
    }

    /**
     * 장비 착용 여부 변경
     * @return
     */
    public boolean changeIsEquip(Item item) {
        Connection conn = getConnect();
        String updateQ = "Update inventory_tbl"//
                + "       SET    equipped = ?" //
                + "       WHERE  equip_code = ?";
        String equipped = ((Weapon) item).getIsEquipped() ? "Y" : "N";

        int equipCode = ((Weapon)item).getEquipCode();
        try {
            PreparedStatement psmt = conn.prepareStatement(updateQ);
            psmt.setString(1, equipped);
            psmt.setInt(2, equipCode);
            int r = psmt.executeUpdate();
            if (r > 0) {
                conn.close();
                return true;
            }
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
    
    // 장비 전용 공간 END

    
    // TODO 소비 전용 공간
    
    /**
     * 소비아이템의 데이터코드 가져오기(user_id와 item_code로 검색)
     * Only 소비아이템 !!!
     * @param userID
     * @param itemCode
     * @return
     */
    private int getDataCode(String userID, int itemCode) {
        String selectQ = "SELECT data_code" //
                + "       FROM   inventory_tbl" //
                + "       WHERE  user_id = LOWER(?)" //
                + "         AND  item_code = ?"; //
        try {
            Connection conn = getConnect();

            PreparedStatement psmt = conn.prepareStatement(selectQ);
            psmt.setString(1, userID);
            psmt.setInt(2, itemCode);
            ResultSet rs = psmt.executeQuery();
            if (rs.next()) {
                int dataCode = rs.getInt("data_code");
                conn.close();
                return dataCode;
            }
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    /**
     * 인벤토리DB에 없는 소비아이템 추가
     * 
     * @param userID
     * @param item
     * @param itemCnt
     * @return
     */
    public boolean insertConsumption(String userID, Item item, int itemCnt) {
        String insertQ = "INSERT INTO inventory_tbl(data_code, user_id, item_code, item_cnt, equipped)" //
                + "       VALUES                   (inventory_seq.nextval, ?, ?, ?, 'N')";

        int itemCode = item.getItemCode();
        
        try {
            Connection conn = getConnect();

            PreparedStatement psmt = conn.prepareStatement(insertQ);
            psmt.setString(1, userID);
            psmt.setInt(2, itemCode);
            psmt.setInt(3, itemCnt);
            int r = psmt.executeUpdate();
            if (r > 0) {
                item.setDataCode(getDataCode(userID, itemCode));
                conn.close();
                return true;
            }
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 인벤토리DB에 있는 소비아이템의 수 변화
     * @param userID
     * @param dataCode
     * @param itemCnt
     * @return
     */
    public boolean updateConsumption(String userID, Item item, int itemCnt) {
        String updateQ = "UPDATE inventory_tbl" //
                + "       SET    item_cnt = ?" //
                + "       WHERE  user_id = LOWER(?)" //
                + "         AND  item_code = ?"; //

        try {
            Connection conn = getConnect();
            PreparedStatement psmt = conn.prepareStatement(updateQ);
            psmt.setInt(1, itemCnt);
            psmt.setString(2, userID);
            psmt.setInt(3, item.getItemCode());
            int r = psmt.executeUpdate();
            if (r > 0) {
                conn.close();
                return true;
            }
            conn.close();

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return false;
    }

    // /**
    //  * 인벤토리 DB에 해당 아이템 등록되어 있는지 확인 (소비아이템 전용)
    //  * @param userID
    //  * @param dataCode
    //  * @return
    //  * true : 있음
    //  * false : 없음
    //  * 
    //  */
    // private boolean findData(String userID, int dataCode) {
    //     Connection conn = getConnect();

    //     String findQ = "SELECT 1" //
    //             + "     FROM   dual" //
    //             + "     WHERE  EXISTS(" //
    //             + "     SELECT 1" //
    //             + "     FROM   inventory_tbl" //
    //             + "     WHERE  user_id = LOWER(?)" //
    //             + "       AND  data_code = ?";

    //     try {
    //         PreparedStatement psmt = conn.prepareStatement(findQ);
    //         psmt.setString(1, userID);
    //         psmt.setInt(2, dataCode);
    //         ResultSet rs = psmt.executeQuery();
    //         if (rs.next()) {
    //             return true;
    //         }

    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }

    //     return false;
    // }

    /**
     * DB에 저장된 모든 소모품의 data_code 리스트 반환
     * @param userID
     * @return
     */
    public List<Integer> getAllConsItemCode(String userID) {
        Connection conn = getConnect();
        String selectQ = "SELECT item_code"//
                + "       FROM   inventory_tbl"
                + "       WHERE  user_id = LOWER(?)"
                + "         AND  equip_code IS NULL";

        List<Integer> codeList = new ArrayList<>();

        try {
            PreparedStatement psmt = conn.prepareStatement(selectQ);
            psmt.setString(1, userID);
            ResultSet rs = psmt.executeQuery();
            while (rs.next()) {
                codeList.add(rs.getInt("item_code"));
            }
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return codeList;
    }

    /**
     * DB에 저장된 소모품 제거
     * @param userID
     * @param itemCode
     * 제거할 소모품의 item_code
     * @return
     */
    public boolean removeCons(String userID, int itemCode) {
        Connection conn = getConnect();
        String deleteQ = "DELETE inventory_tbl" //
                + "       WHERE  item_code = ?";//

        try {
            PreparedStatement psmt = conn.prepareStatement(deleteQ);
            psmt.setInt(1, itemCode);
            int r = psmt.executeUpdate();
            if (r > 0) {
                conn.close();
                return true;
            }
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // TODO 인벤토리 불러오기

    public List<Item> loadAllEquipment(String userID) {
        String selectQ = "SELECT data_code, item_code, equipped, equip_code" //
                + "       FROM   inventory_tbl" //
                + "       WHERE  user_id = LOWER(?)" //
                + "         AND  equip_code IS NOT NULL";
        
        List<Item> equipList = new ArrayList<>();
        Connection conn = getConnect();

        try {
            PreparedStatement psmt = conn.prepareStatement(selectQ);
            psmt.setString(1, userID);
            ResultSet rs = psmt.executeQuery();
            while (rs.next()) {
                int itemCode = rs.getInt("item_code");
                Weapon item = (Weapon) ItemUtils.getItem(itemCode);

                if (item instanceof Weapon) {
                    item.setDataCode(rs.getInt("data_code"));
                    item.setEquipCode(rs.getInt("equip_code"));
                    Boolean isEquipped = rs.getString("equipped").equals("Y") ? true : false;
                    item.setIsEquipped(isEquipped);
                    equipList.add(item);
                }

            }
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return equipList;
    }

    public Map<Integer, Integer> loadAllConsumeCnt(String userID) {
        String selectQ = "SELECT item_code, item_cnt" //
                + "       FROM   inventory_tbl" //
                + "       WHERE  user_id = LOWER(?)" //
                + "         AND  equip_code IS NULL";
        
        Map<Integer, Integer> consumtion = new HashMap<>();
        
        Connection conn = getConnect();

        try {
            PreparedStatement psmt = conn.prepareStatement(selectQ);
            psmt.setString(1, userID);
            ResultSet rs = psmt.executeQuery();
            while (rs.next()) {
                int itemCode = rs.getInt("item_code");
                int itemCnt = rs.getInt("item_cnt");
                consumtion.put(itemCode, itemCnt);
            }
            conn.close();
            

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return consumtion;
    }

    public Map<Integer, Item> loadAllConsume(String userID) {
        String selectQ = "SELECT data_code, item_code" //
                + "       FROM   inventory_tbl" //
                + "       WHERE  user_id = LOWER(?)" //
                + "         AND  equip_code IS NULL";
        
        Map<Integer, Item> consInfo = new HashMap<>();
        
        Connection conn = getConnect();

        try {
            PreparedStatement psmt = conn.prepareStatement(selectQ);
            psmt.setString(1, userID);
            ResultSet rs = psmt.executeQuery();
            while (rs.next()) {
                int itemCode = rs.getInt("item_code");
                Consume item = (Consume) (ItemUtils.getItem(itemCode).clone());
                item.setDataCode(rs.getInt("data_code"));
                consInfo.put(itemCode, item);

            }
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return consInfo;
    }
}

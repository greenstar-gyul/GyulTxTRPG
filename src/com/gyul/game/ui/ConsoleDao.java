package com.gyul.game.ui;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;

import com.gyul.game.dao.GyulDao;
import com.gyul.game.entity.EntityUtils;
import com.gyul.game.item.Item;
import com.gyul.game.item.ItemDao;
import com.gyul.game.item.ItemUtils;
import com.gyul.game.item.Weapon;

public class ConsoleDao extends GyulDao {
    public boolean findID(String userID) {
        Connection conn = getConnect();

        String findQ = "SELECT 1" //
                + "     FROM   dual" //
                + "     WHERE  EXISTS(" //
                + "     SELECT 1" //
                + "     FROM   user_tbl" //
                + "     WHERE  user_id = LOWER(?))";

        try {
            PreparedStatement psmt = conn.prepareStatement(findQ);
            psmt.setString(1, userID);
            ResultSet rs = psmt.executeQuery();
            if (rs.next()) {
                conn.close();
                return true;
            }
            
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
    
    public String login(String userID, String userPW) {
        Connection conn = getConnect();

        String findQ = "SELECT user_name" //
                + "     FROM   user_tbl" //
                + "     WHERE  user_id = LOWER(?)"
                + "       AND  user_pw = ?";

        try {
            PreparedStatement psmt = conn.prepareStatement(findQ);
            psmt.setString(1, userID);
            psmt.setString(2, userPW);
            ResultSet rs = psmt.executeQuery();
            if (rs.next()) {
                String userName = rs.getString("user_name");
                conn.close();
                return userName;
            }
            
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
    
    public boolean registUser(String userID, String userPW, String userName, String userClass) {
        Connection conn = getConnect();
        Map<String, Integer> stat = EntityUtils.getStat(userClass, 1).getLevelStats();

        String insertQ = "INSERT INTO user_tbl(user_id, user_pw, user_name, main_class, user_level, health, mana, attack, user_exp, money)" //
                + "       VALUES              (?, ?, ?, ?, 1, ?, 20, ?, 0, 0)";

        Weapon w = ItemUtils.getRandomWeapon(userClass);
        
        try {
            PreparedStatement psmt = conn.prepareStatement(insertQ);
            psmt.setString(1, userID);
            psmt.setString(2, userPW);
            psmt.setString(3, userName);
            psmt.setString(4, userClass);
            psmt.setInt(5, stat.get("hp"));
            psmt.setInt(6, stat.get("dmg") + w.getAtk());
            int r = psmt.executeUpdate();
            if (r > 0) {
                conn.close();
                return initSave(userID, w);
            }
            
            conn.close();

        } catch (Exception e) {
//            System.out.println("계정 등록 중 오류가 발생했습니다.");
            e.printStackTrace();
        }
        
        return false;
    }
    
    private boolean initSave(String userID, Weapon w) {
        Connection conn = getConnect();

        String insertQ = "INSERT INTO save_tbl(user_id, progress)" //
                + "       VALUES              (?, 1001)";

        try {
            PreparedStatement psmt = conn.prepareStatement(insertQ);
            psmt.setString(1, userID);
            int r = psmt.executeUpdate();
            if (r > 0) {
                conn.close();
                return initInven(userID, w);
            }

            conn.close();
        } catch (Exception e) {
//            System.out.println("계정 등록 중 오류가 발생했습니다.");
            e.printStackTrace();
        }
        
        return false;
    }
    
    private boolean initInven(String userID, Weapon w) {
        ItemDao iDao = new ItemDao();
        
        w.setIsEquipped(true);
        Item potion1 = ItemUtils.getItem(7001);
        Item potion2 = ItemUtils.getItem(7002);
        
        return iDao.insertEquip(userID, w) && iDao.insertConsumption(userID, potion1, 3) && iDao.insertConsumption(userID, potion2, 1);
        
    }
}

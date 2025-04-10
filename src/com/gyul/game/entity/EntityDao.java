package com.gyul.game.entity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.gyul.game.dao.GyulDao;
import com.gyul.game.item.Inventory;

public class EntityDao extends GyulDao {

    public Player loadPlayer(String userID) {
        Player player = null;
        Connection conn = getConnect();

        String findQ = "SELECT u.user_name user_name," //
                + "            u.main_class main_class," //
                + "            u.user_level user_level," //
                + "            u.health health, " //
                + "            u.mana mana, " //
                + "            u.attack attack, " //
                + "            u.user_exp user_exp, " //
                + "            u.money money, " //
                + "            s.progress progress" //
                + "     FROM   user_tbl u JOIN save_tbl s" //
                + "                         ON u.user_id = s.user_id" //
                + "     WHERE  u.user_id = LOWER(?)";

        try {
            PreparedStatement psmt = conn.prepareStatement(findQ);
            psmt.setString(1, userID);
            ResultSet rs = psmt.executeQuery();
            if (rs.next()) {
                String name = rs.getString("user_name");
                String job = rs.getString("main_class");
                int level = rs.getInt("user_level");
                int health = rs.getInt("health");
                int mana = rs.getInt("mana");
                int attack = rs.getInt("attack");
                int exp = rs.getInt("user_exp");
                int money = rs.getInt("money");
                int progress = rs.getInt("progress");
                conn.close();
                return new Player(name, health, attack, level, userID, job, exp, money, progress);
            }
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return player;
    }

    public boolean savePlayer(Player player) {
        Connection conn = getConnect();
        String saveQ = "UPDATE user_tbl " //
                + "     SET    user_level = ?," //
                + "            health = ?," //
                + "            attack = ?," //
                + "            user_exp = ?," //
                + "            money = ?"
                + "     WHERE  user_id = LOWER(?)";

        String userID = player.getUserID();

        try {
            PreparedStatement pstm = conn.prepareStatement(saveQ);
            pstm.setInt(1, player.getLevel());
            pstm.setInt(2, player.getHP());
            pstm.setInt(3, player.getAtk());
            pstm.setInt(4, player.getExp());
            pstm.setInt(5, player.getMoney());
            pstm.setString(6, userID);
            int r = pstm.executeUpdate();
            if (r > 0) {
                conn.close();
                return saveProgress(userID, player.getProgress()) && saveInventory(player);
            }

            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    private boolean saveProgress(String userID, int progress) {
        Connection conn = getConnect();
        String saveQ = "UPDATE save_tbl " //
                + "     SET    progress = ?" //
                + "     WHERE  user_id = LOWER(?)";

        try {
            PreparedStatement pstm = conn.prepareStatement(saveQ);
            pstm.setInt(1, progress);
            pstm.setString(2, userID);
            int r = pstm.executeUpdate();
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

    private boolean saveInventory(Player player) {
        Inventory inventory = player.getInventory();
        return inventory.saveInventory(player.getUserID());
    }
}

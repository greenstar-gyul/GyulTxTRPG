package com.gyul.game.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class GyulDao {
    public static final String DB_URL = "jdbc:oracle:thin:@192.168.0.25:1521:xe";
    public static final String USER_ID = "scott";
    public static final String USER_PW = "tiger";
    
    protected Connection getConnect() {
//        while (true) {
            try {
                Connection conn = DriverManager.getConnection(GyulDao.DB_URL, GyulDao.USER_ID, GyulDao.USER_PW);
//                System.out.println("연결 성공");
                return conn;
    
            } catch (SQLException e) {
                // e.printStackTrace();
                System.err.println(e);
                System.err.println("연결 실패");
            }
//        }
        
        return null;
    }
}

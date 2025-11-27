package com.market.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    
    // 로컬 DB 접속 URL --> 포트 확인 필요
    private static final String DB_URL = "jdbc:mysql://localhost:3306/BookMarket_DB?serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true";
    
    //private static final String DB_URL = "jdbc:mysql://123.212.19.71:3306/BookMarket_DB?serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true";
    
    // DB 사용자 이름
    private static final String USER = "root";
    // private static final String USER = "jdbc_user";
    
    // DB 패스워드
    private static final String PASS = "root"; 
   // private static final String PASS = "root;
    
    public static Connection getConnection() throws SQLException {
        Connection conn = null;
        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            return conn;
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC 드라이버를 찾을 수 없습니다.");
            throw new SQLException(e);
        }
    }
    
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
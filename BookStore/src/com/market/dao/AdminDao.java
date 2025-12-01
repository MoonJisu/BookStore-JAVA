package com.market.dao;

import com.market.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminDao {

    // DB의 admins 테이블에서 관리자 ID와 비밀번호의 유효성을 검사
    public static boolean isAdminValid(String id, String pw) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        // admins 테이블에서 ID와 PW가 일치하는 레코드 조회
        String sql = "SELECT login_id FROM admins WHERE login_id = ? AND password = ?";

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, id);
            pstmt.setString(2, pw);
            rs = pstmt.executeQuery();

            return rs.next(); // 레코드가 존재하면 true(인증 성공)
        } catch (SQLException e) {
            System.out.println("관리자 DB 인증 중 오류 발생: " + e.getMessage());
            return false;
        } finally {
            try {
            	// 자원 해제
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                DBConnection.closeConnection(conn);
            } catch (SQLException e) {
                System.err.println("자원 해제 오류: " + e.getMessage());
            }
        }
    }
}
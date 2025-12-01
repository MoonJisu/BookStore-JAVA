package com.market.dao;

import com.market.member.User;
import com.market.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class UserDao {
	
	// 사용자 이름과 연락처를 DB에서 조회/등록하고 user_id를 반환
	public static int loginOrRegisterUser(User user) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int userId = 0;
        
        String checkSql = "SELECT user_id FROM users WHERE name = ? AND phone = ?";
        String insertSql = "INSERT INTO users (name, phone) VALUES (?, ?)";

        try {
            conn = DBConnection.getConnection();
            
            // 사용자 조회(로그인 시도)
            pstmt = conn.prepareStatement(checkSql);
            pstmt.setString(1, user.getName());
            pstmt.setString(2, String.valueOf(user.getPhone()));
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
            	//기존 사용자 : ID 설정
                userId = rs.getInt("user_id");
            } else {
            	// 신규 사용자 : 등록(회원가입)
                pstmt.close(); //이전 pstmt 닫기
                // INSERT 후 생성된 키(user_id)를 반환받도록 설정
                pstmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
                pstmt.setString(1, user.getName());
                pstmt.setString(2, String.valueOf(user.getPhone()));
                pstmt.executeUpdate();
                
                // 생성된 user_id를 가져와 currentUserId에 설정
                rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    userId = rs.getInt(1);
                }
            }
            return userId; // 획득한 user_id 반환
        } finally {
        	// 자원 해제
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) { e.printStackTrace(); }
            if (conn != null) DBConnection.closeConnection(conn);
        }
	}
	
	// DB에서 특정 사용자의 쿠폰 보유 여부 확인
    public static boolean checkCoupon(int userId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String sql = "SELECT coupon_available FROM users WHERE user_id = ?";
        boolean hasCoupon = false;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                hasCoupon = rs.getInt(1) == 1; // 1이면 쿠폰 있음, 0이면 없음
            }
        } catch (SQLException e) {
            System.out.println("쿠폰 확인 중 오류: " + e.getMessage());
        } finally {
             DBConnection.closeConnection(conn);
        }
        return hasCoupon;
    }
    
    // DB에서 특정 사용자의 쿠폰 사용을 처리
    public static void useCoupon(int userId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        String sql = "UPDATE users SET coupon_available = 0 WHERE user_id = ?";

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("쿠폰 사용 처리 중 오류: " + e.getMessage());
        } finally {
            DBConnection.closeConnection(conn);
        }
    }
    
    // 사용자의 주문 횟수를 확인하여 첫 주문인 경우 쿠폰 지급
    public static boolean checkAndGrantFirstOrderCoupon(int userId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String sqlCount = "SELECT COUNT(*) FROM orders WHERE user_id = ?";
        String sqlUpdate = "UPDATE users SET coupon_available = 1 WHERE user_id = ?";

        try {
            conn = DBConnection.getConnection();
            
            // 주문 횟수 조회
            pstmt = conn.prepareStatement(sqlCount);
            pstmt.setInt(1, userId);
            rs = pstmt.executeQuery();
            
            int orderCount = 0;
            if (rs.next()) {
                orderCount = rs.getInt(1);
            }
            // 주문 횟수가 1인 경우에만 쿠폰 지급 (첫 주문을 완료한 후)
            if (orderCount == 1) {
                pstmt.close(); // 이전 PreparedStatement 닫기
                pstmt = conn.prepareStatement(sqlUpdate);
                pstmt.setInt(1, userId);
                pstmt.executeUpdate();
                
                return true;	// 쿠폰 지급 성공
            }
        } catch (SQLException e) {
            System.out.println("쿠폰 지급 중 오류: " + e.getMessage());
        } finally {
            DBConnection.closeConnection(conn);
        }
        
        return false;	// 쿠폰 지급 실패
    }
    
    // 주문 횟수 조회 (쿠폰 지급 로직에 사용됨)
    public static int getOrderCount(int userId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        String sql = "SELECT COUNT(*) FROM orders WHERE user_id = ?";

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.out.println("주문 횟수 조회 오류: " + e.getMessage());
        } finally {
            DBConnection.closeConnection(conn);
        }
        return 0;
    }
}
package com.market.dao;

import com.market.cart.Cart;
import com.market.cart.CartItem;
import com.market.main.Welcome; 
import com.market.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class OrderDao {

    // 장바구니 내용과 주문 정보를 DB의 orders 및 order_item 테이블에 저장
    public static void insertOrderToDB(int userId, String ordererName, String ordererPhone, String deliveryAddress, Cart mCart) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmtOrder = null;
        PreparedStatement pstmtItem = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            
            // orders 테이블에 주문정보 삽입
            String sqlOrder = "INSERT INTO orders (user_id, orderer_name, orderer_phone, delivery_address) VALUES (?, ?, ?, ?)";
            pstmtOrder = conn.prepareStatement(sqlOrder, Statement.RETURN_GENERATED_KEYS);
            
            pstmtOrder.setInt(1, userId);
            pstmtOrder.setString(2, ordererName);
            pstmtOrder.setString(3, ordererPhone);
            pstmtOrder.setString(4, deliveryAddress);
            
            pstmtOrder.executeUpdate();
            
            // 생성된 order_id(기본키) 가져오기
            int orderId = 0;
            rs = pstmtOrder.getGeneratedKeys();
            if (rs.next()) {
                orderId = rs.getInt(1);
            }
            
            // order_items 테이블에 주문 상세 항목 삽입
            String sqlItem = "INSERT INTO order_items (order_id, book_id, quantity, unit_price) VALUES (?, ?, ?, ?)";
            pstmtItem = conn.prepareStatement(sqlItem);

            for (int i = 0; i < mCart.mCartItem.size(); i++) {
                CartItem item = mCart.mCartItem.get(i);
                pstmtItem.setInt(1, orderId);
                pstmtItem.setString(2, item.getBookID());
                pstmtItem.setInt(3, item.getQuantity());
                pstmtItem.setInt(4, item.getItemBook().getUnitPrice());
                
                pstmtItem.addBatch(); // Batch에 쿼리 추가
            }
            
            pstmtItem.executeBatch(); // Batch 쿼리 일괄 실행

        } catch (SQLException e) {
            // DB 오류 발생 시 로그 출력
            System.err.println("OrderDao.insertOrderToDB 오류 발생: " + e.getMessage());
            throw e; // 예외를 호출자에게 다시 던짐
        } finally {
        	// 자원 해제
            try {
                if (rs != null) rs.close();
                if (pstmtOrder != null) pstmtOrder.close();
                if (pstmtItem != null) pstmtItem.close();
            } catch (SQLException e) { e.printStackTrace(); }
            DBConnection.closeConnection(conn);
        }
    }
    
    // 주문 목록 조회
    public static java.util.List<Welcome.OrderSummary> getOrderList(int userId) {
        java.util.List<Welcome.OrderSummary> list = new java.util.ArrayList<>();

        String sql =
                "SELECT o.order_id, o.order_date, o.delivery_address, " +
                "IFNULL(SUM(oi.quantity * oi.unit_price), 0) AS total_price " +
                "FROM orders o " +
                "LEFT JOIN order_items oi ON o.order_id = oi.order_id " +
                "WHERE o.user_id = ? " +
                "GROUP BY o.order_id, o.order_date, o.delivery_address " +
                "ORDER BY o.order_date DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new Welcome.OrderSummary(
                    rs.getInt("order_id"),
                    rs.getString("order_date"), 
                    rs.getString("delivery_address"),
                    rs.getInt("total_price")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("OrderDao.getOrderList 오류 발생: " + e.getMessage()); 
        }

        return list;
    }
    
    // 특정 주문 상세 조회
    public static Welcome.OrderDetail getOrderDetail(int orderId) {

    	Welcome.OrderDetail d = new Welcome.OrderDetail();
        d.orderId = orderId;

        String sql1 =
            "SELECT order_date, orderer_name, orderer_phone, delivery_address " +
            "FROM orders WHERE order_id = ?";

        String sql2 =
            "SELECT book_id, quantity, unit_price " +
            "FROM order_items WHERE order_id = ?";

        try (Connection conn = DBConnection.getConnection()) {

            // 1) 주문 기본 정보
            PreparedStatement ps = conn.prepareStatement(sql1);
            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                d.orderDate = rs.getString("order_date");
                d.ordererName = rs.getString("orderer_name");
                d.ordererPhone = rs.getString("orderer_phone");
                d.deliveryAddress = rs.getString("delivery_address");
            }

            // 2) 주문 상품 목록
            ps = conn.prepareStatement(sql2);
            ps.setInt(1, orderId);
            rs = ps.executeQuery();

            int sum = 0;
            while (rs.next()) {
                Welcome.OrderItemDetail item = new Welcome.OrderItemDetail(
                        rs.getString("book_id"),
                        rs.getInt("quantity"),
                        rs.getInt("unit_price")
                );
                d.items.add(item);

                sum += item.quantity * item.unitPrice;
            }

            // 원래 상품 총 금액
            d.originalTotal = sum;

            // 쿠폰 사용 로직 재현: Welcome의 래퍼 메서드를 사용
            // DAO 계층에서는 최소한의 로직만 가지도록 UserDAO 호출 대신 Welcome 래퍼 메서드 호출
            if (Welcome.checkCoupon(Welcome.currentUserId) == false) { // 쿠폰이 없다면
                int possibleDiscount = (int)(sum * 0.1);
                d.discount = possibleDiscount;
            } else {
                d.discount = 0;
            }

            d.totalPrice = d.originalTotal - d.discount;

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("OrderDao.getOrderDetail 오류 발생: " + e.getMessage()); 
        }

        return d;
    }
}
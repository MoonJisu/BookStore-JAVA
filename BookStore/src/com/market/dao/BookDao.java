package com.market.dao;

import com.market.bookitem.Book;
import com.market.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class BookDao {

    // DB의 books 테이블에 저장된 모든 도서 정보를 조회하여 ArrayList<Book>에 담음
    public static void setDBToBookList(ArrayList<Book> booklist) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String sql = "SELECT * FROM books";

		try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            
			while (rs.next()) {
				Book bookitem = new Book(
                    rs.getString("bookId"),
                    rs.getString("title"),
                    rs.getInt("unitPrice"),
                    rs.getString("author"),
                    rs.getString("description"),
                    rs.getString("category"),
                    rs.getString("releaseDate"));
				booklist.add(bookitem);
			}
		} finally {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            DBConnection.closeConnection(conn);
        }
    }
    
    // DB의 books 테이블에 저장된 전체 도서 개수를 조회
    public static int totalDBToBookList() throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int count = 0;
        String sql = "SELECT COUNT(bookId) FROM books";

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                count = rs.getInt(1);
            }
        } finally {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            DBConnection.closeConnection(conn);
        }
        return count;
    }
    
    // 키워드로 도서 검색
    public static void searchBookList(ArrayList<Book> booklist, String keyword) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        String sql = 	"SELECT * FROM books WHERE " +
                		"bookId LIKE ? OR " +
                		"title LIKE ? OR " +
                		"author LIKE ? OR " +
                		"description LIKE ? OR " +
                		"category LIKE ? OR " +
                		"releaseDate LIKE ?";

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            
            String q = "%" + keyword + "%";

            pstmt.setString(1, q);
            pstmt.setString(2, q);
            pstmt.setString(3, q);
            pstmt.setString(4, q);
            pstmt.setString(5, q);
            pstmt.setString(6, q);
            
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Book bookitem = new Book(
                    rs.getString("bookId"),
                    rs.getString("title"),
                    rs.getInt("unitPrice"),
                    rs.getString("author"),
                    rs.getString("description"),
                    rs.getString("category"),
                    rs.getString("releaseDate"));
                booklist.add(bookitem);
            }
        } finally {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            DBConnection.closeConnection(conn);
        }
    }
    
    // 새로운 도서정보를 DB의 books 테이블에 INSERT
    public static void insertBookToDB(String[] bookInfo) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        String sql = "INSERT INTO books (bookId, title, unitPrice, author, description, category, releaseDate) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            
            pstmt.setString(1, bookInfo[0]);
            pstmt.setString(2, bookInfo[1]);
            pstmt.setInt(3, Integer.parseInt(bookInfo[2]));
            pstmt.setString(4, bookInfo[3]);
            pstmt.setString(5, bookInfo[4]);
            pstmt.setString(6, bookInfo[5]);
            pstmt.setString(7, bookInfo[6]);

            pstmt.executeUpdate(); // 쿼리 실행
        } finally {
            if (pstmt != null) pstmt.close();
            DBConnection.closeConnection(conn);
        }
    }
}
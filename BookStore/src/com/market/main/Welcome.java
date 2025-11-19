package com.market.main;

import java.util.Scanner;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.market.util.DBConnection;

import com.market.bookitem.Book;
import com.market.cart.Cart;
import com.market.cart.CartItem;
import com.market.exception.CartException;
import com.market.member.Admin;
import com.market.member.User;

public class Welcome {
	static final int NUM_BOOK = 3;
	static final int NUM_ITEM = 7;
	static Cart mCart = new Cart();
	static User mUser;
	
	static String ordererName = "";
	static String ordererPhone = "";
	static String deliveryAddress = "";
	static boolean isOrderPlaced = false; // 주문 완료 여부 확인용
	
	static int currentUserId = 0;
    static boolean isCouponApplied = false; 
    static int finalTotalPrice = 0;         

    // [추가] 장바구니가 비워진 후에도 영수증을 출력하기 위해 마지막 주문 정보를 저장할 리스트
    static ArrayList<CartItem> lastOrderCartItems = new ArrayList<>();

	public static void main(String[] args) {
		ArrayList<Book> mBookList;
		int mTotalBook = 0;

        try (Connection conn = DBConnection.getConnection()) {
            System.out.println("데이터베이스 연결 성공!");
        } catch (SQLException e) {
            System.out.println("데이터베이스 연결 실패: " + e.getMessage());
            System.out.println("프로그램을 종료합니다.");
            return;
        }
        
        Scanner input = new Scanner(System.in);
        System.out.print("당신의 이름을 입력하세요: ");
        String userName = input.next();

        System.out.print("연락처를 입력하세요: ");
        int userMobile = input.nextInt();
        input.nextLine(); 
        
        mUser = new User(userName, userMobile);
        
        loginOrRegisterUser(mUser); 
        
        String greeting = "Welcome to Shopping Mall";
        String tagline = "Welcome to Book Market!";
        
        boolean quit = false;
        while (!quit) {
        	System.out.println("***********************************************");
        	System.out.println("\t" + greeting);
        	System.out.println("\t" + tagline);
        	
        	menuIntroduction();

        	try {
        		System.out.print("메뉴 번호를 선택해주세요: ");
        		int n = input.nextInt();
                input.nextLine(); 

				if (n < 1 || n > 10) {
					System.out.println("1부터 10까지의 숫자를 입력하세요.");
				} else {
					switch (n) {
					case 1:
						menuGuestInfo(userName, userMobile); 
						break;
					case 2:
						menuCartItemList(); 
						break;	
					case 3:
						menuCartClear(); 
						break;
					case 4:
						mTotalBook = totalDBToBookList();
						mBookList = new ArrayList<Book>();
						menuCartAddItem(mBookList); 
						break;
					case 5:
						menuCartEditQuantity(); 
						break;
					case 6:
						menuCartRemoveItem(); 
						break;	
					case 7:
						menuOrder(); // [핵심] 여기서 주문/결제/DB저장 모두 수행
						break;
					case 8:
						menuCartBill(); // [핵심] 여기서는 내역 조회만 수행
						break;
					case 9:
						menuAdminLogin(); 
						break;
					case 10:
						menuExit(); 
						quit = true;
						break;	
					}				
				}
        	} catch (CartException e) {
        		System.out.println(e.getMessage());
        	} catch (SQLException e) {
                System.out.println("데이터베이스 처리 중 오류 발생: " + e.getMessage());
        	} catch (Exception e) {
        		System.out.println("올바르지 않은 메뉴 선택 또는 입력 오류로 종료합니다.");
        		input.nextLine(); 
                quit = true;
            } 
        }
    }

    public static void menuIntroduction() {
        System.out.println("****************************************************");
        System.out.println(" 1. 고객 정보 확인하기 \t6. 장바구니의 항목 삭제하기");
        System.out.println(" 2. 장바구니 상품 목록 \t7. 주문하기");
        System.out.println(" 3. 장바구니 비우기 \t8. 영수증 보기");
        System.out.println(" 4. 장바구니에 항목 추가하기 \t9. 관리자 로그인");
        System.out.println(" 5. 장바구니 수량 변경하기\t10. 종료");
        System.out.println("****************************************************");
    }

    public static void menuGuestInfo(String name, int mobile) {
        System.out.println("현재 고객 정보:");
        System.out.println("이름: " + mUser.getName() + "  연락처: " + mUser.getPhone());
        if(checkCoupon(currentUserId)) {
            System.out.println("보유 쿠폰: [첫 구매 감사 10% 할인 쿠폰]");
        } else {
            System.out.println("보유 쿠폰: 없음");
        }
    }

    public static void menuCartItemList() {
        if (mCart.mCartCount > 0) {
            mCart.printCart();
        } else {
             System.out.println("장바구니에 항목이 없습니다.");
        }
    }

    public static void menuCartClear() throws CartException {
        if (mCart.mCartCount == 0)
            throw new CartException("장바구니에 항목이 없습니다.");
        else {
            System.out.println("장바구니의 모든 항목을 삭제하겠습니까? (Y/N)");
            Scanner input = new Scanner(System.in);
            String str = input.nextLine();

            if (str.equalsIgnoreCase("Y")) {
                System.out.println("장바구니의 모든 항목을 삭제했습니다.");
                mCart.deleteBook();
            }
        }
    }

    public static void menuCartAddItem(ArrayList<Book> booklist) {
        try {
            setDBToBookList(booklist);
            mCart.printBookList(booklist);
        } catch (SQLException e) {
            System.out.println("도서 목록 로딩 실패: " + e.getMessage());
            return;
        }

        boolean quit = false;
        Scanner input = new Scanner(System.in);

        while (!quit) {
            System.out.print("장바구니에 추가할 도서의 ID를 입력하세요: ");
            String str = input.nextLine();

            boolean flag = false;
            int numId = -1;

            for (int i = 0; i < booklist.size(); i++) {
                if (str.equals(booklist.get(i).getBookId())) {
                    numId = i;
                    flag = true;
                    break;
                }
            }

            if (flag) {
                System.out.println("장바구니에 추가하겠습니까? (Y/N)");
                str = input.nextLine();

                if (str.equalsIgnoreCase("Y")) {
                    System.out.println(booklist.get(numId).getBookId() + " 도서가 장바구니에 추가되었습니다.");
                    if (!isCartInBook(booklist.get(numId).getBookId())) {
                        mCart.insertBook(booklist.get(numId));
                    }
                }
                quit = true;
            } else {
                System.out.println("다시 입력해주세요.");
            }
        }
    }
    
    public static void menuCartEditQuantity() throws CartException {
        if (mCart.mCartCount == 0)
            throw new CartException("장바구니에 항목이 없습니다.");
        else {
            menuCartItemList();
            boolean quit = false;
            Scanner input = new Scanner(System.in);

            while (!quit) {
                System.out.print("수량을 변경할 도서의 ID를 입력하세요 (취소: Q): ");
                String bookId = input.nextLine();

                if (bookId.equalsIgnoreCase("Q")) {
                    quit = true;
                    continue;
                }
                
                int cartItemIndex = mCart.getCartItemIndex(bookId);

                if (cartItemIndex != -1) {
                    try {
                        System.out.print("새로운 수량을 입력하세요 (1 이상): ");
                        int newQuantity = input.nextInt();
                        input.nextLine(); 

                        if (newQuantity <= 0) {
                             System.out.println("수량은 1 이상이어야 합니다. 항목 삭제를 원하시면 6번 메뉴를 이용해주세요.");
                             continue;
                        }

                        mCart.setCartItemQuantity(cartItemIndex, newQuantity);
                        System.out.println(bookId + " 도서의 수량이 " + newQuantity + "로 변경되었습니다.");
                        menuCartItemList();
                        quit = true;

                    } catch (Exception e) {
                        System.out.println("잘못된 수량 입력입니다. 다시 입력해주세요.");
                        input.nextLine(); 
                    }
                } else {
                    System.out.println("장바구니에 해당 도서 ID가 없습니다. 다시 입력해주세요.");
                }
            }
        }
    }


    public static void menuCartRemoveItem() throws CartException {
        if (mCart.mCartCount == 0)
            throw new CartException("장바구니에 항목이 없습니다.");
        else {
            menuCartItemList();
            boolean quit = false;

            while (!quit) {
                System.out.print("장바구니에서 삭제할 도서의 ID를 입력하세요: ");
                Scanner input = new Scanner(System.in);
                String str = input.nextLine();

                boolean flag = false;
                int numId = -1;

                for (int i = 0; i < mCart.mCartItem.size(); i++) {
                    if (str.equals(mCart.mCartItem.get(i).getBookID())) {
                        numId = i;
                        flag = true;
                        break;
                    }
                }

                if (flag) {
                    System.out.println("장바구니의 항목을 삭제하겠습니까? (Y/N)");
                    str = input.nextLine();

                    if (str.equalsIgnoreCase("Y")) {
                        System.out.println(mCart.mCartItem.get(numId).getBookID() + " 도서가 장바구니에서 삭제되었습니다.");
                        mCart.removeCart(numId);
                    }
                    quit = true;
                } else {
                    System.out.println("다시 입력해주세요.");
                }
            }
        }
    }
    
    // [수정됨] 주문 프로세스 전체 처리 (DB 저장, 쿠폰 사용, 장바구니 비우기)
    public static void menuOrder() throws CartException, SQLException {
        if (mCart.mCartCount == 0)
            throw new CartException("장바구니에 항목이 없습니다. 주문할 수 없습니다.");
            
        Scanner input = new Scanner(System.in);
        System.out.println("--------------- 주문 정보 입력 ----------------");
        
        System.out.println("배송받을 분은 고객 정보와 같습니까? (Y/N)");
        String str = input.nextLine();

        if (str.equalsIgnoreCase("Y")) {
            ordererName = mUser.getName();
            ordererPhone = String.valueOf(mUser.getPhone());
            System.out.print("배송지를 입력해주세요: ");
            deliveryAddress = input.nextLine();
        } else {
            System.out.print("배송받을 고객명을 입력하세요: ");
            ordererName = input.nextLine();
            System.out.print("배송받을 고객의 연락처를 입력하세요: ");
            ordererPhone = input.nextLine();
            System.out.print("배송받을 고객의 배송지를 입력하세요: ");
            deliveryAddress = input.nextLine();
        }
        
        // === 쿠폰 적용 로직 ===
        int total = mCart.getCartTotal();
        isCouponApplied = false;
        finalTotalPrice = total;

        if (checkCoupon(currentUserId)) {
            System.out.println("\n🎉 10% 할인 쿠폰을 가지고 계십니다! 🎉");
            System.out.print("쿠폰을 이번 주문에 사용하시겠습니까? (Y/N): ");
            String answer = input.nextLine();
            
            if (answer.equalsIgnoreCase("Y")) {
                int discount = (int)(total * 0.1);
                finalTotalPrice = total - discount; 
                isCouponApplied = true;
                System.out.println(">> 쿠폰이 적용되었습니다. (할인액: " + discount + "원)");
            }
        }
        
        System.out.println(">> 최종 결제 금액: " + finalTotalPrice + "원");
        System.out.print("이대로 주문을 확정하시겠습니까? (Y/N): ");
        String confirm = input.nextLine();

        if(confirm.equalsIgnoreCase("Y")) {
             // 1. 주문 정보 DB 저장
            try {
                insertOrderToDB();
                
                // 2. 쿠폰 사용 처리
                if (isCouponApplied) {
                    useCoupon(currentUserId);
                    System.out.println(">> 쿠폰이 사용 처리(소멸)되었습니다.");
                }

                // 3. 첫 구매 쿠폰 발급
                checkAndGrantFirstOrderCoupon(currentUserId);
                
                // 4. 영수증 출력을 위해 현재 장바구니 내용을 백업 (복사)
                lastOrderCartItems = new ArrayList<>(mCart.mCartItem);

                // 5. 장바구니 비우기 및 상태 업데이트
                mCart.deleteBook(); 
                isOrderPlaced = true; 
                System.out.println("✅ 주문이 성공적으로 완료되었습니다! (8. 영수증 보기에서 내역 확인 가능)");

            } catch (SQLException e) {
                System.out.println("❌ 주문 처리 중 오류 발생: " + e.getMessage());
            }
        } else {
            System.out.println("주문이 취소되었습니다.");
        }
    }
    
    // [수정됨] 단순히 저장된 주문 내역(영수증)만 출력하는 역할 (Read-Only)
    public static void menuCartBill() throws CartException {
        if (!isOrderPlaced)
             throw new CartException("최근 완료된 주문 내역이 없습니다. 먼저 7번 메뉴로 주문해주세요.");
             
        System.out.println("--------------- 주문 영수증 ----------------");
        printBill(ordererName, ordererPhone, deliveryAddress, finalTotalPrice);
    }

    // [수정됨] Cart 객체 대신 백업해둔 리스트(lastOrderCartItems)를 사용해 출력
    public static void printBill(String name, String phone, String address, int finalPrice) {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        String strDate = formatter.format(date);

        System.out.println();
        System.out.println("---------------배송받을 고객정보----------------");
        System.out.println("고객명: " + name + "   \t연락처: " + phone);
        System.out.println("배송지: " + address + "   \t발송일: " + strDate);
        
        System.out.println("주문 상품 목록 : ");
        System.out.println("----------------------------------------------------------------");
        System.out.println("         도서ID \t :          수량 \t:                합계");
        
        // 백업해둔 리스트 사용
        for (CartItem item : lastOrderCartItems) {
			System.out.print("    " + item.getBookID() + " \t| ");
			System.out.print("    " + item.getQuantity() + " \t| ");
			System.out.print("    " + item.getTotalPrice());
			System.out.println("  ");
        }
        System.out.println("----------------------------------------------------------------");

        System.out.println("\t\t\t\t최종 결제 금액: " + finalPrice + "원\n");
        System.out.println("----------------------------------------------");
        System.out.println();
    } 

    // ==============================================================
    // 쿠폰 관련 JDBC 메서드들
    // ==============================================================

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
                hasCoupon = rs.getInt(1) == 1;
            }
        } catch (SQLException e) {
            System.out.println("쿠폰 확인 중 오류: " + e.getMessage());
        } finally {
             DBConnection.closeConnection(conn);
        }
        return hasCoupon;
    }

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

    public static void checkAndGrantFirstOrderCoupon(int userId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String sqlCount = "SELECT COUNT(*) FROM orders WHERE user_id = ?";
        String sqlUpdate = "UPDATE users SET coupon_available = 1 WHERE user_id = ?";

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sqlCount);
            pstmt.setInt(1, userId);
            rs = pstmt.executeQuery();
            
            int orderCount = 0;
            if (rs.next()) {
                orderCount = rs.getInt(1);
            }
            
            if (orderCount == 1) {
                pstmt.close(); 
                pstmt = conn.prepareStatement(sqlUpdate);
                pstmt.setInt(1, userId);
                pstmt.executeUpdate();
                System.out.println("🎉 [축하합니다] 첫 주문 감사 이벤트로 10% 할인 쿠폰이 지급되었습니다! 다음 주문시 사용 가능합니다.");
            }

        } catch (SQLException e) {
            System.out.println("쿠폰 지급 중 오류: " + e.getMessage());
        } finally {
            DBConnection.closeConnection(conn);
        }
    }
    
    // ==============================================================

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
    
	public static void BookList(ArrayList<Book> booklist) {
        try {
            setDBToBookList(booklist);
        } catch (SQLException e) {
            System.out.println("도서 목록 로딩 중 데이터베이스 오류 발생: " + e.getMessage());
        }
    }

    public static void menuExit() {
        System.out.println("프로그램을 종료합니다. 감사합니다!");
    }

    public static boolean isCartInBook(String bookId) {
        return mCart.isCartInBook(bookId);
    }

    public static void menuAdminLogin() {
    	System.out.println("관리자 정보를 입력하세요.");

		Scanner input = new Scanner(System.in);
		System.out.print("아이디 : ");
		String adminId = input.next();

		System.out.print("비밀번호 : ");
		String adminPW = input.next();
        input.nextLine(); 
        
        if (!isAdminValid(adminId, adminPW)) {
            System.out.println("관리자 정보가 일치하지 않습니다.");
            return;
        }

		Admin admin = new Admin(mUser.getName(), mUser.getPhone());
        System.out.println("관리자 인증 성공!");

		System.out.println("도서 정보를 추가하겠습니까?  Y  | N ");
		String str = input.nextLine();

		if (str.toUpperCase().equals("Y")) {
            String[] writeBook = new String[7];
			Date date = new Date();
			SimpleDateFormat formatter = new SimpleDateFormat("yyMMddhhmmss");
			String strDate = formatter.format(date);
			writeBook[0] = "ISBN" + strDate;
			System.out.println("도서ID : " + writeBook[0]);

			System.out.print("도서명 : ");
			writeBook[1] = input.nextLine();
			System.out.print("가격 : ");
			writeBook[2] = input.nextLine();
			System.out.print("저자 : ");
			writeBook[3] = input.nextLine();
			System.out.print("설명 : "); 
			writeBook[4] = input.nextLine();
			System.out.print("분야 : ");
			writeBook[5] = input.nextLine();
			System.out.print("출판일 : ");
			writeBook[6] = input.nextLine();

			try {
                insertBookToDB(writeBook);
                System.out.println("새 도서 정보가 DB에 저장되었습니다.");
			} catch (SQLException e) {
                System.out.println("도서 정보 저장 중 데이터베이스 오류 발생: " + e.getMessage());
			}
		} else {
			System.out.println("이름 " + admin.getName() + " 연락처 " + admin.getPhone());
			System.out.println("아이디 " + admin.getId() + " 비밀번호 " + admin.getPassword());
		}
	}
    
    public static boolean isAdminValid(String id, String pw) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String sql = "SELECT login_id FROM admins WHERE login_id = ? AND password = ?"; 

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, id);
            pstmt.setString(2, pw);
            rs = pstmt.executeQuery();

            return rs.next();
        } catch (SQLException e) {
            System.out.println("관리자 DB 인증 중 오류 발생: " + e.getMessage());
            return false;
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                DBConnection.closeConnection(conn);
            } catch (SQLException e) {
                System.err.println("자원 해제 오류: " + e.getMessage());
            }
        }
    }
    
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

            pstmt.executeUpdate();
        } finally {
            if (pstmt != null) pstmt.close();
            DBConnection.closeConnection(conn);
        }
    }

    public static void loginOrRegisterUser(User user) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        String checkSql = "SELECT user_id FROM users WHERE name = ? AND phone = ?";
        String insertSql = "INSERT INTO users (name, phone) VALUES (?, ?)";

        try {
            conn = DBConnection.getConnection();
            
            pstmt = conn.prepareStatement(checkSql);
            pstmt.setString(1, user.getName());
            pstmt.setString(2, String.valueOf(user.getPhone()));
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                currentUserId = rs.getInt("user_id");
                System.out.println("--> [로그인 성공] 기존 고객님 환영합니다! (ID: " + currentUserId + ")");
            } else {
                pstmt.close(); 
                pstmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
                pstmt.setString(1, user.getName());
                pstmt.setString(2, String.valueOf(user.getPhone()));
                pstmt.executeUpdate();
                
                rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    currentUserId = rs.getInt(1);
                }
                System.out.println("--> [회원가입 완료] 신규 고객님 환영합니다! (ID: " + currentUserId + ")");
            }
            
        } catch (SQLException e) {
            System.out.println("고객 로그인/등록 실패: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) { e.printStackTrace(); }
            if (conn != null) DBConnection.closeConnection(conn);
        }
    }

    public static void insertOrderToDB() throws SQLException {
        Connection conn = null;
        PreparedStatement pstmtOrder = null;
        PreparedStatement pstmtItem = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            
            String sqlOrder = "INSERT INTO orders (user_id, orderer_name, orderer_phone, delivery_address) VALUES (?, ?, ?, ?)";
            pstmtOrder = conn.prepareStatement(sqlOrder, Statement.RETURN_GENERATED_KEYS);
            
            pstmtOrder.setInt(1, currentUserId);
            pstmtOrder.setString(2, ordererName);
            pstmtOrder.setString(3, ordererPhone);
            pstmtOrder.setString(4, deliveryAddress);
            
            pstmtOrder.executeUpdate();
            
            int orderId = 0;
            rs = pstmtOrder.getGeneratedKeys();
            if (rs.next()) {
                orderId = rs.getInt(1);
            }

            String sqlItem = "INSERT INTO order_items (order_id, book_id, quantity, unit_price) VALUES (?, ?, ?, ?)";
            pstmtItem = conn.prepareStatement(sqlItem);

            for (int i = 0; i < mCart.mCartItem.size(); i++) {
                CartItem item = mCart.mCartItem.get(i);
                pstmtItem.setInt(1, orderId);
                pstmtItem.setString(2, item.getBookID());
                pstmtItem.setInt(3, item.getQuantity());
                pstmtItem.setInt(4, item.getItemBook().getUnitPrice()); 
                
                pstmtItem.addBatch(); 
            }
            
            pstmtItem.executeBatch(); 

        } finally {
            if (rs != null) rs.close();
            if (pstmtOrder != null) pstmtOrder.close();
            if (pstmtItem != null) pstmtItem.close();
            DBConnection.closeConnection(conn);
        }
    }
}
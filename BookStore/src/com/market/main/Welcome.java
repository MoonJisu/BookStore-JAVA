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
	public static Cart mCart = new Cart();	// + 추가) CartPanel 접근을 위해 public 추가
	public static User mUser;				// + 추가) LoginPanel 접근을 위해 public 추가
	
	// 주문 및 배송정보
	public static String ordererName = "";			// + 추가) OrderPanel2 접근을 위해 public 추가
	public static String ordererPhone = "";			// + 추가) OrderPanel2 접근을 위해 public 추가
	public static String deliveryAddress = "";		// + 추가) OrderPanel2 접근을 위해 public 추가
	static boolean isOrderPlaced = false; // 주문 완료 여부 확인용
	
	public static int currentUserId = 0; //DB에서 조회/생성된 현재 사용자 ID + 추가) InfoPanel 접근을 위해 public 추가
    public static boolean isCouponApplied = false; //현재 주문에 쿠폰이 적용되었는지 여부 + 추가) OrderPanel2 접근을 위해 public 추가
    public static int finalTotalPrice = 0; // 최종 결제 금액(쿠폰 적용 후 금액) + 추가) OrderPanel2 접근을 위해 public 추가      

    // 장바구니가 비워진 후에도 영수증을 출력하기 위해 마지막 주문 정보를 저장할 리스트
    static ArrayList<CartItem> lastOrderCartItems = new ArrayList<>();

	public static void main(String[] args) {
		ArrayList<Book> mBookList;
		int mTotalBook = 0;
		
		//DB 연결 테스트 & 프로그램 시작
        try (Connection conn = DBConnection.getConnection()) {
            System.out.println("데이터베이스 연결 성공");
        } catch (SQLException e) {
            System.out.println("데이터베이스 연결 실패: " + e.getMessage());
            System.out.println("프로그램을 종료합니다."); //DB 연결 실패 시 프로그램 종료
            return;
        }
        
        Scanner input = new Scanner(System.in);
        System.out.print("당신의 이름을 입력하세요: ");
        String userName = input.next();

        System.out.print("연락처를 입력하세요: ");
        int userMobile = input.nextInt();
        input.nextLine();  // 버퍼 비우기 
        
        mUser = new User(userName, userMobile); // 임시 User 객체
        
        // 사용자를 DB에서 조회하거나 새로 등록하고 currentUserId 생성 
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

                // 메뉴 번호가 1~11로 변경됨
				if (n < 1 || n > 11) {
					System.out.println("1부터 11까지의 숫자를 입력하세요.");
				} else {
					switch (n) {
					case 1: // 고객 정보 확인
						menuGuestInfo(userName, userMobile); 
						break;
                    case 2: // [신규] 도서 검색 기능
                        menuSearchBook();
                        break;
					case 3: // [기존 2번] 장바구니 목록 보기
						menuCartItemList(); 
						break;	
					case 4: // [기존 3번] 장바구니 비우기
						menuCartClear(); 
						break;
					case 5: // [기존 4번] 장바구니 항목 추가
						mTotalBook = totalDBToBookList(); //DB에서 총 도서 개수 가져오기
						mBookList = new ArrayList<Book>();
						menuCartAddItem(mBookList);  // DB에서 목록 로드
						break;
					case 6: // [기존 5번] 장바구니 수량 변경
						menuCartEditQuantity(); 
						break;
					case 7: // [기존 6번] 장바구니 항목 삭제
						menuCartRemoveItem(); 
						break;	
					case 8: // [기존 7번] 주문 처리
						menuOrder(); 
						break;
					case 9: // [기존 8번] 영수증 보기
						menuCartBill();
						break;
					case 10: // [기존 9번] 관리자 로그인
						menuAdminLogin(); 
						break;
					case 11: // [기존 10번] 프로그램 종료
						menuExit(); 
						quit = true;
						break;	
					}				
				}
        	} catch (CartException e) { // 장바구니 관련 사용자 정의 예외 처리
        		System.out.println(e.getMessage());
        	} catch (SQLException e) { // DB 작업 중 발생한 예외 처리
                System.out.println("데이터베이스 처리 중 오류 발생: " + e.getMessage());
        	} catch (Exception e) { // 일반적인 입력 오류 및 예상치 못한 예외처리
        		System.out.println("올바르지 않은 메뉴 선택 또는 입력 오류로 종료합니다.");
        		input.nextLine(); // 입력 버퍼 정리
                quit = true;
            } 
        }
    }
	
	// 메인 메뉴 항목 출력 (번호 재정렬)
    public static void menuIntroduction() {
        System.out.println("****************************************************");
        System.out.println(" 1. 고객 정보 확인하기 \t7. 장바구니의 항목 삭제하기");
        System.out.println(" 2. 도서 검색하기      \t8. 주문하기");
        System.out.println(" 3. 장바구니 상품 목록 \t9. 영수증 보기");
        System.out.println(" 4. 장바구니 비우기    \t10. 관리자 로그인");
        System.out.println(" 5. 장바구니에 항목 추가하기 \t11. 종료");
        System.out.println(" 6. 장바구니 수량 변경하기");
        System.out.println("****************************************************");
    }
    
    // 현재 로그인된 고객 정보를 출력
    public static void menuGuestInfo(String name, int mobile) {
        System.out.println("현재 고객 정보:");
        System.out.println("이름: " + mUser.getName() + "  연락처: " + mUser.getPhone());
        if(checkCoupon(currentUserId)) {
            System.out.println("보유 쿠폰: [첫 구매 감사 10% 할인 쿠폰]");
        } else {
            System.out.println("보유 쿠폰: 없음");
        }
    }

    // [신규 기능] 도서 검색 메뉴 처리
    public static void menuSearchBook() {
        Scanner input = new Scanner(System.in);
        System.out.print("검색할 도서의 제목 또는 저자를 입력하세요: ");
        String keyword = input.nextLine();

        ArrayList<Book> searchList = new ArrayList<>();

        try {
            // DB 검색 메서드 호출
            searchBookList(searchList, keyword);
            
            if (searchList.isEmpty()) {
                System.out.println("'" + keyword + "'(으)로 검색된 도서가 없습니다.");
                return;
            }

            // 검색 결과 출력
            System.out.println("----------------------------------------------------------------");
            System.out.println("검색 결과 : " + searchList.size() + "건");
            mCart.printBookList(searchList); // 기존 출력 메서드 재사용

            // 검색 결과에서 바로 장바구니 추가 기능
            System.out.println("----------------------------------------------------------------");
            System.out.println("검색된 도서를 장바구니에 추가하시겠습니까? (Y/N)");
            String str = input.nextLine();

            if (str.equalsIgnoreCase("Y")) {
                while (true) {
                    System.out.print("장바구니에 추가할 도서의 ID를 입력하세요 (취소: Q): ");
                    String bookId = input.nextLine();

                    if (bookId.equalsIgnoreCase("Q")) break;

                    boolean flag = false;
                    Book selectedBook = null;

                    // 검색된 리스트 안에서 해당 ID 찾기
                    for (Book book : searchList) {
                        if (book.getBookId().equals(bookId)) {
                            selectedBook = book;
                            flag = true;
                            break;
                        }
                    }

                    if (flag) {
                        if (!isCartInBook(bookId)) {
                            mCart.insertBook(selectedBook);
                            System.out.println(bookId + " 도서가 장바구니에 추가되었습니다.");
                        } else {
                            System.out.println("이미 장바구니에 있는 도서입니다 (수량 증가).");
                            // Cart 클래스 내부 로직에 따라 이미 있으면 수량만 증가됨 (isCartInBook 활용 필요 시 수정 가능)
                            // 여기서는 단순히 메시지만 출력하고 넘어감, 실제 증가는 isCartInBook 호출 로직에 따름
                            // 위쪽 코드에서 isCartInBook 확인 후 insertBook 하므로, 
                            // Cart.isCartInBook()을 호출해서 수량을 증가시키는 처리가 필요하다면 아래처럼 처리:
                            // mCart.isCartInBook(bookId); // 이 메서드가 수량을 증가시키는 로직을 포함하고 있다면 호출
                        }
                        break; 
                    } else {
                        System.out.println("검색 결과 목록에 없는 도서 ID입니다. 다시 확인해주세요.");
                    }
                }
            }

        } catch (SQLException e) {
            System.out.println("도서 검색 중 데이터베이스 오류 발생: " + e.getMessage());
        }
    }
    
    // [신규 기능] DB에서 키워드로 도서 검색
    public static void searchBookList(ArrayList<Book> booklist, String keyword) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        // LIKE 연산자를 사용하여 제목이나 저자에 키워드가 포함된 책 조회
        String sql = "SELECT * FROM books WHERE title LIKE ? OR author LIKE ?";

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            
            String queryParam = "%" + keyword + "%";
            pstmt.setString(1, queryParam); 
            pstmt.setString(2, queryParam); 
            
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
    
    // 현재 장바구니에 담긴 상품 목록 출력
    public static void menuCartItemList() {
        if (mCart.mCartCount > 0) {
            mCart.printCart();
        } else {
             System.out.println("장바구니에 항목이 없습니다.");
        }
    }
    
    // 장바구니의 모든 항목 삭제
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
    
    // DB에서 도서 목록을 불러와 출력 후 사용자가 선택한 도서를 장바구니에 추가 
    // ArrayList<Book> booklist : DB에서 로드된 Book 객체를 담을 리스트
    public static void menuCartAddItem(ArrayList<Book> booklist) {
        try {
            setDBToBookList(booklist); // DB에서 도서 목록을 가져와 booklist에 채움
            mCart.printBookList(booklist); // 도서 목록 출력
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
            
            // 입력된 ID와 일치하는 도서를 리스트에서 찾음
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
                    // 이미 장바구니에 있는 책인지 확인 후 추가(Cart 클래스에서 수량 증가 로직 처리)
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
    
    // 장바구니 항목의 수량 변경, 장바구니가 비어있을 시 CartException
    public static void menuCartEditQuantity() throws CartException {
        if (mCart.mCartCount == 0)
            throw new CartException("장바구니에 항목이 없습니다.");
        else {
            menuCartItemList(); // 현재 장바구니 목록 출력
            boolean quit = false;
            Scanner input = new Scanner(System.in);

            while (!quit) {
                System.out.print("수량을 변경할 도서의 ID를 입력하세요 (취소: Q): ");
                String bookId = input.nextLine();

                if (bookId.equalsIgnoreCase("Q")) {
                    quit = true;
                    continue;
                }
                // 해당 도서 ID가 장바구니 어디에 있는지 찾음
                int cartItemIndex = mCart.getCartItemIndex(bookId);

                if (cartItemIndex != -1) {
                    try {
                        System.out.print("새로운 수량을 입력하세요 (1 이상): ");
                        int newQuantity = input.nextInt();
                        input.nextLine(); 

                        if (newQuantity <= 0) {
                             System.out.println("수량은 1 이상이어야 합니다. 항목 삭제를 원하시면 7번 메뉴를 이용해주세요.");
                             continue;
                        }
                        
                        // Cart 클래스 메서드를 사용해 수량 업데이트
                        mCart.setCartItemQuantity(cartItemIndex, newQuantity);
                        System.out.println(bookId + " 도서의 수량이 " + newQuantity + "로 변경되었습니다.");
                        menuCartItemList();
                        quit = true;

                    } catch (Exception e) {
                        System.out.println("잘못된 수량 입력입니다. 다시 입력해주세요.");
                        input.nextLine(); // 입력 버퍼 정리
                    }
                } else {
                    System.out.println("장바구니에 해당 도서 ID가 없습니다. 다시 입력해주세요.");
                }
            }
        }
    }

    // 장바구니에서 선책한 항목을 삭제, 장바구니가 비어 있을 경우 CartException
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
                
                // 입력된 ID와 일치하는 항목을 장바구니에서 찾
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
                        mCart.removeCart(numId); // Cart 클래스의 항목 삭제 메서드 호출
                    }
                    quit = true;
                } else {
                    System.out.println("다시 입력해주세요.");
                }
            }
        }
    }
    
    // 주문 정보를 입력받고, 쿠폰 적용 후 DB에 주문을 저장하고, 장바구니 비움
    // CartException : 장바구니가 비어있을 때 발생
    // SQLException : DB 처리 중 오류 발생
    public static void menuOrder() throws CartException, SQLException {
        if (mCart.mCartCount == 0)
            throw new CartException("장바구니에 항목이 없습니다. 주문할 수 없습니다.");
            
        Scanner input = new Scanner(System.in);
        System.out.println("--------------- 주문 정보 입력 ----------------");
        
        System.out.println("배송받을 분은 고객 정보와 같습니까? (Y/N)");
        String str = input.nextLine();
        
        // 배송지 정보 입력 처리
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
        
        // 쿠폰 적용 로직 
        int total = mCart.getCartTotal(); // 장바구니 원금액
        isCouponApplied = false;
        finalTotalPrice = total;

        if (checkCoupon(currentUserId)) {
            System.out.println("\n10% 할인 쿠폰을 가지고 계십니다!");
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
             // 주문 정보 DB 저장
            try {
                insertOrderToDB();
                
                // 쿠폰 사용 처리
                if (isCouponApplied) {
                    useCoupon(currentUserId);
                    System.out.println(">> 쿠폰이 사용 처리(소멸)되었습니다.");
                }

                // 첫 구매 쿠폰 발급
                checkAndGrantFirstOrderCoupon(currentUserId);
                
                // 영수증 출력을 위해 현재 장바구니 내용을 백업
                lastOrderCartItems = new ArrayList<>(mCart.mCartItem);

                // 장바구니 비우기 및 상태 업데이트
                mCart.deleteBook(); 
                isOrderPlaced = true; 
                System.out.println("주문이 성공적으로 완료되었습니다!");

            } catch (SQLException e) {
                System.out.println("주문 처리 중 오류 발생: " + e.getMessage());
            }
        } else {
            System.out.println("주문이 취소되었습니다.");
        }
    }
    
    // 마지막으로 완료된 주문의 영수증 내역 출력
    // CartException : 완료된 주문 내역이 없을 경우 발생
    public static void menuCartBill() throws CartException {
        if (!isOrderPlaced)
             throw new CartException("최근 완료된 주문 내역이 없습니다. 먼저 8번 메뉴로 주문해주세요.");
             
        System.out.println("--------------- 주문 영수증 ----------------");
        printBill(ordererName, ordererPhone, deliveryAddress, finalTotalPrice);
    }

    // 저장된 주문 정보를 기반으로 영수증을 포맷에 맞게 출력
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
        
        // 주문 시 백업해둔 리스트를 사용해 출력
        for (CartItem item : lastOrderCartItems) {
			System.out.print("    " + item.getBookID() + " \t| ");
			System.out.print("    " + item.getQuantity() + " \t| ");
			System.out.print("    " + item.getTotalPrice());
			System.out.println("  ");
        }
        System.out.println("----------------------------------------------------------------");

        System.out.println("\t\t\t\t최종 결제 금액: " + finalPrice + "원\n");
        System.out.println("----------------------------------------------------------------");
        System.out.println();
    } 

    // 쿠폰 관련 JDBC 메서드
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
    // coupon_available를 0으로 업데이트
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
    
    // 사용자의 주문 횟수를 확인하여 첫 주문인 경우 쿠폰 지급 +추가) boolean으로 형식 변경하여 쿠폰 지급 여부 반환 확인
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
            // 주문 횟수가 1인 경우에만 쿠폰 지급
            if (orderCount == 1) {
                pstmt.close();  // 이전 PreparedStatement 닫기
                pstmt = conn.prepareStatement(sqlUpdate);
                pstmt.setInt(1, userId);
                pstmt.executeUpdate();
                
                return true;	// 쿠폰 지급
            }
        } catch (SQLException e) {
            System.out.println("쿠폰 지급 중 오류: " + e.getMessage());
        } finally {
            DBConnection.closeConnection(conn);
        }
        
        return false;	// 쿠폰 지급 실패 
    }
    
    // DB의 books 테이블에 저장된 전체 도서 개수를 조회
    // SQLException : DB 처리 중 오류 발생
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
				// ResultSet에서 데이터를 읽어 Book 객체를 생성하고 리스트에 추가
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
	
	// 프로그램 종료 메시지를 출력
    public static void menuExit() {
        System.out.println("프로그램을 종료합니다. 감사합니다!");
    }
    
    // 장바구니에 해당 도서 ID가 이미 존재하는지 확인
    public static boolean isCartInBook(String bookId) {
        return mCart.isCartInBook(bookId);
    }
    
    // 관리자 로그인 및 새로운 도서 정보를 DB에 추가
    public static void menuAdminLogin() {
    	System.out.println("관리자 정보를 입력하세요.");

		Scanner input = new Scanner(System.in);
		System.out.print("아이디 : ");
		String adminId = input.next();

		System.out.print("비밀번호 : ");
		String adminPW = input.next();
        input.nextLine(); // 버퍼 비우기
        
        // DB를 통해 관리자 ID/PW 유효성 검사
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
			
			// 새로운 도서ID 생성
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
				//DB에 새 도서 정보 저장
                insertBookToDB(writeBook);
                System.out.println("새 도서 정보가 DB에 저장되었습니다.");
			} catch (SQLException e) {
                System.out.println("도서 정보 저장 중 데이터베이스 오류 발생: " + e.getMessage());
			}
		} else {
			// 관리자 인증 성공 후 Y/N에서 N을 선택한 경우 관리자 정보만 출력
			System.out.println("이름 " + admin.getName() + " 연락처 " + admin.getPhone());
			System.out.println("아이디 " + admin.getId() + " 비밀번호 " + admin.getPassword());
		}
	}
    
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
    
    // 사용자 이름과 연락처를 DB의 users 테이블에서 조회
    // 이미 존재하면 로그인 처리(ID 설정), 없으면 회원가입 처리(새 레코드 삽입 후 ID 설정)
    public static void loginOrRegisterUser(User user) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
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
                currentUserId = rs.getInt("user_id");
                System.out.println("--> [로그인 성공] 기존 고객님 환영합니다! (ID: " + currentUserId + ")");
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
                    currentUserId = rs.getInt(1);
                }
                System.out.println("--> [회원가입 완료] 신규 고객님 환영합니다! (ID: " + currentUserId + ")");
            }
            
        } catch (SQLException e) {
            System.out.println("고객 로그인/등록 실패: " + e.getMessage());
        } finally {
        	// 자원 해제
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) { e.printStackTrace(); }
            if (conn != null) DBConnection.closeConnection(conn);
        }
    }
    
    // 장바구니 내용과 주문 정보를 DB의 orders 및 order_item 테이블에 저장
    public static void insertOrderToDB() throws SQLException {
        Connection conn = null;
        PreparedStatement pstmtOrder = null;
        PreparedStatement pstmtItem = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            
            // orders 테이블에 주문정보 삽입
            String sqlOrder = "INSERT INTO orders (user_id, orderer_name, orderer_phone, delivery_address) VALUES (?, ?, ?, ?)";
            // INSERT 후 생성된 order_id를 반환받도록 설정
            pstmtOrder = conn.prepareStatement(sqlOrder, Statement.RETURN_GENERATED_KEYS);
            
            pstmtOrder.setInt(1, currentUserId);
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
                
                pstmtItem.addBatch();  // Batch에 쿼리 추가
            }
            
            pstmtItem.executeBatch(); // Batch 쿼리 일괄 실행

        } finally {
        	// 자원 해제
            if (rs != null) rs.close();
            if (pstmtOrder != null) pstmtOrder.close();
            if (pstmtItem != null) pstmtItem.close();
            DBConnection.closeConnection(conn);
        }
    }
    
    // 주문 횟수 조회
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
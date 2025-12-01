package com.market.main;

import java.util.Scanner;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;

import java.sql.Connection;
import java.sql.SQLException;

import com.market.util.DBConnection;

// DAO import 추가
import com.market.dao.BookDao; 
import com.market.dao.UserDao;
import com.market.dao.OrderDao;
import com.market.dao.AdminDao;

import com.market.bookitem.Book;
import com.market.cart.Cart;
import com.market.cart.CartItem;
import com.market.exception.CartException;
import com.market.member.Admin;
import com.market.member.User;

public class Welcome {
	static final int NUM_BOOK = 3;
	static final int NUM_ITEM = 7;
	public static Cart mCart = new Cart();	
	public static User mUser;				
	
	// 주문 및 배송정보
	public static String ordererName = "";			
	public static String ordererPhone = "";			
	public static String deliveryAddress = "";		
	static boolean isOrderPlaced = false; 
	
	public static int currentUserId = 0; 
    public static boolean isCouponApplied = false; 
    public static int finalTotalPrice = 0;

    static ArrayList<CartItem> lastOrderCartItems = new ArrayList<>();

	public static void main(String[] args) {
		ArrayList<Book> mBookList;
		int mTotalBook = 0;
		
		//DB 연결 테스트 & 프로그램 시작
        try (Connection conn = DBConnection.getConnection()) {
            System.out.println("데이터베이스 연결 성공");
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
        
        mUser = new User(userName, userMobile); // 임시 User 객체
        
        // DAO 호출: 메서드 내에서 currentUserId를 설정하므로, 할당 문을 제거합니다.
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
						mBookList = new ArrayList<Book>();
						menuCartAddItem(mBookList); // DB에서 목록 로드 (DAO 호출)
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
	
	// 메인 메뉴 항목 출력
    public static void menuIntroduction() {
        System.out.println("****************************************************");
        System.out.println(" 1. 고객 정보 확인하기 \t7. 장바구니의 항목 삭제하기");
        System.out.println(" 2. 도서 검색하기      \t8. 주문하기");
        System.out.println(" 3. 장바구니 상품 목록 \t9. 영수증 보기");
        System.out.println(" 4. 장바구니 비우기    \t10. 관리자 로그인");
        System.out.println(" 5. 장바구니에 항목 추가하기 \t11. 종료");
        System.out.println(" 6. 장바구니 수량 변경하기");
        System.out.println("****************************************************");
    }
    
    // 현재 로그인된 고객 정보를 출력
    public static void menuGuestInfo(String name, int mobile) {
        System.out.println("현재 고객 정보:");
        System.out.println("이름: " + mUser.getName() + "  연락처: " + mUser.getPhone());
        // DAO 호출: checkCoupon
        if(UserDao.checkCoupon(currentUserId)) {
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
            // DAO 호출: searchBookList
            BookDao.searchBookList(searchList, keyword);
            
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
    public static void menuCartAddItem(ArrayList<Book> booklist) {
        try {
            // DAO 호출: setDBToBookList
            BookDao.setDBToBookList(booklist); 
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

        // DAO 호출: checkCoupon
        if (UserDao.checkCoupon(currentUserId)) {
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
             // DAO 호출: insertOrderToDB (래퍼 메서드를 통해 DAO 호출)
            try {
                insertOrderToDB();
                
                // 쿠폰 사용 처리
                if (isCouponApplied) {
                    // DAO 호출: useCoupon
                    UserDao.useCoupon(currentUserId);
                    System.out.println(">> 쿠폰이 사용 처리(소멸)되었습니다.");
                }

                // DAO 호출: checkAndGrantFirstOrderCoupon
                if(UserDao.checkAndGrantFirstOrderCoupon(currentUserId)) {
                    System.out.println(">> [축하합니다] 첫 주문 감사 쿠폰(10% 할인)이 지급되었습니다!");
                }
                
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
        System.out.println("고객명: " + name + "   \t연락처: " + phone);
        System.out.println("배송지: " + address + "   \t발송일: " + strDate);
        
        System.out.println("주문 상품 목록 : ");
        System.out.println("----------------------------------------------------------------");
        System.out.println("         도서ID \t :          수량 \t:                합계");
        
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
        
        // DAO 호출: isAdminValid
        if (!AdminDao.isAdminValid(adminId, adminPW)) {
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
                // DAO 호출: insertBookToDB
                BookDao.insertBookToDB(writeBook);
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

    // [DAO 래퍼 메서드] 사용자 로그인/등록 (Welcome.main 및 LoginPanel에서 호출)
    // UserDao.loginOrRegisterUser에서 획득한 user_id를 currentUserId에 할당합니다.
    public static void loginOrRegisterUser(User user) {
        try {
            int userId = UserDao.loginOrRegisterUser(user);
            currentUserId = userId;
            
            if (currentUserId > 0) {
                 // 콘솔 출력은 UserDao에서 처리했으므로 여기서는 생략
            } else {
                 System.out.println("--> [로그인/등록 실패] 유효한 ID를 받지 못했습니다. ID: " + currentUserId);
            }
            
        } catch (SQLException e) {
            System.out.println("고객 로그인/등록 실패: " + e.getMessage());
            currentUserId = 0; // 실패 시 ID를 0으로 설정
        }
    }
    
    // [DAO 래퍼 메서드] 주문 정보를 DB에 저장 (OrderPanel2에서 호출)
    public static void insertOrderToDB() throws SQLException {
        // UI에서 설정된 정적 변수들을 OrderDao에 전달
        OrderDao.insertOrderToDB(
            currentUserId, 
            ordererName, 
            ordererPhone, 
            deliveryAddress, 
            mCart
        );
    }
    
    // [DAO 래퍼 메서드] DB의 books 테이블에 저장된 모든 도서 정보를 조회하여 ArrayList<Book>에 담음
    public static void setDBToBookList(ArrayList<Book> booklist) throws SQLException {
        BookDao.setDBToBookList(booklist);
    }
    
    // [DAO 래퍼 메서드] UI에서 사용하는 도서 검색
    public static void searchBookList(ArrayList<Book> booklist, String keyword) throws SQLException {
        BookDao.searchBookList(booklist, keyword);
    }
    
    // [DAO 래퍼 메서드] UI에서 사용하는 도서 정보 등록
    public static void insertBookToDB(String[] bookInfo) throws SQLException {
        BookDao.insertBookToDB(bookInfo);
    }
    
    // [DAO 래퍼 메서드] UI에서 사용하는 쿠폰 확인
    public static boolean checkCoupon(int userId) {
        return UserDao.checkCoupon(userId);
    }
    
    // [DAO 래퍼 메서드] UI에서 사용하는 쿠폰 사용
    public static void useCoupon(int userId) {
        UserDao.useCoupon(userId);
    }
    
    // [DAO 래퍼 메서드] UI에서 사용하는 첫 주문 쿠폰 지급
    public static boolean checkAndGrantFirstOrderCoupon(int userId) {
        return UserDao.checkAndGrantFirstOrderCoupon(userId);
    }
    
    // [DAO 래퍼 메서드] UI에서 사용하는 주문 횟수 조회
    public static int getOrderCount(int userId) {
        return UserDao.getOrderCount(userId);
    }
    
    // 주문 조회 DTO (DAO 파일에서 참조하므로 Welcome.java에 유지)
    public static class OrderSummary {
        public int orderId;
        public String date;
        public String address;
        public int totalPrice;

        public OrderSummary(int orderId, String date, String address, int totalPrice) {
            this.orderId = orderId;
            this.date = date;
            this.address = address;
            this.totalPrice = totalPrice;
        }
    }
    
    public static class OrderDetail {
        public int orderId;
        public String orderDate;
        public String ordererName;
        public String ordererPhone;
        public String deliveryAddress;
        public int totalPrice;
        
        public int originalTotal;
        public int discount;
        public boolean couponUsed; 

        public java.util.List<OrderItemDetail> items = new java.util.ArrayList<>();
    }

    public static class OrderItemDetail {
        public String bookId;
        public int quantity;
        public int unitPrice;

        public OrderItemDetail(String bookId, int quantity, int unitPrice) {
            this.bookId = bookId;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
        }
    }
    
    // [DAO 래퍼 메서드] UI에서 사용하는 주문 목록 조회
    public static java.util.List<OrderSummary> getOrderList(int userId) {
        return OrderDao.getOrderList(userId);
    }
    
    // [DAO 래퍼 메서드] UI에서 사용하는 주문 상세 조회
    public static OrderDetail getOrderDetail(int orderId) {
        return OrderDao.getOrderDetail(orderId);
    }
}
USE BookMarket_DB;

-- 1. 기존 테이블 삭제
DROP TABLE IF EXISTS order_items;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS admins;
DROP TABLE IF EXISTS books;
DROP TABLE IF EXISTS users;

-- 2. 회원 테이블
CREATE TABLE users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    coupon_available TINYINT DEFAULT 0  -- 0: 없음, 1: 있음 (기본값 0)
);

-- 3. 도서 테이블
CREATE TABLE books (
    bookId VARCHAR(100) PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    unitPrice INT NOT NULL,
    author VARCHAR(50),
    description VARCHAR(500),
    category VARCHAR(50),
    releaseDate VARCHAR(20)
);

-- 4. 관리자 테이블
CREATE TABLE admins (
    admin_id INT PRIMARY KEY AUTO_INCREMENT,
    login_id VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    admin_name VARCHAR(50),
    admin_phone VARCHAR(20)
);

-- 5. 주문 테이블
CREATE TABLE orders (
    order_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    order_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    orderer_name VARCHAR(50) NOT NULL,
    orderer_phone VARCHAR(20) NOT NULL,
    delivery_address VARCHAR(200) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- 6. 주문 상세 테이블
CREATE TABLE order_items (
    item_id INT PRIMARY KEY AUTO_INCREMENT,
    order_id INT NOT NULL,
    book_id VARCHAR(100) NOT NULL,
    quantity INT NOT NULL,
    unit_price INT NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(order_id),
    FOREIGN KEY (book_id) REFERENCES books(bookId)
);

-- 7. 초기 데이터 입력 (도서 및 관리자)
INSERT INTO books (bookId, title, unitPrice, author, description, category, releaseDate)
VALUES 
('ISBN1234', '쉽게 배우는 JSP 웹 프로그래밍', 27000, '송미영', '단계별로 쇼핑몰을 구현하며 배우는 JSP 웹 프로그래밍', 'IT전문서', '2018-10-08'),
('ISBN1235', '안드로이드 프로그래밍', 33000, '우재남', '실습 단계별 명쾌한 멘토링!', 'IT전문서', '2022-01-22'),
('ISBN1236', '스크래치', 22000, '고광일', '컴퓨팅 사고력을 키우는 블록 코딩', '컴퓨터입문', '2019-06-10'),
('ISBN1237', '가상현실의 이해', 22000, '송은지', '가상현실의 개념과 기술 활용분야의 시장 전망', 'IT전문서', '2022-01-03'),
('ISBN1238', '어린 왕자', 12000, '생텍쥐페리', '어른들을 위한 소설', '문학', '1943-04-06');

-- 관리자 계정 생성 (아이디: admin / 비번: admin1234)
INSERT INTO admins (login_id, password, admin_name, admin_phone) VALUES
('admin', 'admin1234', '관리자', '010-1234-5678');


-- 생성 확인
SELECT * FROM books;
SELECT * FROM admins;
SELECT * FROM Users;

-- Admins Table
SELECT * FROM Admins;

-- Orders Table
SELECT * FROM Orders;

-- Order_Items Table
SELECT * FROM Order_Items;

-- Books Table
SELECT * FROM Books;
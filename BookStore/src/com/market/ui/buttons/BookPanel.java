package com.market.ui.buttons;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.awt.event.*;
import java.util.ArrayList;

import com.market.bookitem.Book;
import com.market.main.Welcome;
import com.market.util.DBConnection;


public class BookPanel extends DefaultPanel {
    private DefaultTableModel bookItem;
    private JLabel labelImage; 

    public BookPanel() {

    	// BookPanel 설정
        JPanel card = createCard(800, 600);   
        card.setOpaque(true);	// 패널 배경 불투명 (설정 후 배경색 지정 가능)
        card.setBackground(new Color(255, 255, 255));
        card.setLayout(new BorderLayout());
         
        // titlePanel 설정
        JPanel titlePanel = new JPanel(new BorderLayout());
        JLabel title = new JLabel("도서 목록");	 				// 텍스트 설정    
        title.setFont(new Font("SansSerif", Font.BOLD, 26));	// 폰트 설정
        titlePanel.setOpaque(false);							// 패널 배경 불투명
        titlePanel.add(title, BorderLayout.NORTH);				// 설정 후 패널 생성 (add 먼저하면 위 설정 적용 X)

        
        // 메인 패널
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.anchor = GridBagConstraints.NORTHWEST;


        // 이미지
        ImageIcon defaultImg = new ImageIcon("src/com/market/image/Book_Icon.png");
        Image modifyImg  = defaultImg.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);	// 사진 사이즈 수정
        labelImage = new JLabel(new ImageIcon(modifyImg));

        JPanel imagePanel = new JPanel();
        imagePanel.setOpaque(false);
        imagePanel.add(labelImage);

        mainPanel.add(imagePanel);
        mainPanel.add(Box.createHorizontalStrut(50));


        // 도서 목록 테이블
        String[] tableHeader = {"도서ID", "도서명", "가격", "저자", "설명", "분야", "출판일"};
                
        bookItem = new DefaultTableModel(tableHeader, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;   // 테이블 내 정보 수정 불가
            }
        };
        
        
        // 테이블 생성
        JTable table = new JTable(bookItem);

        table.setRowHeight(28);
        table.setFont(new Font("SansSerif", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 15));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(550, 400));

        gbc.gridx = 1;
        gbc.gridy = 0;
        mainPanel.add(scrollPane, gbc);
        
        
        // 장바구니에 넣기 버튼 생성
        JButton addToCartBtn = new JButton("장바구니 넣기");
        addToCartBtn.setFont(new Font("SansSerif", Font.BOLD, 15));
        addToCartBtn.setPreferredSize(new Dimension(150, 30));

        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        bottomPanel.add(addToCartBtn);
        
        
        // 각 패널 생성 후 배치
        card.add(titlePanel, BorderLayout.WEST);
        card.add(mainPanel, BorderLayout.EAST);
        card.add(bottomPanel, BorderLayout.SOUTH);
        
        setLayout(new GridBagLayout());
        add(card);
        
        // DB에서 데이터 불러오기
        loadBookList();
        
        // 각 항목 클릭 시 이미지 변경
        table.addMouseListener(new MouseAdapter() {
        	@Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row != -1) {
                    String bookId = table.getValueAt(row, 0).toString();
                    setBookImage(bookId);
                }
            }
        });
        
        // 장바구니 버튼 클릭 시 장바구니에 해당 책 정보 입력
        addToCartBtn.addActionListener(e -> {
            int row = table.getSelectedRow();

            if (row == -1) {
                JOptionPane.showMessageDialog(this, "장바구니에 넣을 책을 선택해주세요");
                return;
            }

            String bookId = table.getValueAt(row, 0).toString();
            String name = table.getValueAt(row, 1).toString();
            int price = Integer.parseInt(table.getValueAt(row, 2).toString());

            // Book 객체 생성
            Book selectedBook = new Book(bookId, name, price);

            // 장바구니에 이미 있는 경우 → 수량 1 증가
            if (Welcome.mCart.isCartInBook(bookId)) {
                int index = Welcome.mCart.getCartItemIndex(bookId);
                int newQuantity = Welcome.mCart.mCartItem.get(index).getQuantity() + 1;
                Welcome.mCart.setCartItemQuantity(index, newQuantity);
            } 
            // 장바구니에 없는 경우 → 새로운 항목 추가
            else {
                Welcome.mCart.insertBook(selectedBook);
            }

            JOptionPane.showMessageDialog(this, "장바구니에 추가되었습니다");
        });

    }
    
    
    // 책 리스트 로딩
    private void loadBookList() {
        ArrayList<Book> list = new ArrayList<>();

        try {
            Welcome.setDBToBookList(list);

            // 테이블에 데이터 추가
            for (Book b : list) {
                bookItem.addRow(new Object[]{
                        b.getBookId(),
                        b.getName(),
                        b.getUnitPrice(),
                        b.getAuthor(),
                        b.getDescription(),
                        b.getCategory(),
                        b.getReleaseDate()
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                    "도서 목록 불러오기 실패: " + e.getMessage(),
                    "DB 오류",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    
    
    // 항목 클릭 시 이미지 변경
    private void setBookImage(String bookId) {

        String path = "src/com/market/image/" + bookId + ".jpg";
        ImageIcon img = new ImageIcon(path);

        // 이미지 파일이 없으면 기본 아이콘 유지
        if (img.getIconWidth() == -1) {
            return;
        }

        Image resize = img.getImage().getScaledInstance(200, 240, Image.SCALE_SMOOTH);
        labelImage.setIcon(new ImageIcon(resize));
    }
    
    public void refresh() {
        bookItem.setRowCount(0);
        loadBookList();
    }

}

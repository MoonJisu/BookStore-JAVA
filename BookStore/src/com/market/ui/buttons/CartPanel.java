package com.market.ui.buttons;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

import com.market.cart.CartItem;
import com.market.main.Welcome;
import com.market.util.DBConnection;


public class CartPanel extends DefaultPanel {

    private DefaultTableModel cartModel;
    private JTable table;
    private JLabel labelImage;
    private JLabel totalCost;

    public CartPanel() {

        // CartPanel 설정
        JPanel card = createCard(740, 550);
        card.setOpaque(true);	// 패널 배경 불투명 (설정 후 배경색 지정 가능)
        card.setBackground(new Color(255, 255, 255));
        card.setLayout(new BorderLayout());

        
        // titlePanel 설정
        JPanel titlePanel = new JPanel(new BorderLayout());
        JLabel title = new JLabel("장바구니");	 				// 텍스트 설정    
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
        ImageIcon defaultImg = new ImageIcon("src/com/market/image/Cart_Icon.png");
        Image modifyImg  = defaultImg.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);	// 사진 사이즈 수정
        labelImage = new JLabel(new ImageIcon(modifyImg));

        JPanel imagePanel = new JPanel();
        imagePanel.setOpaque(false);
        imagePanel.add(labelImage);

        mainPanel.add(imagePanel);
        mainPanel.add(Box.createHorizontalStrut(50));

        
        // 장바구니 테이블
        String[] tableheader = {"도서ID", "도서명", "수량", "가격", "합계"};

        cartModel = new DefaultTableModel(tableheader, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return col == 2;	// 다른 항목은 수정 X, 수량만 O
            }
        };

        // 테이블 생성
        table = new JTable(cartModel) {
            @Override
            public TableCellEditor getCellEditor(int row, int column) {

                // 수량 칸 콤보박스 에디터 적용
                if (column == 2) {
                    JComboBox<Integer> combo = new JComboBox<>();
                    for (int i = 1; i <= 99; i++) combo.addItem(i);

                    return new DefaultCellEditor(combo);
                }
                return super.getCellEditor(row, column);
            }
        };
        
        TableColumn NumCell = table.getColumnModel().getColumn(2);

        String[] NumList = new String[99];
        for (int i = 0; i < 99; i++) NumList[i] = String.valueOf(i + 1);

        JComboBox<String> NumBox = new JComboBox<>(NumList);

        // 콤보박스 모양 유지
        NumCell.setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, 
                                                           boolean hasFocus,
                                                           int row, 
                                                           int col) {
            	NumBox.setSelectedItem(value.toString());
                return NumBox;
            }
        });

        NumCell.setCellEditor(new DefaultCellEditor(NumBox));
        
        cartModel.addTableModelListener(e -> {
            if (e.getType() == TableModelEvent.UPDATE) {
                int row = e.getFirstRow();
                int col = e.getColumn();

                // 수량 칼럼(2)에서만 동작
                if (col == 2) {
                    String bookId = cartModel.getValueAt(row, 0).toString();
                    int newQty = Integer.parseInt(cartModel.getValueAt(row, 2).toString());

                    int idx = Welcome.mCart.getCartItemIndex(bookId);
                    if (idx != -1) {
                        Welcome.mCart.setCartItemQuantity(idx, newQty);
                        refresh(); // 합계 갱신
                    }
                }
            }
        });
        
        table.setRowHeight(22);
        table.setFont(new Font("SansSerif", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 15));

        JScrollPane scroll = new JScrollPane(table);
        scroll.setPreferredSize(new Dimension(500, 400));

        gbc.gridx = 1;
        gbc.gridy = 0;
        mainPanel.add(scroll, gbc);
        
        
        // 총액 표시
        totalCost = new JLabel("선택 상품 금액: 0원");
        totalCost.setFont(new Font("SansSerif", Font.BOLD, 16));

        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        totalPanel.setOpaque(false);
        totalPanel.add(totalCost);
        
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(totalPanel, gbc);

        
        // 항목 삭제, 비우기, 주문하기 버튼 생성
        JButton btnRemove = new JButton("선택 삭제");
        JButton btnClear = new JButton("전체 비우기");
        JButton btnOrder = new JButton("주문하기");

        btnRemove.setPreferredSize(new Dimension(130, 30));
        btnClear.setPreferredSize(new Dimension(130, 30));
        btnOrder.setPreferredSize(new Dimension(130, 30));

        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        bottomPanel.add(btnRemove);
        bottomPanel.add(btnClear);
        bottomPanel.add(btnOrder);

        
        // 각 패널 생성 후 배치
        card.add(titlePanel, BorderLayout.WEST);
        card.add(mainPanel, BorderLayout.EAST);
        card.add(bottomPanel, BorderLayout.SOUTH);

        setLayout(new GridBagLayout());
        add(card);

        // DB에서 데이터 불러오기
        refresh();
        
        
        // 각 항목 클릭 시 이미지 변경
        table.addMouseListener(new MouseAdapter() {
        	@Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row != -1) {
                    String bookId = table.getValueAt(row, 0).toString();
                    setCartImage(bookId);
                }
            }
        });
        
        // 버튼 이벤트
        btnRemove.addActionListener(e -> removeSelectedItem());
        btnClear.addActionListener(e -> clearCart());
        btnOrder.addActionListener(e -> order());
    }

    
    // 장바구니 목록 로딩
    public void refresh() {
        cartModel.setRowCount(0); // 초기화
        int totalPrice = 0;

        for (CartItem item : Welcome.mCart.mCartItem) {
            cartModel.addRow(new Object[]{
                item.getBookID(),
                item.getItemBook().getName(),
                item.getQuantity(),
                item.getItemBook().getUnitPrice(),
                item.getTotalPrice() + "원"
            });
            
            totalPrice += item.getTotalPrice();
        } 
        totalCost.setText("선택 상품 금액: " + totalPrice + "원");
    }
    
    
    // 항목 클릭 시 이미지 변경
    private void setCartImage(String bookId) {

        String path = "src/com/market/image/" + bookId + ".jpg";
        ImageIcon img = new ImageIcon(path);

        // 이미지 파일이 없으면 기본 아이콘 유지
        if (img.getIconWidth() == -1) {
            return;
        }

        Image resize = img.getImage().getScaledInstance(200, 240, Image.SCALE_SMOOTH);
        labelImage.setIcon(new ImageIcon(resize));
    }
    
    
    // 이미지 초기화
    private void resetImage() {
        ImageIcon defaultImg = new ImageIcon("src/com/market/image/Cart_Icon.png");
        Image modifyImg = defaultImg.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
        labelImage.setIcon(new ImageIcon(modifyImg));
    }

    
    // 선택 삭제
    private void removeSelectedItem() {
        int row = table.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "삭제할 항목을 선택하세요");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "선택한 항목을 삭제하시겠습니까?",
                "장바구니 항목 삭제",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        String bookId = table.getValueAt(row, 0).toString();
        Welcome.mCart.removeCart(Welcome.mCart.getCartItemIndex(bookId));
        
        refresh();
        resetImage();
    }

    
    // 전체 비우기
    private void clearCart() {
    	 int confirm = JOptionPane.showConfirmDialog(
    	            this,
    	            "장바구니를 비우시겠습니까?",
    	            "장바구니 비우기",
    	            JOptionPane.YES_NO_OPTION,
    	            JOptionPane.WARNING_MESSAGE
    	    );

    	    if (confirm != JOptionPane.YES_OPTION) {
    	        return;
    	    }
    	    
        Welcome.mCart.deleteBook();
        refresh();
        resetImage();
    }

    
    // 주문
    private void order() {
        // 기존 Welcome.java의 주문 로직 자동 실행
        JOptionPane.showMessageDialog(this, "주문 기능은 콘솔 / GUI 통합 중입니다.\nWelcome의 menuOrder()를 GUI 버튼으로 연결 예정!");
    }
}

package com.market.ui.buttons;

import javax.swing.*;
import java.awt.*;
import javax.swing.table.DefaultTableModel;

import com.market.cart.CartItem;
import com.market.main.Welcome;


public class OrderPanel extends DefaultPanel {
	private DefaultTableModel orderItem;
    private JTable table;
    private JLabel totalCost;
	
    public OrderPanel() {
    	
    	// OrderPanel 설정
        JPanel card = createCard(800, 600);   
        card.setOpaque(true);	// 패널 배경 불투명 (설정 후 배경색 지정 가능)
        card.setBackground(new Color(255, 255, 255));
        card.setLayout(new BorderLayout());
         
        // titlePanel 설정
        JPanel titlePanel = new JPanel(new BorderLayout());
        JLabel title = new JLabel("주문하기");	 					// 텍스트 설정    
        title.setFont(new Font("SansSerif", Font.BOLD, 26));	// 폰트 설정
        titlePanel.setOpaque(false);							// 패널 배경 불투명
        titlePanel.add(title, BorderLayout.NORTH);				// 설정 후 패널 생성 (add 먼저하면 위 설정 적용 X)


        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.anchor = GridBagConstraints.CENTER;


        // 결제 목록 테이블
        String[] tableheader = {"도서ID", "도서명", "수량", "가격", "총 액수"};
        
        orderItem = new DefaultTableModel(tableheader, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;   // 주문 페이지에서는 수정 불가
            }
        };

        table = new JTable(orderItem);

        table.setRowHeight(28);
        table.setFont(new Font("SansSerif", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 15));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(650, 350));

        gbc.gridx = 1;
        gbc.gridy = 0;
        mainPanel.add(scrollPane, gbc);
        
        
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
        
        
        // 주문 버튼
        JButton orderBtn = new JButton("주문하기");
        orderBtn.setFont(new Font("SansSerif", Font.BOLD, 15));
        orderBtn.setPreferredSize(new Dimension(150, 30));
        
        orderBtn.addActionListener(e -> {

            // 장바구니 비었는지 체크
            if (Welcome.mCart.mCartItem.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "주문할 항목이 없습니다.\n장바구니에 상품을 추가해주세요.",
                    "주문 불가",
                    JOptionPane.WARNING_MESSAGE);
                return;	// 주문 진행 중단
            }

            // 장바구니에 항목이 있으면 결제창 열기
            OrderPanel2 pay = new OrderPanel2();
            pay.setVisible(true);
        });



        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        bottomPanel.add(orderBtn);

        // 각 패널 배치
        card.add(titlePanel, BorderLayout.WEST);
        card.add(mainPanel, BorderLayout.CENTER);
        card.add(bottomPanel, BorderLayout.SOUTH);
        
        setLayout(new GridBagLayout());
        add(card);
    }
    
    
    // 최신화
    public void refreshOrder() {

        orderItem.setRowCount(0);
        int totalPrice = 0;

        for (CartItem item : Welcome.mCart.mCartItem) {

            orderItem.addRow(new Object[]{
                item.getBookID(),
                item.getItemBook().getName(),
                item.getQuantity(),
                item.getItemBook().getUnitPrice(),
                item.getTotalPrice() + "원"
            });

            totalPrice += item.getTotalPrice();
        }

        totalCost.setText("총 주문 금액: " + totalPrice + "원");
    }
}

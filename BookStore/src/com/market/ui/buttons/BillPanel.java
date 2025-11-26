package com.market.ui.buttons;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;


public class BillPanel extends DefaultPanel {

    public BillPanel() {

    	// BookPanel 설정
        JPanel card = createCard(740, 550);   
        card.setOpaque(true);	// 패널 배경 불투명 (설정 후 배경색 지정 가능)
        card.setBackground(new Color(255, 255, 255));
        card.setLayout(new BorderLayout());
         
        // titlePanel 설정
        JPanel titlePanel = new JPanel(new BorderLayout());
        JLabel title = new JLabel("내역");	 					// 텍스트 설정    
        title.setFont(new Font("SansSerif", Font.BOLD, 26));	// 폰트 설정
        titlePanel.setOpaque(false);							// 패널 배경 불투명
        titlePanel.add(title, BorderLayout.NORTH);				// 설정 후 패널 생성 (add 먼저하면 위 설정 적용 X)


        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.anchor = GridBagConstraints.CENTER;


        // 도서 목록 테이블
        String[] tableHeader = {"도서ID", "도서명", "저자", "설명", "분야", "출판일", "가격", "쿠폰"};
        
        Object[][] data = {
        		
        };

        DefaultTableModel model = new DefaultTableModel(data, tableHeader);
        JTable table = new JTable(model);

        table.setRowHeight(28);
        table.setFont(new Font("SansSerif", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 15));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(600, 300));

        gbc.gridx = 1;
        gbc.gridy = 0;
        mainPanel.add(scrollPane, gbc);
        
        
        JButton orderBtn = new JButton("조회");
        orderBtn.setFont(new Font("SansSerif", Font.BOLD, 15));
        orderBtn.setPreferredSize(new Dimension(150, 30));

        // 아래쪽 패널에 버튼을 하나 넣기
        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        bottomPanel.add(orderBtn);

        card.add(titlePanel, BorderLayout.WEST);
        card.add(mainPanel, BorderLayout.CENTER);
        card.add(bottomPanel, BorderLayout.SOUTH);
        
        setLayout(new GridBagLayout());
        add(card);
    }
}

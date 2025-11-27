package com.market.ui.buttons;

import javax.swing.*;
import java.awt.*;
import com.market.main.Welcome;

public class InfoPanel extends DefaultPanel {
    private JLabel lnfoName;
    private JLabel lnfoPhone;
    private JLabel Coupon;

    public InfoPanel() {
    	// InfoPanel 설정
        JPanel card = createCard(800, 600);   
        card.setOpaque(true);	// 패널 배경 불투명 (설정 후 배경색 지정 가능)
        card.setBackground(new Color(255, 255, 255));
        card.setLayout(new BorderLayout());	// 편의를 위해 제목 영역인 titlePanel과 정보 영역인 mainPanel로 나눠서 설정
        
        
        
        // titlePanel 설정
        JPanel titlePanel = new JPanel(new BorderLayout());
        JLabel title = new JLabel("고객 정보");	 				// 텍스트 설정    
        title.setFont(new Font("SansSerif", Font.BOLD, 26));	// 폰트 설정
        titlePanel.setOpaque(false);							// 패널 배경 불투명
        titlePanel.add(title, BorderLayout.NORTH);				// 설정 후 패널 생성 (add 먼저하면 위 설정 적용 X)
        
        
        // mainPanel 설정
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(20, 20, 20, 20);	// 여백 설정 -> new Insect(Top, left, Bottom, Right)
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.setOpaque(false);	
        
        
        // 이미지 설정
        ImageIcon InfoImage = new ImageIcon("src/com/market/image/Info_Icon.png");
        Image SizeUpImage = InfoImage.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
        JLabel labelImage = new JLabel(new ImageIcon(SizeUpImage));
        
        // Text 패널 (이름, 전화번호, 쿠폰)
        JPanel textPanel = new JPanel(new GridBagLayout());
        textPanel.setOpaque(false);
        
        GridBagConstraints tgbc = new GridBagConstraints();
        tgbc.insets = new Insets(10, 10, 10, 10);
        tgbc.anchor = GridBagConstraints.WEST;
        
        JLabel labelName = new JLabel("이름 : ");
        JLabel labelPhone = new JLabel("전화번호 : ");
        JLabel labelCoupon = new JLabel("보유 쿠폰 : ");
        
        lnfoName = new JLabel("-");
        lnfoPhone = new JLabel("-");
        Coupon = new JLabel("-");

        labelName.setFont(new Font("SansSerif", Font.BOLD, 18));
        labelPhone.setFont(new Font("SansSerif", Font.BOLD, 18));
        labelCoupon.setFont(new Font("SansSerif", Font.BOLD, 18));

        lnfoName.setFont(new Font("SansSerif", Font.PLAIN, 18));
        lnfoPhone.setFont(new Font("SansSerif", Font.PLAIN, 18));
        Coupon.setFont(new Font("SansSerif", Font.PLAIN, 18));
        
        mainPanel.setOpaque(false);	// 패널 배경 불투명
        
       
        // 이름, 전화번호, 쿠폰 텍스트 정렬
        tgbc.gridx = 0; tgbc.gridy = 0; textPanel.add(labelName, tgbc);
        tgbc.gridx = 1; textPanel.add(lnfoName, tgbc);

        tgbc.gridx = 0; tgbc.gridy = 1; textPanel.add(labelPhone, tgbc);
        tgbc.gridx = 1; textPanel.add(lnfoPhone, tgbc);

        tgbc.gridx = 0; tgbc.gridy = 2; textPanel.add(labelCoupon, tgbc);
        tgbc.gridx = 1; textPanel.add(Coupon, tgbc);
        
        
        // 이미지와 텍스트 정렬
        gbc.gridx = 0; gbc.gridy = 0;
        mainPanel.add(labelImage, gbc);
        
        gbc.gridx = 1;
        mainPanel.add(textPanel, gbc);

        card.add(titlePanel, BorderLayout.NORTH);   // 제목은 맨 위
        card.add(mainPanel, BorderLayout.CENTER); 	// 정보들은 중앙

        setLayout(new GridBagLayout());
        add(card);

        reload();
    }
        

    // 사용자 정보 입력 후 사용자 정보 리로드
    public void reload() {
    	
    	// 사용자 정보가 Null 일때
        if (Welcome.mUser == null) {
            lnfoName.setText("이름 : (로그인 정보 없음)");
            lnfoPhone.setText("전화번호 : -");
            Coupon.setText("보유 쿠폰 : -");
            return;
        }
        
        // 사용자 이름, 전화번호, 쿠폰 정보 출력 (Welcome.java)
        lnfoName.setText(Welcome.mUser.getName());
        lnfoPhone.setText("0" + Welcome.mUser.getPhone());

        boolean hasCoupon = Welcome.checkCoupon(Welcome.currentUserId);
        Coupon.setText(hasCoupon ? "10% 할인 쿠폰" : "없음");
    }
}

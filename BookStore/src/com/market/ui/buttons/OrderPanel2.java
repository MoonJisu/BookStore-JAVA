package com.market.ui.buttons;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import com.market.main.Welcome;

public class OrderPanel2 extends JDialog {

    private JTextField OrderName, OrderPhone;
    private JTextField ReceiveName, ReceivePhone, Address;
    
    private JCheckBox sameCheck;
    private JButton PayButton;
    private JComboBox<String> coupon;

    private int originalTotal;
    private int finalTotal;

    private JLabel OriginalPrice;
    private JLabel DiscountPrice;
    private JLabel FinalPrice;


    public OrderPanel2() {
    	
    	// OrderPanel2 설정
        setTitle("주문 정보 입력");
        setModal(true);
        setSize(400, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;


        // 주문자 정보
        JLabel lblOrderTitle = new JLabel("주문자 정보");
        lblOrderTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(lblOrderTitle, gbc);
        
        gbc.gridwidth = 1;
        
        // 주문자 이름
        gbc.gridy++;
        panel.add(new JLabel("이름"), gbc);
        gbc.gridx = 1;
        OrderName = new JTextField(15);
        panel.add(OrderName, gbc);

        // 주문자 전화번호
        gbc.gridx = 0; gbc.gridy++;
        panel.add(new JLabel("전화번호"), gbc);
        gbc.gridx = 1;
        OrderPhone = new JTextField(15);
        panel.add(OrderPhone, gbc);
        

        // 주문자 정보 자동 입력
        if (Welcome.mUser != null) {
            OrderName.setText(Welcome.mUser.getName());
            OrderPhone.setText(String.valueOf(Welcome.mUser.getPhone()));
        }


        // 배송자 동일 체크
        gbc.gridx = 0; gbc.gridy++;
        gbc.gridwidth = 2;
        sameCheck = new JCheckBox("배송 정보가 주문자와 동일");
        panel.add(sameCheck, gbc);
        
        // 여백 생성
        gbc.gridy++;
        panel.add(Box.createVerticalStrut(10), gbc);

        gbc.gridwidth = 1;


        // 베송 정보
        gbc.gridx = 0; gbc.gridy++;
        JLabel lblRecvTitle = new JLabel("배송 정보");
        lblRecvTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
        gbc.gridwidth = 2;
        panel.add(lblRecvTitle, gbc);

        gbc.gridwidth = 1;
        
        // 수령인 이름
        gbc.gridy++;
        panel.add(new JLabel("수령인 이름"), gbc);
        gbc.gridx = 1;
        ReceiveName = new JTextField(15);
        panel.add(ReceiveName, gbc);

        // 수령인 전화번호
        gbc.gridx = 0; gbc.gridy++;
        panel.add(new JLabel("전화번호"), gbc);
        gbc.gridx = 1;
        ReceivePhone = new JTextField(15);
        panel.add(ReceivePhone, gbc);

        // 주소    
        gbc.gridx = 0; gbc.gridy++;
        panel.add(new JLabel("주소"), gbc);
        gbc.gridx = 1;
        Address = new JTextField(15);
        panel.add(Address, gbc);
        
        // 여백 생성
        gbc.gridy++;
        panel.add(Box.createVerticalStrut(10), gbc);

        sameCheck.addActionListener(e -> applySameInfo());


        // 쿠폰 선택
        gbc.gridx = 0; gbc.gridy++;
        JLabel lblCoupon = new JLabel("쿠폰 적용");
        lblCoupon.setFont(new Font("SansSerif", Font.BOLD, 16));
        gbc.gridwidth = 2;
        panel.add(lblCoupon, gbc);

        gbc.gridwidth = 1;

        gbc.gridy++;
        gbc.gridx = 0;
        panel.add(new JLabel("쿠폰 선택"), gbc);

        gbc.gridx = 1;
        
        // DB에서 쿠폰 여부 확인
        boolean hasCoupon = Welcome.checkCoupon(Welcome.currentUserId);

        if (hasCoupon) {
            // 쿠폰 보유 중이면 두 가지 옵션 제공
            coupon = new JComboBox<>(new String[]{
                "적용 안 함",
                "10% 할인 쿠폰 적용"
            });
            coupon.setEnabled(true);
        } else {
            // 쿠폰 없음
            coupon = new JComboBox<>(new String[]{
                "사용 가능한 쿠폰 없음"
            });
            coupon.setEnabled(false);
        }

        panel.add(coupon, gbc);;
        
        // 쿠폰 할인 적용
        coupon.addActionListener(e -> applyCoupon());
        
        // 여백 생성
        gbc.gridy++;
        panel.add(Box.createVerticalStrut(40), gbc);


        // 결제 금액
        originalTotal = Welcome.mCart.getCartTotal();
        finalTotal = originalTotal;

        JPanel pricePanel = new JPanel(new GridLayout(3, 2, 5, 5));
        pricePanel.setBackground(Color.WHITE);

        pricePanel.add(new JLabel("총 상품 금액 : "));
        OriginalPrice = new JLabel(originalTotal + "원");
        pricePanel.add(OriginalPrice);

        pricePanel.add(new JLabel("할인 금액 : "));
        DiscountPrice = new JLabel("0원");
        pricePanel.add(DiscountPrice);

        pricePanel.add(new JLabel("주문 금액 : "));
        FinalPrice = new JLabel(finalTotal + "원");
        FinalPrice.setFont(new Font("SansSerif", Font.BOLD, 15));
        pricePanel.add(FinalPrice);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        panel.add(pricePanel, gbc);


        // 결제 버튼
        PayButton = new JButton("주문하기");
        PayButton.setPreferredSize(new Dimension(120, 30));
        JPanel bottom = new JPanel();
        bottom.setBackground(Color.WHITE);
        bottom.add(PayButton);

        PayButton.addActionListener(e -> submit());


        add(panel, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
    }


    
    // 배송 정보와 동일 여부
    private void applySameInfo() {
        if (sameCheck.isSelected()) {
            ReceiveName.setText(OrderName.getText());
            ReceivePhone.setText(OrderPhone.getText());

            ReceiveName.setEnabled(false);
            ReceivePhone.setEnabled(false);

        } else {
            ReceiveName.setText("");
            ReceivePhone.setText("");

            ReceiveName.setEnabled(true);
            ReceivePhone.setEnabled(true);
        }
    }

    
    // 쿠폰 적용
    private void applyCoupon() {
        if (!coupon.isEnabled()) return;

        String selected = coupon.getSelectedItem().toString();

        // 적용 안 함 선택하면 할인 제거
        if (selected.equals("적용 안 함")) {
            DiscountPrice.setText("0원");
            FinalPrice.setText(originalTotal + "원");
            finalTotal = originalTotal;
            return;
        }

        // 10% 할인 적용
        if (selected.contains("10% 할인")) {
            int discount = (int)(originalTotal * 0.1);
            finalTotal = originalTotal - discount;

            DiscountPrice.setText(discount + "원");
            FinalPrice.setText(finalTotal + "원");
        }
    }


    private void submit() {	
    	// 기본 정보 체크 - 공백 시 주문 불가
        if (OrderName.getText().trim().isEmpty() ||
            OrderPhone.getText().trim().isEmpty() ||
            ReceiveName.getText().trim().isEmpty() ||
            ReceivePhone.getText().trim().isEmpty() ||
            Address.getText().trim().isEmpty()
        ) {
            JOptionPane.showMessageDialog(this,
                    "모든 정보를 입력해야 주문이 가능합니다.",
                    "입력 오류",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // 전화번호는 숫자 10~11자리만 가능
        if (!OrderPhone.getText().matches("\\d{10,11}") ||
        	    !ReceivePhone.getText().matches("\\d{10,11}")) {

        	    JOptionPane.showMessageDialog(this,
        	            "전화번호는 숫자 10~11자만 입력 가능합니다.",
        	            "전화번호 오류",
        	            JOptionPane.WARNING_MESSAGE);
        	    return;
        	}
        
    	// 입력값 가져오기
    	String recName = ReceiveName.getText();
        String recPhone = ReceivePhone.getText();
        String address = Address.getText();

        Welcome.ordererName = recName;
        Welcome.ordererPhone = recPhone;
        Welcome.deliveryAddress = address;


        // 주문 전 주문 수 확인
        int previousOrderCount = Welcome.getOrderCount(Welcome.currentUserId);


        // 쿠폰 사용 처리
        boolean hasCoupon = Welcome.checkCoupon(Welcome.currentUserId);

        if (hasCoupon && coupon.isEnabled() &&
            coupon.getSelectedItem().toString().contains("10% 할인")) 
        {
            Welcome.useCoupon(Welcome.currentUserId);

            JOptionPane.showMessageDialog(this,
                    "10% 할인 쿠폰이 적용되었습니다!",
                    "쿠폰 사용 완료",
                    JOptionPane.INFORMATION_MESSAGE);
        }

        
        // 주문 DB 저장
        try {
            Welcome.insertOrderToDB();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "주문 저장 중 오류 발생: " + e.getMessage());
            return;
        }

        
        // 첫 주문이라면 쿠폰 지급
        if (previousOrderCount == 0) {

            boolean granted = Welcome.checkAndGrantFirstOrderCoupon(Welcome.currentUserId);

            if (granted) {
                JOptionPane.showMessageDialog(this,
                        "[축하합니다] 첫 주문 감사 쿠폰(10% 할인)이 지급되었습니다!",
                        "쿠폰 지급 완료",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }

        
        // 주문 후 장바구니 비우기
        Welcome.mCart.deleteBook();

        
        // 주문 완료 메시지
        JOptionPane.showMessageDialog(this, "주문이 완료되었습니다!");

        dispose();
    }
}

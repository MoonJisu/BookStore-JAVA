package com.market.ui.buttons;

import javax.swing.*;
import java.awt.*;
import com.market.main.Welcome;

public class DetailPanel extends JDialog {

    public DetailPanel(int orderId) {

        setTitle("주문 상세");
        setSize(400, 750);
        setLocationRelativeTo(null);
        setModal(true);
        setLayout(new BorderLayout());

        Welcome.OrderDetail d = Welcome.getOrderDetail(orderId);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        int y = 0;

        // 제목
        JLabel title = new JLabel("주문 상세 내역");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        gbc.gridx = 0; gbc.gridy = y++; gbc.gridwidth = 2;
        panel.add(title, gbc);

        addLine(panel, gbc, y++);

        // 주문 정보
        addHeader(panel, gbc, "주문 정보", y++);

        addRow(panel, gbc, "주문번호 : " + orderId, y++);
        addRow(panel, gbc, "주문일자 : " + d.orderDate, y++);
        addRow(panel, gbc, "주문자명 : " + d.ordererName, y++);
        addRow(panel, gbc, "연락처 : " + d.ordererPhone, y++);
        addRow(panel, gbc, "배송지 : " + d.deliveryAddress, y++);

        addLine(panel, gbc, y++);

        // 상품 목록
        addHeader(panel, gbc, "주문 상품 목록", y++);

        for (Welcome.OrderItemDetail item : d.items) {
            addRow(panel, gbc, "• " + item.bookId, y++);
            addRow(panel, gbc,
                "   수량 : " + item.quantity + "개    |   금액 : " + (item.unitPrice * item.quantity) + "원", y++);
            addSeparator(panel, gbc, y++);
        }

        addHeader(panel, gbc, "결제 금액", y++);

        addRow(panel, gbc, "총 상품 금액 : " + d.originalTotal + "원", y++);
        addRow(panel, gbc, "할인 금액 : " + d.discount + "원", y++);

        JLabel total = new JLabel("최종 결제 금액 : " + d.totalPrice + "원");
        total.setFont(new Font("SansSerif", Font.BOLD, 16));

        gbc.gridx = 0; gbc.gridy = y++; gbc.gridwidth = 2;
        panel.add(total, gbc);

        // 닫기 버튼
        JButton close = new JButton("닫기");
        close.setPreferredSize(new Dimension(100, 30));
        close.addActionListener(e -> dispose());

        JPanel bottom = new JPanel();
        bottom.add(close);

        add(panel, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
    }

    private void addRow(JPanel panel, GridBagConstraints gbc, String text, int y) {
        gbc.gridx = 0; gbc.gridy = y; gbc.gridwidth = 2;
        panel.add(new JLabel(text), gbc);
    }

    private void addHeader(JPanel panel, GridBagConstraints gbc, String text, int y) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 16));
        gbc.gridx = 0; gbc.gridy = y; gbc.gridwidth = 2;
        panel.add(lbl, gbc);
    }

    private void addLine(JPanel panel, GridBagConstraints gbc, int y) {
        gbc.gridx = 0; gbc.gridy = y; gbc.gridwidth = 2;
        panel.add(new JLabel("────────────────────────────"), gbc);
    }

    private void addSeparator(JPanel panel, GridBagConstraints gbc, int y) {
        gbc.gridx = 0; gbc.gridy = y; gbc.gridwidth = 2;
        panel.add(new JLabel("------------------------------------------"), gbc);
    }
}

package com.market.ui;

import javax.swing.*;
import java.awt.*;

import com.market.ui.buttons.*;

public class MainPanel extends JPanel {
    private InfoPanel infoPanel;
    private CartPanel cartPanel;
    private BookPanel bookPanel;
    private OrderPanel orderPanel;
    private BillPanel billPanel;
    private AdminPanel adminPanel;
    private CardLayout contentCard;
    private JPanel contentArea;

    public MainPanel(MainFrame main) {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));

        // ■ 사이드 메뉴 버튼
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(130, 700));
        sidebar.setBackground(Color.WHITE);
        sidebar.setLayout(new GridLayout(10, 1, 5, 15));
        sidebar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        sidebar.add(sideButton("고객정보", "INFO"));
        sidebar.add(sideButton("장바구니", "ITEMLIST"));
        //sidebar.add(sideButton("비우기", "CLEAR"));
        sidebar.add(sideButton("도서목록", "BOOKLIST"));
        //sidebar.add(sideButton("수량변경", "EDIT"));
        //sidebar.add(sideButton("항목삭제", "REMOVE"));
        sidebar.add(sideButton("주문하기", "ORDER"));
        sidebar.add(sideButton("구매내역", "BILL"));
        sidebar.add(sideButton("관리자", "ADMIN"));
        sidebar.add(sideButton("종료", "EXIT"));

        add(sidebar, BorderLayout.EAST);


        // 메인 Content 영역
        contentCard = new CardLayout();
        contentArea = new JPanel(contentCard);
        contentArea.setBackground(new Color(245, 247, 250));

        // 초기 기본 화면만 먼저 등록
        contentArea.add(defaultPage(), "DEFAULT");

        add(contentArea, BorderLayout.CENTER);

        // 최초 보여줄 화면
        contentCard.show(contentArea, "DEFAULT");
    }


    // ■ 버튼 생성 헬퍼
    private JButton sideButton(String text, String action) {
        JButton btn = new JButton(text);

        btn.putClientProperty("JButton.buttonType", "roundRect");
        btn.setFocusPainted(false);
        btn.setBackground(new Color(74, 144, 226));
        btn.setForeground(Color.WHITE);
        btn.setBorderPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));

        btn.addActionListener(e -> handleAction(action));
        return btn;
    }


    // ■ 버튼 클릭 시 실행되는 동작
    private void handleAction(String action) {

        switch (action) {

            case "INFO":
                if (infoPanel == null) {
                    infoPanel = new InfoPanel();
                    contentArea.add(infoPanel, "INFO");
                } else {
                    infoPanel.reload(); // ← 로그인 정보 다시 불러오기 가능
                }
                contentCard.show(contentArea, "INFO");
                break;

            case "ITEMLIST":
                if (cartPanel == null) {
                    cartPanel = new CartPanel();
                    contentArea.add(cartPanel, "ITEMLIST");
                } else {
                    cartPanel.refresh(); 
                }
                contentCard.show(contentArea, "ITEMLIST");
                break;

            case "BOOKLIST":
                if (bookPanel == null) {
                    bookPanel = new BookPanel();
                    contentArea.add(bookPanel, "BOOKLIST");
                } else {
                    bookPanel.refresh();
                }
                contentCard.show(contentArea, "BOOKLIST");
                break;

            case "ORDER":
                if (orderPanel == null) {
                    orderPanel = new OrderPanel();
                    contentArea.add(orderPanel, "ORDER");
                }
                contentCard.show(contentArea, "ORDER");
                break;

            case "BILL":
                if (billPanel == null) {
                    billPanel = new BillPanel();
                    contentArea.add(billPanel, "BILL");
                }
                contentCard.show(contentArea, "BILL");
                break;

            case "ADMIN":
                if (adminPanel == null) {
                    adminPanel = new AdminPanel();
                    contentArea.add(adminPanel, "ADMIN");
                }
                contentCard.show(contentArea, "ADMIN");
                break;

//            case "CLEAR":
//                JOptionPane.showMessageDialog(this, "장바구니 비우기 기능은 UI 패널에서 구현하세요.");
//                break;

            case "EXIT":
                System.exit(0);
                break;
        }
    }


    // ■ 기본 페이지
    private JPanel defaultPage() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(245, 247, 250));

        JLabel label = new JLabel("메뉴를 선택하세요");
        label.setFont(new Font("SansSerif", Font.BOLD, 28));

        panel.add(label);
        return panel;
    }
}

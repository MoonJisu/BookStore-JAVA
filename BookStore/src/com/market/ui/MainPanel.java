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
    private SearchPanel searchPanel;
    
    private CardLayout contentCard;
    private JPanel contentArea;

    public MainPanel(MainFrame main) {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));
        

        // 메뉴 버튼
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(130, 700));
        sidebar.setBackground(Color.WHITE);
        sidebar.setLayout(new GridLayout(10, 1, 5, 15));
        sidebar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        sidebar.add(sideButton("고객정보", "INFO"));
        sidebar.add(sideButton("도서목록", "BOOKLIST"));
        sidebar.add(sideButton("도서검색", "SEARCH"));
        sidebar.add(sideButton("장바구니", "ITEMLIST"));
        sidebar.add(sideButton("주문하기", "ORDER"));
        sidebar.add(sideButton("구매내역", "BILL"));
        sidebar.add(sideButton("관리자", "ADMIN"));
        sidebar.add(sideButton("종료", "EXIT"));

        add(sidebar, BorderLayout.EAST);


        // 메인 Content 영역
        contentCard = new CardLayout();
        contentArea = new JPanel(contentCard);
        contentArea.setBackground(new Color(245, 247, 250));

        // 초기 기본
        contentArea.add(defaultPage(), "DEFAULT");
        
        // 실행 직후 장바구니에서 주문 시 실행 안되는 현상 해결을 위해 미리 OrderPanel 생성
        orderPanel = new OrderPanel();
        contentArea.add(orderPanel, "ORDER");
        
        add(contentArea, BorderLayout.CENTER);

        contentCard.show(contentArea, "DEFAULT");
    }


    // 버튼 생성
    private JButton sideButton(String text, String action) {
        JButton btn = new JButton(text);

        btn.putClientProperty("JButton.buttonType", "roundRect");
        btn.setFocusPainted(false);
        btn.setBackground(new Color(74, 144, 226));
        btn.setForeground(Color.WHITE);
        btn.setBorderPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));

        btn.addActionListener(e -> Buttons(action));
        return btn;
    }


    // 버튼 동작 설정
    private void Buttons(String action) {

        switch (action) {

            case "INFO":
                if (infoPanel == null) {
                    infoPanel = new InfoPanel();
                    contentArea.add(infoPanel, "INFO");
                } else {
                    infoPanel.reload();
                }
                contentCard.show(contentArea, "INFO");
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
                
            case "SEARCH":
                if (searchPanel == null) {
                    searchPanel = new SearchPanel();  // SearchPanel 생성
                    contentArea.add(searchPanel, "SEARCH");
                }
                contentCard.show(contentArea, "SEARCH");
                break;

            case "ITEMLIST":
                if (cartPanel == null) {
                	cartPanel = new CartPanel(this);
                    contentArea.add(cartPanel, "ITEMLIST");
                } else {
                    cartPanel.refresh(); 
                }
                contentCard.show(contentArea, "ITEMLIST");
                break;
            
            case "ORDER":
                orderPanel.refreshOrder();
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

            case "EXIT":
                int result = JOptionPane.showConfirmDialog(
                        JOptionPane.getRootFrame(),   // ← this 대신 사용!
                        "정말 종료하시겠습니까?",
                        "종료 확인",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );

                if (result == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
                break;
        }
    }
    
    
    // 패널에서 패널로 전환
    public void showPage(String page) {
        contentCard.show(contentArea, page);
    }
    
    public void refreshOrderPanel() {
        if (orderPanel != null) {
            orderPanel.refreshOrder();
        }
    }


    // 기본 페이지
    private JPanel defaultPage() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(245, 247, 250));

        JLabel label = new JLabel("메뉴를 선택하세요");
        label.setFont(new Font("SansSerif", Font.BOLD, 28));

        panel.add(label);
        return panel;
    }
}

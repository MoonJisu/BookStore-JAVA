package com.market.ui;

import javax.swing.*;
import java.awt.*;
import com.formdev.flatlaf.FlatLightLaf;

public class MainFrame extends JFrame {
    private CardLayout card;
    private JPanel pages;

    public MainFrame() {
        FlatLightLaf.setup();

        setTitle("온라인 서점");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        card = new CardLayout();
        pages = new JPanel(card);

        // 로그인, 메뉴 화면 추가
        pages.add(new LoginPanel(this), "LOGIN");
        pages.add(new MainPanel(this), "MENU");
        

        add(pages);

        // 로그인 화면 설정
        card.show(pages, "LOGIN");

        
        setVisible(true);

    }

    public void showPage(String page) {
        card.show(pages, page);
    }

    public static void main(String[] args) {
        new MainFrame();
    }
}
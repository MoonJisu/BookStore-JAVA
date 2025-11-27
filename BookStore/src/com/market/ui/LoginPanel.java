package com.market.ui;

import javax.swing.*;
import java.awt.*;
import com.market.main.Welcome;
import com.market.member.User;

public class LoginPanel extends JPanel {

	public LoginPanel(MainFrame main) {

        setLayout(new GridBagLayout());
        setBackground(new Color(245, 245, 245));

        // 카드 박스
        JPanel card = new JPanel();
        card.setBackground(Color.WHITE);
        card.setPreferredSize(new Dimension(700, 550));
        card.setLayout(new GridBagLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.anchor = GridBagConstraints.CENTER;

        int y = 0;


        // 상단 이미지
        gbc.gridy = y++;
        gbc.insets = new Insets(0, 0, 10, 0);

        ImageIcon icon = new ImageIcon("src/com/market/image/Bookstore_Icon.png");
        Image img = icon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
        JLabel imgLabel = new JLabel(new ImageIcon(img));

        card.add(imgLabel, gbc);


        // 제목
        gbc.gridy = y++;
        gbc.insets = new Insets(0, 0, 30, 0);

        JLabel title = new JLabel("[ 고객 정보를 입력하세요 ]", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 15));
        card.add(title, gbc);


        // 입력칸
        gbc.gridwidth = 2;
        gbc.insets = new Insets(5, 0, 5, 0);

        
        // 이름
        gbc.gridy = y++;
        JTextField username = new JTextField();
        username.putClientProperty("JTextField.placeholderText", "이름을 입력하세요");
        username.setPreferredSize(new Dimension(260, 40));
        card.add(username, gbc);
        
        
        // 전화번호
        gbc.gridy = y++;
        JTextField phone = new JTextField();
        phone.putClientProperty("JTextField.placeholderText", "전화번호를 입력하세요");
        phone.setPreferredSize(new Dimension(260, 40));
        card.add(phone, gbc);

        
        // 로그인 버튼
        gbc.gridy = y++;
        gbc.insets = new Insets(20, 0, 10, 0);

        JButton signInBtn = new JButton("확인");
        signInBtn.setForeground(Color.WHITE);
        signInBtn.setBackground(new Color(120, 160, 210));
        signInBtn.setFocusPainted(false);
        signInBtn.setOpaque(true);
        signInBtn.setBorderPainted(false);
        signInBtn.setPreferredSize(new Dimension(260, 40));

        card.add(signInBtn, gbc);
        
        
        // 버튼 클릭시
        signInBtn.addActionListener(e -> {
            String name = username.getText();
            String phoneStr = phone.getText();

            if (name.isEmpty() || phoneStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "이름과 전화번호를 입력하세요.", "입력 오류", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                int phoneNum = Integer.parseInt(phoneStr);

                // 1) User 객체 생성
                User user = new User(name, phoneNum);

                // 2) 사용자에 저장
                Welcome.mUser = user;

                // 3) DB 조회 또는 신규등록
                Welcome.loginOrRegisterUser(user);

                // 4) 메뉴 화면으로 이동
                main.showPage("MENU");

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "전화번호는 숫자만 입력해야 합니다.",
                        "입력 오류",
                        JOptionPane.ERROR_MESSAGE);
            }
        });	     

        // 카드 배치
        add(card);
    }
}

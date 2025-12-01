package com.market.ui.buttons;

import javax.swing.*;
import java.awt.*;
import com.market.dao.AdminDao; // DAO import 추가
import com.market.dao.BookDao; // AdminPanel에서 AddBookPanel을 열고, AddBookPanel에서 BookDao를 사용하기 때문에 명시적 import는 필요 없음.

public class AdminPanel extends JPanel {

	private JPanel card;	// 로그인 카드
	private JPanel adminContent;	// 로그인 성공 후 관리자 화면

	public AdminPanel() {

		setLayout(new GridBagLayout());
		setBackground(new Color(245, 245, 245));

		buildLoginUI(); // 로그인 UI 생성
	}

	private void buildLoginUI() {

		card = new JPanel();
		card.setBackground(Color.WHITE);
		card.setPreferredSize(new Dimension(600, 500));
		card.setLayout(new GridBagLayout());
		card.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true),
				BorderFactory.createEmptyBorder(20, 20, 20, 20)
				));

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(10, 0, 10, 0);
		gbc.gridx = 0;
		gbc.gridwidth = 2;


		// 이미지 (맨 위, 중앙)
		gbc.gridy = 0;
		gbc.insets = new Insets(0, 0, 10, 0);
	gbc.anchor = GridBagConstraints.CENTER;

		ImageIcon icon = new ImageIcon("src/com/market/image/Admin_Icon.png");
		Image img = icon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
		JLabel imgLabel = new JLabel(new ImageIcon(img));

	card.add(imgLabel, gbc);


		// 2) 제목
		gbc.gridy = 1;
		gbc.insets = new Insets(0, 0, 20, 0);

		JLabel title = new JLabel("[ 관리자 계정으로 로그인하세요 ]", SwingConstants.CENTER);
		title.setFont(new Font("SansSerif", Font.BOLD, 15));
		card.add(title, gbc);


		// 입력칸부터는 gridwidth = 1
		gbc.gridwidth = 2;
		gbc.gridy = 2;
		gbc.insets = new Insets(5, 0, 5, 0);

		// 아이디 입력
		JTextField txtId = new JTextField();
		txtId.putClientProperty("JTextField.placeholderText", "아이디를 입력하세요");
		txtId.setPreferredSize(new Dimension(250, 40));
		card.add(txtId, gbc);

		// 비밀번호 입력
		gbc.gridy = 3;
		JPasswordField txtPw = new JPasswordField();
		txtPw.putClientProperty("JTextField.placeholderText", "비밀번호를 입력하세요");
		txtPw.setPreferredSize(new Dimension(250, 40));
		card.add(txtPw, gbc);


		// 4) 로그인 버튼
		gbc.gridy = 4;
		gbc.insets = new Insets(20, 0, 10, 0);

		JButton signInBtn = new JButton("로그인");
		signInBtn.setForeground(Color.WHITE);
		signInBtn.setBackground(new Color(120, 160, 210));
		signInBtn.setFocusPainted(false);
		signInBtn.setOpaque(true);
		signInBtn.setBorderPainted(false);
		signInBtn.setPreferredSize(new Dimension(250, 40));
		card.add(signInBtn, gbc);

		// 이벤트
		signInBtn.addActionListener(e -> {
			String id = txtId.getText().trim();
			String pw = new String(txtPw.getPassword()).trim();

			if (id.isEmpty() || pw.isEmpty()) {
				JOptionPane.showMessageDialog(this,
						"아이디와 비밀번호를 입력하세요.",
						"입력 오류",
						JOptionPane.WARNING_MESSAGE);
				return;
			}

				// DAO 호출: isAdminValid
			if (AdminDao.isAdminValid(id, pw)) {
				JOptionPane.showMessageDialog(this, "관리자 로그인 성공!");

				showAdminManager(); // 관리자 화면으로 전환
			} else {
				JOptionPane.showMessageDialog(this,
						"아이디 또는 비밀번호가 틀렸습니다.",
						"로그인 실패",
						JOptionPane.ERROR_MESSAGE);
			}
		});

		add(card);
	}

	// 로그인 성공 시 관리자 패널 출력
	private void showAdminManager() {

		removeAll(); // 로그인 화면 삭제
		setLayout(new BorderLayout());

		adminContent = new AddBookPanel();	// 책 등록 화면
		add(adminContent, BorderLayout.CENTER);

		revalidate();
		repaint();
		}
}
package com.market.ui.buttons;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;

import com.market.main.Welcome;
import java.util.List;
import com.market.dao.OrderDao; // DAO import 추가

public class BillPanel extends DefaultPanel {

	private JTable table;
	private DefaultTableModel model;
	private JButton btnView;

	public BillPanel() {

		// 주문 조회 설정
		JPanel card = createCard(800, 600);
		card.setLayout(new BorderLayout());
		card.setBackground(Color.WHITE);

		JLabel title = new JLabel("구매 내역");
		title.setFont(new Font("SansSerif", Font.BOLD, 26));

		JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		titlePanel.setOpaque(false);
		titlePanel.add(title);

		// 테이블
		model = new DefaultTableModel(new String[]{
				"주문번호", "주문날짜", "배송지", "총 금액"
		}, 0);

		table = new JTable(model);
		table.setRowHeight(25);

		JScrollPane scroll = new JScrollPane(table);

		// 조회 버튼
		btnView = new JButton("조회하기");
		btnView.setFont(new Font("SansSerif", Font.BOLD, 16));
		btnView.addActionListener(e -> openDetail());

		JPanel bottom = new JPanel();
		bottom.add(btnView);

		// 배치
		card.add(titlePanel, BorderLayout.NORTH);
		card.add(scroll, BorderLayout.CENTER);
		card.add(bottom, BorderLayout.SOUTH);

		setLayout(new GridBagLayout());
		add(card);

		refresh();
	}


	public void refresh() {
		model.setRowCount(0);

        // DAO 호출: getOrderList
		List<Welcome.OrderSummary> list = Welcome.getOrderList(Welcome.currentUserId);

		for (Welcome.OrderSummary s : list) {
			model.addRow(new Object[]{
					s.orderId,
					s.date,
					s.address,
					s.totalPrice + "원"
					}
			);}
    }

	private void openDetail() {

		int row = table.getSelectedRow();
		if (row == -1) {
			JOptionPane.showMessageDialog(this, "주문을 선택하세요");
			return;
		}

		int orderId = (int) table.getValueAt(row, 0);

		new DetailPanel(orderId).setVisible(true);
		}
}
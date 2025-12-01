package com.market.ui.buttons;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.ArrayList;

import com.market.bookitem.Book;
import com.market.main.Welcome;

public class SearchPanel extends DefaultPanel {

    private JTextField searchField;
    private JButton searchButton;
    private JButton addToCartButton;

    private JTable table;
    private DefaultTableModel model;

    public SearchPanel() {
    	// 메인 카드 패널
    	JPanel card = createCard(800, 600);
    	card.setOpaque(true);
    	card.setBackground(Color.WHITE);
    	card.setLayout(new BorderLayout(10, 10));

    	// ====================== 상단 제목 =========================
    	JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    	JLabel title = new JLabel("도서 검색");
    	title.setFont(new Font("SansSerif", Font.BOLD, 26));
    	titlePanel.setOpaque(false);
    	titlePanel.add(title);

    	// ====================== 검색 영역 =========================
    	JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    	searchPanel.setOpaque(false);

    	searchField = new JTextField(25);
    	searchButton = new JButton("검색");

    	searchPanel.add(searchField);
    	searchPanel.add(searchButton);

    	// ====================== 테이블 설정 =========================
    	model = new DefaultTableModel(new String[]{
    			"도서ID", "도서명", "저자", "가격" 
    	}, 0) {
    		@Override
    		public boolean isCellEditable(int r, int c) {
    			return false; // 수정 방지
    		}
    	};
        
        table = new JTable(model);
        table.setRowHeight(24);

        // 컬럼 폭 설정
        TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(120);
        columnModel.getColumn(1).setPreferredWidth(200);
        columnModel.getColumn(2).setPreferredWidth(120);
        columnModel.getColumn(3).setPreferredWidth(60);

        // 테이블 정렬 기능 추가
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setPreferredSize(new Dimension(780, 280)); // 테이블 크기 감소

        // ====================== 하단 버튼 =========================
         JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setOpaque(false);

        addToCartButton = new JButton("장바구니 담기");
        bottomPanel.add(addToCartButton);

         // ====================== 조합 =========================
        JPanel topArea = new JPanel();
        topArea.setLayout(new BoxLayout(topArea, BoxLayout.Y_AXIS));
        topArea.setOpaque(false);

        topArea.add(titlePanel);
        topArea.add(searchPanel);

        card.add(topArea, BorderLayout.NORTH);// (제목 + 검색창)
        card.add(scroll, BorderLayout.CENTER);// 테이블
        card.add(bottomPanel, BorderLayout.SOUTH); // 장바구니 버튼

        add(card);

        // ====================== 이벤트 바인딩 =========================
        searchButton.addActionListener(e -> doSearch());
        addToCartButton.addActionListener(e -> addSelectedToCart());
        }

    // 🔍 검색 실행
    private void doSearch() {
        String keyword = searchField.getText().trim();

        if (keyword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "검색어를 입력하세요.");
            return;
        }
        
        model.setRowCount(0); // 기존 검색 결과 삭제
        try {
            ArrayList<Book> list = new ArrayList<>();
            // DAO 호출: searchBookList
            Welcome.searchBookList(list, keyword);
            if (list.isEmpty()) {
                JOptionPane.showMessageDialog(this, "검색 결과가 없습니다.");
                return;
            }
            
            for (Book b : list) {
                model.addRow(new Object[]{
                    b.getBookId(),
                    b.getName(),
                    b.getAuthor(),
                    b.getUnitPrice() + "원"
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "검색 오류: " + ex.getMessage());
        }
    }
     // 🛒 선택 항목 장바구니에 추가
    private void addSelectedToCart() {
        int row = table.getSelectedRow();
        if (row == -1) {
        JOptionPane.showMessageDialog(this, "추가할 도서를 선택하세요.");
        return;
    }
    // 정렬 상태를 고려한 실제 모델 인덱스 
    int modelRow = table.convertRowIndexToModel(row);
    String bookId = (String) model.getValueAt(modelRow, 0);

    try {
        ArrayList<Book> list = new ArrayList<>();
        // DAO 호출: searchBookList (bookId로 단일 조회)
        Welcome.searchBookList(list, bookId);
        if (list.isEmpty()) {
            JOptionPane.showMessageDialog(this, "해당 도서를 찾을 수 없습니다.");
            return;
        }
        
        Book book = list.get(0);
        if (!Welcome.isCartInBook(bookId)) {
            Welcome.mCart.insertBook(book);
            JOptionPane.showMessageDialog(this, "장바구니에 추가되었습니다.");
        } else {
            JOptionPane.showMessageDialog(this, "이미 장바구니에 있는 도서입니다.");
        }

    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "장바구니 추가 오류: " + ex.getMessage());
        }
    }
}
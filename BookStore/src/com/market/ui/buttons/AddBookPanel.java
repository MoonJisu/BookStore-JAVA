package com.market.ui.buttons;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.*;
import com.market.main.Welcome;

public class AddBookPanel extends DefaultPanel {

    private JTextField idField, titleField, priceField,
            authorField, categoryField, releaseField, descField;

    private JLabel imgLabel;
    private File selectedImageFile;

    private static final String DEFAULT_IMAGE_PATH = "src/com/market/image/Book_Icon.png";

    public AddBookPanel() {
    	
    	// AddBookPanel 설정
        JPanel card = createCard(850, 650);
        card.setLayout(new BorderLayout());
        card.setBackground(Color.WHITE);

        // 상단 타이틀
        JPanel titlePanel = new JPanel(new BorderLayout());
        JLabel title = new JLabel("도서 추가");
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        titlePanel.setOpaque(false);
        titlePanel.add(title, BorderLayout.NORTH);
        card.add(titlePanel, BorderLayout.NORTH);

        // 중앙 입력 Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 35, 15, 35);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        int y = 0;

        idField = addRow(formPanel, gbc, y++, "도서 ID");
        titleField = addRow(formPanel, gbc, y++, "도서명");
        priceField = addRow(formPanel, gbc, y++, "가격");
        authorField = addRow(formPanel, gbc, y++, "저자");
        categoryField = addRow(formPanel, gbc, y++, "분야");
        releaseField = addRow(formPanel, gbc, y++, "출판일");

        // 항목 addRow로 통일
        descField = addRow(formPanel, gbc, y++, "설명");


        // 이미지 선택
        gbc.gridx = 0; gbc.gridy = y;
        JLabel lblImg = new JLabel("이미지");
        lblImg.setFont(new Font("SansSerif", Font.BOLD, 15));
        formPanel.add(lblImg, gbc);

        gbc.gridx = 1;

        JPanel imgPanel = new JPanel(new BorderLayout());
        imgPanel.setOpaque(false);

        imgLabel = new JLabel("이미지 없음(기본 이미지)");
        imgLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));

        JButton btnChoose = new JButton("이미지 선택");
        btnChoose.addActionListener(e -> chooseImage());

        imgPanel.add(imgLabel, BorderLayout.CENTER);
        imgPanel.add(btnChoose, BorderLayout.EAST);

        formPanel.add(imgPanel, gbc);
        y++;

        card.add(formPanel, BorderLayout.CENTER);


        // 등록 버튼
        JButton btnAdd = new JButton("도서 등록");
        btnAdd.setPreferredSize(new Dimension(180, 45));
        btnAdd.setFont(new Font("SansSerif", Font.BOLD, 16));
        btnAdd.addActionListener(e -> saveBook());

        JPanel bottom = new JPanel();
        bottom.setBackground(Color.WHITE);
        bottom.add(btnAdd);

        card.add(bottom, BorderLayout.SOUTH);

        setLayout(new GridBagLayout());
        add(card);
    }


    // 입력칸 생성 메서드
    private JTextField addRow(JPanel panel, GridBagConstraints gbc, int y, String labelText) {

        gbc.gridx = 0;
        gbc.gridy = y;

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("SansSerif", Font.BOLD, 15));
        panel.add(label, gbc);

        gbc.gridx = 1;

        JTextField field = new JTextField();
        field.setPreferredSize(new Dimension(450, 40));
        field.setFont(new Font("SansSerif", Font.PLAIN, 15));

        panel.add(field, gbc);

        return field;
    }


    // 이미지 선택
    private void chooseImage() {

        JFileChooser fc = new JFileChooser();

        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            selectedImageFile = fc.getSelectedFile();
            imgLabel.setText("선택된 이미지 : " + selectedImageFile.getName());
        }
    }


    // 입력 폼 초기화
    private void clearForm() {
        idField.setText("");
        titleField.setText("");
        priceField.setText("");
        authorField.setText("");
        categoryField.setText("");
        releaseField.setText("");
        descField.setText("");

        selectedImageFile = null;
        imgLabel.setText("이미지 없음 (Book_Icon.png 사용)");
    }


    // 도서 저장 + 이미지 복사
    private void saveBook() {

        try {
            String[] data = new String[]{
                    idField.getText(),
                    titleField.getText(),
                    priceField.getText(),
                    authorField.getText(),
                    descField.getText(),
                    categoryField.getText(),
                    releaseField.getText()
            };

            Welcome.insertBookToDB(data);

            // 이미지 선택 안 했으면 기본 이미지 사용
            File imgToCopy = (selectedImageFile != null)
                    ? selectedImageFile
                    : new File(DEFAULT_IMAGE_PATH);

            File dest = new File("src/com/market/image/" + data[0] + ".jpg");
            Files.copy(imgToCopy.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);

            JOptionPane.showMessageDialog(this, "도서가 성공적으로 등록되었습니다!");

            // 초기화
            clearForm();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "도서 등록 실패: " + e.getMessage());
        }
    }
}

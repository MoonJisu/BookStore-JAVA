package com.market.ui.buttons;

import javax.swing.*;
import java.awt.*;

public class DefaultPanel extends JPanel {

    public DefaultPanel() {
        setLayout(new GridBagLayout());
        setBackground(new Color(245, 247, 250));
    }

    protected JPanel createCard(int width, int height) {
        JPanel card = new JPanel();
        card.setPreferredSize(new Dimension(width, height));
        card.setBackground(Color.WHITE);
        card.setLayout(new GridBagLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        return card;
    }

    protected JLabel createTitle(String text) {
        JLabel title = new JLabel(text);
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        return title;
    }
}

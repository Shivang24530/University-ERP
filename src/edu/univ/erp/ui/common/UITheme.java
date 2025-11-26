package edu.univ.erp.ui.common;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class UITheme {
    public static final Color PRIMARY_COLOR = new Color(44, 62, 80); // Dark Blue
    public static final Color SECONDARY_COLOR = new Color(52, 152, 219); // Blue
    public static final Color ACCENT_COLOR = new Color(231, 76, 60); // Red
    public static final Color BACKGROUND_COLOR = new Color(236, 240, 241); // Light Grey
    public static final Color TEXT_COLOR = new Color(44, 62, 80);
    public static final Color WHITE = Color.WHITE;

    public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    public static final Font SUBTITLE_FONT = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font REGULAR_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 14);

    public static void styleFrame(JFrame frame) {
        frame.getContentPane().setBackground(BACKGROUND_COLOR);
    }

    public static void styleButton(JButton button) {
        button.setFont(BUTTON_FONT);
        button.setBackground(SECONDARY_COLOR);
        button.setForeground(WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(true);
        button.setOpaque(true);
        // thin black outline
        button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        // reasonable internal padding so text is not clipped
        button.setMargin(new java.awt.Insets(6, 12, 6, 12));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover Effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(SECONDARY_COLOR.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(SECONDARY_COLOR);
            }
        });
    }
    
    public static void styleDangerButton(JButton button) {
        styleButton(button);
        button.setBackground(ACCENT_COLOR);
        
        // Overwrite Hover Effect for Danger
        for (java.awt.event.MouseListener ml : button.getMouseListeners()) {
             if (ml instanceof MouseAdapter) {
                 button.removeMouseListener(ml);
             }
        }
        
        // ensure danger buttons keep visible border and padding
        button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        button.setMargin(new java.awt.Insets(6, 12, 6, 12));
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(ACCENT_COLOR.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(ACCENT_COLOR);
            }
        });
    }

    public static void styleLabel(JLabel label, Font font) {
        label.setFont(font);
        label.setForeground(TEXT_COLOR);
    }
    
    public static void styleTextField(JTextField field) {
        field.setFont(REGULAR_FONT);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR, 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
    }
}

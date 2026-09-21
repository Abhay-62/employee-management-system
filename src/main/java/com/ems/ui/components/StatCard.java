package com.ems.ui.components;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Reusable metric card displaying an overview statistic with consistent FlatLaf styling.
 */
public class StatCard extends JPanel {

    private final JLabel valueLabel;
    private final JLabel subtitleLabel;

    public StatCard(String title, String initialValue, String subtitle, Color accentColor) {
        setLayout(new BorderLayout());
        setBackground(UIConstants.COLOR_SURFACE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIConstants.COLOR_BORDER, 1),
                new EmptyBorder(16, 20, 16, 20)
        ));

        // Top Header: Label + Accent Dot
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel(title != null ? title.toUpperCase() : "");
        titleLabel.setFont(UIConstants.FONT_CARD_LABEL);
        titleLabel.setForeground(UIConstants.COLOR_TEXT_MUTED);

        if (accentColor != null) {
            JLabel dot = new JLabel("●");
            dot.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            dot.setForeground(accentColor);
            headerPanel.add(dot, BorderLayout.EAST);
        }
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Center: Metric Value
        valueLabel = new JLabel(initialValue != null ? initialValue : "0");
        valueLabel.setFont(UIConstants.FONT_CARD_VALUE);
        valueLabel.setForeground(UIConstants.COLOR_TEXT_MAIN);

        // Bottom: Subtitle / Context
        subtitleLabel = new JLabel(subtitle != null ? subtitle : "");
        subtitleLabel.setFont(UIConstants.FONT_SMALL);
        subtitleLabel.setForeground(UIConstants.COLOR_TEXT_MUTED);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        contentPanel.add(Box.createVerticalStrut(6));
        contentPanel.add(valueLabel);
        contentPanel.add(Box.createVerticalStrut(4));
        contentPanel.add(subtitleLabel);

        add(headerPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
    }

    public void setValue(String value) {
        valueLabel.setText(value);
    }

    public void setSubtitle(String subtitle) {
        subtitleLabel.setText(subtitle);
    }
}

package com.ems.ui.components;

import com.ems.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Reusable top bar displaying the current section title, system connection status,
 * and current authenticated user information with role badge.
 */
public class TopBarPanel extends JPanel {

    public TopBarPanel(String pageTitle, String pageSubtitle, User user) {
        setLayout(new BorderLayout());
        setBackground(UIConstants.COLOR_SURFACE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, UIConstants.COLOR_BORDER),
                new EmptyBorder(14, 28, 14, 28)
        ));
        setPreferredSize(new Dimension(800, 68));

        // Left section: Title and Subtitle
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);

        JLabel titleLabel = new JLabel(pageTitle != null ? pageTitle : "Dashboard");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(UIConstants.COLOR_TEXT_MAIN);

        JLabel subtitleLabel = new JLabel(pageSubtitle != null ? pageSubtitle : "Overview & Key Metrics");
        subtitleLabel.setFont(UIConstants.FONT_SMALL);
        subtitleLabel.setForeground(UIConstants.COLOR_TEXT_MUTED);

        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(2));
        titlePanel.add(subtitleLabel);

        // Right section: Status indicator + User Pill
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 0));
        rightPanel.setOpaque(false);

        // Status indicator
        JPanel statusPill = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        statusPill.setOpaque(false);
        JLabel statusDot = new JLabel("●");
        statusDot.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        statusDot.setForeground(UIConstants.COLOR_SUCCESS);
        JLabel statusText = new JLabel("Live Database Connected");
        statusText.setFont(UIConstants.FONT_SMALL);
        statusText.setForeground(UIConstants.COLOR_TEXT_MUTED);
        statusPill.add(statusDot);
        statusPill.add(statusText);

        // Role badge
        String roleStr = user != null && user.getRole() != null ? user.getRole().toUpperCase() : "USER";
        Color badgeBg = "ADMIN".equals(roleStr) ? new Color(254, 243, 199) : new Color(219, 234, 254);
        Color badgeFg = "ADMIN".equals(roleStr) ? new Color(180, 83, 9) : new Color(30, 64, 175);

        JLabel roleBadge = new JLabel(" " + roleStr + " ");
        roleBadge.setFont(UIConstants.FONT_BADGE);
        roleBadge.setForeground(badgeFg);
        roleBadge.setBackground(badgeBg);
        roleBadge.setOpaque(true);
        roleBadge.setBorder(new EmptyBorder(3, 8, 3, 8));

        // User email
        String email = user != null ? user.getOfficialEmail() : "user@ems.com";
        JLabel userLabel = new JLabel(email);
        userLabel.setFont(UIConstants.FONT_BODY_BOLD);
        userLabel.setForeground(UIConstants.COLOR_TEXT_MAIN);

        rightPanel.add(statusPill);
        rightPanel.add(Box.createHorizontalStrut(8));
        rightPanel.add(roleBadge);
        rightPanel.add(userLabel);

        add(titlePanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.EAST);
    }
}

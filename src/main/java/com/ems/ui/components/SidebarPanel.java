package com.ems.ui.components;

import com.ems.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * Reusable dark sidebar panel for Admin and Employee dashboard navigation.
 * Uses FlatLaf dark sidebar styling (#0F172A), provides active state highlights,
 * handles "Coming Soon" notifications for placeholder items, and provides a Logout action.
 */
public class SidebarPanel extends JPanel {

    private final Runnable logoutAction;

    public SidebarPanel(User user, List<String> navItems, String activeItem, Runnable logoutAction) {
        this.logoutAction = logoutAction;

        setLayout(new BorderLayout());
        setBackground(UIConstants.COLOR_SIDEBAR);
        setPreferredSize(new Dimension(240, 700));

        // 1. Top Brand Header
        JPanel brandPanel = new JPanel();
        brandPanel.setLayout(new BoxLayout(brandPanel, BoxLayout.Y_AXIS));
        brandPanel.setBackground(UIConstants.COLOR_SIDEBAR);
        brandPanel.setBorder(new EmptyBorder(24, 20, 20, 20));

        JPanel logoRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        logoRow.setOpaque(false);

        JLabel logoPill = new JLabel(" EMS ");
        logoPill.setFont(new Font("Segoe UI", Font.BOLD, 12));
        logoPill.setForeground(Color.WHITE);
        logoPill.setBackground(UIConstants.COLOR_PRIMARY);
        logoPill.setOpaque(true);
        logoPill.setBorder(new EmptyBorder(3, 6, 3, 6));

        JLabel brandTitle = new JLabel("Enterprise EMS");
        brandTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        brandTitle.setForeground(Color.WHITE);

        logoRow.add(logoPill);
        logoRow.add(brandTitle);

        JLabel brandSubtitle = new JLabel("Workplace Management");
        brandSubtitle.setFont(UIConstants.FONT_SMALL);
        brandSubtitle.setForeground(UIConstants.COLOR_SIDEBAR_TEXT);
        brandSubtitle.setBorder(new EmptyBorder(4, 2, 0, 0));

        brandPanel.add(logoRow);
        brandPanel.add(brandSubtitle);

        // 2. Navigation Items List (Scrollable if window height is small)
        JPanel navListPanel = new JPanel();
        navListPanel.setLayout(new BoxLayout(navListPanel, BoxLayout.Y_AXIS));
        navListPanel.setBackground(UIConstants.COLOR_SIDEBAR);
        navListPanel.setBorder(new EmptyBorder(8, 12, 8, 12));

        for (String item : navItems) {
            if ("Logout".equalsIgnoreCase(item)) {
                continue; // Rendered separately at the bottom
            }

            boolean isActive = item.equalsIgnoreCase(activeItem);
            JPanel navButton = createNavItemPanel(item, isActive);
            navListPanel.add(navButton);
            navListPanel.add(Box.createVerticalStrut(3));
        }

        JScrollPane scrollPane = new JScrollPane(navListPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        // 3. Bottom User Profile & Logout Panel
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setBackground(new Color(11, 17, 32)); // slightly darker accent
        bottomPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(30, 41, 59)),
                new EmptyBorder(16, 16, 16, 16)
        ));

        // User identity summary
        String email = user != null ? user.getOfficialEmail() : "user@ems.com";
        String role = user != null && user.getRole() != null ? user.getRole().toUpperCase() : "USER";

        JLabel userEmailLabel = new JLabel(email);
        userEmailLabel.setFont(UIConstants.FONT_BODY_BOLD);
        userEmailLabel.setForeground(Color.WHITE);
        userEmailLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel roleLabel = new JLabel("Role: " + role);
        roleLabel.setFont(UIConstants.FONT_SMALL);
        roleLabel.setForeground(UIConstants.COLOR_SIDEBAR_TEXT);
        roleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Logout Button
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setFont(UIConstants.FONT_BODY_BOLD);
        logoutBtn.setForeground(new Color(248, 113, 113)); // subtle red
        logoutBtn.setBackground(new Color(30, 41, 59));
        logoutBtn.setFocusPainted(false);
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        logoutBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        logoutBtn.addActionListener(e -> {
            if (this.logoutAction != null) {
                this.logoutAction.run();
            }
        });

        bottomPanel.add(userEmailLabel);
        bottomPanel.add(Box.createVerticalStrut(2));
        bottomPanel.add(roleLabel);
        bottomPanel.add(Box.createVerticalStrut(12));
        bottomPanel.add(logoutBtn);

        add(brandPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel createNavItemPanel(String itemName, boolean isActive) {
        JPanel panel = new JPanel(new BorderLayout(8, 0));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        panel.setPreferredSize(new Dimension(216, 36));
        panel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        Color normalBg = isActive ? UIConstants.COLOR_SIDEBAR_HOVER : UIConstants.COLOR_SIDEBAR;
        panel.setBackground(normalBg);

        // Left active accent bar
        JPanel accentBar = new JPanel();
        accentBar.setPreferredSize(new Dimension(3, 36));
        accentBar.setBackground(isActive ? UIConstants.COLOR_PRIMARY : UIConstants.COLOR_SIDEBAR);
        panel.add(accentBar, BorderLayout.WEST);

        // Item text
        JLabel label = new JLabel(itemName);
        label.setFont(isActive ? UIConstants.FONT_BODY_BOLD : UIConstants.FONT_BODY);
        label.setForeground(isActive ? Color.WHITE : UIConstants.COLOR_SIDEBAR_TEXT);
        panel.add(label, BorderLayout.CENTER);

        // Optional badge for non-active items
        if (!isActive) {
            JLabel soonBadge = new JLabel("Soon");
            soonBadge.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            soonBadge.setForeground(new Color(100, 116, 139));
            soonBadge.setBorder(new EmptyBorder(0, 0, 0, 8));
            panel.add(soonBadge, BorderLayout.EAST);
        }

        // Hover & Click listeners
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!isActive) {
                    panel.setBackground(UIConstants.COLOR_SIDEBAR_HOVER);
                    label.setForeground(Color.WHITE);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!isActive) {
                    panel.setBackground(UIConstants.COLOR_SIDEBAR);
                    label.setForeground(UIConstants.COLOR_SIDEBAR_TEXT);
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (!isActive) {
                    JOptionPane.showMessageDialog(
                            SidebarPanel.this,
                            "The '" + itemName + "' module is scheduled for implementation in upcoming milestones.",
                            "Coming Soon",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                }
            }
        });

        return panel;
    }
}

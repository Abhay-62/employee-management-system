package com.ems.ui;

import com.ems.model.User;
import com.ems.session.SessionManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Placeholder dashboard window for authenticated EMPLOYEE users.
 * Proves successful role-based authentication, session management, and logout routing.
 */
public class EmployeeDashboard extends JFrame {

    private final User user;

    public EmployeeDashboard(User user) {
        this.user = user;
        initUI();
    }

    private void initUI() {
        setTitle("Employee Dashboard - Employee Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(750, 500);
        setLocationRelativeTo(null);

        JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.setBackground(new Color(248, 250, 252));

        // 1. Navigation Bar
        JPanel navBar = new JPanel(new BorderLayout());
        navBar.setBackground(Color.WHITE);
        navBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)),
                new EmptyBorder(16, 24, 16, 24)
        ));

        JLabel brandLabel = new JLabel("EMS Portal");
        brandLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        brandLabel.setForeground(new Color(15, 23, 42));

        JPanel rightNav = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        rightNav.setOpaque(false);

        JLabel roleBadge = new JLabel(" ROLE: EMPLOYEE ");
        roleBadge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        roleBadge.setForeground(new Color(30, 64, 175));
        roleBadge.setBackground(new Color(219, 234, 254));
        roleBadge.setOpaque(true);
        roleBadge.setBorder(new EmptyBorder(4, 8, 4, 8));

        JLabel userEmailLabel = new JLabel("Welcome, " + (user != null ? user.getOfficialEmail() : "Employee"));
        userEmailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        userEmailLabel.setForeground(new Color(71, 85, 105));

        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        logoutButton.setForeground(new Color(220, 38, 38));
        logoutButton.setBackground(new Color(254, 242, 242));
        logoutButton.setFocusPainted(false);
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutButton.addActionListener(e -> performLogout());

        rightNav.add(roleBadge);
        rightNav.add(userEmailLabel);
        rightNav.add(logoutButton);

        navBar.add(brandLabel, BorderLayout.WEST);
        navBar.add(rightNav, BorderLayout.EAST);

        // 2. Main Content
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(32, 32, 32, 32));

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(32, 40, 32, 40)
        ));

        JLabel titleLabel = new JLabel("Employee Dashboard");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(new Color(15, 23, 42));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel welcomeLabel = new JLabel("Welcome, " + (user != null ? user.getOfficialEmail() : "employee@ems.com"));
        welcomeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        welcomeLabel.setForeground(new Color(37, 99, 235));
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel statusLabel = new JLabel("Authentication & Role Routing: SUCCESS (Role: EMPLOYEE)");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        statusLabel.setForeground(new Color(22, 101, 52));
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sessionInfo = new JLabel("Session State: Active in SessionManager (User ID: " +
                (user != null ? user.getUserId() : "N/A") + ")");
        sessionInfo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sessionInfo.setForeground(new Color(100, 116, 139));
        sessionInfo.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(welcomeLabel);
        card.add(Box.createVerticalStrut(16));
        card.add(statusLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(sessionInfo);

        contentPanel.add(card);

        rootPanel.add(navBar, BorderLayout.NORTH);
        rootPanel.add(contentPanel, BorderLayout.CENTER);

        setContentPane(rootPanel);
    }

    private void performLogout() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to log out of your session?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            // Clear in-memory session
            SessionManager.getInstance().logout();

            // Dispose dashboard
            dispose();

            // Return to login screen
            SwingUtilities.invokeLater(() -> new LoginScreen().setVisible(true));
        }
    }
}

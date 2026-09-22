package com.ems.ui;

import com.ems.dto.EmployeeDashboardData;
import com.ems.model.User;
import com.ems.service.DashboardService;
import com.ems.session.SessionManager;
import com.ems.ui.components.SidebarPanel;
import com.ems.ui.components.UIConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Arrays;

public class EmployeeDashboard extends JFrame {
    private final User user;
    private final DashboardService service = new DashboardService();
    private JLabel profileLabel, pendingLabel, approvedLabel, statusLabel;

    public EmployeeDashboard(User user) {
        this.user = user != null ? user : SessionManager.getInstance().getCurrentUser();
        validateSession();
        initUI();
        loadData();
    }

    private void validateSession() {
        if (user == null || !SessionManager.getInstance().isLoggedIn()
                || !"EMPLOYEE".equalsIgnoreCase(user.getRole())) {
            dispose();
            SwingUtilities.invokeLater(() -> new LoginScreen().setVisible(true));
        }
    }

    private void initUI() {
        setTitle("Employee Dashboard - Employee Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1120, 720);
        setMinimumSize(new Dimension(960, 600));
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UIConstants.COLOR_BACKGROUND);
        root.add(new SidebarPanel(user, Arrays.asList("Dashboard", "My Documents", "Logout"),
                "Dashboard", this::logout, this::navigate), BorderLayout.WEST);

        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(UIConstants.COLOR_BACKGROUND);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(new EmptyBorder(20, 28, 20, 28));
        JLabel title = new JLabel("Employee Dashboard");
        title.setFont(UIConstants.FONT_TITLE);
        title.setForeground(UIConstants.COLOR_TEXT_MAIN);
        JLabel welcome = new JLabel("Welcome, " + user.getOfficialEmail());
        welcome.setForeground(UIConstants.COLOR_TEXT_MUTED);
        JPanel heading = new JPanel();
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));
        heading.setOpaque(false);
        heading.add(title);
        heading.add(Box.createVerticalStrut(4));
        heading.add(welcome);
        header.add(heading, BorderLayout.WEST);

        JPanel content = new JPanel(new BorderLayout(0, 18));
        content.setBackground(UIConstants.COLOR_BACKGROUND);
        content.setBorder(new EmptyBorder(24, 28, 28, 28));

        JPanel cards = new JPanel(new GridLayout(1, 3, 16, 0));
        cards.setOpaque(false);
        pendingLabel = addCard(cards, "Pending Documents", "0", "Awaiting admin review", UIConstants.COLOR_WARNING);
        approvedLabel = addCard(cards, "Approved Documents", "0", "Verified documents", UIConstants.COLOR_SUCCESS);
        statusLabel = addCard(cards, "Account Status", user.getStatus(), "Current account state", UIConstants.COLOR_INFO);

        JPanel profile = new JPanel(new BorderLayout());
        profile.setBackground(Color.WHITE);
        profile.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIConstants.COLOR_BORDER),
                new EmptyBorder(22, 24, 22, 24)));
        JLabel ptitle = new JLabel("My Employment Profile");
        ptitle.setFont(UIConstants.FONT_SECTION);
        ptitle.setForeground(UIConstants.COLOR_TEXT_MAIN);
        profileLabel = new JLabel("Loading your employee profile...");
        profileLabel.setFont(UIConstants.FONT_BODY);
        profileLabel.setForeground(UIConstants.COLOR_TEXT_MUTED);
        profile.add(ptitle, BorderLayout.NORTH);
        profile.add(profileLabel, BorderLayout.CENTER);

        content.add(cards, BorderLayout.NORTH);
        content.add(profile, BorderLayout.CENTER);
        main.add(header, BorderLayout.NORTH);
        main.add(content, BorderLayout.CENTER);
        root.add(main, BorderLayout.CENTER);
        setContentPane(root);
    }

    private JLabel addCard(JPanel parent, String title, String value, String subtitle, Color accent) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(3, 0, 0, 0, accent),
                new EmptyBorder(14, 18, 14, 18)));
        JLabel t = new JLabel(title);
        t.setFont(UIConstants.FONT_CARD_LABEL);
        t.setForeground(UIConstants.COLOR_TEXT_MUTED);
        JLabel v = new JLabel(value);
        v.setFont(UIConstants.FONT_CARD_VALUE);
        v.setForeground(UIConstants.COLOR_TEXT_MAIN);
        JLabel s = new JLabel(subtitle);
        s.setFont(UIConstants.FONT_SMALL);
        s.setForeground(UIConstants.COLOR_TEXT_MUTED);
        card.add(t); card.add(Box.createVerticalStrut(6)); card.add(v);
        card.add(Box.createVerticalStrut(4)); card.add(s);
        parent.add(card);
        return v;
    }

    private void loadData() {
        SwingWorker<EmployeeDashboardData, Void> worker = new SwingWorker<>() {
            protected EmployeeDashboardData doInBackground() {
                return service.getEmployeeDashboardData(user.getUserId());
            }
            protected void done() {
                try {
                    EmployeeDashboardData d = get();
                    pendingLabel.setText(String.valueOf(d.getPendingDocuments()));
                    approvedLabel.setText(String.valueOf(d.getApprovedDocuments()));
                    statusLabel.setText(d.getAccountStatus());
                    if (d.hasProfile()) {
                        profileLabel.setText("<html><b>" + d.getFullName() + "</b> &nbsp; | &nbsp; "
                                + d.getEmployeeCode() + " &nbsp; | &nbsp; " + d.getDesignation()
                                + " &nbsp; | &nbsp; " + d.getDepartmentName() + "</html>");
                    } else {
                        profileLabel.setText("Your employee profile is awaiting administrator assignment.");
                    }
                } catch (Exception e) {
                    profileLabel.setText("Unable to load employee profile.");
                }
            }
        };
        worker.execute();
    }

    private void navigate(String item) {
        if ("My Documents".equalsIgnoreCase(item)) {
            new MyDocuments().setVisible(true);
        }
    }

    private void logout() {
        if (JOptionPane.showConfirmDialog(this, "Are you sure you want to log out?",
                "Confirm Logout", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        SessionManager.getInstance().logout();
        dispose();
        new LoginScreen().setVisible(true);
    }
}

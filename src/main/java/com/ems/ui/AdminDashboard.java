package com.ems.ui;

import com.ems.dto.AdminDashboardStats;
import com.ems.dto.RecentEmployeeDTO;
import com.ems.model.User;
import com.ems.service.DashboardService;
import com.ems.session.SessionManager;
import com.ems.ui.components.SidebarPanel;
import com.ems.ui.components.StatCard;
import com.ems.ui.components.TopBarPanel;
import com.ems.ui.components.UIConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

/**
 * Professional Admin Dashboard featuring real-time PostgreSQL database metrics,
 * recent employee overview table, pending actions summary, and sidebar
 * navigation.
 */
public class AdminDashboard extends JFrame {

    private void handleNavigation(String itemName) {

    if ("Employees".equalsIgnoreCase(itemName)) {
        new EmployeeManagement().setVisible(true);
        return;
    }

    if ("Document Approvals".equalsIgnoreCase(itemName)) {
        new DocumentApprovals().setVisible(true);
        return;
    }

    JOptionPane.showMessageDialog(
            this,
            "The '" + itemName
                    + "' module is scheduled for implementation in upcoming milestones.",
            "Coming Soon",
            JOptionPane.INFORMATION_MESSAGE
    );
}
    private static final List<String> ADMIN_NAV_ITEMS = Arrays.asList(
            "Dashboard",
            "Employees",
            "Departments",
            "Document Approvals",
            "Profile Approvals",
            "Employee Requests",
            "Announcements",
            "Calendar",
            "Reports & Export",
            "Activity & History",
            "Account Settings",
            "Logout");

    private final User user;
    private final DashboardService dashboardService;

    // Stat cards for live metric updates
    private StatCard totalEmployeesCard;
    private StatCard activeEmployeesCard;
    private StatCard inactiveEmployeesCard;
    private StatCard totalDepartmentsCard;

    // Table & Card containers
    private DefaultTableModel recentEmployeesTableModel;
    private JPanel tableContainer;
    private CardLayout tableCardLayout;

    public AdminDashboard() {
        this(SessionManager.getInstance().getCurrentUser());
    }

    public AdminDashboard(User user) {
        this.user = user != null ? user : SessionManager.getInstance().getCurrentUser();
        this.dashboardService = new DashboardService();

        // Enforce session and role security
        validateSession();

        initUI();
        loadDashboardDataAsync();
    }

    private void validateSession() {
        if (user == null || !SessionManager.getInstance().isLoggedIn()) {
            dispose();
            SwingUtilities.invokeLater(() -> new LoginScreen().setVisible(true));
            return;
        }

        // Security check: ensure user possesses ADMIN privileges
        if (!user.isAdmin()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Access Denied: You do not possess administrator permissions.",
                    "Unauthorized Access",
                    JOptionPane.ERROR_MESSAGE);
            SessionManager.getInstance().logout();
            dispose();
            SwingUtilities.invokeLater(() -> new LoginScreen().setVisible(true));
        }
    }

    private void initUI() {
        setTitle("Admin Dashboard - Employee Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1120, 720);
        setMinimumSize(new Dimension(960, 600));
        setLocationRelativeTo(null);

        JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.setBackground(UIConstants.COLOR_BACKGROUND);

        // 1. Sidebar Panel (Left)
        SidebarPanel sidebar = new SidebarPanel(
        user,
        ADMIN_NAV_ITEMS,
        "Dashboard",
        this::performLogout,
        this::handleNavigation
);
        rootPanel.add(sidebar, BorderLayout.WEST);

        // 2. Main Area (TopBar + Content)
        JPanel mainArea = new JPanel(new BorderLayout());
        mainArea.setBackground(UIConstants.COLOR_BACKGROUND);

        TopBarPanel topBar = new TopBarPanel("Admin Dashboard", "System Administration & Key Metrics", user);
        mainArea.add(topBar, BorderLayout.NORTH);

        // Content ScrollPane
        JPanel contentContainer = new JPanel();
        contentContainer.setLayout(new BoxLayout(contentContainer, BoxLayout.Y_AXIS));
        contentContainer.setBackground(UIConstants.COLOR_BACKGROUND);
        contentContainer.setBorder(new EmptyBorder(24, 28, 28, 28));

        // Section A: Overview Metrics (Grid 1x4)
        JPanel statsGrid = new JPanel(new GridLayout(1, 4, 16, 0));
        statsGrid.setOpaque(false);
        statsGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));

        totalEmployeesCard = new StatCard("Total Employees", "...", "All registered staff", UIConstants.COLOR_PRIMARY);
        activeEmployeesCard = new StatCard("Active Employees", "...", "Currently working", UIConstants.COLOR_SUCCESS);
        inactiveEmployeesCard = new StatCard("Inactive Employees", "...", "Deactivated/Archived",
                UIConstants.COLOR_WARNING);
        totalDepartmentsCard = new StatCard("Total Departments", "...", "Active divisions", UIConstants.COLOR_INFO);

        statsGrid.add(totalEmployeesCard);
        statsGrid.add(activeEmployeesCard);
        statsGrid.add(inactiveEmployeesCard);
        statsGrid.add(totalDepartmentsCard);

        contentContainer.add(statsGrid);
        contentContainer.add(Box.createVerticalStrut(24));

        // Section B: Lower Split (Recent Employees Table + Pending Actions Card)
        JPanel lowerPanel = new JPanel(new BorderLayout(20, 0));
        lowerPanel.setOpaque(false);

        // Recent Employees Section (Left 70%)
        JPanel recentPanel = buildRecentEmployeesPanel();
        lowerPanel.add(recentPanel, BorderLayout.CENTER);

        // Pending Actions Section (Right 30%)
        JPanel pendingPanel = buildPendingActionsPanel();
        lowerPanel.add(pendingPanel, BorderLayout.EAST);

        contentContainer.add(lowerPanel);

        JScrollPane scrollPane = new JScrollPane(contentContainer);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(UIConstants.COLOR_BACKGROUND);
        scrollPane.getViewport().setBackground(UIConstants.COLOR_BACKGROUND);

        mainArea.add(scrollPane, BorderLayout.CENTER);
        rootPanel.add(mainArea, BorderLayout.CENTER);

        setContentPane(rootPanel);
    }

    private JPanel buildRecentEmployeesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIConstants.COLOR_SURFACE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIConstants.COLOR_BORDER, 1),
                new EmptyBorder(18, 20, 18, 20)));

        // Panel Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("Recent Employees");
        title.setFont(UIConstants.FONT_SECTION);
        title.setForeground(UIConstants.COLOR_TEXT_MAIN);

        JLabel subtitle = new JLabel("Latest additions to the organizational roster");
        subtitle.setFont(UIConstants.FONT_SMALL);
        subtitle.setForeground(UIConstants.COLOR_TEXT_MUTED);

        header.add(title, BorderLayout.NORTH);
        header.add(subtitle, BorderLayout.SOUTH);
        panel.add(header, BorderLayout.NORTH);

        // Table vs Empty State Container
        tableCardLayout = new CardLayout();
        tableContainer = new JPanel(tableCardLayout);
        tableContainer.setOpaque(false);
        tableContainer.setBorder(new EmptyBorder(12, 0, 0, 0));

        // 1. Table View
        String[] columns = { "ID", "Code", "Full Name", "Department", "Designation", "Joining Date", "Status" };
        recentEmployeesTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(recentEmployeesTableModel);
        table.setRowHeight(34);
        table.setFont(UIConstants.FONT_BODY);
        table.getTableHeader().setFont(UIConstants.FONT_CARD_LABEL);
        table.getTableHeader().setBackground(new Color(241, 245, 249));
        table.getTableHeader().setForeground(UIConstants.COLOR_TEXT_MUTED);
        table.setShowVerticalLines(false);
        table.setGridColor(UIConstants.COLOR_BORDER);
        table.setSelectionBackground(new Color(239, 246, 255));
        table.setSelectionForeground(UIConstants.COLOR_TEXT_MAIN);

        // Center align ID, Code, Joining Date, and Status
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(6).setCellRenderer(centerRenderer);

        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(BorderFactory.createLineBorder(UIConstants.COLOR_BORDER, 1));
        tableScroll.getViewport().setBackground(Color.WHITE);

        // 2. Empty State View
        JPanel emptyStatePanel = new JPanel();
        emptyStatePanel.setLayout(new BoxLayout(emptyStatePanel, BoxLayout.Y_AXIS));
        emptyStatePanel.setOpaque(false);
        emptyStatePanel.setBorder(new EmptyBorder(40, 20, 40, 20));

        JLabel emptyTitle = new JLabel("No employees registered yet");
        emptyTitle.setFont(UIConstants.FONT_BODY_BOLD);
        emptyTitle.setForeground(UIConstants.COLOR_TEXT_MUTED);
        emptyTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel emptySubtitle = new JLabel("Registered employee profiles will appear here once created.");
        emptySubtitle.setFont(UIConstants.FONT_SMALL);
        emptySubtitle.setForeground(UIConstants.COLOR_TEXT_MUTED);
        emptySubtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        emptyStatePanel.add(emptyTitle);
        emptyStatePanel.add(Box.createVerticalStrut(6));
        emptyStatePanel.add(emptySubtitle);

        tableContainer.add(tableScroll, "TABLE");
        tableContainer.add(emptyStatePanel, "EMPTY");
        tableCardLayout.show(tableContainer, "EMPTY");

        panel.add(tableContainer, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildPendingActionsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIConstants.COLOR_SURFACE);
        panel.setPreferredSize(new Dimension(300, 300));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIConstants.COLOR_BORDER, 1),
                new EmptyBorder(18, 20, 18, 20)));

        JLabel title = new JLabel("Pending Actions");
        title.setFont(UIConstants.FONT_SECTION);
        title.setForeground(UIConstants.COLOR_TEXT_MAIN);

        JLabel subtitle = new JLabel("Approvals requiring administrator review");
        subtitle.setFont(UIConstants.FONT_SMALL);
        subtitle.setForeground(UIConstants.COLOR_TEXT_MUTED);

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(title, BorderLayout.NORTH);
        header.add(subtitle, BorderLayout.SOUTH);
        panel.add(header, BorderLayout.NORTH);

        // Clean empty state card
        JPanel emptyCard = new JPanel();
        emptyCard.setLayout(new BoxLayout(emptyCard, BoxLayout.Y_AXIS));
        emptyCard.setOpaque(false);
        emptyCard.setBorder(new EmptyBorder(40, 10, 40, 10));

        JLabel emptyMsg = new JLabel("No pending actions yet.");
        emptyMsg.setFont(UIConstants.FONT_BODY_BOLD);
        emptyMsg.setForeground(UIConstants.COLOR_TEXT_MUTED);
        emptyMsg.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel emptyDetails = new JLabel(
                "<html><div style='text-align:center;'>Document and profile requests will be listed here.</div></html>");
        emptyDetails.setFont(UIConstants.FONT_SMALL);
        emptyDetails.setForeground(UIConstants.COLOR_TEXT_MUTED);
        emptyDetails.setAlignmentX(Component.CENTER_ALIGNMENT);

        emptyCard.add(emptyMsg);
        emptyCard.add(Box.createVerticalStrut(6));
        emptyCard.add(emptyDetails);

        panel.add(emptyCard, BorderLayout.CENTER);
        return panel;
    }

    private void loadDashboardDataAsync() {
        SwingWorker<AdminDashboardStats, Void> worker = new SwingWorker<>() {
            @Override
            protected AdminDashboardStats doInBackground() {
                return dashboardService.getAdminDashboardStats();
            }

            @Override
            protected void done() {
                try {
                    AdminDashboardStats stats = get();
                    updateUIWithStats(stats);
                } catch (Exception e) {
                    System.err.println("Notice: Failed to render admin dashboard data: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void updateUIWithStats(AdminDashboardStats stats) {
        if (stats == null)
            return;

        totalEmployeesCard.setValue(String.valueOf(stats.getTotalEmployees()));
        activeEmployeesCard.setValue(String.valueOf(stats.getActiveEmployees()));
        inactiveEmployeesCard.setValue(String.valueOf(stats.getInactiveEmployees()));
        totalDepartmentsCard.setValue(String.valueOf(stats.getTotalDepartments()));

        List<RecentEmployeeDTO> recentList = stats.getRecentEmployees();
        recentEmployeesTableModel.setRowCount(0);

        if (recentList == null || recentList.isEmpty()) {
            tableCardLayout.show(tableContainer, "EMPTY");
        } else {
            for (RecentEmployeeDTO emp : recentList) {
                recentEmployeesTableModel.addRow(new Object[] {
                        emp.getEmployeeId(),
                        emp.getEmployeeCode(),
                        emp.getFullName(),
                        emp.getDepartmentName(),
                        emp.getDesignation(),
                        emp.getJoiningDate() != null ? emp.getJoiningDate().toString() : "N/A",
                        emp.getStatus()
                });
            }
            tableCardLayout.show(tableContainer, "TABLE");
        }
    }

    private void performLogout() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to log out of your session?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            // 1. Clear in-memory session
            SessionManager.getInstance().logout();

            // 2. Close and dispose dashboard window
            dispose();

            // 3. Return cleanly to Login Screen
            SwingUtilities.invokeLater(() -> new LoginScreen().setVisible(true));
        }
    }
}

package com.ems.ui;

import com.ems.dto.EmployeeProfileDTO;
import com.ems.service.EmployeeService;
import com.ems.session.SessionManager;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutionException;

/**
 * Professional Employee Profile view displaying comprehensive official employment
 * details and personal contact information loaded from PostgreSQL.
 */
public class EmployeeProfile extends JFrame {

    private final long employeeId;
    private final EmployeeService employeeService;
    private final Frame parentFrame;

    private JPanel contentContainer;
    private CardLayout cardLayout;

    public EmployeeProfile(long employeeId) {
        this(employeeId, null);
    }

    public EmployeeProfile(long employeeId, Frame parentFrame) {
        this.employeeId = employeeId;
        this.parentFrame = parentFrame;
        this.employeeService = new EmployeeService();

        validateAdminSession();
        initUI();
        loadProfileDataAsync();
    }

    /**
     * Enforce strict role-based security: only active Admin sessions may view profiles.
     */
    private void validateAdminSession() {
        SessionManager session = SessionManager.getInstance();
        if (!session.isLoggedIn() || !"ADMIN".equalsIgnoreCase(session.getCurrentRole())) {
            JOptionPane.showMessageDialog(
                    null,
                    "Access Denied: Administrator privileges are required to view employee profiles.",
                    "Unauthorized Access",
                    JOptionPane.ERROR_MESSAGE
            );
            dispose();
            throw new IllegalStateException("Admin session required.");
        }
    }

    private void initUI() {
        setTitle("Employee Profile - Employee Management System");
        setSize(960, 780);
        setMinimumSize(new Dimension(840, 650));
        setLocationRelativeTo(parentFrame != null ? parentFrame : null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.setBackground(new Color(248, 250, 252));

        // 1. Top Navigation Bar (Back button)
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        topBar.setBorder(new EmptyBorder(16, 28, 12, 28));

        JButton backButton = new JButton("← Back to Employees");
        backButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        backButton.setForeground(new Color(71, 85, 105));
        backButton.setBackground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.setBorder(new CompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(8, 16, 8, 16)
        ));
        backButton.addActionListener(e -> closeAndReturn());

        topBar.add(backButton, BorderLayout.WEST);
        rootPanel.add(topBar, BorderLayout.NORTH);

        // 2. Card Container for Loading / Content / Error states
        cardLayout = new CardLayout();
        contentContainer = new JPanel(cardLayout);
        contentContainer.setOpaque(false);

        // Loading State Panel
        JPanel loadingPanel = createLoadingPanel();
        contentContainer.add(loadingPanel, "LOADING");

        rootPanel.add(contentContainer, BorderLayout.CENTER);
        setContentPane(rootPanel);
    }

    private JPanel createLoadingPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);

        JLabel label = new JLabel("Loading employee profile...");
        label.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        label.setForeground(new Color(100, 116, 139));
        panel.add(label);
        return panel;
    }

    private JPanel createErrorPanel(String errorMessage) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);

        JPanel box = new JPanel();
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setBackground(Color.WHITE);
        box.setBorder(new CompoundBorder(
                new LineBorder(new Color(254, 202, 202), 1, true),
                new EmptyBorder(32, 40, 32, 40)
        ));

        JLabel title = new JLabel("Unable to Load Profile");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(new Color(220, 38, 38));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel msg = new JLabel("<html><div style='text-align:center; width:340px;'>" + errorMessage + "</div></html>");
        msg.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        msg.setForeground(new Color(71, 85, 105));
        msg.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton returnBtn = new JButton("Close");
        returnBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        returnBtn.setBackground(new Color(220, 38, 38));
        returnBtn.setForeground(Color.WHITE);
        returnBtn.setFocusPainted(false);
        returnBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        returnBtn.addActionListener(e -> closeAndReturn());

        box.add(title);
        box.add(Box.createVerticalStrut(12));
        box.add(msg);
        box.add(Box.createVerticalStrut(20));
        box.add(returnBtn);

        panel.add(box);
        return panel;
    }

    private void loadProfileDataAsync() {
        cardLayout.show(contentContainer, "LOADING");

        SwingWorker<EmployeeProfileDTO, Void> worker = new SwingWorker<>() {
            @Override
            protected EmployeeProfileDTO doInBackground() {
                return employeeService.getEmployeeProfile(employeeId);
            }

            @Override
            protected void done() {
                try {
                    EmployeeProfileDTO profile = get();
                    JPanel profileView = buildProfileView(profile);
                    contentContainer.add(profileView, "CONTENT");
                    cardLayout.show(contentContainer, "CONTENT");
                } catch (ExecutionException e) {
                    Throwable cause = e.getCause();
                    String message = cause != null ? cause.getMessage() : "Database connection failure.";
                    JPanel errorPanel = createErrorPanel(message);
                    contentContainer.add(errorPanel, "ERROR");
                    cardLayout.show(contentContainer, "ERROR");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    JPanel errorPanel = createErrorPanel("Operation was interrupted.");
                    contentContainer.add(errorPanel, "ERROR");
                    cardLayout.show(contentContainer, "ERROR");
                }
            }
        };

        worker.execute();
    }

    private JPanel buildProfileView(EmployeeProfileDTO profile) {
        JPanel scrollContent = new JPanel();
        scrollContent.setLayout(new BoxLayout(scrollContent, BoxLayout.Y_AXIS));
        scrollContent.setOpaque(false);
        scrollContent.setBorder(new EmptyBorder(0, 28, 28, 28));

        // 1. Header Banner Card
        scrollContent.add(createHeaderCard(profile));
        scrollContent.add(Box.createVerticalStrut(20));

        // 2. Official / Basic Information Card
        scrollContent.add(createBasicInfoCard(profile));
        scrollContent.add(Box.createVerticalStrut(20));

        // 3. Personal Information Card
        scrollContent.add(createPersonalInfoCard(profile));
        scrollContent.add(Box.createVerticalStrut(20));

        // 4. Bottom Action Panel
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        footerPanel.setOpaque(false);
        JButton closeBtn = new JButton("Close Profile");
        closeBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        closeBtn.setBackground(new Color(241, 245, 249));
        closeBtn.setForeground(new Color(51, 65, 85));
        closeBtn.setFocusPainted(false);
        closeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeBtn.setPreferredSize(new Dimension(130, 38));
        closeBtn.setBorder(new LineBorder(new Color(203, 213, 225), 1, true));
        closeBtn.addActionListener(e -> closeAndReturn());
        footerPanel.add(closeBtn);
        scrollContent.add(footerPanel);

        JScrollPane scrollPane = new JScrollPane(scrollContent);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(scrollPane, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel createHeaderCard(EmployeeProfileDTO profile) {
        JPanel card = new JPanel(new BorderLayout(20, 0));
        card.setBackground(Color.WHITE);
        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(24, 28, 24, 28)
        ));

        // Left Avatar Circle with Initials
        JPanel avatarPanel = createAvatarCircle(profile.getFullName());
        card.add(avatarPanel, BorderLayout.WEST);

        // Center Details (Name, Designation, Code)
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setOpaque(false);

        JLabel nameLabel = new JLabel(profile.getFullName());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        nameLabel.setForeground(new Color(15, 23, 42));

        String deptDisplay = profile.getDepartmentName() != null ? profile.getDepartmentName() : "General";
        String desigDisplay = profile.getDesignation() != null ? profile.getDesignation() : "Employee";
        JLabel subtitleLabel = new JLabel(desigDisplay + "  •  " + deptDisplay);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(100, 116, 139));

        JLabel codeLabel = new JLabel("Employee Code: " + profile.getEmployeeCode());
        codeLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        codeLabel.setForeground(new Color(37, 99, 235));

        detailsPanel.add(nameLabel);
        detailsPanel.add(Box.createVerticalStrut(4));
        detailsPanel.add(subtitleLabel);
        detailsPanel.add(Box.createVerticalStrut(6));
        detailsPanel.add(codeLabel);

        card.add(detailsPanel, BorderLayout.CENTER);

        // Right Status Badge
        JPanel statusContainer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 10));
        statusContainer.setOpaque(false);
        boolean isActive = "ACTIVE".equalsIgnoreCase(profile.getEmployeeStatus());
        JLabel statusBadge = new JLabel(isActive ? "  ACTIVE  " : "  INACTIVE  ");
        statusBadge.setFont(new Font("Segoe UI", Font.BOLD, 12));
        statusBadge.setOpaque(true);
        if (isActive) {
            statusBadge.setBackground(new Color(220, 252, 231));
            statusBadge.setForeground(new Color(22, 101, 52));
            statusBadge.setBorder(new LineBorder(new Color(187, 247, 208), 1, true));
        } else {
            statusBadge.setBackground(new Color(241, 245, 249));
            statusBadge.setForeground(new Color(71, 85, 105));
            statusBadge.setBorder(new LineBorder(new Color(226, 232, 240), 1, true));
        }
        statusContainer.add(statusBadge);
        card.add(statusContainer, BorderLayout.EAST);

        return card;
    }

    private JPanel createAvatarCircle(String fullName) {
        String initials = "EM";
        if (fullName != null && !fullName.isBlank()) {
            String[] parts = fullName.trim().split("\\s+");
            if (parts.length >= 2) {
                initials = ("" + parts[0].charAt(0) + parts[1].charAt(0)).toUpperCase();
            } else if (parts[0].length() >= 2) {
                initials = parts[0].substring(0, 2).toUpperCase();
            } else {
                initials = parts[0].toUpperCase();
            }
        }

        final String avatarText = initials;
        JPanel avatar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(37, 99, 235));
                g2.fillOval(0, 0, 68, 68);

                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 22));
                FontMetrics fm = g2.getFontMetrics();
                int x = (68 - fm.stringWidth(avatarText)) / 2;
                int y = ((68 - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString(avatarText, x, y);
                g2.dispose();
            }
        };
        avatar.setPreferredSize(new Dimension(68, 68));
        avatar.setMinimumSize(new Dimension(68, 68));
        avatar.setOpaque(false);
        return avatar;
    }

    private JPanel createBasicInfoCard(EmployeeProfileDTO profile) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(22, 26, 22, 26)
        ));

        JLabel sectionTitle = new JLabel("BASIC INFORMATION");
        sectionTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        sectionTitle.setForeground(new Color(15, 23, 42));
        sectionTitle.setBorder(new EmptyBorder(0, 0, 16, 0));
        card.add(sectionTitle, BorderLayout.NORTH);

        // 2-column grid of field pairs
        JPanel grid = new JPanel(new GridLayout(0, 2, 24, 16));
        grid.setOpaque(false);

        String joiningDateFormatted = profile.getJoiningDate() != null
                ? profile.getJoiningDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")) + " (" + profile.getJoiningDate() + ")"
                : "Not provided";

        grid.add(createFieldTile("Employee ID", String.valueOf(profile.getEmployeeId())));
        grid.add(createFieldTile("Employee Code", profile.getEmployeeCode()));
        grid.add(createFieldTile("Full Name", profile.getFullName()));
        grid.add(createFieldTile("Official Email", profile.getOfficialEmail()));
        grid.add(createFieldTile("Designation", profile.getDesignation()));
        grid.add(createFieldTile("Department Name", profile.getDepartmentName() != null ? profile.getDepartmentName() : "Not assigned"));
        grid.add(createFieldTile("Joining Date", joiningDateFormatted));
        grid.add(createFieldTile("Employee Status", profile.getEmployeeStatus()));

        card.add(grid, BorderLayout.CENTER);
        return card;
    }

    private JPanel createPersonalInfoCard(EmployeeProfileDTO profile) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(22, 26, 22, 26)
        ));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 16, 0));

        JLabel sectionTitle = new JLabel("PERSONAL INFORMATION");
        sectionTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        sectionTitle.setForeground(new Color(15, 23, 42));
        header.add(sectionTitle, BorderLayout.WEST);

        if (!profile.hasPersonalInfo()) {
            JLabel badge = new JLabel("No personal record submitted");
            badge.setFont(new Font("Segoe UI", Font.ITALIC, 12));
            badge.setForeground(new Color(148, 163, 184));
            header.add(badge, BorderLayout.EAST);
        }

        card.add(header, BorderLayout.NORTH);

        // 2-column grid
        JPanel grid = new JPanel(new GridLayout(0, 2, 24, 16));
        grid.setOpaque(false);

        grid.add(createFieldTile("Personal Phone", profile.getPersonalPhone()));
        grid.add(createFieldTile("Personal Email", profile.getPersonalEmail()));
        grid.add(createFieldTile("Address", profile.getAddress()));
        grid.add(createFieldTile("Emergency Contact Name", profile.getEmergencyContactName()));
        grid.add(createFieldTile("Emergency Contact Phone", profile.getEmergencyContactPhone()));
        grid.add(createFieldTile("Emergency Contact Relation", profile.getEmergencyContactRelation()));

        card.add(grid, BorderLayout.CENTER);
        return card;
    }

    private JPanel createFieldTile(String labelText, String valueText) {
        JPanel tile = new JPanel();
        tile.setLayout(new BoxLayout(tile, BoxLayout.Y_AXIS));
        tile.setOpaque(false);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(new Color(100, 116, 139));

        boolean isNotProvided = valueText == null || valueText.trim().isBlank();
        JLabel value = new JLabel(isNotProvided ? "Not provided" : valueText.trim());
        value.setFont(new Font("Segoe UI", isNotProvided ? Font.ITALIC : Font.PLAIN, 14));
        value.setForeground(isNotProvided ? new Color(148, 163, 184) : new Color(15, 23, 42));

        tile.add(label);
        tile.add(Box.createVerticalStrut(4));
        tile.add(value);
        return tile;
    }

    private void closeAndReturn() {
        dispose();
        if (parentFrame != null) {
            parentFrame.toFront();
            parentFrame.requestFocus();
        }
    }
}

package com.ems.ui;

import com.ems.model.Employee;
import com.ems.service.EmployeeService;
import com.ems.session.SessionManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class EmployeeManagement extends JFrame {

    private final EmployeeService employeeService;

    private JTable employeeTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JLabel totalEmployeesLabel;

    public EmployeeManagement() {

        validateAdminSession();

        employeeService = new EmployeeService();

        initUI();
        loadEmployees();
    }

    /**
     * Make sure only an authenticated Admin can access this screen.
     */
    private void validateAdminSession() {

        SessionManager sessionManager = SessionManager.getInstance();

        if (!sessionManager.isLoggedIn()
                || !"ADMIN".equalsIgnoreCase(sessionManager.getCurrentRole())) {

            JOptionPane.showMessageDialog(
                    null,
                    "Admin access required.",
                    "Access Denied",
                    JOptionPane.ERROR_MESSAGE
            );

            dispose();
            throw new IllegalStateException("Admin session required.");
        }
    }

    private void initUI() {

        setTitle("Employee Management - Employee Management System");
        setSize(1100, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(248, 250, 252));

        // ---------------- HEADER ----------------

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Employee Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(new Color(15, 23, 42));

        JLabel subtitleLabel = new JLabel(
                "View and manage employee information"
        );
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitleLabel.setForeground(new Color(100, 116, 139));

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);

        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(5));
        titlePanel.add(subtitleLabel);

        headerPanel.add(titlePanel, BorderLayout.WEST);

        JPanel headerRightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, FlowLayout.CENTER));
        headerRightPanel.setOpaque(false);

        totalEmployeesLabel = new JLabel("Total Employees: 0");
        totalEmployeesLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        totalEmployeesLabel.setForeground(new Color(37, 99, 235));

        JButton viewProfileHeaderBtn = new JButton("View Profile");
        viewProfileHeaderBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        viewProfileHeaderBtn.setBackground(Color.WHITE);
        viewProfileHeaderBtn.setForeground(new Color(51, 65, 85));
        viewProfileHeaderBtn.setFocusPainted(false);
        viewProfileHeaderBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        viewProfileHeaderBtn.setPreferredSize(new Dimension(125, 36));
        viewProfileHeaderBtn.setBorder(BorderFactory.createLineBorder(new Color(203, 213, 225), 1, true));
        viewProfileHeaderBtn.addActionListener(e -> onViewProfileClicked());

        JButton addEmployeeButton = new JButton("+ Add Employee");
        addEmployeeButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        addEmployeeButton.setBackground(new Color(37, 99, 235));
        addEmployeeButton.setForeground(Color.WHITE);
        addEmployeeButton.setFocusPainted(false);
        addEmployeeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addEmployeeButton.setPreferredSize(new Dimension(145, 36));
        addEmployeeButton.addActionListener(e -> openAddEmployeeDialog());

        headerRightPanel.add(totalEmployeesLabel);
        headerRightPanel.add(Box.createHorizontalStrut(8));
        headerRightPanel.add(viewProfileHeaderBtn);
        headerRightPanel.add(addEmployeeButton);

        headerPanel.add(headerRightPanel, BorderLayout.EAST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // ---------------- SEARCH BAR ----------------

        JPanel searchPanel = new JPanel(new BorderLayout(10, 0));
        searchPanel.setOpaque(false);

        searchField = new JTextField();
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setToolTipText(
                "Search by employee code, name or designation"
        );

        JButton searchButton = new JButton("Search");
        JButton refreshButton = new JButton("Refresh");

        searchButton.addActionListener(e -> searchEmployees());

        refreshButton.addActionListener(e -> {
            searchField.setText("");
            loadEmployees();
        });

        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);
        searchPanel.add(refreshButton, BorderLayout.WEST);

        JPanel contentPanel = new JPanel(new BorderLayout(0, 15));
        contentPanel.setOpaque(false);

        contentPanel.add(searchPanel, BorderLayout.NORTH);

        // ---------------- TABLE ----------------

        String[] columns = {
                "Employee ID",
                "Employee Code",
                "Name",
                "Designation",
                "Department ID",
                "Status",
                "Joining Date"
        };

        tableModel = new DefaultTableModel(columns, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        employeeTable = new JTable(tableModel);

        employeeTable.setRowHeight(32);
        employeeTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        employeeTable.getTableHeader().setFont(
                new Font("Segoe UI", Font.BOLD, 13)
        );

        employeeTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        // Double-click row to open full profile
        employeeTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2 && employeeTable.getSelectedRow() != -1) {
                    onViewProfileClicked();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(employeeTable);

        contentPanel.add(scrollPane, BorderLayout.CENTER);

        // Bottom Action Bar below Table
        JPanel tableFooterPanel = new JPanel(new BorderLayout());
        tableFooterPanel.setOpaque(false);
        tableFooterPanel.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0));

        JLabel tableHintLabel = new JLabel("💡 Tip: Select an employee and click 'View Profile' or double-click any row to view complete profile.");
        tableHintLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tableHintLabel.setForeground(new Color(100, 116, 139));

        JButton footerViewProfileBtn = new JButton("View Selected Profile");
        footerViewProfileBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        footerViewProfileBtn.setBackground(new Color(241, 245, 249));
        footerViewProfileBtn.setForeground(new Color(51, 65, 85));
        footerViewProfileBtn.setFocusPainted(false);
        footerViewProfileBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        footerViewProfileBtn.setPreferredSize(new Dimension(175, 34));
        footerViewProfileBtn.setBorder(BorderFactory.createLineBorder(new Color(203, 213, 225), 1, true));
        footerViewProfileBtn.addActionListener(e -> onViewProfileClicked());

        tableFooterPanel.add(tableHintLabel, BorderLayout.WEST);
        tableFooterPanel.add(footerViewProfileBtn, BorderLayout.EAST);

        contentPanel.add(tableFooterPanel, BorderLayout.SOUTH);

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        setContentPane(mainPanel);
    }

    /**
     * Load all employees from the database.
     */
    private void loadEmployees() {

        try {

            List<Employee> employees =
                    employeeService.getAllEmployees();

            displayEmployees(employees);

        } catch (RuntimeException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to load employees.\nPlease check the database connection.",
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Search employees using the search field.
     */
    private void searchEmployees() {

        String keyword = searchField.getText();

        try {

            List<Employee> employees =
                    employeeService.searchEmployees(keyword);

            displayEmployees(employees);

        } catch (RuntimeException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to search employees.",
                    "Search Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Put employee data into the table.
     */
    private void displayEmployees(List<Employee> employees) {

        tableModel.setRowCount(0);

        for (Employee employee : employees) {

            tableModel.addRow(new Object[]{
                    employee.getEmployeeId(),
                    employee.getEmployeeCode(),
                    employee.getFullName(),
                    employee.getDesignation(),
                    employee.getDepartmentId(),
                    employee.getStatus(),
                    employee.getJoiningDate()
            });
        }

        totalEmployeesLabel.setText(
                "Total Employees: " + employees.size()
        );
    }

    /**
     * Open the Add Employee modal dialog.
     */
    private void openAddEmployeeDialog() {
        AddEmployeeDialog dialog = new AddEmployeeDialog(this, employeeService, this::loadEmployees);
        dialog.setVisible(true);
    }

    /**
     * Handles viewing the selected employee's full profile.
     */
    private void onViewProfileClicked() {
        int selectedRow = employeeTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select an employee from the table to view their profile.",
                    "No Employee Selected",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        Object idObj = employeeTable.getValueAt(selectedRow, 0);
        if (idObj == null) {
            return;
        }

        try {
            long empId = Long.parseLong(idObj.toString());
            EmployeeProfile profileView = new EmployeeProfile(empId, this);
            profileView.setVisible(true);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Invalid employee ID: " + idObj,
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}

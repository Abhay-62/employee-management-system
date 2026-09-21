package com.ems.ui;

import com.ems.model.Department;
import com.ems.model.Employee;
import com.ems.service.DepartmentService;
import com.ems.service.EmployeeService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Professional dialog form for adding a new employee and generating login credentials.
 */
public class AddEmployeeDialog extends JDialog {

    private final EmployeeService employeeService;
    private final DepartmentService departmentService;
    private final Runnable onSuccessCallback;

    private JTextField nameField;
    private JTextField emailField;
    private JTextField designationField;
    private JComboBox<Department> departmentComboBox;
    private JTextField joiningDateField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JLabel errorLabel;
    private JButton submitButton;
    private JButton cancelButton;

    public AddEmployeeDialog(Frame parent, EmployeeService employeeService, Runnable onSuccessCallback) {
        this(parent, employeeService, new DepartmentService(), onSuccessCallback);
    }

    public AddEmployeeDialog(Frame parent, EmployeeService employeeService,
                              DepartmentService departmentService, Runnable onSuccessCallback) {
        super(parent, "Add New Employee", true);
        this.employeeService = employeeService;
        this.departmentService = departmentService;
        this.onSuccessCallback = onSuccessCallback;

        initUI();
        loadDepartments();
    }

    private void initUI() {
        setSize(520, 720);
        setResizable(false);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.setBackground(new Color(248, 250, 252));
        rootPanel.setBorder(new EmptyBorder(24, 32, 24, 32));

        // 1. Header
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Add New Employee");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(new Color(15, 23, 42));

        JLabel subtitleLabel = new JLabel("Register employee details and configure initial credentials");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitleLabel.setForeground(new Color(100, 116, 139));

        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(4));
        headerPanel.add(subtitleLabel);
        headerPanel.add(Box.createVerticalStrut(16));

        // 2. Error Banner
        errorLabel = new JLabel();
        errorLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        errorLabel.setForeground(new Color(220, 38, 38));
        errorLabel.setVisible(false);

        // 3. Form fields
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setOpaque(false);

        formPanel.add(errorLabel);
        formPanel.add(Box.createVerticalStrut(8));

        // Full Name
        formPanel.add(createFieldLabel("Full Name *"));
        nameField = new JTextField();
        nameField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        nameField.setPreferredSize(new Dimension(440, 36));
        nameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        formPanel.add(nameField);
        formPanel.add(Box.createVerticalStrut(12));

        // Official Email
        formPanel.add(createFieldLabel("Official Email *"));
        emailField = new JTextField();
        emailField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        emailField.setPreferredSize(new Dimension(440, 36));
        emailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        formPanel.add(emailField);
        formPanel.add(Box.createVerticalStrut(12));

        // Designation
        formPanel.add(createFieldLabel("Designation *"));
        designationField = new JTextField();
        designationField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        designationField.setPreferredSize(new Dimension(440, 36));
        designationField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        formPanel.add(designationField);
        formPanel.add(Box.createVerticalStrut(12));

        // Department Dropdown
        formPanel.add(createFieldLabel("Department *"));
        departmentComboBox = new JComboBox<>();
        departmentComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        departmentComboBox.setPreferredSize(new Dimension(440, 36));
        departmentComboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        formPanel.add(departmentComboBox);
        formPanel.add(Box.createVerticalStrut(12));

        // Joining Date
        formPanel.add(createFieldLabel("Joining Date (YYYY-MM-DD) *"));
        joiningDateField = new JTextField(LocalDate.now().toString());
        joiningDateField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        joiningDateField.setPreferredSize(new Dimension(440, 36));
        joiningDateField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        formPanel.add(joiningDateField);
        formPanel.add(Box.createVerticalStrut(12));

        // Temporary Password
        formPanel.add(createFieldLabel("Temporary Password (min 6 characters) *"));
        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        passwordField.setPreferredSize(new Dimension(440, 36));
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        passwordField.putClientProperty("JPasswordField.showRevealButton", true);
        formPanel.add(passwordField);
        formPanel.add(Box.createVerticalStrut(12));

        // Confirm Password
        formPanel.add(createFieldLabel("Confirm Password *"));
        confirmPasswordField = new JPasswordField();
        confirmPasswordField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        confirmPasswordField.setPreferredSize(new Dimension(440, 36));
        confirmPasswordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        formPanel.add(confirmPasswordField);
        formPanel.add(Box.createVerticalStrut(20));

        // 4. Action Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        buttonPanel.setOpaque(false);

        cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cancelButton.setPreferredSize(new Dimension(100, 38));
        cancelButton.setFocusPainted(false);
        cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cancelButton.addActionListener(e -> dispose());

        submitButton = new JButton("Add Employee");
        submitButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        submitButton.setForeground(Color.WHITE);
        submitButton.setBackground(new Color(37, 99, 235));
        submitButton.setPreferredSize(new Dimension(140, 38));
        submitButton.setFocusPainted(false);
        submitButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        submitButton.addActionListener(e -> performAddEmployee());

        buttonPanel.add(cancelButton);
        buttonPanel.add(submitButton);

        // Enter key triggers submit
        KeyAdapter enterListener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performAddEmployee();
                }
            }
        };
        nameField.addKeyListener(enterListener);
        emailField.addKeyListener(enterListener);
        designationField.addKeyListener(enterListener);
        joiningDateField.addKeyListener(enterListener);
        passwordField.addKeyListener(enterListener);
        confirmPasswordField.addKeyListener(enterListener);

        rootPanel.add(headerPanel, BorderLayout.NORTH);
        rootPanel.add(formPanel, BorderLayout.CENTER);
        rootPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(rootPanel);
    }

    private JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(new Color(51, 65, 85));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setBorder(new EmptyBorder(0, 0, 4, 0));
        return label;
    }

    private void loadDepartments() {
        SwingWorker<List<Department>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Department> doInBackground() {
                return departmentService.getActiveDepartments();
            }

            @Override
            protected void done() {
                try {
                    List<Department> departments = get();
                    departmentComboBox.removeAllItems();
                    for (Department dept : departments) {
                        departmentComboBox.addItem(dept);
                    }
                } catch (Exception e) {
                    showError("Failed to load departments from database: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void performAddEmployee() {
        String name = nameField.getText() != null ? nameField.getText().trim() : "";
        String email = emailField.getText() != null ? emailField.getText().trim() : "";
        String designation = designationField.getText() != null ? designationField.getText().trim() : "";
        Department selectedDept = (Department) departmentComboBox.getSelectedItem();
        String dateStr = joiningDateField.getText() != null ? joiningDateField.getText().trim() : "";
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        // Basic client validation
        if (name.isEmpty()) {
            showError("Full name is required.");
            nameField.requestFocusInWindow();
            return;
        }

        if (email.isEmpty()) {
            showError("Official email is required.");
            emailField.requestFocusInWindow();
            return;
        }

        if (designation.isEmpty()) {
            showError("Designation is required.");
            designationField.requestFocusInWindow();
            return;
        }

        if (selectedDept == null) {
            showError("Please select a department.");
            departmentComboBox.requestFocusInWindow();
            return;
        }

        if (dateStr.isEmpty()) {
            showError("Joining date is required.");
            joiningDateField.requestFocusInWindow();
            return;
        }

        LocalDate joiningDate;
        try {
            joiningDate = LocalDate.parse(dateStr);
        } catch (DateTimeParseException e) {
            showError("Invalid joining date format. Please use YYYY-MM-DD (e.g. 2026-09-21).");
            joiningDateField.requestFocusInWindow();
            return;
        }

        if (password.isEmpty()) {
            showError("Temporary password is required.");
            passwordField.requestFocusInWindow();
            return;
        }

        if (password.length() < 6) {
            showError("Password must be at least 6 characters long.");
            passwordField.requestFocusInWindow();
            return;
        }

        if (!password.equals(confirmPassword)) {
            showError("Passwords do not match.");
            confirmPasswordField.requestFocusInWindow();
            return;
        }

        setLoading(true);
        clearError();

        SwingWorker<Employee, Void> worker = new SwingWorker<>() {
            @Override
            protected Employee doInBackground() {
                return employeeService.addEmployee(
                        name,
                        email,
                        designation,
                        selectedDept.getDepartmentId(),
                        joiningDate,
                        password,
                        confirmPassword
                );
            }

            @Override
            protected void done() {
                setLoading(false);
                try {
                    Employee created = get();
                    JOptionPane.showMessageDialog(
                            AddEmployeeDialog.this,
                            "Employee '" + created.getFullName() + "' created successfully!\n" +
                            "Generated Employee Code: " + created.getEmployeeCode() + "\n" +
                            "Login Account: " + email,
                            "Employee Added Successfully",
                            JOptionPane.INFORMATION_MESSAGE
                    );

                    dispose();

                    if (onSuccessCallback != null) {
                        onSuccessCallback.run();
                    }
                } catch (ExecutionException ex) {
                    Throwable cause = ex.getCause();
                    String msg = cause != null ? cause.getMessage() : "Failed to add employee.";
                    showError(msg);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    showError("Operation cancelled.");
                }
            }
        };

        worker.execute();
    }

    private void setLoading(boolean loading) {
        submitButton.setEnabled(!loading);
        cancelButton.setEnabled(!loading);
        nameField.setEnabled(!loading);
        emailField.setEnabled(!loading);
        designationField.setEnabled(!loading);
        departmentComboBox.setEnabled(!loading);
        joiningDateField.setEnabled(!loading);
        passwordField.setEnabled(!loading);
        confirmPasswordField.setEnabled(!loading);

        if (loading) {
            submitButton.setText("Adding...");
        } else {
            submitButton.setText("Add Employee");
        }
    }

    private void showError(String message) {
        errorLabel.setText("<html><div style='width:380px;'>" + message + "</div></html>");
        errorLabel.setVisible(true);
        revalidate();
        repaint();
    }

    private void clearError() {
        errorLabel.setText("");
        errorLabel.setVisible(false);
        revalidate();
        repaint();
    }
}

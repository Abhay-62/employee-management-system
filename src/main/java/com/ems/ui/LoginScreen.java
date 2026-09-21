package com.ems.ui;

import com.ems.exception.AuthenticationException;
import com.ems.model.User;
import com.ems.service.AuthService;
import com.ems.session.SessionManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

/**
 * Shared modern login screen for both Admin and Employee users.
 * Connects directly to AuthService and handles role-based routing upon successful authentication.
 */
public class LoginScreen extends JFrame {

    private final AuthService authService;

    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton togglePasswordButton;
    private JLabel errorLabel;
    private JLabel capsLockLabel;
    private char defaultEchoChar;
    private boolean isPasswordVisible = false;

    public LoginScreen() {
        this(new AuthService());
    }

    public LoginScreen(AuthService authService) {
        this.authService = authService != null ? authService : new AuthService();
        initUI();
    }

    private void initUI() {
        setTitle("Employee Management System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(460, 580);
        setResizable(false);
        setLocationRelativeTo(null);

        // Main content container
        JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.setBackground(new Color(248, 250, 252));
        rootPanel.setBorder(new EmptyBorder(32, 40, 32, 40));

        // 1. Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);

        JLabel logoBadge = new JLabel("EMS");
        logoBadge.setFont(new Font("Segoe UI", Font.BOLD, 18));
        logoBadge.setForeground(new Color(37, 99, 235));
        logoBadge.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel("Welcome Back");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(15, 23, 42));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Employee Management System");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitleLabel.setForeground(new Color(100, 116, 139));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(logoBadge);
        headerPanel.add(Box.createVerticalStrut(8));
        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(4));
        headerPanel.add(subtitleLabel);
        headerPanel.add(Box.createVerticalStrut(20));

        // 2. Form Panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setOpaque(false);

        // Error message label (wrapped in banner)
        errorLabel = new JLabel();
        errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        errorLabel.setForeground(new Color(220, 38, 38));
        errorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        errorLabel.setVisible(false);

        // Email section
        JLabel emailLabel = new JLabel("Official Email");
        emailLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        emailLabel.setForeground(new Color(51, 65, 85));
        emailLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        emailField = new JTextField();
        emailField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        emailField.setPreferredSize(new Dimension(380, 40));
        emailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        emailField.putClientProperty("JTextField.placeholderText", "e.g. user@ems.com");
        emailField.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Password section
        JPanel passwordHeader = new JPanel(new BorderLayout());
        passwordHeader.setOpaque(false);
        passwordHeader.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        passwordHeader.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        passwordLabel.setForeground(new Color(51, 65, 85));

        JButton forgotPasswordButton = new JButton("Forgot Password?");
        forgotPasswordButton.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        forgotPasswordButton.setForeground(new Color(37, 99, 235));
        forgotPasswordButton.setBorderPainted(false);
        forgotPasswordButton.setContentAreaFilled(false);
        forgotPasswordButton.setFocusPainted(false);
        forgotPasswordButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        forgotPasswordButton.addActionListener(e -> onForgotPassword());

        passwordHeader.add(passwordLabel, BorderLayout.WEST);
        passwordHeader.add(forgotPasswordButton, BorderLayout.EAST);

        // Password input + Toggle eye button container
        JPanel passwordInputPanel = new JPanel(new BorderLayout(4, 0));
        passwordInputPanel.setOpaque(false);
        passwordInputPanel.setPreferredSize(new Dimension(380, 40));
        passwordInputPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        passwordInputPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.putClientProperty("JTextField.placeholderText", "Enter password");
        passwordField.putClientProperty("JPasswordField.showRevealButton", true);
        defaultEchoChar = passwordField.getEchoChar();

        togglePasswordButton = new JButton("Show");
        togglePasswordButton.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        togglePasswordButton.setFocusPainted(false);
        togglePasswordButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        togglePasswordButton.addActionListener(e -> togglePasswordVisibility());

        passwordInputPanel.add(passwordField, BorderLayout.CENTER);
        passwordInputPanel.add(togglePasswordButton, BorderLayout.EAST);

        // Caps lock indicator
        capsLockLabel = new JLabel("WARNING: Caps Lock is ON");
        capsLockLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        capsLockLabel.setForeground(new Color(217, 119, 6));
        capsLockLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        capsLockLabel.setVisible(false);

        // Keyboard listeners for Caps Lock check and Enter-to-submit
        KeyAdapter keyAdapter = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                checkCapsLock();
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performLogin();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                checkCapsLock();
            }
        };

        emailField.addKeyListener(keyAdapter);
        passwordField.addKeyListener(keyAdapter);

        // Submit Button
        loginButton = new JButton("Sign In");
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginButton.setForeground(Color.WHITE);
        loginButton.setBackground(new Color(37, 99, 235));
        loginButton.setOpaque(true);
        loginButton.setFocusPainted(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.setPreferredSize(new Dimension(380, 42));
        loginButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        loginButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginButton.addActionListener(e -> performLogin());

        // Assemble Form Panel
        formPanel.add(errorLabel);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(emailLabel);
        formPanel.add(Box.createVerticalStrut(6));
        formPanel.add(emailField);
        formPanel.add(Box.createVerticalStrut(16));
        formPanel.add(passwordHeader);
        formPanel.add(Box.createVerticalStrut(6));
        formPanel.add(passwordInputPanel);
        formPanel.add(Box.createVerticalStrut(4));
        formPanel.add(capsLockLabel);
        formPanel.add(Box.createVerticalStrut(22));
        formPanel.add(loginButton);

        // 3. Footer Panel
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footerPanel.setOpaque(false);
        JLabel footerLabel = new JLabel("Employee Management System • MVP Foundation");
        footerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        footerLabel.setForeground(new Color(148, 163, 184));
        footerPanel.add(footerLabel);

        // Add panels to root
        rootPanel.add(headerPanel, BorderLayout.NORTH);
        rootPanel.add(formPanel, BorderLayout.CENTER);
        rootPanel.add(footerPanel, BorderLayout.SOUTH);

        setContentPane(rootPanel);
    }

    private void togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible;
        if (isPasswordVisible) {
            passwordField.setEchoChar((char) 0);
            togglePasswordButton.setText("Hide");
        } else {
            passwordField.setEchoChar(defaultEchoChar);
            togglePasswordButton.setText("Show");
        }
        passwordField.requestFocusInWindow();
    }

    private void checkCapsLock() {
        try {
            boolean capsLockOn = Toolkit.getDefaultToolkit().getLockingKeyState(KeyEvent.VK_CAPS_LOCK);
            capsLockLabel.setVisible(capsLockOn);
        } catch (Exception ignored) {
            capsLockLabel.setVisible(false);
        }
    }

    private void onForgotPassword() {
        JOptionPane.showMessageDialog(
                this,
                "To reset your credentials, please contact the IT Administrator:\nadmin@ems.com",
                "Password Reset",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void performLogin() {
        String email = emailField.getText();
        char[] passwordChars = passwordField.getPassword();
        String password = new String(passwordChars);

        // Pre-validation before hitting backend
        if (email == null || email.trim().isEmpty()) {
            showError("Please enter your official email.");
            emailField.requestFocusInWindow();
            return;
        }

        if (password.isEmpty()) {
            showError("Please enter your password.");
            passwordField.requestFocusInWindow();
            return;
        }

        setLoading(true);
        clearError();

        // Asynchronous execution to keep UI responsive during network operations
        SwingWorker<User, Void> worker = new SwingWorker<>() {
            @Override
            protected User doInBackground() throws Exception {
                return authService.authenticate(email, password);
            }

            @Override
            protected void done() {
                setLoading(false);
                try {
                    User authenticatedUser = get();
                    handleAuthenticationSuccess(authenticatedUser);
                } catch (ExecutionException ex) {
                    Throwable cause = ex.getCause();
                    if (cause instanceof AuthenticationException) {
                        showError(cause.getMessage());
                    } else if (cause instanceof SQLException) {
                        showError("Unable to connect to database service. Please check your connection.");
                    } else {
                        showError("An unexpected error occurred. Please try again.");
                    }
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    showError("Authentication process was cancelled.");
                }
            }
        };

        worker.execute();
    }

    private void handleAuthenticationSuccess(User user) {
        // Store authenticated user in existing SessionManager
        SessionManager.getInstance().login(user);

        // Dispose login screen
        dispose();

        // Route to the appropriate dashboard based on role
        SwingUtilities.invokeLater(() -> {
            if (user.isAdmin()) {
                new AdminDashboard(user).setVisible(true);
            } else {
                new EmployeeDashboard(user).setVisible(true);
            }
        });
    }

    private void setLoading(boolean loading) {
        loginButton.setEnabled(!loading);
        emailField.setEnabled(!loading);
        passwordField.setEnabled(!loading);
        togglePasswordButton.setEnabled(!loading);
        if (loading) {
            loginButton.setText("Signing in...");
        } else {
            loginButton.setText("Sign In");
        }
    }

    private void showError(String message) {
        errorLabel.setText("<html><div style='width:280px;'>" + message + "</div></html>");
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

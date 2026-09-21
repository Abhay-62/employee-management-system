package com.ems;

import com.formdev.flatlaf.FlatLightLaf;
import com.ems.ui.LoginScreen;

import javax.swing.*;

/**
 * Main application launcher for the Employee Management System desktop application.
 * Sets up FlatLaf modern styling and initializes the primary login screen.
 */
public class Main {

    public static void main(String[] args) {
        // 1. Initialize FlatLaf Look and Feel for modern desktop aesthetics
        try {
            FlatLightLaf.setup();
            // Configure subtle styling defaults
            UIManager.put("Button.arc", 8);
            UIManager.put("Component.arc", 8);
            UIManager.put("TextComponent.arc", 8);
        } catch (Exception e) {
            System.err.println("Notice: FlatLaf Look and Feel initialization fallback: " + e.getMessage());
        }

        // 2. Launch Login Screen on the Swing Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            LoginScreen loginScreen = new LoginScreen();
            loginScreen.setVisible(true);
        });
    }
}

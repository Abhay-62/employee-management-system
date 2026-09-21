package com.ems;

import com.ems.model.User;
import com.ems.service.AuthService;
import com.ems.session.SessionManager;

/**
 * Temporary verification runner to test live Supabase authentication for both demo accounts.
 */
public class LiveAuthVerificationRunner {

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("Running Live Authentication Verification against Supabase");
        System.out.println("==================================================");

        AuthService authService = new AuthService();

        // 1. Test Admin Authentication
        try {
            System.out.print("[TEST 1] Authenticating admin@ems.com... ");
            User admin = authService.authenticate("admin@ems.com", "AdminDemo@123");
            if (admin != null && "ADMIN".equalsIgnoreCase(admin.getRole())) {
                System.out.println("SUCCESS (User ID: " + admin.getUserId() + ", Role: " + admin.getRole() + ")");
                SessionManager.getInstance().login(admin);
                assert SessionManager.getInstance().isLoggedIn();
                assert "ADMIN".equals(SessionManager.getInstance().getCurrentRole());
                SessionManager.getInstance().logout();
            } else {
                System.out.println("FAILED (Role mismatch or null user)");
            }
        } catch (Exception e) {
            System.out.println("FAILED: " + e.getMessage());
        }

        // 2. Test Employee Authentication
        try {
            System.out.print("[TEST 2] Authenticating employee@ems.com... ");
            User emp = authService.authenticate("employee@ems.com", "EmployeeDemo@123");
            if (emp != null && "EMPLOYEE".equalsIgnoreCase(emp.getRole())) {
                System.out.println("SUCCESS (User ID: " + emp.getUserId() + ", Role: " + emp.getRole() + ")");
                SessionManager.getInstance().login(emp);
                assert SessionManager.getInstance().isLoggedIn();
                assert "EMPLOYEE".equals(SessionManager.getInstance().getCurrentRole());
                SessionManager.getInstance().logout();
            } else {
                System.out.println("FAILED (Role mismatch or null user)");
            }
        } catch (Exception e) {
            System.out.println("FAILED: " + e.getMessage());
        }

        // 3. Test Invalid Password
        try {
            System.out.print("[TEST 3] Testing invalid password rejection... ");
            authService.authenticate("admin@ems.com", "WrongPassword!999");
            System.out.println("FAILED (Expected AuthenticationException)");
        } catch (com.ems.exception.AuthenticationException e) {
            System.out.println("SUCCESS (Rejected with: '" + e.getMessage() + "')");
        } catch (Exception e) {
            System.out.println("FAILED: Unexpected exception " + e.getMessage());
        }

        // 4. Test Non-existent User
        try {
            System.out.print("[TEST 4] Testing non-existent user rejection... ");
            authService.authenticate("nobody@ems.com", "SomePassword");
            System.out.println("FAILED (Expected AuthenticationException)");
        } catch (com.ems.exception.AuthenticationException e) {
            System.out.println("SUCCESS (Rejected with: '" + e.getMessage() + "')");
        } catch (Exception e) {
            System.out.println("FAILED: Unexpected exception " + e.getMessage());
        }

        System.out.println("==================================================");
        System.out.println("All Live Backend Verification Checks Completed!");
        System.out.println("==================================================");
    }
}

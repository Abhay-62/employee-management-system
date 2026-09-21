package com.ems;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Development-only utility to generate BCrypt password hashes and ready-to-run SQL
 * INSERT statements for initial demo/presentation accounts.
 *
 * NOTE:
 * - This class resides strictly in src/test/java and is NOT packaged into production builds.
 * - These credentials are for local development/demonstration ONLY.
 */
public class DevAccountSetup {

    // Documented development-only passwords (strictly for demo and testing)
    public static final String DEMO_ADMIN_EMAIL = "admin@ems.com";
    public static final String DEMO_ADMIN_PASSWORD = "AdminDemo@123";

    public static final String DEMO_EMPLOYEE_EMAIL = "employee@ems.com";
    public static final String DEMO_EMPLOYEE_PASSWORD = "EmployeeDemo@123";

    public static void main(String[] args) {
        System.out.println("-- ==============================================================================");
        System.out.println("-- Development Demo Accounts Seed Script (PostgreSQL / Supabase)");
        System.out.println("-- WARNING: For development/demo purposes only. Do NOT use in production!");
        System.out.println("-- ==============================================================================\n");

        // Generate and verify BCrypt hash for Admin
        String adminHash = BCrypt.hashpw(DEMO_ADMIN_PASSWORD, BCrypt.gensalt(12));
        if (!BCrypt.checkpw(DEMO_ADMIN_PASSWORD, adminHash)) {
            System.err.println("ERROR: Admin password hash verification failed.");
            return;
        }

        // Generate and verify BCrypt hash for Employee
        String employeeHash = BCrypt.hashpw(DEMO_EMPLOYEE_PASSWORD, BCrypt.gensalt(12));
        if (!BCrypt.checkpw(DEMO_EMPLOYEE_PASSWORD, employeeHash)) {
            System.err.println("ERROR: Employee password hash verification failed.");
            return;
        }

        // Output SQL INSERT statements
        System.out.println("-- 1. Demo Admin Account (" + DEMO_ADMIN_EMAIL + ")");
        System.out.println("INSERT INTO users (official_email, password_hash, role, status)");
        System.out.println("VALUES ('" + DEMO_ADMIN_EMAIL + "', '" + adminHash + "', 'ADMIN', 'ACTIVE')");
        System.out.println("ON CONFLICT (official_email) DO UPDATE");
        System.out.println("SET password_hash = EXCLUDED.password_hash, role = EXCLUDED.role, status = EXCLUDED.status;\n");

        System.out.println("-- 2. Demo Employee Account (" + DEMO_EMPLOYEE_EMAIL + ")");
        System.out.println("INSERT INTO users (official_email, password_hash, role, status)");
        System.out.println("VALUES ('" + DEMO_EMPLOYEE_EMAIL + "', '" + employeeHash + "', 'EMPLOYEE', 'ACTIVE')");
        System.out.println("ON CONFLICT (official_email) DO UPDATE");
        System.out.println("SET password_hash = EXCLUDED.password_hash, role = EXCLUDED.role, status = EXCLUDED.status;\n");

        System.out.println("-- ==============================================================================");
        System.out.println("-- Instructions:");
        System.out.println("-- 1. Copy the SQL INSERT statements above.");
        System.out.println("-- 2. Paste into Supabase SQL Editor and click 'Run'.");
        System.out.println("-- ==============================================================================");
    }
}

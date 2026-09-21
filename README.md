# Employee Management System (EMS)

A Java desktop application for managing employee data, administration, and organizational workflows.

## Current Status: Foundation Phase

This project is currently in its initial **Foundation Phase**. Development is being conducted incrementally across structured milestones.
At this stage, only the core Maven configuration, project structure, and environment foundation are established. No UI components, database tables, or business features have been initialized yet.

## Project Structure

```text
employee-management-system/
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── ems/
│   │   └── resources/
│   └── test/
│       └── java/
├── .gitignore
└── README.md
```

- **`src/main/java/com/ems`**: Root package for production source code.
- **`src/main/resources`**: Static assets, configuration templates, and application resources.
- **`src/main/resources/sql/mvp_schema.sql`**: MVP database schema DDL script for PostgreSQL/Supabase.
- **`src/test/java`**: Unit and integration test suites.

## Database Schema (MVP)

The presentation MVP utilizes a normalized relational schema in PostgreSQL (hosted on Supabase) consisting of 6 core tables:

### 1. Table Descriptions
- **`users`**: Manages authentication accounts, encrypted password hashes (BCrypt), system roles (`ADMIN`, `EMPLOYEE`), account activation statuses (`ACTIVE`, `INACTIVE`), and session timestamps.
- **`departments`**: Defines organizational divisions, unique codes, descriptive metadata, and department statuses.
- **`employees`**: Stores core professional profiles (full name, unique code, designation, joining date, profile photo reference) linked directly to a user account and department.
- **`employee_personal_info`**: Stores personal details (personal phone/email, residential address, emergency contact name and number) in a dedicated 1-to-1 extension table.
- **`documents`**: Tracks employee document metadata, verification review statuses (`PENDING`, `APPROVED`, `REJECTED`), document categories, rejection notes, and reviewer references. Binary files are stored in external storage; only file references reside here.
- **`audit_logs`**: Provides an append-only security and operational audit trail documenting actions, target modules, affected record IDs, and change summaries.

### 2. Relationships & Integrity Rules
- **`users` ↔ `employees` (1-to-1)**: Every employee record must be linked to exactly one user account (`user_id` is unique with `ON DELETE RESTRICT`).
- **`departments` ↔ `employees` (1-to-Many)**: Every employee belongs to a department (`ON DELETE RESTRICT` prevents accidental deletion of active departments containing staff).
- **`employees` ↔ `employee_personal_info` (1-to-1)**: Personal contact information is keyed directly by `employee_id` (`ON DELETE CASCADE` ensures personal information is removed if an employee is purged).
- **`employees` ↔ `documents` (1-to-Many)**: Employees can upload multiple verification documents (`ON DELETE RESTRICT`).
- **`users` ↔ `documents` (Reviewer)**: Documents record the reviewing administrator (`reviewed_by` references `users(user_id)` with `ON DELETE SET NULL`).
- **`users` ↔ `audit_logs` (1-to-Many, Optional)**: System actions are associated with the acting user (`ON DELETE SET NULL` ensures audit trail longevity even if user accounts are deactivated).

### 3. How to Execute SQL in Supabase SQL Editor
1. Open your [Supabase Dashboard](https://supabase.com/dashboard).
2. Select your project (`wxiqwpglqobgemmkbyxq`).
3. In the left navigation menu, click on the **SQL Editor** icon (`>_`).
4. Click **+ New Query** to open a blank SQL query editor.
5. Open the schema migration file: `src/main/resources/sql/mvp_schema.sql`.
6. Copy the entire contents of `mvp_schema.sql` and paste them into the SQL Editor.
7. Click the **Run** button (or press `Ctrl+Enter`).
8. Check the output panel to verify `Success. No rows returned`.
9. Navigate to the **Table Editor** in the left sidebar to confirm that all 6 tables (`users`, `departments`, `employees`, `employee_personal_info`, `documents`, `audit_logs`) are visible and properly structured.

### 4. Development & Demo Accounts

For local development, presentation, and testing, demo credentials can be generated using `src/test/java/com/ems/DevAccountSetup.java`.

> [!WARNING]
> These demo accounts are intended **strictly for local development and demonstration purposes**. They must **never** be deployed or used in a production environment.

| Role | Official Email | Demo Password | Status |
| :--- | :--- | :--- | :--- |
| **Admin** | `admin@ems.com` | `AdminDemo@123` | `ACTIVE` |
| **Employee** | `employee@ems.com` | `EmployeeDemo@123` | `ACTIVE` |

To generate or view the seed SQL script:
```bash
mvn exec:java -Dexec.mainClass="com.ems.DevAccountSetup" -Dexec.classpathScope=test
```

## Prerequisites

- **Java Development Kit (JDK)**: Version 25 or newer
- **Apache Maven**: Version 3.9+

## Build and Verification

To verify and compile the project using Maven:

```bash
mvn clean compile
```

To run test suites:

```bash
mvn test
```

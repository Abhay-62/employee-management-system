package com.ems.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeProfileDTOTest {

    @Test
    @DisplayName("EmployeeProfileDTO getters, setters and toString")
    void testEmployeeProfileDTO() {
        EmployeeProfileDTO dto = new EmployeeProfileDTO();
        dto.setEmployeeId(101L);
        dto.setUserId(202L);
        dto.setDepartmentId(1L);
        dto.setEmployeeCode("EMP101");
        dto.setFullName("Alice Smith");
        dto.setDesignation("Lead Architect");
        dto.setDepartmentName("Engineering");
        dto.setDepartmentCode("ENG");
        dto.setJoiningDate(LocalDate.of(2025, 5, 10));
        dto.setEmployeeStatus("ACTIVE");
        dto.setOfficialEmail("alice.smith@ems.com");
        dto.setUserStatus("ACTIVE");

        assertEquals(101L, dto.getEmployeeId());
        assertEquals(202L, dto.getUserId());
        assertEquals(1L, dto.getDepartmentId());
        assertEquals("EMP101", dto.getEmployeeCode());
        assertEquals("Alice Smith", dto.getFullName());
        assertEquals("Lead Architect", dto.getDesignation());
        assertEquals("Engineering", dto.getDepartmentName());
        assertEquals("ENG", dto.getDepartmentCode());
        assertEquals(LocalDate.of(2025, 5, 10), dto.getJoiningDate());
        assertEquals("ACTIVE", dto.getEmployeeStatus());
        assertEquals("alice.smith@ems.com", dto.getOfficialEmail());
        assertEquals("ACTIVE", dto.getUserStatus());

        assertNotNull(dto.toString());
        assertTrue(dto.toString().contains("Alice Smith"));
        assertTrue(dto.toString().contains("EMP101"));
    }

    @Test
    @DisplayName("hasPersonalInfo returns false when all personal fields are null or blank")
    void testHasPersonalInfoEmpty() {
        EmployeeProfileDTO dto = new EmployeeProfileDTO();
        assertFalse(dto.hasPersonalInfo());

        dto.setPersonalPhone("   ");
        dto.setPersonalEmail("");
        dto.setAddress(null);
        dto.setEmergencyContactName("  ");
        dto.setEmergencyContactPhone(null);
        dto.setEmergencyContactRelation(" ");
        assertFalse(dto.hasPersonalInfo());
    }

    @Test
    @DisplayName("hasPersonalInfo returns true when at least one field is provided")
    void testHasPersonalInfoPopulated() {
        EmployeeProfileDTO dto = new EmployeeProfileDTO();
        dto.setPersonalPhone("+1-555-0199");
        assertTrue(dto.hasPersonalInfo());

        dto = new EmployeeProfileDTO();
        dto.setEmergencyContactName("Bob Smith");
        assertTrue(dto.hasPersonalInfo());

        dto = new EmployeeProfileDTO();
        dto.setAddress("123 Tech Blvd, Suite 400");
        assertTrue(dto.hasPersonalInfo());
    }
}

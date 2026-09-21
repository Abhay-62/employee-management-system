package com.ems.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class DepartmentTest {

    @Test
    @DisplayName("Test Department getters, setters, and toString")
    void testDepartmentModel() {
        LocalDateTime now = LocalDateTime.now();
        Department dept = new Department(1L, "Engineering", "ENG", "Core Engineering Team", "ACTIVE", now, now);

        assertEquals(1L, dept.getDepartmentId());
        assertEquals("Engineering", dept.getName());
        assertEquals("ENG", dept.getCode());
        assertEquals("Core Engineering Team", dept.getDescription());
        assertEquals("ACTIVE", dept.getStatus());
        assertEquals(now, dept.getCreatedAt());
        assertEquals(now, dept.getUpdatedAt());

        // toString used in JComboBox
        assertEquals("Engineering (ENG)", dept.toString());

        dept.setName("Finance");
        dept.setCode("FIN");
        assertEquals("Finance (FIN)", dept.toString());
    }
}

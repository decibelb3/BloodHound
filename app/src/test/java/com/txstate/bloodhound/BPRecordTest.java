package com.txstate.bloodhound;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for BPRecord - AHA category logic (FR-020) and getters.
 */
class BPRecordTest {

    private BPRecord createRecord(int systolic, int diastolic) {
        return new BPRecord("sess-1", 1000L, systolic, diastolic,
                72, "Morning", "Before", "Resting");
    }

    // --- Crisis (systolic > 180 OR diastolic > 120) ---
    @Test
    @DisplayName("Crisis: systolic > 180")
    void crisis_highSystolic() {
        BPRecord r = createRecord(181, 80);
        assertEquals("Crisis", r.getAHACategory());
    }

    @Test
    @DisplayName("Crisis: diastolic > 120")
    void crisis_highDiastolic() {
        BPRecord r = createRecord(140, 121);
        assertEquals("Crisis", r.getAHACategory());
    }

    @Test
    @DisplayName("Crisis: both at boundary (180/120)")
    void crisis_boundary() {
        assertEquals("Crisis", createRecord(180, 120).getAHACategory());
    }

    // --- Stage 2 (systolic >= 140 OR diastolic >= 90, but not crisis) ---
    @Test
    @DisplayName("Stage 2: systolic 140")
    void stage2_systolic140() {
        assertEquals("Stage 2", createRecord(140, 80).getAHACategory());
    }

    @Test
    @DisplayName("Stage 2: diastolic 90")
    void stage2_diastolic90() {
        assertEquals("Stage 2", createRecord(130, 90).getAHACategory());
    }

    @Test
    @DisplayName("Stage 2: both in range")
    void stage2_both() {
        assertEquals("Stage 2", createRecord(145, 95).getAHACategory());
    }

    // --- Stage 1 (systolic >= 130 OR diastolic >= 80) ---
    @Test
    @DisplayName("Stage 1: systolic 130, diastolic < 80")
    void stage1_systolic130() {
        assertEquals("Stage 1", createRecord(130, 79).getAHACategory());
    }

    @Test
    @DisplayName("Stage 1: diastolic 80, systolic < 130")
    void stage1_diastolic80() {
        assertEquals("Stage 1", createRecord(129, 80).getAHACategory());
    }

    // --- Elevated (systolic >= 120 AND diastolic < 80) ---
    @Test
    @DisplayName("Elevated: 120/79")
    void elevated_boundary() {
        assertEquals("Elevated", createRecord(120, 79).getAHACategory());
    }

    @Test
    @DisplayName("Elevated: 129/79")
    void elevated_midRange() {
        assertEquals("Elevated", createRecord(129, 79).getAHACategory());
    }

    @Test
    @DisplayName("Elevated: diastolic 80 is Stage 1, not Elevated")
    void elevated_diastolic80IsStage1() {
        assertEquals("Stage 1", createRecord(125, 80).getAHACategory());
    }

    // --- Normal ---
    @Test
    @DisplayName("Normal: 119/79")
    void normal_119_79() {
        assertEquals("Normal", createRecord(119, 79).getAHACategory());
    }

    @Test
    @DisplayName("Normal: 100/60")
    void normal_typical() {
        assertEquals("Normal", createRecord(100, 60).getAHACategory());
    }

    @Test
    @DisplayName("Normal: systolic 119, diastolic 80")
    void normal_systolicUnder120() {
        assertEquals("Normal", createRecord(119, 80).getAHACategory());
    }

    // --- Getters ---
    @Test
    @DisplayName("All getters return correct values")
    void getters_returnCorrectValues() {
        BPRecord r = new BPRecord("sid-99", 12345L, 130, 85,
                88, "Evening", "After", "Active");
        assertEquals("sid-99", r.getSessionId());
        assertEquals(12345L, r.getTimestamp());
        assertEquals(130, r.getSystolic());
        assertEquals(85, r.getDiastolic());
        assertEquals(88, r.getHeartRate());
        assertEquals("Evening", r.getTimeOfDay());
        assertEquals("After", r.getMedTiming());
        assertEquals("Active", r.getActivityTiming());
    }
}

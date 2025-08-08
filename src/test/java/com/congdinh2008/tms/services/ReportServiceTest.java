package com.congdinh2008.tms.services;

import com.congdinh2008.tms.dto.response.ProjectStatistics;
import com.congdinh2008.tms.dto.response.UserProductivity;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for DTOs and basic functionality
 */
class ReportServiceTest {

    @Test
    void testProjectStatisticsDTO() {
        // Test DTO creation and getters
        ProjectStatistics statistics = new ProjectStatistics(
            10L,                         // totalTasks
            5L,                          // completedTasks
            3L,                          // inProgressTasks
            2L,                          // overdueTasks
            BigDecimal.valueOf(50.0)     // completionRate
        );
        
        assertEquals(10L, statistics.getTotalTasks());
        assertEquals(5L, statistics.getCompletedTasks());
        assertEquals(3L, statistics.getInProgressTasks());
        assertEquals(2L, statistics.getOverdueTasks());
        assertEquals(BigDecimal.valueOf(50.0), statistics.getCompletionRate());
    }

    @Test
    void testUserProductivityDTO() {
        // Test DTO creation and getters
        UserProductivity productivity = new UserProductivity(
            1L,                          // userId
            "john_doe",                  // userName
            15L,                         // completedTasks
            20L,                         // totalAssignedTasks
            BigDecimal.valueOf(75.0),    // productivityRate
            Duration.ofHours(18)         // averageCompletionTime
        );
        
        assertEquals(1L, productivity.getUserId());
        assertEquals("john_doe", productivity.getUserName());
        assertEquals(15L, productivity.getCompletedTasks());
        assertEquals(20L, productivity.getTotalAssignedTasks());
        assertEquals(BigDecimal.valueOf(75.0), productivity.getProductivityRate());
        assertEquals(Duration.ofHours(18), productivity.getAverageCompletionTime());
    }

    @Test
    void testProjectStatisticsRemainingTasks() {
        // Test that remaining tasks calculation works correctly
        ProjectStatistics statistics = new ProjectStatistics(
            10L,                         // totalTasks
            5L,                          // completedTasks
            3L,                          // inProgressTasks
            2L,                          // overdueTasks
            BigDecimal.valueOf(50.0)     // completionRate
        );
        
        // Remaining tasks = total - completed = 10 - 5 = 5
        assertEquals(5L, statistics.getRemainingTasks());
    }

    @Test
    void testProjectStatisticsNoArgsConstructor() {
        // Test no-args constructor
        ProjectStatistics statistics = new ProjectStatistics();
        
        assertNull(statistics.getTotalTasks());
        assertNull(statistics.getCompletedTasks());
        assertNull(statistics.getInProgressTasks());
        assertNull(statistics.getOverdueTasks());
        assertNull(statistics.getCompletionRate());
    }

    @Test
    void testUserProductivityNoArgsConstructor() {
        // Test no-args constructor
        UserProductivity productivity = new UserProductivity();
        
        assertNull(productivity.getUserId());
        assertNull(productivity.getUserName());
        assertNull(productivity.getCompletedTasks());
        assertNull(productivity.getTotalAssignedTasks());
        assertNull(productivity.getProductivityRate());
        assertNull(productivity.getAverageCompletionTime());
    }

    @Test
    void testProjectStatisticsIsCompleted() {
        // Test completed project detection
        ProjectStatistics completedProject = new ProjectStatistics(
            10L, 10L, 0L, 0L, BigDecimal.valueOf(100.0)
        );
        assertTrue(completedProject.isCompleted());
        
        ProjectStatistics incompleteProject = new ProjectStatistics(
            10L, 5L, 3L, 2L, BigDecimal.valueOf(50.0)
        );
        assertFalse(incompleteProject.isCompleted());
    }

    @Test
    void testProjectStatisticsHasOverdueTasks() {
        // Test overdue tasks detection
        ProjectStatistics withOverdue = new ProjectStatistics(
            10L, 5L, 3L, 2L, BigDecimal.valueOf(50.0)
        );
        assertTrue(withOverdue.hasOverdueTasks());
        
        ProjectStatistics withoutOverdue = new ProjectStatistics(
            10L, 5L, 5L, 0L, BigDecimal.valueOf(50.0)
        );
        assertFalse(withoutOverdue.hasOverdueTasks());
    }
}

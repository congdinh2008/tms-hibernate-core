package com.congdinh2008.tms.utils;

import com.congdinh2008.tms.entities.*;
import com.congdinh2008.tms.enums.TaskPriority;
import com.congdinh2008.tms.enums.TaskStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Custom assertion utilities for enhanced test readability and consistency
 * Provides domain-specific assertions for TMS entities
 */
public class TestAssertions {
    
    /**
     * Assert User properties
     */
    public static void assertUser(User expected, User actual, String message) {
        assertNotNull(actual, message + " - User should not be null");
        assertEquals(expected.getName(), actual.getName(), message + " - User name mismatch");
        assertEquals(expected.getEmail(), actual.getEmail(), message + " - User email mismatch");
        // Note: Password should not be compared in tests for security reasons
    }
    
    public static void assertUser(User expected, User actual) {
        assertUser(expected, actual, "User assertion");
    }
    
    /**
     * Assert Project properties
     */
    public static void assertProject(Project expected, Project actual, String message) {
        assertNotNull(actual, message + " - Project should not be null");
        assertEquals(expected.getName(), actual.getName(), message + " - Project name mismatch");
        assertEquals(expected.getDescription(), actual.getDescription(), message + " - Project description mismatch");
        assertEquals(expected.getStartDate(), actual.getStartDate(), message + " - Project start date mismatch");
    }
    
    public static void assertProject(Project expected, Project actual) {
        assertProject(expected, actual, "Project assertion");
    }
    
    /**
     * Assert Task properties
     */
    public static void assertTask(Task expected, Task actual, String message) {
        assertNotNull(actual, message + " - Task should not be null");
        assertEquals(expected.getTitle(), actual.getTitle(), message + " - Task title mismatch");
        assertEquals(expected.getDescription(), actual.getDescription(), message + " - Task description mismatch");
        assertEquals(expected.getDueDate(), actual.getDueDate(), message + " - Task due date mismatch");
        assertEquals(expected.getPriority(), actual.getPriority(), message + " - Task priority mismatch");
        assertEquals(expected.getStatus(), actual.getStatus(), message + " - Task status mismatch");
    }
    
    public static void assertTask(Task expected, Task actual) {
        assertTask(expected, actual, "Task assertion");
    }
    
    /**
     * Assert Task is overdue
     */
    public static void assertTaskIsOverdue(Task task, String message) {
        assertNotNull(task, message + " - Task should not be null");
        assertNotNull(task.getDueDate(), message + " - Task due date should not be null");
        assertTrue(task.getDueDate().isBefore(LocalDate.now()), 
                   message + " - Task should be overdue (due date before today)");
        assertNotEquals(TaskStatus.DONE, task.getStatus(), 
                       message + " - Overdue task should not be completed");
    }
    
    public static void assertTaskIsOverdue(Task task) {
        assertTaskIsOverdue(task, "Overdue task assertion");
    }
    
    /**
     * Assert Task is completed
     */
    public static void assertTaskIsCompleted(Task task, String message) {
        assertNotNull(task, message + " - Task should not be null");
        assertEquals(TaskStatus.DONE, task.getStatus(), 
                    message + " - Task should be completed");
    }
    
    public static void assertTaskIsCompleted(Task task) {
        assertTaskIsCompleted(task, "Completed task assertion");
    }
    
    /**
     * Assert Tag properties
     */
    public static void assertTag(Tag expected, Tag actual, String message) {
        assertNotNull(actual, message + " - Tag should not be null");
        assertEquals(expected.getName(), actual.getName(), message + " - Tag name mismatch");
    }
    
    public static void assertTag(Tag expected, Tag actual) {
        assertTag(expected, actual, "Tag assertion");
    }
    
    /**
     * Assert TaskHistory properties
     */
    public static void assertTaskHistory(TaskHistory expected, TaskHistory actual, String message) {
        assertNotNull(actual, message + " - TaskHistory should not be null");
        assertEquals(expected.getFieldChanged(), actual.getFieldChanged(), 
                    message + " - TaskHistory field changed mismatch");
        assertEquals(expected.getOldValue(), actual.getOldValue(), 
                    message + " - TaskHistory old value mismatch");
        assertEquals(expected.getNewValue(), actual.getNewValue(), 
                    message + " - TaskHistory new value mismatch");
    }
    
    public static void assertTaskHistory(TaskHistory expected, TaskHistory actual) {
        assertTaskHistory(expected, actual, "TaskHistory assertion");
    }
    
    /**
     * Assert Entity has valid timestamps
     */
    public static void assertValidTimestamps(BaseEntity entity, String message) {
        assertNotNull(entity, message + " - Entity should not be null");
        assertNotNull(entity.getCreatedAt(), message + " - Created timestamp should not be null");
        assertNotNull(entity.getUpdatedAt(), message + " - Updated timestamp should not be null");
        assertTrue(entity.getCreatedAt().isBefore(LocalDateTime.now().plusSeconds(1)), 
                  message + " - Created timestamp should be reasonable");
        assertTrue(entity.getUpdatedAt().isBefore(LocalDateTime.now().plusSeconds(1)), 
                  message + " - Updated timestamp should be reasonable");
    }
    
    public static void assertValidTimestamps(BaseEntity entity) {
        assertValidTimestamps(entity, "Timestamp assertion");
    }
    
    /**
     * Assert Entity has valid ID
     */
    public static void assertValidId(BaseEntity entity, String message) {
        assertNotNull(entity, message + " - Entity should not be null");
        assertNotNull(entity.getId(), message + " - Entity ID should not be null");
        assertTrue(entity.getId() > 0, message + " - Entity ID should be positive");
    }
    
    public static void assertValidId(BaseEntity entity) {
        assertValidId(entity, "ID assertion");
    }
    
    /**
     * Assert Collection is not empty and contains expected size
     */
    public static <T> void assertCollectionSize(List<T> collection, int expectedSize, String message) {
        assertNotNull(collection, message + " - Collection should not be null");
        assertEquals(expectedSize, collection.size(), 
                    message + " - Collection size mismatch. Expected: " + expectedSize + 
                    ", Actual: " + collection.size());
    }
    
    public static <T> void assertCollectionNotEmpty(List<T> collection, String message) {
        assertNotNull(collection, message + " - Collection should not be null");
        assertFalse(collection.isEmpty(), message + " - Collection should not be empty");
    }
    
    public static <T> void assertCollectionNotEmpty(List<T> collection) {
        assertCollectionNotEmpty(collection, "Collection assertion");
    }
    
    /**
     * Assert Task Priority is valid
     */
    public static void assertValidTaskPriority(Task task, String message) {
        assertNotNull(task, message + " - Task should not be null");
        assertNotNull(task.getPriority(), message + " - Task priority should not be null");
        assertTrue(TaskPriority.LOW.equals(task.getPriority()) || 
                  TaskPriority.MEDIUM.equals(task.getPriority()) || 
                  TaskPriority.HIGH.equals(task.getPriority()),
                  message + " - Task priority should be LOW, MEDIUM, or HIGH");
    }
    
    public static void assertValidTaskPriority(Task task) {
        assertValidTaskPriority(task, "Task priority assertion");
    }
    
    /**
     * Assert Task Status is valid
     */
    public static void assertValidTaskStatus(Task task, String message) {
        assertNotNull(task, message + " - Task should not be null");
        assertNotNull(task.getStatus(), message + " - Task status should not be null");
        assertTrue(TaskStatus.TODO.equals(task.getStatus()) || 
                  TaskStatus.IN_PROGRESS.equals(task.getStatus()) || 
                  TaskStatus.DONE.equals(task.getStatus()),
                  message + " - Task status should be TODO, IN_PROGRESS, or DONE");
    }
    
    public static void assertValidTaskStatus(Task task) {
        assertValidTaskStatus(task, "Task status assertion");
    }
    
    /**
     * Assert Business Rule: Task due date should not be in the past for new tasks
     */
    public static void assertTaskDueDateIsValid(Task task, String message) {
        assertNotNull(task, message + " - Task should not be null");
        if (task.getDueDate() != null && TaskStatus.TODO.equals(task.getStatus())) {
            assertFalse(task.getDueDate().isBefore(LocalDate.now()), 
                       message + " - New task due date should not be in the past");
        }
    }
    
    public static void assertTaskDueDateIsValid(Task task) {
        assertTaskDueDateIsValid(task, "Task due date assertion");
    }
    
    /**
     * Assert Email format is valid
     */
    public static void assertValidEmailFormat(String email, String message) {
        assertNotNull(email, message + " - Email should not be null");
        assertTrue(email.contains("@"), message + " - Email should contain @ symbol");
        assertTrue(email.contains("."), message + " - Email should contain domain extension");
        assertFalse(email.startsWith("@"), message + " - Email should not start with @");
        assertFalse(email.endsWith("@"), message + " - Email should not end with @");
    }
    
    public static void assertValidEmailFormat(String email) {
        assertValidEmailFormat(email, "Email format assertion");
    }
}

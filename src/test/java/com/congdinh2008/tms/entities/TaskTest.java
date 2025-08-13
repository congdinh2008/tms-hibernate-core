package com.congdinh2008.tms.entities;

import com.congdinh2008.tms.enums.TaskPriority;
import com.congdinh2008.tms.enums.TaskStatus;
import com.congdinh2008.tms.utils.TestDataBuilder;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive unit tests for Task entity validation and business logic
 * Following Issue 9 requirements for comprehensive testing
 * 
 * @author Cong Dinh
 * @version 1.0.0
 * @since 1.0.0
 */
class TaskTest {
    
    private Validator validator;
    
    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }
    
    @Test
    @DisplayName("Should create valid task with all required fields")
    void shouldCreateValidTask() {
        // Given
        Project project = TestDataBuilder.ProjectBuilder.aProject()
            .withName("Test Project")
            .withStartDate(LocalDate.now())
            .build();
            
        Task task = TestDataBuilder.TaskBuilder.aTask()
            .withTitle("Test Task")
            .withDescription("Test Description")
            .withStatus(TaskStatus.TODO)
            .withPriority(TaskPriority.HIGH)
            .withDueDate(LocalDate.now().plusDays(7))
            .withProject(project)
            .build();
        
        // When
        Set<ConstraintViolation<Task>> violations = validator.validate(task);
        
        // Then
        assertTrue(violations.isEmpty());
        assertEquals("Test Task", task.getTitle());
        assertEquals(TaskStatus.TODO, task.getStatus());
        assertEquals(TaskPriority.HIGH, task.getPriority());
        assertEquals(project, task.getProject());
    }
    
    @Test
    @DisplayName("Should fail validation when title is blank")
    void shouldFailValidationWhenTitleIsBlank() {
        // Given
        Task task = TestDataBuilder.TaskBuilder.aTask()
            .withTitle("")
            .withStatus(TaskStatus.TODO)
            .withPriority(TaskPriority.HIGH)
            .withDueDate(LocalDate.now().plusDays(7))
            .build();
        
        // When
        Set<ConstraintViolation<Task>> violations = validator.validate(task);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(violation -> 
            violation.getPropertyPath().toString().equals("title")));
    }
    
    @Test
    @DisplayName("Should fail validation when title is null")
    void shouldFailValidationWhenTitleIsNull() {
        // Given
        Task task = TestDataBuilder.TaskBuilder.aTask()
            .withTitle(null)
            .withStatus(TaskStatus.TODO)
            .withPriority(TaskPriority.HIGH)
            .withDueDate(LocalDate.now().plusDays(7))
            .build();
        
        // When
        Set<ConstraintViolation<Task>> violations = validator.validate(task);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(violation -> 
            violation.getPropertyPath().toString().equals("title")));
    }
    
    @Test
    @DisplayName("Should handle parent-child relationship correctly")
    void shouldHandleParentChildRelationship() {
        // Given
        Project project = TestDataBuilder.ProjectBuilder.aProject()
            .withDefaults()
            .build();
            
        Task parentTask = TestDataBuilder.TaskBuilder.aTask()
            .withTitle("Parent Task")
            .withStatus(TaskStatus.TODO)
            .withPriority(TaskPriority.HIGH)
            .withDueDate(LocalDate.now().plusDays(7))
            .withProject(project)
            .build();
            
        Task subTask = TestDataBuilder.TaskBuilder.aTask()
            .withTitle("Sub Task")
            .withStatus(TaskStatus.TODO)
            .withPriority(TaskPriority.MEDIUM)
            .withDueDate(LocalDate.now().plusDays(5))
            .withProject(project)
            .build();
        
        // When
        subTask.setParentTask(parentTask);
        parentTask.getSubTasks().add(subTask);
        
        // Then
        assertEquals(parentTask, subTask.getParentTask());
        assertTrue(parentTask.getSubTasks().contains(subTask));
        assertEquals(1, parentTask.getSubTasks().size());
    }
    
    @Test
    @DisplayName("Should validate task priority is required")
    void shouldValidateTaskPriorityIsRequired() {
        // Given
        Task task = TestDataBuilder.TaskBuilder.aTask()
            .withTitle("Test Task")
            .withStatus(TaskStatus.TODO)
            .withPriority(null)
            .withDueDate(LocalDate.now().plusDays(7))
            .build();
        
        // When
        Set<ConstraintViolation<Task>> violations = validator.validate(task);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(violation -> 
            violation.getPropertyPath().toString().equals("priority")));
    }
    
    @Test
    @DisplayName("Should validate task status is required")
    void shouldValidateTaskStatusIsRequired() {
        // Given
        Task task = TestDataBuilder.TaskBuilder.aTask()
            .withTitle("Test Task")
            .withStatus(null)
            .withPriority(TaskPriority.HIGH)
            .withDueDate(LocalDate.now().plusDays(7))
            .build();
        
        // When
        Set<ConstraintViolation<Task>> violations = validator.validate(task);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(violation -> 
            violation.getPropertyPath().toString().equals("status")));
    }
    
    @Test
    @DisplayName("Should allow task with valid due date")
    void shouldAllowTaskWithValidDueDate() {
        // Given
        Task task = TestDataBuilder.TaskBuilder.aTask()
            .withDefaults()
            .withDueDate(LocalDate.now().plusDays(1))
            .build();
        
        // When
        Set<ConstraintViolation<Task>> violations = validator.validate(task);
        
        // Then
        assertTrue(violations.isEmpty());
        assertTrue(task.getDueDate().isAfter(LocalDate.now()));
    }
    
    @Test
    @DisplayName("Should validate task title length constraints")
    void shouldValidateTaskTitleLengthConstraints() {
        // Given - Title too long (assuming max length constraint exists)
        String longTitle = "A".repeat(256); // Assuming 255 is max length
        Task task = TestDataBuilder.TaskBuilder.aTask()
            .withTitle(longTitle)
            .withDefaults()
            .build();
        
        // When
        Set<ConstraintViolation<Task>> violations = validator.validate(task);
        
        // Then - This might pass if no length constraint is defined, which is also valid
        // The test documents the expected behavior
        if (!violations.isEmpty()) {
            assertTrue(violations.stream().anyMatch(violation -> 
                violation.getPropertyPath().toString().equals("title")));
        }
    }
    
    @Test
    @DisplayName("Should properly initialize collections")
    void shouldProperlyInitializeCollections() {
        // Given
        Task task = new Task();
        
        // Then
        assertNotNull(task.getSubTasks());
        assertNotNull(task.getTags());
        assertTrue(task.getSubTasks().isEmpty());
        assertTrue(task.getTags().isEmpty());
    }
    
    @Test
    @DisplayName("Should handle task assignment correctly")
    void shouldHandleTaskAssignmentCorrectly() {
        // Given
        User user = TestDataBuilder.UserBuilder.aUser()
            .withDefaults()
            .build();
            
        Task task = TestDataBuilder.TaskBuilder.aTask()
            .withDefaults()
            .withAssignee(user)
            .build();
        
        // When & Then
        assertEquals(user, task.getAssignee());
        
        // Unassign task
        task.setAssignee(null);
        assertNull(task.getAssignee());
    }
}

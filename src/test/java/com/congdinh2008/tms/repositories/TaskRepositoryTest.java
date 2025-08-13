package com.congdinh2008.tms.repositories;

import com.congdinh2008.tms.config.ApplicationConfig;
import com.congdinh2008.tms.config.TestConfig;
import com.congdinh2008.tms.entities.Project;
import com.congdinh2008.tms.entities.Task;
import com.congdinh2008.tms.entities.User;
import com.congdinh2008.tms.enums.TaskPriority;
import com.congdinh2008.tms.enums.TaskStatus;
import com.congdinh2008.tms.utils.TestDataBuilder;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for TaskRepository following Issue 9 requirements
 * Tests repository methods with real database operations
 * 
 * @author Cong Dinh
 * @version 1.0.0
 * @since 1.0.0
 */
@SpringJUnitConfig
@ContextConfiguration(classes = {ApplicationConfig.class, TestConfig.class})
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TaskRepositoryTest {
    
    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private ProjectRepository projectRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    private Project testProject;
    private User testUser;
    
    @BeforeEach
    void setUp() {
        // Create test project with start date in past
        testProject = TestDataBuilder.ProjectBuilder.aProject()
            .withDefaults()
            .withStartDate(LocalDate.now().minusDays(30))  // Override defaults with start 30 days ago
            .build();
        testProject = projectRepository.save(testProject);
        
        // Create test user
        testUser = TestDataBuilder.UserBuilder.aUser()
            .withDefaults()
            .build();
        testUser = userRepository.save(testUser);
        
        // Add user as project member
        testProject.addMember(testUser);
        testProject = projectRepository.save(testProject);
    }
    
    @Test
    @Order(1)
    @DisplayName("Should save and find task by ID")
    void shouldSaveAndFindTaskById() {
        // Given
        Task task = TestDataBuilder.TaskBuilder.aTask()
            .withTitle("Integration Test Task")
            .withDescription("Test Description")
            .withStatus(TaskStatus.TODO)
            .withPriority(TaskPriority.HIGH)
            .withDueDate(LocalDate.now().plusDays(7))
            .withProject(testProject)
            .build();
        
        // When
        Task savedTask = taskRepository.save(task);
        Task foundTask = taskRepository.findById(savedTask.getId());
        
        // Then
        assertNotNull(foundTask);
        assertEquals("Integration Test Task", foundTask.getTitle());
        assertEquals(testProject.getName(), foundTask.getProject().getName());
        assertEquals(TaskStatus.TODO, foundTask.getStatus());
        assertEquals(TaskPriority.HIGH, foundTask.getPriority());
    }
    
    @Test
    @Order(2)
    @DisplayName("Should find overdue tasks using query")
    void shouldFindOverdueTasks() {
        // Given
        Task overdueTask = TestDataBuilder.TaskBuilder.aTask()
            .withTitle("Overdue Task")
            .withStatus(TaskStatus.TODO)
            .withPriority(TaskPriority.HIGH)
            .withDueDate(LocalDate.now().minusDays(1))
            .withProject(testProject)
            .build();
            
        Task futureTask = TestDataBuilder.TaskBuilder.aTask()
            .withTitle("Future Task")
            .withStatus(TaskStatus.TODO)
            .withPriority(TaskPriority.MEDIUM)
            .withDueDate(LocalDate.now().plusDays(7))
            .withProject(testProject)
            .build();
        
        taskRepository.save(overdueTask);
        taskRepository.save(futureTask);
        
        // When
        List<Task> overdueTasks = taskRepository.findOverdueTasks();
        
        // Then
        assertFalse(overdueTasks.isEmpty());
        assertTrue(overdueTasks.stream()
            .anyMatch(task -> "Overdue Task".equals(task.getTitle())));
        assertTrue(overdueTasks.stream()
            .noneMatch(task -> "Future Task".equals(task.getTitle())));
    }
    
    @Test
    @Order(3)
    @DisplayName("Should find tasks by project")
    void shouldFindTasksByProject() {
        // Given
        Project anotherProject = TestDataBuilder.ProjectBuilder.aProject()
            .withName("Another Project")
            .withDescription("Another test project")
            .withStartDate(LocalDate.now().minusDays(30))  // Start 30 days ago like testProject
            .build();
        anotherProject = projectRepository.save(anotherProject);
        
        Task task1 = TestDataBuilder.TaskBuilder.aTask()
            .withDefaults()
            .withTitle("Task in Project 1")
            .withProject(testProject)
            .withDueDate(LocalDate.now().plusDays(7))  // Valid due date
            .build();
            
        Task task2 = TestDataBuilder.TaskBuilder.aTask()
            .withDefaults()
            .withTitle("Task in Project 2")
            .withProject(anotherProject)
            .withDueDate(LocalDate.now().plusDays(7))  // Valid due date
            .build();
        
        taskRepository.save(task1);
        taskRepository.save(task2);
        
        // When
        List<Task> tasksInProject1 = taskRepository.findByProject(testProject.getId());
        List<Task> tasksInProject2 = taskRepository.findByProject(anotherProject.getId());
        
        // Then
        assertFalse(tasksInProject1.isEmpty());
        assertFalse(tasksInProject2.isEmpty());
        assertTrue(tasksInProject1.stream()
            .anyMatch(task -> "Task in Project 1".equals(task.getTitle())));
        assertTrue(tasksInProject2.stream()
            .anyMatch(task -> "Task in Project 2".equals(task.getTitle())));
    }
    
    @Test
    @Order(4)
    @DisplayName("Should find tasks by assignee")
    void shouldFindTasksByAssignee() {
        // Given
        User anotherUser = TestDataBuilder.UserBuilder.aUser()
            .withName("Another User")
            .withEmail("another@example.com")
            .withPassword("password456")
            .build();
        anotherUser = userRepository.save(anotherUser);
        
        // Add another user as project member
        testProject.addMember(anotherUser);
        testProject = projectRepository.save(testProject);
        
        Task task1 = TestDataBuilder.TaskBuilder.aTask()
            .withDefaults()
            .withTitle("Task for User 1")
            .withProject(testProject)
            .withAssignee(testUser)
            .withDueDate(LocalDate.now().plusDays(7))  // Valid due date
            .build();
            
        Task task2 = TestDataBuilder.TaskBuilder.aTask()
            .withDefaults()
            .withTitle("Task for User 2")
            .withProject(testProject)
            .withAssignee(anotherUser)
            .withDueDate(LocalDate.now().plusDays(14))  // Valid due date
            .build();
        
        taskRepository.save(task1);
        taskRepository.save(task2);
        
        // When
        List<Task> tasksForUser1 = taskRepository.findByAssignee(testUser.getId());
        List<Task> tasksForUser2 = taskRepository.findByAssignee(anotherUser.getId());
        
        // Then
        assertFalse(tasksForUser1.isEmpty());
        assertFalse(tasksForUser2.isEmpty());
        assertTrue(tasksForUser1.stream()
            .anyMatch(task -> "Task for User 1".equals(task.getTitle())));
        assertTrue(tasksForUser2.stream()
            .anyMatch(task -> "Task for User 2".equals(task.getTitle())));
    }
    
    @Test
    @Order(5)
    @DisplayName("Should handle task updates correctly")
    void shouldHandleTaskUpdatesCorrectly() {
        // Given
        Task task = TestDataBuilder.TaskBuilder.aTask()
            .withTitle("Original Title")
            .withStatus(TaskStatus.TODO)
            .withProject(testProject)
            .withDefaults()
            .build();
        
        Task savedTask = taskRepository.save(task);
        
        // When
        savedTask.setTitle("Updated Title");
        savedTask.setStatus(TaskStatus.IN_PROGRESS);
        Task updatedTask = taskRepository.update(savedTask);
        
        // Then
        assertNotNull(updatedTask);
        assertEquals("Updated Title", updatedTask.getTitle());
        assertEquals(TaskStatus.IN_PROGRESS, updatedTask.getStatus());
        
        // Verify persistence
        Task foundTask = taskRepository.findById(savedTask.getId());
        assertEquals("Updated Title", foundTask.getTitle());
        assertEquals(TaskStatus.IN_PROGRESS, foundTask.getStatus());
    }
    
    @Test
    @Order(6)
    @DisplayName("Should delete task correctly")
    void shouldDeleteTaskCorrectly() {
        // Given
        Task task = TestDataBuilder.TaskBuilder.aTask()
            .withTitle("Task to Delete")
            .withProject(testProject)
            .withDefaults()
            .build();
        
        Task savedTask = taskRepository.save(task);
        Long taskId = savedTask.getId();
        
        // When
        taskRepository.delete(savedTask);
        
        // Then
        Task deletedTask = taskRepository.findById(taskId);
        assertNull(deletedTask);
    }
}

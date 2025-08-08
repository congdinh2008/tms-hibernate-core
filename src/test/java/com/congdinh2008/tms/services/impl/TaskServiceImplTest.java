package com.congdinh2008.tms.services.impl;

import com.congdinh2008.tms.dto.request.CreateTaskRequest;
import com.congdinh2008.tms.dto.request.UpdateTaskRequest;
import com.congdinh2008.tms.dto.response.TaskResponse;
import com.congdinh2008.tms.entities.Project;
import com.congdinh2008.tms.entities.Task;
import com.congdinh2008.tms.entities.User;
import com.congdinh2008.tms.enums.TaskPriority;
import com.congdinh2008.tms.enums.TaskStatus;
import com.congdinh2008.tms.exceptions.BusinessRuleViolationException;
import com.congdinh2008.tms.exceptions.EntityNotFoundException;
import com.congdinh2008.tms.exceptions.InvalidAssignmentException;
import com.congdinh2008.tms.repositories.ProjectRepository;
import com.congdinh2008.tms.repositories.TagRepository;
import com.congdinh2008.tms.repositories.TaskRepository;
import com.congdinh2008.tms.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for TaskServiceImpl
 * 
 * @author Cong Dinh
 * @version 1.0.0
 * @since 1.0.0
 */
class TaskServiceImplTest {
    
    @Mock
    private TaskRepository taskRepository;
    
    @Mock
    private ProjectRepository projectRepository;
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private TagRepository tagRepository;
    
    private TaskServiceImpl taskService;
    
    private Task testTask;
    private Project testProject;
    private User testUser;
    private CreateTaskRequest createRequest;
    private UpdateTaskRequest updateRequest;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        taskService = new TaskServiceImpl(taskRepository, projectRepository, userRepository, tagRepository);
        
        testProject = new Project();
        testProject.setId(1L);
        testProject.setName("Test Project");
        
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");
        
        testTask = new Task();
        testTask.setId(1L);
        testTask.setTitle("Test Task");
        testTask.setDescription("Test Description");
        testTask.setStatus(TaskStatus.TODO);
        testTask.setPriority(TaskPriority.MEDIUM);
        testTask.setDueDate(LocalDate.now().plusDays(7));
        testTask.setProject(testProject);
        testTask.setTags(new ArrayList<>());
        
        createRequest = new CreateTaskRequest();
        createRequest.setTitle("New Task");
        createRequest.setDescription("New Description");
        createRequest.setProjectId(1L);
        createRequest.setAssigneeId(1L);
        createRequest.setPriority(TaskPriority.HIGH);
        createRequest.setDueDate(LocalDate.now().plusDays(5));
        
        updateRequest = new UpdateTaskRequest();
        updateRequest.setTitle("Updated Task");
        updateRequest.setStatus(TaskStatus.IN_PROGRESS);
    }
    
    @Test
    void create_ShouldCreateTask_WhenValidRequest() {
        // Given
        when(projectRepository.findByIdOptional(1L)).thenReturn(Optional.of(testProject));
        when(userRepository.findByIdOptional(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.isUserMemberOfProject(1L, 1L)).thenReturn(true);
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);
        
        // When
        TaskResponse result = taskService.create(createRequest);
        
        // Then
        assertNotNull(result);
        assertEquals(testTask.getId(), result.getId());
        verify(taskRepository).save(any(Task.class));
    }
    
    @Test
    void create_ShouldThrowEntityNotFoundException_WhenProjectNotFound() {
        // Given
        when(projectRepository.findByIdOptional(1L)).thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(EntityNotFoundException.class, () -> taskService.create(createRequest));
        verify(taskRepository, never()).save(any(Task.class));
    }
    
    @Test
    void create_ShouldThrowInvalidAssignmentException_WhenAssigneeNotProjectMember() {
        // Given
        when(projectRepository.findByIdOptional(1L)).thenReturn(Optional.of(testProject));
        when(userRepository.findByIdOptional(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.isUserMemberOfProject(1L, 1L)).thenReturn(false);
        
        // When & Then
        assertThrows(InvalidAssignmentException.class, () -> taskService.create(createRequest));
        verify(taskRepository, never()).save(any(Task.class));
    }
    
    @Test
    void getById_ShouldReturnTask_WhenTaskExists() {
        // Given
        when(taskRepository.findByIdOptional(1L)).thenReturn(Optional.of(testTask));
        
        // When
        TaskResponse result = taskService.getById(1L);
        
        // Then
        assertNotNull(result);
        assertEquals(testTask.getId(), result.getId());
    }
    
    @Test
    void getById_ShouldThrowEntityNotFoundException_WhenTaskNotFound() {
        // Given
        when(taskRepository.findByIdOptional(1L)).thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(EntityNotFoundException.class, () -> taskService.getById(1L));
    }
    
    @Test
    void delete_ShouldDeleteTask_WhenNoSubtasks() {
        // Given
        when(taskRepository.findByIdOptional(1L)).thenReturn(Optional.of(testTask));
        when(taskRepository.findSubTasks(1L)).thenReturn(new ArrayList<>());
        
        // When
        taskService.delete(1L);
        
        // Then
        verify(taskRepository).delete(testTask);
    }
    
    @Test
    void delete_ShouldThrowBusinessRuleViolationException_WhenHasSubtasks() {
        // Given
        Task subtask = new Task();
        subtask.setId(2L);
        
        when(taskRepository.findByIdOptional(1L)).thenReturn(Optional.of(testTask));
        when(taskRepository.findSubTasks(1L)).thenReturn(List.of(subtask));
        
        // When & Then
        BusinessRuleViolationException exception = assertThrows(
                BusinessRuleViolationException.class, 
                () -> taskService.delete(1L)
        );
        assertEquals("R6", exception.getRuleCode());
        verify(taskRepository, never()).delete(any(Task.class));
    }
    
    @Test
    void assignTask_ShouldAssignTask_WhenUserIsProjectMember() {
        // Given
        when(taskRepository.findByIdOptional(1L)).thenReturn(Optional.of(testTask));
        when(userRepository.findByIdOptional(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.isUserMemberOfProject(1L, 1L)).thenReturn(true);
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);
        
        // When
        TaskResponse result = taskService.assignTask(1L, 1L);
        
        // Then
        assertNotNull(result);
        assertEquals(testUser, testTask.getAssignee());
        verify(taskRepository).save(testTask);
    }
    
    @Test
    void assignTask_ShouldThrowInvalidAssignmentException_WhenUserNotProjectMember() {
        // Given
        when(taskRepository.findByIdOptional(1L)).thenReturn(Optional.of(testTask));
        when(userRepository.findByIdOptional(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.isUserMemberOfProject(1L, 1L)).thenReturn(false);
        
        // When & Then
        assertThrows(InvalidAssignmentException.class, () -> taskService.assignTask(1L, 1L));
        verify(taskRepository, never()).save(any(Task.class));
    }
    
    @Test
    void changeStatus_ShouldChangeStatus_WhenValidTransition() {
        // Given
        when(taskRepository.findByIdOptional(1L)).thenReturn(Optional.of(testTask));
        when(taskRepository.findSubTasks(1L)).thenReturn(new ArrayList<>());
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);
        
        // When
        TaskResponse result = taskService.changeStatus(1L, TaskStatus.DONE);
        
        // Then
        assertNotNull(result);
        assertEquals(TaskStatus.DONE, testTask.getStatus());
        verify(taskRepository).save(testTask);
    }
    
    @Test
    void changeStatus_ShouldThrowBusinessRuleViolationException_WhenCompletingWithIncompleteSubtasks() {
        // Given
        Task incompleteSubtask = new Task();
        incompleteSubtask.setId(2L);
        incompleteSubtask.setStatus(TaskStatus.TODO);
        
        when(taskRepository.findByIdOptional(1L)).thenReturn(Optional.of(testTask));
        when(taskRepository.findSubTasks(1L)).thenReturn(List.of(incompleteSubtask));
        
        // When & Then
        BusinessRuleViolationException exception = assertThrows(
                BusinessRuleViolationException.class, 
                () -> taskService.changeStatus(1L, TaskStatus.DONE)
        );
        assertEquals("R7", exception.getRuleCode());
        verify(taskRepository, never()).save(any(Task.class));
    }
    
    @Test
    void getTasksByProject_ShouldReturnTasks_WhenProjectExists() {
        // Given
        when(projectRepository.existsById(1L)).thenReturn(true);
        when(taskRepository.findByProject(1L)).thenReturn(List.of(testTask));
        
        // When
        List<TaskResponse> result = taskService.getTasksByProject(1L);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testTask.getId(), result.get(0).getId());
    }
    
    @Test
    void getTasksByProject_ShouldThrowEntityNotFoundException_WhenProjectNotFound() {
        // Given
        when(projectRepository.existsById(1L)).thenReturn(false);
        
        // When & Then
        assertThrows(EntityNotFoundException.class, () -> taskService.getTasksByProject(1L));
    }
    
    @Test
    void unassignTask_ShouldUnassignTask_WhenTaskExists() {
        // Given
        testTask.setAssignee(testUser);
        when(taskRepository.findByIdOptional(1L)).thenReturn(Optional.of(testTask));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);
        
        // When
        TaskResponse result = taskService.unassignTask(1L);
        
        // Then
        assertNotNull(result);
        assertNull(testTask.getAssignee());
        verify(taskRepository).save(testTask);
    }
    
    @Test
    void getAll_ShouldReturnAllTasks() {
        // Given
        List<Task> tasks = List.of(testTask);
        when(taskRepository.findAll()).thenReturn(tasks);
        
        // When
        List<TaskResponse> result = taskService.getAll();
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testTask.getId(), result.get(0).getId());
    }
}

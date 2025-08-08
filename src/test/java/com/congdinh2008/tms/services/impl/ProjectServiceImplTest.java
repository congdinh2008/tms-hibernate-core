package com.congdinh2008.tms.services.impl;

import com.congdinh2008.tms.dto.request.CreateProjectRequest;
import com.congdinh2008.tms.dto.request.UpdateProjectRequest;
import com.congdinh2008.tms.dto.response.ProjectResponse;
import com.congdinh2008.tms.entities.Project;
import com.congdinh2008.tms.entities.User;
import com.congdinh2008.tms.exceptions.BusinessRuleViolationException;
import com.congdinh2008.tms.exceptions.DuplicateEntityException;
import com.congdinh2008.tms.exceptions.EntityNotFoundException;
import com.congdinh2008.tms.repositories.ProjectRepository;
import com.congdinh2008.tms.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ProjectServiceImpl
 * 
 * @author Cong Dinh
 * @version 1.0.0
 * @since 1.0.0
 */
class ProjectServiceImplTest {
    
    @Mock
    private ProjectRepository projectRepository;
    
    @Mock
    private UserRepository userRepository;
    
    private ProjectServiceImpl projectService;
    
    private Project testProject;
    private User testUser;
    private CreateProjectRequest createRequest;
    private UpdateProjectRequest updateRequest;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        projectService = new ProjectServiceImpl(projectRepository, userRepository);
        
        testProject = new Project();
        testProject.setId(1L);
        testProject.setName("Test Project");
        testProject.setDescription("Test Description");
        testProject.setStartDate(LocalDate.now());
        testProject.setMembers(new ArrayList<>());
        
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password");
        
        createRequest = new CreateProjectRequest();
        createRequest.setName("New Project");
        createRequest.setDescription("New Description");
        createRequest.setStartDate(LocalDate.now());
        createRequest.setMemberIds(Set.of(1L));
        
        updateRequest = new UpdateProjectRequest();
        updateRequest.setName("Updated Project");
        updateRequest.setDescription("Updated Description");
    }
    
    @Test
    void create_ShouldCreateProject_WhenValidRequest() {
        // Given
        when(projectRepository.findByNameContaining(anyString())).thenReturn(new ArrayList<>());
        when(userRepository.findByIdOptional(1L)).thenReturn(Optional.of(testUser));
        when(projectRepository.save(any(Project.class))).thenReturn(testProject);
        
        // When
        ProjectResponse result = projectService.create(createRequest);
        
        // Then
        assertNotNull(result);
        assertEquals(testProject.getId(), result.getId());
        verify(projectRepository).save(any(Project.class));
    }
    
    @Test
    void create_ShouldThrowDuplicateEntityException_WhenProjectNameExists() {
        // Given
        Project duplicateProject = new Project();
        duplicateProject.setId(2L);
        duplicateProject.setName("New Project"); // Same name as request
        
        List<Project> existingProjects = List.of(duplicateProject);
        when(projectRepository.findByNameContaining(anyString())).thenReturn(existingProjects);
        when(userRepository.findByIdOptional(1L)).thenReturn(Optional.of(testUser)); // Mock owner lookup
        
        // When & Then
        assertThrows(DuplicateEntityException.class, () -> projectService.create(createRequest));
        verify(projectRepository, never()).save(any(Project.class));
    }
    
    @Test
    void create_ShouldThrowEntityNotFoundException_WhenMemberNotFound() {
        // Given
        when(projectRepository.findByNameContaining(anyString())).thenReturn(new ArrayList<>());
        when(userRepository.findByIdOptional(1L)).thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(EntityNotFoundException.class, () -> projectService.create(createRequest));
        verify(projectRepository, never()).save(any(Project.class));
    }
    
    @Test
    void getById_ShouldReturnProject_WhenProjectExists() {
        // Given
        when(projectRepository.findByIdOptional(1L)).thenReturn(Optional.of(testProject));
        
        // When
        ProjectResponse result = projectService.getById(1L);
        
        // Then
        assertNotNull(result);
        assertEquals(testProject.getId(), result.getId());
    }
    
    @Test
    void getById_ShouldThrowEntityNotFoundException_WhenProjectNotFound() {
        // Given
        when(projectRepository.findByIdOptional(1L)).thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(EntityNotFoundException.class, () -> projectService.getById(1L));
    }
    
    @Test
    void update_ShouldUpdateProject_WhenValidRequest() {
        // Given
        when(projectRepository.findByIdOptional(1L)).thenReturn(Optional.of(testProject));
        when(projectRepository.findByNameContaining(anyString())).thenReturn(new ArrayList<>());
        when(projectRepository.save(any(Project.class))).thenReturn(testProject);
        
        // When
        ProjectResponse result = projectService.update(1L, updateRequest);
        
        // Then
        assertNotNull(result);
        verify(projectRepository).save(testProject);
    }
    
    @Test
    void update_ShouldThrowDuplicateEntityException_WhenNewNameExists() {
        // Given
        Project anotherProject = new Project();
        anotherProject.setId(2L);
        anotherProject.setName("Updated Project");
        
        when(projectRepository.findByIdOptional(1L)).thenReturn(Optional.of(testProject));
        when(projectRepository.findByNameContaining(anyString())).thenReturn(List.of(anotherProject));
        
        // When & Then
        assertThrows(DuplicateEntityException.class, () -> projectService.update(1L, updateRequest));
        verify(projectRepository, never()).save(any(Project.class));
    }
    
    @Test
    void delete_ShouldDeleteProject_WhenNoActiveTasks() {
        // Given
        when(projectRepository.findByIdOptional(1L)).thenReturn(Optional.of(testProject));
        when(projectRepository.hasIncompleteTasks(1L)).thenReturn(false);
        
        // When
        projectService.delete(1L);
        
        // Then
        verify(projectRepository).delete(testProject);
    }
    
    @Test
    void delete_ShouldThrowBusinessRuleViolationException_WhenHasActiveTasks() {
        // Given
        when(projectRepository.findByIdOptional(1L)).thenReturn(Optional.of(testProject));
        when(projectRepository.hasIncompleteTasks(1L)).thenReturn(true);
        
        // When & Then
        BusinessRuleViolationException exception = assertThrows(
                BusinessRuleViolationException.class, 
                () -> projectService.delete(1L)
        );
        assertEquals("R1", exception.getRuleCode());
        verify(projectRepository, never()).delete(any(Project.class));
    }
    
    @Test
    void addMember_ShouldAddMember_WhenValidRequest() {
        // Given
        when(projectRepository.findByIdOptional(1L)).thenReturn(Optional.of(testProject));
        when(userRepository.findByIdOptional(1L)).thenReturn(Optional.of(testUser));
        when(projectRepository.save(any(Project.class))).thenReturn(testProject);
        
        // When
        ProjectResponse result = projectService.addMember(1L, 1L);
        
        // Then
        assertNotNull(result);
        assertTrue(testProject.getMembers().contains(testUser));
        verify(projectRepository).save(testProject);
    }
    
    @Test
    void addMember_ShouldThrowDuplicateEntityException_WhenMemberAlreadyExists() {
        // Given
        testProject.getMembers().add(testUser);
        when(projectRepository.findByIdOptional(1L)).thenReturn(Optional.of(testProject));
        when(userRepository.findByIdOptional(1L)).thenReturn(Optional.of(testUser));
        
        // When & Then
        assertThrows(DuplicateEntityException.class, () -> projectService.addMember(1L, 1L));
        verify(projectRepository, never()).save(any(Project.class));
    }
    
    @Test
    void removeMember_ShouldRemoveMember_WhenMemberExists() {
        // Given
        testProject.getMembers().add(testUser);
        when(projectRepository.findByIdOptional(1L)).thenReturn(Optional.of(testProject));
        when(userRepository.findByIdOptional(1L)).thenReturn(Optional.of(testUser));
        when(projectRepository.save(any(Project.class))).thenReturn(testProject);
        
        // When
        ProjectResponse result = projectService.removeMember(1L, 1L);
        
        // Then
        assertNotNull(result);
        assertFalse(testProject.getMembers().contains(testUser));
        verify(projectRepository).save(testProject);
    }
    
    @Test
    void getAll_ShouldReturnAllProjects() {
        // Given
        List<Project> projects = List.of(testProject);
        when(projectRepository.findAll()).thenReturn(projects);
        
        // When
        List<ProjectResponse> result = projectService.getAll();
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testProject.getId(), result.get(0).getId());
    }
    
    @Test
    void count_ShouldReturnProjectCount() {
        // Given
        when(projectRepository.count()).thenReturn(5L);
        
        // When
        long result = projectService.count();
        
        // Then
        assertEquals(5L, result);
    }
    
    @Test
    void existsById_ShouldReturnTrue_WhenProjectExists() {
        // Given
        when(projectRepository.existsById(1L)).thenReturn(true);
        
        // When
        boolean result = projectService.existsById(1L);
        
        // Then
        assertTrue(result);
    }
    
    @Test
    void getProjectsByMember_ShouldReturnProjects_WhenUserExists() {
        // Given
        when(userRepository.existsById(1L)).thenReturn(true);
        when(projectRepository.findProjectsByUser(1L)).thenReturn(List.of(testProject));
        
        // When
        List<ProjectResponse> result = projectService.getProjectsByMember(1L);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testProject.getId(), result.get(0).getId());
    }
    
    @Test
    void getProjectsByMember_ShouldThrowEntityNotFoundException_WhenUserNotFound() {
        // Given
        when(userRepository.existsById(1L)).thenReturn(false);
        
        // When & Then
        assertThrows(EntityNotFoundException.class, () -> projectService.getProjectsByMember(1L));
    }
}

package com.congdinh2008.tms.services.impl;

import com.congdinh2008.tms.dto.request.CreateUserRequest;
import com.congdinh2008.tms.dto.request.UpdateUserRequest;
import com.congdinh2008.tms.dto.response.UserResponse;
import com.congdinh2008.tms.entities.User;
import com.congdinh2008.tms.exceptions.DuplicateEntityException;
import com.congdinh2008.tms.exceptions.EntityNotFoundException;
import com.congdinh2008.tms.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UserServiceImpl
 * 
 * @author Cong Dinh
 * @version 1.0.0
 * @since 1.0.0
 */
class UserServiceImplTest {
    
    @Mock
    private UserRepository userRepository;
    
    private UserServiceImpl userService;
    
    private User testUser;
    private CreateUserRequest createRequest;
    private UpdateUserRequest updateRequest;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserServiceImpl(userRepository);
        
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password123");
        
        createRequest = new CreateUserRequest();
        createRequest.setFullName("New User");
        createRequest.setEmail("new@example.com");
        createRequest.setPassword("newpassword");
        
        updateRequest = new UpdateUserRequest();
        updateRequest.setFullName("Updated User");
        updateRequest.setEmail("updated@example.com");
    }
    
    @Test
    void create_ShouldCreateUser_WhenValidRequest() {
        // Given
        when(userRepository.existsByEmail(createRequest.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        
        // When
        UserResponse result = userService.create(createRequest);
        
        // Then
        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        verify(userRepository).save(any(User.class));
    }
    
    @Test
    void create_ShouldThrowDuplicateEntityException_WhenEmailExists() {
        // Given
        when(userRepository.existsByEmail(createRequest.getEmail())).thenReturn(true);
        
        // When & Then
        assertThrows(DuplicateEntityException.class, () -> userService.create(createRequest));
        verify(userRepository, never()).save(any(User.class));
    }
    
    @Test
    void getById_ShouldReturnUser_WhenUserExists() {
        // Given
        when(userRepository.findByIdOptional(1L)).thenReturn(Optional.of(testUser));
        
        // When
        UserResponse result = userService.getById(1L);
        
        // Then
        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        assertEquals(testUser.getName(), result.getName()); // Use getName() which maps to User.name
        assertEquals(testUser.getEmail(), result.getEmail());
    }
    
    @Test
    void getById_ShouldThrowEntityNotFoundException_WhenUserNotFound() {
        // Given
        when(userRepository.findByIdOptional(1L)).thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(EntityNotFoundException.class, () -> userService.getById(1L));
    }
    
    @Test
    void update_ShouldUpdateUser_WhenValidRequest() {
        // Given
        when(userRepository.findByIdOptional(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        
        // When
        UserResponse result = userService.update(1L, updateRequest);
        
        // Then
        assertNotNull(result);
        verify(userRepository).save(testUser);
    }
    
    @Test
    void delete_ShouldDeleteUser_WhenUserExists() {
        // Given
        when(userRepository.findByIdOptional(1L)).thenReturn(Optional.of(testUser));
        
        // When
        userService.delete(1L);
        
        // Then
        verify(userRepository).delete(testUser);
    }
    
    @Test
    void delete_ShouldThrowEntityNotFoundException_WhenUserNotFound() {
        // Given
        when(userRepository.findByIdOptional(1L)).thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(EntityNotFoundException.class, () -> userService.delete(1L));
        verify(userRepository, never()).delete(any(User.class));
    }
    
    @Test
    void getAll_ShouldReturnAllUsers() {
        // Given
        List<User> users = List.of(testUser);
        when(userRepository.findAll()).thenReturn(users);
        
        // When
        List<UserResponse> result = userService.getAll();
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testUser.getId(), result.get(0).getId());
    }
    
    @Test
    void findByEmail_ShouldReturnUser_WhenEmailExists() {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(testUser);
        
        // When
        UserResponse result = userService.findByEmail("test@example.com");
        
        // Then
        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        assertEquals(testUser.getEmail(), result.getEmail());
    }
    
    @Test
    void findByEmail_ShouldThrowEntityNotFoundException_WhenEmailNotFound() {
        // Given
        when(userRepository.findByEmail("notfound@example.com")).thenReturn(null);
        
        // When & Then
        assertThrows(EntityNotFoundException.class, () -> userService.findByEmail("notfound@example.com"));
    }
    
    @Test
    void searchByName_ShouldReturnUsers_WhenNameMatches() {
        // Given
        when(userRepository.findByNameContaining("Test")).thenReturn(List.of(testUser));
        
        // When
        List<UserResponse> result = userService.searchByName("Test");
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testUser.getId(), result.get(0).getId());
    }
    
    @Test
    void searchByName_ShouldReturnEmptyList_WhenNoMatches() {
        // Given
        when(userRepository.findByNameContaining("NonExistent")).thenReturn(List.of());
        
        // When
        List<UserResponse> result = userService.searchByName("NonExistent");
        
        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}

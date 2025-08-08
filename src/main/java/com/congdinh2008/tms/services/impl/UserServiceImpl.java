package com.congdinh2008.tms.services.impl;

import com.congdinh2008.tms.dto.request.CreateUserRequest;
import com.congdinh2008.tms.dto.request.UpdateUserRequest;
import com.congdinh2008.tms.dto.response.UserResponse;
import com.congdinh2008.tms.entities.User;
import com.congdinh2008.tms.exceptions.DuplicateEntityException;
import com.congdinh2008.tms.exceptions.EntityNotFoundException;
import com.congdinh2008.tms.exceptions.InvalidAssignmentException;
import com.congdinh2008.tms.repositories.UserRepository;
import com.congdinh2008.tms.services.UserService;
import com.congdinh2008.tms.utils.MapperUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of UserService providing business logic for user operations
 * 
 * @author Cong Dinh
 * @version 1.0.0
 * @since 1.0.0
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private static final String USER_ENTITY = "User";
    
    private final UserRepository userRepository;
    
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    @Override
    public UserResponse create(CreateUserRequest request) {
        logger.info("Creating user with email: {}", request.getEmail());
        
        // Business Rule: Check for duplicate email
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEntityException(USER_ENTITY, "email", request.getEmail());
        }
        
        // Convert request to entity - map appropriate fields
        User user = new User();
        user.setName(request.getFullName()); // Use fullName as name in User entity
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        
        // Save user
        User savedUser = userRepository.save(user);
        
        logger.info("User created successfully with ID: {}", savedUser.getId());
        return MapperUtil.mapToDto(savedUser, UserResponse.class);
    }
    
    @Override
    @Transactional(readOnly = true)
    public UserResponse getById(Long id) {
        logger.debug("Retrieving user with ID: {}", id);
        
        User user = userRepository.findByIdOptional(id)
                .orElseThrow(() -> new EntityNotFoundException(USER_ENTITY, id));
        
        return MapperUtil.mapToDto(user, UserResponse.class);
    }
    
    @Override
    public UserResponse update(Long id, UpdateUserRequest request) {
        logger.info("Updating user with ID: {}", id);
        
        User existingUser = userRepository.findByIdOptional(id)
                .orElseThrow(() -> new EntityNotFoundException(USER_ENTITY, id));
        
        // Business Rule: Check for duplicate email if email is being changed
        if (request.getEmail() != null && !request.getEmail().equals(existingUser.getEmail()) 
            && userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEntityException(USER_ENTITY, "email", request.getEmail());
        }
        
        // Update fields that exist in User entity
        if (request.getEmail() != null) {
            existingUser.setEmail(request.getEmail());
        }
        if (request.getFullName() != null) {
            existingUser.setName(request.getFullName()); // Use fullName as name
        }
        if (request.getPassword() != null) {
            existingUser.setPassword(request.getPassword());
        }
        
        User updatedUser = userRepository.save(existingUser);
        
        logger.info("User updated successfully with ID: {}", updatedUser.getId());
        return MapperUtil.mapToDto(updatedUser, UserResponse.class);
    }
    
    @Override
    public void delete(Long id) {
        logger.info("Deleting user with ID: {}", id);
        
        User user = userRepository.findByIdOptional(id)
                .orElseThrow(() -> new EntityNotFoundException(USER_ENTITY, id));
        
        userRepository.delete(user);
        logger.info("User deleted successfully with ID: {}", id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAll() {
        logger.debug("Retrieving all users");
        
        List<User> users = userRepository.findAll();
        List<UserResponse> responses = new ArrayList<>();
        
        for (User user : users) {
            responses.add(MapperUtil.mapToDto(user, UserResponse.class));
        }
        
        return responses;
    }
    
    @Override
    @Transactional(readOnly = true)
    public long count() {
        return userRepository.count();
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return userRepository.existsById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public UserResponse findByEmail(String email) {
        logger.debug("Finding user by email: {}", email);
        
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new EntityNotFoundException("User with email " + email + " not found");
        }
        
        return MapperUtil.mapToDto(user, UserResponse.class);
    }
    
    @Override
    @Transactional(readOnly = true)
    public UserResponse findByUsername(String username) {
        // Not implemented - User entity doesn't have username field
        logger.warn("findByUsername not implemented - User entity uses name field instead");
        return null;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> searchByName(String name) {
        logger.debug("Searching users by name: {}", name);
        
        List<User> users = userRepository.findByNameContaining(name);
        List<UserResponse> responses = new ArrayList<>();
        
        for (User user : users) {
            responses.add(MapperUtil.mapToDto(user, UserResponse.class));
        }
        
        return responses;
    }
    
    @Override
    public UserResponse activateUser(Long userId) {
        // Not implemented - User entity doesn't have isActive field
        logger.warn("activateUser not implemented - User entity doesn't have isActive field");
        User user = userRepository.findByIdOptional(userId)
                .orElseThrow(() -> new EntityNotFoundException(USER_ENTITY, userId));
        return MapperUtil.mapToDto(user, UserResponse.class);
    }
    
    @Override
    public UserResponse deactivateUser(Long userId) {
        // Not implemented - User entity doesn't have isActive field
        logger.warn("deactivateUser not implemented - User entity doesn't have isActive field");
        User user = userRepository.findByIdOptional(userId)
                .orElseThrow(() -> new EntityNotFoundException(USER_ENTITY, userId));
        return MapperUtil.mapToDto(user, UserResponse.class);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getActiveUsers() {
        // Return all users since there's no active/inactive distinction
        logger.debug("Retrieving all users (no active/inactive distinction in entity)");
        
        List<User> users = userRepository.findAll();
        List<UserResponse> responses = new ArrayList<>();
        
        for (User user : users) {
            responses.add(MapperUtil.mapToDto(user, UserResponse.class));
        }
        
        return responses;
    }
    
    @Override
    public UserResponse changePassword(Long userId, String oldPassword, String newPassword) {
        logger.info("Changing password for user with ID: {}", userId);
        
        User user = userRepository.findByIdOptional(userId)
                .orElseThrow(() -> new EntityNotFoundException(USER_ENTITY, userId));
        
        // Simple password validation (in real app, would use proper password encoder)
        if (!user.getPassword().equals(oldPassword)) {
            throw new InvalidAssignmentException("Current password is incorrect");
        }
        
        user.setPassword(newPassword);
        User updatedUser = userRepository.save(user);
        
        logger.info("Password changed successfully");
        return MapperUtil.mapToDto(updatedUser, UserResponse.class);
    }
}

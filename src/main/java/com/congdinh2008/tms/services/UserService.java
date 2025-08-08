package com.congdinh2008.tms.services;

import com.congdinh2008.tms.dto.request.CreateUserRequest;
import com.congdinh2008.tms.dto.request.UpdateUserRequest;
import com.congdinh2008.tms.dto.response.UserResponse;

import java.util.List;

/**
 * Service interface for user-related operations
 * 
 * @author Cong Dinh
 * @version 1.0.0
 * @since 1.0.0
 */
public interface UserService extends BaseService<UserResponse, Long> {
    
    /**
     * Create a new user
     * 
     * @param request user creation request
     * @return created user response
     */
    UserResponse create(CreateUserRequest request);
    
    /**
     * Update an existing user
     * 
     * @param id user ID
     * @param request user update request
     * @return updated user response
     */
    UserResponse update(Long id, UpdateUserRequest request);
    
    /**
     * Find user by email
     * 
     * @param email email address
     * @return user response or null if not found
     */
    UserResponse findByEmail(String email);
    
    /**
     * Find user by username
     * 
     * @param username username
     * @return user response or null if not found
     */
    UserResponse findByUsername(String username);
    
    /**
     * Search users by name containing the specified string
     * 
     * @param name name fragment to search for
     * @return list of users matching the search criteria
     */
    List<UserResponse> searchByName(String name);
    
    /**
     * Activate a user account
     * 
     * @param userId user ID
     * @return updated user response
     */
    UserResponse activateUser(Long userId);
    
    /**
     * Deactivate a user account
     * 
     * @param userId user ID
     * @return updated user response
     */
    UserResponse deactivateUser(Long userId);
    
    /**
     * Get all active users
     * 
     * @return list of active users
     */
    List<UserResponse> getActiveUsers();
    
    /**
     * Change user password
     * 
     * @param userId user ID
     * @param oldPassword current password
     * @param newPassword new password
     * @return updated user response
     */
    UserResponse changePassword(Long userId, String oldPassword, String newPassword);
}

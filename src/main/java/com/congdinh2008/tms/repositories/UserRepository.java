package com.congdinh2008.tms.repositories;

import com.congdinh2008.tms.entities.User;
import java.util.List;

/**
 * Repository interface for User entity operations
 */
public interface UserRepository extends BaseRepository<User, Long> {
    
    /**
     * Find user by email address
     * @param email the email to search for
     * @return the user with the specified email, or null if not found
     */
    User findByEmail(String email);
    
    /**
     * Find users by name containing the specified string (case-insensitive)
     * @param name the name fragment to search for
     * @return list of users whose names contain the specified string
     */
    List<User> findByNameContaining(String name);
    
    /**
     * Check if user exists by email
     * @param email the email to check
     * @return true if user with email exists, false otherwise
     */
    boolean existsByEmail(String email);
    
    /**
     * Find all members of a specific project
     * @param projectId the project ID
     * @return list of users who are members of the project
     */
    List<User> findProjectMembers(Long projectId);
    
    /**
     * Check if user is a member of a specific project
     * @param userId the user ID
     * @param projectId the project ID
     * @return true if user is member of project, false otherwise
     */
    boolean isUserMemberOfProject(Long userId, Long projectId);
    
    /**
     * Find users who have tasks assigned in a specific project
     * @param projectId the project ID
     * @return list of users with assigned tasks in the project
     */
    List<User> findUsersWithTasksInProject(Long projectId);
    
    /**
     * Get user productivity statistics in a specific project
     * Native query with complex aggregations for performance
     * @param projectId the project ID
     * @return list of objects containing user and productivity statistics
     */
    List<Object[]> getUserProductivityInProject(Long projectId);
    
    /**
     * Find top performing users based on completed tasks
     * Native query for performance optimization
     * @param limit maximum number of users to return
     * @return list of users ordered by performance
     */
    List<User> findTopPerformers(Integer limit);
    
    /**
     * Find users with workload analysis
     * Native query with task counting and status analysis
     * @param startDate analysis start date
     * @param endDate analysis end date
     * @return list of objects containing user and workload data
     */
    List<Object[]> getUserWorkloadAnalysis(java.time.LocalDateTime startDate, java.time.LocalDateTime endDate);
}

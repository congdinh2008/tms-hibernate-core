package com.congdinh2008.tms.repositories;

import com.congdinh2008.tms.entities.Project;
import java.util.List;

/**
 * Repository interface for Project entity operations
 */
public interface ProjectRepository extends BaseRepository<Project, Long> {
    
    /**
     * Find projects by name containing the specified string (case-insensitive)
     * @param name the name fragment to search for
     * @return list of projects whose names contain the specified string
     */
    List<Project> findByNameContaining(String name);
    
    /**
     * Find projects where a user is a member
     * @param userId the user ID
     * @return list of projects where the user is a member
     */
    List<Project> findProjectsByUser(Long userId);
    
    /**
     * Check if project has incomplete tasks (Business Rule R1)
     * @param projectId the project ID
     * @return true if project has incomplete tasks, false otherwise
     */
    boolean hasIncompleteTasks(Long projectId);
    
    /**
     * Find projects with their task counts
     * @return list of projects with additional task count information
     */
    List<Object[]> findProjectsWithTaskCount();
    
    /**
     * Check if project can be deleted (Business Rule R1)
     * @param projectId the project ID
     * @return true if project can be deleted, false otherwise
     */
    boolean canDeleteProject(Long projectId);
    
    /**
     * Find active projects (projects with at least one incomplete task)
     * @return list of active projects
     */
    List<Project> findActiveProjects();
    
    /**
     * Find projects with overdue tasks
     * @return list of projects that have overdue tasks
     */
    List<Project> findProjectsWithOverdueTasks();
}

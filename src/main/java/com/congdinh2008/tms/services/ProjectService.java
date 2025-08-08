package com.congdinh2008.tms.services;

import com.congdinh2008.tms.dto.request.CreateProjectRequest;
import com.congdinh2008.tms.dto.request.UpdateProjectRequest;
import com.congdinh2008.tms.dto.response.ProjectResponse;

import java.util.List;

/**
 * Service interface for project-related operations
 * 
 * @author Cong Dinh
 * @version 1.0.0
 * @since 1.0.0
 */
public interface ProjectService extends BaseService<ProjectResponse, Long> {
    
    /**
     * Create a new project
     * 
     * @param request project creation request
     * @return created project response
     */
    ProjectResponse create(CreateProjectRequest request);
    
    /**
     * Update an existing project
     * 
     * @param id project ID
     * @param request project update request
     * @return updated project response
     */
    ProjectResponse update(Long id, UpdateProjectRequest request);
    
    /**
     * Add a member to the project
     * 
     * @param projectId project ID
     * @param userId user ID to add as member
     * @return updated project response
     */
    ProjectResponse addMember(Long projectId, Long userId);
    
    /**
     * Remove a member from the project
     * 
     * @param projectId project ID
     * @param userId user ID to remove from members
     * @return updated project response
     */
    ProjectResponse removeMember(Long projectId, Long userId);
    
    /**
     * Get all projects where a user is a member
     * 
     * @param userId user ID
     * @return list of projects where user is a member
     */
    List<ProjectResponse> getProjectsByMember(Long userId);
}

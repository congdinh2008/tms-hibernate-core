package com.congdinh2008.tms.services;

import com.congdinh2008.tms.dto.request.CreateTaskRequest;
import com.congdinh2008.tms.dto.request.UpdateTaskRequest;
import com.congdinh2008.tms.dto.response.TaskResponse;
import com.congdinh2008.tms.enums.TaskStatus;

import java.util.List;

/**
 * Service interface for task-related operations
 * 
 * @author Cong Dinh
 * @version 1.0.0
 * @since 1.0.0
 */
public interface TaskService extends BaseService<TaskResponse, Long> {
    
    /**
     * Create a new task
     * 
     * @param request task creation request
     * @return created task response
     */
    TaskResponse create(CreateTaskRequest request);
    
    /**
     * Update an existing task
     * 
     * @param id task ID
     * @param request task update request
     * @return updated task response
     */
    TaskResponse update(Long id, UpdateTaskRequest request);
    
    /**
     * Assign task to a user
     * 
     * @param taskId task ID
     * @param userId user ID to assign task to
     * @return updated task response
     */
    TaskResponse assignTask(Long taskId, Long userId);
    
    /**
     * Unassign task from current assignee
     * 
     * @param taskId task ID
     * @return updated task response
     */
    TaskResponse unassignTask(Long taskId);
    
    /**
     * Change task status
     * 
     * @param taskId task ID
     * @param status new status
     * @return updated task response
     */
    TaskResponse changeStatus(Long taskId, TaskStatus status);
    
    /**
     * Get tasks by project ID
     * 
     * @param projectId project ID
     * @return list of tasks in the project
     */
    List<TaskResponse> getTasksByProject(Long projectId);
    
    /**
     * Get tasks assigned to a user
     * 
     * @param userId user ID
     * @return list of tasks assigned to the user
     */
    List<TaskResponse> getTasksByAssignee(Long userId);
    
    /**
     * Get subtasks of a parent task
     * 
     * @param parentTaskId parent task ID
     * @return list of subtasks
     */
    List<TaskResponse> getSubtasks(Long parentTaskId);
    
    /**
     * Add a tag to a task
     * 
     * @param taskId task ID
     * @param tagId tag ID
     * @return updated task response
     */
    TaskResponse addTag(Long taskId, Long tagId);
    
    /**
     * Remove a tag from a task
     * 
     * @param taskId task ID
     * @param tagId tag ID
     * @return updated task response
     */
    TaskResponse removeTag(Long taskId, Long tagId);
}

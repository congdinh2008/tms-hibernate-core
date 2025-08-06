package com.congdinh2008.tms.repositories;

import com.congdinh2008.tms.dto.search.TaskSearchCriteria;
import com.congdinh2008.tms.entities.Task;
import com.congdinh2008.tms.enums.TaskPriority;
import com.congdinh2008.tms.enums.TaskStatus;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for Task entity operations
 */
public interface TaskRepository extends BaseRepository<Task, Long> {
    
    /**
     * Find tasks by project ID
     * @param projectId the project ID
     * @return list of tasks in the project
     */
    List<Task> findByProject(Long projectId);
    
    /**
     * Find tasks assigned to a specific user
     * @param userId the user ID
     * @return list of tasks assigned to the user
     */
    List<Task> findByAssignee(Long userId);
    
    /**
     * Find tasks by status
     * @param status the task status
     * @return list of tasks with the specified status
     */
    List<Task> findByStatus(TaskStatus status);
    
    /**
     * Find tasks by priority
     * @param priority the task priority
     * @return list of tasks with the specified priority
     */
    List<Task> findByPriority(TaskPriority priority);
    
    /**
     * Find overdue tasks (due date before reference date and not completed)
     * @param referenceDate the reference date (usually current time)
     * @return list of overdue tasks
     */
    List<Task> findOverdueTasks(LocalDateTime referenceDate);
    
    /**
     * Find subtasks of a parent task
     * @param parentTaskId the parent task ID
     * @return list of subtasks
     */
    List<Task> findSubTasks(Long parentTaskId);
    
    /**
     * Check if setting a parent task would create a circular reference
     * @param taskId the task ID
     * @param parentTaskId the proposed parent task ID
     * @return true if circular reference would be created, false otherwise
     */
    boolean isCircularReference(Long taskId, Long parentTaskId);
    
    /**
     * Search tasks using complex criteria
     * @param criteria the search criteria
     * @return list of tasks matching the criteria
     */
    List<Task> searchTasks(TaskSearchCriteria criteria);
    
    /**
     * Find tasks that have a specific tag
     * @param tagId the tag ID
     * @return list of tasks with the specified tag
     */
    List<Task> findTasksByTag(Long tagId);
    
    /**
     * Check if a task can be assigned to a user (business rule R3)
     * @param taskId the task ID
     * @param userId the user ID
     * @return true if task can be assigned to user, false otherwise
     */
    boolean canAssignTask(Long taskId, Long userId);
    
    /**
     * Find tasks due within a specific number of days
     * @param days the number of days from now
     * @return list of tasks due within the specified days
     */
    List<Task> findTasksDueWithinDays(int days);
    
    /**
     * Find root tasks (tasks without parent) in a project
     * @param projectId the project ID
     * @return list of root tasks in the project
     */
    List<Task> findRootTasks(Long projectId);
}

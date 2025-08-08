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
     * Find overdue tasks (due date before now and not completed)
     * @return list of overdue tasks
     */
    List<Task> findOverdueTasks();
    
    /**
     * Find tasks with complex filters using native query
     * @param assigneeId the assignee ID (optional)
     * @param status the task status (optional)
     * @param priority the task priority (optional)
     * @param dueBefore tasks due before this date (optional)
     * @return list of tasks matching the filters
     */
    List<Task> findTasksWithComplexFilters(Long assigneeId, TaskStatus status, TaskPriority priority, LocalDateTime dueBefore);
    
    /**
     * Get task statistics by project using native query
     * @param projectId the project ID
     * @return list of arrays containing status, count, and average completion time
     */
    List<Object[]> getTaskStatisticsByProject(Long projectId);
    
    /**
     * Get popular tags with usage count
     * @param limit maximum number of tags to return
     * @return list of arrays containing tag name and usage count
     */
    List<Object[]> getPopularTags(int limit);
    
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
    
    /**
     * Find tasks in a specific project assigned to a user with given status
     * Native query for performance optimization
     * @param userId the user ID
     * @param status the task status
     * @param projectId the project ID
     * @return list of tasks matching criteria
     */
    List<Task> findUserTasksInProject(Long userId, String status, Long projectId);
    
    /**
     * Find tasks with many changes (indicating problematic tasks)
     * Uses native query with JOIN and GROUP BY for performance
     * @param projectId the project ID
     * @param minChanges minimum number of changes to be considered "many"
     * @return list of objects containing task and change count
     */
    List<Object[]> findTasksWithManyChanges(Long projectId, Integer minChanges);
    
    /**
     * Find tasks by priority with project information
     * Native query for optimized data retrieval
     * @param priority the task priority
     * @return list of tasks with priority
     */
    List<Task> findTasksByPriorityNative(String priority);
    
    /**
     * Find tasks completed within date range
     * Native query for date-based filtering
     * @param startDate start of date range
     * @param endDate end of date range
     * @return list of completed tasks in date range
     */
    List<Task> findCompletedTasksInDateRange(LocalDateTime startDate, LocalDateTime endDate);
}

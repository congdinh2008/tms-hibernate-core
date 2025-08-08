package com.congdinh2008.tms.services;

import com.congdinh2008.tms.dto.response.ProjectStatistics;
import com.congdinh2008.tms.dto.response.UserProductivity;
import com.congdinh2008.tms.entities.Tag;
import com.congdinh2008.tms.entities.Task;
import com.congdinh2008.tms.entities.TaskHistory;
import com.congdinh2008.tms.enums.TaskStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Service interface for generating various reports and analytics
 */
public interface ReportService {
    
    /**
     * Get overdue tasks with pagination
     * 
     * @param page page number (0-based)
     * @param size page size
     * @return list of overdue tasks
     */
    List<Task> getOverdueTasks(int page, int size);
    
    /**
     * Get task change history for a specific task
     * 
     * @param taskId the task ID
     * @return list of task history entries
     */
    List<TaskHistory> getTaskChangeHistory(Long taskId);
    
    /**
     * Get number of completed tasks by user in specified days
     * Uses stored procedure for performance
     * 
     * @param userId the user ID
     * @param days number of days to look back
     * @return number of completed tasks
     */
    Long getUserCompletedTasksCount(Long userId, Integer days);
    
    /**
     * Get comprehensive project summary with statistics
     * Uses stored procedure for complex calculations
     * 
     * @param projectId the project ID
     * @return project statistics
     */
    ProjectStatistics getProjectSummary(Long projectId);
    
    /**
     * Get team productivity report for a project within date range
     * 
     * @param projectId the project ID
     * @param startDate start date of analysis
     * @param endDate end date of analysis
     * @return list of user productivity metrics
     */
    List<UserProductivity> getTeamProductivityReport(Long projectId, LocalDate startDate, LocalDate endDate);
    
    /**
     * Get tasks due within specified number of days
     * 
     * @param daysAhead number of days ahead to check
     * @return list of tasks due soon
     */
    List<Task> getTasksDueSoon(Integer daysAhead);
    
    /**
     * Get task distribution by status for a project
     * 
     * @param projectId the project ID
     * @return map of task status to count
     */
    Map<TaskStatus, Long> getTaskDistributionByStatus(Long projectId);
    
    /**
     * Get most used tags with usage count
     * 
     * @param limit maximum number of tags to return
     * @return list of most popular tags
     */
    List<Tag> getMostUsedTags(Integer limit);
    
    /**
     * Get tasks with many changes (indicating problematic tasks)
     * 
     * @param projectId the project ID
     * @param minChanges minimum number of changes to be considered "many"
     * @return list of tasks with many changes
     */
    List<Task> getTasksWithManyChanges(Long projectId, Integer minChanges);
    
    /**
     * Get weekly productivity report for all users
     * 
     * @param weekStart start of the week
     * @return list of user productivity for the week
     */
    List<UserProductivity> getWeeklyProductivityReport(LocalDate weekStart);
    
    /**
     * Get project health summary (combination of multiple metrics)
     * 
     * @param projectId the project ID
     * @return detailed project health report
     */
    Map<String, Object> getProjectHealthSummary(Long projectId);
    
    /**
     * Get user workload analysis
     * 
     * @param userId the user ID
     * @param startDate analysis start date
     * @param endDate analysis end date
     * @return user workload metrics
     */
    Map<String, Object> getUserWorkloadAnalysis(Long userId, LocalDate startDate, LocalDate endDate);
}

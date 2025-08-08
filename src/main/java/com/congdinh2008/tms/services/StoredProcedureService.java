package com.congdinh2008.tms.services;

import com.congdinh2008.tms.dto.response.ProjectStatistics;
import com.congdinh2008.tms.dto.response.UserProductivity;

import java.time.LocalDate;
import java.util.List;

/**
 * Service interface for executing stored procedures and complex database operations
 */
public interface StoredProcedureService {
    
    /**
     * Count completed tasks by user within specified number of days
     * Uses stored procedure: sp_count_completed_tasks_by_user
     * 
     * @param userId the user ID
     * @param numberOfDays number of days to look back from current date
     * @return number of tasks completed by user in the specified time period
     */
    Long countCompletedTasksByUser(Long userId, Integer numberOfDays);
    
    /**
     * Get comprehensive project statistics
     * Uses stored procedure: sp_project_statistics
     * 
     * @param projectId the project ID
     * @return project statistics including completion rate, task counts
     */
    ProjectStatistics getProjectStatistics(Long projectId);
    
    /**
     * Get user productivity report for a date range
     * 
     * @param startDate start date of the report period
     * @param endDate end date of the report period
     * @return list of user productivity metrics
     */
    List<UserProductivity> getUserProductivityReport(LocalDate startDate, LocalDate endDate);
    
    /**
     * Cleanup old task history records
     * Removes task history older than specified days
     * 
     * @param daysToKeep number of days to keep (older records will be deleted)
     * @return number of records deleted
     */
    Integer cleanupOldHistory(Integer daysToKeep);
    
    /**
     * Get productivity statistics for users in a specific project
     * 
     * @param projectId the project ID
     * @param startDate start date of the analysis period
     * @param endDate end date of the analysis period
     * @return list of user productivity in the project
     */
    List<UserProductivity> getProjectUserProductivity(Long projectId, LocalDate startDate, LocalDate endDate);
    
    /**
     * Execute database maintenance operations
     * Includes cleanup, statistics updates, and performance optimizations
     * 
     * @return summary of maintenance operations performed
     */
    String executeMaintenanceOperations();
}

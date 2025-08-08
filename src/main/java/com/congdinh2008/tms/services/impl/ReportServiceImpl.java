package com.congdinh2008.tms.services.impl;

import com.congdinh2008.tms.dto.response.ProjectStatistics;
import com.congdinh2008.tms.dto.response.UserProductivity;
import com.congdinh2008.tms.entities.Tag;
import com.congdinh2008.tms.entities.Task;
import com.congdinh2008.tms.entities.TaskHistory;
import com.congdinh2008.tms.enums.TaskStatus;
import com.congdinh2008.tms.repositories.TagRepository;
import com.congdinh2008.tms.repositories.TaskHistoryRepository;
import com.congdinh2008.tms.repositories.TaskRepository;
import com.congdinh2008.tms.services.ReportService;
import com.congdinh2008.tms.services.StoredProcedureService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementation of ReportService for generating various reports and analytics
 */
@Service
@Transactional(readOnly = true)
public class ReportServiceImpl implements ReportService {
    
    private static final Logger logger = LoggerFactory.getLogger(ReportServiceImpl.class);
    
    private final TaskRepository taskRepository;
    private final TaskHistoryRepository taskHistoryRepository;
    private final TagRepository tagRepository;
    private final StoredProcedureService storedProcedureService;
    
    public ReportServiceImpl(TaskRepository taskRepository, 
                           TaskHistoryRepository taskHistoryRepository,
                           TagRepository tagRepository,
                           StoredProcedureService storedProcedureService) {
        this.taskRepository = taskRepository;
        this.taskHistoryRepository = taskHistoryRepository;
        this.tagRepository = tagRepository;
        this.storedProcedureService = storedProcedureService;
    }
    
    @Override
    public List<Task> getOverdueTasks(int page, int size) {
        logger.info("Getting overdue tasks - page: {}, size: {}", page, size);
        
        List<Task> overdueTasks = taskRepository.findOverdueTasks();
        
        // Manual pagination since we're not using Spring Data
        int start = page * size;
        int end = Math.min(start + size, overdueTasks.size());
        
        if (start >= overdueTasks.size()) {
            return new ArrayList<>();
        }
        
        List<Task> pagedResults = overdueTasks.subList(start, end);
        logger.info("Found {} overdue tasks (page {} of {})", pagedResults.size(), page, 
                   (overdueTasks.size() + size - 1) / size);
        
        return pagedResults;
    }
    
    @Override
    public List<TaskHistory> getTaskChangeHistory(Long taskId) {
        logger.info("Getting change history for task {}", taskId);
        
        List<TaskHistory> history = taskHistoryRepository.findByTaskIdOrderByTimestampDesc(taskId);
        
        logger.info("Found {} history entries for task {}", history.size(), taskId);
        return history;
    }
    
    @Override
    public Long getUserCompletedTasksCount(Long userId, Integer days) {
        logger.info("Getting completed tasks count for user {} in last {} days", userId, days);
        
        return storedProcedureService.countCompletedTasksByUser(userId, days);
    }
    
    @Override
    public ProjectStatistics getProjectSummary(Long projectId) {
        logger.info("Getting project summary for project {}", projectId);
        
        return storedProcedureService.getProjectStatistics(projectId);
    }
    
    @Override
    public List<UserProductivity> getTeamProductivityReport(Long projectId, LocalDate startDate, LocalDate endDate) {
        logger.info("Generating team productivity report for project {} from {} to {}", 
                   projectId, startDate, endDate);
        
        return storedProcedureService.getProjectUserProductivity(projectId, startDate, endDate);
    }
    
    @Override
    public List<Task> getTasksDueSoon(Integer daysAhead) {
        logger.info("Getting tasks due within {} days", daysAhead);
        
        List<Task> tasksDueSoon = taskRepository.findTasksDueWithinDays(daysAhead);
        
        logger.info("Found {} tasks due within {} days", tasksDueSoon.size(), daysAhead);
        return tasksDueSoon;
    }
    
    @Override
    public Map<TaskStatus, Long> getTaskDistributionByStatus(Long projectId) {
        logger.info("Getting task distribution by status for project {}", projectId);
        
        List<Task> projectTasks = taskRepository.findByProject(projectId);
        
        Map<TaskStatus, Long> distribution = projectTasks.stream()
            .collect(Collectors.groupingBy(
                Task::getStatus,
                Collectors.counting()
            ));
        
        // Ensure all statuses are represented (even with 0 count)
        for (TaskStatus status : TaskStatus.values()) {
            distribution.putIfAbsent(status, 0L);
        }
        
        logger.info("Task distribution for project {}: {}", projectId, distribution);
        return distribution;
    }
    
    @Override
    public List<Tag> getMostUsedTags(Integer limit) {
        logger.info("Getting {} most used tags", limit);
        
        // This would require a complex query; for now, return all tags
        // In a real implementation, we'd use a native query to count tag usage
        List<Tag> allTags = tagRepository.findAll(0, limit, "name", "ASC");
        
        logger.info("Returning {} tags", allTags.size());
        return allTags;
    }
    
    @Override
    public List<Task> getTasksWithManyChanges(Long projectId, Integer minChanges) {
        logger.info("Getting tasks with many changes for project {} (min changes: {})", 
                   projectId, minChanges);
        
        List<Task> projectTasks = taskRepository.findByProject(projectId);
        List<Task> tasksWithManyChanges = new ArrayList<>();
        
        for (Task task : projectTasks) {
            List<TaskHistory> history = taskHistoryRepository.findByTaskIdOrderByTimestampDesc(task.getId());
            if (history.size() >= minChanges) {
                tasksWithManyChanges.add(task);
            }
        }
        
        logger.info("Found {} tasks with {} or more changes", tasksWithManyChanges.size(), minChanges);
        return tasksWithManyChanges;
    }
    
    @Override
    public List<UserProductivity> getWeeklyProductivityReport(LocalDate weekStart) {
        logger.info("Generating weekly productivity report starting {}", weekStart);
        
        LocalDate weekEnd = weekStart.plusDays(6);
        return storedProcedureService.getUserProductivityReport(weekStart, weekEnd);
    }
    
    @Override
    public Map<String, Object> getProjectHealthSummary(Long projectId) {
        logger.info("Generating project health summary for project {}", projectId);
        
        Map<String, Object> healthSummary = new HashMap<>();
        
        // Get project statistics
        ProjectStatistics stats = storedProcedureService.getProjectStatistics(projectId);
        healthSummary.put("statistics", stats);
        
        // Get task distribution
        Map<TaskStatus, Long> distribution = getTaskDistributionByStatus(projectId);
        healthSummary.put("taskDistribution", distribution);
        
        // Get overdue tasks count
        List<Task> overdueTasks = getOverdueTasks(0, Integer.MAX_VALUE);
        long projectOverdueTasks = overdueTasks.stream()
            .filter(task -> task.getProject().getId().equals(projectId))
            .count();
        healthSummary.put("overdueTasksCount", projectOverdueTasks);
        
        // Calculate health score (0-100)
        double healthScore = calculateProjectHealthScore(stats, projectOverdueTasks);
        healthSummary.put("healthScore", healthScore);
        
        // Health status
        String healthStatus = getHealthStatus(healthScore);
        healthSummary.put("healthStatus", healthStatus);
        
        logger.info("Project {} health score: {} ({})", projectId, healthScore, healthStatus);
        return healthSummary;
    }
    
    @Override
    public Map<String, Object> getUserWorkloadAnalysis(Long userId, LocalDate startDate, LocalDate endDate) {
        logger.info("Analyzing workload for user {} from {} to {}", userId, startDate, endDate);
        
        Map<String, Object> workloadAnalysis = new HashMap<>();
        
        // Get assigned tasks
        List<Task> assignedTasks = taskRepository.findByAssignee(userId);
        workloadAnalysis.put("totalAssignedTasks", assignedTasks.size());
        
        // Get completed tasks in period
        Long completedInPeriod = storedProcedureService.countCompletedTasksByUser(
            userId, (int) ChronoUnit.DAYS.between(startDate, endDate));
        workloadAnalysis.put("completedTasksInPeriod", completedInPeriod);
        
        // Calculate workload intensity
        long activeTasks = assignedTasks.stream()
            .filter(task -> task.getStatus() != TaskStatus.DONE)
            .count();
        workloadAnalysis.put("activeTasks", activeTasks);
        
        // Workload status
        String workloadStatus = getWorkloadStatus(activeTasks);
        workloadAnalysis.put("workloadStatus", workloadStatus);
        
        // Average completion time (simplified calculation)
        long avgCompletionDays = completedInPeriod > 0 ? 
            ChronoUnit.DAYS.between(startDate, endDate) / completedInPeriod : 0;
        workloadAnalysis.put("averageCompletionDays", avgCompletionDays);
        
        logger.info("User {} workload: {} active tasks, {} completed in period", 
                   userId, activeTasks, completedInPeriod);
        return workloadAnalysis;
    }
    
    // Helper methods
    
    private double calculateProjectHealthScore(ProjectStatistics stats, long overdueTasksCount) {
        if (stats.getTotalTasks() == 0) {
            return 100.0; // No tasks = perfect health
        }
        
        double completionRatio = stats.getCompletionRate().doubleValue() / 100.0;
        double overdueRatio = (double) overdueTasksCount / stats.getTotalTasks();
        
        // Health score: completion ratio (70%) - overdue penalty (30%)
        double healthScore = (completionRatio * 70) + ((1 - overdueRatio) * 30);
        
        return Math.max(0, Math.min(100, healthScore));
    }
    
    private String getHealthStatus(double healthScore) {
        if (healthScore >= 80) return "Excellent";
        if (healthScore >= 60) return "Good";
        if (healthScore >= 40) return "Fair";
        if (healthScore >= 20) return "Poor";
        return "Critical";
    }
    
    private String getWorkloadStatus(long activeTasks) {
        if (activeTasks <= 3) return "Light";
        if (activeTasks <= 6) return "Moderate";
        if (activeTasks <= 10) return "Heavy";
        return "Overloaded";
    }
}

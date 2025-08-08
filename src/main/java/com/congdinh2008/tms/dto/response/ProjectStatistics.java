package com.congdinh2008.tms.dto.response;

import java.math.BigDecimal;

/**
 * DTO for project statistics returned by stored procedures
 */
public class ProjectStatistics {
    
    /**
     * Total number of tasks in the project
     */
    private Long totalTasks;
    
    /**
     * Number of completed tasks (status = DONE)
     */
    private Long completedTasks;
    
    /**
     * Number of tasks in progress (status = IN_PROGRESS)  
     */
    private Long inProgressTasks;
    
    /**
     * Number of overdue tasks (due date < now and status != DONE)
     */
    private Long overdueTasks;
    
    /**
     * Completion rate as percentage (0.00 to 100.00)
     */
    private BigDecimal completionRate;
    
    // Constructors
    public ProjectStatistics() {}
    
    public ProjectStatistics(Long totalTasks, Long completedTasks, Long inProgressTasks, 
                           Long overdueTasks, BigDecimal completionRate) {
        this.totalTasks = totalTasks;
        this.completedTasks = completedTasks;
        this.inProgressTasks = inProgressTasks;
        this.overdueTasks = overdueTasks;
        this.completionRate = completionRate;
    }
    
    // Getters and Setters
    public Long getTotalTasks() { return totalTasks; }
    public void setTotalTasks(Long totalTasks) { this.totalTasks = totalTasks; }
    
    public Long getCompletedTasks() { return completedTasks; }
    public void setCompletedTasks(Long completedTasks) { this.completedTasks = completedTasks; }
    
    public Long getInProgressTasks() { return inProgressTasks; }
    public void setInProgressTasks(Long inProgressTasks) { this.inProgressTasks = inProgressTasks; }
    
    public Long getOverdueTasks() { return overdueTasks; }
    public void setOverdueTasks(Long overdueTasks) { this.overdueTasks = overdueTasks; }
    
    public BigDecimal getCompletionRate() { return completionRate; }
    public void setCompletionRate(BigDecimal completionRate) { this.completionRate = completionRate; }
    
    /**
     * Calculate remaining tasks (total - completed)
     */
    public Long getRemainingTasks() {
        if (totalTasks == null || completedTasks == null) {
            return 0L;
        }
        return totalTasks - completedTasks;
    }
    
    /**
     * Check if project is completed (all tasks done)
     */
    public boolean isCompleted() {
        return totalTasks != null && completedTasks != null && 
               totalTasks.equals(completedTasks) && totalTasks > 0;
    }
    
    /**
     * Check if project has overdue tasks
     */
    public boolean hasOverdueTasks() {
        return overdueTasks != null && overdueTasks > 0;
    }
}

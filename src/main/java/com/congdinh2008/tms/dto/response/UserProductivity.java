package com.congdinh2008.tms.dto.response;

import java.math.BigDecimal;
import java.time.Duration;

/**
 * DTO for user productivity metrics returned by stored procedures
 */
public class UserProductivity {
    
    /**
     * User ID
     */
    private Long userId;
    
    /**
     * User name
     */
    private String userName;
    
    /**
     * Number of tasks completed by user
     */
    private Long completedTasks;
    
    /**
     * Total number of tasks assigned to user
     */
    private Long totalAssignedTasks;
    
    /**
     * Productivity rate as percentage (0.00 to 100.00)
     */
    private BigDecimal productivityRate;
    
    /**
     * Average time to complete tasks
     */
    private Duration averageCompletionTime;
    
    // Constructors
    public UserProductivity() {}
    
    public UserProductivity(Long userId, String userName, Long completedTasks, 
                          Long totalAssignedTasks, BigDecimal productivityRate,
                          Duration averageCompletionTime) {
        this.userId = userId;
        this.userName = userName;
        this.completedTasks = completedTasks;
        this.totalAssignedTasks = totalAssignedTasks;
        this.productivityRate = productivityRate;
        this.averageCompletionTime = averageCompletionTime;
    }
    
    // Getters and Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    
    public Long getCompletedTasks() { return completedTasks; }
    public void setCompletedTasks(Long completedTasks) { this.completedTasks = completedTasks; }
    
    public Long getTotalAssignedTasks() { return totalAssignedTasks; }
    public void setTotalAssignedTasks(Long totalAssignedTasks) { this.totalAssignedTasks = totalAssignedTasks; }
    
    public BigDecimal getProductivityRate() { return productivityRate; }
    public void setProductivityRate(BigDecimal productivityRate) { this.productivityRate = productivityRate; }
    
    public Duration getAverageCompletionTime() { return averageCompletionTime; }
    public void setAverageCompletionTime(Duration averageCompletionTime) { this.averageCompletionTime = averageCompletionTime; }
    
    // Utility methods
    
    /**
     * Get remaining tasks (total - completed)
     */
    public Long getRemainingTasks() {
        if (totalAssignedTasks == null || completedTasks == null) {
            return 0L;
        }
        return totalAssignedTasks - completedTasks;
    }
    
    /**
     * Check if user has high productivity (>= 80%)
     */
    public boolean isHighPerformer() {
        return productivityRate != null && 
               productivityRate.compareTo(new BigDecimal("80.00")) >= 0;
    }
    
    /**
     * Check if user has completed all assigned tasks
     */
    public boolean hasCompletedAllTasks() {
        return totalAssignedTasks != null && completedTasks != null &&
               totalAssignedTasks.equals(completedTasks) && totalAssignedTasks > 0;
    }
}

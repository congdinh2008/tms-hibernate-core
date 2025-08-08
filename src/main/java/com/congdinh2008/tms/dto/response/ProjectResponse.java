package com.congdinh2008.tms.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * Response DTO for project data
 * 
 * @author Cong Dinh
 * @version 1.0.0
 * @since 1.0.0
 */
public class ProjectResponse {
    
    private Long id;
    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
    private Set<UserResponse> members;
    private Integer taskCount;
    private Integer completedTaskCount;
    
    // Constructors
    public ProjectResponse() {}
    
    public ProjectResponse(Long id, String name, String description, LocalDate startDate) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.startDate = startDate;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public LocalDate getStartDate() {
        return startDate;
    }
    
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    
    public LocalDate getEndDate() {
        return endDate;
    }
    
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public String getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
    
    public String getUpdatedBy() {
        return updatedBy;
    }
    
    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }
    
    public Set<UserResponse> getMembers() {
        return members;
    }
    
    public void setMembers(Set<UserResponse> members) {
        this.members = members;
    }
    
    public Integer getTaskCount() {
        return taskCount;
    }
    
    public void setTaskCount(Integer taskCount) {
        this.taskCount = taskCount;
    }
    
    public Integer getCompletedTaskCount() {
        return completedTaskCount;
    }
    
    public void setCompletedTaskCount(Integer completedTaskCount) {
        this.completedTaskCount = completedTaskCount;
    }
}

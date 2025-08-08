package com.congdinh2008.tms.dto.response;

import com.congdinh2008.tms.enums.TaskStatus;
import com.congdinh2008.tms.enums.TaskPriority;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * Response DTO for task data
 * 
 * @author Cong Dinh
 * @version 1.0.0
 * @since 1.0.0
 */
public class TaskResponse {
    
    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private LocalDate dueDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
    private ProjectResponse project;
    private UserResponse assignee;
    private Set<TagResponse> tags;
    private TaskResponse parentTask;
    private Set<TaskResponse> subtasks;
    
    // Constructors
    public TaskResponse() {}
    
    public TaskResponse(Long id, String title, TaskStatus status, TaskPriority priority) {
        this.id = id;
        this.title = title;
        this.status = status;
        this.priority = priority;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public TaskStatus getStatus() {
        return status;
    }
    
    public void setStatus(TaskStatus status) {
        this.status = status;
    }
    
    public TaskPriority getPriority() {
        return priority;
    }
    
    public void setPriority(TaskPriority priority) {
        this.priority = priority;
    }
    
    public LocalDate getDueDate() {
        return dueDate;
    }
    
    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
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
    
    public ProjectResponse getProject() {
        return project;
    }
    
    public void setProject(ProjectResponse project) {
        this.project = project;
    }
    
    public UserResponse getAssignee() {
        return assignee;
    }
    
    public void setAssignee(UserResponse assignee) {
        this.assignee = assignee;
    }
    
    public Set<TagResponse> getTags() {
        return tags;
    }
    
    public void setTags(Set<TagResponse> tags) {
        this.tags = tags;
    }
    
    public TaskResponse getParentTask() {
        return parentTask;
    }
    
    public void setParentTask(TaskResponse parentTask) {
        this.parentTask = parentTask;
    }
    
    public Set<TaskResponse> getSubtasks() {
        return subtasks;
    }
    
    public void setSubtasks(Set<TaskResponse> subtasks) {
        this.subtasks = subtasks;
    }
}

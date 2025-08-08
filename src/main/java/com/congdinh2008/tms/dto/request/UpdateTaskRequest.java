package com.congdinh2008.tms.dto.request;

import com.congdinh2008.tms.enums.TaskStatus;
import com.congdinh2008.tms.enums.TaskPriority;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.Set;

/**
 * Request DTO for updating an existing task
 * 
 * @author Cong Dinh
 * @version 1.0.0
 * @since 1.0.0
 */
public class UpdateTaskRequest {
    
    @Size(min = 1, max = 255, message = "Task title must be between 1 and 255 characters")
    private String title;
    
    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    private String description;
    
    private Long assigneeId;
    
    private TaskStatus status;
    
    private TaskPriority priority;
    
    private LocalDate dueDate;
    
    private Set<Long> tagIds;
    
    private Long parentTaskId;
    
    // Constructors
    public UpdateTaskRequest() {}
    
    public UpdateTaskRequest(String title, String description) {
        this.title = title;
        this.description = description;
    }
    
    // Getters and Setters
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
    
    public Long getAssigneeId() {
        return assigneeId;
    }
    
    public void setAssigneeId(Long assigneeId) {
        this.assigneeId = assigneeId;
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
    
    public Set<Long> getTagIds() {
        return tagIds;
    }
    
    public void setTagIds(Set<Long> tagIds) {
        this.tagIds = tagIds;
    }
    
    public Long getParentTaskId() {
        return parentTaskId;
    }
    
    public void setParentTaskId(Long parentTaskId) {
        this.parentTaskId = parentTaskId;
    }
}

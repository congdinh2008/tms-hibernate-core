package com.congdinh2008.tms.dto.search;

import com.congdinh2008.tms.enums.TaskPriority;
import com.congdinh2008.tms.enums.TaskStatus;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Search criteria for Task queries
 * Used to build complex search queries with multiple filters
 */
public class TaskSearchCriteria {
    
    private String keyword; // Search in title and description
    private TaskStatus status;
    private TaskPriority priority;
    private Long projectId;
    private Long assigneeId;
    private Long parentTaskId;
    private List<Long> tagIds;
    private LocalDateTime dueDateFrom;
    private LocalDateTime dueDateTo;
    private LocalDateTime createdAtFrom;
    private LocalDateTime createdAtTo;
    private Boolean hasSubTasks; // null = don't filter, true = has subtasks, false = no subtasks
    private Boolean isOverdue; // null = don't filter, true = overdue only, false = not overdue only

    // Default constructor
    public TaskSearchCriteria() {}

    // Builder pattern
    public static TaskSearchCriteriaBuilder builder() {
        return new TaskSearchCriteriaBuilder();
    }

    // Getters and Setters
    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }

    public TaskStatus getStatus() { return status; }
    public void setStatus(TaskStatus status) { this.status = status; }

    public TaskPriority getPriority() { return priority; }
    public void setPriority(TaskPriority priority) { this.priority = priority; }

    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }

    public Long getAssigneeId() { return assigneeId; }
    public void setAssigneeId(Long assigneeId) { this.assigneeId = assigneeId; }

    public Long getParentTaskId() { return parentTaskId; }
    public void setParentTaskId(Long parentTaskId) { this.parentTaskId = parentTaskId; }

    public List<Long> getTagIds() { return tagIds; }
    public void setTagIds(List<Long> tagIds) { this.tagIds = tagIds; }

    public LocalDateTime getDueDateFrom() { return dueDateFrom; }
    public void setDueDateFrom(LocalDateTime dueDateFrom) { this.dueDateFrom = dueDateFrom; }

    public LocalDateTime getDueDateTo() { return dueDateTo; }
    public void setDueDateTo(LocalDateTime dueDateTo) { this.dueDateTo = dueDateTo; }

    public LocalDateTime getCreatedAtFrom() { return createdAtFrom; }
    public void setCreatedAtFrom(LocalDateTime createdAtFrom) { this.createdAtFrom = createdAtFrom; }

    public LocalDateTime getCreatedAtTo() { return createdAtTo; }
    public void setCreatedAtTo(LocalDateTime createdAtTo) { this.createdAtTo = createdAtTo; }

    public Boolean getHasSubTasks() { return hasSubTasks; }
    public void setHasSubTasks(Boolean hasSubTasks) { this.hasSubTasks = hasSubTasks; }

    public Boolean getIsOverdue() { return isOverdue; }
    public void setIsOverdue(Boolean isOverdue) { this.isOverdue = isOverdue; }

    // Builder class
    public static class TaskSearchCriteriaBuilder {
        private TaskSearchCriteria criteria = new TaskSearchCriteria();

        public TaskSearchCriteriaBuilder keyword(String keyword) {
            criteria.setKeyword(keyword);
            return this;
        }

        public TaskSearchCriteriaBuilder status(TaskStatus status) {
            criteria.setStatus(status);
            return this;
        }

        public TaskSearchCriteriaBuilder priority(TaskPriority priority) {
            criteria.setPriority(priority);
            return this;
        }

        public TaskSearchCriteriaBuilder projectId(Long projectId) {
            criteria.setProjectId(projectId);
            return this;
        }

        public TaskSearchCriteriaBuilder assigneeId(Long assigneeId) {
            criteria.setAssigneeId(assigneeId);
            return this;
        }

        public TaskSearchCriteriaBuilder parentTaskId(Long parentTaskId) {
            criteria.setParentTaskId(parentTaskId);
            return this;
        }

        public TaskSearchCriteriaBuilder tagIds(List<Long> tagIds) {
            criteria.setTagIds(tagIds);
            return this;
        }

        public TaskSearchCriteriaBuilder dueDateFrom(LocalDateTime dueDateFrom) {
            criteria.setDueDateFrom(dueDateFrom);
            return this;
        }

        public TaskSearchCriteriaBuilder dueDateTo(LocalDateTime dueDateTo) {
            criteria.setDueDateTo(dueDateTo);
            return this;
        }

        public TaskSearchCriteriaBuilder createdAtFrom(LocalDateTime createdAtFrom) {
            criteria.setCreatedAtFrom(createdAtFrom);
            return this;
        }

        public TaskSearchCriteriaBuilder createdAtTo(LocalDateTime createdAtTo) {
            criteria.setCreatedAtTo(createdAtTo);
            return this;
        }

        public TaskSearchCriteriaBuilder hasSubTasks(Boolean hasSubTasks) {
            criteria.setHasSubTasks(hasSubTasks);
            return this;
        }

        public TaskSearchCriteriaBuilder isOverdue(Boolean isOverdue) {
            criteria.setIsOverdue(isOverdue);
            return this;
        }

        public TaskSearchCriteria build() {
            return criteria;
        }
    }
}

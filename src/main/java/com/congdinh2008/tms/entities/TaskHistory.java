package com.congdinh2008.tms.entities;

import com.congdinh2008.tms.enums.FieldType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * TaskHistory entity for tracking task changes
 * 
 * @author Cong Dinh
 * @version 1.0.0
 * @since 1.0.0
 */
@Entity
@Table(name = "task_histories")
public class TaskHistory extends BaseEntity {

    @NotNull(message = "Change date is required")
    @Column(name = "change_date", nullable = false)
    private LocalDateTime changeDate;

    @NotNull(message = "Field changed is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "field_changed", nullable = false, length = 50)
    private FieldType fieldChanged;

    @Column(name = "old_value", columnDefinition = "TEXT")
    private String oldValue;

    @Column(name = "new_value", columnDefinition = "TEXT")
    private String newValue;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "changed_by", nullable = false)
    private User changedBy;

    // Constructors
    public TaskHistory() {
        super();
        this.changeDate = LocalDateTime.now();
    }

    public TaskHistory(Task task, User changedBy, FieldType fieldChanged, String oldValue, String newValue) {
        this();
        this.task = task;
        this.changedBy = changedBy;
        this.fieldChanged = fieldChanged;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    // Getters and Setters
    public LocalDateTime getChangeDate() {
        return changeDate;
    }

    public void setChangeDate(LocalDateTime changeDate) {
        this.changeDate = changeDate;
    }

    public FieldType getFieldChanged() {
        return fieldChanged;
    }

    public void setFieldChanged(FieldType fieldChanged) {
        this.fieldChanged = fieldChanged;
    }

    public String getOldValue() {
        return oldValue;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public User getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(User changedBy) {
        this.changedBy = changedBy;
    }

    // Business methods
    public String getChangeDescription() {
        return String.format("%s changed from '%s' to '%s'", 
                fieldChanged.getDisplayName(), 
                oldValue != null ? oldValue : "null", 
                newValue != null ? newValue : "null");
    }

    // equals, hashCode, toString
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        TaskHistory that = (TaskHistory) o;
        return Objects.equals(changeDate, that.changeDate) && 
               fieldChanged == that.fieldChanged && 
               Objects.equals(task, that.task);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), changeDate, fieldChanged, task);
    }

    @Override
    public String toString() {
        return String.format("TaskHistory{id=%d, taskId=%d, fieldChanged=%s, changeDate=%s, changedBy=%s, version=%d}",
                getId(), 
                task != null ? task.getId() : null, 
                fieldChanged, 
                changeDate, 
                changedBy != null ? changedBy.getName() : null, 
                getVersion());
    }
}

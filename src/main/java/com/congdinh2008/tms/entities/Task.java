package com.congdinh2008.tms.entities;

import com.congdinh2008.tms.enums.TaskPriority;
import com.congdinh2008.tms.enums.TaskStatus;
import com.congdinh2008.tms.validators.ValidAssignee;
import com.congdinh2008.tms.validators.ValidDueDate;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Task entity representing tasks in the system
 * 
 * @author Cong Dinh
 * @version 1.0.0
 * @since 1.0.0
 */
@Entity
@Table(name = "tasks")
@ValidDueDate
@ValidAssignee
public class Task extends BaseEntity {

    @NotBlank(message = "Task title is required")
    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "Task status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private TaskStatus status;

    @NotNull(message = "Task priority is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false, length = 10)
    private TaskPriority priority;

    @NotNull(message = "Due date is required")
    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignee_id")
    private User assignee;

    // Self-referencing relationship for parent-child tasks
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_task_id")
    private Task parentTask;

    @OneToMany(mappedBy = "parentTask", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> subTasks = new ArrayList<>();

    // Many-to-many relationship with tags
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "task_tags",
        joinColumns = @JoinColumn(name = "task_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private List<Tag> tags = new ArrayList<>();

    // Task history
    @OneToMany(mappedBy = "task", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TaskHistory> taskHistories = new ArrayList<>();

    // Constructors
    public Task() {
        super();
        this.status = TaskStatus.TODO;
        this.priority = TaskPriority.MEDIUM;
    }

    public Task(String title, String description, TaskStatus status, TaskPriority priority, LocalDate dueDate, Project project) {
        this();
        this.title = title;
        this.description = description;
        this.status = status != null ? status : TaskStatus.TODO;
        this.priority = priority != null ? priority : TaskPriority.MEDIUM;
        this.dueDate = dueDate;
        this.project = project;
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

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public User getAssignee() {
        return assignee;
    }

    public void setAssignee(User assignee) {
        this.assignee = assignee;
    }

    public Task getParentTask() {
        return parentTask;
    }

    public void setParentTask(Task parentTask) {
        this.parentTask = parentTask;
    }

    public List<Task> getSubTasks() {
        return subTasks;
    }

    public void setSubTasks(List<Task> subTasks) {
        this.subTasks = subTasks;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public List<TaskHistory> getTaskHistories() {
        return taskHistories;
    }

    public void setTaskHistories(List<TaskHistory> taskHistories) {
        this.taskHistories = taskHistories;
    }

    // Helper methods for sub-tasks
    public void addSubTask(Task subTask) {
        if (!subTasks.contains(subTask)) {
            subTasks.add(subTask);
            subTask.setParentTask(this);
        }
    }

    public void removeSubTask(Task subTask) {
        if (subTasks.contains(subTask)) {
            subTasks.remove(subTask);
            subTask.setParentTask(null);
        }
    }

    // Helper methods for tags
    public void addTag(Tag tag) {
        if (!tags.contains(tag)) {
            tags.add(tag);
            tag.getTasks().add(this);
        }
    }

    public void removeTag(Tag tag) {
        if (tags.contains(tag)) {
            tags.remove(tag);
            tag.getTasks().remove(this);
        }
    }

    // Helper methods for task history
    public void addTaskHistory(TaskHistory taskHistory) {
        if (!taskHistories.contains(taskHistory)) {
            taskHistories.add(taskHistory);
            taskHistory.setTask(this);
        }
    }

    // Business methods
    public boolean isOverdue() {
        return dueDate != null && dueDate.isBefore(LocalDate.now()) && status != TaskStatus.DONE;
    }

    public boolean isCompleted() {
        return status == TaskStatus.DONE;
    }

    public boolean hasSubTasks() {
        return !subTasks.isEmpty();
    }

    public boolean isSubTask() {
        return parentTask != null;
    }

    public int getSubTaskCount() {
        return subTasks.size();
    }

    public long getCompletedSubTaskCount() {
        return subTasks.stream()
                .filter(Task::isCompleted)
                .count();
    }

    public double getCompletionPercentage() {
        if (subTasks.isEmpty()) {
            return isCompleted() ? 100.0 : 0.0;
        }
        return (double) getCompletedSubTaskCount() / subTasks.size() * 100.0;
    }

    public boolean isAssignedTo(User user) {
        return assignee != null && assignee.equals(user);
    }

    public boolean hasTag(Tag tag) {
        return tags.contains(tag);
    }

    // equals, hashCode, toString
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Task task = (Task) o;
        return Objects.equals(title, task.title) && 
               Objects.equals(project, task.project) && 
               Objects.equals(dueDate, task.dueDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), title, project, dueDate);
    }

    @Override
    public String toString() {
        return String.format("Task{id=%d, title='%s', status=%s, priority=%s, dueDate=%s, " +
                           "projectId=%d, assigneeId=%d, parentTaskId=%d, subTaskCount=%d, tagCount=%d, version=%d}",
                getId(), title, status, priority, dueDate,
                project != null ? project.getId() : null,
                assignee != null ? assignee.getId() : null,
                parentTask != null ? parentTask.getId() : null,
                subTasks.size(), tags.size(), getVersion());
    }
}

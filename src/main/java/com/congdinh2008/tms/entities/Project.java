package com.congdinh2008.tms.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Project entity representing projects in the system
 * 
 * @author Cong Dinh
 * @version 1.0.0
 * @since 1.0.0
 */
@Entity
@Table(name = "projects")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Project extends BaseEntity {

    @NotBlank(message = "Project name is required")
    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "Start date is required")
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    // Relationships
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "project_members",
        joinColumns = @JoinColumn(name = "project_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> members = new ArrayList<>();

    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> tasks = new ArrayList<>();

    // Constructors
    public Project() {
        super();
    }

    public Project(String name, String description, LocalDate startDate) {
        this();
        this.name = name;
        this.description = description;
        this.startDate = startDate;
    }

    // Getters and Setters
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

    public List<User> getMembers() {
        return members;
    }

    public void setMembers(List<User> members) {
        this.members = members;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    // Helper methods
    public void addMember(User user) {
        if (!members.contains(user)) {
            members.add(user);
            user.getProjects().add(this);
        }
    }

    public void removeMember(User user) {
        if (members.contains(user)) {
            members.remove(user);
            user.getProjects().remove(this);
        }
    }

    public void addTask(Task task) {
        if (!tasks.contains(task)) {
            tasks.add(task);
            task.setProject(this);
        }
    }

    public void removeTask(Task task) {
        if (tasks.contains(task)) {
            tasks.remove(task);
            task.setProject(null);
        }
    }

    // Business methods
    public boolean isMember(User user) {
        return members.contains(user);
    }

    public int getTaskCount() {
        return tasks.size();
    }

    public long getCompletedTaskCount() {
        return tasks.stream()
                .filter(task -> task.getStatus() == com.congdinh2008.tms.enums.TaskStatus.DONE)
                .count();
    }

    // equals, hashCode, toString
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Project project = (Project) o;
        return Objects.equals(name, project.name) && Objects.equals(startDate, project.startDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name, startDate);
    }

    @Override
    public String toString() {
        return String.format("Project{id=%d, name='%s', startDate=%s, memberCount=%d, taskCount=%d, version=%d}",
                getId(), name, startDate, members.size(), tasks.size(), getVersion());
    }
}

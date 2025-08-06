package com.congdinh2008.tms.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * User entity representing system users
 * 
 * @author Cong Dinh
 * @version 1.0.0
 * @since 1.0.0
 */
@Entity
@Table(name = "users")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class User extends BaseEntity {

    @NotBlank(message = "Name is required")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email;

    @NotBlank(message = "Password is required")
    @Column(name = "password", nullable = false, length = 255)
    private String password;

    // Relationships
    @ManyToMany(mappedBy = "members", fetch = FetchType.LAZY)
    private List<Project> projects = new ArrayList<>();

    @OneToMany(mappedBy = "assignee", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Task> assignedTasks = new ArrayList<>();

    @OneToMany(mappedBy = "changedBy", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<TaskHistory> taskHistories = new ArrayList<>();

    // Constructors
    public User() {
        super();
    }

    public User(String name, String email, String password) {
        this();
        this.name = name;
        this.email = email;
        this.password = password;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Project> getProjects() {
        return projects;
    }

    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }

    public List<Task> getAssignedTasks() {
        return assignedTasks;
    }

    public void setAssignedTasks(List<Task> assignedTasks) {
        this.assignedTasks = assignedTasks;
    }

    public List<TaskHistory> getTaskHistories() {
        return taskHistories;
    }

    public void setTaskHistories(List<TaskHistory> taskHistories) {
        this.taskHistories = taskHistories;
    }

    // Helper methods
    public void addProject(Project project) {
        if (!projects.contains(project)) {
            projects.add(project);
            project.getMembers().add(this);
        }
    }

    public void removeProject(Project project) {
        if (projects.contains(project)) {
            projects.remove(project);
            project.getMembers().remove(this);
        }
    }

    public void addAssignedTask(Task task) {
        if (!assignedTasks.contains(task)) {
            assignedTasks.add(task);
            task.setAssignee(this);
        }
    }

    public void removeAssignedTask(Task task) {
        if (assignedTasks.contains(task)) {
            assignedTasks.remove(task);
            task.setAssignee(null);
        }
    }

    // equals, hashCode, toString
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        User user = (User) o;
        return Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), email);
    }

    @Override
    public String toString() {
        return String.format("User{id=%d, name='%s', email='%s', version=%d}",
                getId(), name, email, getVersion());
    }
}

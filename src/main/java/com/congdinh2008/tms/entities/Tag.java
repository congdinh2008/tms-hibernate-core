package com.congdinh2008.tms.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Tag entity for task categorization
 * 
 * @author Cong Dinh
 * @version 1.0.0
 * @since 1.0.0
 */
@Entity
@Table(name = "tags")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Tag extends BaseEntity {

    @NotBlank(message = "Tag name is required")
    @Column(name = "name", nullable = false, unique = true, length = 50)
    private String name;

    // Relationships
    @ManyToMany(mappedBy = "tags", fetch = FetchType.LAZY)
    private List<Task> tasks = new ArrayList<>();

    // Constructors
    public Tag() {
        super();
    }

    public Tag(String name) {
        this();
        this.name = name;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    // Helper methods
    public void addTask(Task task) {
        if (!tasks.contains(task)) {
            tasks.add(task);
            task.getTags().add(this);
        }
    }

    public void removeTask(Task task) {
        if (tasks.contains(task)) {
            tasks.remove(task);
            task.getTags().remove(this);
        }
    }

    // Business methods
    public int getTaskCount() {
        return tasks.size();
    }

    // equals, hashCode, toString
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Tag tag = (Tag) o;
        return Objects.equals(name, tag.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name);
    }

    @Override
    public String toString() {
        return String.format("Tag{id=%d, name='%s', taskCount=%d, version=%d}",
                getId(), name, tasks.size(), getVersion());
    }
}

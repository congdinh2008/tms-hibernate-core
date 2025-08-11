package com.congdinh2008.tms.utils;

import com.congdinh2008.tms.entities.*;
import com.congdinh2008.tms.enums.TaskPriority;
import com.congdinh2008.tms.enums.TaskStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Test Data Builder pattern for creating consistent test data
 * Reduces code duplication and improves test maintainability
 */
public class TestDataBuilder {
    
    /**
     * User Builder
     */
    public static class UserBuilder {
        private User user = new User();
        
        public static UserBuilder aUser() {
            return new UserBuilder();
        }
        
        public UserBuilder withName(String name) {
            user.setName(name);
            return this;
        }
        
        public UserBuilder withEmail(String email) {
            user.setEmail(email);
            return this;
        }
        
        public UserBuilder withPassword(String password) {
            user.setPassword(password);
            return this;
        }
        
        public UserBuilder withDefaults() {
            return withName("Test User")
                    .withEmail("test@example.com")
                    .withPassword("password123");
        }
        
        public User build() {
            return user;
        }
    }
    
    /**
     * Project Builder
     */
    public static class ProjectBuilder {
        private Project project = new Project();
        
        public static ProjectBuilder aProject() {
            return new ProjectBuilder();
        }
        
        public ProjectBuilder withName(String name) {
            project.setName(name);
            return this;
        }
        
        public ProjectBuilder withDescription(String description) {
            project.setDescription(description);
            return this;
        }
        
        public ProjectBuilder withStartDate(LocalDate startDate) {
            project.setStartDate(startDate);
            return this;
        }
        
        public ProjectBuilder withDefaults() {
            return withName("Test Project")
                    .withDescription("Test project description")
                    .withStartDate(LocalDate.now());
        }
        
        public Project build() {
            return project;
        }
    }
    
    /**
     * Task Builder
     */
    public static class TaskBuilder {
        private Task task = new Task();
        
        public static TaskBuilder aTask() {
            return new TaskBuilder();
        }
        
        public TaskBuilder withTitle(String title) {
            task.setTitle(title);
            return this;
        }
        
        public TaskBuilder withDescription(String description) {
            task.setDescription(description);
            return this;
        }
        
        public TaskBuilder withDueDate(LocalDate dueDate) {
            task.setDueDate(dueDate);
            return this;
        }
        
        public TaskBuilder withPriority(TaskPriority priority) {
            task.setPriority(priority);
            return this;
        }
        
        public TaskBuilder withStatus(TaskStatus status) {
            task.setStatus(status);
            return this;
        }
        
        public TaskBuilder withProject(Project project) {
            task.setProject(project);
            return this;
        }
        
        public TaskBuilder withAssignee(User assignee) {
            task.setAssignee(assignee);
            return this;
        }
        
        public TaskBuilder withDefaults() {
            return withTitle("Test Task")
                    .withDescription("Test task description")
                    .withDueDate(LocalDate.now().plusDays(7))
                    .withPriority(TaskPriority.MEDIUM)
                    .withStatus(TaskStatus.TODO);
        }
        
        public TaskBuilder asOverdue() {
            return withDueDate(LocalDate.now().minusDays(1))
                    .withStatus(TaskStatus.TODO);
        }
        
        public TaskBuilder asCompleted() {
            return withStatus(TaskStatus.DONE);
        }
        
        public TaskBuilder asInProgress() {
            return withStatus(TaskStatus.IN_PROGRESS);
        }
        
        public Task build() {
            return task;
        }
    }
    
    /**
     * Tag Builder
     */
    public static class TagBuilder {
        private Tag tag = new Tag();
        
        public static TagBuilder aTag() {
            return new TagBuilder();
        }
        
        public TagBuilder withName(String name) {
            tag.setName(name);
            return this;
        }
        
        public TagBuilder withDefaults() {
            return withName("Test Tag");
        }
        
        public Tag build() {
            return tag;
        }
    }
    
    /**
     * TaskHistory Builder
     */
    public static class TaskHistoryBuilder {
        private TaskHistory taskHistory = new TaskHistory();
        
        public static TaskHistoryBuilder aTaskHistory() {
            return new TaskHistoryBuilder();
        }
        
        public TaskHistoryBuilder withTask(Task task) {
            taskHistory.setTask(task);
            return this;
        }
        
        public TaskHistoryBuilder withChangedBy(User user) {
            taskHistory.setChangedBy(user);
            return this;
        }
        
        public TaskHistoryBuilder withChangeDate(LocalDateTime changeDate) {
            taskHistory.setChangeDate(changeDate);
            return this;
        }
        
        public TaskHistoryBuilder withFieldChanged(com.congdinh2008.tms.enums.FieldType fieldType) {
            taskHistory.setFieldChanged(fieldType);
            return this;
        }
        
        public TaskHistoryBuilder withOldValue(String oldValue) {
            taskHistory.setOldValue(oldValue);
            return this;
        }
        
        public TaskHistoryBuilder withNewValue(String newValue) {
            taskHistory.setNewValue(newValue);
            return this;
        }
        
        public TaskHistoryBuilder withDefaults() {
            return withFieldChanged(com.congdinh2008.tms.enums.FieldType.STATUS)
                    .withOldValue("TODO")
                    .withNewValue("IN_PROGRESS")
                    .withChangeDate(LocalDateTime.now());
        }
        
        public TaskHistory build() {
            return taskHistory;
        }
    }
}

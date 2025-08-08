package com.congdinh2008.tms.services.impl;

import com.congdinh2008.tms.dto.request.CreateTaskRequest;
import com.congdinh2008.tms.dto.request.UpdateTaskRequest;
import com.congdinh2008.tms.dto.response.TaskResponse;
import com.congdinh2008.tms.entities.Project;
import com.congdinh2008.tms.entities.Tag;
import com.congdinh2008.tms.entities.Task;
import com.congdinh2008.tms.entities.User;
import com.congdinh2008.tms.enums.TaskStatus;
import com.congdinh2008.tms.exceptions.BusinessRuleViolationException;
import com.congdinh2008.tms.exceptions.CircularReferenceException;
import com.congdinh2008.tms.exceptions.DuplicateEntityException;
import com.congdinh2008.tms.exceptions.EntityNotFoundException;
import com.congdinh2008.tms.exceptions.InvalidAssignmentException;
import com.congdinh2008.tms.repositories.ProjectRepository;
import com.congdinh2008.tms.repositories.TagRepository;
import com.congdinh2008.tms.repositories.TaskRepository;
import com.congdinh2008.tms.repositories.UserRepository;
import com.congdinh2008.tms.services.TaskService;
import com.congdinh2008.tms.utils.MapperUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of TaskService providing business logic for task operations
 * 
 * @author Cong Dinh
 * @version 1.0.0
 * @since 1.0.0
 */
@Service
@Transactional
public class TaskServiceImpl implements TaskService {
    
    private static final Logger logger = LoggerFactory.getLogger(TaskServiceImpl.class);
    private static final String TASK_ENTITY = "Task";
    
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;
    
    public TaskServiceImpl(TaskRepository taskRepository, ProjectRepository projectRepository,
                          UserRepository userRepository, TagRepository tagRepository) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.tagRepository = tagRepository;
    }
    
    @Override
    public TaskResponse create(CreateTaskRequest request) {
        logger.info("Creating task with title: {}", request.getTitle());
        
        // Validate project exists
        Project project = projectRepository.findByIdOptional(request.getProjectId())
                .orElseThrow(() -> new EntityNotFoundException("Project", request.getProjectId()));
        
        // Validate assignee if provided
        User assignee = null;
        if (request.getAssigneeId() != null) {
            assignee = userRepository.findByIdOptional(request.getAssigneeId())
                    .orElseThrow(() -> new EntityNotFoundException("User", request.getAssigneeId()));
            
            // Business Rule R3: Only project members can be assigned tasks
            if (!userRepository.isUserMemberOfProject(request.getAssigneeId(), request.getProjectId())) {
                throw new InvalidAssignmentException("User must be a project member to be assigned tasks");
            }
        }
        
        // Validate parent task if provided
        Task parentTask = null;
        if (request.getParentTaskId() != null) {
            parentTask = taskRepository.findByIdOptional(request.getParentTaskId())
                    .orElseThrow(() -> new EntityNotFoundException(TASK_ENTITY, request.getParentTaskId()));
            
            // Business Rule R4: Parent task must be in the same project
            if (!parentTask.getProject().getId().equals(request.getProjectId())) {
                throw new BusinessRuleViolationException("R4", 
                        "Parent task must be in the same project as the subtask");
            }
        }
        
        // Convert request to entity
        Task task = MapperUtil.mapToEntity(request, Task.class);
        task.setProject(project);
        task.setAssignee(assignee);
        task.setParentTask(parentTask);
        task.setStatus(TaskStatus.TODO); // Default status
        
        // Add tags if provided
        if (request.getTagIds() != null && !request.getTagIds().isEmpty()) {
            List<Tag> tags = new ArrayList<>();
            for (Long tagId : request.getTagIds()) {
                Tag tag = tagRepository.findByIdOptional(tagId)
                        .orElseThrow(() -> new EntityNotFoundException("Tag", tagId));
                tags.add(tag);
            }
            task.setTags(tags);
        }
        
        // Save task
        Task savedTask = taskRepository.save(task);
        
        logger.info("Task created successfully with ID: {}", savedTask.getId());
        return MapperUtil.mapToDto(savedTask, TaskResponse.class);
    }
    
    @Override
    @Transactional(readOnly = true)
    public TaskResponse getById(Long id) {
        logger.debug("Retrieving task with ID: {}", id);
        
        Task task = taskRepository.findByIdOptional(id)
                .orElseThrow(() -> new EntityNotFoundException(TASK_ENTITY, id));
        
        return MapperUtil.mapToDto(task, TaskResponse.class);
    }
    
    @Override
    public TaskResponse update(Long id, UpdateTaskRequest request) {
        logger.info("Updating task with ID: {}", id);
        
        Task existingTask = taskRepository.findByIdOptional(id)
                .orElseThrow(() -> new EntityNotFoundException(TASK_ENTITY, id));
        
        // Validate assignee if being changed
        if (request.getAssigneeId() != null) {
            User assignee = userRepository.findByIdOptional(request.getAssigneeId())
                    .orElseThrow(() -> new EntityNotFoundException("User", request.getAssigneeId()));
            
            // Business Rule R3: Only project members can be assigned tasks
            if (!userRepository.isUserMemberOfProject(request.getAssigneeId(), 
                    existingTask.getProject().getId())) {
                throw new InvalidAssignmentException("User must be a project member to be assigned tasks");
            }
            
            existingTask.setAssignee(assignee);
        }
        
        // Validate parent task if being changed
        if (request.getParentTaskId() != null) {
            Task parentTask = taskRepository.findByIdOptional(request.getParentTaskId())
                    .orElseThrow(() -> new EntityNotFoundException(TASK_ENTITY, request.getParentTaskId()));
            
            // Business Rule R5: Prevent circular references
            if (isCircularReference(id, request.getParentTaskId())) {
                throw new CircularReferenceException("Cannot set parent task - would create circular reference");
            }
            
            // Business Rule R4: Parent task must be in the same project
            if (!parentTask.getProject().getId().equals(existingTask.getProject().getId())) {
                throw new BusinessRuleViolationException("R4", 
                        "Parent task must be in the same project as the subtask");
            }
            
            existingTask.setParentTask(parentTask);
        }
        
        // Update other fields
        MapperUtil.updateEntityFromDto(request, existingTask);
        
        // Update tags if provided
        if (request.getTagIds() != null) {
            List<Tag> tags = new ArrayList<>();
            for (Long tagId : request.getTagIds()) {
                Tag tag = tagRepository.findByIdOptional(tagId)
                        .orElseThrow(() -> new EntityNotFoundException("Tag", tagId));
                tags.add(tag);
            }
            existingTask.setTags(tags);
        }
        
        Task updatedTask = taskRepository.save(existingTask);
        
        logger.info("Task updated successfully with ID: {}", updatedTask.getId());
        return MapperUtil.mapToDto(updatedTask, TaskResponse.class);
    }
    
    @Override
    public void delete(Long id) {
        logger.info("Deleting task with ID: {}", id);
        
        Task task = taskRepository.findByIdOptional(id)
                .orElseThrow(() -> new EntityNotFoundException(TASK_ENTITY, id));
        
        // Business Rule R6: Tasks with subtasks cannot be deleted
        List<Task> subtasks = taskRepository.findSubTasks(id);
        if (!subtasks.isEmpty()) {
            throw new BusinessRuleViolationException("R6", 
                    "Cannot delete task with subtasks. Delete or reassign subtasks first.");
        }
        
        taskRepository.delete(task);
        logger.info("Task deleted successfully with ID: {}", id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> getAll() {
        logger.debug("Retrieving all tasks");
        
        List<Task> tasks = taskRepository.findAll();
        List<TaskResponse> responses = new ArrayList<>();
        
        for (Task task : tasks) {
            responses.add(MapperUtil.mapToDto(task, TaskResponse.class));
        }
        
        return responses;
    }
    
    @Override
    @Transactional(readOnly = true)
    public long count() {
        return taskRepository.count();
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return taskRepository.existsById(id);
    }
    
    @Override
    public TaskResponse assignTask(Long taskId, Long userId) {
        logger.info("Assigning task {} to user {}", taskId, userId);
        
        Task task = taskRepository.findByIdOptional(taskId)
                .orElseThrow(() -> new EntityNotFoundException(TASK_ENTITY, taskId));
        
        User user = userRepository.findByIdOptional(userId)
                .orElseThrow(() -> new EntityNotFoundException("User", userId));
        
        // Business Rule R3: Only project members can be assigned tasks
        if (!userRepository.isUserMemberOfProject(userId, task.getProject().getId())) {
            throw new InvalidAssignmentException("User must be a project member to be assigned tasks");
        }
        
        task.setAssignee(user);
        Task updatedTask = taskRepository.save(task);
        
        logger.info("Task assigned successfully");
        return MapperUtil.mapToDto(updatedTask, TaskResponse.class);
    }
    
    @Override
    public TaskResponse unassignTask(Long taskId) {
        logger.info("Unassigning task {}", taskId);
        
        Task task = taskRepository.findByIdOptional(taskId)
                .orElseThrow(() -> new EntityNotFoundException(TASK_ENTITY, taskId));
        
        task.setAssignee(null);
        Task updatedTask = taskRepository.save(task);
        
        logger.info("Task unassigned successfully");
        return MapperUtil.mapToDto(updatedTask, TaskResponse.class);
    }
    
    @Override
    public TaskResponse changeStatus(Long taskId, TaskStatus status) {
        logger.info("Changing status of task {} to {}", taskId, status);
        
        Task task = taskRepository.findByIdOptional(taskId)
                .orElseThrow(() -> new EntityNotFoundException(TASK_ENTITY, taskId));
        
        // Business Rule R7: Cannot complete task if it has incomplete subtasks
        if (status == TaskStatus.DONE) {
            List<Task> subtasks = taskRepository.findSubTasks(taskId);
            boolean hasIncompleteSubtasks = subtasks.stream()
                    .anyMatch(subtask -> subtask.getStatus() != TaskStatus.DONE);
            
            if (hasIncompleteSubtasks) {
                throw new BusinessRuleViolationException("R7", 
                        "Cannot complete task with incomplete subtasks. Complete all subtasks first.");
            }
        }
        
        task.setStatus(status);
        Task updatedTask = taskRepository.save(task);
        
        logger.info("Task status changed successfully");
        return MapperUtil.mapToDto(updatedTask, TaskResponse.class);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> getTasksByProject(Long projectId) {
        logger.debug("Retrieving tasks for project {}", projectId);
        
        if (!projectRepository.existsById(projectId)) {
            throw new EntityNotFoundException("Project", projectId);
        }
        
        List<Task> tasks = taskRepository.findByProject(projectId);
        List<TaskResponse> responses = new ArrayList<>();
        
        for (Task task : tasks) {
            responses.add(MapperUtil.mapToDto(task, TaskResponse.class));
        }
        
        return responses;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> getTasksByAssignee(Long userId) {
        logger.debug("Retrieving tasks assigned to user {}", userId);
        
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User", userId);
        }
        
        List<Task> tasks = taskRepository.findByAssignee(userId);
        List<TaskResponse> responses = new ArrayList<>();
        
        for (Task task : tasks) {
            responses.add(MapperUtil.mapToDto(task, TaskResponse.class));
        }
        
        return responses;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> getSubtasks(Long parentTaskId) {
        logger.debug("Retrieving subtasks of task {}", parentTaskId);
        
        if (!taskRepository.existsById(parentTaskId)) {
            throw new EntityNotFoundException(TASK_ENTITY, parentTaskId);
        }
        
        List<Task> subtasks = taskRepository.findSubTasks(parentTaskId);
        List<TaskResponse> responses = new ArrayList<>();
        
        for (Task task : subtasks) {
            responses.add(MapperUtil.mapToDto(task, TaskResponse.class));
        }
        
        return responses;
    }
    
    @Override
    public TaskResponse addTag(Long taskId, Long tagId) {
        logger.info("Adding tag {} to task {}", tagId, taskId);
        
        Task task = taskRepository.findByIdOptional(taskId)
                .orElseThrow(() -> new EntityNotFoundException(TASK_ENTITY, taskId));
        
        Tag tag = tagRepository.findByIdOptional(tagId)
                .orElseThrow(() -> new EntityNotFoundException("Tag", tagId));
        
        if (task.getTags().contains(tag)) {
            throw new DuplicateEntityException("Task tag", "tagId", tagId.toString());
        }
        
        task.getTags().add(tag);
        Task updatedTask = taskRepository.save(task);
        
        logger.info("Tag added successfully to task");
        return MapperUtil.mapToDto(updatedTask, TaskResponse.class);
    }
    
    @Override
    public TaskResponse removeTag(Long taskId, Long tagId) {
        logger.info("Removing tag {} from task {}", tagId, taskId);
        
        Task task = taskRepository.findByIdOptional(taskId)
                .orElseThrow(() -> new EntityNotFoundException(TASK_ENTITY, taskId));
        
        Tag tag = tagRepository.findByIdOptional(tagId)
                .orElseThrow(() -> new EntityNotFoundException("Tag", tagId));
        
        if (!task.getTags().contains(tag)) {
            throw new EntityNotFoundException("Task tag", tagId);
        }
        
        task.getTags().remove(tag);
        Task updatedTask = taskRepository.save(task);
        
        logger.info("Tag removed successfully from task");
        return MapperUtil.mapToDto(updatedTask, TaskResponse.class);
    }
    
    /**
     * Check if setting a parent task would create a circular reference
     * 
     * @param taskId current task ID
     * @param parentTaskId proposed parent task ID
     * @return true if circular reference would be created
     */
    private boolean isCircularReference(Long taskId, Long parentTaskId) {
        if (taskId.equals(parentTaskId)) {
            return true;
        }
        
        Task parentTask = taskRepository.findByIdOptional(parentTaskId).orElse(null);
        if (parentTask == null) {
            return false;
        }
        
        // Check if any ancestor of the parent task is the current task
        while (parentTask.getParentTask() != null) {
            if (parentTask.getParentTask().getId().equals(taskId)) {
                return true;
            }
            parentTask = parentTask.getParentTask();
        }
        
        return false;
    }
}

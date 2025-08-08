package com.congdinh2008.tms.services.impl;

import com.congdinh2008.tms.dto.request.CreateProjectRequest;
import com.congdinh2008.tms.dto.request.UpdateProjectRequest;
import com.congdinh2008.tms.dto.response.ProjectResponse;
import com.congdinh2008.tms.entities.Project;
import com.congdinh2008.tms.entities.User;
import com.congdinh2008.tms.exceptions.BusinessRuleViolationException;
import com.congdinh2008.tms.exceptions.DuplicateEntityException;
import com.congdinh2008.tms.exceptions.EntityNotFoundException;
import com.congdinh2008.tms.repositories.ProjectRepository;
import com.congdinh2008.tms.repositories.UserRepository;
import com.congdinh2008.tms.services.ProjectService;
import com.congdinh2008.tms.utils.MapperUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of ProjectService providing business logic for project operations
 * 
 * @author Cong Dinh
 * @version 1.0.0
 * @since 1.0.0
 */
@Service
@Transactional
public class ProjectServiceImpl implements ProjectService {
    
    private static final Logger logger = LoggerFactory.getLogger(ProjectServiceImpl.class);
    
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    
    public ProjectServiceImpl(ProjectRepository projectRepository, UserRepository userRepository) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }
    
    @Override
    public ProjectResponse create(CreateProjectRequest request) {
        logger.info("Creating project with name: {}", request.getName());
        
        // Business Rule: Check for duplicate project name
        List<Project> existingProjects = projectRepository.findByNameContaining(request.getName());
        boolean duplicateExists = existingProjects.stream()
                .anyMatch(p -> p.getName().equalsIgnoreCase(request.getName()));
        
        if (duplicateExists) {
            throw new DuplicateEntityException("Project", "name", request.getName());
        }
        
        // Convert request to entity
        Project project = MapperUtil.mapToEntity(request, Project.class);
        
        // Add members if provided
        if (request.getMemberIds() != null && !request.getMemberIds().isEmpty()) {
            List<User> members = new ArrayList<>();
            for (Long memberId : request.getMemberIds()) {
                User user = userRepository.findByIdOptional(memberId)
                        .orElseThrow(() -> new EntityNotFoundException("User", memberId));
                members.add(user);
            }
            project.setMembers(members);
        }
        
        // Save project
        Project savedProject = projectRepository.save(project);
        
        logger.info("Project created successfully with ID: {}", savedProject.getId());
        return MapperUtil.mapToDto(savedProject, ProjectResponse.class);
    }
    
    @Override
    @Transactional(readOnly = true)
    public ProjectResponse getById(Long id) {
        logger.debug("Retrieving project with ID: {}", id);
        
        Project project = projectRepository.findByIdOptional(id)
                .orElseThrow(() -> new EntityNotFoundException("Project", id));
        
        return MapperUtil.mapToDto(project, ProjectResponse.class);
    }
    
    @Override
    public ProjectResponse update(Long id, UpdateProjectRequest request) {
        logger.info("Updating project with ID: {}", id);
        
        Project existingProject = projectRepository.findByIdOptional(id)
                .orElseThrow(() -> new EntityNotFoundException("Project", id));
        
        // Business Rule: Check for duplicate name if name is being changed
        if (request.getName() != null && !request.getName().equals(existingProject.getName())) {
            List<Project> existingProjects = projectRepository.findByNameContaining(request.getName());
            boolean duplicateExists = existingProjects.stream()
                    .anyMatch(p -> p.getName().equalsIgnoreCase(request.getName()) && !p.getId().equals(id));
            
            if (duplicateExists) {
                throw new DuplicateEntityException("Project", "name", request.getName());
            }
        }
        
        // Update fields
        MapperUtil.updateEntityFromDto(request, existingProject);
        
        // Update members if provided
        if (request.getMemberIds() != null) {
            List<User> members = new ArrayList<>();
            for (Long memberId : request.getMemberIds()) {
                User user = userRepository.findByIdOptional(memberId)
                        .orElseThrow(() -> new EntityNotFoundException("User", memberId));
                members.add(user);
            }
            existingProject.setMembers(members);
        }
        
        Project updatedProject = projectRepository.save(existingProject);
        
        logger.info("Project updated successfully with ID: {}", updatedProject.getId());
        return MapperUtil.mapToDto(updatedProject, ProjectResponse.class);
    }
    
    @Override
    public void delete(Long id) {
        logger.info("Deleting project with ID: {}", id);
        
        Project project = projectRepository.findByIdOptional(id)
                .orElseThrow(() -> new EntityNotFoundException("Project", id));
        
        // Business Rule R1: Projects with active tasks cannot be deleted
        if (projectRepository.hasIncompleteTasks(id)) {
            throw new BusinessRuleViolationException("R1", 
                    "Cannot delete project with active tasks. Complete or reassign all tasks first.");
        }
        
        projectRepository.delete(project);
        logger.info("Project deleted successfully with ID: {}", id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ProjectResponse> getAll() {
        logger.debug("Retrieving all projects");
        
        List<Project> projects = projectRepository.findAll();
        List<ProjectResponse> responses = new ArrayList<>();
        
        for (Project project : projects) {
            responses.add(MapperUtil.mapToDto(project, ProjectResponse.class));
        }
        
        return responses;
    }
    
    @Override
    @Transactional(readOnly = true)
    public long count() {
        return projectRepository.count();
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return projectRepository.existsById(id);
    }
    
    @Override
    public ProjectResponse addMember(Long projectId, Long userId) {
        logger.info("Adding member {} to project {}", userId, projectId);
        
        Project project = projectRepository.findByIdOptional(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project", projectId));
        
        User user = userRepository.findByIdOptional(userId)
                .orElseThrow(() -> new EntityNotFoundException("User", userId));
        
        if (project.getMembers().contains(user)) {
            throw new DuplicateEntityException("Project member", "userId", userId.toString());
        }
        
        project.getMembers().add(user);
        Project updatedProject = projectRepository.save(project);
        
        logger.info("Member added successfully to project");
        return MapperUtil.mapToDto(updatedProject, ProjectResponse.class);
    }
    
    @Override
    public ProjectResponse removeMember(Long projectId, Long userId) {
        logger.info("Removing member {} from project {}", userId, projectId);
        
        Project project = projectRepository.findByIdOptional(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project", projectId));
        
        User user = userRepository.findByIdOptional(userId)
                .orElseThrow(() -> new EntityNotFoundException("User", userId));
        
        if (!project.getMembers().contains(user)) {
            throw new EntityNotFoundException("Project member", userId);
        }
        
        project.getMembers().remove(user);
        Project updatedProject = projectRepository.save(project);
        
        logger.info("Member removed successfully from project");
        return MapperUtil.mapToDto(updatedProject, ProjectResponse.class);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ProjectResponse> getProjectsByMember(Long userId) {
        logger.debug("Retrieving projects for user {}", userId);
        
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User", userId);
        }
        
        List<Project> projects = projectRepository.findProjectsByUser(userId);
        List<ProjectResponse> responses = new ArrayList<>();
        
        for (Project project : projects) {
            responses.add(MapperUtil.mapToDto(project, ProjectResponse.class));
        }
        
        return responses;
    }
}

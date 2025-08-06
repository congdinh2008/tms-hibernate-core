package com.congdinh2008.tms.repositories.impl;

import com.congdinh2008.tms.entities.Project;
import com.congdinh2008.tms.exceptions.RepositoryException;
import com.congdinh2008.tms.repositories.ProjectRepository;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Implementation of ProjectRepository interface
 */
@Repository
public class ProjectRepositoryImpl extends BaseRepositoryImpl<Project, Long> implements ProjectRepository {
    
    private static final Logger logger = LoggerFactory.getLogger(ProjectRepositoryImpl.class);
    
    public ProjectRepositoryImpl() {
        super();
    }
    
    @Override
    public List<Project> findByNameContaining(String name) {
        String correlationId = UUID.randomUUID().toString().substring(0, 8);
        logger.debug("[{}] Finding projects by name containing: {}", correlationId, name);
        
        try {
            Session session = sessionFactory.getCurrentSession();
            Query<Project> query = session.createQuery(
                "FROM Project p WHERE LOWER(p.name) LIKE LOWER(:name)", Project.class);
            query.setParameter("name", "%" + name + "%");
            
            List<Project> results = query.getResultList();
            logger.debug("[{}] Found {} projects by name containing: {}", correlationId, results.size(), name);
            return results;
        } catch (Exception e) {
            logger.error("[{}] Error finding projects by name containing: {}", correlationId, name, e);
            throw new RepositoryException("Error finding projects by name containing", e);
        }
    }
    
    @Override
    public List<Project> findProjectsByUser(Long userId) {
        String correlationId = UUID.randomUUID().toString().substring(0, 8);
        logger.debug("[{}] Finding projects by user ID: {}", correlationId, userId);
        
        try {
            Session session = sessionFactory.getCurrentSession();
            Query<Project> query = session.createQuery(
                "SELECT DISTINCT p FROM Project p JOIN p.members m WHERE m.id = :userId ORDER BY p.name ASC", 
                Project.class);
            query.setParameter("userId", userId);
            
            List<Project> results = query.getResultList();
            logger.debug("[{}] Found {} projects for user ID: {}", correlationId, results.size(), userId);
            return results;
        } catch (Exception e) {
            logger.error("[{}] Error finding projects by user ID: {}", correlationId, userId, e);
            throw new RepositoryException("Error finding projects by user ID", e);
        }
    }
    
    @Override
    public boolean hasIncompleteTasks(Long projectId) {
        String correlationId = UUID.randomUUID().toString().substring(0, 8);
        logger.debug("[{}] Checking if project has incomplete tasks: {}", correlationId, projectId);
        
        try {
            Session session = sessionFactory.getCurrentSession();
            Query<Long> query = session.createQuery(
                "SELECT COUNT(t) FROM Task t WHERE t.project.id = :projectId AND t.status != 'DONE'", 
                Long.class);
            query.setParameter("projectId", projectId);
            
            Long count = query.uniqueResult();
            boolean hasIncompleteTasks = count != null && count > 0;
            
            logger.debug("[{}] Project ID: {} has incomplete tasks: {} (count: {})", 
                correlationId, projectId, hasIncompleteTasks, count);
            return hasIncompleteTasks;
        } catch (Exception e) {
            logger.error("[{}] Error checking if project has incomplete tasks: {}", correlationId, projectId, e);
            throw new RepositoryException("Error checking if project has incomplete tasks", e);
        }
    }
    
    @Override
    public List<Object[]> findProjectsWithTaskCount() {
        String correlationId = UUID.randomUUID().toString().substring(0, 8);
        logger.debug("[{}] Finding projects with task count", correlationId);
        
        try {
            Session session = sessionFactory.getCurrentSession();
            String sql = """
                SELECT 
                    p.id,
                    p.name,
                    p.description,
                    COUNT(t.id) as task_count,
                    COUNT(CASE WHEN t.status = 'DONE' THEN 1 END) as completed_count
                FROM projects p
                LEFT JOIN tasks t ON p.id = t.project_id
                GROUP BY p.id, p.name, p.description
                ORDER BY p.name ASC
                """;
            
            Query<Object[]> query = session.createNativeQuery(sql, Object[].class);
            List<Object[]> results = query.getResultList();
            
            logger.debug("[{}] Found {} projects with task count", correlationId, results.size());
            return results;
        } catch (Exception e) {
            logger.error("[{}] Error finding projects with task count", correlationId, e);
            throw new RepositoryException("Error finding projects with task count", e);
        }
    }
    
    @Override
    public boolean canDeleteProject(Long projectId) {
        String correlationId = UUID.randomUUID().toString().substring(0, 8);
        logger.debug("[{}] Checking if project can be deleted: {}", correlationId, projectId);
        
        try {
            // Business Rule R1: Project can only be deleted if it has no tasks
            return !hasIncompleteTasks(projectId);
        } catch (Exception e) {
            logger.error("[{}] Error checking if project can be deleted: {}", correlationId, projectId, e);
            throw new RepositoryException("Error checking if project can be deleted", e);
        }
    }
    
    @Override
    public List<Project> findActiveProjects() {
        String correlationId = UUID.randomUUID().toString().substring(0, 8);
        logger.debug("[{}] Finding active projects", correlationId);
        
        try {
            Session session = sessionFactory.getCurrentSession();
            Query<Project> query = session.createQuery(
                "SELECT DISTINCT p FROM Project p JOIN p.tasks t WHERE t.status != 'DONE'", 
                Project.class);
            
            List<Project> results = query.getResultList();
            logger.debug("[{}] Found {} active projects", correlationId, results.size());
            return results;
        } catch (Exception e) {
            logger.error("[{}] Error finding active projects", correlationId, e);
            throw new RepositoryException("Error finding active projects", e);
        }
    }
    
    @Override
    public List<Project> findProjectsWithOverdueTasks() {
        String correlationId = UUID.randomUUID().toString().substring(0, 8);
        logger.debug("[{}] Finding projects with overdue tasks", correlationId);
        
        try {
            Session session = sessionFactory.getCurrentSession();
            Query<Project> query = session.createQuery(
                "SELECT DISTINCT p FROM Project p JOIN p.tasks t WHERE t.dueDate < CURRENT_DATE AND t.status != 'DONE'", 
                Project.class);
            
            List<Project> results = query.getResultList();
            logger.debug("[{}] Found {} projects with overdue tasks", correlationId, results.size());
            return results;
        } catch (Exception e) {
            logger.error("[{}] Error finding projects with overdue tasks", correlationId, e);
            throw new RepositoryException("Error finding projects with overdue tasks", e);
        }
    }
}

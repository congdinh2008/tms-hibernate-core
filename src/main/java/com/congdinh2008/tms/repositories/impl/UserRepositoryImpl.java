package com.congdinh2008.tms.repositories.impl;

import com.congdinh2008.tms.entities.User;
import com.congdinh2008.tms.exceptions.RepositoryException;
import com.congdinh2008.tms.repositories.UserRepository;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository implementation for User entity operations
 */
@Repository
public class UserRepositoryImpl extends BaseRepositoryImpl<User, Long> implements UserRepository {
    
    @Override
    public User findByEmail(String email) {
        String correlationId = UUID.randomUUID().toString();
        log.debug("{} - Finding user by email: {}", correlationId, email);
        
        try {
            Session session = getCurrentSession();
            String hql = "FROM User u WHERE u.email = :email";
            Query<User> query = session.createQuery(hql, User.class);
            query.setParameter("email", email);
            User user = query.uniqueResult();
            
            if (user != null) {
                log.debug("{} - Found user by email: {}", correlationId, email);
            } else {
                log.debug("{} - No user found with email: {}", correlationId, email);
            }
            return user;
        } catch (Exception e) {
            log.error("{} - Error finding user by email: {}", correlationId, e.getMessage(), e);
            throw new RepositoryException("Failed to find user by email", e);
        }
    }
    
    @Override
    public List<User> findByNameContaining(String name) {
        String correlationId = UUID.randomUUID().toString();
        log.debug("{} - Finding users by name containing: {}", correlationId, name);
        
        try {
            Session session = getCurrentSession();
            String hql = "FROM User u WHERE LOWER(u.name) LIKE LOWER(:name)";
            Query<User> query = session.createQuery(hql, User.class);
            query.setParameter("name", "%" + name + "%");
            List<User> users = query.getResultList();
            
            log.debug("{} - Found {} users with name containing: {}", correlationId, users.size(), name);
            return users;
        } catch (Exception e) {
            log.error("{} - Error finding users by name: {}", correlationId, e.getMessage(), e);
            throw new RepositoryException("Failed to find users by name", e);
        }
    }
    
    @Override
    public boolean existsByEmail(String email) {
        String correlationId = UUID.randomUUID().toString();
        log.debug("{} - Checking if user exists by email: {}", correlationId, email);
        
        try {
            Session session = getCurrentSession();
            String hql = "SELECT COUNT(*) FROM User u WHERE u.email = :email";
            Query<Long> query = session.createQuery(hql, Long.class);
            query.setParameter("email", email);
            Long count = query.getSingleResult();
            
            boolean exists = count > 0;
            log.debug("{} - User exists by email: {}", correlationId, exists);
            return exists;
        } catch (Exception e) {
            log.error("{} - Error checking user existence by email: {}", correlationId, e.getMessage(), e);
            throw new RepositoryException("Failed to check user existence by email", e);
        }
    }
    
    @Override
    public List<User> findProjectMembers(Long projectId) {
        String correlationId = UUID.randomUUID().toString();
        log.debug("{} - Finding project members for project ID: {}", correlationId, projectId);
        
        try {
            Session session = getCurrentSession();
            // Using native SQL for performance with many-to-many relationship
            String sql = """
                SELECT u.* FROM users u
                INNER JOIN project_members pm ON u.id = pm.user_id
                WHERE pm.project_id = :projectId
                ORDER BY u.name
                """;
            
            Query<User> query = session.createNativeQuery(sql, User.class);
            query.setParameter("projectId", projectId);
            List<User> members = query.getResultList();
            
            log.debug("{} - Found {} project members for project ID: {}", 
                     correlationId, members.size(), projectId);
            return members;
        } catch (Exception e) {
            log.error("{} - Error finding project members: {}", correlationId, e.getMessage(), e);
            throw new RepositoryException("Failed to find project members", e);
        }
    }
    
    @Override
    public boolean isUserMemberOfProject(Long userId, Long projectId) {
        String correlationId = UUID.randomUUID().toString();
        log.debug("{} - Checking if user {} is member of project {}", correlationId, userId, projectId);
        
        try {
            Session session = getCurrentSession();
            String sql = """
                SELECT COUNT(*) FROM project_members pm
                WHERE pm.user_id = :userId AND pm.project_id = :projectId
                """;
            
            Query<Number> query = session.createNativeQuery(sql, Number.class);
            query.setParameter("userId", userId);
            query.setParameter("projectId", projectId);
            Number count = query.getSingleResult();
            
            boolean isMember = count.longValue() > 0;
            log.debug("{} - User {} is member of project {}: {}", 
                     correlationId, userId, projectId, isMember);
            return isMember;
        } catch (Exception e) {
            log.error("{} - Error checking user project membership: {}", correlationId, e.getMessage(), e);
            throw new RepositoryException("Failed to check user project membership", e);
        }
    }
    
    @Override
    public List<User> findUsersWithTasksInProject(Long projectId) {
        String correlationId = UUID.randomUUID().toString();
        log.debug("{} - Finding users with tasks in project ID: {}", correlationId, projectId);
        
        try {
            Session session = getCurrentSession();
            String hql = """
                SELECT DISTINCT u FROM User u
                INNER JOIN u.assignedTasks t
                WHERE t.project.id = :projectId
                ORDER BY u.name
                """;
            
            Query<User> query = session.createQuery(hql, User.class);
            query.setParameter("projectId", projectId);
            List<User> users = query.getResultList();
            
            log.debug("{} - Found {} users with tasks in project ID: {}", 
                     correlationId, users.size(), projectId);
            return users;
        } catch (Exception e) {
            log.error("{} - Error finding users with tasks in project: {}", correlationId, e.getMessage(), e);
            throw new RepositoryException("Failed to find users with tasks in project", e);
        }
    }
}

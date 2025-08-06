package com.congdinh2008.tms.repositories.impl;

import com.congdinh2008.tms.entities.TaskHistory;
import com.congdinh2008.tms.exceptions.RepositoryException;
import com.congdinh2008.tms.repositories.TaskHistoryRepository;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Implementation of TaskHistoryRepository interface
 */
@Repository
public class TaskHistoryRepositoryImpl extends BaseRepositoryImpl<TaskHistory, Long> implements TaskHistoryRepository {
    
    private static final Logger logger = LoggerFactory.getLogger(TaskHistoryRepositoryImpl.class);
    
    public TaskHistoryRepositoryImpl() {
        super();
    }
    
    @Override
    public List<TaskHistory> findByTaskIdOrderByTimestampDesc(Long taskId) {
        String correlationId = UUID.randomUUID().toString().substring(0, 8);
        logger.debug("[{}] Finding task history by task ID: {}", correlationId, taskId);
        
        try {
            Session session = sessionFactory.getCurrentSession();
            Query<TaskHistory> query = session.createQuery(
                "FROM TaskHistory th WHERE th.task.id = :taskId ORDER BY th.timestamp DESC", 
                TaskHistory.class);
            query.setParameter("taskId", taskId);
            
            List<TaskHistory> results = query.getResultList();
            logger.debug("[{}] Found {} task history entries for task ID: {}", 
                correlationId, results.size(), taskId);
            return results;
        } catch (Exception e) {
            logger.error("[{}] Error finding task history by task ID: {}", correlationId, taskId, e);
            throw new RepositoryException("Error finding task history by task ID", e);
        }
    }
    
    @Override
    public List<TaskHistory> findByUserIdOrderByTimestampDesc(Long userId) {
        String correlationId = UUID.randomUUID().toString().substring(0, 8);
        logger.debug("[{}] Finding task history by user ID: {}", correlationId, userId);
        
        try {
            Session session = sessionFactory.getCurrentSession();
            Query<TaskHistory> query = session.createQuery(
                "FROM TaskHistory th WHERE th.user.id = :userId ORDER BY th.timestamp DESC", 
                TaskHistory.class);
            query.setParameter("userId", userId);
            
            List<TaskHistory> results = query.getResultList();
            logger.debug("[{}] Found {} task history entries for user ID: {}", 
                correlationId, results.size(), userId);
            return results;
        } catch (Exception e) {
            logger.error("[{}] Error finding task history by user ID: {}", correlationId, userId, e);
            throw new RepositoryException("Error finding task history by user ID", e);
        }
    }
    
    @Override
    public List<TaskHistory> findByTaskIdAndUserId(Long taskId, Long userId) {
        String correlationId = UUID.randomUUID().toString().substring(0, 8);
        logger.debug("[{}] Finding task history by task ID: {} and user ID: {}", 
            correlationId, taskId, userId);
        
        try {
            Session session = sessionFactory.getCurrentSession();
            Query<TaskHistory> query = session.createQuery(
                "FROM TaskHistory th WHERE th.task.id = :taskId AND th.user.id = :userId ORDER BY th.timestamp DESC", 
                TaskHistory.class);
            query.setParameter("taskId", taskId);
            query.setParameter("userId", userId);
            
            List<TaskHistory> results = query.getResultList();
            logger.debug("[{}] Found {} task history entries for task ID: {} and user ID: {}", 
                correlationId, results.size(), taskId, userId);
            return results;
        } catch (Exception e) {
            logger.error("[{}] Error finding task history by task ID: {} and user ID: {}", 
                correlationId, taskId, userId, e);
            throw new RepositoryException("Error finding task history by task and user", e);
        }
    }
    
    @Override
    public List<TaskHistory> findByTimestampBetween(LocalDateTime startDate, LocalDateTime endDate) {
        String correlationId = UUID.randomUUID().toString().substring(0, 8);
        logger.debug("[{}] Finding task history between {} and {}", correlationId, startDate, endDate);
        
        try {
            Session session = sessionFactory.getCurrentSession();
            Query<TaskHistory> query = session.createQuery(
                "FROM TaskHistory th WHERE th.timestamp BETWEEN :startDate AND :endDate ORDER BY th.timestamp DESC", 
                TaskHistory.class);
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
            
            List<TaskHistory> results = query.getResultList();
            logger.debug("[{}] Found {} task history entries between {} and {}", 
                correlationId, results.size(), startDate, endDate);
            return results;
        } catch (Exception e) {
            logger.error("[{}] Error finding task history between dates", correlationId, e);
            throw new RepositoryException("Error finding task history between dates", e);
        }
    }
    
    @Override
    public List<TaskHistory> findRecentHistory(int limit) {
        String correlationId = UUID.randomUUID().toString().substring(0, 8);
        logger.debug("[{}] Finding {} recent task history entries", correlationId, limit);
        
        try {
            Session session = sessionFactory.getCurrentSession();
            Query<TaskHistory> query = session.createQuery(
                "FROM TaskHistory th ORDER BY th.timestamp DESC", TaskHistory.class);
            query.setMaxResults(limit);
            
            List<TaskHistory> results = query.getResultList();
            logger.debug("[{}] Found {} recent task history entries", correlationId, results.size());
            return results;
        } catch (Exception e) {
            logger.error("[{}] Error finding recent task history", correlationId, e);
            throw new RepositoryException("Error finding recent task history", e);
        }
    }
    
    @Override
    public List<TaskHistory> findByChangeType(String changeType) {
        String correlationId = UUID.randomUUID().toString().substring(0, 8);
        logger.debug("[{}] Finding task history by change type: {}", correlationId, changeType);
        
        try {
            Session session = sessionFactory.getCurrentSession();
            Query<TaskHistory> query = session.createQuery(
                "FROM TaskHistory th WHERE th.changeType = :changeType ORDER BY th.timestamp DESC", 
                TaskHistory.class);
            query.setParameter("changeType", changeType);
            
            List<TaskHistory> results = query.getResultList();
            logger.debug("[{}] Found {} task history entries for change type: {}", 
                correlationId, results.size(), changeType);
            return results;
        } catch (Exception e) {
            logger.error("[{}] Error finding task history by change type: {}", correlationId, changeType, e);
            throw new RepositoryException("Error finding task history by change type", e);
        }
    }
    
    @Override
    public List<TaskHistory> findByTaskIdIn(List<Long> taskIds) {
        String correlationId = UUID.randomUUID().toString().substring(0, 8);
        logger.debug("[{}] Finding task history for {} tasks", correlationId, taskIds.size());
        
        try {
            Session session = sessionFactory.getCurrentSession();
            Query<TaskHistory> query = session.createQuery(
                "FROM TaskHistory th WHERE th.task.id IN :taskIds ORDER BY th.timestamp DESC", 
                TaskHistory.class);
            query.setParameter("taskIds", taskIds);
            
            List<TaskHistory> results = query.getResultList();
            logger.debug("[{}] Found {} task history entries for {} tasks", 
                correlationId, results.size(), taskIds.size());
            return results;
        } catch (Exception e) {
            logger.error("[{}] Error finding task history for multiple tasks", correlationId, e);
            throw new RepositoryException("Error finding task history for multiple tasks", e);
        }
    }
    
    @Override
    public List<Object[]> getUserActivityStatistics(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        String correlationId = UUID.randomUUID().toString().substring(0, 8);
        logger.debug("[{}] Getting user activity statistics for user: {} between {} and {}", 
            correlationId, userId, startDate, endDate);
        
        try {
            Session session = sessionFactory.getCurrentSession();
            String sql = """
                SELECT 
                    th.change_type,
                    COUNT(*) as change_count,
                    DATE(th.timestamp) as change_date
                FROM task_history th
                WHERE th.user_id = :userId 
                    AND th.timestamp BETWEEN :startDate AND :endDate
                GROUP BY th.change_type, DATE(th.timestamp)
                ORDER BY change_date DESC, change_count DESC
                """;
            
            Query<Object[]> query = session.createNativeQuery(sql, Object[].class);
            query.setParameter("userId", userId);
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
            
            List<Object[]> results = query.getResultList();
            logger.debug("[{}] Retrieved activity statistics: {} entries for user: {}", 
                correlationId, results.size(), userId);
            return results;
        } catch (Exception e) {
            logger.error("[{}] Error getting user activity statistics for user: {}", correlationId, userId, e);
            throw new RepositoryException("Error getting user activity statistics", e);
        }
    }
    
    @Override
    public Long getTaskChangeCount(Long taskId) {
        String correlationId = UUID.randomUUID().toString().substring(0, 8);
        logger.debug("[{}] Getting change count for task ID: {}", correlationId, taskId);
        
        try {
            Session session = sessionFactory.getCurrentSession();
            Query<Long> query = session.createQuery(
                "SELECT COUNT(th) FROM TaskHistory th WHERE th.task.id = :taskId", Long.class);
            query.setParameter("taskId", taskId);
            
            Long count = query.uniqueResult();
            logger.debug("[{}] Task ID: {} has {} changes", correlationId, taskId, count);
            return count != null ? count : 0L;
        } catch (Exception e) {
            logger.error("[{}] Error getting task change count for task ID: {}", correlationId, taskId, e);
            throw new RepositoryException("Error getting task change count", e);
        }
    }
    
    @Override
    public int deleteOldHistory(LocalDateTime beforeDate) {
        String correlationId = UUID.randomUUID().toString().substring(0, 8);
        logger.debug("[{}] Deleting task history before: {}", correlationId, beforeDate);
        
        try {
            Session session = sessionFactory.getCurrentSession();
            Query<?> query = session.createQuery(
                "DELETE FROM TaskHistory th WHERE th.timestamp < :beforeDate");
            query.setParameter("beforeDate", beforeDate);
            
            int deletedCount = query.executeUpdate();
            logger.debug("[{}] Deleted {} old task history entries before: {}", 
                correlationId, deletedCount, beforeDate);
            return deletedCount;
        } catch (Exception e) {
            logger.error("[{}] Error deleting old task history before: {}", correlationId, beforeDate, e);
            throw new RepositoryException("Error deleting old task history", e);
        }
    }
}

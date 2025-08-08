package com.congdinh2008.tms.services.impl;

import com.congdinh2008.tms.config.HibernateUtil;
import com.congdinh2008.tms.dto.response.ProjectStatistics;
import com.congdinh2008.tms.dto.response.UserProductivity;
import com.congdinh2008.tms.services.StoredProcedureService;
import org.hibernate.Session;
import org.hibernate.query.NativeQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of StoredProcedureService for executing stored procedures and complex database operations
 */
@Service
@Transactional(readOnly = true)
public class StoredProcedureServiceImpl implements StoredProcedureService {
    
    private static final Logger logger = LoggerFactory.getLogger(StoredProcedureServiceImpl.class);
    
    @Override
    public Long countCompletedTasksByUser(Long userId, Integer numberOfDays) {
        logger.info("Counting completed tasks for user {} in last {} days", userId, numberOfDays);
        
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Use native SQL to call stored procedure since Hibernate Core doesn't have good stored procedure support
            String sql = "SELECT sp_count_completed_tasks_by_user(:userId, :numberOfDays)";
            
            NativeQuery<Number> query = session.createNativeQuery(sql, Number.class);
            query.setParameter("userId", userId);
            query.setParameter("numberOfDays", numberOfDays);
            
            Number result = query.getSingleResult();
            Long taskCount = result != null ? result.longValue() : 0L;
            
            logger.info("User {} completed {} tasks in last {} days", userId, taskCount, numberOfDays);
            return taskCount;
            
        } catch (Exception e) {
            logger.error("Error counting completed tasks for user {}: {}", userId, e.getMessage(), e);
            return 0L;
        }
    }
    
    @Override
    public ProjectStatistics getProjectStatistics(Long projectId) {
        logger.info("Getting statistics for project {}", projectId);
        
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Use native SQL to call stored procedure
            String sql = "SELECT * FROM sp_project_statistics(:projectId)";
            
            NativeQuery<Object[]> query = session.createNativeQuery(sql, Object[].class);
            query.setParameter("projectId", projectId);
            
            Object[] result = query.getSingleResult();
            
            if (result != null && result.length >= 5) {
                ProjectStatistics stats = new ProjectStatistics();
                stats.setTotalTasks(result[0] != null ? ((Number) result[0]).longValue() : 0L);
                stats.setCompletedTasks(result[1] != null ? ((Number) result[1]).longValue() : 0L);
                stats.setInProgressTasks(result[2] != null ? ((Number) result[2]).longValue() : 0L);
                stats.setOverdueTasks(result[3] != null ? ((Number) result[3]).longValue() : 0L);
                stats.setCompletionRate(result[4] != null ? (BigDecimal) result[4] : BigDecimal.ZERO);
                
                logger.info("Project {} statistics: {} total, {} completed, {}% completion rate", 
                           projectId, stats.getTotalTasks(), stats.getCompletedTasks(), stats.getCompletionRate());
                return stats;
            } else {
                logger.warn("No statistics found for project {}", projectId);
                return new ProjectStatistics(0L, 0L, 0L, 0L, BigDecimal.ZERO);
            }
            
        } catch (Exception e) {
            logger.error("Error getting statistics for project {}: {}", projectId, e.getMessage(), e);
            return new ProjectStatistics(0L, 0L, 0L, 0L, BigDecimal.ZERO);
        }
    }
    
    @Override
    public List<UserProductivity> getUserProductivityReport(LocalDate startDate, LocalDate endDate) {
        logger.info("Generating user productivity report from {} to {}", startDate, endDate);
        
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Use native query for complex productivity analysis
            String sql = """
                SELECT u.id as user_id,
                       u.name as user_name,
                       COUNT(DISTINCT CASE WHEN th.field_changed = 'STATUS' AND th.new_value = 'DONE' 
                                           AND th.change_date >= :startDate AND th.change_date <= :endDate 
                                           THEN th.task_id END) as completed_tasks,
                       COUNT(DISTINCT t.id) as total_assigned_tasks,
                       CASE WHEN COUNT(DISTINCT t.id) > 0 THEN 
                           ROUND((COUNT(DISTINCT CASE WHEN th.field_changed = 'STATUS' AND th.new_value = 'DONE' 
                                                      AND th.change_date >= :startDate AND th.change_date <= :endDate 
                                                      THEN th.task_id END) * 100.0 / COUNT(DISTINCT t.id)), 2)
                           ELSE 0 
                       END as productivity_rate
                FROM users u
                LEFT JOIN tasks t ON u.id = t.assignee_id
                LEFT JOIN task_history th ON t.id = th.task_id AND th.changed_by_id = u.id
                WHERE u.id IS NOT NULL
                GROUP BY u.id, u.name
                HAVING COUNT(DISTINCT t.id) > 0
                ORDER BY productivity_rate DESC, completed_tasks DESC
                """;
            
            NativeQuery<Object[]> query = session.createNativeQuery(sql, Object[].class);
            query.setParameter("startDate", startDate.atStartOfDay());
            query.setParameter("endDate", endDate.atTime(23, 59, 59));
            
            List<Object[]> results = query.getResultList();
            List<UserProductivity> productivityList = new ArrayList<>();
            
            for (Object[] row : results) {
                UserProductivity productivity = new UserProductivity();
                productivity.setUserId(row[0] != null ? ((Number) row[0]).longValue() : null);
                productivity.setUserName(row[1] != null ? row[1].toString() : "Unknown");
                productivity.setCompletedTasks(row[2] != null ? ((Number) row[2]).longValue() : 0L);
                productivity.setTotalAssignedTasks(row[3] != null ? ((Number) row[3]).longValue() : 0L);
                productivity.setProductivityRate(row[4] != null ? (BigDecimal) row[4] : BigDecimal.ZERO);
                
                productivityList.add(productivity);
            }
            
            logger.info("Generated productivity report for {} users", productivityList.size());
            return productivityList;
            
        } catch (Exception e) {
            logger.error("Error generating user productivity report: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }
    
    @Override
    @Transactional
    public Integer cleanupOldHistory(Integer daysToKeep) {
        logger.info("Cleaning up task history older than {} days", daysToKeep);
        
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            
            // Calculate cutoff date
            LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysToKeep);
            
            // Delete old task history records
            String deleteHql = "DELETE FROM TaskHistory th WHERE th.changeDate < :cutoffDate";
            int deletedCount = session.createMutationQuery(deleteHql)
                .setParameter("cutoffDate", cutoffDate)
                .executeUpdate();
            
            session.getTransaction().commit();
            
            logger.info("Cleaned up {} old task history records", deletedCount);
            return deletedCount;
            
        } catch (Exception e) {
            logger.error("Error cleaning up old history: {}", e.getMessage(), e);
            return 0;
        }
    }
    
    @Override
    public List<UserProductivity> getProjectUserProductivity(Long projectId, LocalDate startDate, LocalDate endDate) {
        logger.info("Getting user productivity for project {} from {} to {}", projectId, startDate, endDate);
        
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Native query for project-specific user productivity
            String sql = """
                SELECT u.id as user_id,
                       u.name as user_name,
                       COUNT(DISTINCT CASE WHEN th.field_changed = 'STATUS' AND th.new_value = 'DONE' 
                                           AND th.change_date >= :startDate AND th.change_date <= :endDate 
                                           THEN th.task_id END) as completed_tasks,
                       COUNT(DISTINCT t.id) as total_assigned_tasks,
                       CASE WHEN COUNT(DISTINCT t.id) > 0 THEN 
                           ROUND((COUNT(DISTINCT CASE WHEN th.field_changed = 'STATUS' AND th.new_value = 'DONE' 
                                                      AND th.change_date >= :startDate AND th.change_date <= :endDate 
                                                      THEN th.task_id END) * 100.0 / COUNT(DISTINCT t.id)), 2)
                           ELSE 0 
                       END as productivity_rate
                FROM users u
                JOIN user_project up ON u.id = up.user_id
                LEFT JOIN tasks t ON u.id = t.assignee_id AND t.project_id = :projectId
                LEFT JOIN task_history th ON t.id = th.task_id AND th.changed_by_id = u.id
                WHERE up.project_id = :projectId
                GROUP BY u.id, u.name
                ORDER BY productivity_rate DESC, completed_tasks DESC
                """;
            
            NativeQuery<Object[]> query = session.createNativeQuery(sql, Object[].class);
            query.setParameter("projectId", projectId);
            query.setParameter("startDate", startDate.atStartOfDay());
            query.setParameter("endDate", endDate.atTime(23, 59, 59));
            
            List<Object[]> results = query.getResultList();
            List<UserProductivity> productivityList = new ArrayList<>();
            
            for (Object[] row : results) {
                UserProductivity productivity = new UserProductivity();
                productivity.setUserId(row[0] != null ? ((Number) row[0]).longValue() : null);
                productivity.setUserName(row[1] != null ? row[1].toString() : "Unknown");
                productivity.setCompletedTasks(row[2] != null ? ((Number) row[2]).longValue() : 0L);
                productivity.setTotalAssignedTasks(row[3] != null ? ((Number) row[3]).longValue() : 0L);
                productivity.setProductivityRate(row[4] != null ? (BigDecimal) row[4] : BigDecimal.ZERO);
                
                productivityList.add(productivity);
            }
            
            logger.info("Generated project {} productivity report for {} users", projectId, productivityList.size());
            return productivityList;
            
        } catch (Exception e) {
            logger.error("Error getting project user productivity: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }
    
    @Override
    @Transactional
    public String executeMaintenanceOperations() {
        logger.info("Executing database maintenance operations");
        
        StringBuilder summary = new StringBuilder();
        
        try {
            // 1. Cleanup old history (keep last 90 days)
            Integer deletedRecords = cleanupOldHistory(90);
            summary.append("Deleted ").append(deletedRecords).append(" old history records. ");
            
            // 2. Update database statistics (PostgreSQL specific)
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                session.beginTransaction();
                
                // Analyze tables for better query performance
                String[] tables = {"tasks", "users", "projects", "task_history", "tags"};
                for (String table : tables) {
                    session.createNativeMutationQuery("ANALYZE " + table).executeUpdate();
                }
                
                session.getTransaction().commit();
                summary.append("Updated statistics for database tables. ");
            }
            
            // 3. Report summary
            summary.append("Maintenance completed successfully.");
            
            if (logger.isInfoEnabled()) {
                logger.info("Database maintenance completed: {}", summary);
            }
            return summary.toString();
            
        } catch (Exception e) {
            logger.error("Error during maintenance operations: {}", e.getMessage(), e);
            return "Maintenance failed: " + e.getMessage();
        }
    }
}

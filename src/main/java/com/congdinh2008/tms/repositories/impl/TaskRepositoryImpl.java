package com.congdinh2008.tms.repositories.impl;

import com.congdinh2008.tms.dto.search.TaskSearchCriteria;
import com.congdinh2008.tms.entities.Task;
import com.congdinh2008.tms.enums.TaskPriority;
import com.congdinh2008.tms.enums.TaskStatus;
import com.congdinh2008.tms.exceptions.RepositoryException;
import com.congdinh2008.tms.repositories.TaskRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Repository implementation for Task entity operations
 */
@Repository
public class TaskRepositoryImpl extends BaseRepositoryImpl<Task, Long> implements TaskRepository {
    
    @Override
    public List<Task> findByProject(Long projectId) {
        String correlationId = UUID.randomUUID().toString();
        log.debug("{} - Finding tasks by project ID: {}", correlationId, projectId);
        
        try {
            Session session = getCurrentSession();
            String hql = "FROM Task t WHERE t.project.id = :projectId ORDER BY t.createdAt DESC";
            Query<Task> query = session.createQuery(hql, Task.class);
            query.setParameter("projectId", projectId);
            List<Task> tasks = query.getResultList();
            
            log.debug("{} - Found {} tasks for project ID: {}", correlationId, tasks.size(), projectId);
            return tasks;
        } catch (Exception e) {
            log.error("{} - Error finding tasks by project: {}", correlationId, e.getMessage(), e);
            throw new RepositoryException("Failed to find tasks by project", e);
        }
    }
    
    @Override
    public List<Task> findByAssignee(Long userId) {
        String correlationId = UUID.randomUUID().toString();
        log.debug("{} - Finding tasks by assignee ID: {}", correlationId, userId);
        
        try {
            Session session = getCurrentSession();
            String hql = "FROM Task t WHERE t.assignee.id = :userId ORDER BY t.dueDate ASC";
            Query<Task> query = session.createQuery(hql, Task.class);
            query.setParameter("userId", userId);
            List<Task> tasks = query.getResultList();
            
            log.debug("{} - Found {} tasks for user ID: {}", correlationId, tasks.size(), userId);
            return tasks;
        } catch (Exception e) {
            log.error("{} - Error finding tasks by assignee: {}", correlationId, e.getMessage(), e);
            throw new RepositoryException("Failed to find tasks by assignee", e);
        }
    }
    
    @Override
    public List<Task> findByStatus(TaskStatus status) {
        String correlationId = UUID.randomUUID().toString();
        log.debug("{} - Finding tasks by status: {}", correlationId, status);
        
        try {
            Session session = getCurrentSession();
            String hql = "FROM Task t WHERE t.status = :status ORDER BY t.dueDate ASC";
            Query<Task> query = session.createQuery(hql, Task.class);
            query.setParameter("status", status);
            List<Task> tasks = query.getResultList();
            
            log.debug("{} - Found {} tasks with status: {}", correlationId, tasks.size(), status);
            return tasks;
        } catch (Exception e) {
            log.error("{} - Error finding tasks by status: {}", correlationId, e.getMessage(), e);
            throw new RepositoryException("Failed to find tasks by status", e);
        }
    }
    
    @Override
    public List<Task> findByPriority(TaskPriority priority) {
        String correlationId = UUID.randomUUID().toString();
        log.debug("{} - Finding tasks by priority: {}", correlationId, priority);
        
        try {
            Session session = getCurrentSession();
            String hql = "FROM Task t WHERE t.priority = :priority ORDER BY t.dueDate ASC";
            Query<Task> query = session.createQuery(hql, Task.class);
            query.setParameter("priority", priority);
            List<Task> tasks = query.getResultList();
            
            log.debug("{} - Found {} tasks with priority: {}", correlationId, tasks.size(), priority);
            return tasks;
        } catch (Exception e) {
            log.error("{} - Error finding tasks by priority: {}", correlationId, e.getMessage(), e);
            throw new RepositoryException("Failed to find tasks by priority", e);
        }
    }
    
    @Override
    public List<Task> findOverdueTasks(LocalDateTime referenceDate) {
        String correlationId = UUID.randomUUID().toString();
        log.debug("{} - Finding overdue tasks before: {}", correlationId, referenceDate);
        
        try {
            Session session = getCurrentSession();
            // Native SQL for better performance
            String sql = """
                SELECT t.* FROM tasks t
                WHERE t.due_date < :referenceDate
                  AND t.status != 'DONE'
                ORDER BY t.due_date ASC
                """;
            
            Query<Task> query = session.createNativeQuery(sql, Task.class);
            query.setParameter("referenceDate", referenceDate);
            List<Task> tasks = query.getResultList();
            
            log.debug("{} - Found {} overdue tasks", correlationId, tasks.size());
            return tasks;
        } catch (Exception e) {
            log.error("{} - Error finding overdue tasks: {}", correlationId, e.getMessage(), e);
            throw new RepositoryException("Failed to find overdue tasks", e);
        }
    }
    
    @Override
    public List<Task> findSubTasks(Long parentTaskId) {
        String correlationId = UUID.randomUUID().toString();
        log.debug("{} - Finding subtasks for parent task ID: {}", correlationId, parentTaskId);
        
        try {
            Session session = getCurrentSession();
            String hql = "FROM Task t WHERE t.parentTask.id = :parentTaskId ORDER BY t.createdAt ASC";
            Query<Task> query = session.createQuery(hql, Task.class);
            query.setParameter("parentTaskId", parentTaskId);
            List<Task> subtasks = query.getResultList();
            
            log.debug("{} - Found {} subtasks for parent task ID: {}", 
                     correlationId, subtasks.size(), parentTaskId);
            return subtasks;
        } catch (Exception e) {
            log.error("{} - Error finding subtasks: {}", correlationId, e.getMessage(), e);
            throw new RepositoryException("Failed to find subtasks", e);
        }
    }
    
    @Override
    public boolean isCircularReference(Long taskId, Long parentTaskId) {
        String correlationId = UUID.randomUUID().toString();
        log.debug("{} - Checking circular reference: task {} -> parent {}", 
                 correlationId, taskId, parentTaskId);
        
        try {
            Session session = getCurrentSession();
            // Recursive query to check for circular reference
            String sql = """
                WITH RECURSIVE task_hierarchy AS (
                    -- Base case: start from the proposed parent
                    SELECT id, parent_task_id, 1 AS level
                    FROM tasks
                    WHERE id = :parentTaskId
                    
                    UNION ALL
                    
                    -- Recursive case: follow parent chain
                    SELECT t.id, t.parent_task_id, th.level + 1
                    FROM tasks t
                    INNER JOIN task_hierarchy th ON t.id = th.parent_task_id
                    WHERE th.level < 10 -- Prevent infinite recursion
                )
                SELECT COUNT(*) FROM task_hierarchy
                WHERE id = :taskId
                """;
            
            Query<Number> query = session.createNativeQuery(sql, Number.class);
            query.setParameter("parentTaskId", parentTaskId);
            query.setParameter("taskId", taskId);
            Number count = query.getSingleResult();
            
            boolean isCircular = count.longValue() > 0;
            log.debug("{} - Circular reference check result: {}", correlationId, isCircular);
            return isCircular;
        } catch (Exception e) {
            log.error("{} - Error checking circular reference: {}", correlationId, e.getMessage(), e);
            throw new RepositoryException("Failed to check circular reference", e);
        }
    }
    
    @Override
    public List<Task> searchTasks(TaskSearchCriteria criteria) {
        String correlationId = UUID.randomUUID().toString();
        log.debug("{} - Searching tasks with criteria", correlationId);
        
        try {
            Session session = getCurrentSession();
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Task> cq = cb.createQuery(Task.class);
            Root<Task> task = cq.from(Task.class);
            
            List<Predicate> predicates = new ArrayList<>();
            
            // Keyword search in title and description
            if (criteria.getKeyword() != null && !criteria.getKeyword().trim().isEmpty()) {
                String keyword = "%" + criteria.getKeyword().toLowerCase() + "%";
                Predicate titlePredicate = cb.like(cb.lower(task.get("title")), keyword);
                Predicate descPredicate = cb.like(cb.lower(task.get("description")), keyword);
                predicates.add(cb.or(titlePredicate, descPredicate));
            }
            
            // Status filter
            if (criteria.getStatus() != null) {
                predicates.add(cb.equal(task.get("status"), criteria.getStatus()));
            }
            
            // Priority filter
            if (criteria.getPriority() != null) {
                predicates.add(cb.equal(task.get("priority"), criteria.getPriority()));
            }
            
            // Project filter
            if (criteria.getProjectId() != null) {
                predicates.add(cb.equal(task.get("project").get("id"), criteria.getProjectId()));
            }
            
            // Assignee filter
            if (criteria.getAssigneeId() != null) {
                predicates.add(cb.equal(task.get("assignee").get("id"), criteria.getAssigneeId()));
            }
            
            // Parent task filter
            if (criteria.getParentTaskId() != null) {
                predicates.add(cb.equal(task.get("parentTask").get("id"), criteria.getParentTaskId()));
            }
            
            // Due date range filter
            if (criteria.getDueDateFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(task.get("dueDate"), criteria.getDueDateFrom()));
            }
            if (criteria.getDueDateTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(task.get("dueDate"), criteria.getDueDateTo()));
            }
            
            // Created date range filter
            if (criteria.getCreatedAtFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(task.get("createdAt"), criteria.getCreatedAtFrom()));
            }
            if (criteria.getCreatedAtTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(task.get("createdAt"), criteria.getCreatedAtTo()));
            }
            
            // Has subtasks filter
            if (criteria.getHasSubTasks() != null) {
                if (criteria.getHasSubTasks()) {
                    // Tasks that have subtasks
                    predicates.add(cb.isNotEmpty(task.get("subTasks")));
                } else {
                    // Tasks that don't have subtasks
                    predicates.add(cb.isEmpty(task.get("subTasks")));
                }
            }
            
            // Overdue filter
            if (criteria.getIsOverdue() != null) {
                LocalDateTime now = LocalDateTime.now();
                if (criteria.getIsOverdue()) {
                    // Overdue tasks
                    predicates.add(cb.and(
                        cb.lessThan(task.get("dueDate"), now),
                        cb.notEqual(task.get("status"), TaskStatus.DONE)
                    ));
                } else {
                    // Not overdue tasks
                    predicates.add(cb.or(
                        cb.greaterThanOrEqualTo(task.get("dueDate"), now),
                        cb.equal(task.get("status"), TaskStatus.DONE)
                    ));
                }
            }
            
            // Tag filter
            if (criteria.getTagIds() != null && !criteria.getTagIds().isEmpty()) {
                Join<Object, Object> tagJoin = task.join("tags", JoinType.INNER);
                predicates.add(tagJoin.get("id").in(criteria.getTagIds()));
            }
            
            // Apply all predicates
            if (!predicates.isEmpty()) {
                cq.where(cb.and(predicates.toArray(new Predicate[0])));
            }
            
            // Default ordering by due date
            cq.orderBy(cb.asc(task.get("dueDate")));
            
            Query<Task> query = session.createQuery(cq);
            List<Task> results = query.getResultList();
            
            log.debug("{} - Found {} tasks matching search criteria", correlationId, results.size());
            return results;
        } catch (Exception e) {
            log.error("{} - Error searching tasks: {}", correlationId, e.getMessage(), e);
            throw new RepositoryException("Failed to search tasks", e);
        }
    }
    
    @Override
    public List<Task> findTasksByTag(Long tagId) {
        String correlationId = UUID.randomUUID().toString();
        log.debug("{} - Finding tasks by tag ID: {}", correlationId, tagId);
        
        try {
            Session session = getCurrentSession();
            String hql = """
                SELECT DISTINCT t FROM Task t
                JOIN t.tags tag
                WHERE tag.id = :tagId
                ORDER BY t.dueDate ASC
                """;
            
            Query<Task> query = session.createQuery(hql, Task.class);
            query.setParameter("tagId", tagId);
            List<Task> tasks = query.getResultList();
            
            log.debug("{} - Found {} tasks with tag ID: {}", correlationId, tasks.size(), tagId);
            return tasks;
        } catch (Exception e) {
            log.error("{} - Error finding tasks by tag: {}", correlationId, e.getMessage(), e);
            throw new RepositoryException("Failed to find tasks by tag", e);
        }
    }
    
    @Override
    public boolean canAssignTask(Long taskId, Long userId) {
        String correlationId = UUID.randomUUID().toString();
        log.debug("{} - Checking if task {} can be assigned to user {}", correlationId, taskId, userId);
        
        try {
            Session session = getCurrentSession();
            // Check if user is member of the task's project (Business Rule R3)
            String sql = """
                SELECT COUNT(*) FROM tasks t
                INNER JOIN project_members pm ON t.project_id = pm.project_id
                WHERE t.id = :taskId AND pm.user_id = :userId
                """;
            
            Query<Number> query = session.createNativeQuery(sql, Number.class);
            query.setParameter("taskId", taskId);
            query.setParameter("userId", userId);
            Number count = query.getSingleResult();
            
            boolean canAssign = count.longValue() > 0;
            log.debug("{} - Can assign task {} to user {}: {}", correlationId, taskId, userId, canAssign);
            return canAssign;
        } catch (Exception e) {
            log.error("{} - Error checking task assignment: {}", correlationId, e.getMessage(), e);
            throw new RepositoryException("Failed to check task assignment", e);
        }
    }
    
    @Override
    public List<Task> findTasksDueWithinDays(int days) {
        String correlationId = UUID.randomUUID().toString();
        log.debug("{} - Finding tasks due within {} days", correlationId, days);
        
        try {
            Session session = getCurrentSession();
            LocalDateTime fromDate = LocalDateTime.now();
            LocalDateTime toDate = fromDate.plusDays(days);
            
            String hql = """
                FROM Task t
                WHERE t.dueDate BETWEEN :fromDate AND :toDate
                  AND t.status != :doneStatus
                ORDER BY t.dueDate ASC
                """;
            
            Query<Task> query = session.createQuery(hql, Task.class);
            query.setParameter("fromDate", fromDate);
            query.setParameter("toDate", toDate);
            query.setParameter("doneStatus", TaskStatus.DONE);
            List<Task> tasks = query.getResultList();
            
            log.debug("{} - Found {} tasks due within {} days", correlationId, tasks.size(), days);
            return tasks;
        } catch (Exception e) {
            log.error("{} - Error finding tasks due within days: {}", correlationId, e.getMessage(), e);
            throw new RepositoryException("Failed to find tasks due within days", e);
        }
    }
    
    @Override
    public List<Task> findRootTasks(Long projectId) {
        String correlationId = UUID.randomUUID().toString();
        log.debug("{} - Finding root tasks in project ID: {}", correlationId, projectId);
        
        try {
            Session session = getCurrentSession();
            String hql = """
                FROM Task t
                WHERE t.project.id = :projectId
                  AND t.parentTask IS NULL
                ORDER BY t.createdAt ASC
                """;
            
            Query<Task> query = session.createQuery(hql, Task.class);
            query.setParameter("projectId", projectId);
            List<Task> tasks = query.getResultList();
            
            log.debug("{} - Found {} root tasks in project ID: {}", 
                     correlationId, tasks.size(), projectId);
            return tasks;
        } catch (Exception e) {
            log.error("{} - Error finding root tasks: {}", correlationId, e.getMessage(), e);
            throw new RepositoryException("Failed to find root tasks", e);
        }
    }
}

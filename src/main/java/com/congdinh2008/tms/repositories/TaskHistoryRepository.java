package com.congdinh2008.tms.repositories;

import com.congdinh2008.tms.entities.TaskHistory;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for TaskHistory entity operations
 */
public interface TaskHistoryRepository extends BaseRepository<TaskHistory, Long> {
    
    /**
     * Find task history by task ID ordered by timestamp descending
     * @param taskId the task ID
     * @return list of task history entries for the task
     */
    List<TaskHistory> findByTaskIdOrderByTimestampDesc(Long taskId);
    
    /**
     * Find task history by user ID ordered by timestamp descending
     * @param userId the user ID
     * @return list of task history entries by the user
     */
    List<TaskHistory> findByUserIdOrderByTimestampDesc(Long userId);
    
    /**
     * Find task history by task and user
     * @param taskId the task ID
     * @param userId the user ID
     * @return list of task history entries for the task by the user
     */
    List<TaskHistory> findByTaskIdAndUserId(Long taskId, Long userId);
    
    /**
     * Find task history within date range
     * @param startDate the start date
     * @param endDate the end date
     * @return list of task history entries within the date range
     */
    List<TaskHistory> findByTimestampBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Find recent task history (last N entries)
     * @param limit the maximum number of entries to return
     * @return list of recent task history entries
     */
    List<TaskHistory> findRecentHistory(int limit);
    
    /**
     * Find task history by change type
     * @param changeType the type of change (CREATE, UPDATE, DELETE, etc.)
     * @return list of task history entries of the specified type
     */
    List<TaskHistory> findByChangeType(String changeType);
    
    /**
     * Find task history by multiple tasks
     * @param taskIds list of task IDs
     * @return list of task history entries for the specified tasks
     */
    List<TaskHistory> findByTaskIdIn(List<Long> taskIds);
    
    /**
     * Get user activity statistics
     * @param userId the user ID
     * @param startDate the start date for statistics
     * @param endDate the end date for statistics
     * @return list of activity statistics
     */
    List<Object[]> getUserActivityStatistics(Long userId, LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Get task change frequency
     * @param taskId the task ID
     * @return count of changes for the task
     */
    Long getTaskChangeCount(Long taskId);
    
    /**
     * Delete old history entries before specified date
     * @param beforeDate the cutoff date for deletion
     * @return number of deleted entries
     */
    int deleteOldHistory(LocalDateTime beforeDate);
}

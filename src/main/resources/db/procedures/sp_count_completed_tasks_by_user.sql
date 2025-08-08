-- Stored Procedure: Count completed tasks by user within specified days
-- Purpose: Returns the number of tasks completed by a user in the last N days
-- Parameters:
--   p_user_id: ID of the user to check
--   p_number_of_days: Number of days to look back from current date

CREATE OR REPLACE FUNCTION sp_count_completed_tasks_by_user(
    p_user_id BIGINT,
    p_number_of_days INTEGER
) RETURNS BIGINT
LANGUAGE plpgsql
AS $$
DECLARE
    task_count BIGINT;
    cutoff_date TIMESTAMP;
BEGIN
    -- Calculate cutoff date
    cutoff_date := NOW() - INTERVAL '1 day' * p_number_of_days;
    
    -- Count completed tasks by this user in the time period
    -- Using task_history to track when tasks were marked as DONE
    SELECT COUNT(DISTINCT th.task_id)
    INTO task_count
    FROM task_history th
    JOIN tasks t ON th.task_id = t.id
    WHERE th.changed_by_id = p_user_id
      AND th.field_changed = 'STATUS'
      AND th.new_value = 'DONE'
      AND th.change_date >= cutoff_date;
    
    -- Return 0 if no tasks found
    IF task_count IS NULL THEN
        task_count := 0;
    END IF;
    
    RETURN task_count;
EXCEPTION
    WHEN OTHERS THEN
        -- Log error and return 0 on any exception
        RAISE NOTICE 'Error in sp_count_completed_tasks_by_user: %', SQLERRM;
        RETURN 0;
END;
$$;

-- Grant execute permission
-- GRANT EXECUTE ON FUNCTION sp_count_completed_tasks_by_user(BIGINT, INTEGER) TO tms_user;

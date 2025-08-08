-- Stored Procedure: Get comprehensive project statistics
-- Purpose: Returns detailed statistics for a project including task counts and completion rate
-- Parameters:
--   p_project_id: ID of the project to analyze
-- Returns: Table with total_tasks, completed_tasks, in_progress_tasks, overdue_tasks, completion_rate

CREATE OR REPLACE FUNCTION sp_project_statistics(p_project_id BIGINT)
RETURNS TABLE(
    total_tasks BIGINT,
    completed_tasks BIGINT,
    in_progress_tasks BIGINT,
    overdue_tasks BIGINT,
    completion_rate DECIMAL(5,2)
)
LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY
    SELECT 
        COUNT(*) as total_tasks,
        COUNT(CASE WHEN t.status = 'DONE' THEN 1 END) as completed_tasks,
        COUNT(CASE WHEN t.status = 'IN_PROGRESS' THEN 1 END) as in_progress_tasks,
        COUNT(CASE WHEN t.status != 'DONE' AND t.due_date < NOW() THEN 1 END) as overdue_tasks,
        CASE 
            WHEN COUNT(*) > 0 THEN 
                ROUND((COUNT(CASE WHEN t.status = 'DONE' THEN 1 END) * 100.0 / COUNT(*)), 2)
            ELSE 0 
        END as completion_rate
    FROM tasks t
    WHERE t.project_id = p_project_id;
    
EXCEPTION
    WHEN OTHERS THEN
        -- Return zeros on any exception
        RAISE NOTICE 'Error in sp_project_statistics: %', SQLERRM;
        RETURN QUERY SELECT 0::BIGINT, 0::BIGINT, 0::BIGINT, 0::BIGINT, 0.00::DECIMAL(5,2);
END;
$$;

-- Grant execute permission
-- GRANT EXECUTE ON FUNCTION sp_project_statistics(BIGINT) TO tms_user;

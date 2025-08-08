-- Performance optimization indexes for Task Management System
-- Version: V002
-- Description: Create performance indexes for reporting and analytics

-- Index for task_history table - frequently queried by task_id and change_date
CREATE INDEX IF NOT EXISTS idx_task_history_task_id_date 
ON task_history(task_id, change_date);

-- Index for task_history table - frequently queried by changed_by_id and field_changed
CREATE INDEX IF NOT EXISTS idx_task_history_changed_by_field 
ON task_history(changed_by_id, field_changed);

-- Index for tasks table - frequently queried by status and due_date for overdue tasks
CREATE INDEX IF NOT EXISTS idx_tasks_status_due_date 
ON tasks(status, due_date);

-- Index for tasks table - frequently queried by assignee_id and status
CREATE INDEX IF NOT EXISTS idx_tasks_assignee_status 
ON tasks(assignee_id, status);

-- Index for tasks table - frequently queried by project_id and status for project reports
CREATE INDEX IF NOT EXISTS idx_tasks_project_status 
ON tasks(project_id, status);

-- Index for tasks table - frequently queried by due_date for upcoming tasks
CREATE INDEX IF NOT EXISTS idx_tasks_due_date 
ON tasks(due_date);

-- Index for task_history table - frequently queried by change_date for time-based reports
CREATE INDEX IF NOT EXISTS idx_task_history_change_date 
ON task_history(change_date);

-- Index for tasks table - frequently queried by priority for priority-based filtering
CREATE INDEX IF NOT EXISTS idx_tasks_priority 
ON tasks(priority);

-- Index for user_project junction table - frequently queried for project membership
CREATE INDEX IF NOT EXISTS idx_user_project_user_id 
ON user_project(user_id);

CREATE INDEX IF NOT EXISTS idx_user_project_project_id 
ON user_project(project_id);

-- Index for task_tags junction table - frequently queried for tag relationships
CREATE INDEX IF NOT EXISTS idx_task_tags_task_id 
ON task_tags(task_id);

CREATE INDEX IF NOT EXISTS idx_task_tags_tag_id 
ON task_tags(tag_id);

-- Composite index for tasks table - parent-child relationships
CREATE INDEX IF NOT EXISTS idx_tasks_parent_task_id 
ON tasks(parent_task_id);

-- Index for tasks table - created timestamp for analytics
CREATE INDEX IF NOT EXISTS idx_tasks_created_at 
ON tasks(created_at);

-- Index for users table - frequently queried by email for authentication
-- This might already exist due to unique constraint, but ensure it's optimized
CREATE INDEX IF NOT EXISTS idx_users_email 
ON users(email);

-- Index for projects table - frequently queried by start_date for timeline reports
CREATE INDEX IF NOT EXISTS idx_projects_start_date 
ON projects(start_date);

-- Performance optimization: Update table statistics after index creation
-- This helps PostgreSQL query planner make better decisions
ANALYZE tasks;
ANALYZE task_history;
ANALYZE users;
ANALYZE projects;
ANALYZE tags;
ANALYZE user_project;
ANALYZE task_tags;

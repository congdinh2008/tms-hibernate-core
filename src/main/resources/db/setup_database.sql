-- Database setup script for Task Management System
-- This script should be run as PostgreSQL superuser

-- Create database
CREATE DATABASE task_management_system
    WITH
    OWNER = postgres
    ENCODING = 'UTF8'
    LC_COLLATE = 'en_US.UTF-8'
    LC_CTYPE = 'en_US.UTF-8'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1;

-- Create user
CREATE USER tms_user WITH
    LOGIN
    NOSUPERUSER
    NOCREATEDB
    NOCREATEROLE
    INHERIT
    NOREPLICATION
    CONNECTION LIMIT -1
    PASSWORD 'tms_password';

-- Grant privileges
GRANT ALL PRIVILEGES ON DATABASE task_management_system TO tms_user;

-- Connect to the database
\c task_management_system;

-- Grant schema privileges
GRANT ALL ON SCHEMA public TO tms_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO tms_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO tms_user;

-- Set default privileges for future objects
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO tms_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO tms_user;

-- Create application schema (optional, for better organization)
CREATE SCHEMA IF NOT EXISTS tms;
GRANT ALL ON SCHEMA tms TO tms_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA tms GRANT ALL ON TABLES TO tms_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA tms GRANT ALL ON SEQUENCES TO tms_user;

-- Set search path for the user
ALTER USER tms_user SET search_path = public, tms;

-- Display confirmation
\echo 'Database setup completed successfully!'
\echo 'Database: task_management_system'
\echo 'User: tms_user'
\echo 'Schema: public, tms'

# Database Setup Guide

## PostgreSQL Database Configuration for Task Management System

### Prerequisites
- PostgreSQL 12+ installed and running
- Access to PostgreSQL with superuser privileges

### 1. Database Setup

#### Option A: Using the SQL Script (Recommended)
1. Navigate to the project root directory
2. Run the database setup script:
```bash
psql -U postgres -h localhost -f src/main/resources/db/setup_database.sql
```

#### Option B: Manual Setup
1. Connect to PostgreSQL as superuser:
```bash
psql -U postgres -h localhost
```

2. Create the database:
```sql
CREATE DATABASE task_management_system
    WITH ENCODING = 'UTF8'
    LC_COLLATE = 'en_US.UTF-8' 
    LC_CTYPE = 'en_US.UTF-8';
```

3. Create the user:
```sql
CREATE USER tms_user WITH 
    LOGIN
    PASSWORD 'tms_password';
```

4. Grant privileges:
```sql
GRANT ALL PRIVILEGES ON DATABASE task_management_system TO tms_user;
```

5. Connect to the new database and set additional privileges:
```sql
\c task_management_system;
GRANT ALL ON SCHEMA public TO tms_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO tms_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO tms_user;
```

### 2. Connection Details

After setup, the application will connect using:
- **Host:** localhost
- **Port:** 5432 (default)
- **Database:** task_management_system
- **Username:** tms_user
- **Password:** tms_password

### 3. Verification

To verify the setup:
1. Connect as the application user:
```bash
psql -U tms_user -d task_management_system -h localhost
```

2. Test basic operations:
```sql
-- Should work without errors
CREATE TABLE test_table (id SERIAL PRIMARY KEY, name VARCHAR(50));
DROP TABLE test_table;
```

### 4. Configuration Files

The database connection is configured in:
- `src/main/resources/hibernate.properties` - Main Hibernate configuration
- `src/main/resources/logback.xml` - Logging configuration for SQL statements

### 5. Troubleshooting

#### Connection Issues
- Ensure PostgreSQL is running: `pg_ctl status`
- Check if user exists: `\du` in psql
- Verify database exists: `\l` in psql
- Check pg_hba.conf for authentication settings

#### Permission Issues
- Ensure user has proper privileges on database and schema
- Check default privileges are set correctly
- Verify search_path includes required schemas

#### Common Errors
1. **"database does not exist"** - Run the setup script again
2. **"role does not exist"** - Create the tms_user with proper privileges
3. **"permission denied"** - Grant necessary privileges to tms_user

### 6. Production Considerations

For production environments:
1. Use strong passwords (change from default 'tms_password_2024')
2. Restrict network access (pg_hba.conf)
3. Use SSL connections
4. Regular backups
5. Monitor connection pools and cache performance
6. Consider read replicas for performance

### 7. Development vs Production

The current configuration is optimized for development:
- `hibernate.hbm2ddl.auto=create-drop` will recreate tables on each restart
- SQL logging is enabled for debugging (`hibernate.show_sql=true`)
- Connection pool settings are conservative (10 max connections)
- Statistics generation is enabled for monitoring

For production, consider:
- Change `hibernate.hbm2ddl.auto` to `validate` or `none`
- Disable SQL logging (`hibernate.show_sql=false`)
- Optimize connection pool settings based on load
- Use connection pool and cache monitoring
- Configure appropriate cache TTL values
- Set up database monitoring and alerting

### 8. Hibernate Configuration Features

#### Connection Pooling (HikariCP)
- **Maximum Pool Size**: 10 connections
- **Minimum Idle**: 5 connections
- **Idle Timeout**: 5 minutes
- **Connection Timeout**: 30 seconds

#### Second-Level Cache (EhCache)
- **Entity Cache**: Project and Tag entities (1-hour TTL)
- **Query Cache**: Enabled with 30-minute TTL
- **Cache Provider**: EhCache 3.x with JSR-107 support

#### Performance Optimizations
- **Batch Processing**: Batch size of 20 for inserts/updates
- **Fetch Size**: 50 for JDBC result sets
- **SQL Optimization**: Order inserts/updates enabled
- **Statistics**: Hibernate statistics generation enabled

#### Logging & Monitoring
- **SQL Logging**: All SQL statements logged with formatting
- **MDC Correlation**: Request tracing with correlation IDs
- **Performance Metrics**: Connection pool and cache statistics
- **Error Handling**: Comprehensive error logging and recovery

### 9. Testing

Run configuration validation tests:
```bash
# Test Hibernate configuration without database connection
mvn test -Dtest=HibernateConfigurationTest

# Test SessionFactory creation (requires running PostgreSQL)
mvn test -Dtest=HibernateUtilTest#testSessionFactoryCreation

# Run all tests
mvn test
```

### 10. Troubleshooting

#### HikariCP Connection Issues
**Error**: `ClassNotFoundException: HikariCPConnectionProvider`
**Solution**: Ensure hibernate-hikaricp dependency is included in pom.xml

#### Cache Configuration Issues
**Error**: Issues with EhCache initialization
**Solution**: 
- Verify ehcache.xml is in classpath
- Check hibernate-jcache dependency is included
- Ensure correct JCacheRegionFactory is configured

#### Configuration Validation
```bash
# Check PostgreSQL service
brew services list | grep postgresql  # macOS
sudo systemctl status postgresql      # Linux

# Test database connection
psql -h localhost -p 5432 -U tms_user -d task_management_system

# Verify Hibernate configuration
mvn test -Dtest=HibernateConfigurationTest
```

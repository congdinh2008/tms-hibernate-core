package com.congdinh2008.tms.integration;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.NativeQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;

import com.congdinh2008.tms.config.ApplicationConfig;
import com.congdinh2008.tms.config.TestConfig;
import com.congdinh2008.tms.entities.Project;
import com.congdinh2008.tms.entities.Task;
import com.congdinh2008.tms.entities.User;
import com.congdinh2008.tms.enums.TaskPriority;
import com.congdinh2008.tms.enums.TaskStatus;
import com.congdinh2008.tms.repositories.ProjectRepository;
import com.congdinh2008.tms.repositories.TaskRepository;
import com.congdinh2008.tms.repositories.UserRepository;
import com.congdinh2008.tms.utils.TestDataBuilder;

/**
 * Integration tests for stored procedure-like functionality using native SQL queries
 * Tests complex analytical queries that would typically be implemented as stored procedures
 */
@SpringJUnitConfig
@ContextConfiguration(classes = {ApplicationConfig.class, TestConfig.class})
@ActiveProfiles("test")
@Transactional
@DisplayName("Stored Procedure Integration Tests")
class StoredProcedureIntegrationTest {

    @Autowired
    private SessionFactory sessionFactory;
    
    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ProjectRepository projectRepository;
    
    private User testUser;
    private Project testProject;
    private Task overDueTask;
    private Task completedTask;

    @BeforeEach
    void setUp() {
        // Create test user
        testUser = TestDataBuilder.UserBuilder.aUser()
                .withDefaults()
                .withName("Test User")
                .withEmail("test@example.com")
                .build();
        userRepository.save(testUser);

        // Create test project
        testProject = TestDataBuilder.ProjectBuilder.aProject()
                .withDefaults()
                .withName("Test Project")
                .withStartDate(LocalDate.now().minusDays(30))
                .build();
        
        // Add user to project
        testProject.getMembers().add(testUser);
        projectRepository.save(testProject);

        // Create overdue task
        overDueTask = TestDataBuilder.TaskBuilder.aTask()
                .withDefaults()
                .withTitle("Overdue Task")
                .withProject(testProject)
                .withAssignee(testUser)
                .withStatus(TaskStatus.IN_PROGRESS)
                .withPriority(TaskPriority.HIGH)
                .withDueDate(LocalDate.now().minusDays(5))
                .build();
        taskRepository.save(overDueTask);

        // Create completed task
        completedTask = TestDataBuilder.TaskBuilder.aTask()
                .withDefaults()
                .withTitle("Completed Task")
                .withProject(testProject)
                .withAssignee(testUser)
                .withStatus(TaskStatus.DONE)
                .withPriority(TaskPriority.MEDIUM)
                .withDueDate(LocalDate.now().plusDays(5))
                .build();
        taskRepository.save(completedTask);
    }

    @Test
    @DisplayName("Should execute overdue tasks report stored procedure simulation")
    @Transactional
    void shouldExecuteOverdueTasksReportStoredProcedure() {
        // Simulate stored procedure call using native SQL with PostgreSQL syntax
        Session session = sessionFactory.getCurrentSession();
        String sql = """
            SELECT 
                t.title,
                t.due_date,
                t.priority,
                u.name as assignee_name,
                p.name as project_name,
                (CURRENT_DATE - t.due_date) as days_overdue
            FROM tasks t
            JOIN users u ON t.assignee_id = u.id
            JOIN projects p ON t.project_id = p.id
            WHERE t.due_date < CURRENT_DATE 
            AND t.status != 'DONE'
            ORDER BY t.due_date ASC
            """;
        
        @SuppressWarnings("unchecked")
        NativeQuery<Object[]> query = session.createNativeQuery(sql, Object[].class);
        List<Object[]> results = query.getResultList();
        
        assertThat(results).isNotEmpty();
        assertThat(results).hasSize(1);
        
        Object[] firstResult = results.get(0);
        assertThat(firstResult[0]).isEqualTo("Overdue Task");
        assertThat(firstResult[3]).isEqualTo("Test User");
        assertThat(firstResult[4]).isEqualTo("Test Project");
    }

    @Test
    @DisplayName("Should execute task statistics stored procedure simulation")
    @Transactional
    void shouldExecuteTaskStatisticsStoredProcedure() {
        Session session = sessionFactory.getCurrentSession();
        String sql = """
            SELECT 
                p.name as project_name,
                COUNT(t.id) as total_tasks,
                SUM(CASE WHEN t.status = 'DONE' THEN 1 ELSE 0 END) as completed_tasks,
                SUM(CASE WHEN t.status = 'IN_PROGRESS' THEN 1 ELSE 0 END) as in_progress_tasks,
                SUM(CASE WHEN t.status = 'TODO' THEN 1 ELSE 0 END) as todo_tasks,
                ROUND(
                    (SUM(CASE WHEN t.status = 'DONE' THEN 1 ELSE 0 END) * 100.0) / COUNT(t.id), 
                    2
                ) as completion_percentage
            FROM projects p
            LEFT JOIN tasks t ON p.id = t.project_id
            GROUP BY p.id, p.name
            ORDER BY completion_percentage DESC
            """;
        
        @SuppressWarnings("unchecked")
        NativeQuery<Object[]> query = session.createNativeQuery(sql, Object[].class);
        List<Object[]> results = query.getResultList();
        
        assertThat(results).isNotEmpty();
        
        Object[] projectStats = results.get(0);
        assertThat(projectStats[0]).isEqualTo("Test Project");
        assertThat(projectStats[1]).isEqualTo(2L); // total tasks
        assertThat(projectStats[2]).isEqualTo(1L); // completed tasks
    }

    @Test
    @DisplayName("Should execute user workload analysis stored procedure simulation")
    @Transactional
    void shouldExecuteUserWorkloadAnalysisStoredProcedure() {
        Session session = sessionFactory.getCurrentSession();
        String sql = """
            SELECT 
                u.name as user_name,
                u.email,
                COUNT(t.id) as total_assigned_tasks,
                SUM(CASE WHEN t.status = 'DONE' THEN 1 ELSE 0 END) as completed_tasks,
                SUM(CASE WHEN t.due_date < CURRENT_DATE AND t.status != 'DONE' THEN 1 ELSE 0 END) as overdue_tasks,
                SUM(CASE WHEN t.priority = 'HIGH' THEN 1 ELSE 0 END) as high_priority_tasks
            FROM users u
            LEFT JOIN tasks t ON u.id = t.assignee_id
            GROUP BY u.id, u.name, u.email
            HAVING COUNT(t.id) > 0
            ORDER BY total_assigned_tasks DESC
            """;
        
        @SuppressWarnings("unchecked")
        NativeQuery<Object[]> query = session.createNativeQuery(sql, Object[].class);
        List<Object[]> results = query.getResultList();
        
        assertThat(results).isNotEmpty();
        
        Object[] userWorkload = results.get(0);
        assertThat(userWorkload[0]).isEqualTo("Test User");
        assertThat(userWorkload[1]).isEqualTo("test@example.com");
        assertThat(userWorkload[2]).isEqualTo(2L); // total assigned tasks
    }

    @Test
    @DisplayName("Should execute project completion percentage stored procedure simulation")
    @Transactional
    void shouldExecuteProjectCompletionPercentageStoredProcedure() {
        Session session = sessionFactory.getCurrentSession();
        String sql = """
            SELECT 
                p.name as project_name,
                COUNT(t.id) as total_tasks,
                SUM(CASE WHEN t.status = 'DONE' THEN 1 ELSE 0 END) as completed_tasks,
                CASE 
                    WHEN COUNT(t.id) = 0 THEN 0
                    ELSE ROUND((SUM(CASE WHEN t.status = 'DONE' THEN 1 ELSE 0 END) * 100.0) / COUNT(t.id), 2)
                END as completion_percentage,
                MIN(t.due_date) as earliest_due_date,
                MAX(t.due_date) as latest_due_date
            FROM projects p
            LEFT JOIN tasks t ON p.id = t.project_id
            WHERE p.id = :projectId
            GROUP BY p.id, p.name
            """;
        
        @SuppressWarnings("unchecked")
        NativeQuery<Object[]> query = session.createNativeQuery(sql, Object[].class);
        query.setParameter("projectId", testProject.getId());
        List<Object[]> results = query.getResultList();
        
        assertThat(results).isNotEmpty();
        
        Object[] projectCompletion = results.get(0);
        assertThat(projectCompletion[0]).isEqualTo("Test Project");
        assertThat(projectCompletion[1]).isEqualTo(2L); // total tasks
        assertThat(projectCompletion[2]).isEqualTo(1L); // completed tasks
    }

    @Test
    @DisplayName("Should execute productivity metrics stored procedure simulation")
    @Transactional
    void shouldExecuteProductivityMetricsStoredProcedure() {
        Session session = sessionFactory.getCurrentSession();
        String sql = """
            SELECT 
                CAST(t.created_at AS DATE) as creation_date,
                COUNT(t.id) as tasks_created,
                SUM(CASE WHEN t.status = 'DONE' THEN 1 ELSE 0 END) as tasks_completed,
                AVG(CASE 
                    WHEN t.status = 'DONE' AND t.updated_at > t.created_at 
                    THEN EXTRACT(DAY FROM (t.updated_at - t.created_at))
                    ELSE NULL 
                END) as avg_completion_days
            FROM tasks t
            WHERE t.created_at >= CURRENT_DATE - INTERVAL '30 days'
            GROUP BY CAST(t.created_at AS DATE)
            ORDER BY creation_date DESC
            """;
        
        @SuppressWarnings("unchecked")
        NativeQuery<Object[]> query = session.createNativeQuery(sql, Object[].class);
        List<Object[]> results = query.getResultList();
        
        // At least the test data should be present
        assertThat(results).isNotEmpty();
    }
}

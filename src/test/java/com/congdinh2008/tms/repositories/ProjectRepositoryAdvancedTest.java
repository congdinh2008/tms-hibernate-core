package com.congdinh2008.tms.repositories;

import com.congdinh2008.tms.entities.Project;
import com.congdinh2008.tms.entities.Task;
import com.congdinh2008.tms.entities.User;
import com.congdinh2008.tms.enums.TaskStatus;
import com.congdinh2008.tms.enums.TaskPriority;
import com.congdinh2008.tms.config.ApplicationConfig;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for ProjectRepository business logic
 * Tests advanced features, business rules, and edge cases
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProjectRepositoryAdvancedTest {

    private static AnnotationConfigApplicationContext context;
    private static PlatformTransactionManager transactionManager;
    
    private ProjectRepository projectRepository;
    private TaskRepository taskRepository;
    private UserRepository userRepository;
    
    private TransactionStatus transactionStatus;

    @BeforeAll
    static void setUpClass() {
        context = new AnnotationConfigApplicationContext(ApplicationConfig.class);
        
        try {
            transactionManager = context.getBean(PlatformTransactionManager.class);
        } catch (Exception e) {
            SessionFactory sessionFactory = context.getBean(SessionFactory.class);
            transactionManager = new HibernateTransactionManager(sessionFactory);
        }
    }

    @AfterAll
    static void tearDownClass() {
        if (context != null) {
            context.close();
        }
    }

    @BeforeEach
    void setUp() {
        projectRepository = context.getBean(ProjectRepository.class);
        taskRepository = context.getBean(TaskRepository.class);
        userRepository = context.getBean(UserRepository.class);
        
        transactionStatus = transactionManager.getTransaction(new DefaultTransactionDefinition());
    }

    @AfterEach
    void tearDown() {
        if (transactionStatus != null && !transactionStatus.isCompleted()) {
            transactionManager.rollback(transactionStatus);
        }
    }

    @Test
    @Order(1)
    @DisplayName("Test Business Rule R1 - Project Deletion with Tasks")
    void testBusinessRuleR1_ProjectDeletionWithTasks() {
        // Create project
        Project project = new Project();
        project.setName("Project with Tasks");
        project.setDescription("Test project");
        project.setStartDate(LocalDate.now());
        project = projectRepository.save(project);
        
        // Create incomplete task
        Task task = new Task();
        task.setTitle("Incomplete Task");
        task.setDescription("Task description");
        task.setDueDate(LocalDate.now().plusDays(7));
        task.setProject(project);
        task.setStatus(TaskStatus.TODO);
        task.setPriority(TaskPriority.MEDIUM);
        taskRepository.save(task);
        
        // Test: Project should NOT be deletable
        assertTrue(projectRepository.hasIncompleteTasks(project.getId()));
        assertFalse(projectRepository.canDeleteProject(project.getId()));
        
        // Complete the task - use DONE status
        task.setStatus(TaskStatus.DONE);
        taskRepository.update(task);
        
        // Test: Project should now be deletable
        assertFalse(projectRepository.hasIncompleteTasks(project.getId()));
        assertTrue(projectRepository.canDeleteProject(project.getId()));
    }

    @Test
    @Order(2)
    @DisplayName("Test Project Search by Name")
    void testProjectSearchByName() {
        // Create test projects
        Project project1 = new Project();
        project1.setName("Java Spring Project");
        project1.setDescription("Spring development");
        project1.setStartDate(LocalDate.now());
        projectRepository.save(project1);
        
        Project project2 = new Project();
        project2.setName("React Frontend");
        project2.setDescription("Frontend development");
        project2.setStartDate(LocalDate.now());
        projectRepository.save(project2);
        
        // Test partial name search (case insensitive)
        List<Project> springProjects = projectRepository.findByNameContaining("spring");
        assertEquals(1, springProjects.size());
        assertEquals("Java Spring Project", springProjects.get(0).getName());
        
        List<Project> javaProjects = projectRepository.findByNameContaining("java");
        assertEquals(1, javaProjects.size());
        
        List<Project> allProjects = projectRepository.findByNameContaining("project");
        assertEquals(1, allProjects.size());
    }

    @Test
    @Order(3)
    @DisplayName("Test Projects with Task Statistics")
    void testProjectsWithTaskStatistics() {
        // Create project
        Project project = new Project();
        project.setName("Statistics Project");
        project.setDescription("Test statistics");
        project.setStartDate(LocalDate.now());
        project = projectRepository.save(project);
        
        // Create multiple tasks with different statuses
        Task task1 = new Task();
        task1.setTitle("Completed Task");
        task1.setDescription("Task description");
        task1.setDueDate(LocalDate.now().plusDays(1));
        task1.setProject(project);
        task1.setStatus(TaskStatus.DONE);
        task1.setPriority(TaskPriority.HIGH);
        taskRepository.save(task1);
        
        Task task2 = new Task();
        task2.setTitle("In Progress Task");
        task2.setDescription("Task description");
        task2.setDueDate(LocalDate.now().plusDays(2));
        task2.setProject(project);
        task2.setStatus(TaskStatus.IN_PROGRESS);
        task2.setPriority(TaskPriority.MEDIUM);
        taskRepository.save(task2);
        
        Task task3 = new Task();
        task3.setTitle("Todo Task");
        task3.setDescription("Task description");
        task3.setDueDate(LocalDate.now().plusDays(3));
        task3.setProject(project);
        task3.setStatus(TaskStatus.TODO);
        task3.setPriority(TaskPriority.LOW);
        taskRepository.save(task3);
        
        // Test statistics query
        List<Object[]> stats = projectRepository.findProjectsWithTaskCount();
        assertTrue(stats.size() > 0);
        
        // Find our test project in results
        Object[] projectStats = stats.stream()
            .filter(row -> "Statistics Project".equals(row[1]))
            .findFirst()
            .orElse(null);
        
        assertNotNull(projectStats);
        assertEquals("Statistics Project", projectStats[1]); // name
        assertEquals(3L, ((Number) projectStats[3]).longValue()); // total task count
        assertEquals(1L, ((Number) projectStats[4]).longValue()); // completed count
    }

    @Test
    @Order(4)
    @DisplayName("Test User Project Membership")
    void testUserProjectMembership() {
        // Create user
        User user = new User();
        user.setName("Test User");
        user.setEmail("testuser@example.com");
        user.setPassword("password");
        user = userRepository.save(user);
        
        // Create project and add user as member
        Project project = new Project();
        project.setName("Member Project");
        project.setDescription("Project with members");
        project.setStartDate(LocalDate.now());
        project.getMembers().add(user);
        project = projectRepository.save(project);
        
        // Test: Find projects by user
        List<Project> userProjects = projectRepository.findProjectsByUser(user.getId());
        assertEquals(1, userProjects.size());
        assertEquals("Member Project", userProjects.get(0).getName());
        
        // Test: User not member of other projects
        Project otherProject = new Project();
        otherProject.setName("Other Project");
        otherProject.setDescription("Project without this user");
        otherProject.setStartDate(LocalDate.now());
        projectRepository.save(otherProject);
        
        userProjects = projectRepository.findProjectsByUser(user.getId());
        assertEquals(1, userProjects.size()); // Still only 1 project
    }

    @Test
    @Order(5)
    @DisplayName("Test Active Projects Query")
    void testActiveProjectsQuery() {
        // Create project with active tasks
        Project activeProject = new Project();
        activeProject.setName("Active Project");
        activeProject.setDescription("Has active tasks");
        activeProject.setStartDate(LocalDate.now());
        activeProject = projectRepository.save(activeProject);
        
        // Create active task
        Task activeTask = new Task();
        activeTask.setTitle("Active Task");
        activeTask.setDescription("Task description");
        activeTask.setDueDate(LocalDate.now().plusDays(1));
        activeTask.setProject(activeProject);
        activeTask.setStatus(TaskStatus.IN_PROGRESS);
        activeTask.setPriority(TaskPriority.HIGH);
        taskRepository.save(activeTask);
        
        // Create project with only completed tasks
        Project completedProject = new Project();
        completedProject.setName("Completed Project");
        completedProject.setDescription("All tasks completed");
        completedProject.setStartDate(LocalDate.now());
        completedProject = projectRepository.save(completedProject);
        
        Task completedTask = new Task();
        completedTask.setTitle("Completed Task");
        completedTask.setDescription("Task description");
        completedTask.setDueDate(LocalDate.now().plusDays(1));  // Future date
        completedTask.setProject(completedProject);
        completedTask.setStatus(TaskStatus.DONE);
        completedTask.setPriority(TaskPriority.MEDIUM);
        taskRepository.save(completedTask);
        
        // Test: Only active project should be returned
        List<Project> activeProjects = projectRepository.findActiveProjects();
        assertTrue(activeProjects.size() >= 1);
        assertTrue(activeProjects.stream().anyMatch(p -> "Active Project".equals(p.getName())));
        assertFalse(activeProjects.stream().anyMatch(p -> "Completed Project".equals(p.getName())));
    }

    @Test
    @Order(6)
    @DisplayName("Test Overdue Projects Query")
    void testOverdueProjectsQuery() {
        // Create project with overdue tasks
        Project overdueProject = new Project();
        overdueProject.setName("Overdue Project");
        overdueProject.setDescription("Has overdue tasks");
        overdueProject.setStartDate(LocalDate.now().minusDays(10));
        overdueProject = projectRepository.save(overdueProject);
        
        // Create overdue task - due date after project start but before today
        Task overdueTask = new Task();
        overdueTask.setTitle("Overdue Task");
        overdueTask.setDescription("Task description");
        overdueTask.setDueDate(LocalDate.now().minusDays(5)); // Past due date, but after project start
        overdueTask.setProject(overdueProject);
        overdueTask.setStatus(TaskStatus.TODO); // Not completed
        overdueTask.setPriority(TaskPriority.HIGH);
        taskRepository.save(overdueTask);
        
        // Test: Project should appear in overdue list
        List<Project> overdueProjects = projectRepository.findProjectsWithOverdueTasks();
        assertTrue(overdueProjects.size() >= 1);
        assertTrue(overdueProjects.stream().anyMatch(p -> "Overdue Project".equals(p.getName())));
    }

    @Test
    @Order(7)
    @DisplayName("Test Edge Cases and Error Handling")
    void testEdgeCasesAndErrorHandling() {
        // Test with null/empty parameters
        List<Project> results = projectRepository.findByNameContaining("");
        assertNotNull(results);
        
        // Test with non-existent user ID
        List<Project> userProjects = projectRepository.findProjectsByUser(99999L);
        assertNotNull(userProjects);
        assertTrue(userProjects.isEmpty());
        
        // Test business rule check for non-existent project
        assertFalse(projectRepository.hasIncompleteTasks(99999L));
        assertTrue(projectRepository.canDeleteProject(99999L)); // No tasks = can delete
    }
}

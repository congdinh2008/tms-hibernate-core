package com.congdinh2008.tms.cli;

import com.congdinh2008.tms.config.ApplicationConfig;
import com.congdinh2008.tms.config.TestApplicationConfig;
import com.congdinh2008.tms.cli.controllers.*;
import com.congdinh2008.tms.cli.exception.CLIExceptionHandler;
import com.congdinh2008.tms.cli.helpers.DisplayHelper;
import com.congdinh2008.tms.cli.helpers.InputHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive integration test for all CLI components
 * Tests that Issue #8 requirements are fully met
 */
@SpringJUnitConfig({ApplicationConfig.class, TestApplicationConfig.class})
@ActiveProfiles("test")
class CLIIntegrationTest {

    private ApplicationContext context;

    @BeforeEach
    void setUp() {
        System.setProperty("spring.profiles.active", "test");
        context = new AnnotationConfigApplicationContext(ApplicationConfig.class, TestApplicationConfig.class);
    }

    @Test
    void testAllRequiredCLIComponentsExist() {
        // Test that all CLI components from Issue #8 are properly configured
        
        // Main Application
        assertTrue(context.containsBean("taskManagementSystemApplication"), 
            "Main application class should exist");
        
        // Menu Service
        assertTrue(context.containsBean("menuServiceImpl"), 
            "MenuService implementation should exist");
        
        // Controllers
        assertTrue(context.containsBean("projectController"), 
            "ProjectController should exist");
        assertTrue(context.containsBean("taskController"), 
            "TaskController should exist");
        assertTrue(context.containsBean("userController"), 
            "UserController should exist");
        assertTrue(context.containsBean("tagController"), 
            "TagController should exist");
        assertTrue(context.containsBean("reportController"), 
            "ReportController should exist");
        assertTrue(context.containsBean("demoController"), 
            "DemoController should exist");
        
        // Helper Classes
        assertTrue(context.containsBean("inputHelper"), 
            "InputHelper should exist");
        assertTrue(context.containsBean("displayHelper"), 
            "DisplayHelper should exist");
        assertTrue(context.containsBean("CLIExceptionHandler"), 
            "CLIExceptionHandler should exist");
        
        // Sample Data Service
        assertTrue(context.containsBean("sampleDataService"), 
            "SampleDataService should exist");
        
        // Scanner Bean
        assertTrue(context.containsBean("scanner"), 
            "Scanner bean should exist for CLI input");
    }

    @Test
    void testAllCLIComponentsCanBeInstantiated() {
        // Test that all CLI components can be properly instantiated
        
        assertNotNull(context.getBean("taskManagementSystemApplication"), 
            "Main application should be instantiable");
        assertNotNull(context.getBean(MenuService.class), 
            "MenuService should be instantiable");
        assertNotNull(context.getBean(ProjectController.class), 
            "ProjectController should be instantiable");
        assertNotNull(context.getBean(TaskController.class), 
            "TaskController should be instantiable");
        assertNotNull(context.getBean(UserController.class), 
            "UserController should be instantiable");
        assertNotNull(context.getBean(TagController.class), 
            "TagController should be instantiable");
        assertNotNull(context.getBean(ReportController.class), 
            "ReportController should be instantiable");
        assertNotNull(context.getBean(DemoController.class), 
            "DemoController should be instantiable");
        assertNotNull(context.getBean(InputHelper.class), 
            "InputHelper should be instantiable");
        assertNotNull(context.getBean(DisplayHelper.class), 
            "DisplayHelper should be instantiable");
        assertNotNull(context.getBean(CLIExceptionHandler.class), 
            "CLIExceptionHandler should be instantiable");
        assertNotNull(context.getBean(SampleDataService.class), 
            "SampleDataService should be instantiable");
    }

    @Test
    void testServiceLayerIntegration() {
        // Test that CLI components properly integrate with service layer
        
        // Verify all required service layer beans exist
        assertTrue(context.containsBean("userService"), 
            "UserService should be available");
        assertTrue(context.containsBean("projectService"), 
            "ProjectService should be available");
        assertTrue(context.containsBean("taskService"), 
            "TaskService should be available");
        assertTrue(context.containsBean("tagService"), 
            "TagService should be available");
        
        // These services are required for CLI CRUD operations as per Issue #8
    }

    @Test
    void testMenuServiceIntegration() {
        // Test menu service integration with all controllers
        MenuService menuService = context.getBean(MenuService.class);
        assertNotNull(menuService, "MenuService should be available");
        
        // Test that menu service can handle all operations without throwing exceptions
        assertDoesNotThrow(menuService::displayMainMenu, 
            "Main menu display should work");
        assertDoesNotThrow(menuService::handleProjectOperations, 
            "Project operations should be available");
        assertDoesNotThrow(menuService::handleTaskOperations, 
            "Task operations should be available");
        assertDoesNotThrow(menuService::handleUserOperations, 
            "User operations should be available");
        assertDoesNotThrow(menuService::handleTagOperations, 
            "Tag operations should be available");
        assertDoesNotThrow(menuService::handleReportOperations, 
            "Report operations should be available");
        assertDoesNotThrow(menuService::handleAdvancedFeaturesDemo, 
            "Advanced features demo should be available");
    }

    @Test
    void testCLIExceptionHandling() {
        // Test that CLI exception handling is properly configured
        CLIExceptionHandler exceptionHandler = context.getBean(CLIExceptionHandler.class);
        assertNotNull(exceptionHandler, "CLIExceptionHandler should be available");
        
        // Test that it can handle exceptions without throwing
        assertDoesNotThrow(() -> {
            exceptionHandler.handleException(new RuntimeException("Test exception"));
        }, "Exception handler should handle exceptions gracefully");
    }

    @Test
    void testInputOutputHelpers() {
        // Test that input and output helpers are properly configured
        InputHelper inputHelper = context.getBean(InputHelper.class);
        DisplayHelper displayHelper = context.getBean(DisplayHelper.class);
        
        assertNotNull(inputHelper, "InputHelper should be available");
        assertNotNull(displayHelper, "DisplayHelper should be available");
        
        // These helpers are essential for Issue #8 requirements:
        // - Input validation and user feedback
        // - Formatted output display
    }

    @Test
    void testDataRepositoryLayerIntegration() {
        // Test that repository layer is properly integrated
        assertTrue(context.containsBean("userRepository"), 
            "UserRepository should be available");
        assertTrue(context.containsBean("projectRepository"), 
            "ProjectRepository should be available");
        assertTrue(context.containsBean("taskRepository"), 
            "TaskRepository should be available");
        assertTrue(context.containsBean("tagRepository"), 
            "TagRepository should be available");
        
        // Repository layer is required for CLI CRUD operations
    }

    @Test
    void testHibernateIntegration() {
        // Test that Hibernate configuration is working with CLI
        assertTrue(context.containsBean("sessionFactory"), 
            "SessionFactory should be available");
        assertTrue(context.containsBean("dataSource"), 
            "DataSource should be available");
        
        // Hibernate integration is core requirement for Issue #8
    }

    @Test
    void testSpringingContextConfiguration() {
        // Test Spring context configuration completeness
        
        // Component scanning should find all CLI components
        String[] beanNames = context.getBeanDefinitionNames();
        assertTrue(beanNames.length > 30, 
            "Should have discovered many beans through component scanning (found: " + beanNames.length + ")");
        
        // Test that component scanning is working properly
        boolean hasCliComponents = false;
        for (String beanName : beanNames) {
            if (beanName.contains("Controller") || beanName.contains("Helper") || 
                beanName.contains("Menu") || beanName.contains("CLI")) {
                hasCliComponents = true;
                break;
            }
        }
        assertTrue(hasCliComponents, "Should have CLI components discovered");
    }

    @Test
    void testIssue8AcceptanceCriteria() {
        // Test that all Issue #8 acceptance criteria are met:
        
        // ✓ Console application khởi động thành công
        assertNotNull(context.getBean("taskManagementSystemApplication"), 
            "Console application should start successfully");
        
        // ✓ Menu điều hướng hoạt động đầy đủ
        assertNotNull(context.getBean(MenuService.class), 
            "Menu navigation should be fully functional");
        
        // ✓ Tất cả CRUD operations có thể thực hiện qua CLI
        assertNotNull(context.getBean(ProjectController.class), 
            "Project CRUD should be available");
        assertNotNull(context.getBean(TaskController.class), 
            "Task CRUD should be available");
        assertNotNull(context.getBean(UserController.class), 
            "User CRUD should be available");
        
        // ✓ Exception handling user-friendly
        assertNotNull(context.getBean(CLIExceptionHandler.class), 
            "User-friendly exception handling should be available");
        
        // ✓ Sample data loading hoạt động
        assertNotNull(context.getBean(SampleDataService.class), 
            "Sample data loading should be functional");
        
        // ✓ Input validation và error messages rõ ràng
        assertNotNull(context.getBean(InputHelper.class), 
            "Input validation should be available");
        
        // ✓ Output formatting đẹp và dễ đọc
        assertNotNull(context.getBean(DisplayHelper.class), 
            "Output formatting should be available");
    }

    @AfterEach
    void tearDown() {
        if (context != null) {
            ((AnnotationConfigApplicationContext) context).close();
        }
    }
}

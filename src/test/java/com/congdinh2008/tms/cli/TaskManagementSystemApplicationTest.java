package com.congdinh2008.tms.cli;

import com.congdinh2008.tms.TaskManagementSystemApplication;
import com.congdinh2008.tms.config.ApplicationConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test class for TaskManagementSystemApplication CLI
 * Tests the CLI application startup and basic functionality
 */
@SpringJUnitConfig(ApplicationConfig.class)
class TaskManagementSystemApplicationTest {

    private ApplicationContext context;
    private ByteArrayOutputStream outputStream;
    private PrintStream originalOut;

    @BeforeEach
    void setUp() {
        // Capture console output
        outputStream = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
        
        // Initialize Spring context
        context = new AnnotationConfigApplicationContext(ApplicationConfig.class);
    }

    @Test
    void testApplicationContextLoads() {
        // Test that Spring context loads successfully
        assertNotNull(context, "Application context should not be null");
        assertTrue(context.containsBean("taskManagementSystemApplication"), 
            "Application bean should be present in context");
    }

    @Test
    void testTaskManagementSystemApplicationBeanCreation() {
        // Test that the main application bean can be created
        TaskManagementSystemApplication app = context.getBean(TaskManagementSystemApplication.class);
        assertNotNull(app, "TaskManagementSystemApplication bean should not be null");
    }

    @Test
    void testMenuServiceBeanExists() {
        // Test that MenuService bean exists
        assertTrue(context.containsBean("menuServiceImpl"), 
            "MenuService implementation should be present");
        MenuService menuService = context.getBean(MenuService.class);
        assertNotNull(menuService, "MenuService should not be null");
    }

    @Test
    void testSampleDataServiceBeanExists() {
        // Test that SampleDataService bean exists
        assertTrue(context.containsBean("sampleDataService"), 
            "SampleDataService should be present");
        SampleDataService sampleDataService = context.getBean(SampleDataService.class);
        assertNotNull(sampleDataService, "SampleDataService should not be null");
    }

    @Test
    void testInputHelperBeanExists() {
        // Test that InputHelper bean exists
        assertTrue(context.containsBean("inputHelper"), 
            "InputHelper should be present");
    }

    @Test
    void testDisplayHelperBeanExists() {
        // Test that DisplayHelper bean exists
        assertTrue(context.containsBean("displayHelper"), 
            "DisplayHelper should be present");
    }

    @Test
    void testCLIExceptionHandlerBeanExists() {
        // Test that CLIExceptionHandler bean exists
        assertTrue(context.containsBean("CLIExceptionHandler"), 
            "CLIExceptionHandler should be present");
    }

    @Test
    void testAllControllerBeansExist() {
        // Test that all CLI controller beans exist
        assertTrue(context.containsBean("projectController"), 
            "ProjectController should be present");
        assertTrue(context.containsBean("taskController"), 
            "TaskController should be present");
        assertTrue(context.containsBean("userController"), 
            "UserController should be present");
        assertTrue(context.containsBean("tagController"), 
            "TagController should be present");
        assertTrue(context.containsBean("reportController"), 
            "ReportController should be present");
        assertTrue(context.containsBean("demoController"), 
            "DemoController should be present");
    }

    @Test
    void testMenuServiceDisplayMainMenu() {
        // Test that menu service can display main menu without errors
        MenuService menuService = context.getBean(MenuService.class);
        assertDoesNotThrow(menuService::displayMainMenu, 
            "Displaying main menu should not throw exception");
        
        String output = outputStream.toString();
        assertFalse(output.isEmpty(), "Menu display should produce output");
        assertTrue(output.contains("TASK MANAGEMENT SYSTEM"), 
            "Menu should contain system title");
    }

    @Test
    void testApplicationConfigurationIsValid() {
        // Test that all required beans are properly configured
        String[] expectedBeans = {
            "taskManagementSystemApplication",
            "menuServiceImpl", 
            "sampleDataService",
            "inputHelper",
            "displayHelper",
            "CLIExceptionHandler",
            "projectController",
            "taskController", 
            "userController",
            "tagController",
            "reportController",
            "demoController"
        };
        
        for (String beanName : expectedBeans) {
            assertTrue(context.containsBean(beanName), 
                "Bean '" + beanName + "' should be present in context");
        }
    }

    @Test
    void testScannerBeanExists() {
        // Test that Scanner bean is configured
        assertTrue(context.containsBean("scanner"), 
            "Scanner bean should be present for CLI input");
    }

    @Test
    void testServiceLayerBeansExist() {
        // Test that service layer beans exist for CLI operations
        assertTrue(context.containsBean("userService"), 
            "UserService should be present");
        assertTrue(context.containsBean("projectService"), 
            "ProjectService should be present");
        assertTrue(context.containsBean("taskService"), 
            "TaskService should be present");
        assertTrue(context.containsBean("tagService"), 
            "TagService should be present");
    }

    @Test
    void testMainMethodDoesNotCrash() {
        // Test that main method setup doesn't crash
        assertDoesNotThrow(() -> {
            AnnotationConfigApplicationContext testContext = new AnnotationConfigApplicationContext(ApplicationConfig.class);
            TaskManagementSystemApplication app = testContext.getBean(TaskManagementSystemApplication.class);
            assertNotNull(app, "Application should be created successfully");
            testContext.close();
        }, "Application context creation should not throw exception");
    }

    @AfterEach
    void tearDown() {
        if (originalOut != null) {
            System.setOut(originalOut);
        }
        if (context != null) {
            ((AnnotationConfigApplicationContext) context).close();
        }
    }
}

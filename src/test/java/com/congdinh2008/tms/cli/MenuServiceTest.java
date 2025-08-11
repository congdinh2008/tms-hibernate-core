package com.congdinh2008.tms.cli;

import com.congdinh2008.tms.cli.impl.MenuServiceImpl;
import com.congdinh2008.tms.config.ApplicationConfig;
import com.congdinh2008.tms.config.TestApplicationConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for MenuService and MenuServiceImpl
 * Tests menu display functionality and navigation
 */
@SpringJUnitConfig({ApplicationConfig.class, TestApplicationConfig.class})
@ActiveProfiles("test")
class MenuServiceTest {

    private ApplicationContext context;
    private MenuService menuService;
    private ByteArrayOutputStream outputStream;
    private PrintStream originalOut;

    @BeforeEach
    void setUp() {
        // Capture console output
        outputStream = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
        
        // Initialize Spring context with test profile and get menu service
        System.setProperty("spring.profiles.active", "test");
        context = new AnnotationConfigApplicationContext(ApplicationConfig.class, TestApplicationConfig.class);
        menuService = context.getBean(MenuService.class);
    }

    @Test
    void testMenuServiceImplementationExists() {
        // Test that MenuServiceImpl is properly configured
        assertNotNull(menuService, "MenuService should not be null");
        assertTrue(menuService instanceof MenuServiceImpl, 
            "MenuService should be instance of MenuServiceImpl");
    }

    @Test
    void testDisplayMainMenu() {
        // Test main menu display
        assertDoesNotThrow(menuService::displayMainMenu, 
            "displayMainMenu should not throw exception");
        
        String output = outputStream.toString();
        assertFalse(output.isEmpty(), "Main menu should produce output");
        
        // Check for required menu items mentioned in Issue #8
        assertTrue(output.contains("TASK MANAGEMENT SYSTEM"), 
            "Should contain system title");
        assertTrue(output.contains("Quản lý Dự án") || output.contains("Projects"), 
            "Should contain project management option");
        assertTrue(output.contains("Quản lý Công việc") || output.contains("Tasks"), 
            "Should contain task management option");
        assertTrue(output.contains("Quản lý Người dùng") || output.contains("Users"), 
            "Should contain user management option");
        assertTrue(output.contains("Tags") || output.contains("Thẻ"), 
            "Should contain tag management option");
        assertTrue(output.contains("Báo cáo") || output.contains("Report"), 
            "Should contain reports option");
        assertTrue(output.contains("Demo") || output.contains("nâng cao"), 
            "Should contain advanced features demo option");
        assertTrue(output.contains("Thoát") || output.contains("Exit"), 
            "Should contain exit option");
    }

    @Test
    void testDisplayMainMenuFormatting() {
        // Test that main menu has proper formatting
        menuService.displayMainMenu();
        String output = outputStream.toString();
        
        // Check for proper formatting elements
        assertTrue(output.contains("=") || output.contains("-") || output.contains("─"), 
            "Menu should have decorative borders");
        assertTrue(output.contains("1.") || output.contains("2."), 
            "Menu should have numbered options");
        assertTrue(output.contains("0."), 
            "Menu should have exit option numbered as 0");
    }

    @Test
    void testHandleProjectOperations() {
        // Test project operations handling
        assertDoesNotThrow(menuService::handleProjectOperations, 
            "handleProjectOperations should not throw exception");
    }

    @Test
    void testHandleTaskOperations() {
        // Test task operations handling
        assertDoesNotThrow(menuService::handleTaskOperations, 
            "handleTaskOperations should not throw exception");
    }

    @Test
    void testHandleUserOperations() {
        // Test user operations handling
        assertDoesNotThrow(menuService::handleUserOperations, 
            "handleUserOperations should not throw exception");
    }

    @Test
    void testHandleTagOperations() {
        // Test tag operations handling
        assertDoesNotThrow(menuService::handleTagOperations, 
            "handleTagOperations should not throw exception");
    }

    @Test
    void testHandleReportOperations() {
        // Test report operations handling
        assertDoesNotThrow(menuService::handleReportOperations, 
            "handleReportOperations should not throw exception");
    }

    @Test
    void testHandleAdvancedFeaturesDemo() {
        // Test advanced features demo handling
        assertDoesNotThrow(menuService::handleAdvancedFeaturesDemo, 
            "handleAdvancedFeaturesDemo should not throw exception");
    }

    @Test
    void testAllMenuHandlersExist() {
        // Verify all required menu handlers are implemented
        // This test ensures Issue #8 requirements are met
        
        // Test that all handle methods exist and can be called
        assertDoesNotThrow(() -> {
            menuService.handleProjectOperations();
            menuService.handleTaskOperations();
            menuService.handleUserOperations();
            menuService.handleTagOperations();
            menuService.handleReportOperations();
            menuService.handleAdvancedFeaturesDemo();
        }, "All menu handlers should be implemented and not throw exceptions");
    }

    @Test
    void testMenuServiceIsSingleton() {
        // Test that MenuService is properly configured as singleton
        MenuService menuService1 = context.getBean(MenuService.class);
        MenuService menuService2 = context.getBean(MenuService.class);
        assertSame(menuService1, menuService2, 
            "MenuService should be singleton");
    }

    @Test
    void testVietnameseLanguageSupport() {
        // Test that Vietnamese language is properly supported
        menuService.displayMainMenu();
        String output = outputStream.toString();
        
        // Check for Vietnamese characters and text
        assertTrue(output.contains("Quản lý") || output.contains("Dự án") || 
                   output.contains("Công việc") || output.contains("Người dùng"), 
            "Menu should support Vietnamese language");
    }

    @Test
    void testEmojiSupport() {
        // Test that emoji characters are supported for better UX
        menuService.displayMainMenu();
        String output = outputStream.toString();
        
        // Check for emoji presence (Issue #8 mentions emoji-enhanced options)
        assertTrue(output.contains("📋") || output.contains("👥") || 
                   output.contains("🏷️") || output.contains("📊") || 
                   output.contains("🚀") || output.contains("🚪"), 
            "Menu should include emoji for better user experience");
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

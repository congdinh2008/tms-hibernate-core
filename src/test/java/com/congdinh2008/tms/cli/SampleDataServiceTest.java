package com.congdinh2008.tms.cli;

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
 * Test class for SampleDataService
 * Tests sample data loading functionality
 */
@SpringJUnitConfig(ApplicationConfig.class)
class SampleDataServiceTest {

    private ApplicationContext context;
    private SampleDataService sampleDataService;
    private ByteArrayOutputStream outputStream;
    private PrintStream originalOut;

    @BeforeEach
    void setUp() {
        // Capture console output
        outputStream = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
        
        // Initialize Spring context and get sample data service
        context = new AnnotationConfigApplicationContext(ApplicationConfig.class);
        sampleDataService = context.getBean(SampleDataService.class);
    }

    @Test
    void testSampleDataServiceExists() {
        // Test that SampleDataService is properly configured
        assertNotNull(sampleDataService, "SampleDataService should not be null");
    }

    @Test
    void testSampleDataServiceIsComponent() {
        // Test that SampleDataService is registered as Spring component
        assertTrue(context.containsBean("sampleDataService"), 
            "SampleDataService should be registered as Spring component");
    }

    @Test
    void testSampleDataServiceIsSingleton() {
        // Test that SampleDataService is properly configured as singleton
        SampleDataService service1 = context.getBean(SampleDataService.class);
        SampleDataService service2 = context.getBean(SampleDataService.class);
        assertSame(service1, service2, "SampleDataService should be singleton");
    }

    @Test
    void testLoadSampleDataDoesNotThrow() {
        // Test that sample data loading doesn't throw exceptions
        assertDoesNotThrow(sampleDataService::loadSampleData, 
            "loadSampleData should not throw exception");
    }

    @Test
    void testLoadSampleDataProducesOutput() {
        // Test that sample data loading produces console output
        sampleDataService.loadSampleData();
        
        String output = outputStream.toString();
        assertFalse(output.isEmpty(), "Sample data loading should produce output");
        
        // Check for expected output messages from Issue #8
        assertTrue(output.contains("dữ liệu mẫu") || output.contains("sample data") || 
                   output.contains("Loading") || output.contains("Đang tải"), 
            "Should contain sample data loading message");
    }

    @Test
    void testSampleDataServiceHasRequiredDependencies() {
        // Test that SampleDataService has all required service dependencies
        assertNotNull(sampleDataService, "SampleDataService should be properly instantiated");
        
        // Verify required service beans exist for sample data creation
        assertTrue(context.containsBean("userService"), 
            "UserService should be available for sample data");
        assertTrue(context.containsBean("projectService"), 
            "ProjectService should be available for sample data");
        assertTrue(context.containsBean("taskService"), 
            "TaskService should be available for sample data");
        assertTrue(context.containsBean("tagService"), 
            "TagService should be available for sample data");
    }

    @Test
    void testSampleDataCreatesRealisticData() {
        // Test that sample data service creates realistic test data
        // As per Issue #8 requirement for "realistic test data for demonstrations"
        
        assertDoesNotThrow(() -> {
            sampleDataService.loadSampleData();
            
            String output = outputStream.toString();
            // Should indicate completion
            assertTrue(output.contains("✅") || output.contains("success") || 
                       output.contains("hoàn thành") || output.contains("xong"), 
                "Should indicate successful completion");
        }, "Sample data creation should complete successfully");
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

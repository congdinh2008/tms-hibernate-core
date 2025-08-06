package com.congdinh2008.tms;

import com.congdinh2008.tms.config.ApplicationConfig;
import com.congdinh2008.tms.service.HibernateService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the main Application
 * Validates the Spring IoC container integration
 */
class ApplicationTest {

    @Test
    @DisplayName("Should demonstrate Spring IoC container usage")
    void testSpringIoCIntegration() {
        // Test Spring ApplicationContext loading and bean retrieval
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ApplicationConfig.class)) {
            assertNotNull(context, "Spring context should be created successfully");
            
            // Test HibernateService bean injection
            HibernateService hibernateService = context.getBean(HibernateService.class);
            assertNotNull(hibernateService, "HibernateService should be injected by Spring IoC");
            assertTrue(hibernateService.isSessionFactoryOpen(), 
                    "SessionFactory should be properly initialized via dependency injection");
            
            // Test statistics access (demonstrates that all dependencies are properly wired)
            assertDoesNotThrow(hibernateService::logConnectionPoolStats, 
                    "Should be able to access Hibernate statistics without errors");
        }
    }

    @Test
    @DisplayName("Should handle application lifecycle correctly")
    void testApplicationLifecycle() {
        HibernateService hibernateService;
        
        // Test application startup with Spring IoC
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ApplicationConfig.class)) {
            hibernateService = context.getBean(HibernateService.class);
            
            // Verify proper initialization
            assertTrue(hibernateService.isSessionFactoryOpen(), 
                    "HibernateService should be properly initialized during application startup");
        }
        
        // After context is closed, SessionFactory should be cleaned up
        assertFalse(hibernateService.isSessionFactoryOpen(), 
                "HibernateService should be properly cleaned up during application shutdown");
    }

    @Test
    @DisplayName("Should validate dependency injection chain")
    void testDependencyInjectionChain() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ApplicationConfig.class)) {
            // Verify all required beans are present
            assertTrue(context.containsBean("dataSource"), "DataSource bean should be registered");
            assertTrue(context.containsBean("sessionFactory"), "SessionFactory bean should be registered");
            assertTrue(context.containsBean("hibernateService"), "HibernateService bean should be registered");
            
            // Verify dependency injection works correctly
            HibernateService hibernateService = context.getBean(HibernateService.class);
            assertNotNull(hibernateService.getSessionFactory(), 
                    "SessionFactory should be injected into HibernateService");
            
            // Verify the injected SessionFactory is the same bean from the context
            assertSame(hibernateService.getSessionFactory(), context.getBean("sessionFactory"),
                    "Injected SessionFactory should be the same instance as the Spring bean");
        }
    }

    @Test
    @DisplayName("Test application main method execution")
    void testApplicationMainMethod() {
        // Test that main method runs without throwing exceptions
        assertDoesNotThrow(() -> {
            Application.main(new String[]{});
        });
    }
}

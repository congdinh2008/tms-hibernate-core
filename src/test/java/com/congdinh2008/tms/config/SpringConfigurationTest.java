package com.congdinh2008.tms.config;

import com.congdinh2008.tms.service.HibernateService;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for Spring IoC container configuration
 * Validates Spring beans configuration and dependency injection
 */
class SpringConfigurationTest {

    @Test
    @DisplayName("Should load Spring application context successfully")
    void testSpringContextLoading() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ApplicationConfig.class)) {
            assertNotNull(context, "Spring context should be loaded");
            assertTrue(context.isActive(), "Spring context should be active");
            
            // Verify essential beans are registered
            String[] beanNames = context.getBeanDefinitionNames();
            assertTrue(beanNames.length > 0, "Spring context should have registered beans");
        }
    }

    @Test
    @DisplayName("Should configure DataSource bean correctly")
    void testDataSourceConfiguration() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ApplicationConfig.class)) {
            DataSource dataSource = context.getBean("dataSource", DataSource.class);
            
            assertNotNull(dataSource, "DataSource bean should be configured");
            assertEquals("com.zaxxer.hikari.HikariDataSource", dataSource.getClass().getName(),
                    "DataSource should be HikariDataSource");
        }
    }

    @Test
    @DisplayName("Should configure SessionFactory bean correctly")
    void testSessionFactoryConfiguration() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ApplicationConfig.class)) {
            SessionFactory sessionFactory = context.getBean("sessionFactory", SessionFactory.class);
            
            assertNotNull(sessionFactory, "SessionFactory bean should be configured");
            assertFalse(sessionFactory.isClosed(), "SessionFactory should be open");
        }
    }

    @Test
    @DisplayName("Should configure HibernateService with dependency injection")
    void testHibernateServiceConfiguration() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ApplicationConfig.class)) {
            HibernateService hibernateService = context.getBean(HibernateService.class);
            
            assertNotNull(hibernateService, "HibernateService bean should be configured");
            assertTrue(hibernateService.isSessionFactoryOpen(), 
                    "HibernateService should have SessionFactory injected and open");
        }
    }

    @Test
    @DisplayName("Should handle bean dependencies correctly")
    void testBeanDependencies() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ApplicationConfig.class)) {
            // Verify dependency chain: DataSource -> SessionFactory -> HibernateService
            DataSource dataSource = context.getBean("dataSource", DataSource.class);
            SessionFactory sessionFactory = context.getBean("sessionFactory", SessionFactory.class);
            HibernateService hibernateService = context.getBean(HibernateService.class);
            
            assertNotNull(dataSource, "DataSource should be available");
            assertNotNull(sessionFactory, "SessionFactory should be available");
            assertNotNull(hibernateService, "HibernateService should be available");
            
            // Verify SessionFactory is properly configured with DataSource
            assertFalse(sessionFactory.isClosed(), "SessionFactory should be properly initialized");
            
            // Verify HibernateService has SessionFactory injected
            assertEquals(sessionFactory, hibernateService.getSessionFactory(),
                    "HibernateService should have the same SessionFactory instance");
        }
    }

    @Test
    @DisplayName("Should create singleton beans")
    void testSingletonBeans() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ApplicationConfig.class)) {
            // Get beans multiple times and verify they are the same instances
            DataSource dataSource1 = context.getBean("dataSource", DataSource.class);
            DataSource dataSource2 = context.getBean("dataSource", DataSource.class);
            assertSame(dataSource1, dataSource2, "DataSource should be singleton");

            SessionFactory sessionFactory1 = context.getBean("sessionFactory", SessionFactory.class);
            SessionFactory sessionFactory2 = context.getBean("sessionFactory", SessionFactory.class);
            assertSame(sessionFactory1, sessionFactory2, "SessionFactory should be singleton");

            HibernateService hibernateService1 = context.getBean(HibernateService.class);
            HibernateService hibernateService2 = context.getBean(HibernateService.class);
            assertSame(hibernateService1, hibernateService2, "HibernateService should be singleton");
        }
    }

    @Test
    @DisplayName("Should validate bean lifecycle management")
    void testBeanLifecycle() {
        HibernateService hibernateService;
        SessionFactory sessionFactory;
        
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ApplicationConfig.class)) {
            hibernateService = context.getBean(HibernateService.class);
            sessionFactory = context.getBean("sessionFactory", SessionFactory.class);
            
            // Beans should be active when context is open
            assertTrue(hibernateService.isSessionFactoryOpen(), 
                    "HibernateService should be active when context is open");
            assertFalse(sessionFactory.isClosed(), 
                    "SessionFactory should be open when context is active");
        }
        
        // SessionFactory should be closed when context is destroyed
        assertTrue(sessionFactory.isClosed(), 
                "SessionFactory should be closed when context is destroyed");
    }

    @Test
    @DisplayName("Should handle component scanning correctly")
    void testComponentScanning() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ApplicationConfig.class)) {
            // Verify that @Service annotation is picked up by component scanning
            assertTrue(context.containsBean("hibernateService"), 
                    "HibernateService should be found by component scanning");
            
            // Verify bean is properly configured
            HibernateService hibernateService = context.getBean("hibernateService", HibernateService.class);
            assertNotNull(hibernateService, "HibernateService bean should be available");
        }
    }
}

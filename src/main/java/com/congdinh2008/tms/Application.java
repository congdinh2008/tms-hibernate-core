package com.congdinh2008.tms;

import com.congdinh2008.tms.config.ApplicationConfig;
import com.congdinh2008.tms.service.HibernateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Main application class for Task Management System
 * Now uses Spring IoC container for dependency management
 * 
 * @author congdinh2008
 * @since 1.0.0
 */
public class Application {
    
    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    /**
     * Main method - entry point of the application
     * 
     * @param args command line arguments
     */
    public static void main(String[] args) {
        logger.info("Starting Task Management System with Spring IoC...");
        
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ApplicationConfig.class)) {
            
            // Display application information
            System.out.println("Task Management System - Version 1.0.0-SNAPSHOT");
            System.out.println("Java Version: " + System.getProperty("java.version"));
            System.out.println("Spring IoC Container: " + context.getClass().getSimpleName());
            
            // Demonstrate Spring IoC usage
            demonstrateSpringIoC(context);
            
            logger.info("Application started successfully with Spring IoC");
            
        } catch (Exception e) {
            logger.error("Application failed to start: {}", e.getMessage(), e);
            System.exit(1);
        }
    }
    
    /**
     * Demonstrates Spring IoC container usage
     * 
     * @param context the Spring application context
     */
    private static void demonstrateSpringIoC(ApplicationContext context) {
        logger.info("Demonstrating Spring IoC container...");
        
        // Get HibernateService bean from Spring container
        HibernateService hibernateService = context.getBean(HibernateService.class);
        
        // Check if SessionFactory is properly injected and initialized
        if (hibernateService.isSessionFactoryOpen()) {
            logger.info("SessionFactory is open and ready for use");
            hibernateService.logConnectionPoolStats();
        } else {
            logger.warn("SessionFactory is not open");
        }
        
        // Display bean information
        String[] beanNames = context.getBeanDefinitionNames();
        logger.info("Spring IoC container initialized with {} beans", beanNames.length);
        
        logger.debug("Registered beans:");
        for (String beanName : beanNames) {
            Object bean = context.getBean(beanName);
            logger.debug("- {}: {}", beanName, bean.getClass().getSimpleName());
        }
    }
}

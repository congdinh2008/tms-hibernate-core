package com.congdinh2008.tms.config;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.UUID;

/**
 * Hibernate Utility class for managing SessionFactory
 * Implements Singleton pattern for SessionFactory management
 */
public class HibernateUtil {
    
    private static final Logger logger = LoggerFactory.getLogger(HibernateUtil.class);
    private static final String CORRELATION_ID = "correlationId";
    private static final String HIBERNATE_PROPERTIES_FILE = "hibernate.properties";
    
    private static SessionFactory sessionFactory;
    private static ServiceRegistry serviceRegistry;
    
    // Private constructor to prevent instantiation
    private HibernateUtil() {}
    
    /**
     * Get SessionFactory instance using Singleton pattern
     * 
     * @return SessionFactory instance
     * @throws HibernateConfigurationException if SessionFactory cannot be created
     */
    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            synchronized (HibernateUtil.class) {
                if (sessionFactory == null) {
                    buildSessionFactory();
                }
            }
        }
        return sessionFactory;
    }
    
    /**
     * Build SessionFactory from hibernate.properties file
     * 
     * @throws HibernateConfigurationException if configuration fails
     */
    private static void buildSessionFactory() {
        String correlationId = UUID.randomUUID().toString();
        MDC.put(CORRELATION_ID, correlationId);
        
        try {
            logger.info("Starting Hibernate SessionFactory initialization...");
            
            // Load hibernate.properties
            Properties hibernateProperties = loadHibernateProperties();
            
            // Create configuration
            Configuration configuration = new Configuration();
            configuration.addProperties(hibernateProperties);
            
            // Entity classes will be added here when created
            addEntityClasses(configuration);
            
            if (logger.isDebugEnabled()) {
                logger.debug("Hibernate configuration loaded with {} properties", hibernateProperties.size());
            }
            
            // Build ServiceRegistry
            serviceRegistry = new StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties())
                    .build();
            
            if (logger.isDebugEnabled()) {
                logger.debug("Hibernate ServiceRegistry built successfully");
            }
            
            // Build SessionFactory
            sessionFactory = configuration.buildSessionFactory(serviceRegistry);
            
            logger.info("Hibernate SessionFactory initialized successfully");
            if (logger.isDebugEnabled()) {
                logger.debug("SessionFactory created: {}", sessionFactory.getClass().getName());
            }
            
        } catch (HibernateConfigurationException ex) {
            logger.error("Hibernate configuration failed: {}", ex.getMessage(), ex);
            cleanupOnFailure();
            throw ex;
        } catch (Exception ex) {
            logger.error("Unexpected error during SessionFactory creation: {}", ex.getMessage(), ex);
            cleanupOnFailure();
            throw new HibernateConfigurationException("Failed to initialize Hibernate SessionFactory", ex);
        } finally {
            MDC.remove(CORRELATION_ID);
        }
    }
    
    /**
     * Cleanup resources on SessionFactory creation failure
     */
    private static void cleanupOnFailure() {
        if (serviceRegistry != null) {
            try {
                StandardServiceRegistryBuilder.destroy(serviceRegistry);
            } catch (Exception cleanupEx) {
                logger.warn("Error during cleanup after SessionFactory creation failure", cleanupEx);
            }
        }
    }
    
    /**
     * Load hibernate properties from hibernate.properties file
     * 
     * @return Properties object containing hibernate configuration
     * @throws HibernateConfigurationException if properties cannot be loaded
     */
    private static Properties loadHibernateProperties() {
        Properties properties = new Properties();
        
        try (InputStream inputStream = HibernateUtil.class.getClassLoader()
                .getResourceAsStream(HIBERNATE_PROPERTIES_FILE)) {
            
            if (inputStream == null) {
                throw new HibernateConfigurationException(
                    "Unable to find " + HIBERNATE_PROPERTIES_FILE + " file in classpath");
            }
            
            properties.load(inputStream);
            if (logger.isDebugEnabled()) {
                logger.debug("Loaded {} properties from {}", properties.size(), HIBERNATE_PROPERTIES_FILE);
                logDatabaseConfiguration(properties);
            }
            
        } catch (IOException ex) {
            logger.error("Error loading {} file: {}", HIBERNATE_PROPERTIES_FILE, ex.getMessage(), ex);
            throw new HibernateConfigurationException("Failed to load " + HIBERNATE_PROPERTIES_FILE, ex);
        }
        
        return properties;
    }
    
    /**
     * Log database configuration details (without sensitive information)
     * 
     * @param properties the hibernate properties
     */
    private static void logDatabaseConfiguration(Properties properties) {
        if (logger.isDebugEnabled()) {
            logger.debug("Database URL: {}", properties.getProperty("hibernate.connection.url"));
            logger.debug("Database Driver: {}", properties.getProperty("hibernate.connection.driver_class"));
            logger.debug("Hibernate Dialect: {}", properties.getProperty("hibernate.dialect"));
            logger.debug("HBM2DDL Auto: {}", properties.getProperty("hibernate.hbm2ddl.auto"));
            logger.debug("Show SQL: {}", properties.getProperty("hibernate.show_sql"));
            logger.debug("Second Level Cache: {}", properties.getProperty("hibernate.cache.use_second_level_cache"));
        }
    }
    
    /**
     * Add entity classes to configuration
     * This method will be implemented when entity classes are created
     * 
     * @param configuration the Hibernate configuration
     */
    @SuppressWarnings("unused")
    private static void addEntityClasses(Configuration configuration) {
        // Entity classes will be added here when they are created in future issues
        logger.debug("Entity classes will be registered when available");
    }
    
    /**
     * Shutdown SessionFactory and cleanup resources
     */
    public static void shutdown() {
        String correlationId = UUID.randomUUID().toString();
        MDC.put(CORRELATION_ID, correlationId);
        
        try {
            logger.info("Shutting down Hibernate SessionFactory...");
            
            if (sessionFactory != null && !sessionFactory.isClosed()) {
                sessionFactory.close();
                logger.info("SessionFactory closed successfully");
            }
            
            if (serviceRegistry != null) {
                StandardServiceRegistryBuilder.destroy(serviceRegistry);
                logger.info("ServiceRegistry destroyed successfully");
            }
            
        } catch (Exception ex) {
            logger.error("Error during SessionFactory shutdown: {}", ex.getMessage(), ex);
        } finally {
            sessionFactory = null;
            serviceRegistry = null;
            MDC.remove(CORRELATION_ID);
        }
    }
    
    /**
     * Check if SessionFactory is initialized and not closed
     * 
     * @return true if SessionFactory is available and open
     */
    public static boolean isSessionFactoryActive() {
        return sessionFactory != null && !sessionFactory.isClosed();
    }
    
    /**
     * Get current SessionFactory statistics (if enabled)
     * 
     * @return Statistics object or null if not available
     */
    public static org.hibernate.stat.Statistics getStatistics() {
        if (isSessionFactoryActive()) {
            return sessionFactory.getStatistics();
        }
        return null;
    }
    
    /**
     * Custom exception for Hibernate configuration errors
     */
    public static class HibernateConfigurationException extends RuntimeException {
        public HibernateConfigurationException(String message) {
            super(message);
        }
        
        public HibernateConfigurationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}

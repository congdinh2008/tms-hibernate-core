package com.congdinh2008.tms.config;

import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for Hibernate configuration setup
 * Validates configuration loading, properties validation, and basic setup
 * without requiring actual database connection
 */
class HibernateConfigurationTest {

    @Test
    @DisplayName("Should load hibernate.properties file successfully")
    void testHibernatePropertiesLoading() throws IOException {
        Properties properties = new Properties();
        properties.load(HibernateConfigurationTest.class
                .getClassLoader()
                .getResourceAsStream("hibernate.properties"));
        
        assertNotNull(properties, "hibernate.properties should be loaded");
        assertFalse(properties.isEmpty(), "hibernate.properties should not be empty");
        
        // Verify required database properties
        assertNotNull(properties.getProperty("hibernate.connection.url"), 
                "Database URL should be configured");
        assertNotNull(properties.getProperty("hibernate.connection.username"), 
                "Database username should be configured");
        assertNotNull(properties.getProperty("hibernate.connection.password"), 
                "Database password should be configured");
        assertNotNull(properties.getProperty("hibernate.connection.driver_class"), 
                "Database driver should be configured");
        assertNotNull(properties.getProperty("hibernate.dialect"), 
                "Hibernate dialect should be configured");
    }

    @Test
    @DisplayName("Should validate Hibernate configuration properties")
    void testHibernateConfigurationValidation() throws IOException {
        Properties properties = new Properties();
        properties.load(HibernateConfigurationTest.class
                .getClassLoader()
                .getResourceAsStream("hibernate.properties"));
        
        // Validate core database settings
        assertEquals("jdbc:postgresql://localhost:5432/postgres", 
                properties.getProperty("hibernate.connection.url"));
        assertEquals("postgres", 
                properties.getProperty("hibernate.connection.username"));
        assertEquals("org.postgresql.Driver", 
                properties.getProperty("hibernate.connection.driver_class"));
        assertEquals("org.hibernate.dialect.PostgreSQLDialect", 
                properties.getProperty("hibernate.dialect"));
        
        // Validate cache settings
        assertEquals("true", 
                properties.getProperty("hibernate.cache.use_second_level_cache"));
        assertEquals("true", 
                properties.getProperty("hibernate.cache.use_query_cache"));
        assertEquals("org.hibernate.cache.jcache.JCacheRegionFactory", 
                properties.getProperty("hibernate.cache.region.factory_class"));
        assertEquals("org.ehcache.jsr107.EhcacheCachingProvider", 
                properties.getProperty("hibernate.javax.cache.provider"));
        assertEquals("ehcache.xml", 
                properties.getProperty("hibernate.javax.cache.uri"));
        
        // Validate connection pooling settings
        assertEquals("com.zaxxer.hikari.hibernate.HikariConnectionProvider", 
                properties.getProperty("hibernate.connection.provider_class"));
        assertEquals("10", 
                properties.getProperty("hibernate.hikari.maximumPoolSize"));
        assertEquals("5", 
                properties.getProperty("hibernate.hikari.minimumIdle"));
        
        // Validate performance settings
        assertEquals("true", 
                properties.getProperty("hibernate.show_sql"));
        assertEquals("true", 
                properties.getProperty("hibernate.format_sql"));
        assertEquals("true", 
                properties.getProperty("hibernate.generate_statistics"));
        assertEquals("20", 
                properties.getProperty("hibernate.jdbc.batch_size"));
        assertEquals("50", 
                properties.getProperty("hibernate.jdbc.fetch_size"));
    }

    @Test
    @DisplayName("Should create Hibernate Configuration object successfully")
    void testHibernateConfigurationCreation() {
        assertDoesNotThrow(() -> {
            Configuration configuration = new Configuration();
            
            // Load properties from file
            Properties properties = new Properties();
            properties.load(HibernateConfigurationTest.class
                    .getClassLoader()
                    .getResourceAsStream("hibernate.properties"));
            
            // Configure Hibernate
            configuration.setProperties(properties);
            
            assertNotNull(configuration, "Configuration should be created");
            assertNotNull(configuration.getProperties(), "Configuration properties should be set");
            
            // Verify some key properties are set
            assertEquals("org.hibernate.dialect.PostgreSQLDialect", 
                    configuration.getProperty("hibernate.dialect"));
            assertEquals("true", 
                    configuration.getProperty("hibernate.show_sql"));
        }, "Configuration creation should not throw exceptions");
    }

    @Test
    @DisplayName("Should validate ServiceRegistry can be built with configuration")
    void testServiceRegistryBuilding() {
        assertDoesNotThrow(() -> {
            Configuration configuration = new Configuration();
            
            // Load properties from file
            Properties properties = new Properties();
            properties.load(HibernateConfigurationTest.class
                    .getClassLoader()
                    .getResourceAsStream("hibernate.properties"));
            
            // Configure Hibernate
            configuration.setProperties(properties);
            
            // Build service registry (without trying to connect to database)
            StandardServiceRegistryBuilder registryBuilder = 
                    new StandardServiceRegistryBuilder().applySettings(configuration.getProperties());
            
            assertNotNull(registryBuilder, "ServiceRegistryBuilder should be created");
        }, "ServiceRegistry building should not throw exceptions during setup");
    }

    @Test
    @DisplayName("Should validate ehcache.xml exists and is accessible")
    void testEhcacheConfigurationExists() {
        assertNotNull(HibernateConfigurationTest.class
                .getClassLoader()
                .getResource("ehcache.xml"), 
                "ehcache.xml should exist in classpath");
    }

    @Test
    @DisplayName("Should handle missing properties gracefully")
    void testMissingPropertiesHandling() {
        Properties emptyProperties = new Properties();
        Configuration configuration = new Configuration();
        configuration.setProperties(emptyProperties);
        
        // Should not throw exception when getting non-existent property
        assertNull(configuration.getProperty("non.existent.property"));
    }

    @Test
    @DisplayName("Should validate all required Hibernate properties are present")
    void testAllRequiredPropertiesPresent() throws IOException {
        Properties properties = new Properties();
        properties.load(HibernateConfigurationTest.class
                .getClassLoader()
                .getResourceAsStream("hibernate.properties"));
        
        // Required database connection properties
        String[] requiredProperties = {
                "hibernate.connection.url",
                "hibernate.connection.username", 
                "hibernate.connection.password",
                "hibernate.connection.driver_class",
                "hibernate.dialect",
                "hibernate.hbm2ddl.auto",
                "hibernate.show_sql",
                "hibernate.format_sql"
        };
        
        for (String property : requiredProperties) {
            assertNotNull(properties.getProperty(property), 
                    "Required property " + property + " should be present");
            assertFalse(properties.getProperty(property).trim().isEmpty(), 
                    "Required property " + property + " should not be empty");
        }
    }
}

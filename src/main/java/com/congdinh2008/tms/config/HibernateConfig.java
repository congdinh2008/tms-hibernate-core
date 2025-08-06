package com.congdinh2008.tms.config;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.orm.hibernate5.LocalSessionFactoryBuilder;

import javax.sql.DataSource;
import java.util.Properties;
import java.util.UUID;

/**
 * Spring Configuration class for Hibernate SessionFactory
 * Uses Spring IoC container for dependency injection and lifecycle management
 * 
 * @author congdinh2008
 * @since 1.0.0
 */
@Configuration
@PropertySource("classpath:hibernate.properties")
public class HibernateConfig {

    private static final Logger logger = LoggerFactory.getLogger(HibernateConfig.class);

    @Value("${hibernate.connection.driver_class}")
    private String driverClassName;

    @Value("${hibernate.connection.url}")
    private String url;

    @Value("${hibernate.connection.username}")
    private String username;

    @Value("${hibernate.connection.password}")
    private String password;

    @Value("${hibernate.dialect}")
    private String dialect;

    @Value("${hibernate.hbm2ddl.auto}")
    private String hbm2ddlAuto;

    @Value("${hibernate.show_sql}")
    private String showSql;

    @Value("${hibernate.format_sql}")
    private String formatSql;

    @Value("${hibernate.use_sql_comments}")
    private String useSqlComments;

    @Value("${hibernate.generate_statistics}")
    private String generateStatistics;

    @Value("${hibernate.cache.use_second_level_cache}")
    private String useSecondLevelCache;

    @Value("${hibernate.cache.use_query_cache}")
    private String useQueryCache;

    @Value("${hibernate.cache.region.factory_class}")
    private String cacheRegionFactoryClass;

    @Value("${hibernate.javax.cache.provider}")
    private String cacheProvider;

    @Value("${hibernate.javax.cache.uri}")
    private String cacheUri;

    @Value("${hibernate.connection.provider_class}")
    private String connectionProviderClass;

    @Value("${hibernate.hikari.minimumIdle}")
    private String hikariMinimumIdle;

    @Value("${hibernate.hikari.maximumPoolSize}")
    private String hikariMaximumPoolSize;

    @Value("${hibernate.hikari.idleTimeout}")
    private String hikariIdleTimeout;

    @Value("${hibernate.jdbc.batch_size}")
    private String jdbcBatchSize;

    @Value("${hibernate.jdbc.fetch_size}")
    private String jdbcFetchSize;

    @Value("${hibernate.order_inserts}")
    private String orderInserts;

    @Value("${hibernate.order_updates}")
    private String orderUpdates;

    @Value("${hibernate.batch_versioned_data}")
    private String batchVersionedData;

    @Value("${hibernate.cache.use_structured_entries}")
    private String cacheUseStructuredEntries;

    @Value("${hibernate.physical_naming_strategy}")
    private String physicalNamingStrategy;

    @Value("${hibernate.implicit_naming_strategy}")
    private String implicitNamingStrategy;

    /**
     * Creates and configures Hibernate SessionFactory as a Spring Bean
     * 
     * @param dataSource the configured DataSource
     * @return configured SessionFactory instance
     */
    @Bean(name = "sessionFactory", destroyMethod = "close")
    public SessionFactory sessionFactory(DataSource dataSource) {
        String correlationId = UUID.randomUUID().toString();
        MDC.put("correlationId", correlationId);

        try {
            logger.info("Starting Hibernate SessionFactory initialization with Spring IoC...");
            logger.debug("Loaded properties from @PropertySource annotation");
            
            // Log key configuration details
            logger.debug("Database URL: {}", url);
            logger.debug("Database Driver: {}", driverClassName);
            logger.debug("Hibernate Dialect: {}", dialect);
            logger.debug("HBM2DDL Auto: {}", hbm2ddlAuto);
            logger.debug("Show SQL: {}", showSql);
            logger.debug("Second Level Cache: {}", useSecondLevelCache);

            Properties hibernateProperties = createHibernateProperties();
            
            LocalSessionFactoryBuilder sessionFactoryBuilder = new LocalSessionFactoryBuilder(dataSource);
            sessionFactoryBuilder.addProperties(hibernateProperties);
            
            logger.debug("Entity classes will be registered when available");

            SessionFactory sessionFactory = sessionFactoryBuilder.buildSessionFactory();
            
            logger.info("Hibernate SessionFactory initialized successfully with Spring IoC");
            logger.debug("SessionFactory created: {}", sessionFactory.getClass().getSimpleName());
            
            return sessionFactory;

        } catch (Exception e) {
            logger.error("Failed to initialize Hibernate SessionFactory with Spring IoC: {}", e.getMessage(), e);
            throw new HibernateConfigurationException("Failed to initialize Hibernate SessionFactory", e);
        } finally {
            MDC.remove("correlationId");
        }
    }

    /**
     * Creates Hibernate properties from Spring configuration values
     * 
     * @return Properties object with all Hibernate configuration
     */
    private Properties createHibernateProperties() {
        Properties properties = new Properties();

        // Database connection properties
        properties.setProperty("hibernate.connection.driver_class", driverClassName);
        properties.setProperty("hibernate.connection.url", url);
        properties.setProperty("hibernate.connection.username", username);
        properties.setProperty("hibernate.connection.password", password);

        // Core Hibernate settings
        properties.setProperty("hibernate.dialect", dialect);
        properties.setProperty("hibernate.hbm2ddl.auto", hbm2ddlAuto);
        properties.setProperty("hibernate.show_sql", showSql);
        properties.setProperty("hibernate.format_sql", formatSql);
        properties.setProperty("hibernate.use_sql_comments", useSqlComments);
        properties.setProperty("hibernate.generate_statistics", generateStatistics);

        // Connection pooling
        properties.setProperty("hibernate.connection.provider_class", connectionProviderClass);
        properties.setProperty("hibernate.hikari.minimumIdle", hikariMinimumIdle);
        properties.setProperty("hibernate.hikari.maximumPoolSize", hikariMaximumPoolSize);
        properties.setProperty("hibernate.hikari.idleTimeout", hikariIdleTimeout);

        // Second-level cache
        properties.setProperty("hibernate.cache.use_second_level_cache", useSecondLevelCache);
        properties.setProperty("hibernate.cache.use_query_cache", useQueryCache);
        properties.setProperty("hibernate.cache.region.factory_class", cacheRegionFactoryClass);
        properties.setProperty("hibernate.javax.cache.provider", cacheProvider);
        properties.setProperty("hibernate.javax.cache.uri", cacheUri);

        // Performance optimizations
        properties.setProperty("hibernate.jdbc.batch_size", jdbcBatchSize);
        properties.setProperty("hibernate.jdbc.fetch_size", jdbcFetchSize);
        properties.setProperty("hibernate.order_inserts", orderInserts);
        properties.setProperty("hibernate.order_updates", orderUpdates);
        properties.setProperty("hibernate.batch_versioned_data", batchVersionedData);
        properties.setProperty("hibernate.cache.use_structured_entries", cacheUseStructuredEntries);

        // Naming strategies
        properties.setProperty("hibernate.physical_naming_strategy", physicalNamingStrategy);
        properties.setProperty("hibernate.implicit_naming_strategy", implicitNamingStrategy);

        logger.debug("Hibernate configuration created with {} properties", properties.size());
        return properties;
    }

    /**
     * Custom exception for Hibernate configuration failures
     */
    public static class HibernateConfigurationException extends RuntimeException {
        public HibernateConfigurationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}

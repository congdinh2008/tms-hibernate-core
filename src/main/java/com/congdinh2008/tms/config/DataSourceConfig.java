package com.congdinh2008.tms.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import javax.sql.DataSource;
import java.util.UUID;

/**
 * Spring Configuration class for DataSource
 * Configures HikariCP connection pool as a Spring Bean
 * 
 * @author congdinh2008
 * @since 1.0.0
 */
@Configuration
@PropertySource("classpath:hibernate.properties")
public class DataSourceConfig {

    private static final Logger logger = LoggerFactory.getLogger(DataSourceConfig.class);

    @Value("${hibernate.connection.driver_class}")
    private String driverClassName;

    @Value("${hibernate.connection.url}")
    private String url;

    @Value("${hibernate.connection.username}")
    private String username;

    @Value("${hibernate.connection.password}")
    private String password;

    @Value("${hibernate.hikari.minimumIdle}")
    private int minimumIdle;

    @Value("${hibernate.hikari.maximumPoolSize}")
    private int maximumPoolSize;

    @Value("${hibernate.hikari.idleTimeout}")
    private long idleTimeout;

    /**
     * Creates and configures HikariCP DataSource as a Spring Bean
     * 
     * @return configured DataSource instance
     */
    @Bean(name = "dataSource", destroyMethod = "close")
    public DataSource dataSource() {
        String correlationId = UUID.randomUUID().toString();
        MDC.put("correlationId", correlationId);

        try {
            logger.info("Starting HikariCP DataSource initialization with Spring IoC...");
            
            HikariConfig config = new HikariConfig();
            config.setDriverClassName(driverClassName);
            config.setJdbcUrl(url);
            config.setUsername(username);
            config.setPassword(password);
            config.setMinimumIdle(minimumIdle);
            config.setMaximumPoolSize(maximumPoolSize);
            config.setIdleTimeout(idleTimeout);
            config.setConnectionTimeout(30000); // 30 seconds
            config.setValidationTimeout(5000);  // 5 seconds
            config.setLeakDetectionThreshold(60000); // 1 minute
            
            // Connection pool settings
            config.setConnectionTestQuery("SELECT 1");
            config.setAutoCommit(true);
            
            logger.debug("HikariCP configuration - URL: {}", url);
            logger.debug("HikariCP configuration - Driver: {}", driverClassName);
            logger.debug("HikariCP configuration - Username: {}", username);
            logger.debug("HikariCP configuration - MinIdle: {}", minimumIdle);
            logger.debug("HikariCP configuration - MaxPoolSize: {}", maximumPoolSize);
            logger.debug("HikariCP configuration - IdleTimeout: {}ms", idleTimeout);

            HikariDataSource dataSource = new HikariDataSource(config);
            
            logger.info("HikariCP DataSource initialized successfully with Spring IoC");
            logger.debug("DataSource created: {}", dataSource.getClass().getSimpleName());
            
            return dataSource;

        } catch (Exception e) {
            logger.error("Failed to initialize HikariCP DataSource with Spring IoC: {}", e.getMessage(), e);
            throw new DataSourceConfigurationException("Failed to initialize HikariCP DataSource", e);
        } finally {
            MDC.remove("correlationId");
        }
    }

    /**
     * Custom exception for DataSource configuration failures
     */
    public static class DataSourceConfigurationException extends RuntimeException {
        public DataSourceConfigurationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}

package com.congdinh2008.tms.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Root Spring Configuration class
 * Combines all configuration classes and enables component scanning
 * 
 * @author congdinh2008
 * @since 1.0.0
 */
@Configuration
@ComponentScan(basePackages = "com.congdinh2008.tms")
@Import({DataSourceConfig.class, HibernateConfig.class})
public class ApplicationConfig {
    // Root configuration class - specific beans can be added here if needed
}

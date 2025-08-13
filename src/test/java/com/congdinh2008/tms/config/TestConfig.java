package com.congdinh2008.tms.config;

import com.zaxxer.hikari.HikariDataSource;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

/**
 * Test Configuration for setting up test-specific beans and data sources
 * 
 * @author Cong Dinh
 * @version 1.0.0
 * @since 1.0.0
 */
@Configuration
@Profile("test")
public class TestConfig {
    
    @Bean
    @Primary
    public DataSource testDataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
        dataSource.setUsername("sa");
        dataSource.setPassword("");
        dataSource.setDriverClassName("org.h2.Driver");
        
        // Test-specific connection pool settings
        dataSource.setMinimumIdle(2);
        dataSource.setMaximumPoolSize(5);
        dataSource.setConnectionTimeout(10000);
        dataSource.setIdleTimeout(300000);
        dataSource.setMaxLifetime(600000);
        
        return dataSource;
    }
    
    @Bean
    @Primary
    public PlatformTransactionManager testTransactionManager(@Autowired SessionFactory sessionFactory) {
        HibernateTransactionManager transactionManager = new HibernateTransactionManager();
        transactionManager.setSessionFactory(sessionFactory);
        return transactionManager;
    }
}

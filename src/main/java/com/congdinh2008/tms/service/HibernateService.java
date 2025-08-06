package com.congdinh2008.tms.service;

import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.UUID;

/**
 * Spring Service for managing Hibernate SessionFactory
 * Provides access to SessionFactory and related operations using Spring IoC
 * 
 * @author congdinh2008
 * @since 1.0.0
 */
@Service
public class HibernateService {

    private static final Logger logger = LoggerFactory.getLogger(HibernateService.class);
    private static final String CORRELATION_ID = "correlationId";

    private final SessionFactory sessionFactory;

    /**
     * Constructor injection for SessionFactory
     * 
     * @param sessionFactory the SessionFactory to inject
     */
    @Autowired
    public HibernateService(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    /**
     * Post-construct initialization
     */
    @PostConstruct
    public void initialize() {
        String correlationId = UUID.randomUUID().toString();
        MDC.put(CORRELATION_ID, correlationId);

        try {
            logger.info("HibernateService initialized with Spring IoC");
            logger.debug("SessionFactory injected: {}", sessionFactory.getClass().getSimpleName());
            
            if (sessionFactory.getStatistics().isStatisticsEnabled()) {
                logger.debug("Hibernate statistics are enabled");
            }
        } finally {
            MDC.remove(CORRELATION_ID);
        }
    }

    /**
     * Pre-destroy cleanup
     */
    @PreDestroy
    public void cleanup() {
        String correlationId = UUID.randomUUID().toString();
        MDC.put(CORRELATION_ID, correlationId);

        try {
            logger.info("HibernateService cleanup initiated");
        } finally {
            MDC.remove(CORRELATION_ID);
        }
    }

    /**
     * Gets the Hibernate SessionFactory instance
     * 
     * @return SessionFactory instance managed by Spring
     */
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    /**
     * Checks if the SessionFactory is open and active
     * 
     * @return true if SessionFactory is open, false otherwise
     */
    public boolean isSessionFactoryOpen() {
        return sessionFactory != null && !sessionFactory.isClosed();
    }

    /**
     * Gets Hibernate statistics if enabled
     * 
     * @return Statistics instance or null if statistics are disabled
     */
    public Statistics getStatistics() {
        if (sessionFactory != null && sessionFactory.getStatistics().isStatisticsEnabled()) {
            return sessionFactory.getStatistics();
        }
        return null;
    }

    /**
     * Logs current connection pool statistics if available
     */
    public void logConnectionPoolStats() {
        String correlationId = UUID.randomUUID().toString();
        MDC.put(CORRELATION_ID, correlationId);

        try {
            Statistics stats = getStatistics();
            if (stats != null) {
                logger.info("Hibernate Statistics - Sessions opened: {}", stats.getSessionOpenCount());
                logger.info("Hibernate Statistics - Sessions closed: {}", stats.getSessionCloseCount());
                logger.info("Hibernate Statistics - Transactions: {}", stats.getTransactionCount());
                
                double hitRatio = stats.getQueryCacheHitCount() > 0 ? 
                    (double) stats.getQueryCacheHitCount() / stats.getQueryExecutionCount() * 100 : 0.0;
                logger.info("Hibernate Statistics - Cache hit ratio: {}%", String.format("%.2f", hitRatio));
            } else {
                logger.debug("Hibernate statistics are not enabled");
            }
        } finally {
            MDC.remove(CORRELATION_ID);
        }
    }
}

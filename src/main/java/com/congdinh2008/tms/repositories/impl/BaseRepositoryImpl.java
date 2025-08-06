package com.congdinh2008.tms.repositories.impl;

import com.congdinh2008.tms.exceptions.EntityNotFoundException;
import com.congdinh2008.tms.exceptions.OptimisticLockingException;
import com.congdinh2008.tms.exceptions.RepositoryException;
import com.congdinh2008.tms.repositories.BaseRepository;
import jakarta.persistence.OptimisticLockException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Base repository implementation providing common CRUD operations
 * with pagination, sorting, and transaction management
 */
public abstract class BaseRepositoryImpl<T, ID> implements BaseRepository<T, ID> {
    
    protected final Logger log = LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    protected SessionFactory sessionFactory;
    
    protected final Class<T> entityClass;
    
    @SuppressWarnings("unchecked")
    public BaseRepositoryImpl() {
        this.entityClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass())
                .getActualTypeArguments()[0];
    }
    
    protected Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }
    
    @Override
    public T save(T entity) {
        String correlationId = UUID.randomUUID().toString();
        log.debug("{} - Saving entity: {}", correlationId, entityClass.getSimpleName());
        
        try {
            Session session = getCurrentSession();
            session.persist(entity);
            log.info("{} - Successfully saved entity: {}", correlationId, entityClass.getSimpleName());
            return entity;
        } catch (Exception e) {
            log.error("{} - Error saving entity: {}", correlationId, e.getMessage(), e);
            throw new RepositoryException("Failed to save " + entityClass.getSimpleName(), e);
        }
    }
    
    @Override
    public T findById(ID id) {
        String correlationId = UUID.randomUUID().toString();
        log.debug("{} - Finding {} by id: {}", correlationId, entityClass.getSimpleName(), id);
        
        try {
            Session session = getCurrentSession();
            T entity = session.get(entityClass, id);
            if (entity != null) {
                log.debug("{} - Found entity: {}", correlationId, entityClass.getSimpleName());
            } else {
                log.debug("{} - Entity not found: {} with id: {}", correlationId, entityClass.getSimpleName(), id);
            }
            return entity;
        } catch (Exception e) {
            log.error("{} - Error finding entity by id: {}", correlationId, e.getMessage(), e);
            throw new RepositoryException("Failed to find " + entityClass.getSimpleName() + " by id", e);
        }
    }
    
    @Override
    public Optional<T> findByIdOptional(ID id) {
        return Optional.ofNullable(findById(id));
    }
    
    @Override
    public List<T> findAll() {
        String correlationId = UUID.randomUUID().toString();
        log.debug("{} - Finding all {}", correlationId, entityClass.getSimpleName());
        
        try {
            Session session = getCurrentSession();
            String hql = "FROM " + entityClass.getSimpleName();
            Query<T> query = session.createQuery(hql, entityClass);
            List<T> results = query.getResultList();
            log.debug("{} - Found {} entities", correlationId, results.size());
            return results;
        } catch (Exception e) {
            log.error("{} - Error finding all entities: {}", correlationId, e.getMessage(), e);
            throw new RepositoryException("Failed to find all " + entityClass.getSimpleName(), e);
        }
    }
    
    @Override
    public List<T> findAll(int page, int size) {
        String correlationId = UUID.randomUUID().toString();
        log.debug("{} - Finding {} with pagination: page={}, size={}", 
                 correlationId, entityClass.getSimpleName(), page, size);
        
        try {
            Session session = getCurrentSession();
            String hql = "FROM " + entityClass.getSimpleName();
            Query<T> query = session.createQuery(hql, entityClass);
            query.setFirstResult(page * size);
            query.setMaxResults(size);
            List<T> results = query.getResultList();
            log.debug("{} - Found {} entities for page {}", correlationId, results.size(), page);
            return results;
        } catch (Exception e) {
            log.error("{} - Error finding entities with pagination: {}", correlationId, e.getMessage(), e);
            throw new RepositoryException("Failed to find " + entityClass.getSimpleName() + " with pagination", e);
        }
    }
    
    @Override
    public List<T> findAll(int page, int size, String sortBy, String sortDir) {
        String correlationId = UUID.randomUUID().toString();
        log.debug("{} - Finding {} with pagination and sorting: page={}, size={}, sortBy={}, sortDir={}", 
                 correlationId, entityClass.getSimpleName(), page, size, sortBy, sortDir);
        
        try {
            Session session = getCurrentSession();
            String direction = "DESC".equalsIgnoreCase(sortDir) ? "DESC" : "ASC";
            String hql = "FROM " + entityClass.getSimpleName() + " ORDER BY " + sortBy + " " + direction;
            Query<T> query = session.createQuery(hql, entityClass);
            query.setFirstResult(page * size);
            query.setMaxResults(size);
            List<T> results = query.getResultList();
            log.debug("{} - Found {} entities for page {} with sorting", correlationId, results.size(), page);
            return results;
        } catch (Exception e) {
            log.error("{} - Error finding entities with pagination and sorting: {}", correlationId, e.getMessage(), e);
            throw new RepositoryException("Failed to find " + entityClass.getSimpleName() + " with pagination and sorting", e);
        }
    }
    
    @Override
    public T update(T entity) {
        String correlationId = UUID.randomUUID().toString();
        log.debug("{} - Updating entity: {}", correlationId, entityClass.getSimpleName());
        
        try {
            Session session = getCurrentSession();
            T updated = session.merge(entity);
            log.info("{} - Successfully updated entity: {}", correlationId, entityClass.getSimpleName());
            return updated;
        } catch (OptimisticLockException e) {
            log.error("{} - Optimistic locking failure: {}", correlationId, e.getMessage());
            throw new OptimisticLockingException(entityClass.getSimpleName(), "unknown");
        } catch (Exception e) {
            log.error("{} - Error updating entity: {}", correlationId, e.getMessage(), e);
            throw new RepositoryException("Failed to update " + entityClass.getSimpleName(), e);
        }
    }
    
    @Override
    public void delete(T entity) {
        String correlationId = UUID.randomUUID().toString();
        log.debug("{} - Deleting entity: {}", correlationId, entityClass.getSimpleName());
        
        try {
            Session session = getCurrentSession();
            session.remove(entity);
            log.info("{} - Successfully deleted entity: {}", correlationId, entityClass.getSimpleName());
        } catch (Exception e) {
            log.error("{} - Error deleting entity: {}", correlationId, e.getMessage(), e);
            throw new RepositoryException("Failed to delete " + entityClass.getSimpleName(), e);
        }
    }
    
    @Override
    public void deleteById(ID id) {
        String correlationId = UUID.randomUUID().toString();
        log.debug("{} - Deleting {} by id: {}", correlationId, entityClass.getSimpleName(), id);
        
        T entity = findById(id);
        if (entity == null) {
            throw new EntityNotFoundException(entityClass.getSimpleName(), id);
        }
        delete(entity);
    }
    
    @Override
    public long count() {
        String correlationId = UUID.randomUUID().toString();
        log.debug("{} - Counting {}", correlationId, entityClass.getSimpleName());
        
        try {
            Session session = getCurrentSession();
            String hql = "SELECT COUNT(*) FROM " + entityClass.getSimpleName();
            Query<Long> query = session.createQuery(hql, Long.class);
            Long count = query.getSingleResult();
            log.debug("{} - Count result: {}", correlationId, count);
            return count;
        } catch (Exception e) {
            log.error("{} - Error counting entities: {}", correlationId, e.getMessage(), e);
            throw new RepositoryException("Failed to count " + entityClass.getSimpleName(), e);
        }
    }
    
    @Override
    public boolean existsById(ID id) {
        String correlationId = UUID.randomUUID().toString();
        log.debug("{} - Checking existence of {} by id: {}", correlationId, entityClass.getSimpleName(), id);
        
        try {
            Session session = getCurrentSession();
            String hql = "SELECT COUNT(*) FROM " + entityClass.getSimpleName() + " WHERE id = :id";
            Query<Long> query = session.createQuery(hql, Long.class);
            query.setParameter("id", id);
            Long count = query.getSingleResult();
            boolean exists = count > 0;
            log.debug("{} - Entity exists: {}", correlationId, exists);
            return exists;
        } catch (Exception e) {
            log.error("{} - Error checking entity existence: {}", correlationId, e.getMessage(), e);
            throw new RepositoryException("Failed to check existence of " + entityClass.getSimpleName(), e);
        }
    }
    
    @Override
    public void flush() {
        getCurrentSession().flush();
    }
    
    @Override
    public void clear() {
        getCurrentSession().clear();
    }
}

package com.congdinh2008.tms.repositories.impl;

import com.congdinh2008.tms.entities.Tag;
import com.congdinh2008.tms.exceptions.RepositoryException;
import com.congdinh2008.tms.repositories.TagRepository;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Implementation of TagRepository interface
 */
@Repository
public class TagRepositoryImpl extends BaseRepositoryImpl<Tag, Long> implements TagRepository {
    
    private static final Logger logger = LoggerFactory.getLogger(TagRepositoryImpl.class);
    
    public TagRepositoryImpl() {
        super();
    }
    
    @Override
    public Tag findByName(String name) {
        String correlationId = UUID.randomUUID().toString().substring(0, 8);
        logger.debug("[{}] Finding tag by name: {}", correlationId, name);
        
        try {
            Session session = sessionFactory.getCurrentSession();
            Query<Tag> query = session.createQuery(
                "FROM Tag t WHERE t.name = :name", Tag.class);
            query.setParameter("name", name);
            
            Tag result = query.uniqueResult();
            logger.debug("[{}] Found tag by name: {}", correlationId, result != null);
            return result;
        } catch (Exception e) {
            logger.error("[{}] Error finding tag by name: {}", correlationId, name, e);
            throw new RepositoryException("Error finding tag by name", e);
        }
    }
    
    @Override
    public List<Tag> findByNameContaining(String name) {
        String correlationId = UUID.randomUUID().toString().substring(0, 8);
        logger.debug("[{}] Finding tags by name containing: {}", correlationId, name);
        
        try {
            Session session = sessionFactory.getCurrentSession();
            Query<Tag> query = session.createQuery(
                "FROM Tag t WHERE LOWER(t.name) LIKE LOWER(:name)", Tag.class);
            query.setParameter("name", "%" + name + "%");
            
            List<Tag> results = query.getResultList();
            logger.debug("[{}] Found {} tags by name containing: {}", correlationId, results.size(), name);
            return results;
        } catch (Exception e) {
            logger.error("[{}] Error finding tags by name containing: {}", correlationId, name, e);
            throw new RepositoryException("Error finding tags by name containing", e);
        }
    }
    
    @Override
    public boolean existsByName(String name) {
        String correlationId = UUID.randomUUID().toString().substring(0, 8);
        logger.debug("[{}] Checking if tag exists by name: {}", correlationId, name);
        
        try {
            Session session = sessionFactory.getCurrentSession();
            Query<Long> query = session.createQuery(
                "SELECT COUNT(t) FROM Tag t WHERE t.name = :name", Long.class);
            query.setParameter("name", name);
            
            Long count = query.uniqueResult();
            boolean exists = count != null && count > 0;
            logger.debug("[{}] Tag exists by name {}: {}", correlationId, name, exists);
            return exists;
        } catch (Exception e) {
            logger.error("[{}] Error checking if tag exists by name: {}", correlationId, name, e);
            throw new RepositoryException("Error checking if tag exists by name", e);
        }
    }
    
    @Override
    public List<Tag> findPopularTags(int limit) {
        String correlationId = UUID.randomUUID().toString().substring(0, 8);
        logger.debug("[{}] Finding {} popular tags", correlationId, limit);
        
        try {
            Session session = sessionFactory.getCurrentSession();
            String sql = """
                SELECT t.id, t.name, t.created_at, t.updated_at, t.version,
                       COUNT(tt.task_id) as usage_count
                FROM tags t
                LEFT JOIN task_tags tt ON t.id = tt.tag_id
                GROUP BY t.id, t.name, t.created_at, t.updated_at, t.version
                ORDER BY usage_count DESC
                LIMIT :limit
                """;
            
            Query<Object[]> query = session.createNativeQuery(sql, Object[].class);
            query.setParameter("limit", limit);
            
            List<Object[]> results = query.getResultList();
            
            // Convert to Tag entities
            List<Tag> tags = results.stream()
                .map(row -> {
                    Tag tag = new Tag();
                    tag.setId(((Number) row[0]).longValue());
                    tag.setName((String) row[1]);
                    tag.setCreatedAt((java.time.LocalDateTime) row[2]);
                    tag.setUpdatedAt((java.time.LocalDateTime) row[3]);
                    tag.setVersion(((Number) row[4]).longValue());
                    return tag;
                })
                .toList();
            
            logger.debug("[{}] Found {} popular tags", correlationId, tags.size());
            return tags;
        } catch (Exception e) {
            logger.error("[{}] Error finding popular tags", correlationId, e);
            throw new RepositoryException("Error finding popular tags", e);
        }
    }
    
    @Override
    public Tag findOrCreate(String tagName) {
        String correlationId = UUID.randomUUID().toString().substring(0, 8);
        logger.debug("[{}] Finding or creating tag: {}", correlationId, tagName);
        
        try {
            // First try to find existing tag
            Tag existingTag = findByName(tagName);
            if (existingTag != null) {
                logger.debug("[{}] Found existing tag: {}", correlationId, tagName);
                return existingTag;
            }
            
            // Create new tag if not found
            Tag newTag = new Tag();
            newTag.setName(tagName);
            
            Tag savedTag = save(newTag);
            logger.debug("[{}] Created new tag: {} with ID: {}", correlationId, tagName, savedTag.getId());
            return savedTag;
        } catch (Exception e) {
            logger.error("[{}] Error finding or creating tag: {}", correlationId, tagName, e);
            throw new RepositoryException("Error finding or creating tag", e);
        }
    }
    
    @Override
    public List<Object[]> getTagUsageStatistics() {
        String correlationId = UUID.randomUUID().toString().substring(0, 8);
        logger.debug("[{}] Getting tag usage statistics", correlationId);
        
        try {
            Session session = sessionFactory.getCurrentSession();
            String sql = """
                SELECT t.name, COUNT(tt.task_id) as usage_count
                FROM tags t
                LEFT JOIN task_tags tt ON t.id = tt.tag_id
                GROUP BY t.id, t.name
                ORDER BY usage_count DESC, t.name ASC
                """;
            
            Query<Object[]> query = session.createNativeQuery(sql, Object[].class);
            List<Object[]> results = query.getResultList();
            
            logger.debug("[{}] Retrieved usage statistics for {} tags", correlationId, results.size());
            return results;
        } catch (Exception e) {
            logger.error("[{}] Error getting tag usage statistics", correlationId, e);
            throw new RepositoryException("Error getting tag usage statistics", e);
        }
    }
}

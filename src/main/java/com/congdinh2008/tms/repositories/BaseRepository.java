package com.congdinh2008.tms.repositories;

import java.util.List;
import java.util.Optional;

/**
 * Base repository interface providing common CRUD operations
 * with pagination and sorting support
 * 
 * @param <T> The entity type
 * @param <ID> The entity ID type
 */
public interface BaseRepository<T, ID> {
    
    /**
     * Save an entity
     * @param entity the entity to save
     * @return the saved entity
     */
    T save(T entity);
    
    /**
     * Find entity by ID
     * @param id the entity ID
     * @return the entity or null if not found
     */
    T findById(ID id);
    
    /**
     * Find entity by ID, returning Optional
     * @param id the entity ID
     * @return Optional containing the entity
     */
    Optional<T> findByIdOptional(ID id);
    
    /**
     * Find all entities
     * @return list of all entities
     */
    List<T> findAll();
    
    /**
     * Find all entities with pagination
     * @param page the page number (0-based)
     * @param size the page size
     * @return list of entities for the specified page
     */
    List<T> findAll(int page, int size);
    
    /**
     * Find all entities with pagination and sorting
     * @param page the page number (0-based)
     * @param size the page size
     * @param sortBy the field to sort by
     * @param sortDir the sort direction (ASC or DESC)
     * @return list of entities for the specified page
     */
    List<T> findAll(int page, int size, String sortBy, String sortDir);
    
    /**
     * Update an entity
     * @param entity the entity to update
     * @return the updated entity
     */
    T update(T entity);
    
    /**
     * Delete an entity
     * @param entity the entity to delete
     */
    void delete(T entity);
    
    /**
     * Delete entity by ID
     * @param id the entity ID
     */
    void deleteById(ID id);
    
    /**
     * Count total number of entities
     * @return the total count
     */
    long count();
    
    /**
     * Check if entity exists by ID
     * @param id the entity ID
     * @return true if exists, false otherwise
     */
    boolean existsById(ID id);
    
    /**
     * Flush the current session
     */
    void flush();
    
    /**
     * Clear the current session
     */
    void clear();
}

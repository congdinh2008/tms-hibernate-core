package com.congdinh2008.tms.services;

import java.util.List;

/**
 * Base service interface providing common operations for all service implementations
 * 
 * @param <T> The response DTO type
 * @param <I> The entity ID type
 * 
 * @author Cong Dinh
 * @version 1.0.0
 * @since 1.0.0
 */
public interface BaseService<T, I> {
    
    /**
     * Get entity by ID
     * 
     * @param id entity ID
     * @return entity response DTO
     */
    T getById(I id);
    
    /**
     * Delete entity by ID
     * 
     * @param id entity ID
     */
    void delete(I id);
    
    /**
     * Get all entities
     * 
     * @return list of entity response DTOs
     */
    List<T> getAll();
    
    /**
     * Count total number of entities
     * 
     * @return total count
     */
    long count();
    
    /**
     * Check if entity exists by ID
     * 
     * @param id entity ID
     * @return true if exists, false otherwise
     */
    boolean existsById(I id);
}

package com.congdinh2008.tms.utils;

import org.springframework.beans.BeanUtils;

/**
 * Utility class for mapping between DTOs and entities
 * 
 * @author Cong Dinh
 * @version 1.0.0
 * @since 1.0.0
 */
public final class MapperUtil {
    
    private MapperUtil() {
        // Private constructor to hide implicit public one
    }
    
    /**
     * Maps source object to target type
     * 
     * @param source the source object
     * @param targetClass the target class
     * @param <T> target type
     * @return mapped object
     */
    public static <T> T mapToDto(Object source, Class<T> targetClass) {
        if (source == null) {
            return null;
        }
        
        try {
            T target = targetClass.getDeclaredConstructor().newInstance();
            BeanUtils.copyProperties(source, target);
            return target;
        } catch (Exception e) {
            throw new IllegalArgumentException("Error mapping object to DTO: " + e.getMessage(), e);
        }
    }
    
    /**
     * Maps source object to target type
     * 
     * @param source the source object
     * @param targetClass the target class
     * @param <T> target type
     * @return mapped object
     */
    public static <T> T mapToEntity(Object source, Class<T> targetClass) {
        if (source == null) {
            return null;
        }
        
        try {
            T target = targetClass.getDeclaredConstructor().newInstance();
            BeanUtils.copyProperties(source, target);
            return target;
        } catch (Exception e) {
            throw new IllegalArgumentException("Error mapping DTO to entity: " + e.getMessage(), e);
        }
    }
    
    /**
     * Updates target entity with non-null values from source DTO
     * 
     * @param source the source DTO
     * @param target the target entity
     */
    public static void updateEntityFromDto(Object source, Object target) {
        if (source == null || target == null) {
            return;
        }
        
        try {
            BeanUtils.copyProperties(source, target);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error updating entity from DTO: " + e.getMessage(), e);
        }
    }
}

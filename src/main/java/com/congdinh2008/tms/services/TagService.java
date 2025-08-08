package com.congdinh2008.tms.services;

import com.congdinh2008.tms.dto.request.CreateTagRequest;
import com.congdinh2008.tms.dto.request.UpdateTagRequest;
import com.congdinh2008.tms.dto.response.TagResponse;

import java.util.List;

/**
 * Service interface for tag-related operations
 * 
 * @author Cong Dinh
 * @version 1.0.0
 * @since 1.0.0
 */
public interface TagService extends BaseService<TagResponse, Long> {
    
    /**
     * Create a new tag
     * 
     * @param request tag creation request
     * @return created tag response
     */
    TagResponse create(CreateTagRequest request);
    
    /**
     * Update an existing tag
     * 
     * @param id tag ID
     * @param request tag update request
     * @return updated tag response
     */
    TagResponse update(Long id, UpdateTagRequest request);
    
    /**
     * Find tag by name
     * 
     * @param name tag name
     * @return tag response or null if not found
     */
    TagResponse findByName(String name);
    
    /**
     * Search tags by name containing the specified string
     * 
     * @param name name fragment to search for
     * @return list of tags matching the search criteria
     */
    List<TagResponse> searchByName(String name);
    
    /**
     * Get all tags used by a specific project
     * 
     * @param projectId project ID
     * @return list of tags used in the project
     */
    List<TagResponse> getTagsByProject(Long projectId);
    
    /**
     * Get tags by color
     * 
     * @param color tag color
     * @return list of tags with the specified color
     */
    List<TagResponse> getTagsByColor(String color);
    
    /**
     * Get most used tags
     * 
     * @param limit maximum number of tags to return
     * @return list of most frequently used tags
     */
    List<TagResponse> getMostUsedTags(int limit);
}

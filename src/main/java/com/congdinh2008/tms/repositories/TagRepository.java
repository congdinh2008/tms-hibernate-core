package com.congdinh2008.tms.repositories;

import com.congdinh2008.tms.entities.Tag;
import java.util.List;

/**
 * Repository interface for Tag entity operations
 */
public interface TagRepository extends BaseRepository<Tag, Long> {
    
    /**
     * Find tag by name
     * @param name the tag name
     * @return the tag with the specified name, or null if not found
     */
    Tag findByName(String name);
    
    /**
     * Find tags by name containing the specified string (case-insensitive)
     * @param name the name fragment to search for
     * @return list of tags whose names contain the specified string
     */
    List<Tag> findByNameContaining(String name);
    
    /**
     * Check if tag exists by name
     * @param name the tag name to check
     * @return true if tag with name exists, false otherwise
     */
    boolean existsByName(String name);
    
    /**
     * Find popular tags (tags used in most tasks)
     * @param limit the maximum number of tags to return
     * @return list of popular tags ordered by usage count
     */
    List<Tag> findPopularTags(int limit);
    
    /**
     * Find or create a tag with the specified name
     * @param tagName the tag name
     * @return existing tag or newly created tag
     */
    Tag findOrCreate(String tagName);
    
    /**
     * Get tag usage statistics
     * @return list of objects containing tag and usage count
     */
    List<Object[]> getTagUsageStatistics();
}

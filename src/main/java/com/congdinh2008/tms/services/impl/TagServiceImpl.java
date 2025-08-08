package com.congdinh2008.tms.services.impl;

import com.congdinh2008.tms.dto.request.CreateTagRequest;
import com.congdinh2008.tms.dto.request.UpdateTagRequest;
import com.congdinh2008.tms.dto.response.TagResponse;
import com.congdinh2008.tms.entities.Tag;
import com.congdinh2008.tms.exceptions.DuplicateEntityException;
import com.congdinh2008.tms.exceptions.EntityNotFoundException;
import com.congdinh2008.tms.repositories.TagRepository;
import com.congdinh2008.tms.services.TagService;
import com.congdinh2008.tms.utils.MapperUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of TagService providing business logic for tag operations
 * 
 * @author Cong Dinh
 * @version 1.0.0
 * @since 1.0.0
 */
@Service
@Transactional
public class TagServiceImpl implements TagService {
    
    private static final Logger logger = LoggerFactory.getLogger(TagServiceImpl.class);
    private static final String TAG_ENTITY = "Tag";
    
    private final TagRepository tagRepository;
    
    public TagServiceImpl(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }
    
    @Override
    public TagResponse create(CreateTagRequest request) {
        logger.info("Creating tag with name: {}", request.getName());
        
        // Business Rule: Check for duplicate tag name
        Tag existingTag = tagRepository.findByName(request.getName());
        if (existingTag != null) {
            throw new DuplicateEntityException(TAG_ENTITY, "name", request.getName());
        }
        
        // Convert request to entity
        Tag tag = MapperUtil.mapToEntity(request, Tag.class);
        
        // Save tag
        Tag savedTag = tagRepository.save(tag);
        
        logger.info("Tag created successfully with ID: {}", savedTag.getId());
        return MapperUtil.mapToDto(savedTag, TagResponse.class);
    }
    
    @Override
    @Transactional(readOnly = true)
    public TagResponse getById(Long id) {
        logger.debug("Retrieving tag with ID: {}", id);
        
        Tag tag = tagRepository.findByIdOptional(id)
                .orElseThrow(() -> new EntityNotFoundException(TAG_ENTITY, id));
        
        return MapperUtil.mapToDto(tag, TagResponse.class);
    }
    
    @Override
    public TagResponse update(Long id, UpdateTagRequest request) {
        logger.info("Updating tag with ID: {}", id);
        
        Tag existingTag = tagRepository.findByIdOptional(id)
                .orElseThrow(() -> new EntityNotFoundException(TAG_ENTITY, id));
        
        // Business Rule: Check for duplicate name if name is being changed
        if (request.getName() != null && !request.getName().equals(existingTag.getName())) {
            Tag existingByName = tagRepository.findByName(request.getName());
            if (existingByName != null) {
                throw new DuplicateEntityException(TAG_ENTITY, "name", request.getName());
            }
        }
        
        // Update fields
        MapperUtil.updateEntityFromDto(request, existingTag);
        
        Tag updatedTag = tagRepository.save(existingTag);
        
        logger.info("Tag updated successfully with ID: {}", updatedTag.getId());
        return MapperUtil.mapToDto(updatedTag, TagResponse.class);
    }
    
    @Override
    public void delete(Long id) {
        logger.info("Deleting tag with ID: {}", id);
        
        Tag tag = tagRepository.findByIdOptional(id)
                .orElseThrow(() -> new EntityNotFoundException(TAG_ENTITY, id));
        
        tagRepository.delete(tag);
        logger.info("Tag deleted successfully with ID: {}", id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TagResponse> getAll() {
        logger.debug("Retrieving all tags");
        
        List<Tag> tags = tagRepository.findAll();
        List<TagResponse> responses = new ArrayList<>();
        
        for (Tag tag : tags) {
            responses.add(MapperUtil.mapToDto(tag, TagResponse.class));
        }
        
        return responses;
    }
    
    @Override
    @Transactional(readOnly = true)
    public long count() {
        return tagRepository.count();
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return tagRepository.existsById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public TagResponse findByName(String name) {
        logger.debug("Finding tag by name: {}", name);
        
        Tag tag = tagRepository.findByName(name);
        if (tag == null) {
            throw new EntityNotFoundException("Tag with name " + name + " not found");
        }
        
        return MapperUtil.mapToDto(tag, TagResponse.class);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TagResponse> searchByName(String name) {
        logger.debug("Searching tags by name: {}", name);
        
        List<Tag> tags = tagRepository.findByNameContaining(name);
        List<TagResponse> responses = new ArrayList<>();
        
        for (Tag tag : tags) {
            responses.add(MapperUtil.mapToDto(tag, TagResponse.class));
        }
        
        return responses;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TagResponse> getTagsByProject(Long projectId) {
        // Not implemented - method not available in repository
        logger.warn("getTagsByProject not implemented - method not available in TagRepository");
        return new ArrayList<>();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TagResponse> getTagsByColor(String color) {
        // Not implemented - method not available in repository
        logger.warn("getTagsByColor not implemented - method not available in TagRepository");
        return new ArrayList<>();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TagResponse> getMostUsedTags(int limit) {
        logger.debug("Retrieving most used tags with limit: {}", limit);
        
        List<Tag> tags = tagRepository.findPopularTags(limit);
        List<TagResponse> responses = new ArrayList<>();
        
        for (Tag tag : tags) {
            responses.add(MapperUtil.mapToDto(tag, TagResponse.class));
        }
        
        return responses;
    }
}

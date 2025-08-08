package com.congdinh2008.tms.services.impl;

import com.congdinh2008.tms.dto.request.CreateTagRequest;
import com.congdinh2008.tms.dto.request.UpdateTagRequest;
import com.congdinh2008.tms.dto.response.TagResponse;
import com.congdinh2008.tms.entities.Tag;
import com.congdinh2008.tms.exceptions.DuplicateEntityException;
import com.congdinh2008.tms.exceptions.EntityNotFoundException;
import com.congdinh2008.tms.repositories.TagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for TagServiceImpl
 * 
 * @author Cong Dinh
 * @version 1.0.0
 * @since 1.0.0
 */
class TagServiceImplTest {
    
    @Mock
    private TagRepository tagRepository;
    
    private TagServiceImpl tagService;
    
    private Tag testTag;
    private CreateTagRequest createRequest;
    private UpdateTagRequest updateRequest;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        tagService = new TagServiceImpl(tagRepository);
        
        testTag = new Tag();
        testTag.setId(1L);
        testTag.setName("Bug");
        testTag.setTasks(new ArrayList<>());
        
        createRequest = new CreateTagRequest();
        createRequest.setName("Feature");
        
        updateRequest = new UpdateTagRequest();
        updateRequest.setName("Enhancement");
    }
    
    @Test
    void create_ShouldCreateTag_WhenValidRequest() {
        // Given
        when(tagRepository.findByName(createRequest.getName())).thenReturn(null);
        when(tagRepository.save(any(Tag.class))).thenReturn(testTag);
        
        // When
        TagResponse result = tagService.create(createRequest);
        
        // Then
        assertNotNull(result);
        assertEquals(testTag.getId(), result.getId());
        verify(tagRepository).save(any(Tag.class));
    }
    
    @Test
    void create_ShouldThrowDuplicateEntityException_WhenTagNameExists() {
        // Given
        when(tagRepository.findByName(createRequest.getName())).thenReturn(testTag);
        
        // When & Then
        assertThrows(DuplicateEntityException.class, () -> tagService.create(createRequest));
        verify(tagRepository, never()).save(any(Tag.class));
    }
    
    @Test
    void getById_ShouldReturnTag_WhenTagExists() {
        // Given
        when(tagRepository.findByIdOptional(1L)).thenReturn(Optional.of(testTag));
        
        // When
        TagResponse result = tagService.getById(1L);
        
        // Then
        assertNotNull(result);
        assertEquals(testTag.getId(), result.getId());
        assertEquals(testTag.getName(), result.getName());
    }
    
    @Test
    void getById_ShouldThrowEntityNotFoundException_WhenTagNotFound() {
        // Given
        when(tagRepository.findByIdOptional(1L)).thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(EntityNotFoundException.class, () -> tagService.getById(1L));
    }
    
    @Test
    void update_ShouldUpdateTag_WhenValidRequest() {
        // Given
        when(tagRepository.findByIdOptional(1L)).thenReturn(Optional.of(testTag));
        when(tagRepository.findByName(updateRequest.getName())).thenReturn(null);
        when(tagRepository.save(any(Tag.class))).thenReturn(testTag);
        
        // When
        TagResponse result = tagService.update(1L, updateRequest);
        
        // Then
        assertNotNull(result);
        assertEquals(updateRequest.getName(), testTag.getName());
        verify(tagRepository).save(testTag);
    }
    
    @Test
    void update_ShouldAllowSameNameForSameTag() {
        // Given
        when(tagRepository.findByIdOptional(1L)).thenReturn(Optional.of(testTag));
        when(tagRepository.findByName(testTag.getName())).thenReturn(testTag);
        when(tagRepository.save(any(Tag.class))).thenReturn(testTag);
        
        updateRequest.setName(testTag.getName()); // Same name
        
        // When
        TagResponse result = tagService.update(1L, updateRequest);
        
        // Then
        assertNotNull(result);
        verify(tagRepository).save(testTag);
    }
    
    @Test
    void update_ShouldThrowDuplicateEntityException_WhenTagNameTakenByAnotherTag() {
        // Given
        Tag anotherTag = new Tag();
        anotherTag.setId(2L);
        anotherTag.setName(updateRequest.getName());
        
        when(tagRepository.findByIdOptional(1L)).thenReturn(Optional.of(testTag));
        when(tagRepository.findByName(updateRequest.getName())).thenReturn(anotherTag);
        
        // When & Then
        assertThrows(DuplicateEntityException.class, () -> tagService.update(1L, updateRequest));
        verify(tagRepository, never()).save(any(Tag.class));
    }
    
    @Test
    void delete_ShouldDeleteTag_WhenTagExists() {
        // Given
        when(tagRepository.findByIdOptional(1L)).thenReturn(Optional.of(testTag));
        
        // When
        tagService.delete(1L);
        
        // Then
        verify(tagRepository).delete(testTag);
    }
    
    @Test
    void delete_ShouldThrowEntityNotFoundException_WhenTagNotFound() {
        // Given
        when(tagRepository.findByIdOptional(1L)).thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(EntityNotFoundException.class, () -> tagService.delete(1L));
        verify(tagRepository, never()).delete(any(Tag.class));
    }
    
    @Test
    void getAll_ShouldReturnAllTags() {
        // Given
        List<Tag> tags = List.of(testTag);
        when(tagRepository.findAll()).thenReturn(tags);
        
        // When
        List<TagResponse> result = tagService.getAll();
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testTag.getId(), result.get(0).getId());
    }
    
    @Test
    void findByName_ShouldReturnTag_WhenNameExists() {
        // Given
        when(tagRepository.findByName("Bug")).thenReturn(testTag);
        
        // When
        TagResponse result = tagService.findByName("Bug");
        
        // Then
        assertNotNull(result);
        assertEquals(testTag.getId(), result.getId());
        assertEquals(testTag.getName(), result.getName());
    }
    
    @Test
    void findByName_ShouldThrowEntityNotFoundException_WhenNameNotFound() {
        // Given
        when(tagRepository.findByName("NonExistent")).thenReturn(null);
        
        // When & Then
        assertThrows(EntityNotFoundException.class, () -> tagService.findByName("NonExistent"));
    }
    
    @Test
    void searchByName_ShouldReturnTags_WhenNameMatches() {
        // Given
        when(tagRepository.findByNameContaining("Bu")).thenReturn(List.of(testTag));
        
        // When
        List<TagResponse> result = tagService.searchByName("Bu");
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testTag.getId(), result.get(0).getId());
    }
    
    @Test
    void searchByName_ShouldReturnEmptyList_WhenNoMatches() {
        // Given
        when(tagRepository.findByNameContaining("XYZ")).thenReturn(List.of());
        
        // When
        List<TagResponse> result = tagService.searchByName("XYZ");
        
        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}

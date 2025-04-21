package com.pht.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import com.pht.dto.request.CategoryRequest;
import com.pht.entity.CategoryEntity;
import com.pht.exception.data.DataConflictException;
import com.pht.exception.data.EntityNotFoundException;
import com.pht.exception.file.UploadFileException;
import com.pht.exception.security.AccessDeniedException;
import com.pht.repo.CategoryRepo;
import com.pht.repo.ProductRepo;
import com.pht.service.impl.CategoryServiceImpl;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceImplTest {

    @Mock
    private CategoryRepo categoryRepo;

    @Mock
    private ProductRepo productRepo;

    @Mock
    private MetafileService metafileService;

    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private CategoryEntity category;
    private CategoryRequest categoryRequest;
    private UUID fileId;

    @BeforeEach
    void setUp() {
        fileId = UUID.randomUUID();

        categoryRequest = new CategoryRequest();
        categoryRequest.setName("Test Category");
        categoryRequest.setFile(multipartFile);

        category = CategoryEntity.builder()
                .id(1L)
                .name("Test Category")
                .image(fileId)
                .isDeleted(false)
                .build();
    }

    @Test
    void testGetAll_ShouldReturnListOfCategories() {
        // Arrange
        List<CategoryEntity> expectedCategories = Arrays.asList(category);
        when(categoryRepo.findAllByIsDeletedIsFalse()).thenReturn(expectedCategories);

        // Act
        List<CategoryEntity> result = categoryService.getAll();

        // Assert
        assertEquals(expectedCategories, result);
        verify(categoryRepo).findAllByIsDeletedIsFalse();
    }

    @Test
    void testCreate_ShouldCreateAndReturnCategory() {
        // Arrange
        when(metafileService.save(any(MultipartFile.class))).thenReturn(fileId);
        when(categoryRepo.save(any(CategoryEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        CategoryEntity result = categoryService.create(categoryRequest);

        // Assert
        assertNotNull(result);
        assertEquals(categoryRequest.getName(), result.getName());
        assertEquals(fileId, result.getImage());
        verify(metafileService).save(multipartFile);
        verify(categoryRepo).save(any(CategoryEntity.class));
    }

    @Test
    void testCreate_WhenFileUploadFails_ShouldThrowException() {
        // Arrange
        when(metafileService.save(any(MultipartFile.class))).thenReturn(null);

        // Act & Assert
        assertThrows(UploadFileException.class, () -> categoryService.create(categoryRequest));
        verify(metafileService).save(multipartFile);
        verify(categoryRepo, never()).save(any(CategoryEntity.class));
    }

    @Test
    void testUpdate_ShouldUpdateAndReturnCategory() {
        // Arrange
        long categoryId = 1L;
        when(categoryRepo.findById(categoryId)).thenReturn(Optional.of(category));
        when(metafileService.save(any(MultipartFile.class))).thenReturn(fileId);
        when(categoryRepo.save(any(CategoryEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        CategoryEntity result = categoryService.update(categoryRequest, categoryId);

        // Assert
        assertNotNull(result);
        assertEquals(categoryRequest.getName(), result.getName());
        assertEquals(fileId, result.getImage());
        verify(categoryRepo).findById(categoryId);
        verify(metafileService).delete(category.getImage());
        verify(metafileService).save(multipartFile);
        verify(categoryRepo).save(category);
    }

    @Test
    void testUpdate_WhenCategoryNotFound_ShouldThrowException() {
        // Arrange
        long categoryId = 1L;
        when(categoryRepo.findById(categoryId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> categoryService.update(categoryRequest, categoryId));
        verify(categoryRepo).findById(categoryId);
        verify(metafileService, never()).delete(any());
        verify(metafileService, never()).save(any());
    }

    @Test
    void testUpdate_WhenCategoryIsDeleted_ShouldThrowException() {
        // Arrange
        long categoryId = 1L;
        category.setDeleted(true);
        when(categoryRepo.findById(categoryId)).thenReturn(Optional.of(category));

        // Act & Assert
        assertThrows(AccessDeniedException.class, () -> categoryService.update(categoryRequest, categoryId));
        verify(categoryRepo).findById(categoryId);
        verify(metafileService, never()).delete(any());
        verify(metafileService, never()).save(any());
    }

    @Test
    void testUpdate_WhenFileUploadFails_ShouldThrowException() {
        // Arrange
        long categoryId = 1L;
        when(categoryRepo.findById(categoryId)).thenReturn(Optional.of(category));
        when(metafileService.save(any(MultipartFile.class))).thenReturn(null);

        // Act & Assert
        assertThrows(UploadFileException.class, () -> categoryService.update(categoryRequest, categoryId));
        verify(categoryRepo).findById(categoryId);
        verify(metafileService).delete(category.getImage());
        verify(metafileService).save(multipartFile);
        verify(categoryRepo, never()).save(any());
    }

    @Test
    void testDelete_ShouldMarkCategoryAsDeleted() {
        // Arrange
        long categoryId = 1L;
        when(categoryRepo.findById(categoryId)).thenReturn(Optional.of(category));
        when(productRepo.existsByCategoryIdAndIsDeletedIsFalse(categoryId)).thenReturn(false);

        // Act
        categoryService.delete(categoryId);

        // Assert
        assertTrue(category.isDeleted());
        verify(categoryRepo).findById(categoryId);
        verify(productRepo).existsByCategoryIdAndIsDeletedIsFalse(categoryId);
        verify(categoryRepo).save(category);
    }

    @Test
    void testDelete_WhenCategoryNotFound_ShouldThrowException() {
        // Arrange
        long categoryId = 1L;
        when(categoryRepo.findById(categoryId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> categoryService.delete(categoryId));
        verify(categoryRepo).findById(categoryId);
        verify(productRepo, never()).existsByCategoryIdAndIsDeletedIsFalse(anyLong());
    }

    @Test
    void testDelete_WhenProductsExist_ShouldThrowException() {
        // Arrange
        long categoryId = 1L;
        when(categoryRepo.findById(categoryId)).thenReturn(Optional.of(category));
        when(productRepo.existsByCategoryIdAndIsDeletedIsFalse(categoryId)).thenReturn(true);

        // Act & Assert
        assertThrows(DataConflictException.class, () -> categoryService.delete(categoryId));
        verify(categoryRepo).findById(categoryId);
        verify(productRepo).existsByCategoryIdAndIsDeletedIsFalse(categoryId);
        verify(categoryRepo, never()).save(any());
    }
}

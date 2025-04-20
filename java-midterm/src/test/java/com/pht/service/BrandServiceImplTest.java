package com.pht.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import com.pht.dto.request.BrandRequest;
import com.pht.entity.BrandEntity;
import com.pht.exception.data.DataConflictException;
import com.pht.exception.data.EntityNotFoundException;
import com.pht.exception.file.UploadFileException;
import com.pht.exception.security.AccessDeniedException;
import com.pht.repo.BrandRepo;
import com.pht.repo.ProductRepo;
import com.pht.service.impl.BrandServiceImpl;

@ExtendWith(MockitoExtension.class)
public class BrandServiceImplTest {

    @Mock
    private BrandRepo repo;

    @Mock
    private ProductRepo productRepo;

    @Mock
    private MetafileService metafileService;

    @Mock
    private MultipartFile file;

    @InjectMocks
    private BrandServiceImpl brandService;

    private final long brandId = 1L;
    private final String brandName = "Test Brand";
    private final UUID imageId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

    @Test
    void testGetAll_ShouldReturnAllNonDeletedBrands() {
        // Arrange
        List<BrandEntity> expectedBrands = Arrays.asList(
                BrandEntity.builder().id(1L).name("Brand 1").image(
                        imageId).isDeleted(false).build(),
                BrandEntity.builder().id(2L).name("Brand 2").image(imageId).isDeleted(false).build());

        when(repo.findAllByIsDeletedIsFalse()).thenReturn(expectedBrands);

        // Act
        List<BrandEntity> result = brandService.getAll();

        // Assert
        assertEquals(expectedBrands, result);
        verify(repo).findAllByIsDeletedIsFalse();
    }

    @Test
    void testCreate_WithValidRequest_ShouldCreateBrand() {
        // Arrange
        BrandRequest request = new BrandRequest();
        request.setName(brandName);
        request.setFile(file);

        when(metafileService.save(file)).thenReturn(imageId);

        // Act
        BrandEntity result = brandService.create(request);

        // Assert
        assertEquals(brandName, result.getName());
        assertEquals(imageId, result.getImage());
        assertEquals(false, result.isDeleted());

        verify(metafileService).save(file);
        verify(repo).save(any(BrandEntity.class));
    }

    @Test
    void testCreate_WhenFileUploadFails_ShouldThrowUploadFileException() {
        // Arrange
        BrandRequest request = new BrandRequest();
        request.setName(brandName);
        request.setFile(file);

        when(metafileService.save(file)).thenReturn(null);

        // Act & Assert
        assertThrows(UploadFileException.class, () -> brandService.create(request));
        verify(metafileService).save(file);
        verifyNoInteractions(repo);
    }

    @Test
    void testUpdate_WithValidRequest_ShouldUpdateBrand() {
        // Arrange
        BrandEntity existingBrand = BrandEntity.builder()
                .id(brandId)
                .name("Old Brand")
                .image(imageId)
                .isDeleted(false)
                .build();

        BrandRequest request = new BrandRequest();
        request.setName("Updated Brand");
        request.setFile(file);

        when(repo.findById(brandId)).thenReturn(Optional.of(existingBrand));
        doNothing().when(metafileService).delete(imageId);
        when(metafileService.save(file)).thenReturn(imageId);

        // Act
        BrandEntity result = brandService.update(request, brandId);

        // Assert
        assertEquals("Updated Brand", result.getName());
        assertEquals(imageId, result.getImage());
        assertEquals(false, result.isDeleted());

        verify(repo).findById(brandId);
        verify(metafileService).delete(imageId);
        verify(metafileService).save(file);
        verify(repo).save(existingBrand);
    }

    @Test
    void testUpdate_WhenBrandNotFound_ShouldThrowEntityNotFoundException() {
        // Arrange
        BrandRequest request = new BrandRequest();
        request.setName("Updated Brand");
        request.setFile(file);

        when(repo.findById(brandId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> brandService.update(request, brandId));
        verify(repo).findById(brandId);
        verifyNoInteractions(metafileService);
    }

    @Test
    void testUpdate_WhenBrandIsDeleted_ShouldThrowAccessDeniedException() {
        // Arrange
        BrandEntity deletedBrand = BrandEntity.builder()
                .id(brandId)
                .name("Deleted Brand")
                .image(imageId)
                .isDeleted(true)
                .build();

        BrandRequest request = new BrandRequest();
        request.setName("Updated Brand");
        request.setFile(file);

        when(repo.findById(brandId)).thenReturn(Optional.of(deletedBrand));

        // Act & Assert
        assertThrows(AccessDeniedException.class, () -> brandService.update(request, brandId));
        verify(repo).findById(brandId);
        verifyNoInteractions(metafileService);
    }

    @Test
    void testUpdate_WhenFileUploadFails_ShouldThrowUploadFileException() {
        // Arrange
        BrandEntity existingBrand = BrandEntity.builder()
                .id(brandId)
                .name("Old Brand")
                .image(imageId)
                .isDeleted(false)
                .build();

        BrandRequest request = new BrandRequest();
        request.setName("Updated Brand");
        request.setFile(file);

        when(repo.findById(brandId)).thenReturn(Optional.of(existingBrand));
        doNothing().when(metafileService).delete(imageId);
        when(metafileService.save(file)).thenReturn(null);

        // Act & Assert
        assertThrows(UploadFileException.class, () -> brandService.update(request, brandId));
        verify(repo).findById(brandId);
        verify(metafileService).delete(imageId);
        verify(metafileService).save(file);
    }

    @Test
    void testDelete_WithValidIdAndNoProducts_ShouldMarkAsDeleted() {
        // Arrange
        BrandEntity existingBrand = BrandEntity.builder()
                .id(brandId)
                .name("Brand to Delete")
                .image(imageId)
                .isDeleted(false)
                .build();

        when(repo.findById(brandId)).thenReturn(Optional.of(existingBrand));
        when(productRepo.existsByBrandIdAndIsDeletedIsFalse(brandId)).thenReturn(false);

        // Act
        brandService.delete(brandId);

        // Assert
        assertEquals(true, existingBrand.isDeleted());
        verify(repo).findById(brandId);
        verify(productRepo).existsByBrandIdAndIsDeletedIsFalse(brandId);
        verify(repo).save(existingBrand);
    }

    @Test
    void testDelete_WhenBrandNotFound_ShouldThrowEntityNotFoundException() {
        // Arrange
        when(repo.findById(brandId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> brandService.delete(brandId));
        verify(repo).findById(brandId);
        verifyNoInteractions(productRepo);
    }

    @Test
    void testDelete_WhenBrandHasProducts_ShouldThrowDataConflictException() {
        // Arrange
        BrandEntity existingBrand = BrandEntity.builder()
                .id(brandId)
                .name("Brand with Products")
                .image(imageId)
                .isDeleted(false)
                .build();

        when(repo.findById(brandId)).thenReturn(Optional.of(existingBrand));
        when(productRepo.existsByBrandIdAndIsDeletedIsFalse(brandId)).thenReturn(true);

        // Act & Assert
        assertThrows(DataConflictException.class, () -> brandService.delete(brandId));
        verify(repo).findById(brandId);
        verify(productRepo).existsByBrandIdAndIsDeletedIsFalse(brandId);
        verify(repo, never()).save(any());
    }
}
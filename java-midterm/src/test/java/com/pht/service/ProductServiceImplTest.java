package com.pht.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.pht.dro.response.ProductResponse;
import com.pht.dto.CustomPaging;
import com.pht.dto.ProductSort;
import com.pht.dto.request.FilterRequest;
import com.pht.dto.request.ProductRequest;
import com.pht.entity.BrandEntity;
import com.pht.entity.CategoryEntity;
import com.pht.entity.ProductEntity;
import com.pht.exception.data.DataConflictException;
import com.pht.exception.data.EntityNotFoundException;
import com.pht.exception.file.UploadFileException;
import com.pht.repo.BrandRepo;
import com.pht.repo.CategoryRepo;
import com.pht.repo.ProductRepo;
import com.pht.service.impl.ProductServiceImpl;

@ExtendWith(MockitoExtension.class)
public class ProductServiceImplTest {
    @Mock
    private BrandRepo brandRepo;

    @Mock
    private CategoryRepo categoryRepo;

    @Mock
    private ProductRepo productRepo;

    @Mock
    private MetafileService metafileService;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    void testGetByFilter_ShouldReturnPagedProducts() {
        FilterRequest filter = new FilterRequest();
        filter.setSort(ProductSort.NEWEST);
        filter.setBrandId(1L);
        filter.setCategoryId(1L);
        filter.setKeyword("name");
        Pageable pageable = PageRequest.of(0, 12, Sort.by("createdAt").descending());

        BrandEntity brand = new BrandEntity(1L, "Brand 1", UUID.randomUUID(), false);
        CategoryEntity category = new CategoryEntity(1L, "Category 1", false, UUID.randomUUID());
        UUID imageId = UUID.randomUUID();

        ProductEntity product = new ProductEntity(1L, "Product 1", "Description", 100, imageId, 10, 10, false,
                LocalDate.now(), LocalDate.now(), category, brand);
        Page<ProductEntity> page = new PageImpl<>(List.of(product), pageable, 1);

        when(productRepo.getByFilter(eq(1L), eq(1L), eq("name"), eq(pageable))).thenReturn(page);
        CustomPaging<ProductResponse> result = productService.getByFilter(filter);

        assertNotNull(result);
    }

    @Test
    void testGetById_ShouldReturnProduct() {
        long productId = 1L;
        UUID imageId = UUID.randomUUID();
        BrandEntity brand = new BrandEntity(1L, "Brand 1", imageId, false);
        CategoryEntity category = new CategoryEntity(1L, "Category 1", false, imageId);
        ProductEntity mockProduct = new ProductEntity(1L, "Product 1", "Description", 100, imageId, 10, 10, false,
                LocalDate.now(), LocalDate.now(), category, brand);

        when(productRepo.findById(productId)).thenReturn(Optional.of(mockProduct));
        ProductResponse result = productService.getById(productId);

        assertNotNull(result);
        assertEquals(productId, result.getId());
        assertEquals("Product 1", result.getName());
    }

    @Test
    void testCreate_ShouldThrowException_WhenFileUploadFails() {
        ProductRequest request = new ProductRequest();
        when(metafileService.save(any())).thenReturn(null);
        assertThrows(UploadFileException.class, () -> productService.create(request));
    }

    @Test
    void testCreate_ShouldReturnProductResponse_WhenSuccessful() {
        // Arrange
        UUID imageId = UUID.randomUUID();
        long brandId = 1L;
        long categoryId = 1L;

        BrandEntity brand = BrandEntity.builder()
                .id(brandId).name("Brand 1").image(imageId).isDeleted(false)
                .build();
        CategoryEntity category = CategoryEntity.builder()
                .id(categoryId).name("Category 1").image(imageId).isDeleted(false)
                .build();

        ProductRequest request = new ProductRequest("Product 1", "Description", 1L, 1L, 10, 1L, 1L, null);

        when(metafileService.save(any())).thenReturn(imageId); // ✅ Quan trọng!
        when(brandRepo.findById(brandId)).thenReturn(Optional.of(brand));
        when(categoryRepo.findById(categoryId)).thenReturn(Optional.of(category));

        ArgumentCaptor<ProductEntity> captor = ArgumentCaptor.forClass(ProductEntity.class);
        when(productRepo.save(captor.capture())).thenAnswer(i -> i.getArgument(0));

        // Act
        ProductResponse result = productService.create(request);

        // Assert
        assertNotNull(result);
        assertEquals("Product 1", result.getName());
        assertEquals(imageId, result.getImage());
        assertEquals(brand, result.getBrand());
        assertEquals(category, result.getCategory());
    }

    @Test
    void testUpdate_ShouldThrowException_WhenProductNotFound() {
        long productId = 1L;
        ProductRequest request = new ProductRequest("Product 1", "Description", 1L, 1L, 10, 1L, 1L, null);

        when(productRepo.findById(productId)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> productService.update(request, productId));
    }

    @Test
    void testDelete_ShouldThrowException_WhenProductAlreadyDeleted() {
        long productId = 1L;
        UUID imageId = UUID.randomUUID();
        BrandEntity brand = new BrandEntity(1L, "Brand 1", imageId, false);
        CategoryEntity category = new CategoryEntity(1L, "Category 1", false, imageId);
        ProductEntity mockProduct = new ProductEntity();
        mockProduct.setId(1L);
        mockProduct.setName("Product 1");
        mockProduct.setDescription("Description");
        mockProduct.setPrice(100L);
        mockProduct.setImage(imageId);
        mockProduct.setStock(10);
        mockProduct.setDiscount(10);
        mockProduct.setDeleted(true);
        mockProduct.setCreatedAt(LocalDate.now());
        mockProduct.setUpdatedAt(LocalDate.now());
        mockProduct.setCategory(category);
        mockProduct.setBrand(brand);

        when(productRepo.findById(productId)).thenReturn(Optional.of(mockProduct));

        // Kiểm tra exception được ném ra
        assertThrows(DataConflictException.class, () -> productService.delete(productId));
    }

    @Test
    void testDelete_ShouldSuccessfullyDeleteProduct() {
        long productId = 1L;
        UUID imageId = UUID.randomUUID();
        BrandEntity brand = new BrandEntity(1L, "Brand 1", imageId, false);
        CategoryEntity category = new CategoryEntity(1L, "Category 1", false, imageId);
        ProductEntity mockProduct = new ProductEntity();
        mockProduct.setId(1L);
        mockProduct.setName("Product 1");
        mockProduct.setDescription("Description");
        mockProduct.setPrice(100L);
        mockProduct.setImage(imageId);
        mockProduct.setStock(10);
        mockProduct.setDiscount(10);
        mockProduct.setDeleted(false);
        mockProduct.setCreatedAt(LocalDate.now());
        mockProduct.setUpdatedAt(LocalDate.now());
        mockProduct.setCategory(category);
        mockProduct.setBrand(brand);
        when(productRepo.findById(productId)).thenReturn(Optional.of(mockProduct));

        productService.delete(productId);
        assertTrue(mockProduct.isDeleted());
        verify(productRepo).save(mockProduct);
    }
}

package com.pht.service.impl;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.pht.dro.response.ProductResponse;
import com.pht.dto.CustomPaging;
import com.pht.dto.request.FilterRequest;
import com.pht.dto.request.ProductRequest;
import com.pht.entity.ProductEntity;
import com.pht.exception.data.DataConflictException;
import com.pht.exception.data.EntityNotFoundException;
import com.pht.exception.file.UploadFileException;
import com.pht.exception.security.AccessDeniedException;
import com.pht.repo.BrandRepo;
import com.pht.repo.CategoryRepo;
import com.pht.repo.ProductRepo;
import com.pht.service.MetafileService;
import com.pht.service.ProductService;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductServiceImpl implements ProductService {
	BrandRepo brandRepo;
	CategoryRepo categoryRepo;
	ProductRepo productRepo;
	MetafileService metafileService;

	@Override
	public CustomPaging<ProductResponse> getByFilter(FilterRequest filter) {
		Pageable pageable = PageRequest.of(filter.getPage(), 12, filter.getSort());
		var pages = productRepo.getByFilter(filter.getBrandId(), filter.getCategoryId(), filter.getKeyword(), pageable);
		return new CustomPaging<ProductResponse>(pages, this::mapToResponse);
	}
	
	@Override
	public ProductResponse getById(long id) {
		var product = productRepo.findById(id).orElseThrow(() -> new EntityNotFoundException("Sản phẩm không tồn tại"));
		if (product.isDeleted()) {
			throw new AccessDeniedException("Sản phẩm đã bị xóa");
		}

		return mapToResponse(product);
	}

	@Transactional
	@Override
	public ProductResponse create(ProductRequest request) {
		var fileId = metafileService.save(request.getFile());
		if (fileId == null) {
			throw new UploadFileException("Lỗi khi lưu file");
		}
		var brand = brandRepo.findById(request.getBrandId())
				.orElseThrow(() -> new EntityNotFoundException("Không tồn tại nhãn hàng này"));
		if (brand.isDeleted()) {
			throw new AccessDeniedException("Nhãn hàng đã bị xóa");
		}
		var category = categoryRepo.findById(request.getCategoryId())
				.orElseThrow(() -> new EntityNotFoundException("Không tồn tại danh mục này"));
		if (category.isDeleted()) {
			throw new AccessDeniedException("Danh mục đã bị xóa");
		}

		var entity = mapToEntity(request);
		entity.setBrand(brand);
		entity.setCategory(category);
		entity.setImage(fileId);
		productRepo.save(entity);

		return mapToResponse(entity);
	}

	@Transactional
	@Override
	public ProductResponse update(ProductRequest request, long id) {
		var product = productRepo.findById(id).orElseThrow(() -> new EntityNotFoundException("Sản phẩm không tồn tại"));
		if (product.isDeleted()) {
			throw new AccessDeniedException("Sản phẩm đã bị xóa");
		}

		var brand = brandRepo.findById(request.getBrandId())
				.orElseThrow(() -> new EntityNotFoundException("Không tồn tại nhãn hàng này"));
		if (brand.isDeleted()) {
			throw new AccessDeniedException("Nhãn hàng đã bị xóa");
		}
		var category = categoryRepo.findById(request.getCategoryId())
				.orElseThrow(() -> new EntityNotFoundException("Không tồn tại danh mục này"));
		if (category.isDeleted()) {
			throw new AccessDeniedException("Danh mục đã bị xóa");
		}

		product.setBrand(brand);
		product.setCategory(category);
		product.setName(request.getName());
		product.setDiscount(request.getDiscount());
		product.setDescription(request.getDescription());
		product.setPrice(request.getPrice());
		product.setStock(request.getStock());

		metafileService.delete(product.getImage());
		var fileId = metafileService.save(request.getFile());
		if (fileId == null) {
			throw new UploadFileException("Lỗi khi lưu file");
		}
		product.setImage(fileId);
		productRepo.save(product);

		return mapToResponse(product);
	}

	@Override
	public void delete(long id) {
		var product = productRepo.findById(id).orElseThrow(() -> new EntityNotFoundException("Sản phẩm không tồn tại"));
		if (product.isDeleted()) {
			throw new DataConflictException("Sản phẩm đã bị xóa");
		}

		product.setDeleted(true);
		productRepo.save(product);
	}

//	@formatter:off
	private ProductEntity mapToEntity(ProductRequest request) {
		return ProductEntity.builder()
				.price(request.getPrice()).discount(request.getDiscount())
				.name(request.getName()).description(request.getDescription())
				.stock(request.getStock())
				.build();
	}
	
	private ProductResponse mapToResponse(ProductEntity entity) {
		return ProductResponse.builder()
				.id(entity.getId())
				.brand(entity.getBrand()).category(entity.getCategory())
				.price(entity.getPrice()).discount(entity.getDiscount())
				.name(entity.getName()).description(entity.getDescription())
				.stock(entity.getStock()).image(entity.getImage())
				.build();
	}
}

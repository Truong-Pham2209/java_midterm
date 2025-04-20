package com.pht.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.pht.dto.request.CategoryRequest;
import com.pht.entity.CategoryEntity;
import com.pht.exception.data.DataConflictException;
import com.pht.exception.data.EntityNotFoundException;
import com.pht.exception.file.UploadFileException;
import com.pht.exception.security.AccessDeniedException;
import com.pht.repo.CategoryRepo;
import com.pht.repo.ProductRepo;
import com.pht.service.CategoryService;
import com.pht.service.MetafileService;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryServiceImpl implements CategoryService {
	CategoryRepo repo;
	ProductRepo productRepo;
	MetafileService metafileService;

	@Override
	public List<CategoryEntity> getAll() {
		return repo.findAllByIsDeletedIsFalse();
	}

	@Transactional
	@Override
	public CategoryEntity create(CategoryRequest categoryRequest) {
		var fileId = metafileService.save(categoryRequest.getFile());
		if (fileId == null) {
			throw new UploadFileException("Lỗi khi lưu file");
		}
		var categry = CategoryEntity.builder().name(categoryRequest.getName()).image(fileId).build();
		repo.save(categry);
		return categry;
	}

	@Transactional
	@Override
	public CategoryEntity update(CategoryRequest categoryRequest, long id) {
		var category = repo.findById(id).orElseThrow(() -> new EntityNotFoundException("Danh mục k tồn tại"));
		if (category.isDeleted()) {
			throw new AccessDeniedException("Từ chối truy cập, Danh mục đã bị xóa");
		}
		metafileService.delete(category.getImage());
		var fileId = metafileService.save(categoryRequest.getFile());
		if (fileId == null) {
			throw new UploadFileException("Lỗi khi lưu file");
		}
		category.setImage(fileId);
		category.setName(categoryRequest.getName());
		repo.save(category);
		return category;
	}

	@Transactional
	@Override
	public void delete(long id) {
		var entity = repo.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Danh mục k tồn tại"));
		if (productRepo.existsByCategoryIdAndIsDeletedIsFalse(id)) {
			throw new DataConflictException("Đang còn các sản phẩm liên kết tới Danh mục không thể xóa");
		}
		entity.setDeleted(true);
		repo.save(entity);
	}
}

package com.pht.service;

import java.util.List;

import com.pht.dto.request.CategoryRequest;
import com.pht.entity.CategoryEntity;

public interface CategoryService {
	List<CategoryEntity> getAll();

	CategoryEntity create(CategoryRequest categoryRequest);

	CategoryEntity update(CategoryRequest categoryRequest, long id);

	void delete(long id);
}

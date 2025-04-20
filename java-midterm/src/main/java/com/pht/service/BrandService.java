package com.pht.service;

import java.util.List;

import com.pht.dto.request.BrandRequest;
import com.pht.entity.BrandEntity;

public interface BrandService {
	List<BrandEntity> getAll();

	BrandEntity create(BrandRequest brandRequest);

	BrandEntity update(BrandRequest brandRequest, long id);

	void delete(long id);
}

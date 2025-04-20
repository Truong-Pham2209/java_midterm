package com.pht.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.pht.dto.request.BrandRequest;
import com.pht.entity.BrandEntity;
import com.pht.exception.data.DataConflictException;
import com.pht.exception.data.EntityNotFoundException;
import com.pht.exception.file.UploadFileException;
import com.pht.exception.security.AccessDeniedException;
import com.pht.repo.BrandRepo;
import com.pht.repo.ProductRepo;
import com.pht.service.BrandService;
import com.pht.service.MetafileService;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BrandServiceImpl implements BrandService {
	BrandRepo repo;
	ProductRepo productRepo;
	MetafileService metafileService;

	@Override
	public List<BrandEntity> getAll() {
		return repo.findAllByIsDeletedIsFalse();
	}

	@Transactional
	@Override
	public BrandEntity create(BrandRequest brandRequest) {
		var fileId = metafileService.save(brandRequest.getFile());
		if (fileId == null) {
			throw new UploadFileException("Lỗi khi lưu file");
		}
		var brand = BrandEntity.builder().name(brandRequest.getName()).image(fileId).build();
		repo.save(brand);
		return brand;
	}

	@Transactional
	@Override
	public BrandEntity update(BrandRequest brandRequest, long id) {
		var brand = repo.findById(id).orElseThrow(() -> new EntityNotFoundException("Nhãn hàng k tồn tại"));
		if (brand.isDeleted()) {
			throw new AccessDeniedException("Từ chối truy cập, nhãn hàng đã bị xóa");
		}
		metafileService.delete(brand.getImage());
		var fileId = metafileService.save(brandRequest.getFile());
		if (fileId == null) {
			throw new UploadFileException("Lỗi khi lưu file");
		}
		brand.setImage(fileId);
		brand.setName(brandRequest.getName());
		repo.save(brand);
		return brand;
	}

	@Override
	public void delete(long id) {
		BrandEntity brandEntity = repo.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Nhãn hàng k tồn tại"));
		if (productRepo.existsByBrandIdAndIsDeletedIsFalse(id)) {
			throw new DataConflictException("Đang còn các sản phẩm liên kết tới nhãn hàng không thể xóa");
		}
		brandEntity.setDeleted(true);
		repo.save(brandEntity);
	}

}

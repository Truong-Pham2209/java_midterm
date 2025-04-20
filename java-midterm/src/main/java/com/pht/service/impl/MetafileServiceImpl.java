package com.pht.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.pht.entity.MetafileEntity;
import com.pht.exception.file.InvalidContentTypeException;
import com.pht.repo.MetafileRepo;
import com.pht.service.MetafileService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MetafileServiceImpl implements MetafileService {
	MetafileRepo repo;

	private final static String STORAGE_PATH = File.separator + "Data" + File.separator + "Uploads" + File.separator
			+ "Java_Midterm";

	@Override
	public Path getById(UUID id) {
		var entity = repo.findById(id).orElseThrow(() -> new EntityNotFoundException("File không tồn tại"));

		String filePath = entity.getFilePath();
		if (!new File(filePath).exists()) {
			log.error("Physical file not found for metafile ID: {} at path: {}", id, filePath);
			throw new EntityNotFoundException("Đường dẫn file không tồn tại");
		}

		return Paths.get(filePath);
	}

	@Override
	public void delete(UUID id) {
		try {
			var entity = repo.findById(id);
			if (entity.isEmpty())
				return;

			String path = entity.get().getFilePath();
			Files.deleteIfExists(Paths.get(path));
		} catch (IOException e) {
			log.error("Không thể xóa file: ", e.getMessage(), e);
		}
	}

	@Transactional
	@Override
	public UUID save(MultipartFile file) {
		File directory = new File(STORAGE_PATH);
		if (!directory.exists()) {
			directory.mkdirs();
		}

		if (file == null) {
			throw new InvalidContentTypeException("Không có file nào được gửi lên.");
		}

		if (file.isEmpty() || file.getContentType() == null || !file.getContentType().startsWith("image/")) {
			throw new InvalidContentTypeException("Nội dung file không hợp lệ, chỉ chấp nhận file ảnh.");
		}

		String storedFileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
		Path filePath = Paths.get(STORAGE_PATH, storedFileName);

		try {
			Files.copy(file.getInputStream(), filePath);
			var entity = MetafileEntity.builder().originalFileName(file.getOriginalFilename())
					.storedFileName(storedFileName).filePath(filePath.toString()).contentType(file.getContentType())
					.build();
			repo.save(entity);
			return entity.getId();
		} catch (IOException e) {
			log.error("Lỗi trong quá trình lưu file: ", e.getMessage(), e);
			return null;
		}
	}

}

package com.pht.service;

import java.nio.file.Path;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

public interface MetafileService {
	Path getById(UUID id);
	
	void delete(UUID id);
	
	UUID save(MultipartFile file);
}

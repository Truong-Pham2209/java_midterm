package com.pht.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.pht.dto.request.FilterRequest;
import com.pht.dto.request.ProductRequest;
import com.pht.service.ProductService;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductApi {
	ProductService service;

	@PostMapping("/filter/")
	public ResponseEntity<?> getAll(@RequestBody @Valid FilterRequest filter) {
		return ResponseEntity.ok(service.getByFilter(filter));
	}
	
	@GetMapping("/{id}/")
	public ResponseEntity<?> getById(@PathVariable("id") long id) {
		return ResponseEntity.ok(service.getById(id));
	}

	@PreAuthorize("hasAuthority('ADMIN')")
	@PostMapping(path = "/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> create(@RequestPart("product") @Valid ProductRequest request,
			@RequestPart("file") MultipartFile file) {
		if (file == null) {
			throw new IllegalArgumentException("Phải có ảnh mô tả");
		}
		request.setFile(file);
		var product = service.create(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(product);
	}

	@PreAuthorize("hasAuthority('ADMIN')")
	@PutMapping(path = "/{id}/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> update(@PathVariable("id") long id, @RequestPart("product") @Valid ProductRequest request,
			@RequestPart("file") MultipartFile file) {
		if (file == null) {
			throw new IllegalArgumentException("Phải có ảnh mô tả");
		}
		request.setFile(file);
		var product = service.update(request, id);
		return ResponseEntity.ok(product);
	}

	@PreAuthorize("hasAuthority('ADMIN')")
	@DeleteMapping("/{id}/")
	public ResponseEntity<?> delete(@PathVariable("id") long id) {
		service.delete(id);
		return ResponseEntity.ok().build();
	}
}

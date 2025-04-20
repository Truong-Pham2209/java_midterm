package com.pht.dto.request;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductRequest {
	@NotBlank(message = "Tên là bắt buộc")
	String name;
	
	@NotBlank(message = "Tên là bắt buộc")
	String description;
	
	@NotNull(message = "Tên là bắt buộc")
	@Min(value = 1000, message = "Giá tối thiểu là 1000")
	Long price;
	
	@NotNull(message = "Tồn kho là bắt buộc")
	@Min(value = 0, message = "Tồn kho tối thiểu là 0")
	long stock;
	
	@NotNull(message = "Giảm giá là bắt buộc")
	@Min(value = 0, message = "Giảm giá tối thiếu là 0")
	@Max(value = 99, message = "Giảm giá tối đa là 99")
	int discount;
	
	@NotNull(message = "Nhãn hàng là bắt buộc")
	Long brandId;
	
	@NotNull(message = "Danh mục là bắt buộc")
	Long categoryId;

	MultipartFile file;
}

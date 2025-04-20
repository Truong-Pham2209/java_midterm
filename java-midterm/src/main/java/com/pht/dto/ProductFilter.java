package com.pht.dto;

import org.springframework.data.domain.Sort;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductFilter {
	Long brandId;

	Long categoryId;

	String keyword;

	@NotNull(message = "Sắp xếp k được để trống")
	ProductSort sort;
	
	int page;
	
	public Sort getSort() {
		return null;
	}
}

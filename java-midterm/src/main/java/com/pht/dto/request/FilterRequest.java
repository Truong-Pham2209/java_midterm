package com.pht.dto.request;

import org.springframework.data.domain.Sort;

import com.pht.dto.ProductSort;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FilterRequest {
	Long brandId;
	Long categoryId;

	@NotNull(message = "Từ khóa không được null")
	String keyword;

	@NotNull(message = "Trường sắp xếp không được null")
	ProductSort sort;

	@Min(value = 0, message = "Phân  trang bé nhất là 0")
	int page;

	public Sort getSort() {
		return switch (sort) {
		case HIGHEST_COST -> Sort.by("price").descending();
		case LOWEST_COST -> Sort.by("price").ascending();
		case NEWEST -> Sort.by("createdAt").descending();
		};
	}
}

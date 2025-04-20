package com.pht.dro.response;

import java.util.UUID;

import com.pht.entity.BrandEntity;
import com.pht.entity.CategoryEntity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductResponse {
	long id;
	String name;
	String description;
	long price;
	long stock;
	int discount;
	UUID image;
	CategoryEntity category;
	BrandEntity brand;
}

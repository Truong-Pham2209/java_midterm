package com.pht.service;

import com.pht.dro.response.ProductResponse;
import com.pht.dto.CustomPaging;
import com.pht.dto.request.FilterRequest;
import com.pht.dto.request.ProductRequest;

public interface ProductService {
	CustomPaging<ProductResponse> getByFilter(FilterRequest filter);
	
	ProductResponse getById(long id);
	
	ProductResponse create(ProductRequest request);
	
	ProductResponse update(ProductRequest request, long id);
	
	void delete(long id);
}

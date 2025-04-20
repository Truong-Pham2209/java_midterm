package com.pht.service;

import java.util.List;

import com.pht.dto.CustomPaging;
import com.pht.dto.request.ConfirmedOrderRequest;
import com.pht.dto.request.OrderRequest;
import com.pht.dto.response.OrderResponse;

public interface OrderService {
	List<OrderResponse> getAllInCart();
	
	CustomPaging<OrderResponse> getOrderHistory(int page);
	
	void confirmedOrder(ConfirmedOrderRequest request);
	
	void addToCart(OrderRequest request);
	
	void delete(long id);
}

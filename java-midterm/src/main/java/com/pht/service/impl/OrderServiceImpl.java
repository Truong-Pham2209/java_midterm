package com.pht.service.impl;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.pht.dto.CustomPaging;
import com.pht.dto.OrderItem;
import com.pht.dto.request.ConfirmedOrderRequest;
import com.pht.dto.request.OrderRequest;
import com.pht.dto.response.OrderResponse;
import com.pht.entity.OrderEntity;
import com.pht.repo.OrderRepo;
import com.pht.repo.ProductRepo;
import com.pht.repo.UserRepo;
import com.pht.service.OrderService;
import com.pht.util.AuditingUtil;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderServiceImpl implements OrderService {
	OrderRepo orderRepo;
	UserRepo userRepo;
	ProductRepo productRepo;

	@Override
	public List<OrderResponse> getAllInCart() {
		String user = AuditingUtil.getCurrentUser();
		var orders = orderRepo.getAllOrderInCart(user);
		return orders.stream().map(o -> {
			long price = o.getProduct().getPrice();
			var order = mapToResponse(o);
			order.setPrice(price);
			return order;
		}).toList();
	}

	@Override
	public CustomPaging<OrderResponse> getOrderHistory(int page) {
		Pageable pageable = PageRequest.of(page, 10);
		String user = AuditingUtil.getCurrentUser();
		var pages = orderRepo.getHistoryOrder(user, pageable);

		return new CustomPaging<OrderResponse>(pages, o -> {
			var order = mapToResponse(o);
			order.setPrice(o.getPrice());
			return order;
		});
	}

	@Transactional
	@Override
	public void addToCart(OrderRequest request) {
		String username = AuditingUtil.getCurrentUser();
		var user = userRepo.findByUsername(username).orElse(null);
		if (user == null)
			return;

		var product = productRepo.findById(request.getProductId()).orElse(null);
		if (product == null || product.isDeleted())
			return;

		if (orderRepo.isProductInCart(product.getId(), user.getId()))
			return;

		var order = OrderEntity.builder().inCart(true).product(product).user(user).build();
		orderRepo.save(order);
	}

	@Transactional
	@Override
	public void delete(long id) {
		orderRepo.deleteById(id);
	}

	@Override
	public void confirmedOrder(ConfirmedOrderRequest request) {
		String username = AuditingUtil.getCurrentUser();
		List<OrderEntity> orders = orderRepo.findAllById(request.getItems().stream().map(OrderItem::getId).toList());
		for (OrderEntity o : orders) {
			if (!o.isInCart()) {
				continue;
			}

			if (!username.equals(o.getUser().getUsername()))
				continue;

			var item = request.getItems().stream().filter(i -> i.getId() == o.getId()).findFirst().orElse(null);
			if (item == null)
				continue;

			o.setAddress(request.getAddress());
			int price = (int) (o.getProduct().getPrice() * (100 - o.getProduct().getDiscount()) / 100);
			o.setPrice(price);
			o.setInCart(false);
			o.setQuantity(item.getQuantity());
		}

		orderRepo.saveAll(orders);
	}

//	@formatter:off
	private OrderResponse mapToResponse(OrderEntity entity) {
		return OrderResponse.builder()
				.id(entity.getId())
				.address(entity.getAddress())
				.discount(entity.getProduct().getDiscount())
				.image(entity.getProduct().getImage())
				.productName(entity.getProduct().getName())
				.quantity(entity.getQuantity())
				.build();
	}
}

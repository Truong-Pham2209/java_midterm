package com.pht.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pht.dto.request.ConfirmedOrderRequest;
import com.pht.dto.request.OrderRequest;
import com.pht.service.OrderService;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderApi {
	OrderService service;
	
	@PreAuthorize("hasAuthority('USER')")
	@GetMapping("/cart")
	public ResponseEntity<?> getCart() {
		return ResponseEntity.ok(service.getAllInCart());
	}
	
	@PreAuthorize("hasAuthority('USER')")
	@GetMapping("/history")
	public ResponseEntity<?> getHistory(@RequestParam int page) {
		return ResponseEntity.ok(service.getOrderHistory(page));
	}

	@PreAuthorize("hasAuthority('USER')")
	@PostMapping(path = "/cart")
	public ResponseEntity<?> addToCart(@RequestBody @Valid OrderRequest request) {
		service.addToCart(request);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@PreAuthorize("hasAuthority('USER')")
	@PostMapping(path = "/")
	public ResponseEntity<?> confirmedOrder(@RequestBody @Valid ConfirmedOrderRequest request) {
		service.confirmedOrder(request);
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	@PreAuthorize("hasAuthority('USER')")
	@DeleteMapping("/{id}/")
	public ResponseEntity<?> delete(@PathVariable("id") long id) {
		service.delete(id);
		return ResponseEntity.ok().build();
	}
}

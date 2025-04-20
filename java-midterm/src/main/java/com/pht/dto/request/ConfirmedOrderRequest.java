package com.pht.dto.request;

import java.util.List;

import com.pht.dto.OrderItem;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConfirmedOrderRequest {
	@NotEmpty(message = "Danh sách order không được phép trống")
	List<OrderItem> items;

	@NotBlank(message = "Địa chỉ không được để trống")
	String address;
}

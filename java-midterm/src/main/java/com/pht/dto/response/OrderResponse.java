package com.pht.dto.response;

import java.util.UUID;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class OrderResponse {
	long id;
	UUID image;
	String productName;
	long price;
	int quantity;
	int discount;
	String address;
}

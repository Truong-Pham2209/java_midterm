package com.pht.dro.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemResponse {
	String product;
	long productId;
	String image;
	int quantity;
	double subtotal;
	double discount;
	double cost;
	double productPrice;
}

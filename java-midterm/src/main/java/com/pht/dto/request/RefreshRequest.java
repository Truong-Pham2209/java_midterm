package com.pht.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RefreshRequest {
	@NotBlank(message = "Refresh Token không được để trống")
	String refreshToken;
}


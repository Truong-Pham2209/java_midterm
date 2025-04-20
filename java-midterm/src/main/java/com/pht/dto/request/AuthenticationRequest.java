package com.pht.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthenticationRequest {
	@NotBlank(message = "Tên đăng nhập là bắt buộc")
	String username;

	@NotBlank(message = "Mật khẩu là bắt buộc")
	String password;
}

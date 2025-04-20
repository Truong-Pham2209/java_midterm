package com.pht.dto.request;

import com.pht.dto.RoleCode;

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
public class UserRequest {
	@NotBlank(message = "Tên đăng nhập không được để trống")
	String username;
	
	@NotBlank(message = "Mật khẩu không được để trống")
	String password;
	
	@NotBlank(message = "Địa chỉ là bắt buộc")
	String address;
	
	@NotBlank(message = "SDT là bắt buộc")
	String phoneNumber;
	
	@NotBlank(message = "Vai trò là bắt buộc")
	RoleCode role;
}

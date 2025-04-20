package com.pht.api;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pht.dro.response.JwtResponse;
import com.pht.dto.request.AuthenticationRequest;
import com.pht.dto.request.RefreshRequest;
import com.pht.service.TokenService;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthenticateApi {
	TokenService service;
	AuthenticationManager authenticationManager;
	
	
	@PostMapping("/login")
	public JwtResponse getToken(@RequestBody @Valid AuthenticationRequest authRequest) throws Exception {
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
		if (!authentication.isAuthenticated())
			return null;

		return service.generateToken(authentication);
	}
	
	@PostMapping("/refresh-token")
	public JwtResponse refreshToken(@RequestBody @Valid RefreshRequest refresh) {
		return service.refresh(refresh.getRefreshToken());
	}
}

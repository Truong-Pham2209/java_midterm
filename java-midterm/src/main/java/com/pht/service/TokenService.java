package com.pht.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import com.pht.dro.response.JwtResponse;

public interface TokenService {
	JwtResponse generateToken(Authentication authentication);
	
	String extractUsername(String token);
	
	Boolean validateToken(String token, UserDetails userDetails);
	
	JwtResponse refresh(String refreshToken);
}

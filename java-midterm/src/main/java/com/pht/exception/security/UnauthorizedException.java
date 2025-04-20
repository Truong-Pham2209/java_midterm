package com.pht.exception.security;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends AbstractSecurityException {
	private static final long serialVersionUID = 1L;

	public UnauthorizedException(String msg) {
		super(msg, HttpStatus.UNAUTHORIZED);
	}
}

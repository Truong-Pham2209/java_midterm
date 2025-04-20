package com.pht.exception.security;

import org.springframework.http.HttpStatus;

public class AccessDeniedException extends AbstractSecurityException {
	private static final long serialVersionUID = 1L;

	public AccessDeniedException(String msg) {
		super(msg, HttpStatus.FORBIDDEN);
	}
}

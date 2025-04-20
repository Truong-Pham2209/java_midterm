package com.pht.exception.security;

import org.springframework.http.HttpStatus;

import com.pht.exception.AbstractException;

public abstract class AbstractSecurityException extends AbstractException{
	private static final long serialVersionUID = 1L;

	private final HttpStatus httpStatus;

	public AbstractSecurityException(String msg, HttpStatus httpStatus) {
		super(msg);
		this.httpStatus = httpStatus;
	}

	public HttpStatus getHttpStatus() {
		return httpStatus;
	}
}

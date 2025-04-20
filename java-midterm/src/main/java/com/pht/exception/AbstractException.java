package com.pht.exception;

import org.springframework.http.HttpStatus;

public abstract class AbstractException extends RuntimeException{
	private static final long serialVersionUID = 1L;
	public abstract HttpStatus getHttpStatus();

	public AbstractException(String msg) {
		super(msg);
	}
}

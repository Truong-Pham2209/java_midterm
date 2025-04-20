package com.pht.exception.data;

import org.springframework.http.HttpStatus;

import com.pht.exception.AbstractException;

public abstract class AbstractDataException extends AbstractException {
	private static final long serialVersionUID = 1L;

	private final HttpStatus httpStatus;

	public AbstractDataException(String msg, HttpStatus httpStatus) {
		super(msg);
		this.httpStatus = httpStatus;
	}

	public HttpStatus getHttpStatus() {
		return httpStatus;
	}
}

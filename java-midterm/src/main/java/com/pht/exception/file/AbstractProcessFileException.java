package com.pht.exception.file;

import org.springframework.http.HttpStatus;

import com.pht.exception.AbstractException;

public abstract class AbstractProcessFileException extends AbstractException {
	private static final long serialVersionUID = 1L;
	private final HttpStatus httpStatus;

	public AbstractProcessFileException(String message, HttpStatus status) {
		super(message);
		this.httpStatus = status;
	}

	public HttpStatus getHttpStatus() {
		return httpStatus;
	}
}
package com.pht.exception.file;

import org.springframework.http.HttpStatus;

public class InvalidContentTypeException extends AbstractProcessFileException {
	private static final long serialVersionUID = 1L;

	public InvalidContentTypeException(String message) {
		super(message, HttpStatus.BAD_REQUEST);
	}
}

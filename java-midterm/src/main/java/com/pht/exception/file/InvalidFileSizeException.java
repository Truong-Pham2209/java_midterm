package com.pht.exception.file;

import org.springframework.http.HttpStatus;

public class InvalidFileSizeException extends AbstractProcessFileException {
	private static final long serialVersionUID = 1L;

	public InvalidFileSizeException(String message) {
		super(message, HttpStatus.PAYLOAD_TOO_LARGE);
	}
}

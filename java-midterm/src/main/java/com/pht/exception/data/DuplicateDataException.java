package com.pht.exception.data;

import org.springframework.http.HttpStatus;

public class DuplicateDataException extends AbstractDataException {
	private static final long serialVersionUID = 1L;

	public DuplicateDataException(String message) {
		super(message, HttpStatus.CONFLICT);
	}

}

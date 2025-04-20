package com.pht.exception.data;

import org.springframework.http.HttpStatus;

public class EntityNotFoundException extends AbstractDataException {
	private static final long serialVersionUID = 1L;

	public EntityNotFoundException(String msg) {
		super(msg, HttpStatus.NOT_FOUND);
	}
}

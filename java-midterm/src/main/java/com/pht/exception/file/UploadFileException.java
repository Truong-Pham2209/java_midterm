package com.pht.exception.file;

import org.springframework.http.HttpStatus;

public class UploadFileException extends AbstractProcessFileException {
	private static final long serialVersionUID = 1L;

	public UploadFileException(String message) {
		super(message, HttpStatus.BAD_REQUEST);
	}
}

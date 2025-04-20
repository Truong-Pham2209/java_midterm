package com.pht.exception.data;

import org.springframework.http.HttpStatus;

public class DataConflictException extends AbstractDataException {
    private static final long serialVersionUID = 1L;

    public DataConflictException(String message) {
        super(message, HttpStatus.CONFLICT);
    }

}
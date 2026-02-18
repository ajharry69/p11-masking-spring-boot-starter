package com.github.ajharry69.demo.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Getter
public class KCBException extends ResponseStatusException {
    private final String errorCode;

    public KCBException(HttpStatus httpStatus, String errorCode) {
        super(httpStatus, errorCode);
        this.errorCode = errorCode;
    }
}

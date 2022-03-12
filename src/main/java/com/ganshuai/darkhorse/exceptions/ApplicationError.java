package com.ganshuai.darkhorse.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Getter
public class ApplicationError extends ResponseStatusException{
    private final HttpStatus status;
    private final String code;
    private final String message;

    public ApplicationError (HttpStatus status, String code, String message) {
        super(status);
        this.status = status;
        this.code = code;
        this.message = message;
    }
}

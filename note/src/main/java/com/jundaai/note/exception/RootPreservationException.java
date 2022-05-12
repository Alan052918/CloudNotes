package com.jundaai.note.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class RootPreservationException extends RuntimeException {

    public RootPreservationException(String operation) {
        super("Root folder is preserved, " + operation + " failed.");
    }

}

package com.jundaai.note.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class RootDeletionException extends RuntimeException {

    public RootDeletionException() {
        super("Root folder cannot be deleted.");
    }

}

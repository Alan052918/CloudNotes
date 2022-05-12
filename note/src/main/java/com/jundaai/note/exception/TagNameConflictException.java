package com.jundaai.note.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class TagNameConflictException extends RuntimeException {

    public TagNameConflictException(String tagName) {
        super("Tag name: " + tagName + " conflicts with an existing tag.");
    }

}

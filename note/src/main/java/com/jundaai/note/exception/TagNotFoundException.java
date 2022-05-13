package com.jundaai.note.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class TagNotFoundException extends RuntimeException {

    public TagNotFoundException(Long tagId, String tagName) {
        super("Tag by id: " + tagId + " and name: " + tagName + " was not found.");
    }

}

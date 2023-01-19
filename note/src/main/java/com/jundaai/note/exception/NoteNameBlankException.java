package com.jundaai.note.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(HttpStatus.BAD_REQUEST)
public class NoteNameBlankException extends RuntimeException {

    public NoteNameBlankException() {
        super("Note name cannot be blank (null or all whitespaces).");
    }
}

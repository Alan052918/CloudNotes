package com.jundaai.note.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class NoteNameConflictException extends RuntimeException {

    public NoteNameConflictException(String noteName) {
        super("Note name: " + noteName + " conflicts with an existing note under the same folder.");
    }

}

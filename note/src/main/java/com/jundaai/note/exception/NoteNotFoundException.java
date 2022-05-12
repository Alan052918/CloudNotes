package com.jundaai.note.exception;

public class NoteNotFoundException extends RuntimeException {

    public NoteNotFoundException(Long noteId) {
        super("Note by id :" + noteId + " was not found.");
    }

}

package com.jundaai.note.exception;

public class NoteMultipleFolderException extends RuntimeException {

    public NoteMultipleFolderException(Long noteId) {
        super("Note by id: " + noteId + " is not newly created and already has a folder.");
    }

}

package com.jundaai.note.exception;

public class FolderMultipleParentException extends RuntimeException {

    public FolderMultipleParentException(Long folderId) {
        super("Folder by id: " + folderId + " is not newly created and already has a parent.");
    }

}

package com.jundaai.note.exception;

public class FolderNotFoundException extends RuntimeException {

    public FolderNotFoundException(Long folderId) {
        super("Folder by id :" + folderId + " was not found.");
    }

}

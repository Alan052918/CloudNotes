package com.jundaai.note.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class FolderNotFoundException extends RuntimeException {

    public FolderNotFoundException(Long folderId) {
        super("Folder by id: " + folderId + " was not found.");
    }

}

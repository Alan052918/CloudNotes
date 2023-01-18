package com.jundaai.note.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(HttpStatus.CONFLICT)
public class FolderNameConflictException extends RuntimeException {

    public FolderNameConflictException(String folderName) {
        super("Folder name: " + folderName + " conflicts with an existing folder under the same parent.");
    }
}

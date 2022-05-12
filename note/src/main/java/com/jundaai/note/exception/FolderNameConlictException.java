package com.jundaai.note.exception;

public class FolderNameConlictException extends RuntimeException {

    public FolderNameConlictException(String folderName) {
        super("Folder name: " + folderName + " conflicts with an existing folder under the same parent.");
    }

}

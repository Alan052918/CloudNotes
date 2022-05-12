package com.jundaai.note.form;

import com.jundaai.note.model.Folder;
import com.jundaai.note.model.Note;

public record FolderUpdateForm(String newName,
                               Folder addSubFolder,
                               Folder deleteSubFolder,
                               Note addNote,
                               Note deleteNote) {
}

package com.jundaai.note.form;

import com.jundaai.note.model.Folder;

public record NoteUpdateForm(String newName,
                             String newContent,
                             Long toFolderId) {
}

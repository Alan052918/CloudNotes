package com.jundaai.note.form;

import com.jundaai.note.model.Folder;

public record FolderUpdateForm(String newName,
                               Folder moveToParent) {
}

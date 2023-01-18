package com.jundaai.note.form.note;

import javax.validation.constraints.NotBlank;


public record NoteUpdateForm(@NotBlank String updateType,
                             String newName,
                             String newContent,
                             Long toFolderId,
                             String tagName) {

}

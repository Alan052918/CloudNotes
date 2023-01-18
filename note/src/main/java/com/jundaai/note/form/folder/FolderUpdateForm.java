package com.jundaai.note.form.folder;

import javax.validation.constraints.NotBlank;


public record FolderUpdateForm(@NotBlank String updateType, String newName, Long toParentId) {

}

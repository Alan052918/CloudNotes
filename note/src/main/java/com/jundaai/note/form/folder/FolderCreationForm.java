package com.jundaai.note.form.folder;

import javax.validation.constraints.NotBlank;


public record FolderCreationForm(@NotBlank String name) {

}

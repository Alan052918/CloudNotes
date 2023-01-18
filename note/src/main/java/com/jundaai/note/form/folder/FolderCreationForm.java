package com.jundaai.note.form.folder;

import jakarta.validation.constraints.NotBlank;


public record FolderCreationForm(@NotBlank String name) {

}

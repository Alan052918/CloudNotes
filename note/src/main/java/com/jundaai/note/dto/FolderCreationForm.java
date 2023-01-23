package com.jundaai.note.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;


@Builder
public record FolderCreationForm(@NotBlank(message = "Folder name cannot be blank (null or all whitespaces).") String name) {

}

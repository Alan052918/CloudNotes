package com.jundaai.note.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;


@Builder
public record FolderCreationForm(@NotBlank(message = "folder name cannot be blank.") String name) {

}

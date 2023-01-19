package com.jundaai.note.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;


@Builder
public record NoteCreationForm(@NotBlank(message = "note name cannot be blank.") String name, String content) {

}

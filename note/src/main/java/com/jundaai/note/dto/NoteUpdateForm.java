package com.jundaai.note.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;


@Builder
public record NoteUpdateForm(@NotBlank(message = "Note update type cannot be blank.") String updateType,
                             String newName,
                             String newContent,
                             Long toFolderId,
                             String tagName) {

}

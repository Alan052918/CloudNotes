package com.jundaai.note.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;


@Builder
public record FolderUpdateForm(@NotBlank(message = "Folder update type cannot be blank.") String updateType,
                               String newName,
                               Long toParentId) {

}

package com.jundaai.note.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;


@Builder
public record TagOperationForm(@NotBlank(message = "Tag name cannot be blank.") String name) {

}

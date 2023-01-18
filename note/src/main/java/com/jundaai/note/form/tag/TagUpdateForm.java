package com.jundaai.note.form.tag;

import jakarta.validation.constraints.NotBlank;


public record TagUpdateForm(@NotBlank String newName) {

}

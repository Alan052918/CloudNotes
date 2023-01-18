package com.jundaai.note.form.tag;

import javax.validation.constraints.NotBlank;


public record TagCreationForm(@NotBlank String name) {

}

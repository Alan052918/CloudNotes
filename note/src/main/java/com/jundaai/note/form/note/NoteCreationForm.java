package com.jundaai.note.form.note;

import javax.validation.constraints.NotBlank;


public record NoteCreationForm(@NotBlank String name, String content) {

}

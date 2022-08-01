package com.jundaai.note.form.note;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public final class NoteCreationForm {

    @NotBlank
    private String name;
    private String content;

}

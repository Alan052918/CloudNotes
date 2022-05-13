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
public final class NoteUpdateForm {

    @NotBlank
    private String updateType;

    private String newName;
    private String newContent;
    private Long toFolderId;
    private String tagName;


}

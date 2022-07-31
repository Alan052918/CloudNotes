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

    /**
     * Folder name is not unique, notes in different folders can have the same name
     * Cannot move to a non-existing folder
     */
    private Long toFolderId;

    /**
     * Tag names are unique
     * A new tag is created if the name of the tag to add is not found
     */
    private String tagName;

}

package com.jundaai.note.form.folder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public final class FolderUpdateForm {

    @NotBlank
    private String updateType;
    private String newName;
    private Long toParentId;

}

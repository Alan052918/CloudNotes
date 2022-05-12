package com.jundaai.note.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sun.istack.NotNull;
import lombok.*;
import net.minidev.json.annotate.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.ZonedDateTime;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Note {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "note_id_sequence"
    )
    @SequenceGenerator(
            name = "note_id_sequence",
            sequenceName = "note_id_sequence"
    )
    private Long id;

    @NotBlank
    private String name;

    @Column(columnDefinition = "TEXT")
    private String content;

    @NotNull
    private ZonedDateTime createdAt;

    @NotNull
    private ZonedDateTime updatedAt;

    @ManyToOne
    @JoinColumn(
            name = "folder_id",
            referencedColumnName = "id"
    )
    @ToString.Exclude
    @JsonIgnoreProperties(value = {"parentFolder", "subFolders", "notes"})
    private Folder folder;

    @ManyToMany
    @JoinTable(
            name = "note_tag",
            joinColumns = @JoinColumn(
                    name = "note_id",
                    referencedColumnName = "id"
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "tag_id",
                    referencedColumnName = "id"
            )
    )
    @ToString.Exclude
    @JsonIgnore
    private List<Tag> tags;

}

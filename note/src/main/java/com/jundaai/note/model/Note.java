package com.jundaai.note.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.*;
import java.time.ZonedDateTime;

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

    private String name;

    @Column(columnDefinition = "TEXT")
    private String content;

    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;

    @ManyToOne
    @JoinColumn(
            name = "folder_id",
            referencedColumnName = "id"
    )
    @ToString.Exclude
    @JsonIgnoreProperties(value = {"parentFolder", "subFolders", "notes"})
    private Folder folder;

}

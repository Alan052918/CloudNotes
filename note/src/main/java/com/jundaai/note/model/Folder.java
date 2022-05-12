package com.jundaai.note.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import net.minidev.json.annotate.JsonIgnore;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Folder {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "folder_id_sequence"
    )
    @SequenceGenerator(
            name = "folder_id_sequence",
            sequenceName = "folder_id_sequence"
    )
    private Long id;

    private String name;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;

    @ManyToOne
    @JoinColumn(
            name = "parent_id",
            referencedColumnName = "id"
    )
    @ToString.Exclude
    @JsonIgnoreProperties(value = {"parentFolder", "subFolders", "notes"})
    private Folder parentFolder;

    @OneToMany(
            cascade = CascadeType.ALL,
            fetch = FetchType.EAGER,
            mappedBy = "parentFolder",
            orphanRemoval = true
    )
    @ToString.Exclude
    @JsonIgnoreProperties(value = {"parentFolder", "subFolders", "notes"})
    private List<Folder> subFolders;

    @OneToMany(
            cascade = CascadeType.ALL,
            fetch = FetchType.EAGER,
            mappedBy = "folder",
            orphanRemoval = true
    )
    @ToString.Exclude
    @JsonIgnoreProperties(value = {"content", "folder"})
    private List<Note> notes;

}

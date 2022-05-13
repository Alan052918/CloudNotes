package com.jundaai.note.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sun.istack.NotNull;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
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
            strategy = GenerationType.IDENTITY
    )
    private Long id;

    @NotBlank
    private String name;

    @NotNull
    private ZonedDateTime createdAt;

    @NotNull
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

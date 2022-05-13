package com.jundaai.note.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sun.istack.NotNull;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.ZonedDateTime;
import java.util.List;

@Entity
@Table(
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"name"})
        }
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @NotNull
    private ZonedDateTime createdAt;

    @NotNull
    private ZonedDateTime updatedAt;

    @ManyToMany(
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            },
            fetch = FetchType.EAGER,
            mappedBy = "tags"
    )
    @JsonIgnore
    private List<Note> notes;

}

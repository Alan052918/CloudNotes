package com.jundaai.note.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sun.istack.NotNull;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.ZonedDateTime;
import java.util.List;

@Entity
//@Table(
//        uniqueConstraints = {
//                @UniqueConstraint(columnNames = {"name"})
//        }
//)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Tag {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "tag_id_sequence"
    )
    @SequenceGenerator(
            name = "tag_id_sequence",
            sequenceName = "tag_id_sequence"
    )
    private Long id;

    @NotBlank
    private String name;

    @NotNull
    private ZonedDateTime createdAt;

    @NotNull
    private ZonedDateTime updatedAt;

    @ManyToMany(
            cascade = CascadeType.ALL,
            fetch = FetchType.EAGER,
            mappedBy = "tags"
    )
    @ToString.Exclude
    @JsonIgnore
    private List<Note> notes;
}

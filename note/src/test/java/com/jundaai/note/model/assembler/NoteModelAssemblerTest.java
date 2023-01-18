package com.jundaai.note.model.assembler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.jundaai.note.controller.FolderController;
import com.jundaai.note.controller.NoteController;
import com.jundaai.note.model.Folder;
import com.jundaai.note.model.Note;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;


@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class NoteModelAssemblerTest {

    private static final Folder folder = Folder.builder()
            .id(1L)
            .name("root")
            .createdAt(ZonedDateTime.now())
            .updatedAt(ZonedDateTime.now())
            .parentFolder(null)
            .subFolders(new ArrayList<>())
            .notes(new ArrayList<>())
            .build();
    @Autowired
    private NoteModelAssembler testModelAssembler;

    @Test
    public void toModel_Success() {
        // given
        ZonedDateTime now = ZonedDateTime.now();
        Note note = Note.builder()
                .id(1L)
                .name("Note")
                .createdAt(now)
                .updatedAt(now)
                .folder(folder)
                .tags(new ArrayList<>())
                .build();
        EntityModel<Note> expectedModel = EntityModel.of(note,
                linkTo(methodOn(NoteController.class).getNoteById(note.getId())).withSelfRel(),
                linkTo(methodOn(FolderController.class).getFolderById(note.getFolder().getId())).withRel("folder"),
                linkTo(methodOn(NoteController.class).getAllNotes()).withRel("all notes"));

        // when
        EntityModel<Note> gotModel = testModelAssembler.toModel(note);

        // then
        assertEquals(expectedModel, gotModel);
    }

    @Test
    public void toCollectionModel_Success() {
        // given
        ZonedDateTime now = ZonedDateTime.now();
        Note note1 = Note.builder()
                .id(1L)
                .name("Note")
                .createdAt(now)
                .updatedAt(now)
                .folder(folder)
                .tags(new ArrayList<>())
                .build();
        Note note2 = Note.builder()
                .id(2L)
                .name("Another note")
                .createdAt(now)
                .updatedAt(now)
                .folder(folder)
                .tags(new ArrayList<>())
                .build();
        List<Note> notes = List.of(note1, note2);
        CollectionModel<EntityModel<Note>> expectedModel = notes.stream()
                .map(note -> EntityModel.of(note,
                        linkTo(methodOn(NoteController.class).getNoteById(note.getId())).withSelfRel(),
                        linkTo(methodOn(FolderController.class).getFolderById(note.getFolder().getId()))
                                .withRel("folder"),
                        linkTo(methodOn(NoteController.class).getAllNotes()).withRel("all notes")))
                .collect(Collectors.collectingAndThen(Collectors.toList(), CollectionModel::of));

        // when
        CollectionModel<EntityModel<Note>> gotModel = testModelAssembler.toCollectionModel(notes);

        // then
        assertEquals(expectedModel, gotModel);
    }
}

package com.jundaai.note.controller;

import java.net.URI;
import java.util.List;

import com.jundaai.note.dto.NoteCreationForm;
import com.jundaai.note.dto.NoteUpdateForm;
import com.jundaai.note.model.Note;
import com.jundaai.note.model.assembler.NoteModelAssembler;
import com.jundaai.note.service.NoteService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;


@Validated
@RestController
@RequestMapping(path = "/api/v1")
@Slf4j
public class NoteController {

    private final NoteService noteService;
    private final NoteModelAssembler noteModelAssembler;

    public NoteController(NoteService noteService, NoteModelAssembler noteModelAssembler) {
        this.noteService = noteService;
        this.noteModelAssembler = noteModelAssembler;
    }

    @GetMapping(path = "notes")
    public ResponseEntity<CollectionModel<EntityModel<Note>>> getAllNotes() {
        log.info("Request to get all notes");
        final List<Note> notes = noteService.getAllNotes();
        return ResponseEntity.ok(noteModelAssembler.toCollectionModel(notes));
    }

    @GetMapping(path = "folders/{folderId}/notes")
    public ResponseEntity<CollectionModel<EntityModel<Note>>> getAllNotesByFolderId(
            @PathVariable(name = "folderId") Long folderId) {
        log.info("Request to get all notes by folder id: {}", folderId);
        final List<Note> notes = noteService.getAllNotesByFolderId(folderId);
        return ResponseEntity.ok(noteModelAssembler.toCollectionModel(notes));
    }

    @GetMapping(path = "tags/{tagId}/notes")
    public ResponseEntity<CollectionModel<EntityModel<Note>>> getAllNotesByTagId(
            @PathVariable(name = "tagId") Long tagId) {
        log.info("Request to get all notes by tag id: {}", tagId);
        final List<Note> notes = noteService.getAllNotesByTagId(tagId);
        return ResponseEntity.ok(noteModelAssembler.toCollectionModel(notes));
    }

    @GetMapping(path = "notes/{noteId}")
    public ResponseEntity<EntityModel<Note>> getNoteById(@PathVariable(name = "noteId") Long noteId) {
        log.info("Request to get note by note id: {}", noteId);
        final Note note = noteService.getNoteById(noteId);
        return ResponseEntity.ok(noteModelAssembler.toModel(note));
    }

    @PostMapping(path = "folders/{folderId}/notes")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<EntityModel<Note>> createNoteByFolderId(@PathVariable(name = "folderId") Long folderId,
                                                                  @Valid @RequestBody NoteCreationForm creationForm) {
        log.info("Request to create new note: {}, folder id: {}", creationForm, folderId);
        final Note note = noteService.createNoteByFolderId(folderId, creationForm);
        final URI uri = MvcUriComponentsBuilder.fromController(NoteController.class)
                .path("folders/{folderId}/notes")
                .buildAndExpand(note.getId()).toUri();
        return ResponseEntity.created(uri).body(noteModelAssembler.toModel(note));
    }

    @PatchMapping(path = "notes/{noteId}")
    public ResponseEntity<EntityModel<Note>> updateNoteById(@PathVariable(name = "noteId") Long noteId,
                                                            @Valid @RequestBody NoteUpdateForm updateForm) {
        log.info("Request to update note by id: {}, dto: {}", noteId, updateForm);
        final Note note = noteService.updateNoteById(noteId, updateForm);
        return ResponseEntity.ok(noteModelAssembler.toModel(note));
    }

    @DeleteMapping("notes/{noteId}")
    public ResponseEntity<?> deleteNoteById(@PathVariable(name = "noteId") Long noteId) {
        log.info("Request to delete note by id: {}", noteId);
        noteService.deleteNoteById(noteId);
        return ResponseEntity.noContent().build();
    }
}

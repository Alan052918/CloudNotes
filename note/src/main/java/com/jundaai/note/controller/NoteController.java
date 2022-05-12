package com.jundaai.note.controller;

import com.jundaai.note.form.create.NoteCreationForm;
import com.jundaai.note.form.update.NoteUpdateForm;
import com.jundaai.note.model.Note;
import com.jundaai.note.model.assembler.NoteModelAssembler;
import com.jundaai.note.service.NoteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/api/v1")
@Slf4j
public class NoteController {

    private final NoteService noteService;
    private final NoteModelAssembler noteModelAssembler;

    @Autowired
    public NoteController(NoteService noteService, NoteModelAssembler noteModelAssembler) {
        this.noteService = noteService;
        this.noteModelAssembler = noteModelAssembler;
    }

    @GetMapping(path = "notes")
    public CollectionModel<EntityModel<Note>> getAllNotes() {
        log.info("Request to get all notes");
        List<Note> notes = noteService.getAllNotes();
        return noteModelAssembler.toCollectionModel(notes);
    }

    @GetMapping(path = "folders/{folderId}/notes")
    public CollectionModel<EntityModel<Note>> getAllNotesByFolderId(@PathVariable(name = "folderId") Long folderId) {
        log.info("Request to get all notes by folder id: {}", folderId);
        List<Note> notes = noteService.getAllNotesByFolderId(folderId);
        return noteModelAssembler.toCollectionModel(notes);
    }

    @GetMapping(path = "notes/{noteId}")
    public EntityModel<Note> getNoteById(@PathVariable(name = "noteId") Long noteId) {
        log.info("Request to get note by note id: {}", noteId);
        Note note = noteService.getNoteById(noteId);
        return noteModelAssembler.toModel(note);
    }

    @PostMapping(path = "folders/{folderId}/notes")
    @ResponseStatus(HttpStatus.CREATED)
    public EntityModel<Note> createNoteByFolderId(@PathVariable(name = "folderId") Long folderId,
                                                  @Valid @RequestBody NoteCreationForm creationForm) {
        log.info("Request to create new note: {}, folder id: {}", creationForm, folderId);
        Note note = noteService.createNoteByFolderId(folderId, creationForm);
        return noteModelAssembler.toModel(note);
    }

    @PatchMapping(path = "notes/{noteId}")
    public EntityModel<Note> updateNoteById(@PathVariable(name = "noteId") Long noteId,
                                            @Valid @RequestBody NoteUpdateForm updateForm) {
        log.info("Request to update note by id: {}, form: {}", noteId, updateForm);
        Note note = noteService.updateNoteById(noteId, updateForm);
        return noteModelAssembler.toModel(note);
    }

    @DeleteMapping("notes/{noteId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<?> deleteNoteById(@PathVariable(name = "noteId") Long noteId) {
        log.info("Request to delete note by id: {}", noteId);
        noteService.deleteNoteById(noteId);
        return ResponseEntity.noContent().build();
    }

}

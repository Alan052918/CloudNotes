package com.jundaai.note.controller;

import com.jundaai.note.form.note.NoteCreationForm;
import com.jundaai.note.form.note.NoteUpdateForm;
import com.jundaai.note.model.Note;
import com.jundaai.note.model.assembler.NoteModelAssembler;
import com.jundaai.note.service.NoteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(NoteController.class)
public class NoteControllerTest extends ControllerTest {

    @MockBean
    private NoteService mockNoteService;
    @MockBean
    private NoteModelAssembler mockNoteModelAssembler;

    @Override
    @BeforeEach
    void setUp() {
        super.setUp();
        JacksonTester.initFields(this, mapper);
        mockMvc = MockMvcBuilders
                .standaloneSetup(new NoteController(mockNoteService, mockNoteModelAssembler))
                .build();
    }

    @Test
    public void getAllNotes_200Ok() throws Exception {
        // given
        CollectionModel<EntityModel<Note>> collectionModel = mockNotes
                .stream()
                .map(note -> EntityModel.of(
                        note,
                        linkTo(methodOn(NoteController.class).getNoteById(note.getId())).withSelfRel()
                ))
                .collect(Collectors.collectingAndThen(Collectors.toList(), CollectionModel::of));

        // when, then
        when(mockNoteService.getAllNotes()).thenReturn(mockNotes);
        when(mockNoteModelAssembler.toCollectionModel(mockNotes)).thenReturn(collectionModel);
        mockMvc
                .perform(get(BASE_PATH + NOTE_PATH).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].id").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].name").value("Go"));

        verify(mockNoteService).getAllNotes();
        verify(mockNoteModelAssembler).toCollectionModel(mockNotes);
    }

    @Test
    public void getAllNotesByFolderId_200Ok() throws Exception {
        // given
        Long testId = mockFolderIds.get(1);
        CollectionModel<EntityModel<Note>> collectionModel = mockNotes
                .stream()
                .map(note -> EntityModel.of(
                        note,
                        linkTo(methodOn(NoteController.class).getNoteById(note.getId())).withSelfRel()
                ))
                .collect(Collectors.collectingAndThen(Collectors.toList(), CollectionModel::of));

        // when, then
        when(mockNoteService.getAllNotesByFolderId(testId)).thenReturn(mockNotes);
        when(mockNoteModelAssembler.toCollectionModel(mockNotes)).thenReturn(collectionModel);
        mockMvc
                .perform(get(BASE_PATH + FOLDER_PATH + "/" + testId + "/notes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].id").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].name").value("Go"));

        verify(mockNoteService).getAllNotesByFolderId(testId);
        verify(mockNoteModelAssembler).toCollectionModel(mockNotes);
    }

    @Test
    public void getAllNotesByTagId_200Ok() throws Exception {
        // given
        Long testId = mockTagIds.get(0);
        CollectionModel<EntityModel<Note>> collectionModel = mockNotes
                .stream()
                .map(note -> EntityModel.of(
                        note,
                        linkTo(methodOn(NoteController.class).getNoteById(note.getId())).withSelfRel()
                ))
                .collect(Collectors.collectingAndThen(Collectors.toList(), CollectionModel::of));

        // when, then
        when(mockNoteService.getAllNotesByTagId(testId)).thenReturn(mockNotes);
        when(mockNoteModelAssembler.toCollectionModel(mockNotes)).thenReturn(collectionModel);
        mockMvc
                .perform(get(BASE_PATH + TAG_PATH + "/" + testId + "/notes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].id").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].name").value("Go"));

        verify(mockNoteService).getAllNotesByTagId(testId);
        verify(mockNoteModelAssembler).toCollectionModel(mockNotes);
    }

    @Test
    public void getNoteById_200Ok() throws Exception {
        // given
        Long testId = mockNoteIds.get(0);
        Note testNote = mockNotes.get(0);
        EntityModel<Note> entityModel = EntityModel.of(
                testNote,
                linkTo(methodOn(NoteController.class).getNoteById(testId)).withSelfRel()
        );

        // when, then
        when(mockNoteService.getNoteById(testId)).thenReturn(testNote);
        when(mockNoteModelAssembler.toModel(testNote)).thenReturn(entityModel);
        mockMvc
                .perform(get(BASE_PATH + NOTE_PATH + "/" + testId).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(8))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Go"));

        verify(mockNoteService).getNoteById(testId);
        verify(mockNoteModelAssembler).toModel(testNote);
    }

    @Test
    public void createNoteByFolderId_201Created() throws Exception {
        // given
        Long testId = mockFolderIds.get(0);
        Long newId = 100L;
        ZonedDateTime now = ZonedDateTime.now();
        Note newNote = Note
                .builder()
                .id(newId)
                .name("New Note")
                .content("This is a new note.")
                .createdAt(now)
                .updatedAt(now)
                .folder(mockFolders.get(0))
                .tags(new ArrayList<>())
                .build();
        EntityModel<Note> entityModel = EntityModel.of(
                newNote,
                linkTo(methodOn(NoteController.class).getNoteById(newId)).withSelfRel()
        );
        String requestBody = """
                {
                    "name": "New Note",
                    "content": "This is a new note."
                }
                """;

        // when, then
        when(mockNoteService.createNoteByFolderId(eq(testId), any(NoteCreationForm.class))).thenReturn(newNote);
        when(mockNoteModelAssembler.toModel(newNote)).thenReturn(entityModel);
        mockMvc
                .perform(MockMvcRequestBuilders
                        .post(BASE_PATH + FOLDER_PATH + "/" + testId + "/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(8))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(100))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("New Note"));

        verify(mockNoteService).createNoteByFolderId(eq(testId), any(NoteCreationForm.class));
        verify(mockNoteModelAssembler).toModel(newNote);
    }

    @Test
    public void updateNoteById_200Ok() throws Exception {
        // given
        Note testNote = mockNotes.get(0);

        assertEquals("Go", testNote.getName());

        Long testId = testNote.getId();
        ZonedDateTime now = ZonedDateTime.now();
        testNote.setName("New Name");
        testNote.setUpdatedAt(now);
        EntityModel<Note> entityModel = EntityModel.of(
                testNote,
                linkTo(methodOn(NoteController.class).getNoteById(testId)).withSelfRel()
        );
        String requestBody = """
                {
                    "updateType": "RENAME_NOTE",
                    "newName": "New Name"
                }
                """;

        // when, then
        when(mockNoteService.updateNoteById(eq(testId), any(NoteUpdateForm.class))).thenReturn(testNote);
        when(mockNoteModelAssembler.toModel(testNote)).thenReturn(entityModel);
        mockMvc
                .perform(MockMvcRequestBuilders
                        .patch(BASE_PATH + NOTE_PATH + "/" + testId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(8))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("New Name"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.updatedAt").value(now.toEpochSecond()));

        verify(mockNoteService).updateNoteById(eq(testId), any(NoteUpdateForm.class));
        verify(mockNoteModelAssembler).toModel(testNote);
    }

    @Test
    public void deleteNoteById_204NoContent() throws Exception {
        // given
        Long testId = mockNoteIds.get(0);

        // when, then
        mockMvc
                .perform(MockMvcRequestBuilders
                        .delete(BASE_PATH + NOTE_PATH + "/" + testId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        verify(mockNoteService).deleteNoteById(testId);
    }

}

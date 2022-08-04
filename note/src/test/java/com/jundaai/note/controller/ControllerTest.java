package com.jundaai.note.controller;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jundaai.note.model.Folder;
import com.jundaai.note.model.Note;
import com.jundaai.note.model.Tag;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ExtendWith(MockitoExtension.class)
public class ControllerTest {

    MockMvc mockMvc;

    ObjectMapper mapper;
    ListAppender<ILoggingEvent> loggingEventListAppender;

    List<Folder> mockFolders;
    List<Note> mockNotes;
    List<Tag> mockTags;
    List<Long> mockFolderIds;
    List<Long> mockNoteIds;
    List<Long> mockTagIds;

    static final String BASE_PATH = "http://localhost/api/v1";
    static final String FOLDER_PATH = "/folders";
    static final String NOTE_PATH = "/notes";
    static final String TAG_PATH = "/tags";

    @BeforeEach
    void setUp() {
        mapper = JsonMapper
                .builder()
                .addModule(new JavaTimeModule())
                .build();
        loggingEventListAppender = new ListAppender<>();
        loggingEventListAppender.start();

        mockFolders = new ArrayList<>();
        mockNotes = new ArrayList<>();
        mockTags = new ArrayList<>();
        loadFoldersNotesAndTags();
    }

    void loadFoldersNotesAndTags() {
        ZonedDateTime now = ZonedDateTime.now();
        Folder root = Folder
                .builder()
                .id(0L)
                .name("root")
                .createdAt(now)
                .updatedAt(now)
                .parentFolder(null)
                .subFolders(new ArrayList<>())
                .notes(new ArrayList<>())
                .build();
        Folder pl = Folder
                .builder()
                .id(1L)
                .name("Programming Languages")
                .createdAt(now)
                .updatedAt(now)
                .parentFolder(root)
                .subFolders(new ArrayList<>())
                .notes(new ArrayList<>())
                .build();
        Folder ds = Folder
                .builder()
                .id(2L)
                .name("Data Structures")
                .createdAt(now)
                .updatedAt(now)
                .parentFolder(root)
                .subFolders(new ArrayList<>())
                .notes(new ArrayList<>())
                .build();
        Note go = Note
                .builder()
                .id(3L)
                .name("Go")
                .content("Go is a general purpose programming language.")
                .createdAt(now)
                .updatedAt(now)
                .folder(pl)
                .tags(new ArrayList<>())
                .build();
        Tag google = Tag
                .builder()
                .id(4L)
                .name("Google")
                .createdAt(now)
                .updatedAt(now)
                .notes(new ArrayList<>())
                .build();
        Tag microsoft = Tag
                .builder()
                .id(5L)
                .name("Microsoft")
                .createdAt(now)
                .updatedAt(now)
                .notes(new ArrayList<>())
                .build();
        List<Tag> tags = go.getTags();
        tags.add(google);
        go.setTags(tags);
        List<Note> notes = new ArrayList<>();
        notes.add(go);
        pl.setNotes(notes);
        google.setNotes(notes);
        mockFolders.add(root);
        mockFolders.add(pl);
        mockFolders.add(ds);
        mockNotes.add(go);
        mockTags.add(google);
        mockTags.add(microsoft);
        mockFolderIds = mockFolders
                .stream()
                .map(Folder::getId)
                .collect(Collectors.toList());
        mockNoteIds = mockNotes
                .stream()
                .map(Note::getId)
                .collect(Collectors.toList());
        mockTagIds = mockTags
                .stream()
                .map(Tag::getId)
                .collect(Collectors.toList());
    }

    @AfterEach
    void tearDown() {

    }

}

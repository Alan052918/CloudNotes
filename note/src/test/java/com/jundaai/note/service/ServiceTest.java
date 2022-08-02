package com.jundaai.note.service;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.jundaai.note.model.Folder;
import com.jundaai.note.model.Note;
import com.jundaai.note.model.Tag;
import com.jundaai.note.repository.FolderRepository;
import com.jundaai.note.repository.NoteRepository;
import com.jundaai.note.repository.TagRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ServiceTest {

    AutoCloseable autoCloseable;

    @Mock
    FolderRepository mockFolderRepository;
    @Mock
    NoteRepository mockNoteRepository;
    @Mock
    TagRepository mockTagRepository;

    ArgumentCaptor<Folder> folderArgumentCaptor;
    ArgumentCaptor<Note> noteArgumentCaptor;
    ArgumentCaptor<Tag> tagArgumentCaptor;

    ListAppender<ILoggingEvent> loggingEventListAppender;

    List<Folder> mockFolders;
    List<Note> mockNotes;
    List<Tag> mockTags;
    List<Long> mockFolderIds;
    List<Long> mockNoteIds;
    List<Long> mockTagIds;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);

        folderArgumentCaptor = ArgumentCaptor.forClass(Folder.class);
        noteArgumentCaptor = ArgumentCaptor.forClass(Note.class);
        tagArgumentCaptor = ArgumentCaptor.forClass(Tag.class);

        loggingEventListAppender = new ListAppender<>();
        loggingEventListAppender.start();

        mockFolders = new ArrayList<>();
        mockNotes = new ArrayList<>();
        mockTags = new ArrayList<>();
        mockFolderIds = new ArrayList<>();
        mockNoteIds = new ArrayList<>();
        mockTagIds = new ArrayList<>();
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
    void tearDown() throws Exception {
        autoCloseable.close();
    }

}

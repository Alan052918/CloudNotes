package com.jundaai.note.service;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.jundaai.note.exception.*;
import com.jundaai.note.form.note.NoteCreationForm;
import com.jundaai.note.form.note.NoteUpdateForm;
import com.jundaai.note.form.note.NoteUpdateType;
import com.jundaai.note.model.Folder;
import com.jundaai.note.model.Note;
import com.jundaai.note.model.Tag;
import com.jundaai.note.repository.FolderRepository;
import com.jundaai.note.repository.NoteRepository;
import com.jundaai.note.repository.TagRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NoteServiceTest {

    private AutoCloseable autoCloseable;
    private NoteService testService;

    @Mock
    private FolderRepository mockFolderRepository;
    @Mock
    private NoteRepository mockNoteRepository;
    @Mock
    private TagRepository mockTagRepository;

    private ArgumentCaptor<Folder> folderArgumentCaptor;
    private ArgumentCaptor<Note> noteArgumentCaptor;
    private ArgumentCaptor<Tag> tagArgumentCaptor;
    private ListAppender<ILoggingEvent> loggingEventListAppender;

    private List<Folder> savedFolders;
    private List<Note> savedNotes;
    private List<Tag> savedTags;
    private List<Long> savedFolderIds;
    private List<Long> savedNoteIds;
    private List<Long> savedTagIds;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        testService = new NoteService(mockFolderRepository, mockNoteRepository, mockTagRepository);

        folderArgumentCaptor = ArgumentCaptor.forClass(Folder.class);
        noteArgumentCaptor = ArgumentCaptor.forClass(Note.class);
        tagArgumentCaptor = ArgumentCaptor.forClass(Tag.class);

        Logger logger = (Logger) LoggerFactory.getLogger(NoteService.class);
        loggingEventListAppender = new ListAppender<>();
        loggingEventListAppender.start();
        logger.addAppender(loggingEventListAppender);

        savedFolders = new ArrayList<>();
        savedNotes = new ArrayList<>();
        savedTags = new ArrayList<>();
        savedFolderIds = new ArrayList<>();
        savedNoteIds = new ArrayList<>();
        savedTagIds = new ArrayList<>();
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
        Note go = Note
                .builder()
                .id(2L)
                .name("Go")
                .content("Go is a general purpose programming language.")
                .createdAt(now)
                .updatedAt(now)
                .folder(pl)
                .tags(new ArrayList<>())
                .build();
        Tag google = Tag
                .builder()
                .id(3L)
                .name("Google")
                .createdAt(now)
                .updatedAt(now)
                .notes(new ArrayList<>())
                .build();
        Tag microsoft = Tag
                .builder()
                .id(4L)
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
        savedFolders.add(root);
        savedFolders.add(pl);
        savedNotes.add(go);
        savedTags.add(google);
        savedTags.add(microsoft);
        savedFolderIds = savedFolders
                .stream()
                .map(Folder::getId)
                .collect(Collectors.toList());
        savedNoteIds = savedNotes
                .stream()
                .map(Note::getId)
                .collect(Collectors.toList());
        savedTagIds = savedTags
                .stream()
                .map(Tag::getId)
                .collect(Collectors.toList());
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void getAllNotes_Success() {
        // given
        List<Note> expectedNotes = savedNotes;

        // when
        when(mockNoteRepository.findAll()).thenReturn(savedNotes);
        List<Note> gotNotes = testService.getAllNotes();

        // then
        verify(mockNoteRepository).findAll();
        assertEquals(expectedNotes, gotNotes);
    }

    @Test
    void getAllNotesByFolderId_Success() {
        // given
        Long testId = savedFolderIds.get(1);
        List<Note> expectedNotes = savedNotes;

        // when
        when(mockFolderRepository.findById(testId)).thenReturn(Optional.of(savedFolders.get(1)));
        List<Note> gotNotes = testService.getAllNotesByFolderId(testId);

        // then
        verify(mockFolderRepository).findById(testId);
        assertEquals(expectedNotes, gotNotes);
    }

    @Test
    void getAllNotesByFolderId_NotExistingFolderId_ExceptionThrown() {
        // given
        Long notExistingId = -1L;
        String expectedMessage = "Folder by id: " + notExistingId + " was not found.";

        // when
        Exception exception = assertThrows(FolderNotFoundException.class,
                () -> testService.getAllNotesByFolderId(notExistingId));

        // then
        verify(mockFolderRepository).findById(notExistingId);
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void getAllNotesByTagId_Success() {
        // given
        Long testId = savedTagIds.get(0);
        List<Note> expectedNotes = savedNotes;

        // when
        when(mockTagRepository.findById(testId)).thenReturn(Optional.of(savedTags.get(0)));
        List<Note> gotNotes = testService.getAllNotesByTagId(testId);

        // then
        verify(mockTagRepository).findById(testId);
        assertEquals(expectedNotes, gotNotes);
    }

    @Test
    void getAllNotesByTagId_NotExistingTagId_ExceptionThrown() {
        // given
        Long notExistingId = -1L;
        String expectedMessage = "Tag by id: " + notExistingId + " was not found.";

        // when
        Exception exception = assertThrows(TagNotFoundException.class,
                () -> testService.getAllNotesByTagId(notExistingId));

        // then
        verify(mockTagRepository).findById(notExistingId);
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void getNoteById_Success() {
        // given
        Long testId = savedNoteIds.get(0);
        Note expectedNote = savedNotes.get(0);

        // when
        when(mockNoteRepository.findById(testId)).thenReturn(Optional.of(expectedNote));
        Note gotNote = testService.getNoteById(testId);

        // then
        verify(mockNoteRepository).findById(testId);
        assertEquals(expectedNote, gotNote);
    }

    @Test
    void getNoteById_NotExistingId_ExceptionThrown() {
        // given
        Long notExistingId = -1L;
        String expectedMessage = "Note by id: " + notExistingId + " was not found.";

        // when
        Exception exception = assertThrows(NoteNotFoundException.class,
                () -> testService.getNoteById(notExistingId));

        // then
        verify(mockNoteRepository).findById(notExistingId);
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void createNoteByFolderId_Success() {
        // given
        String testName = "New Note";
        String testContent = "This is a new note.";
        Long testFolderId = savedFolderIds.get(0);
        Folder testFolder = savedFolders.get(0);
        NoteCreationForm testForm = NoteCreationForm
                .builder()
                .name(testName)
                .content(testContent)
                .build();

        // when
        when(mockFolderRepository.findById(testFolderId)).thenReturn(Optional.of(testFolder));
        testService.createNoteByFolderId(testFolderId, testForm);

        // then
        verify(mockFolderRepository).findById(testFolderId);
        verify(mockNoteRepository).existsByNameWithSameFolder(testName, testFolder);
        verify(mockNoteRepository).save(noteArgumentCaptor.capture());

        Note capturedNote = noteArgumentCaptor.getValue();
        assertEquals(testName, capturedNote.getName());
        assertEquals(testContent, capturedNote.getContent());
    }

    @Test
    void createNoteByFolderId_NotExistingFolderId_ExceptionThrown() {
        // given
        Long notExistingId = -1L;
        NoteCreationForm testForm = NoteCreationForm
                .builder()
                .name("")
                .content("")
                .build();
        String expectedMessage = "Folder by id: " + notExistingId + " was not found.";

        // when
        Exception exception = assertThrows(FolderNotFoundException.class,
                () -> testService.createNoteByFolderId(notExistingId, testForm));

        // then
        verify(mockFolderRepository).findById(notExistingId);
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void createNoteByFolderId_ConflictingNoteName_ExceptionThrown() {
        // given
        String conflictingNoteName = "Go";
        Long testFolderId = savedFolderIds.get(1);
        Folder testFolder = savedFolders.get(1);
        NoteCreationForm testForm = NoteCreationForm
                .builder()
                .name(conflictingNoteName)
                .content("")
                .build();
        String expectedMessage = "Note name: " + conflictingNoteName +
                " conflicts with an existing note under the same folder.";

        // when
        when(mockFolderRepository.findById(testFolderId)).thenReturn(Optional.of(testFolder));
        when(mockNoteRepository.existsByNameWithSameFolder(conflictingNoteName, testFolder)).thenReturn(true);
        Exception exception = assertThrows(NoteNameConflictException.class,
                () -> testService.createNoteByFolderId(testFolderId, testForm));

        // then
        verify(mockFolderRepository).findById(testFolderId);
        verify(mockNoteRepository).existsByNameWithSameFolder(conflictingNoteName, testFolder);
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void updateNoteById_Rename_Success() {
        // given
        String newName = "New Name";
        Long testId = savedNoteIds.get(0);
        Folder testFolder = savedFolders.get(1);
        NoteUpdateForm testForm = NoteUpdateForm
                .builder()
                .updateType(NoteUpdateType.RENAME_NOTE)
                .newName(newName)
                .build();

        // when
        when(mockNoteRepository.findById(testId)).thenReturn(Optional.of(savedNotes.get(0)));
        testService.updateNoteById(testId, testForm);

        // then
        verify(mockNoteRepository).findById(testId);
        verify(mockNoteRepository).existsByNameWithSameFolder(newName, testFolder);
        verify(mockNoteRepository).save(noteArgumentCaptor.capture());

        Note capturedNote = noteArgumentCaptor.getValue();
        assertEquals(newName, capturedNote.getName());
    }

    @Test
    void updateNoteById_ChangeContent_Success() {
        // given
        String newContent = "New Content";
        Long testId = savedNoteIds.get(0);
        NoteUpdateForm testForm = NoteUpdateForm
                .builder()
                .updateType(NoteUpdateType.MODIFY_CONTENT)
                .newContent(newContent)
                .build();

        // when
        when(mockNoteRepository.findById(testId)).thenReturn(Optional.of(savedNotes.get(0)));
        testService.updateNoteById(testId, testForm);

        // then
        verify(mockNoteRepository).findById(testId);
        verify(mockNoteRepository).save(noteArgumentCaptor.capture());

        Note capturedNote = noteArgumentCaptor.getValue();
        assertEquals(newContent, capturedNote.getContent());
    }

    @Test
    void updateNoteById_Move_Success() {
        // given
        Long testId = savedNoteIds.get(0);
        Long testToFolderId = savedFolderIds.get(0);
        NoteUpdateForm testForm = NoteUpdateForm
                .builder()
                .updateType(NoteUpdateType.MOVE_NOTE)
                .toFolderId(testToFolderId)
                .build();

        // when
        when(mockNoteRepository.findById(testId)).thenReturn(Optional.of(savedNotes.get(0)));
        when(mockFolderRepository.findById(testToFolderId)).thenReturn(Optional.of(savedFolders.get(0)));
        testService.updateNoteById(testId, testForm);

        // then
        verify(mockNoteRepository).findById(testId);
        verify(mockFolderRepository).findById(testToFolderId);
        verify(mockFolderRepository, times(2)).save(folderArgumentCaptor.capture());
        verify(mockNoteRepository).save(noteArgumentCaptor.capture());

        Note capturedNote = noteArgumentCaptor.getValue();
        assertEquals(testToFolderId, capturedNote.getFolder().getId());

        Folder capturedFromFolder = folderArgumentCaptor.getAllValues().get(0);
        assertFalse(capturedFromFolder.getNotes().contains(capturedNote));

        Folder capturedToFolder = folderArgumentCaptor.getAllValues().get(1);
        assertTrue(capturedToFolder.getNotes().contains(capturedNote));
    }

    @Test
    void updateNoteById_AddNewTag_Success() {
        // given
        String newTagName = "New Tag";
        NoteUpdateForm testForm = NoteUpdateForm
                .builder()
                .updateType(NoteUpdateType.ADD_TAG)
                .tagName(newTagName)
                .build();
        ZonedDateTime now = ZonedDateTime.now();
        Tag newTag = Tag
                .builder()
                .id(5L)
                .name(newTagName)
                .createdAt(now)
                .updatedAt(now)
                .notes(new ArrayList<>())
                .build();
        Long testId = savedNoteIds.get(0);
        Note testNote = savedNotes.get(0);
        assertFalse(testNote.getTags().contains(newTag));

        // when
        when(mockNoteRepository.findById(testId)).thenReturn(Optional.of(testNote));
        when(mockTagRepository.save(any(Tag.class))).thenReturn(newTag);
        testService.updateNoteById(testId, testForm);

        // then
        verify(mockNoteRepository).findById(testId);
        verify(mockTagRepository).existsByName(newTagName);
        verify(mockTagRepository, times(2)).save(tagArgumentCaptor.capture());
        verify(mockNoteRepository).save(noteArgumentCaptor.capture());

        Note capturedNote = noteArgumentCaptor.getValue();
        List<Tag> capturedTags = tagArgumentCaptor.getAllValues();
        assertTrue(capturedNote.getTags().contains(newTag));
        assertTrue(capturedTags.get(1).getNotes().contains(capturedNote));
    }

    @Test
    void updateNoteById_AddExistingTag_Success() {
        // given
        String existingTagName = "Microsoft";
        NoteUpdateForm testForm = NoteUpdateForm
                .builder()
                .updateType(NoteUpdateType.ADD_TAG)
                .tagName(existingTagName)
                .build();
        Tag existingTag = savedTags.get(1);
        Long testId = savedNoteIds.get(0);
        Note testNote = savedNotes.get(0);
        assertFalse(testNote.getTags().contains(existingTag));

        // when
        when(mockNoteRepository.findById(testId)).thenReturn(Optional.of(testNote));
        when(mockTagRepository.existsByName(existingTagName)).thenReturn(true);
        when(mockTagRepository.getByName(existingTagName)).thenReturn(existingTag);
        testService.updateNoteById(testId, testForm);

        // then
        verify(mockNoteRepository).findById(testId);
        verify(mockTagRepository).existsByName(existingTagName);
        verify(mockTagRepository).getByName(existingTagName);
        verify(mockTagRepository).save(tagArgumentCaptor.capture());
        verify(mockNoteRepository).save(noteArgumentCaptor.capture());

        Note capturedNote = noteArgumentCaptor.getValue();
        Tag capturedTag = tagArgumentCaptor.getValue();
        assertTrue(capturedNote.getTags().contains(existingTag));
        assertTrue(capturedTag.getNotes().contains(testNote));
    }

    @Test
    void updateNoteById_RemoveOldTag_Success() {
        // given
        String oldTagName = "Google";
        NoteUpdateForm testForm = NoteUpdateForm
                .builder()
                .updateType(NoteUpdateType.REMOVE_TAG)
                .tagName(oldTagName)
                .build();
        Tag oldTag = savedTags.get(0);
        Long testId = savedNoteIds.get(0);
        Note testNote = savedNotes.get(0);
        assertTrue(testNote.getTags().contains(oldTag));

        // when
        when(mockNoteRepository.findById(testId)).thenReturn(Optional.of(testNote));
        when(mockTagRepository.findByName(oldTagName)).thenReturn(Optional.of(oldTag));
        testService.updateNoteById(testId, testForm);

        // then
        verify(mockNoteRepository).findById(testId);
        verify(mockTagRepository).findByName(oldTagName);
        verify(mockTagRepository).save(tagArgumentCaptor.capture());
        verify(mockNoteRepository).save(noteArgumentCaptor.capture());

        Note capturedNote = noteArgumentCaptor.getValue();
        Tag capturedTag = tagArgumentCaptor.getValue();
        assertFalse(capturedNote.getTags().contains(oldTag));
        assertFalse(capturedTag.getNotes().contains(testNote));
    }

    @Test
    void updateNoteById_NotExistingId_ExceptionThrown() {
        // given
        Long notExistingId = -1L;
        String expectedMessage = "Note by id: " + notExistingId + " was not found.";

        // when
        Exception exception = assertThrows(NoteNotFoundException.class,
                () -> testService.updateNoteById(notExistingId, NoteUpdateForm.builder().build()));

        // then
        verify(mockNoteRepository).findById(notExistingId);
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void updateNoteById_ConflictingNewName_ExceptionThrown() {
        // given
        Long testId = savedNoteIds.get(0);
        String conflictingNewName = "Go";
        NoteUpdateForm testForm = NoteUpdateForm
                .builder()
                .updateType(NoteUpdateType.RENAME_NOTE)
                .newName(conflictingNewName)
                .build();
        String expectedMessage = "Note name: " + conflictingNewName +
                " conflicts with an existing note under the same folder.";

        // when
        when(mockNoteRepository.findById(testId)).thenReturn(Optional.of(savedNotes.get(0)));
        when(mockNoteRepository.existsByNameWithSameFolder(conflictingNewName, savedFolders.get(1))).thenReturn(true);
        Exception exception = assertThrows(NoteNameConflictException.class,
                () -> testService.updateNoteById(testId, testForm));

        // then
        verify(mockNoteRepository).findById(testId);
        verify(mockNoteRepository).existsByNameWithSameFolder(conflictingNewName, savedFolders.get(1));
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void updateNoteById_SetIdenticalContent_Aborted() {
        // given
        Long testId = savedNoteIds.get(0);
        Note testNote = savedNotes.get(0);
        String oldContent = testNote.getContent();
        NoteUpdateForm testForm = NoteUpdateForm
                .builder()
                .updateType(NoteUpdateType.MODIFY_CONTENT)
                .newContent(oldContent)
                .build();
        String expectedMessage = "New Content identical to the old. Abort.";

        // when
        when(mockNoteRepository.findById(testId)).thenReturn(Optional.of(testNote));
        testService.updateNoteById(testId, testForm);

        // then
        List<ILoggingEvent> loggingEvents = loggingEventListAppender.list;
        assertEquals(expectedMessage, loggingEvents.get(1).getMessage());
        assertEquals(Level.ERROR, loggingEvents.get(1).getLevel());
    }

    @Test
    void updateNoteById_InvalidMoveToFolder_ExceptionThrownOrAborted() {
        // given
        Long testId = savedNoteIds.get(0);
        Long notExistingId = -1L;
        Long testFolderId = savedFolderIds.get(1);
        NoteUpdateForm testForm1 = NoteUpdateForm
                .builder()
                .updateType(NoteUpdateType.MOVE_NOTE)
                .toFolderId(notExistingId)
                .build();
        NoteUpdateForm testForm2 = NoteUpdateForm
                .builder()
                .updateType(NoteUpdateType.MOVE_NOTE)
                .toFolderId(testFolderId)
                .build();
        String expectedMessage1 = "Folder by id: " + notExistingId + " was not found.";
        String expectedMessage2 = "Destination folder identical as current folder. Abort.";

        // when
        when(mockNoteRepository.findById(testId)).thenReturn(Optional.of(savedNotes.get(0)));
        Exception exception = assertThrows(FolderNotFoundException.class,
                () -> testService.updateNoteById(testId, testForm1));

        when(mockFolderRepository.findById(testFolderId)).thenReturn(Optional.of(savedFolders.get(1)));
        testService.updateNoteById(testId, testForm2);

        // then
        verify(mockNoteRepository, times(2)).findById(testId);
        verify(mockFolderRepository).findById(notExistingId);
        verify(mockFolderRepository).findById(testFolderId);

        assertEquals(expectedMessage1, exception.getMessage());

        List<ILoggingEvent> loggingEvents = loggingEventListAppender.list;
        assertEquals(expectedMessage2, loggingEvents.get(2).getMessage());
        assertEquals(Level.ERROR, loggingEvents.get(2).getLevel());
    }

    @Test
    void updateNoteById_AddOldTag_Aborted() {
        // given
        Long testId = savedNoteIds.get(0);
        String oldTagName = "Google";
        NoteUpdateForm testForm = NoteUpdateForm
                .builder()
                .updateType(NoteUpdateType.ADD_TAG)
                .tagName(oldTagName)
                .build();
        String expectedMessage = "Note already contains tag to add. Abort.";

        // when
        when(mockNoteRepository.findById(testId)).thenReturn(Optional.of(savedNotes.get(0)));
        when(mockTagRepository.existsByName(oldTagName)).thenReturn(true);
        when(mockTagRepository.getByName(oldTagName)).thenReturn(savedTags.get(0));
        testService.updateNoteById(testId, testForm);

        // then
        verify(mockNoteRepository).findById(testId);
        verify(mockTagRepository).existsByName(oldTagName);
        verify(mockTagRepository).getByName(oldTagName);

        List<ILoggingEvent> loggingEvents = loggingEventListAppender.list;
        assertEquals(expectedMessage, loggingEvents.get(1).getMessage());
        assertEquals(Level.ERROR, loggingEvents.get(1).getLevel());
    }

    @Test
    void updateNoteById_InvalidRemoveTag_ExceptionThrown() {
        // given
        Long testId = savedNoteIds.get(0);
        Note testNote = savedNotes.get(0);
        String notExistingTagName = "Not Exist";
        String testTagName = "Microsoft";
        Tag testTag = savedTags.get(1);
        NoteUpdateForm testForm1 = NoteUpdateForm
                .builder()
                .updateType(NoteUpdateType.REMOVE_TAG)
                .tagName(notExistingTagName)
                .build();
        NoteUpdateForm testForm2 = NoteUpdateForm
                .builder()
                .updateType(NoteUpdateType.REMOVE_TAG)
                .tagName(testTagName)
                .build();
        String expectedMessage1 = "Tag by name: " + notExistingTagName + " was not found.";
        String expectedMessage2 = "Note " + testNote + " has no tag " + testTag;

        // when
        when(mockNoteRepository.findById(testId)).thenReturn(Optional.of(testNote));
        Exception exception1 = assertThrows(TagNotFoundException.class,
                () -> testService.updateNoteById(testId, testForm1));

        when(mockTagRepository.findByName(testTagName)).thenReturn(Optional.of(testTag));
        Exception exception2 = assertThrows(BadRequestException.class,
                () -> testService.updateNoteById(testId, testForm2));

        // then
        verify(mockNoteRepository, times(2)).findById(testId);
        verify(mockTagRepository).findByName(notExistingTagName);
        verify(mockTagRepository).findByName(testTagName);

        assertEquals(expectedMessage1, exception1.getMessage());
        assertEquals(expectedMessage2, exception2.getMessage());
    }

    @Test
    void updateNoteById_UnsupportedNoteUpdateType_ExceptionThrown() {
        // given
        Long testId = savedNoteIds.get(0);
        NoteUpdateForm testForm = NoteUpdateForm
                .builder()
                .updateType("Unsupported Operation")
                .build();
        String expectedMessage = "Unsupported note update type.";

        // when
        when(mockNoteRepository.findById(testId)).thenReturn(Optional.of(savedNotes.get(0)));
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> testService.updateNoteById(testId, testForm));

        // then
        verify(mockNoteRepository).findById(testId);
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void deleteNoteById_Success() {
        // given
        Long testId = savedNoteIds.get(0);
        Note testNote = savedNotes.get(0);
        Folder testFolder = savedFolders.get(1);
        assertTrue(testFolder.getNotes().contains(testNote));

        // when
        when(mockNoteRepository.findById(testId)).thenReturn(Optional.of(testNote));
        testService.deleteNoteById(testId);

        // then
        verify(mockNoteRepository).findById(testId);
        verify(mockFolderRepository).save(folderArgumentCaptor.capture());
        verify(mockNoteRepository).deleteById(testId);

        Folder capturedFolder = folderArgumentCaptor.getValue();
        assertFalse(capturedFolder.getNotes().contains(testNote));
    }

    @Test
    void deleteNoteById_NotExistingId_ExceptionThrown() {
        // given
        Long notExistingId = -1L;
        String expectedMessage = "Note by id: " + notExistingId + " was not found.";

        // when
        Exception exception = assertThrows(NoteNotFoundException.class,
                () -> testService.deleteNoteById(notExistingId));

        // then
        verify(mockNoteRepository).findById(notExistingId);
        assertEquals(expectedMessage, exception.getMessage());
    }

}

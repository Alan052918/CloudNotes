package com.jundaai.note.service;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.jundaai.note.exception.*;
import com.jundaai.note.form.note.NoteCreationForm;
import com.jundaai.note.form.note.NoteUpdateForm;
import com.jundaai.note.form.note.NoteUpdateType;
import com.jundaai.note.model.Folder;
import com.jundaai.note.model.Note;
import com.jundaai.note.model.Tag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NoteServiceTest extends ServiceTest {

    @Autowired
    private NoteService testService;

    @Override
    @BeforeEach
    void setUp() {
        super.setUp();
        testService = new NoteService(mockFolderRepository, mockNoteRepository, mockTagRepository);
        Logger logger = (Logger) LoggerFactory.getLogger(NoteService.class);
        logger.addAppender(loggingEventListAppender);
    }

    @Test
    public void getAllNotes_Success() {
        // given
        List<Note> expectedNotes = mockNotes;

        // when
        when(mockNoteRepository.findAll()).thenReturn(mockNotes);
        List<Note> gotNotes = testService.getAllNotes();

        // then
        verify(mockNoteRepository).findAll();
        assertEquals(expectedNotes, gotNotes);
    }

    @Test
    public void getAllNotesByFolderId_Success() {
        // given
        Long testId = mockFolderIds.get(1);
        List<Note> expectedNotes = mockNotes;

        // when
        when(mockFolderRepository.findById(testId)).thenReturn(Optional.of(mockFolders.get(1)));
        List<Note> gotNotes = testService.getAllNotesByFolderId(testId);

        // then
        verify(mockFolderRepository).findById(testId);
        assertEquals(expectedNotes, gotNotes);
    }

    @Test
    public void getAllNotesByFolderId_NotExistingFolderId_ExceptionThrown() {
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
    public void getAllNotesByTagId_Success() {
        // given
        Long testId = mockTagIds.get(0);
        List<Note> expectedNotes = mockNotes;

        // when
        when(mockTagRepository.findById(testId)).thenReturn(Optional.of(mockTags.get(0)));
        List<Note> gotNotes = testService.getAllNotesByTagId(testId);

        // then
        verify(mockTagRepository).findById(testId);
        assertEquals(expectedNotes, gotNotes);
    }

    @Test
    public void getAllNotesByTagId_NotExistingTagId_ExceptionThrown() {
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
    public void getNoteById_Success() {
        // given
        Long testId = mockNoteIds.get(0);
        Note expectedNote = mockNotes.get(0);

        // when
        when(mockNoteRepository.findById(testId)).thenReturn(Optional.of(expectedNote));
        Note gotNote = testService.getNoteById(testId);

        // then
        verify(mockNoteRepository).findById(testId);
        assertEquals(expectedNote, gotNote);
    }

    @Test
    public void getNoteById_NotExistingId_ExceptionThrown() {
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
    public void createNoteByFolderId_Success() {
        // given
        String testName = "New Note";
        String testContent = "This is a new note.";
        Long testFolderId = mockFolderIds.get(0);
        Folder testFolder = mockFolders.get(0);
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
    public void createNoteByFolderId_NotExistingFolderId_ExceptionThrown() {
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
    public void createNoteByFolderId_ConflictingNoteName_ExceptionThrown() {
        // given
        String conflictingNoteName = "Go";
        Long testFolderId = mockFolderIds.get(1);
        Folder testFolder = mockFolders.get(1);
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
    public void updateNoteById_Rename_Success() {
        // given
        String newName = "New Name";
        Long testId = mockNoteIds.get(0);
        Folder testFolder = mockFolders.get(1);
        NoteUpdateForm testForm = NoteUpdateForm
                .builder()
                .updateType(NoteUpdateType.RENAME_NOTE)
                .newName(newName)
                .build();

        // when
        when(mockNoteRepository.findById(testId)).thenReturn(Optional.of(mockNotes.get(0)));
        testService.updateNoteById(testId, testForm);

        // then
        verify(mockNoteRepository).findById(testId);
        verify(mockNoteRepository).existsByNameWithSameFolder(newName, testFolder);
        verify(mockNoteRepository).save(noteArgumentCaptor.capture());

        Note capturedNote = noteArgumentCaptor.getValue();
        assertEquals(newName, capturedNote.getName());
    }

    @Test
    public void updateNoteById_ChangeContent_Success() {
        // given
        String newContent = "New Content";
        Long testId = mockNoteIds.get(0);
        NoteUpdateForm testForm = NoteUpdateForm
                .builder()
                .updateType(NoteUpdateType.MODIFY_CONTENT)
                .newContent(newContent)
                .build();

        // when
        when(mockNoteRepository.findById(testId)).thenReturn(Optional.of(mockNotes.get(0)));
        testService.updateNoteById(testId, testForm);

        // then
        verify(mockNoteRepository).findById(testId);
        verify(mockNoteRepository).save(noteArgumentCaptor.capture());

        Note capturedNote = noteArgumentCaptor.getValue();
        assertEquals(newContent, capturedNote.getContent());
    }

    @Test
    public void updateNoteById_Move_Success() {
        // given
        Long testId = mockNoteIds.get(0);
        Long testToFolderId = mockFolderIds.get(0);
        NoteUpdateForm testForm = NoteUpdateForm
                .builder()
                .updateType(NoteUpdateType.MOVE_NOTE)
                .toFolderId(testToFolderId)
                .build();

        // when
        when(mockNoteRepository.findById(testId)).thenReturn(Optional.of(mockNotes.get(0)));
        when(mockFolderRepository.findById(testToFolderId)).thenReturn(Optional.of(mockFolders.get(0)));
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
    public void updateNoteById_AddNewTag_Success() {
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
        Long testId = mockNoteIds.get(0);
        Note testNote = mockNotes.get(0);
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
    public void updateNoteById_AddExistingTag_Success() {
        // given
        String existingTagName = "Microsoft";
        NoteUpdateForm testForm = NoteUpdateForm
                .builder()
                .updateType(NoteUpdateType.ADD_TAG)
                .tagName(existingTagName)
                .build();
        Tag existingTag = mockTags.get(1);
        Long testId = mockNoteIds.get(0);
        Note testNote = mockNotes.get(0);
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
    public void updateNoteById_RemoveOldTag_Success() {
        // given
        String oldTagName = "Google";
        NoteUpdateForm testForm = NoteUpdateForm
                .builder()
                .updateType(NoteUpdateType.REMOVE_TAG)
                .tagName(oldTagName)
                .build();
        Tag oldTag = mockTags.get(0);
        Long testId = mockNoteIds.get(0);
        Note testNote = mockNotes.get(0);
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
    public void updateNoteById_NotExistingId_ExceptionThrown() {
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
    public void updateNoteById_ConflictingNewName_ExceptionThrown() {
        // given
        Long testId = mockNoteIds.get(0);
        String conflictingNewName = "Go";
        NoteUpdateForm testForm = NoteUpdateForm
                .builder()
                .updateType(NoteUpdateType.RENAME_NOTE)
                .newName(conflictingNewName)
                .build();
        String expectedMessage = "Note name: " + conflictingNewName +
                " conflicts with an existing note under the same folder.";

        // when
        when(mockNoteRepository.findById(testId)).thenReturn(Optional.of(mockNotes.get(0)));
        when(mockNoteRepository.existsByNameWithSameFolder(conflictingNewName, mockFolders.get(1))).thenReturn(true);
        Exception exception = assertThrows(NoteNameConflictException.class,
                () -> testService.updateNoteById(testId, testForm));

        // then
        verify(mockNoteRepository).findById(testId);
        verify(mockNoteRepository).existsByNameWithSameFolder(conflictingNewName, mockFolders.get(1));
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    public void updateNoteById_SetIdenticalContent_Aborted() {
        // given
        Long testId = mockNoteIds.get(0);
        Note testNote = mockNotes.get(0);
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
    public void updateNoteById_InvalidMoveToFolder_ExceptionThrownOrAborted() {
        // given
        Long testId = mockNoteIds.get(0);
        Long notExistingId = -1L;
        Long testFolderId = mockFolderIds.get(1);
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
        when(mockNoteRepository.findById(testId)).thenReturn(Optional.of(mockNotes.get(0)));
        Exception exception = assertThrows(FolderNotFoundException.class,
                () -> testService.updateNoteById(testId, testForm1));

        when(mockFolderRepository.findById(testFolderId)).thenReturn(Optional.of(mockFolders.get(1)));
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
    public void updateNoteById_AddOldTag_Aborted() {
        // given
        Long testId = mockNoteIds.get(0);
        String oldTagName = "Google";
        NoteUpdateForm testForm = NoteUpdateForm
                .builder()
                .updateType(NoteUpdateType.ADD_TAG)
                .tagName(oldTagName)
                .build();
        String expectedMessage = "Note already contains tag to add. Abort.";

        // when
        when(mockNoteRepository.findById(testId)).thenReturn(Optional.of(mockNotes.get(0)));
        when(mockTagRepository.existsByName(oldTagName)).thenReturn(true);
        when(mockTagRepository.getByName(oldTagName)).thenReturn(mockTags.get(0));
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
    public void updateNoteById_InvalidRemoveTag_ExceptionThrown() {
        // given
        Long testId = mockNoteIds.get(0);
        Note testNote = mockNotes.get(0);
        String notExistingTagName = "Not Exist";
        String testTagName = "Microsoft";
        Tag testTag = mockTags.get(1);
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
    public void updateNoteById_UnsupportedNoteUpdateType_ExceptionThrown() {
        // given
        Long testId = mockNoteIds.get(0);
        NoteUpdateForm testForm = NoteUpdateForm
                .builder()
                .updateType("Unsupported Operation")
                .build();
        String expectedMessage = "Unsupported note update type.";

        // when
        when(mockNoteRepository.findById(testId)).thenReturn(Optional.of(mockNotes.get(0)));
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> testService.updateNoteById(testId, testForm));

        // then
        verify(mockNoteRepository).findById(testId);
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    public void deleteNoteById_Success() {
        // given
        Long testId = mockNoteIds.get(0);
        Note testNote = mockNotes.get(0);
        Folder testFolder = mockFolders.get(1);
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
    public void deleteNoteById_NotExistingId_ExceptionThrown() {
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

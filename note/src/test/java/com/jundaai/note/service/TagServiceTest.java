package com.jundaai.note.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import ch.qos.logback.classic.Logger;
import com.jundaai.note.dto.TagOperationForm;
import com.jundaai.note.exception.NoteNotFoundException;
import com.jundaai.note.exception.TagNameBlankException;
import com.jundaai.note.exception.TagNameConflictException;
import com.jundaai.note.exception.TagNotFoundException;
import com.jundaai.note.model.Note;
import com.jundaai.note.model.Tag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;


@ExtendWith(MockitoExtension.class)
public class TagServiceTest extends ServiceTest {

    private TagService testService;

    @Override
    @BeforeEach
    void setUp() {
        super.setUp();
        testService = new TagService(mockNoteRepository, mockTagRepository);
        Logger logger = (Logger) LoggerFactory.getLogger(TagService.class);
        logger.addAppender(loggingEventListAppender);
    }

    @Test
    public void getAllTags_Success() {
        // given
        List<Tag> expectedTags = mockTags;

        // when
        when(mockTagRepository.findAll()).thenReturn(mockTags);
        List<Tag> gotTags = testService.getAllTags();

        // then
        verify(mockTagRepository).findAll();
        assertEquals(expectedTags, gotTags);
    }

    @Test
    public void getAllTagsByNoteId_Success() {
        // given
        Long testNoteId = mockNoteIds.get(0);
        Note testNote = mockNotes.get(0);
        List<Tag> expectedTags = Collections.singletonList(mockTags.get(0));

        // when
        when(mockNoteRepository.findAllTagsById(testNoteId)).thenReturn(Optional.of(testNote.getTags()));
        List<Tag> gotTags = testService.getAllTagsByNoteId(testNoteId);

        // then
        verify(mockNoteRepository).findAllTagsById(testNoteId);
        assertEquals(expectedTags, gotTags);
    }

    @Test
    public void getAllTagsByNoteId_NotExistingNoteId_ExceptionThrown() {
        // given
        Long notExistingId = -1L;
        String expectedMessage = "Note by id: " + notExistingId + " was not found.";

        // when
        Exception exception = assertThrows(NoteNotFoundException.class,
                () -> testService.getAllTagsByNoteId(notExistingId));

        // then
        verify(mockNoteRepository).findAllTagsById(notExistingId);
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    public void getTagById_Success() {
        // given
        Long testId = mockTagIds.get(0);
        Tag expectedTag = mockTags.get(0);

        // when
        when(mockTagRepository.findById(testId)).thenReturn(Optional.of(mockTags.get(0)));
        Tag gotTag = testService.getTagById(testId);

        // then
        verify(mockTagRepository).findById(testId);
        assertEquals(expectedTag, gotTag);
    }

    @Test
    public void getTagById_NotExistingId_ExceptionThrown() {
        // given
        Long notExistingId = -1L;
        String expectedMessage = "Tag by id: " + notExistingId + " was not found.";

        // when
        Exception exception = assertThrows(TagNotFoundException.class, () -> testService.getTagById(notExistingId));

        // then
        verify(mockTagRepository).findById(notExistingId);
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    public void createTag_Success() {
        // given
        String testName = "New Tag";
        TagOperationForm testForm = TagOperationForm.builder().name(testName).build();

        // when
        when(mockTagRepository.existsByName(testName)).thenReturn(false);
        testService.createTag(testForm);

        // then
        verify(mockTagRepository).existsByName(testName);
        verify(mockTagRepository).save(tagArgumentCaptor.capture());

        Tag capturedTag = tagArgumentCaptor.getValue();
        assertEquals(testName, capturedTag.getName());
    }

    @Test
    public void createTag_InvalidTagName_ExceptionThrown() {
        // given
        String blankTagName = "";
        String conflictingTagName = "Google";
        TagOperationForm testForm1 = TagOperationForm.builder().build();
        TagOperationForm testForm2 = TagOperationForm.builder().name(blankTagName).build();
        TagOperationForm testForm3 = TagOperationForm.builder().name(conflictingTagName).build();
        String expectedMessage1 = "Tag name cannot be blank (null or all whitespaces).";
        String expectedMessage2 = "Tag name: " + conflictingTagName + " conflicts with an existing tag.";

        // when
        Exception exception1 = assertThrows(TagNameBlankException.class, () -> testService.createTag(testForm1));
        Exception exception2 = assertThrows(TagNameBlankException.class, () -> testService.createTag(testForm2));

        when(mockTagRepository.existsByName(conflictingTagName)).thenReturn(true);
        Exception exception3 = assertThrows(TagNameConflictException.class, () -> testService.createTag(testForm3));

        // then
        verify(mockTagRepository).existsByName(conflictingTagName);
        assertEquals(expectedMessage1, exception1.getMessage());
        assertEquals(expectedMessage1, exception2.getMessage());
        assertEquals(expectedMessage2, exception3.getMessage());
    }

    @Test
    public void updateTagById_Success() {
        // given
        Long testId = mockTagIds.get(0);
        String newName = "New Tag";
        TagOperationForm testForm = TagOperationForm.builder().name(newName).build();

        // when
        when(mockTagRepository.findById(testId)).thenReturn(Optional.of(mockTags.get(0)));
        when(mockTagRepository.existsByName(newName)).thenReturn(false);
        testService.updateTagById(testId, testForm);

        // then
        verify(mockTagRepository).findById(testId);
        verify(mockTagRepository).existsByName(newName);
        verify(mockTagRepository).save(tagArgumentCaptor.capture());

        Tag capturedTag = tagArgumentCaptor.getValue();
        assertEquals(newName, capturedTag.getName());
    }

    @Test
    public void updateTagById_NotExistingId_ExceptionThrown() {
        // given
        Long notExistingId = -1L;
        String expectedMessage = "Tag by id: " + notExistingId + " was not found.";

        // when
        Exception exception = assertThrows(TagNotFoundException.class,
                () -> testService.updateTagById(notExistingId, new TagOperationForm("New Tag")));

        // then
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    public void updateTagById_InvalidNewName_ExceptionThrown() {
        // given
        Long testId = mockTagIds.get(0);
        String blankTagName = "";
        String conflictingTagName = "Microsoft";
        TagOperationForm testForm1 = TagOperationForm.builder().build();
        TagOperationForm testForm2 = TagOperationForm.builder().name(blankTagName).build();
        TagOperationForm testForm3 = TagOperationForm.builder().name(conflictingTagName).build();
        String expectedMessage1 = "Tag name cannot be blank (null or all whitespaces).";
        String expectedMessage2 = "Tag name: " + conflictingTagName + " conflicts with an existing tag.";

        // when
        when(mockTagRepository.findById(testId)).thenReturn(Optional.of(mockTags.get(0)));
        when(mockTagRepository.existsByName(conflictingTagName)).thenReturn(true);
        Exception exception1 = assertThrows(TagNameBlankException.class,
                () -> testService.updateTagById(testId, testForm1));
        Exception exception2 = assertThrows(TagNameBlankException.class,
                () -> testService.updateTagById(testId, testForm2));
        Exception exception3 = assertThrows(TagNameConflictException.class,
                () -> testService.updateTagById(testId, testForm3));

        // then
        verify(mockTagRepository, times(3)).findById(testId);
        verify(mockTagRepository).existsByName(conflictingTagName);
        assertEquals(expectedMessage1, exception1.getMessage());
        assertEquals(expectedMessage1, exception2.getMessage());
        assertEquals(expectedMessage2, exception3.getMessage());
    }

    @Test
    public void deleteTagById_Success() {
        // given
        Long testId = mockTagIds.get(0);
        Tag testTag = mockTags.get(0);
        Note testNote = mockNotes.get(0);

        assertTrue(testNote.getTags().contains(testTag));

        // when
        when(mockTagRepository.findById(testId)).thenReturn(Optional.of(testTag));
        testService.deleteTagById(testId);

        // then
        verify(mockTagRepository).findById(testId);
        verify(mockTagRepository).deleteById(testId);
        verify(mockNoteRepository).save(noteArgumentCaptor.capture());

        Note capturedNote = noteArgumentCaptor.getValue();
        assertFalse(capturedNote.getTags().contains(testTag));
    }

    @Test
    public void deleteTagById_NotExistingId_ExceptionThrown() {
        // given
        Long notExistingId = -1L;
        String expectedMessage = "Tag by id: " + notExistingId + " was not found.";

        // then
        Exception exception = assertThrows(TagNotFoundException.class, () -> testService.deleteTagById(notExistingId));

        // then
        assertEquals(expectedMessage, exception.getMessage());
    }
}

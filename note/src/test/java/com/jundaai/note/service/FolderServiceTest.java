package com.jundaai.note.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.jundaai.note.dto.FolderCreationForm;
import com.jundaai.note.dto.FolderUpdateForm;
import com.jundaai.note.dto.FolderUpdateType;
import com.jundaai.note.exception.FolderNameBlankException;
import com.jundaai.note.exception.FolderNameConflictException;
import com.jundaai.note.exception.FolderNotFoundException;
import com.jundaai.note.exception.RootPreservationException;
import com.jundaai.note.model.Folder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;


@ExtendWith(MockitoExtension.class)
public class FolderServiceTest extends ServiceTest {

    private FolderService testService;

    @Override
    @BeforeEach
    void setUp() {
        super.setUp();
        testService = new FolderService(mockFolderRepository);
        Logger logger = (Logger) LoggerFactory.getLogger(FolderService.class);
        logger.addAppender(loggingEventListAppender);
    }

    @Test
    public void getAllFolders_Success() {
        // given
        List<Folder> expectedFolders = mockFolders;

        // when
        when(mockFolderRepository.findAll()).thenReturn(mockFolders);
        List<Folder> gotFolders = testService.getAllFolders();

        // then
        verify(mockFolderRepository).findAll();
        assertEquals(expectedFolders, gotFolders);
    }

    @Test
    public void getFolderById_Success() {
        // given
        Long testId = mockFolderIds.get(0);
        Folder expectedFolder = mockFolders.get(0);

        // when
        when(mockFolderRepository.findById(testId)).thenReturn(Optional.ofNullable(mockFolders.get(0)));
        Folder gotFolder = testService.getFolderById(testId);

        // then
        verify(mockFolderRepository).findById(testId);
        assertEquals(expectedFolder, gotFolder);
    }

    @Test
    public void getFolderById_NotExistingId_ExceptionThrown() {
        // given
        Long notExistingId = -1L;
        String expectedMessage = "Folder by id: " + notExistingId + " was not found.";

        // when
        Exception exception = assertThrows(FolderNotFoundException.class,
                () -> testService.getFolderById(notExistingId));

        // then
        verify(mockFolderRepository).findById(notExistingId);
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    public void getSubFoldersByParentId_Success() {
        // given
        Long testParentId = mockFolderIds.get(0);
        List<Folder> expectedSubFolders = List.of(mockFolders.get(1), mockFolders.get(2));

        // when
        when(mockFolderRepository.findSubFoldersByParentId(testParentId))
                .thenReturn(Optional.of(List.of(mockFolders.get(1), mockFolders.get(2))));
        List<Folder> gotSubFolders = testService.getSubFoldersByParentId(testParentId);

        // then
        verify(mockFolderRepository).findSubFoldersByParentId(testParentId);
        assertEquals(expectedSubFolders, gotSubFolders);
    }

    @Test
    public void getSubFoldersByParentId_NotExistingParentId_ExceptionThrown() {
        // given
        Long notExistingId = -1L;
        String expectedMessage = "Folder by id: " + notExistingId + " was not found.";

        // when
        Exception exception = assertThrows(FolderNotFoundException.class,
                () -> testService.getSubFoldersByParentId(notExistingId));

        // then
        verify(mockFolderRepository).findSubFoldersByParentId(notExistingId);
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    public void createFolderByParentId_Success() {
        // given
        String testName = "Test Folder";
        Long testParentId = mockFolderIds.get(0);
        FolderCreationForm testForm = FolderCreationForm.builder().name(testName).build();

        // when
        when(mockFolderRepository.findById(testParentId)).thenReturn(Optional.ofNullable(mockFolders.get(0)));
        testService.createFolderByParentId(testParentId, testForm);

        // then
        verify(mockFolderRepository).findById(testParentId);
        verify(mockFolderRepository, times(2)).save(folderArgumentCaptor.capture());

        Folder capturedFolder = folderArgumentCaptor.getAllValues().get(0);
        assertEquals(testName, capturedFolder.getName());
    }

    @Test
    public void createFolderByParentId_NotExistingParentId_ExceptionThrown() {
        // given
        Long notExistingId = -1L;
        FolderCreationForm testForm = FolderCreationForm.builder().name("Test Name").build();
        String expectedMessage = "Folder by id: " + notExistingId + " was not found.";

        // when
        Exception exception = assertThrows(FolderNotFoundException.class,
                () -> testService.createFolderByParentId(notExistingId, testForm));

        // then
        verify(mockFolderRepository).findById(notExistingId);
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    public void createFolderByParentId_InvalidFolderName_ExceptionThrown() {
        // given
        String preservedRootName = "root";
        String blankFolderName = "";
        String conflictingFolderName = "Java";
        Long testParentId = mockFolderIds.get(0);
        Folder testParent = mockFolders.get(0);
        FolderCreationForm testForm1 = FolderCreationForm.builder().name(preservedRootName).build();
        FolderCreationForm testForm2 = FolderCreationForm.builder().build();
        FolderCreationForm testForm3 = FolderCreationForm.builder().name(blankFolderName).build();
        FolderCreationForm testForm4 = FolderCreationForm.builder().name(conflictingFolderName).build();
        String expectedMessage1 = "Root folder is preserved, Create folder named 'root' failed.";
        String expectedMessage2 = "Folder name cannot be blank (null or all whitespaces).";
        String expectedMessage3 = "Folder name: " + conflictingFolderName +
                " conflicts with an existing folder under the same parent.";

        // when
        when(mockFolderRepository.findById(testParentId)).thenReturn(Optional.ofNullable(mockFolders.get(0)));
        Exception exception1 = assertThrows(RootPreservationException.class,
                () -> testService.createFolderByParentId(testParentId, testForm1));
        Exception exception2 = assertThrows(FolderNameBlankException.class,
                () -> testService.createFolderByParentId(testParentId, testForm2));
        Exception exception3 = assertThrows(FolderNameBlankException.class,
                () -> testService.createFolderByParentId(testParentId, testForm3));

        when(mockFolderRepository.existsByNameWithSameParent(conflictingFolderName, mockFolders.get(0)))
                .thenReturn(true);
        Exception exception4 = assertThrows(FolderNameConflictException.class,
                () -> testService.createFolderByParentId(testParentId, testForm4));

        // then
        verify(mockFolderRepository).existsByNameWithSameParent(conflictingFolderName, testParent);
        assertEquals(expectedMessage1, exception1.getMessage());
        assertEquals(expectedMessage2, exception2.getMessage());
        assertEquals(expectedMessage2, exception3.getMessage());
        assertEquals(expectedMessage3, exception4.getMessage());
    }

    @Test
    public void updateFolderById_Rename_Success() {
        // given
        String newName = "New Name";
        Long testId = mockFolderIds.get(1);
        FolderUpdateForm testRenameForm = FolderUpdateForm.builder()
                .updateType(FolderUpdateType.RENAME_FOLDER.name())
                .newName(newName)
                .build();

        // when
        when(mockFolderRepository.findById(testId)).thenReturn(Optional.ofNullable(mockFolders.get(1)));
        testService.updateFolderById(testId, testRenameForm);

        // then
        verify(mockFolderRepository).findById(testId);
        verify(mockFolderRepository).save(folderArgumentCaptor.capture());

        Folder capturedFolder = folderArgumentCaptor.getValue();
        assertEquals(newName, capturedFolder.getName());
    }

    @Test
    public void updateFolderById_Move_Success() {
        // given
        Long testId = mockFolderIds.get(2);
        Long testToParentId = mockFolderIds.get(1);
        FolderUpdateForm testMoveForm = FolderUpdateForm.builder()
                .updateType(FolderUpdateType.MOVE_FOLDER.name())
                .toParentId(testToParentId)
                .build();

        // when
        when(mockFolderRepository.findById(testId)).thenReturn(Optional.ofNullable(mockFolders.get(2)));
        when(mockFolderRepository.findById(testToParentId)).thenReturn(Optional.ofNullable(mockFolders.get(1)));
        testService.updateFolderById(testId, testMoveForm);

        // then
        verify(mockFolderRepository).findById(testId);
        verify(mockFolderRepository).findById(testToParentId);
        verify(mockFolderRepository, times(3)).save(folderArgumentCaptor.capture());

        Folder capturedFolder = folderArgumentCaptor.getAllValues().get(2);
        assertEquals(testToParentId, capturedFolder.getParentFolder().getId());

        Folder capturedFromFolder = folderArgumentCaptor.getAllValues().get(0);
        assertFalse(capturedFromFolder.getSubFolders().contains(capturedFolder));

        Folder capturedToFolder = folderArgumentCaptor.getAllValues().get(1);
        assertTrue(capturedToFolder.getSubFolders().contains(capturedFolder));
    }

    @Test
    public void updateFolderById_NotExistingId_ExceptionThrown() {
        // given
        Long notExistingId = -1L;
        FolderUpdateForm testForm = FolderUpdateForm.builder()
                .updateType(FolderUpdateType.RENAME_FOLDER.name())
                .newName("New Name")
                .build();
        String expectedMessage = "Folder by id: " + notExistingId + " was not found.";

        // when
        Exception exception = assertThrows(FolderNotFoundException.class,
                () -> testService.updateFolderById(notExistingId, testForm));

        // then
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    public void updateFolderById_InvalidNewName_ExceptionThrown() {
        // given
        String preservedRootName = "root";
        String blankFolderName = "";
        String conflictingFolderName = "Java";
        Long rootId = mockFolderIds.get(0);
        Long testId = mockFolderIds.get(1);
        FolderUpdateForm testForm1 = FolderUpdateForm.builder()
                .updateType(FolderUpdateType.RENAME_FOLDER.name())
                .newName(preservedRootName)
                .build();
        FolderUpdateForm testForm2 = FolderUpdateForm.builder()
                .updateType(FolderUpdateType.RENAME_FOLDER.name())
                .build();
        FolderUpdateForm testForm3 = FolderUpdateForm.builder()
                .updateType(FolderUpdateType.RENAME_FOLDER.name())
                .newName(blankFolderName)
                .build();
        FolderUpdateForm testForm4 = FolderUpdateForm.builder()
                .updateType(FolderUpdateType.RENAME_FOLDER.name())
                .newName(conflictingFolderName)
                .build();
        String expectedMessage1 = "Root folder is preserved, " + FolderUpdateType.RENAME_FOLDER + " failed.";
        String expectedMessage2 = "Folder name cannot be blank (null or all whitespaces).";
        String expectedMessage3 = "Folder name: " + conflictingFolderName +
                " conflicts with an existing folder under the same parent.";

        // when
        // root folder cannot be renamed
        when(mockFolderRepository.findById(rootId)).thenReturn(Optional.ofNullable(mockFolders.get(0)));
        Exception exception1 = assertThrows(RootPreservationException.class,
                () -> testService.updateFolderById(rootId, testForm1));

        // other folders cannot be named "root"
        when(mockFolderRepository.findById(testId)).thenReturn(Optional.ofNullable(mockFolders.get(1)));
        Exception exception2 = assertThrows(RootPreservationException.class,
                () -> testService.updateFolderById(testId, testForm1));
        Exception exception3 = assertThrows(FolderNameBlankException.class,
                () -> testService.updateFolderById(testId, testForm2));
        Exception exception4 = assertThrows(FolderNameBlankException.class,
                () -> testService.updateFolderById(testId, testForm3));

        when(mockFolderRepository.existsByNameWithSameParent(conflictingFolderName, mockFolders.get(0)))
                .thenReturn(true);
        Exception exception5 = assertThrows(FolderNameConflictException.class,
                () -> testService.updateFolderById(testId, testForm4));

        // then
        verify(mockFolderRepository).findById(rootId);
        verify(mockFolderRepository, times(4)).findById(testId);
        verify(mockFolderRepository).existsByNameWithSameParent(conflictingFolderName, mockFolders.get(0));

        assertEquals(expectedMessage1, exception1.getMessage());
        assertEquals(expectedMessage1, exception2.getMessage());
        assertEquals(expectedMessage2, exception3.getMessage());
        assertEquals(expectedMessage2, exception4.getMessage());
        assertEquals(expectedMessage3, exception5.getMessage());
    }

    @Test
    public void updateFolderById_NotExistingToParentId_ExceptionThrown() {
        // given
        Long testId = mockFolderIds.get(1);
        Long notExistingId = -1L;
        FolderUpdateForm testForm = FolderUpdateForm.builder()
                .updateType(FolderUpdateType.MOVE_FOLDER.name())
                .toParentId(notExistingId)
                .build();
        String expectedMessage = "Folder by id: " + notExistingId + " was not found.";

        // when
        when(mockFolderRepository.findById(testId)).thenReturn(Optional.ofNullable(mockFolders.get(1)));
        Exception exception = assertThrows(FolderNotFoundException.class,
                () -> testService.updateFolderById(testId, testForm));

        // then
        verify(mockFolderRepository).findById(notExistingId);
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    public void updateFolderById_UnsupportedFolderOperationType_ExceptionThrown() {
        // given
        Long testId = mockFolderIds.get(1);
        String unsupportedOperation = "Unsupported Operation";
        FolderUpdateForm testForm = FolderUpdateForm.builder().updateType(unsupportedOperation).build();
        // when
        when(mockFolderRepository.findById(testId)).thenReturn(Optional.ofNullable(mockFolders.get(1)));
        Exception exception = assertThrows(UnsupportedOperationException.class,
                () -> testService.updateFolderById(testId, testForm));

        // then
        verify(mockFolderRepository).findById(testId);
        assertEquals(unsupportedOperation, exception.getMessage());
    }

    @Test
    public void updateFolderById_InvalidToParentFolder_Aborted() {
        // given
        Long testId = mockFolderIds.get(1);
        Long testParentId = mockFolderIds.get(0);
        FolderUpdateForm testForm1 = FolderUpdateForm.builder()
                .updateType(FolderUpdateType.MOVE_FOLDER.name())
                .toParentId(testId)
                .build();
        FolderUpdateForm testForm2 = FolderUpdateForm.builder()
                .updateType(FolderUpdateType.MOVE_FOLDER.name())
                .toParentId(testParentId)
                .build();
        String expectedMessage1 = "Cannot move folder to self. Abort.";
        String expectedMessage2 = "Destination folder identical as current parent folder. Abort.";

        // when
        when(mockFolderRepository.findById(testId)).thenReturn(Optional.ofNullable(mockFolders.get(1)));
        when(mockFolderRepository.findById(testParentId)).thenReturn(Optional.ofNullable(mockFolders.get(0)));
        testService.updateFolderById(testId, testForm1);
        testService.updateFolderById(testId, testForm2);

        // then
        verify(mockFolderRepository, times(3)).findById(testId);
        verify(mockFolderRepository).findById(testParentId);

        List<ILoggingEvent> loggingEvents = loggingEventListAppender.list;
        assertEquals(expectedMessage1, loggingEvents.get(1).getMessage());
        assertEquals(Level.ERROR, loggingEvents.get(1).getLevel());
        assertEquals(expectedMessage2, loggingEvents.get(3).getMessage());
        assertEquals(Level.ERROR, loggingEvents.get(3).getLevel());
    }

    @Test
    public void deleteFolderById_Success() {
        // given
        Long testId = mockFolderIds.get(1);

        // when
        when(mockFolderRepository.findById(testId)).thenReturn(Optional.ofNullable(mockFolders.get(1)));
        testService.deleteFolderById(testId);

        // then
        verify(mockFolderRepository).deleteById(testId);
    }

    @Test
    public void deleteFolderById_InvalidFolderId_ExceptionThrown() {
        // given
        Long notExistingId = -1L;
        Long rootId = mockFolderIds.get(0);
        String expectedMessage1 = "Folder by id: " + notExistingId + " was not found.";
        String expectedMessage2 = "Root folder is preserved, Delete root folder failed.";

        // when
        Exception exception1 = assertThrows(FolderNotFoundException.class,
                () -> testService.deleteFolderById(notExistingId));

        when(mockFolderRepository.findById(rootId)).thenReturn(Optional.ofNullable(mockFolders.get(0)));
        Exception exception2 = assertThrows(RootPreservationException.class,
                () -> testService.deleteFolderById(rootId));

        // then
        verify(mockFolderRepository).findById(rootId);
        assertEquals(expectedMessage1, exception1.getMessage());
        assertEquals(expectedMessage2, exception2.getMessage());
    }
}

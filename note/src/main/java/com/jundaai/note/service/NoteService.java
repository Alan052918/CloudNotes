package com.jundaai.note.service;

import com.jundaai.note.exception.FolderNotFoundException;
import com.jundaai.note.exception.NoteNameConflictException;
import com.jundaai.note.exception.NoteNotFoundException;
import com.jundaai.note.form.FolderUpdateForm;
import com.jundaai.note.form.NoteCreationForm;
import com.jundaai.note.form.NoteUpdateForm;
import com.jundaai.note.model.Folder;
import com.jundaai.note.model.Note;
import com.jundaai.note.repository.FolderRepository;
import com.jundaai.note.repository.NoteRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class NoteService {

    private final FolderRepository folderRepository;
    private final NoteRepository noteRepository;

    @Autowired
    public NoteService(FolderRepository folderRepository, NoteRepository noteRepository) {
        this.folderRepository = folderRepository;
        this.noteRepository = noteRepository;
    }

    public List<Note> getAllNotes() {
        log.info("Get all notes");
        return noteRepository.findAll();
    }

    public List<Note> getAllNotesByFolderId(Long folderId) {
        log.info("Get all notes by folder id: {}", folderId);
        return folderRepository.getNotesById(folderId);
    }

    public Note getNoteById(Long noteId) {
        log.info("Get note by id: {}", noteId);
        return noteRepository.findById(noteId)
                .orElseThrow(() -> new NoteNotFoundException(noteId));
    }

    @Transactional
    public Note createNoteByFolderId(Long folderId, NoteCreationForm creationForm) {
        log.info("Create new note: {}, folder id: {}", creationForm, folderId);

        String noteName = creationForm.name();
        if (noteName == null) {
            throw new IllegalArgumentException("Note name cannot be null.");
        }

        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new FolderNotFoundException(folderId));
        boolean nameConflicted = noteRepository.existsByNameWithSameFolder(noteName, folder);
        if (nameConflicted) {
            throw new NoteNameConflictException(noteName);
        }

        ZonedDateTime now = ZonedDateTime.now();
        Note note = Note.builder()
                .name(creationForm.name())
                .createdAt(now)
                .updatedAt(now)
                .folder(folder)
                .build();
        note = noteRepository.save(note);

        List<Note> folderNotes = folder.getNotes();
        folderNotes.add(note);
        folder.setNotes(folderNotes);
        folder.setUpdatedAt(now);
        folderRepository.save(folder);

        return note;
    }

    @Transactional
    public Note updateNoteById(Long noteId, NoteUpdateForm updateForm) {
        log.info("Update note by id: {}, form: {}", noteId, updateForm);
        boolean isUpdated = false;
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new NoteNotFoundException(noteId));

        String newName = updateForm.newName();
        if (newName != null && newName.length() > 0 && !Objects.equals(newName, note.getName())) {
            boolean nameConflicted = noteRepository.existsByNameWithSameFolder(newName, note.getFolder());
            if (nameConflicted) {
                throw new NoteNameConflictException(newName);
            }
            note.setName(newName);
            isUpdated = true;
        }

        String newContent = updateForm.newContent();
        if (newContent != null && newContent.length() > 0 && !Objects.equals(newContent, note.getContent())) {
            note.setContent(newContent);
            isUpdated = true;
        }

        Long toFolderId = updateForm.toFolderId();
        Folder toFolder = folderRepository.findById(toFolderId)
                .orElseThrow(() -> new FolderNotFoundException(toFolderId));
        Folder fromFolder = note.getFolder();
        if (toFolder != null) {
            boolean existsById = folderRepository.existsById(toFolder.getId());
            if (!existsById) {
                throw new FolderNotFoundException(toFolder.getId());
            }
            if (!Objects.equals(toFolder, fromFolder)) {
                note.setFolder(toFolder);

                List<Note> fromFolderNotes = fromFolder.getNotes();
                fromFolderNotes.remove(note);
                fromFolder.setNotes(fromFolderNotes);

                List<Note> toFolderNotes = toFolder.getNotes();
                toFolderNotes.add(note);
                toFolder.setNotes(toFolderNotes);

                isUpdated = true;
            }
        }

        if (isUpdated) {
            ZonedDateTime now = ZonedDateTime.now();
            note.setUpdatedAt(now);
            note.getFolder().setUpdatedAt(now);
        }

        return noteRepository.save(note);
    }

    @Transactional
    public void deleteNoteById(Long noteId) {
        log.info("Delete note by id: {}", noteId);
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new NoteNotFoundException(noteId));

        Folder folder = note.getFolder();
        List<Note> folderNotes = folder.getNotes();
        folderNotes.remove(note);
        folder.setNotes(folderNotes);
        folder.setUpdatedAt(ZonedDateTime.now());
        folderRepository.save(folder);

        noteRepository.deleteById(noteId);
    }

}

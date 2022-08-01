package com.jundaai.note.service;

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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class NoteService {

    private final FolderRepository folderRepository;
    private final NoteRepository noteRepository;
    private final TagRepository tagRepository;

    @Autowired
    public NoteService(FolderRepository folderRepository, NoteRepository noteRepository, TagRepository tagRepository) {
        this.folderRepository = folderRepository;
        this.noteRepository = noteRepository;
        this.tagRepository = tagRepository;
    }

    public List<Note> getAllNotes() {
        log.info("Get all notes");
        return noteRepository.findAll();
    }

    public List<Note> getAllNotesByFolderId(Long folderId) {
        log.info("Get all notes by folder id: {}", folderId);
        Folder folder = folderRepository
                .findById(folderId)
                .orElseThrow(() -> new FolderNotFoundException(folderId));
        return folder.getNotes();
    }

    public List<Note> getAllNotesByTagId(Long tagId) {
        log.info("Get all notes by tag id: {}", tagId);
        Tag tag = tagRepository
                .findById(tagId)
                .orElseThrow(() -> new TagNotFoundException("id: " + tagId));
        return tag.getNotes();
    }

    public Note getNoteById(Long noteId) {
        log.info("Get note by id: {}", noteId);
        return noteRepository
                .findById(noteId)
                .orElseThrow(() -> new NoteNotFoundException(noteId));
    }

    @Transactional
    public Note createNoteByFolderId(Long folderId, NoteCreationForm creationForm) {
        log.info("Create new note: {}, folder id: {}", creationForm, folderId);

        String noteName = creationForm.getName();
        Folder folder = folderRepository
                .findById(folderId)
                .orElseThrow(() -> new FolderNotFoundException(folderId));
        boolean nameConflicted = noteRepository.existsByNameWithSameFolder(noteName, folder);
        if (nameConflicted) {
            throw new NoteNameConflictException(noteName);
        }

        ZonedDateTime now = ZonedDateTime.now();
        Note note = Note
                .builder()
                .name(noteName)
                .content("")
                .createdAt(now)
                .updatedAt(now)
                .folder(folder)
                .tags(new ArrayList<>())
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
        ZonedDateTime now = ZonedDateTime.now();
        Note note = noteRepository
                .findById(noteId)
                .orElseThrow(() -> new NoteNotFoundException(noteId));

        switch (updateForm.getUpdateType()) {
            case NoteUpdateType.RENAME_NOTE -> {
                String newName = updateForm.getNewName();
                boolean nameConflicted = noteRepository.existsByNameWithSameFolder(newName, note.getFolder());
                if (nameConflicted) {
                    throw new NoteNameConflictException(newName);
                }
                note.setName(newName);
            }
            case NoteUpdateType.MODIFY_CONTENT -> {
                String newContent = updateForm.getNewContent();
                if (newContent.equals(note.getContent())) {
                    log.error("New Content identical to the old. Abort.");
                    return note;
                }
                note.setContent(newContent);
            }
            case NoteUpdateType.MOVE_NOTE -> {
                Long toFolderId = updateForm.getToFolderId();
                Folder toFolder = folderRepository
                        .findById(toFolderId)
                        .orElseThrow(() -> new FolderNotFoundException(toFolderId));
                Folder fromFolder = note.getFolder();
                if (Objects.equals(toFolder, fromFolder)) {
                    log.error("Destination folder identical as current folder. Abort.");
                    return note;
                }
                note.setFolder(toFolder);
                note = noteRepository.save(note);

                List<Note> fromFolderNotes = fromFolder.getNotes();
                fromFolderNotes.remove(note);
                fromFolder.setNotes(fromFolderNotes);
                fromFolder.setUpdatedAt(now);
                folderRepository.save(fromFolder);

                List<Note> toFolderNotes = toFolder.getNotes();
                toFolderNotes.add(note);
                toFolder.setNotes(toFolderNotes);
                toFolder.setUpdatedAt(now);
                folderRepository.save(toFolder);
            }
            case NoteUpdateType.ADD_TAG -> {
                Tag tag;
                String tagName = updateForm.getTagName();
                boolean existsByName = tagRepository.existsByName(tagName);
                if (existsByName) {
                    tag = tagRepository.getByName(tagName);
                } else {
                    tag = Tag
                            .builder()
                            .name(tagName)
                            .createdAt(now)
                            .updatedAt(now)
                            .notes(new ArrayList<>())
                            .build();
                    tag = tagRepository.save(tag);
                }
                if (note.getTags().contains(tag)) {
                    log.error("Note already contains tag to add. Abort.");
                    return note;
                }

                List<Note> tagNotes = tag.getNotes();
                tagNotes.add(note);
                tag.setNotes(tagNotes);
                tag.setUpdatedAt(now);
                tagRepository.save(tag);

                List<Tag> noteTags = note.getTags();
                noteTags.add(tag);
                note.setTags(noteTags);
            }
            case NoteUpdateType.REMOVE_TAG -> {
                String tagName = updateForm.getTagName();
                Tag tag = tagRepository
                        .findByName(tagName)
                        .orElseThrow(() -> new TagNotFoundException("name: " + tagName));
                if (!note.getTags().contains(tag)) {
                    throw new BadRequestException("Note " + note + " has no tag " + tag);
                }

                List<Note> tagNotes = tag.getNotes();
                tagNotes.remove(note);
                tag.setNotes(tagNotes);
                tag.setUpdatedAt(now);
                tagRepository.save(tag);

                List<Tag> noteTags = note.getTags();
                noteTags.remove(tag);
                note.setTags(noteTags);
            }
            default -> throw new IllegalArgumentException("Unsupported note update type.");
        }
        note.setUpdatedAt(now);
        note.getFolder().setUpdatedAt(now);
        return noteRepository.save(note);
    }

    @Transactional
    public void deleteNoteById(Long noteId) {
        log.info("Delete note by id: {}", noteId);
        Note note = noteRepository
                .findById(noteId)
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

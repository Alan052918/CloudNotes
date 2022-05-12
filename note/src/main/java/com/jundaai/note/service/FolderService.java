package com.jundaai.note.service;

import com.jundaai.note.exception.*;
import com.jundaai.note.form.FolderCreationForm;
import com.jundaai.note.form.FolderUpdateForm;
import com.jundaai.note.model.Folder;
import com.jundaai.note.model.Note;
import com.jundaai.note.repository.FolderRepository;
import com.jundaai.note.repository.NoteRepository;
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
public class FolderService {

    private final FolderRepository folderRepository;
    private final NoteRepository noteRepository;

    @Autowired
    public FolderService(FolderRepository folderRepository, NoteRepository noteRepository) {
        this.folderRepository = folderRepository;
        this.noteRepository = noteRepository;
    }

    public List<Folder> getAllFolders() {
        log.info("Get all folders");
        return folderRepository.findAll();
    }

    public Folder getFolderById(Long folderId) {
        log.info("Get folder by id: {}", folderId);
        return folderRepository.findById(folderId)
                .orElseThrow(() -> new FolderNotFoundException(folderId));
    }

    public List<Folder> getSubFoldersByParentId(Long parentId) {
        log.info("Get sub-folders by parent id: {}", parentId);
        return folderRepository.getSubFoldersByParentId(parentId);
    }

    @Transactional
    public Folder createFolderByParentId(Long parentId, FolderCreationForm folderCreationForm) {
        log.info("Create new folder: {}, parent folder id: {}", folderCreationForm, parentId);
        String folderName = folderCreationForm.name();
        if (folderName == null) {
            throw new IllegalArgumentException("Folder name cannot be null.");
        }
        if (Objects.equals(folderName, "root")) {
            throw new IllegalArgumentException("Folder name cannot be 'root', reserved for root folder.");
        }
        Folder parent = folderRepository.findById(parentId)
                .orElseThrow(() -> new FolderNotFoundException(parentId));
        ZonedDateTime now = ZonedDateTime.now();

        Folder folder = Folder.builder()
                .name(folderName)
                .createdAt(now)
                .updatedAt(now)
                .parentFolder(parent)
                .subFolders(new ArrayList<>())
                .notes(new ArrayList<>())
                .build();
        folder = folderRepository.save(folder);

        List<Folder> parentSubFolders = parent.getSubFolders();
        parentSubFolders.add(folder);
        parent.setSubFolders(parentSubFolders);
        parent.setUpdatedAt(now);
        folderRepository.save(parent);

        return folder;
    }

    @Transactional
    public Folder updateFolderById(Long folderId, FolderUpdateForm updateForm) {
        log.info("Update folder by id: {}, form: {}", folderId, updateForm);
        boolean isUpdated = false;
        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new FolderNotFoundException(folderId));

        String newName = updateForm.newName();
        if (newName != null && newName.length() > 0 && !Objects.equals(newName, folder.getName())) {
            boolean nameConflicted = folderRepository.existsByNameWithSameParent(newName, folder.getParentFolder());
            if (nameConflicted) {
                throw new FolderNameConlictException(newName);
            }
            folder.setName(newName);
            isUpdated = true;
            log.info("New folder name: {}", newName);
        }

        Folder addSubFolder = updateForm.addSubFolder();
        if (addSubFolder != null) {
            if (addSubFolder.getParentFolder() != null && !Objects.equals(addSubFolder.getParentFolder(), folder)) {
                throw new FolderMultipleParentException(addSubFolder.getId());
            }
            List<Folder> subFolders = folder.getSubFolders();
            subFolders.add(addSubFolder);
            folder.setSubFolders(subFolders);
            isUpdated = true;
            log.info("Add new sub-folder: {}", addSubFolder);
        }

        Folder deleteSubFolder = updateForm.deleteSubFolder();
        if (deleteSubFolder != null) {
            boolean existsById = folderRepository.existsById(deleteSubFolder.getId());
            if (!existsById) {
                throw new FolderNotFoundException(deleteSubFolder.getId());
            }
            List<Folder> subFolders = folder.getSubFolders();
            subFolders.remove(deleteSubFolder);
            folder.setSubFolders(subFolders);
            isUpdated = true;
            log.info("Delete sub-folder: {}", deleteSubFolder);
        }

        Note addNote = updateForm.addNote();
        if (addNote != null) {
            if (addNote.getFolder() != null && !Objects.equals(addNote.getFolder(), folder)) {
                throw new NoteMultipleFolderException(addNote.getId());
            }
            List<Note> notes = folder.getNotes();
            notes.add(addNote);
            folder.setNotes(notes);
            isUpdated = true;
            log.info("Add note: {}", addNote);
        }

        Note deleteNode = updateForm.deleteNote();
        if (deleteNode != null) {
            boolean existsById = noteRepository.existsById(deleteNode.getId());
            if (!existsById) {
                throw new NoteNotFoundException(deleteNode.getId());
            }
            List<Note> notes = folder.getNotes();
            notes.remove(deleteNode);
            folder.setNotes(notes);
            isUpdated = true;
            log.info("Delete note: {}", deleteNode);
        }

        if (isUpdated) {
            folder.setUpdatedAt(ZonedDateTime.now());
        }

        return folderRepository.save(folder);
    }

    @Transactional
    public void deleteFolderById(Long folderId) {
        log.info("Delete folder by id: {}", folderId);
        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new FolderNotFoundException(folderId));
        if (Objects.equals(folder.getName(), "root")) {
            throw new ForbiddenOperationException("Root folder cannot be deleted.");
        }

        Folder parent = folder.getParentFolder();
        List<Folder> parentSubFolders = parent.getSubFolders();
        parentSubFolders.remove(folder);
        parent.setSubFolders(parentSubFolders);
        parent.setUpdatedAt(ZonedDateTime.now());
        folderRepository.save(parent);

        folderRepository.deleteById(folderId);
    }

}

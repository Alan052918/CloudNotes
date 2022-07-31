package com.jundaai.note.service;

import com.jundaai.note.exception.FolderNameBlankException;
import com.jundaai.note.exception.FolderNameConflictException;
import com.jundaai.note.exception.FolderNotFoundException;
import com.jundaai.note.exception.RootPreservationException;
import com.jundaai.note.form.folder.FolderCreationForm;
import com.jundaai.note.form.folder.FolderUpdateForm;
import com.jundaai.note.form.folder.FolderOperationType;
import com.jundaai.note.model.Folder;
import com.jundaai.note.repository.FolderRepository;
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

    @Autowired
    public FolderService(FolderRepository folderRepository) {
        this.folderRepository = folderRepository;
    }

    public List<Folder> getAllFolders() {
        log.info("Get all folders");
        return folderRepository.findAll();
    }

    public Folder getFolderById(Long folderId) {
        log.info("Get folder by id: {}", folderId);
        return folderRepository
                .findById(folderId)
                .orElseThrow(() -> new FolderNotFoundException(folderId));
    }

    public List<Folder> getSubFoldersByParentId(Long parentId) {
        log.info("Get sub-folders by parent id: {}", parentId);
        return folderRepository
                .findSubFoldersByParentId(parentId)
                .orElseThrow(() -> new FolderNotFoundException(parentId));
    }

    @Transactional
    public Folder createFolderByParentId(Long parentId, FolderCreationForm folderCreationForm) {
        log.info("Create new folder: {}, parent folder id: {}", folderCreationForm, parentId);

        String folderName = folderCreationForm.getName();
        if (folderName.equals("root")) {
            throw new RootPreservationException(FolderOperationType.CREATE_FOLDER);
        }
        if (folderName.isBlank()) {
            throw new FolderNameBlankException();
        }

        Folder parent = folderRepository
                .findById(parentId)
                .orElseThrow(() -> new FolderNotFoundException(parentId));
        boolean nameConflicted = folderRepository.existsByNameWithSameParent(folderName, parent);
        if (nameConflicted) {
            throw new FolderNameConflictException(folderName);
        }

        ZonedDateTime now = ZonedDateTime.now();
        Folder folder = Folder
                .builder()
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
        ZonedDateTime now = ZonedDateTime.now();
        Folder folder = folderRepository
                .findById(folderId)
                .orElseThrow(() -> new FolderNotFoundException(folderId));
        if (folder.getName().equals("root")) {
            throw new RootPreservationException(updateForm.getUpdateType());
        }

        switch (updateForm.getUpdateType()) {
            case FolderOperationType.RENAME_FOLDER -> {
                String newName = updateForm.getNewName();
                if (newName.equals("root")) {
                    throw new RootPreservationException(FolderOperationType.RENAME_FOLDER);
                }
                if (newName.isBlank()) {
                    throw new FolderNameBlankException();
                }
                boolean nameConflicted = folderRepository.existsByNameWithSameParent(newName, folder.getParentFolder());
                if (nameConflicted) {
                    throw new FolderNameConflictException(newName);
                }
                folder.setName(newName);
                folder.setUpdatedAt(now);
            }
            case FolderOperationType.MOVE_FOLDER -> {
                Long toParentId = updateForm.getToParentId();
                Folder toParent = folderRepository
                        .findById(toParentId)
                        .orElseThrow(() -> new FolderNotFoundException(toParentId));
                Folder fromParent = folder.getParentFolder();

                if (toParent == null) {
                    log.error("Cannot move folder to null parent folder. Abort.");
                    return folder;
                }
                if (Objects.equals(toParent, folder)) {
                    log.error("Cannot move folder to self. Abort.");
                    return folder;
                }
                if (Objects.equals(toParent, fromParent)) {
                    log.error("Destination folder identical as current parent folder. Abort.");
                    return folder;
                }

                folder.setParentFolder(toParent);
                folder.setUpdatedAt(now);

                List<Folder> fromParentSubFolders = fromParent.getSubFolders();
                fromParentSubFolders.remove(folder);
                fromParent.setSubFolders(fromParentSubFolders);
                fromParent.setUpdatedAt(now);

                List<Folder> toParentSubFolders = toParent.getSubFolders();
                toParentSubFolders.add(folder);
                toParent.setSubFolders(toParentSubFolders);
                toParent.setUpdatedAt(now);

                folderRepository.save(fromParent);
                folderRepository.save(toParent);
            }
            default -> throw new IllegalArgumentException("Unsupported folder update type.");
        }
        return folderRepository.save(folder);
    }

    @Transactional
    public void deleteFolderById(Long folderId) {
        log.info("Delete folder by id: {}", folderId);
        Folder folder = folderRepository
                .findById(folderId)
                .orElseThrow(() -> new FolderNotFoundException(folderId));
        if (Objects.equals(folder.getName(), "root")) {
            throw new RootPreservationException(FolderOperationType.DELETE_FOLDER);
        }
        folderRepository.deleteById(folderId);
    }

}

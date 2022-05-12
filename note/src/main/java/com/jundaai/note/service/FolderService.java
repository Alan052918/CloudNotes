package com.jundaai.note.service;

import com.jundaai.note.exception.FolderNameConflictException;
import com.jundaai.note.exception.FolderNotFoundException;
import com.jundaai.note.exception.RootDeletionException;
import com.jundaai.note.form.FolderCreationForm;
import com.jundaai.note.form.FolderUpdateForm;
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
        boolean nameConflicted = folderRepository.existsByNameWithSameParent(folderName, parent);
        if (nameConflicted) {
            throw new FolderNameConflictException(folderName);
        }

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
                throw new FolderNameConflictException(newName);
            }
            folder.setName(newName);
            isUpdated = true;
        }

        Long toParentId = updateForm.toParentId();
        Folder toParent = folderRepository.findById(toParentId)
                .orElseThrow(() -> new FolderNotFoundException(toParentId));
        Folder fromParent = folder.getParentFolder();
        if (toParent != null && !Objects.equals(toParent, folder)) {
            boolean existsById = folderRepository.existsById(toParent.getId());
            if (!existsById) {
                throw new FolderNotFoundException(toParent.getId());
            }
            if (!Objects.equals(toParent, fromParent)) {
                folder.setParentFolder(toParent);

                List<Folder> fromParentSubFolders = fromParent.getSubFolders();
                fromParentSubFolders.remove(folder);
                fromParent.setSubFolders(fromParentSubFolders);

                List<Folder> toParentSubFolders = toParent.getSubFolders();
                toParentSubFolders.add(folder);
                toParent.setSubFolders(toParentSubFolders);

                isUpdated = true;
            }
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
            throw new RootDeletionException();
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

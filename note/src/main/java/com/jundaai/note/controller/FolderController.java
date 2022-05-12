package com.jundaai.note.controller;

import com.jundaai.note.form.FolderCreationForm;
import com.jundaai.note.form.FolderUpdateForm;
import com.jundaai.note.model.Folder;
import com.jundaai.note.model.assembler.FolderModelAssembler;
import com.jundaai.note.service.FolderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/v1/folders")
@Slf4j
public class FolderController {

    private final FolderService folderService;
    private final FolderModelAssembler folderModelAssembler;

    @Autowired
    public FolderController(FolderService folderService, FolderModelAssembler folderModelAssembler) {
        this.folderService = folderService;
        this.folderModelAssembler = folderModelAssembler;
    }

    @GetMapping
    public CollectionModel<EntityModel<Folder>> getAllFolders() {
        log.info("Request to get all folders");
        List<Folder> folders = folderService.getAllFolders();
        return folderModelAssembler.toCollectionModel(folders);
    }

    @GetMapping(path = "{folderId}")
    public EntityModel<Folder> getFolderById(@PathVariable(name = "folderId") Long folderId) {
        log.info("Request to get folder by id: {}", folderId);
        Folder folder = folderService.getFolderById(folderId);
        return folderModelAssembler.toModel(folder);
    }

    @GetMapping(path = "{folderId}/subFolders")
    public CollectionModel<EntityModel<Folder>> getSubFoldersByParentId(@PathVariable(name = "folderId") Long parentId) {
        log.info("Request to get sub-folders by parent id: {}", parentId);
        List<Folder> subFolders = folderService.getSubFoldersByParentId(parentId);
        return folderModelAssembler.toCollectionModel(subFolders);
    }

    @PostMapping(path = "{folderId}/subFolders")
    @ResponseStatus(HttpStatus.CREATED)
    public EntityModel<Folder> createFolderByParentId(@PathVariable(name = "folderId") Long parentId,
                                            @RequestBody FolderCreationForm creationForm) {
        log.info("Request to create new folder: {}, parent folder id: {}", creationForm, parentId);
        Folder folder = folderService.createFolderByParentId(parentId, creationForm);
        return folderModelAssembler.toModel(folder);
    }

    @PatchMapping(path = "{folderId}")
    public EntityModel<Folder> updateFolderById(@PathVariable(name = "folderId") Long folderId,
                                                @RequestBody FolderUpdateForm updateForm) {
        log.info("Request to update folder by id: {}, form: {}", folderId, updateForm);
        Folder folder = folderService.updateFolderById(folderId, updateForm);
        return folderModelAssembler.toModel(folder);
    }

    @DeleteMapping("{folderId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<?> deleteFolderById(@PathVariable(name = "folderId") Long folderId) {
        log.info("Request to delete folder by id: {}", folderId);
        folderService.deleteFolderById(folderId);
        return ResponseEntity.noContent().build();
    }

}

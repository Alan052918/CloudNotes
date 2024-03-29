package com.jundaai.note.controller;

import java.net.URI;
import java.util.List;

import com.jundaai.note.dto.FolderCreationForm;
import com.jundaai.note.dto.FolderUpdateForm;
import com.jundaai.note.model.Folder;
import com.jundaai.note.model.assembler.FolderModelAssembler;
import com.jundaai.note.service.FolderService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;


@Validated
@RestController
@RequestMapping(path = "api/v1/folders")
@Slf4j
public class FolderController {

    private final FolderService folderService;
    private final FolderModelAssembler folderModelAssembler;

    public FolderController(FolderService folderService, FolderModelAssembler folderModelAssembler) {
        this.folderService = folderService;
        this.folderModelAssembler = folderModelAssembler;
    }

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<Folder>>> getAllFolders() {
        log.info("Request to get all folders");
        final List<Folder> folders = folderService.getAllFolders();
        return ResponseEntity.ok(folderModelAssembler.toCollectionModel(folders));
    }

    @GetMapping(path = "{folderId}")
    public ResponseEntity<EntityModel<Folder>> getFolderById(@PathVariable(name = "folderId") Long folderId) {
        log.info("Request to get folder by id: {}", folderId);
        final Folder folder = folderService.getFolderById(folderId);
        return ResponseEntity.ok(folderModelAssembler.toModel(folder));
    }

    @GetMapping(path = "{folderId}/subFolders")
    public ResponseEntity<CollectionModel<EntityModel<Folder>>> getSubFoldersByParentId(
            @PathVariable(name = "folderId") Long parentId) {
        log.info("Request to get sub-folders by parent id: {}", parentId);
        final List<Folder> subFolders = folderService.getSubFoldersByParentId(parentId);
        return ResponseEntity.ok(folderModelAssembler.toCollectionModel(subFolders));
    }

    @PostMapping(path = "{folderId}/subFolders")
    public ResponseEntity<EntityModel<Folder>> createFolderByParentId(
            @PathVariable(name = "folderId") Long parentId,
            @Valid @RequestBody FolderCreationForm creationForm) {
        log.info("Request to create new folder: {}, parent folder id: {}", creationForm, parentId);
        final Folder folder = folderService.createFolderByParentId(parentId, creationForm);
        final URI uri = MvcUriComponentsBuilder.fromController(getClass())
                .path("{folderId}/subFolders")
                .buildAndExpand(folder.getId()).toUri();
        return ResponseEntity.created(uri).body(folderModelAssembler.toModel(folder));
    }

    @PatchMapping(path = "{folderId}")
    public ResponseEntity<EntityModel<Folder>> updateFolderById(@PathVariable(name = "folderId") Long folderId,
                                                                @Valid @RequestBody FolderUpdateForm updateForm) {
        log.info("Request to update folder by id: {}, dto: {}", folderId, updateForm);
        final Folder folder = folderService.updateFolderById(folderId, updateForm);
        return ResponseEntity.ok(folderModelAssembler.toModel(folder));
    }

    @DeleteMapping("{folderId}")
    public ResponseEntity<?> deleteFolderById(@PathVariable(name = "folderId") Long folderId) {
        log.info("Request to delete folder by id: {}", folderId);
        folderService.deleteFolderById(folderId);
        return ResponseEntity.noContent().build();
    }
}

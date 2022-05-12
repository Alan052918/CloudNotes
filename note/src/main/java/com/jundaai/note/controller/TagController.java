package com.jundaai.note.controller;

import com.jundaai.note.form.create.TagCreationForm;
import com.jundaai.note.form.update.TagUpdateForm;
import com.jundaai.note.model.Tag;
import com.jundaai.note.model.assembler.TagModelAssembler;
import com.jundaai.note.service.TagService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/api/v1")
@Slf4j
public class TagController {

    private final TagService tagService;
    private final TagModelAssembler tagModelAssembler;

    @Autowired
    public TagController(TagService tagService, TagModelAssembler tagModelAssembler) {
        this.tagService = tagService;
        this.tagModelAssembler = tagModelAssembler;
    }

    @GetMapping(path = "tags")
    public CollectionModel<EntityModel<Tag>> getAllTags() {
        log.info("Request to get all tags");
        List<Tag> tags = tagService.getAllTags();
        return tagModelAssembler.toCollectionModel(tags);
    }

    @GetMapping(path = "notes/{noteId}/tags")
    public CollectionModel<EntityModel<Tag>> getAllTagsByNoteId(@PathVariable(name = "noteId") Long noteId) {
        log.info("Request to get all tags by note id: {}", noteId);
        List<Tag> tags = tagService.getAllTagsByNoteId(noteId);
        return tagModelAssembler.toCollectionModel(tags);
    }

    @GetMapping(path = "tags/{tagId}")
    public EntityModel<Tag> getTagById(@PathVariable(name = "tagId") Long tagId) {
        log.info("Request to get tag by id: {}", tagId);
        Tag tag = tagService.getTagById(tagId);
        return tagModelAssembler.toModel(tag);
    }

    @PostMapping(path = "tags")
    @ResponseStatus(HttpStatus.CREATED)
    public EntityModel<Tag> createTag(@Valid @RequestBody TagCreationForm creationForm) {
        log.info("Request to create new tag: {}", creationForm);
        Tag tag = tagService.createTag(creationForm);
        return tagModelAssembler.toModel(tag);
    }

    @PatchMapping(path = "tags/{tagId}")
    public EntityModel<Tag> updateTag(@PathVariable(name = "tagId") Long tagId,
                                      @Valid @RequestBody TagUpdateForm updateForm) {
        log.info("Request to update tag by id: {}, form: {}", tagId, updateForm);
        Tag tag = tagService.updateTagById(tagId, updateForm);
        return tagModelAssembler.toModel(tag);
    }

    @DeleteMapping(path = "tags/{tagId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<?> deleteTagById(@PathVariable(name = "tagId") Long tagId) {
        log.info("Request to delete tag by id: {}", tagId);
        tagService.deleteTagById(tagId);
        return ResponseEntity.noContent().build();
    }

}

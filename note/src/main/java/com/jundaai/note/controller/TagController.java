package com.jundaai.note.controller;

import java.net.URI;
import java.util.List;

import javax.validation.Valid;

import com.jundaai.note.form.tag.TagCreationForm;
import com.jundaai.note.form.tag.TagUpdateForm;
import com.jundaai.note.model.Tag;
import com.jundaai.note.model.assembler.TagModelAssembler;
import com.jundaai.note.service.TagService;
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
@RequestMapping(path = "/api/v1")
@Slf4j
public class TagController {

    private final TagService tagService;
    private final TagModelAssembler tagModelAssembler;

    public TagController(TagService tagService, TagModelAssembler tagModelAssembler) {
        this.tagService = tagService;
        this.tagModelAssembler = tagModelAssembler;
    }

    @GetMapping(path = "tags")
    public ResponseEntity<CollectionModel<EntityModel<Tag>>> getAllTags() {
        log.info("Request to get all tags");
        final List<Tag> tags = tagService.getAllTags();
        return ResponseEntity.ok(tagModelAssembler.toCollectionModel(tags));
    }

    @GetMapping(path = "notes/{noteId}/tags")
    public ResponseEntity<CollectionModel<EntityModel<Tag>>> getAllTagsByNoteId(
            @PathVariable(name = "noteId") Long noteId) {
        log.info("Request to get all tags by note id: {}", noteId);
        final List<Tag> tags = tagService.getAllTagsByNoteId(noteId);
        return ResponseEntity.ok(tagModelAssembler.toCollectionModel(tags));
    }

    @GetMapping(path = "tags/{tagId}")
    public ResponseEntity<EntityModel<Tag>> getTagById(@PathVariable(name = "tagId") Long tagId) {
        log.info("Request to get tag by id: {}", tagId);
        final Tag tag = tagService.getTagById(tagId);
        return ResponseEntity.ok(tagModelAssembler.toModel(tag));
    }

    @PostMapping(path = "tags")
    public ResponseEntity<EntityModel<Tag>> createTag(@Valid @RequestBody TagCreationForm creationForm) {
        log.info("Request to create new tag: {}", creationForm);
        final Tag tag = tagService.createTag(creationForm);
        final URI uri = MvcUriComponentsBuilder.fromController(TagController.class)
                .path("tags")
                .buildAndExpand(tag.getId())
                .toUri();
        return ResponseEntity.created(uri).body(tagModelAssembler.toModel(tag));
    }

    @PatchMapping(path = "tags/{tagId}")
    public ResponseEntity<EntityModel<Tag>> updateTag(@PathVariable(name = "tagId") Long tagId,
                                                      @Valid @RequestBody TagUpdateForm updateForm) {
        log.info("Request to update tag by id: {}, form: {}", tagId, updateForm);
        final Tag tag = tagService.updateTagById(tagId, updateForm);
        return ResponseEntity.ok(tagModelAssembler.toModel(tag));
    }

    @DeleteMapping(path = "tags/{tagId}")
    public ResponseEntity<?> deleteTagById(@PathVariable(name = "tagId") Long tagId) {
        log.info("Request to delete tag by id: {}", tagId);
        tagService.deleteTagById(tagId);
        return ResponseEntity.noContent().build();
    }
}

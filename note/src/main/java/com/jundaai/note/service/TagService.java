package com.jundaai.note.service;

import com.jundaai.note.exception.TagNameConflictException;
import com.jundaai.note.exception.TagNotFoundException;
import com.jundaai.note.form.tag.TagCreationForm;
import com.jundaai.note.form.tag.TagUpdateForm;
import com.jundaai.note.model.Tag;
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
public class TagService {

    private final NoteRepository noteRepository;
    private final TagRepository tagRepository;

    @Autowired
    public TagService(NoteRepository noteRepository, TagRepository tagRepository) {
        this.noteRepository = noteRepository;
        this.tagRepository = tagRepository;
    }

    public List<Tag> getAllTags() {
        log.info("Get all tags");
        return tagRepository.findAll();
    }

    public List<Tag> getAllTagsByNoteId(Long noteId) {
        log.info("Get all tags by note id: {}", noteId);
        return noteRepository.getTagsById(noteId);
    }

    public Tag getTagById(Long tagId) {
        log.info("Get tag by id: {}", tagId);
        return tagRepository.findById(tagId)
                .orElseThrow(() -> new TagNotFoundException(tagId));
    }

    @Transactional
    public Tag createTag(TagCreationForm creationForm) {
        log.info("Create new tag: {}", creationForm);

        String name = creationForm.getName();
        if (name == null) {
            throw new IllegalArgumentException("Tag name cannot be null.");
        }
        boolean existsByName = tagRepository.existsByName(name);
        if (existsByName) {
            throw new TagNameConflictException(name);
        }

        ZonedDateTime now = ZonedDateTime.now();
        Tag tag = Tag.builder()
                .name(name)
                .createdAt(now)
                .updatedAt(now)
                .notes(new ArrayList<>())
                .build();

        return tagRepository.save(tag);
    }


    @Transactional
    public Tag updateTagById(Long tagId, TagUpdateForm updateForm) {
        log.info("Update tag by id: {}, form: {}", tagId, updateForm);
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new TagNotFoundException(tagId));

        String newName = updateForm.getNewName();
        if (!Objects.equals(newName, tag.getName())) {
            boolean nameConflicted = tagRepository.existsByName(newName);
            if (nameConflicted) {
                throw new TagNameConflictException(newName);
            }
            tag.setName(newName);
            tag.setUpdatedAt(ZonedDateTime.now());
        }

        return tagRepository.save(tag);
    }

    @Transactional
    public void deleteTagById(Long tagId) {
        log.info("Delete tag by id: {}", tagId);
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new TagNotFoundException(tagId));

        tag.getNotes().forEach(note -> {
            List<Tag> noteTags = note.getTags();
            noteTags.remove(tag);
            note.setTags(noteTags);
        });

        tagRepository.deleteById(tagId);
    }

}

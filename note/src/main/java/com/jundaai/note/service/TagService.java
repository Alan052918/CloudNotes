package com.jundaai.note.service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import com.jundaai.note.exception.NoteNotFoundException;
import com.jundaai.note.exception.TagNameBlankException;
import com.jundaai.note.exception.TagNameConflictException;
import com.jundaai.note.exception.TagNotFoundException;
import com.jundaai.note.dto.TagOperationForm;
import com.jundaai.note.model.Tag;
import com.jundaai.note.repository.NoteRepository;
import com.jundaai.note.repository.TagRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class TagService {

    private final NoteRepository noteRepository;
    private final TagRepository tagRepository;

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
        return noteRepository.findAllTagsById(noteId)
                .orElseThrow(() -> new NoteNotFoundException(noteId));
    }

    public Tag getTagById(Long tagId) {
        log.info("Get tag by id: {}", tagId);
        return tagRepository.findById(tagId)
                .orElseThrow(() -> new TagNotFoundException("id: " + tagId));
    }

    @Transactional
    public Tag createTag(TagOperationForm creationForm) {
        log.info("Create new tag: {}", creationForm);
        String name = creationForm.name();
        if (name == null || name.isBlank()) {
            throw new TagNameBlankException();
        }
        if (tagRepository.existsByName(name)) {
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
    public Tag updateTagById(Long tagId, TagOperationForm updateForm) {
        log.info("Update tag by id: {}, dto: {}", tagId, updateForm);
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new TagNotFoundException("id: " + tagId));
        String newName = updateForm.name();
        if (newName == null || newName.isBlank()) {
            throw new TagNameBlankException();
        }
        if (tagRepository.existsByName(newName)) {
            throw new TagNameConflictException(newName);
        }
        tag.setName(newName);
        tag.setUpdatedAt(ZonedDateTime.now());
        return tagRepository.save(tag);
    }

    @Transactional
    public void deleteTagById(Long tagId) {
        log.info("Delete tag by id: {}", tagId);
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new TagNotFoundException("id: " + tagId));
        tag.getNotes().forEach(note -> {
            List<Tag> noteTags = note.getTags();
            noteTags.remove(tag);
            note.setTags(noteTags);
            noteRepository.save(note);
        });
        tagRepository.deleteById(tagId);
    }
}

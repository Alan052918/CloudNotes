package com.jundaai.note.repository;

import java.util.List;
import java.util.Optional;

import com.jundaai.note.model.Folder;
import com.jundaai.note.model.Note;
import com.jundaai.note.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface NoteRepository extends JpaRepository<Note, Long> {

    @Query(value = "select count(n) > 0 from Note n where n.name = ?1 and n.folder = ?2")
    boolean existsByNameWithSameFolder(String noteName, Folder folder);

    @Query(value = "select n.tags from Note n where n.id = ?1")
    Optional<List<Tag>> findAllTagsById(Long noteId);
}

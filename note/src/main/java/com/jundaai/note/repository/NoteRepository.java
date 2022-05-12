package com.jundaai.note.repository;

import com.jundaai.note.model.Folder;
import com.jundaai.note.model.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {

    @Query(value = "select count(n) > 0 from Note n where n.name = ?1 and n.folder = ?2")
    boolean existsByNameWithSameFolder(String noteName, Folder folder);

}

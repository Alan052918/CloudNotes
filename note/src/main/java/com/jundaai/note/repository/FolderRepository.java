package com.jundaai.note.repository;

import com.jundaai.note.model.Folder;
import com.jundaai.note.model.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FolderRepository extends JpaRepository<Folder, Long> {

    @Query("select f.subFolders from Folder f where f.id = ?1")
    List<Folder> getSubFoldersByParentId(Long parentId);

    @Query("select f.notes from Folder f where f.id = ?1")
    List<Note> getNotesById(Long folderId);

    @Query("select count(f) > 0 from Folder f where f.name = ?1 and f.parentFolder = ?2")
    boolean existsByNameWithSameParent(String name, Folder parent);

}

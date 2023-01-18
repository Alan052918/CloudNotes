package com.jundaai.note.repository;

import java.util.List;
import java.util.Optional;

import com.jundaai.note.model.Folder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface FolderRepository extends JpaRepository<Folder, Long> {

    @Query(value = "select f.subFolders from Folder f where f.id = ?1")
    Optional<List<Folder>> findSubFoldersByParentId(Long parentId);

    @Query(value = "select count(f) > 0 from Folder f where f.name = ?1 and f.parentFolder = ?2")
    boolean existsByNameWithSameParent(String name, Folder parent);
}

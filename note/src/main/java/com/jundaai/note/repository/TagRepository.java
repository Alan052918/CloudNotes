package com.jundaai.note.repository;

import com.jundaai.note.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {

    @Query(value = "select count(t) > 0 from Tag t where t.name = ?1")
    boolean existsByName(String name);

    @Query(value = "select t from Tag t where t.name = ?1")
    Optional<Tag> findByName(String tagName);

    @Query(value = "select t from Tag t where t.name = ?1")
    Tag getByName(String tagName);

}

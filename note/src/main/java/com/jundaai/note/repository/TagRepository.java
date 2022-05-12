package com.jundaai.note.repository;

import com.jundaai.note.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

    @Query(value = "select count(t) from Tag t where t.name = ?1")
    boolean existsByName(String name);

}

package com.jundaai.note.repository;

import com.jundaai.note.model.Folder;
import com.jundaai.note.model.Tag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class NoteRepositoryTest extends RepositoryTest {

    @Autowired
    private NoteRepository testRepository;

    @BeforeEach
    void setUp() {
        super.setUp();
        testRepository.saveAll(mockNotes);
    }

    @Test
    public void existsByNameWithSameFolder_True() {
        // given
        String testName = mockNotes.get(0).getName();
        Folder testFolder = mockFolders.get(1);

        // when
        boolean existsByNameWithSameFolder = testRepository.existsByNameWithSameFolder(testName, testFolder);

        // then
        assertTrue(existsByNameWithSameFolder);
    }

    @Test
    public void existsByNameWithSameFolder_False() {
        // given
        String testName = "New Note";
        Folder testFolder = mockFolders.get(1);

        // when
        boolean existsByNameWithSameFolder = testRepository.existsByNameWithSameFolder(testName, testFolder);

        // then
        assertFalse(existsByNameWithSameFolder);
    }

    @Test
    public void findAllTagsById_Success() {
        // given
        Long testId = testRepository.findAll().get(0).getId();
        List<String> expectedTagNames = Collections.singletonList(mockTags.get(0).getName());

        // when
        List<Tag> gotTags = testRepository.findAllTagsById(testId).orElse(null);

        // then
        assert gotTags != null;
        assertEquals(expectedTagNames, gotTags.stream().map(Tag::getName).toList());
    }

    @Test
    public void findAllTagsById_NotExistingId_EmptyListReturned() {
        // given
        Long notExistingId = -1L;

        // when
        List<Tag> gotTags = testRepository.findAllTagsById(notExistingId).orElse(null);

        // then
        assertEquals(new ArrayList<>(), gotTags);
    }

}

package com.jundaai.note.repository;

import com.jundaai.note.model.Tag;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class TagRepositoryTest extends RepositoryTest {

    @Autowired
    private TagRepository testRepository;

    @Override
    @BeforeEach
    void setUp() {
        super.setUp();
        testRepository.saveAll(mockTags);
    }

    @AfterEach
    public void tearDown() {
        testRepository.deleteAll();
    }

    @Test
    public void existsByName_True() {
        // given
        String testName = mockTags.get(0).getName();

        // when
        boolean existsByName = testRepository.existsByName(testName);

        // then
        assertTrue(existsByName);
    }

    @Test
    public void existsByName_False() {
        // given
        String notExistingName = "New Name";

        // when
        boolean existsByName = testRepository.existsByName(notExistingName);

        // then
        assertFalse(existsByName);
    }

    @Test
    public void findByName_Success() {
        // given
        Tag expectedTag = mockTags.get(0);
        String testName = expectedTag.getName();

        // when
        Tag gotTag = testRepository.findByName(testName).orElse(null);

        // then
        assertEquals(expectedTag, gotTag);
    }

    @Test
    public void findByName_EmptyReturned() {
        // given
        String notExistingName = "New Name";

        // when
        Tag gotTag = testRepository.findByName(notExistingName).orElse(null);

        // then
        assertNull(gotTag);
    }

    @Test
    public void getByName_Success() {
        // given
        Tag expectedTag = mockTags.get(0);
        String testName = expectedTag.getName();

        // when
        Tag gotTag = testRepository.getByName(testName);

        // then
        assertEquals(expectedTag, gotTag);
    }

}

package com.jundaai.note.repository;

import com.jundaai.note.model.Folder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class FolderRepositoryTest extends RepositoryTest {

    @Autowired
    private FolderRepository testRepository;

    @Override
    @BeforeEach
    void setUp() {
        super.setUp();
        testRepository.saveAll(mockFolders);
    }

    @AfterEach
    public void tearDown() {
        testRepository.deleteAll();
    }

    @Test
    public void findSubFoldersByParentId_Success() {
        // given
        Long testId = testRepository.findAll().get(0).getId();
        List<String> expectedFolderNames = Arrays.asList(mockFolders.get(1).getName(), mockFolders.get(2).getName());

        // when
        List<Folder> gotFolders = testRepository.findSubFoldersByParentId(testId).orElse(null);

        // then
        assert gotFolders != null;
        assertEquals(expectedFolderNames, gotFolders.stream().map(Folder::getName).toList());
    }

    @Test
    public void findSubFoldersByParentId_NotExistingParentId_EmptyListReturned() {
        // given
        Long notExistingId = -1L;

        // when
        List<Folder> gotFolders = testRepository.findSubFoldersByParentId(notExistingId).orElse(null);

        // then
        assertEquals(new ArrayList<>(), gotFolders);
    }

    @Test
    public void existsByNameWithSameParent_True() {
        // given
        String testName = mockFolders.get(1).getName();
        Folder testParent = mockFolders.get(0);

        // when
        boolean existsByNameWithSameParent = testRepository.existsByNameWithSameParent(testName, testParent);

        // then
        assertTrue(existsByNameWithSameParent);
    }

    @Test
    public void existsByNameWithSameParent_False() {
        // given
        String testName = "New Folder";
        Folder testParent = mockFolders.get(0);

        // when
        boolean existsByNameWithSameParent = testRepository.existsByNameWithSameParent(testName, testParent);

        // then
        assertFalse(existsByNameWithSameParent);
    }

}

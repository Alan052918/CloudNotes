package com.jundaai.note;

import com.jundaai.note.controller.FolderController;
import com.jundaai.note.controller.NoteController;
import com.jundaai.note.controller.TagController;
import com.jundaai.note.repository.FolderRepository;
import com.jundaai.note.repository.NoteRepository;
import com.jundaai.note.repository.TagRepository;
import com.jundaai.note.service.FolderService;
import com.jundaai.note.service.NoteService;
import com.jundaai.note.service.TagService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class SmokeTest {

    @Autowired
    private FolderController folderController;
    @Autowired
    private NoteController noteController;
    @Autowired
    private TagController tagController;

    @Autowired
    private FolderService folderService;
    @Autowired
    private NoteService noteService;
    @Autowired
    private TagService tagService;

    @Autowired
    private FolderRepository folderRepository;
    @Autowired
    private NoteRepository noteRepository;
    @Autowired
    private TagRepository tagRepository;

    @Test
    public void contextLoads() {
        assertNotNull(folderController);
        assertNotNull(noteController);
        assertNotNull(tagController);

        assertNotNull(folderService);
        assertNotNull(noteService);
        assertNotNull(tagService);

        assertNotNull(folderRepository);
        assertNotNull(noteRepository);
        assertNotNull(tagRepository);
    }

}

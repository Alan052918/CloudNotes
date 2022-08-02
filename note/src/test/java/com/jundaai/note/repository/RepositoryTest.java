package com.jundaai.note.repository;

import com.jundaai.note.model.Folder;
import com.jundaai.note.model.Note;
import com.jundaai.note.model.Tag;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class RepositoryTest {

    @Autowired
    private FolderRepository mockFolderRepository;
    @Autowired
    private NoteRepository mockNoteRepository;
    @Autowired
    private TagRepository mockTagRepository;

    List<Folder> mockFolders;
    List<Note> mockNotes;
    List<Tag> mockTags;

    @BeforeEach
    void setUp() {
        mockFolders = new ArrayList<>();
        mockNotes = new ArrayList<>();
        mockTags = new ArrayList<>();
        loadFoldersNotesAndTags();
    }

    void loadFoldersNotesAndTags() {
        ZonedDateTime now = ZonedDateTime.now();
        Folder root = mockFolderRepository.save(
                Folder
                        .builder()
                        .name("root")
                        .createdAt(now)
                        .updatedAt(now)
                        .parentFolder(null)
                        .subFolders(new ArrayList<>())
                        .notes(new ArrayList<>())
                        .build()
        );
        Folder pl = mockFolderRepository.save(
                Folder
                        .builder()
                        .name("Programming Languages")
                        .createdAt(now)
                        .updatedAt(now)
                        .parentFolder(root)
                        .subFolders(new ArrayList<>())
                        .notes(new ArrayList<>())
                        .build()
        );
        Folder ds = mockFolderRepository.save(
                Folder
                        .builder()
                        .name("Data Structures")
                        .createdAt(now)
                        .updatedAt(now)
                        .parentFolder(root)
                        .subFolders(new ArrayList<>())
                        .notes(new ArrayList<>())
                        .build()
        );
        Note go = mockNoteRepository.save(
                Note
                        .builder()
                        .name("Go")
                        .content("Go is a general purpose programming language.")
                        .createdAt(now)
                        .updatedAt(now)
                        .folder(pl)
                        .tags(new ArrayList<>())
                        .build()
        );
        Tag google = mockTagRepository.save(
                Tag
                        .builder()
                        .name("Google")
                        .createdAt(now)
                        .updatedAt(now)
                        .notes(new ArrayList<>())
                        .build()
        );
        Tag microsoft = mockTagRepository.save(
                Tag
                        .builder()
                        .name("Microsoft")
                        .createdAt(now)
                        .updatedAt(now)
                        .notes(new ArrayList<>())
                        .build()
        );
        List<Tag> tags = go.getTags();
        tags.add(google);
        go.setTags(tags);
        List<Note> notes = new ArrayList<>();
        notes.add(go);
        pl.setNotes(notes);
        google.setNotes(notes);
        mockFolders.add(root);
        mockFolders.add(pl);
        mockFolders.add(ds);
        mockNotes.add(go);
        mockTags.add(google);
        mockTags.add(microsoft);
    }

}

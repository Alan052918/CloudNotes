package com.jundaai.note.config;

import com.jundaai.note.model.Folder;
import com.jundaai.note.model.Note;
import com.jundaai.note.model.Tag;
import com.jundaai.note.repository.FolderRepository;
import com.jundaai.note.repository.NoteRepository;
import com.jundaai.note.repository.TagRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Configuration
@Slf4j
public class LoadDatabase {

    @Bean
    CommandLineRunner initDatabase(FolderRepository folderRepository,
                                   NoteRepository noteRepository,
                                   TagRepository tagRepository) {
        log.info("Loading Database...");
        return args -> {
            ZonedDateTime now = ZonedDateTime.now();
            Folder root = folderRepository.save(Folder.builder()
                    .name("root")
                    .createdAt(now)
                    .updatedAt(now)
                    .parentFolder(null)
                    .subFolders(new ArrayList<>())
                    .notes(new ArrayList<>())
                    .build());
            Folder java = folderRepository.save(Folder.builder()
                    .name("Java")
                    .createdAt(now)
                    .updatedAt(now)
                    .parentFolder(root)
                    .subFolders(new ArrayList<>())
                    .notes(new ArrayList<>())
                    .build());
            folderRepository.save(Folder.builder()
                    .name("iOS")
                    .createdAt(now)
                    .updatedAt(now)
                    .parentFolder(root)
                    .subFolders(new ArrayList<>())
                    .notes(new ArrayList<>())
                    .build());
            noteRepository.save(Note.builder()
                    .name("Summary")
                    .content("This is a cloud note storage.")
                    .createdAt(now)
                    .updatedAt(now)
                    .folder(root)
                    .tags(new ArrayList<>())
                    .build());

            Note jdbc = noteRepository.save(Note.builder()
                    .name("JDBC")
                    .content("Java Database Connection API.")
                    .createdAt(now)
                    .updatedAt(now)
                    .folder(java)
                    .tags(new ArrayList<>())
                    .build());
            Tag pl = tagRepository.save(Tag.builder()
                    .name("Programming Language")
                    .createdAt(now)
                    .updatedAt(now)
                    .notes(new ArrayList<>())
                    .build());

            List<Tag> tags = jdbc.getTags();
            tags.add(pl);
            jdbc.setTags(tags);

            noteRepository.save(jdbc);
            tagRepository.save(pl);
        };
    }

}

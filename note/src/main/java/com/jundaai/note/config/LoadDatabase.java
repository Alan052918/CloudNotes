package com.jundaai.note.config;

import com.jundaai.note.model.Folder;
import com.jundaai.note.repository.FolderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.ZonedDateTime;
import java.util.ArrayList;

@Configuration
@Slf4j
public class LoadDatabase {

    @Bean
    CommandLineRunner initDatabase(FolderRepository folderRepository) {
        return args -> {
            ZonedDateTime now = ZonedDateTime.now();
            folderRepository.save(Folder.builder()
                    .name("root")
                    .createdAt(now)
                    .updatedAt(now)
                    .parentFolder(null)
                    .subFolders(new ArrayList<>())
                    .notes(new ArrayList<>())
                    .build());
        };
    }

}

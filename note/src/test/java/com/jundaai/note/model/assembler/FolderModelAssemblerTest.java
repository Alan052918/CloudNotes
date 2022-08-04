package com.jundaai.note.model.assembler;

import com.jundaai.note.controller.FolderController;
import com.jundaai.note.model.Folder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class FolderModelAssemblerTest {

    @Autowired
    private FolderModelAssembler testModelAssembler;

    @Test
    public void toModel_Success() {
        // given
        ZonedDateTime now = ZonedDateTime.now();
        Folder folder = Folder
                .builder()
                .id(1L)
                .name("Folder")
                .createdAt(now)
                .updatedAt(now)
                .parentFolder(null)
                .subFolders(new ArrayList<>())
                .notes(new ArrayList<>())
                .build();
        EntityModel<Folder> expectedModel = EntityModel.of(
                folder,
                linkTo(methodOn(FolderController.class).getFolderById(folder.getId())).withSelfRel(),
                linkTo(methodOn(FolderController.class).getAllFolders()).withRel("all folders")
        );

        // when
        EntityModel<Folder> gotModel = testModelAssembler.toModel(folder);

        // then
        assertEquals(expectedModel, gotModel);
    }

    @Test
    public void toCollectionModel_Success() {
        // given
        ZonedDateTime now = ZonedDateTime.now();
        Folder folder1 = Folder
                .builder()
                .id(1L)
                .name("Folder")
                .createdAt(now)
                .updatedAt(now)
                .parentFolder(null)
                .subFolders(new ArrayList<>())
                .notes(new ArrayList<>())
                .build();
        Folder folder2 = Folder
                .builder()
                .id(2L)
                .name("Another folder")
                .createdAt(now)
                .updatedAt(now)
                .parentFolder(null)
                .subFolders(new ArrayList<>())
                .notes(new ArrayList<>())
                .build();
        List<Folder> folders = List.of(folder1, folder2);
        CollectionModel<EntityModel<Folder>> expectedModel = folders
                .stream()
                .map(folder -> EntityModel.of(
                        folder,
                        linkTo(methodOn(FolderController.class).getFolderById(folder.getId())).withSelfRel(),
                        linkTo(methodOn(FolderController.class).getAllFolders()).withRel("all folders")
                ))
                .collect(Collectors.collectingAndThen(Collectors.toList(), CollectionModel::of));

        // when
        CollectionModel<EntityModel<Folder>> gotModel = testModelAssembler.toCollectionModel(folders);

        // then
        assertEquals(expectedModel, gotModel);
    }

}

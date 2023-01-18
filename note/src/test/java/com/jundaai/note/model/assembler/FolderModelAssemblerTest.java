package com.jundaai.note.model.assembler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.jundaai.note.controller.FolderController;
import com.jundaai.note.model.Folder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;


@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class FolderModelAssemblerTest {

    List<Folder> mockFolders;
    @Autowired
    private FolderModelAssembler testModelAssembler;

    @BeforeEach
    void setUp() {
        mockFolders = new ArrayList<>();
        loadFolders();
    }

    void loadFolders() {
        ZonedDateTime now = ZonedDateTime.now();
        Folder parentFolder = Folder.builder()
                .id(1L)
                .name("Parent Folder")
                .createdAt(now)
                .updatedAt(now)
                .parentFolder(null)
                .subFolders(new ArrayList<>())
                .notes(new ArrayList<>())
                .build();
        mockFolders.add(parentFolder);
        Folder folder = Folder.builder()
                .id(2L)
                .name("Folder")
                .createdAt(now)
                .updatedAt(now)
                .parentFolder(parentFolder)
                .subFolders(new ArrayList<>())
                .notes(new ArrayList<>())
                .build();
        mockFolders.add(folder);
    }

    @Test
    public void toModel_Success() {
        // given
        Folder folder = mockFolders.get(1);
        EntityModel<Folder> expectedModel = EntityModel.of(folder,
                linkTo(methodOn(FolderController.class).getFolderById(folder.getId())).withSelfRel(),
                linkTo(methodOn(FolderController.class).getFolderById(folder.getParentFolder().getId()))
                        .withRel("parent"),
                linkTo(methodOn(FolderController.class).getAllFolders()).withRel("all folders"));

        // when
        EntityModel<Folder> gotModel = testModelAssembler.toModel(folder);

        // then
        assertEquals(expectedModel, gotModel);
    }

    @Test
    public void toCollectionModel_Success() {
        // given
        CollectionModel<EntityModel<Folder>> expectedModel = mockFolders.stream()
                .map(folder -> {
                    EntityModel<Folder> entityModel = EntityModel.of(folder,
                            linkTo(methodOn(FolderController.class).getFolderById(folder.getId())).withSelfRel());
                    if (folder.getParentFolder() != null) {
                        entityModel.add(
                                linkTo(methodOn(FolderController.class).getFolderById(folder.getParentFolder().getId()))
                                        .withRel("parent"));
                    }
                    entityModel.add(linkTo(methodOn(FolderController.class).getAllFolders()).withRel("all folders"));
                    return entityModel;
                })
                .collect(Collectors.collectingAndThen(Collectors.toList(), CollectionModel::of));

        // when
        CollectionModel<EntityModel<Folder>> gotModel = testModelAssembler.toCollectionModel(mockFolders);

        // then
        assertEquals(expectedModel, gotModel);
    }
}

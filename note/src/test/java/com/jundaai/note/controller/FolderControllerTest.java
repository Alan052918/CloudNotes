package com.jundaai.note.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.jundaai.note.dto.FolderCreationForm;
import com.jundaai.note.dto.FolderUpdateForm;
import com.jundaai.note.dto.FolderUpdateType;
import com.jundaai.note.exception.FolderNotFoundException;
import com.jundaai.note.exception.advice.RestResponseEntityExceptionHandler;
import com.jundaai.note.exception.advice.ValidationExceptionHandler;
import com.jundaai.note.model.Folder;
import com.jundaai.note.model.assembler.FolderModelAssembler;
import com.jundaai.note.service.FolderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;


@WebMvcTest(FolderController.class)
public class FolderControllerTest extends ControllerTest {

    @MockBean
    private FolderService mockFolderService;
    @MockBean
    private FolderModelAssembler mockFolderModelAssembler;

    @Override
    @BeforeEach
    void setUp() {
        super.setUp();
        JacksonTester.initFields(this, mapper);
        mockMvc = MockMvcBuilders.standaloneSetup(new FolderController(mockFolderService, mockFolderModelAssembler))
                .setControllerAdvice(ValidationExceptionHandler.class, RestResponseEntityExceptionHandler.class)
                .build();
    }

    @Test
    public void getAllFolders_200Ok() throws Exception {
        // given
        CollectionModel<EntityModel<Folder>> collectionModel = mockFolders.stream()
                .map(folder -> EntityModel.of(folder,
                        linkTo(methodOn(FolderController.class).getFolderById(folder.getId())).withSelfRel()))
                .collect(Collectors.collectingAndThen(Collectors.toList(), CollectionModel::of));

        // when, then
        when(mockFolderService.getAllFolders()).thenReturn(mockFolders);
        when(mockFolderModelAssembler.toCollectionModel(mockFolders)).thenReturn(collectionModel);
        mockMvc.perform(get(BASE_PATH + FOLDER_PATH).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].id").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].name").value("root"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[1].id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[1].name").value("Programming Languages"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[2].id").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[2].name").value("Data Structures"));

        verify(mockFolderService).getAllFolders();
        verify(mockFolderModelAssembler).toCollectionModel(mockFolders);
    }

    @Test
    public void getFolderById_200Ok() throws Exception {
        // given
        var testId = mockFolderIds.get(0);
        var testFolder = mockFolders.get(0);
        EntityModel<Folder> entityModel = EntityModel.of(testFolder,
                linkTo(methodOn(FolderController.class).getFolderById(testId)).withSelfRel());

        // when, then
        when(mockFolderService.getFolderById(testId)).thenReturn(testFolder);
        when(mockFolderModelAssembler.toModel(testFolder)).thenReturn(entityModel);
        mockMvc.perform(get(BASE_PATH + FOLDER_PATH + "/" + testId).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(8))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("root"));

        verify(mockFolderService).getFolderById(testId);
        verify(mockFolderModelAssembler).toModel(testFolder);
    }

    @Test
    public void getSubFoldersByParentId_200Ok() throws Exception {
        // given
        var testId = mockFolderIds.get(0);
        var testFolders = List.of(mockFolders.get(1), mockFolders.get(2));
        CollectionModel<EntityModel<Folder>> collectionModel = testFolders.stream()
                .map(folder -> EntityModel.of(folder,
                        linkTo(methodOn(FolderController.class).getFolderById(folder.getId())).withSelfRel()))
                .collect(Collectors.collectingAndThen(Collectors.toList(), CollectionModel::of));

        // when, then
        when(mockFolderService.getSubFoldersByParentId(testId)).thenReturn(testFolders);
        when(mockFolderModelAssembler.toCollectionModel(testFolders)).thenReturn(collectionModel);
        mockMvc.perform(get(BASE_PATH + FOLDER_PATH + "/" + testId + "/subFolders")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].name").value("Programming Languages"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[1].id").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[1].name").value("Data Structures"));

        verify(mockFolderService).getSubFoldersByParentId(testId);
        verify(mockFolderModelAssembler).toCollectionModel(testFolders);
    }

    @Test
    public void createFolderByParentId_201Created() throws Exception {
        // given
        var testId = mockFolderIds.get(0);
        var newId = 100L;
        var now = ZonedDateTime.now();
        var newFolder = Folder.builder()
                .id(newId)
                .name("New Folder")
                .createdAt(now)
                .updatedAt(now)
                .parentFolder(mockFolders.get(0))
                .subFolders(new ArrayList<>())
                .notes(new ArrayList<>())
                .build();
        EntityModel<Folder> entityModel = EntityModel.of(newFolder,
                linkTo(methodOn(FolderController.class).getFolderById(newId)).withSelfRel());
        var requestBody = mapper.writeValueAsString(new FolderCreationForm("New Folder"));

        // when, then
        when(mockFolderService.createFolderByParentId(eq(testId), any(FolderCreationForm.class))).thenReturn(newFolder);
        when(mockFolderModelAssembler.toModel(newFolder)).thenReturn(entityModel);
        mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH + FOLDER_PATH + "/" + testId + "/subFolders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(8))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(100))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("New Folder"));

        verify(mockFolderService).createFolderByParentId(eq(testId), any(FolderCreationForm.class));
        verify(mockFolderModelAssembler).toModel(newFolder);
    }

    @Test
    public void createFolderByParentId_InvalidFolderName_400BadRequest() throws Exception {
        // given
        var testId = mockFolderIds.get(0);
        var requestBody1 = mapper.writeValueAsString(FolderCreationForm.builder().build());
        var requestBody2 = mapper.writeValueAsString(FolderCreationForm.builder().name("").build());

        // when, then
        mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH + FOLDER_PATH + "/" + testId + "/subFolders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody1)
                        .characterEncoding("UTF-8"))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name")
                        .value("Folder name cannot be blank (null or all whitespaces)."));
        mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH + FOLDER_PATH + "/" + testId + "/subFolders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody2)
                        .characterEncoding("UTF-8"))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name")
                        .value("Folder name cannot be blank (null or all whitespaces)."));
    }

    @Test
    public void updateFolderById_200Ok() throws Exception {
        // given
        var testFolder = mockFolders.get(0);

        assertEquals("root", testFolder.getName());

        var testId = testFolder.getId();
        var now = ZonedDateTime.now();
        testFolder.setName("New Name");
        testFolder.setUpdatedAt(now);
        EntityModel<Folder> entityModel = EntityModel.of(testFolder,
                linkTo(methodOn(FolderController.class).getFolderById(testId)).withSelfRel());
        var requestBody = mapper.writeValueAsString(FolderUpdateForm.builder()
                .updateType(FolderUpdateType.RENAME_FOLDER.name())
                .newName("New Name")
                .build());

        // when, then
        when(mockFolderService.updateFolderById(eq(testId), any(FolderUpdateForm.class))).thenReturn(testFolder);
        when(mockFolderModelAssembler.toModel(testFolder)).thenReturn(entityModel);
        mockMvc.perform(MockMvcRequestBuilders.patch(BASE_PATH + FOLDER_PATH + "/" + testId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(8))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("New Name"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.updatedAt").value(now.toEpochSecond()));

        verify(mockFolderService).updateFolderById(eq(testId), any(FolderUpdateForm.class));
        verify(mockFolderModelAssembler).toModel(testFolder);
    }

    @Test
    public void updateFolderById_NotExistingId_404NotFound() throws Exception {
        // given
        var notExistingId = -1L;
        var requestBody = mapper.writeValueAsString(FolderUpdateForm.builder()
                .updateType(FolderUpdateType.RENAME_FOLDER.name())
                .newName("New Name")
                .build());

        // when, then
        when(mockFolderService.updateFolderById(eq(notExistingId), any(FolderUpdateForm.class)))
                .thenThrow(new FolderNotFoundException(notExistingId));
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.patch(BASE_PATH + FOLDER_PATH + "/" + notExistingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .characterEncoding("UTF-8"))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andReturn();

        verify(mockFolderService).updateFolderById(eq(notExistingId), any(FolderUpdateForm.class));
        assertEquals("Folder by id: " + notExistingId + " was not found.", result.getResponse().getContentAsString());
    }

    @Test
    public void deleteFolderById_204NoContent() throws Exception {
        // given
        var testId = mockFolderIds.get(1);

        // when, then
        mockMvc.perform(MockMvcRequestBuilders.delete(BASE_PATH + FOLDER_PATH + "/" + testId))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        verify(mockFolderService).deleteFolderById(testId);
    }

    @Test
    public void deleteFolderById_NotExistingId_404NotFound() throws Exception {
        // given
        var notExistingId = -1L;

        // when, then
        doThrow(new FolderNotFoundException(notExistingId)).when(mockFolderService).deleteFolderById(notExistingId);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.delete(BASE_PATH + FOLDER_PATH + "/" + notExistingId)
                        .characterEncoding("UTF-8"))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andReturn();

        verify(mockFolderService).deleteFolderById(notExistingId);
        assertEquals("Folder by id: " + notExistingId + " was not found.", result.getResponse().getContentAsString());
    }
}

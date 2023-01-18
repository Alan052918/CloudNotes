package com.jundaai.note.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.stream.Collectors;

import com.jundaai.note.form.tag.TagCreationForm;
import com.jundaai.note.form.tag.TagUpdateForm;
import com.jundaai.note.model.Tag;
import com.jundaai.note.model.assembler.TagModelAssembler;
import com.jundaai.note.service.TagService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;


@WebMvcTest(TagController.class)
public class TagControllerTest extends ControllerTest {

    @MockBean
    private TagService mockTagService;
    @MockBean
    private TagModelAssembler mockTagModelAssembler;

    @Override
    @BeforeEach
    void setUp() {
        super.setUp();
        JacksonTester.initFields(this, mapper);
        mockMvc = MockMvcBuilders.standaloneSetup(new TagController(mockTagService, mockTagModelAssembler))
                .build();
    }

    @Test
    public void getAllTags_200Ok() throws Exception {
        // given
        CollectionModel<EntityModel<Tag>> collectionModel = mockTags.stream()
                .map(tag -> EntityModel.of(tag,
                        linkTo(methodOn(TagController.class).getTagById(tag.getId())).withSelfRel()))
                .collect(Collectors.collectingAndThen(Collectors.toList(), CollectionModel::of));

        // when, then
        when(mockTagService.getAllTags()).thenReturn(mockTags);
        when(mockTagModelAssembler.toCollectionModel(mockTags)).thenReturn(collectionModel);
        mockMvc.perform(get(BASE_PATH + TAG_PATH).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].id").value(4))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].name").value("Google"));

        verify(mockTagService).getAllTags();
        verify(mockTagModelAssembler).toCollectionModel(mockTags);
    }

    @Test
    public void getAllTagsByNoteId_200Ok() throws Exception {
        // given
        Long testId = mockNoteIds.get(0);
        CollectionModel<EntityModel<Tag>> collectionModel = mockTags.stream()
                .map(tag -> EntityModel.of(tag,
                        linkTo(methodOn(TagController.class).getTagById(tag.getId())).withSelfRel()))
                .collect(Collectors.collectingAndThen(Collectors.toList(), CollectionModel::of));

        // when, then
        when(mockTagService.getAllTagsByNoteId(testId)).thenReturn(mockTags);
        when(mockTagModelAssembler.toCollectionModel(mockTags)).thenReturn(collectionModel);
        mockMvc.perform(get(BASE_PATH + NOTE_PATH + "/" + testId + "/tags")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].id").value(4))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].name").value("Google"));

        verify(mockTagService).getAllTagsByNoteId(testId);
        verify(mockTagModelAssembler).toCollectionModel(mockTags);
    }

    @Test
    public void getTagById_200Ok() throws Exception {
        // given
        Long testId = mockTagIds.get(0);
        Tag testTag = mockTags.get(0);
        EntityModel<Tag> entityModel = EntityModel.of(testTag,
                linkTo(methodOn(TagController.class).getTagById(testId)).withSelfRel());

        // when, then
        when(mockTagService.getTagById(testId)).thenReturn(testTag);
        when(mockTagModelAssembler.toModel(testTag)).thenReturn(entityModel);
        mockMvc.perform(get(BASE_PATH + TAG_PATH + "/" + testId).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(5))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(4))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Google"));

        verify(mockTagService).getTagById(testId);
        verify(mockTagModelAssembler).toModel(testTag);
    }

    @Test
    public void createTag_201Created() throws Exception {
        // given
        Long newId = 100L;
        ZonedDateTime now = ZonedDateTime.now();
        Tag newTag = Tag.builder()
                .id(newId)
                .name("New Tag")
                .createdAt(now)
                .updatedAt(now)
                .notes(new ArrayList<>())
                .build();
        EntityModel<Tag> entityModel = EntityModel.of(newTag,
                linkTo(methodOn(TagController.class).getTagById(newId)).withSelfRel());
        String requestBody = mapper.writeValueAsString(new TagCreationForm("New Tag"));

        // when, then
        when(mockTagService.createTag(any(TagCreationForm.class))).thenReturn(newTag);
        when(mockTagModelAssembler.toModel(newTag)).thenReturn(entityModel);
        mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH + TAG_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(5))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(100))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("New Tag"));

        verify(mockTagService).createTag(any(TagCreationForm.class));
        verify(mockTagModelAssembler).toModel(newTag);
    }

    @Test
    public void updateTag_200Ok() throws Exception {
        // given
        Tag testTag = mockTags.get(0);

        assertEquals("Google", testTag.getName());

        Long testId = testTag.getId();
        ZonedDateTime now = ZonedDateTime.now();
        testTag.setName("New Name");
        testTag.setUpdatedAt(now);
        EntityModel<Tag> entityModel = EntityModel.of(testTag,
                linkTo(methodOn(TagController.class).getTagById(testId)).withSelfRel());
        String requestBody = mapper.writeValueAsString(new TagUpdateForm("New Name"));

        // when, then
        when(mockTagService.updateTagById(eq(testId), any(TagUpdateForm.class))).thenReturn(testTag);
        when(mockTagModelAssembler.toModel(testTag)).thenReturn(entityModel);
        mockMvc.perform(MockMvcRequestBuilders.patch(BASE_PATH + TAG_PATH + "/" + testId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(5))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(4))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("New Name"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.updatedAt").value(now.toEpochSecond()));

        verify(mockTagService).updateTagById(eq(testId), any(TagUpdateForm.class));
        verify(mockTagModelAssembler).toModel(testTag);
    }

    @Test
    public void deleteTagById_204NoContent() throws Exception {
        // given
        Long testId = mockTagIds.get(0);

        // when, then
        mockMvc.perform(MockMvcRequestBuilders.delete(BASE_PATH + TAG_PATH + "/" + testId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        verify(mockTagService).deleteTagById(testId);
    }
}

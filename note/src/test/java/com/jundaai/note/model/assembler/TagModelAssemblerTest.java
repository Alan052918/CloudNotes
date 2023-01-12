package com.jundaai.note.model.assembler;

import com.jundaai.note.controller.TagController;
import com.jundaai.note.model.Tag;
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
public class TagModelAssemblerTest {

    @Autowired
    private TagModelAssembler testModelAssembler;

    @Test
    public void toModel_Success() {
        // given
        ZonedDateTime now = ZonedDateTime.now();
        Tag tag = Tag
                .builder()
                .id(1L)
                .name("Tag")
                .createdAt(now)
                .updatedAt(now)
                .notes(new ArrayList<>())
                .build();
        EntityModel<Tag> expectedModel = EntityModel.of(
                tag,
                linkTo(methodOn(TagController.class).getTagById(tag.getId())).withSelfRel(),
                linkTo(methodOn(TagController.class).getAllTags()).withRel("all tags"));

        // when
        EntityModel<Tag> gotModel = testModelAssembler.toModel(tag);

        // then
        assertEquals(expectedModel, gotModel);
    }

    @Test
    public void toCollectionModel_Success() {
        // given
        ZonedDateTime now = ZonedDateTime.now();
        Tag tag1 = Tag
                .builder()
                .id(1L)
                .name("Tag")
                .createdAt(now)
                .updatedAt(now)
                .notes(new ArrayList<>())
                .build();
        Tag tag2 = Tag
                .builder()
                .id(2L)
                .name("Another tag")
                .createdAt(now)
                .updatedAt(now)
                .notes(new ArrayList<>())
                .build();
        List<Tag> notes = List.of(tag1, tag2);
        CollectionModel<EntityModel<Tag>> expectedModel = notes
                .stream()
                .map(tag -> EntityModel.of(
                        tag,
                        linkTo(methodOn(TagController.class).getTagById(tag.getId())).withSelfRel(),
                        linkTo(methodOn(TagController.class).getAllTags()).withRel("all tags")))
                .collect(Collectors.collectingAndThen(Collectors.toList(), CollectionModel::of));

        // when
        CollectionModel<EntityModel<Tag>> gotModel = testModelAssembler.toCollectionModel(notes);

        // then
        assertEquals(expectedModel, gotModel);
    }

}

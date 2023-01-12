package com.jundaai.note.model.assembler;

import com.jundaai.note.controller.TagController;
import com.jundaai.note.model.Tag;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class TagModelAssembler implements RepresentationModelAssembler<Tag, EntityModel<Tag>> {

    @Override
    public EntityModel<Tag> toModel(Tag entity) {
        return EntityModel.of(
                entity,
                linkTo(methodOn(TagController.class).getTagById(entity.getId())).withSelfRel(),
                linkTo(methodOn(TagController.class).getAllTags()).withRel("all tags"));
    }

    @Override
    public CollectionModel<EntityModel<Tag>> toCollectionModel(Iterable<? extends Tag> entities) {
        return RepresentationModelAssembler.super.toCollectionModel(entities);
    }

}

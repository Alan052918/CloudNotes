package com.jundaai.note.model.assembler;

import com.jundaai.note.model.Tag;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class TagModelAssembler implements RepresentationModelAssembler<Tag, EntityModel<Tag>> {

    @Override
    public EntityModel<Tag> toModel(Tag entity) {
        return null;
    }

    @Override
    public CollectionModel<EntityModel<Tag>> toCollectionModel(Iterable<? extends Tag> entities) {
        return RepresentationModelAssembler.super.toCollectionModel(entities);
    }

}

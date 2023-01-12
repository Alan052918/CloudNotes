package com.jundaai.note.model.assembler;

import com.jundaai.note.controller.FolderController;
import com.jundaai.note.model.Folder;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class FolderModelAssembler implements RepresentationModelAssembler<Folder, EntityModel<Folder>> {

    @Override
    public EntityModel<Folder> toModel(Folder entity) {
        EntityModel<Folder> entityModel = EntityModel.of(
                entity,
                linkTo(methodOn(FolderController.class).getFolderById(entity.getId())).withSelfRel());
        if (entity.getParentFolder() != null) {
            entityModel.add(
                    linkTo(methodOn(FolderController.class).getFolderById(entity.getParentFolder().getId()))
                            .withRel("parent"));
        }
        entityModel.add(linkTo(methodOn(FolderController.class).getAllFolders()).withRel("all folders"));
        return entityModel;
    }

    @Override
    public CollectionModel<EntityModel<Folder>> toCollectionModel(Iterable<? extends Folder> entities) {
        return RepresentationModelAssembler.super.toCollectionModel(entities);
    }

}

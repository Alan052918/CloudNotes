package com.jundaai.note.model.assembler;

import com.jundaai.note.controller.FolderController;
import com.jundaai.note.controller.NoteController;
import com.jundaai.note.model.Note;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class NoteModelAssembler implements RepresentationModelAssembler<Note, EntityModel<Note>> {

    @Override
    public EntityModel<Note> toModel(Note entity) {
        return EntityModel.of(entity,
                linkTo(methodOn(NoteController.class).getNoteById(entity.getId())).withSelfRel(),
                linkTo(methodOn(FolderController.class).getFolderById(entity.getFolder().getId())).withRel("folder"),
                linkTo(methodOn(NoteController.class).getAllNotes()).withRel("all notes"));
    }

    @Override
    public CollectionModel<EntityModel<Note>> toCollectionModel(Iterable<? extends Note> entities) {
        return RepresentationModelAssembler.super.toCollectionModel(entities);
    }

}

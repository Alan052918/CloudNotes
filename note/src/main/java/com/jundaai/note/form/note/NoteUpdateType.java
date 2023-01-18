package com.jundaai.note.form.note;

public enum NoteUpdateType {

    RENAME_NOTE("RENAME_NOTE"),
    MODIFY_CONTENT("MODIFY_CONTENT"),
    MOVE_NOTE("MOVE_NOTE"),
    ADD_TAG("ADD_TAG"),
    REMOVE_TAG("REMOVE_TAG");

    public final String label;

    NoteUpdateType(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}

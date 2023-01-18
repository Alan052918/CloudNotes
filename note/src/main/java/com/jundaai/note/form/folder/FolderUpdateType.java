package com.jundaai.note.form.folder;

public enum FolderUpdateType {
    RENAME_FOLDER("RENAME_FOLDER"),
    MOVE_FOLDER("MOVE_FOLDER");

    public final String label;

    FolderUpdateType(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}

# API

- [API](#api)
  - [api/v1/folders](#apiv1folders)
    - [GET request](#get-request)
  - [api/v1/folders/{folderId}](#apiv1foldersfolderid)
    - [GET request](#get-request-1)
    - [PATCH request](#patch-request)
    - [DELETE request](#delete-request)
  - [api/v1/folders/{folderId}/subFolders](#apiv1foldersfolderidsubfolders)
    - [GET request](#get-request-2)
    - [POST request](#post-request)
  - [api/v1/folders/{folderId}/notes](#apiv1foldersfolderidnotes)
    - [GET request](#get-request-3)
    - [POST request](#post-request-1)
  - [api/v1/notes](#apiv1notes)
    - [GET request](#get-request-4)
  - [api/v1/notes/{noteId}](#apiv1notesnoteid)
    - [GET request](#get-request-5)
    - [PATCH request](#patch-request-1)
    - [DELETE request](#delete-request-1)
  - [api/v1/notes/{noteId}/tags](#apiv1notesnoteidtags)
    - [GET request](#get-request-6)
  - [api/v1/tags](#apiv1tags)
    - [GET request](#get-request-7)
    - [POST request](#post-request-2)
  - [api/v1/tags/{tagId}](#apiv1tagstagid)
    - [GET request](#get-request-8)
    - [PATCH request](#patch-request-2)
    - [DELETE request](#delete-request-2)
  - [api/v1/tags/{tagId}/notes](#apiv1tagstagidnotes)
    - [GET request](#get-request-9)

## api/v1/folders

### GET request

- Description: get all folders
- Success status code: `200 OK`

## api/v1/folders/{folderId}

### GET request

- Description: get the folder by id 'folderId'
- Success status code: `200 OK`
- Exceptions:
  - [FolderNotFoundException](#foldernotfoundexception)

### PATCH request

- Description: update the folder by id 'folderId'
  - Rename folder
  - Move to another parent folder
- Success status code: `200 OK`
- Exceptions:
  - [FolderNotFoundException](#foldernotfoundexception)
  - [FolderNameConflictException](#foldernameconflictexception)

Example payload:

```json
{
  "updateType": "RENAME_FOLDER",
  "newName": "Python"
}
```

```json
{
  "updateType": "MOVE_FOLDER",
  "toParentId": 2
}
```

### DELETE request

- Description: delete the folder by id 'folderId'
- Success status code: `204 NO CONTENT`
- Exceptions:
  - [FolderNotFoundException](#foldernotfoundexception)
  - [RootDeletionException](#rootdeletionexception)

## api/v1/folders/{folderId}/subFolders

### GET request

- Description: get all sub-folders under the folder by id 'folderId'
- Success status code: `200 OK`
- Exceptions:
  - [FolderNotFoundException](#foldernotfoundexception)

### POST request

- Description: Create a new folder under the parent folder by id 'folderId'
- Success status code: `201 CREATED`
- Exceptions:
  - [FolderNotFoundException](#foldernotfoundexception)
  - [FolderNameConflictException](#foldernameconflictexception)

Example payload:

```json
{
  "name": "c++"
}
```

## api/v1/folders/{folderId}/notes

### GET request

- Description: get all notes under the folder by id 'folderId'
- Success status code: `200 OK`
- Exceptions:
  - [FolderNotFoundException](#foldernotfoundexception)

### POST request

- Description: Create a note under the folder by id 'folderId'
- Success status code: `201 CREATED`
- Exceptions:
  - [FolderNotFoundException](#foldernotfoundexception)
  - [NoteNameConflictException](#notenameconflictexception)

Example payload:

```json
{
  "name": "spring"
}
```

## api/v1/notes

### GET request

- Description: get all notes
- Success status code: `200 OK`

## api/v1/notes/{noteId}

### GET request

- Description: get the note by id 'noteId'
- Success status code: `200 OK`
- Exceptions:
  - [NoteNotFoundException](#notenotfoundexception)

### PATCH request

- Description: update the note by id 'noteId'
  - Rename note
  - Modify note content
  - Move to another folder
  - Add a tag
  - Delete a tag
- Success status code: `200 OK`
- Exceptions:
  - [NoteNameConflictException](#notenameconflictexception)
  - [NoteNotFoundException](#notenotfoundexception)
  - [FolderNotFoundException](#foldernotfoundexception)
  - [TagNotFoundException](#tagnotfoundexception)

Example payload:

```json
{
  "updateType": "RENAME_NOTE",
  "newName": "lalaland"
}
```

```json
{
  "updateType": "MODIFY_CONTENT",
  "newContent": "Hello, World!"
}
```

```json
{
  "updateType": "MOVE_NOTE",
  "toFolderId": 2
}
```

```json
{
  "updateType": "ADD_TAG",
  "tagName": "pl"
}
```

```json
{
  "updateType": "REMOVE_TAG",
  "tagName": "pl"
}
```

### DELETE request

- Description: delete the note by id 'noteId'
- Success status code: `204 NO CONTENT`
- Exceptions:
  - [NoteNotFoundException](#notenotfoundexception)

## api/v1/notes/{noteId}/tags

### GET request

- Description: get all tags attached to the note by id 'noteId'
- Success status code: `200 OK`

## api/v1/tags

### GET request

- Description: get all tags
- Success status code: `200 OK`

### POST request

- Description: create a new tag
- Success status code: `201 CREATED`
- Exceptions:
  - [TagNameConflictException](#tagnameconflictexception)

Example payload:

```json
{
  "name": "pl"
}
```

## api/v1/tags/{tagId}

### GET request

- Description: get the tag by id 'tagId'
- Success status code: `200 OK`
- Exceptions:
  - [TagNotFoundException](#tagnotfoundexception)

### PATCH request

- Description: update the tag by id 'tagId'
  - Rename tag
- Success status code: `200 OK`
- Exceptions:
  - [TagNameConflictException](#tagnameconflictexception)
  - [TagNotFoundException](#tagnotfoundexception)

Example payload:

```json
{
  "newName": "ProgrammingLanguage"
}
```

### DELETE request

- Description: delete the tag by id 'tagId'
- Success status code: `204 NO CONTENT`
- Exceptions:
  - [TagNotFoundException](#tagnotfoundexception)

## api/v1/tags/{tagId}/notes

### GET request

- Description: get all notes by tag id 'tagId'
- Succcess status code: `200 OK`

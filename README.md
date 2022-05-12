# CloudNotes

A REST API for note storage built with Spring Boot.

- [CloudNotes](#cloudnotes)
  - [Model](#model)
  - [Custom Exceptions](#custom-exceptions)
  - [API](#api)
    - [/api/v1/folders](#apiv1folders)
    - [api/v1/folders/{folderId}](#apiv1foldersfolderid)
    - [api/v1/folders/{folderId}/subFolders](#apiv1foldersfolderidsubfolders)
    - [api/v1/folders/{folderId}/notes](#apiv1foldersfolderidnotes)
    - [api/v1/notes](#apiv1notes)
    - [api/v1/notes/{noteId}](#apiv1notesnoteid)

## Model

- Folder: A folder can have multiple sub-folders and multiple notes
  - id: `Long`
  - name: `String`
  - createdAt: `ZonedDateTime`
  - updatedAt: `ZonedDateTime`
  - parentFolder: `Folder`
  - subFolders: `List<Folder>`
  - notes: `List<Note>`
- Note: A note belongs to only one folder
  - id: `Long`
  - name: `String`
  - content: `String`
  - createdAt: `ZonedDateTime`
  - updatedAt: `ZonedDateTime`
  - folder: `Folder`

## Custom Exceptions

- FolderNameConflictException
  - Description: disallow two sub-folders under the same parent folder share the same name
  - Response status code: `409 CONFLICT`
- FolderNotFoundException
  - Description: no folder by the given id found in database
  - Response status code: `404 NOT FOUND`
- NoteNameConflictException
  - Description: disallow two notes under the same folder share the same name
  - Response status code: `409 CONFLICT`
- NoteNotFoundException
  - Description: no note by the given id found in database
  - Response status code: `404 NOT FOUND`
- RootDeletionException
  - Description: disallow deleting the root folder (named 'root', ancester of all folders and notes)
  - Response status code: `400 BAD REQUEST`

## API

### /api/v1/folders

- GET request
  - Description: get all folders
  - Success status code: `200 OK`

### api/v1/folders/{folderId}

- GET request
  - Description: get the folder by id 'folderId'
  - Success status code: `200 OK`
  - Exceptions:
    - FolderNotFoundException
- PATCH request
  - Description: update the folder by id 'folderId'
    - Rename folder
    - Move to another parent folder
  - Success status code: `200 OK`
  - Exceptions:
    - FolderNotFoundException
    - FolderNameConflictException
- DELETE request
  - Description: delete the folder by id 'folderId'
  - Success status code: `204 NO CONTENT`
  - Exceptions:
    - FolderNotFoundException
    - RootDeletionException

### api/v1/folders/{folderId}/subFolders

- GET request
  - Description: get all sub-folders under the folder by id 'folderId'
  - Success status code: `200 OK`
  - Exceptions:
    - FolderNotFoundException
- POST request
  - Description: Create a new folder under the parent folder by id 'folderId'
  - Success status code: `201 CREATED`
  - Exceptions:
    - FolderNotFoundException
    - FolderNameConflictException

### api/v1/folders/{folderId}/notes

- GET request
  - Description: get all notes under the folder by id 'folderId'
  - Success status code: `200 OK`
  - Exceptions:
    - FolderNotFoundException
- POST request
  - Description: Create a note under the folder by id 'folderId'
  - Success status code: `201 CREATED`
  - Exceptions:
    - FolderNotFoundException
    - NoteNameConflictException

### api/v1/notes

- GET request
  - Description: get all notes
  - Success status code: `200 OK`

### api/v1/notes/{noteId}

- GET request
  - Description: get the note by id 'noteId'
  - Success status code: `200 OK`
  - Exceptions:
    - NoteNotFoundException
- PATCH request
  - Description: update the note by id 'noteId'
    - Rename note
    - Modify note content
    - Move to another folder
  - Success status code: `200 OK`
  - Exceptions:
    - NoteNotFoundException
    - FolderNotFoundException
- DELETE request
  - Description: delete the note by id 'noteId'
  - Success status code: `204 NO CONTENT`
  - Exceptions:
    - NoteNotFoundException

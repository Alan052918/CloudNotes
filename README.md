# CloudNotes

A REST API for note storage built with Spring Boot.

Key points for 9053 evaluation:

- Spring Boot
  - Spring Web MVC: controller, service, repository architecture
  - Spring Data JPA: entity, JQL query
    - OneToMany, ManyToOne, ManyToMany relations
  - Spring HATEOAS: entity model, collection model, add links to API (makes it RESTful)
  - Spring Validation: validate request forms
- Maven
  - Maven multi-module project: dependency management, plugin management
  - Build and run Spring Boot Application
- MySQL
- Docker Compose
- API design
- Exception handling

## Table of Contents

- [CloudNotes](#cloudnotes)
  - [Table of Contents](#table-of-contents)
  - [Getting Started](#getting-started)
  - [Model](#model)
  - [Custom Exceptions](#custom-exceptions)
    - [BadRequestException](#badrequestexception)
    - [FolderNameConflictException](#foldernameconflictexception)
    - [FolderNotFoundException](#foldernotfoundexception)
    - [NoteNameConflictException](#notenameconflictexception)
    - [NoteNotFoundException](#notenotfoundexception)
    - [RootPreservationException](#rootpreservationexception)
    - [TagNameConflictException](#tagnameconflictexception)
    - [TagNotFoundException](#tagnotfoundexception)
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

## Getting Started

Environment

- Java SE 1.8
- Docker Compose 3.8
- Spring Boot 2.6.4
- MySQL 8

Clone the repository

```shell
git clone https://github.com/Alan052918/CloudNotes.git
```

Run MySQL container

```shell
cd CloudNotes
docker compose up -d
```

Run note Spring Boot Application

```shell
cd note
mvn spring-boot:run
```

- [ ] TODO: containerize note application
- [ ] TODO: add more submodules and service discovery to make this project fully-microservice.

Warning: There is a [LoadDatabase.java](note/src/main/java/com/jundaai/note/config/LoadDatabase.java) that populates the database with some folders, notes, and tags. My intention is to save you some time creating data, but please drop all tables before rerunning the application. An in-memory database like H2 that allows clean starts is preferable for testing purposes.

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
- Tag: A tag can be attached to multiple notes, a note can also have multiple tags
  - id: `Long`
  - name: `String`
  - createdAt: `ZonedDateTime`
  - updatedAt: `ZonedDateTime`

## Custom Exceptions

### BadRequestException

- Description: generic exception for handling bad requests
- Response status code: `400 BAD REQUEST`

### FolderNameConflictException

- Description: disallow two sub-folders under the same parent folder with the same name
- Response status code: `409 CONFLICT`

### FolderNotFoundException

- Description: no folder by the given id found in database
- Response status code: `404 NOT FOUND`

### NoteNameConflictException

- Description: disallow two notes under the same folder with the same name
- Response status code: `409 CONFLICT`

### NoteNotFoundException

- Description: no note by the given id found in database
- Response status code: `404 NOT FOUND`

### RootPreservationException

- Description: disallow deleting the root folder (named 'root', ancester of all folders and notes), or create a new folder named 'root'
- Response status code: `400 BAD REQUEST`

### TagNameConflictException

- Description: disallow two tags with the same name
- Response status code: `409 CONFLICT`

### TagNotFoundException

- Description: no tag by the given id found in database
- Response status code: `404 NOT FOUND`

## API

### api/v1/folders

#### GET request

- Description: get all folders
- Success status code: `200 OK`

### api/v1/folders/{folderId}

#### GET request

- Description: get the folder by id 'folderId'
- Success status code: `200 OK`
- Exceptions:
  - [FolderNotFoundException](#foldernotfoundexception)

#### PATCH request

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

#### DELETE request

- Description: delete the folder by id 'folderId'
- Success status code: `204 NO CONTENT`
- Exceptions:
  - [FolderNotFoundException](#foldernotfoundexception)
  - [RootDeletionException](#rootdeletionexception)

### api/v1/folders/{folderId}/subFolders

#### GET request

- Description: get all sub-folders under the folder by id 'folderId'
- Success status code: `200 OK`
- Exceptions:
  - [FolderNotFoundException](#foldernotfoundexception)

#### POST request

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

### api/v1/folders/{folderId}/notes

#### GET request

- Description: get all notes under the folder by id 'folderId'
- Success status code: `200 OK`
- Exceptions:
  - [FolderNotFoundException](#foldernotfoundexception)

#### POST request

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

### api/v1/notes

#### GET request

- Description: get all notes
- Success status code: `200 OK`

### api/v1/notes/{noteId}

#### GET request

- Description: get the note by id 'noteId'
- Success status code: `200 OK`
- Exceptions:
  - [NoteNotFoundException](#notenotfoundexception)

#### PATCH request

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

#### DELETE request

- Description: delete the note by id 'noteId'
- Success status code: `204 NO CONTENT`
- Exceptions:
  - [NoteNotFoundException](#notenotfoundexception)

### api/v1/notes/{noteId}/tags

#### GET request

- Description: get all tags attached to the note by id 'noteId'
- Success status code: `200 OK`

### api/v1/tags

#### GET request

- Description: get all tags
- Success status code: `200 OK`

#### POST request

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

### api/v1/tags/{tagId}

#### GET request

- Description: get the tag by id 'tagId'
- Success status code: `200 OK`
- Exceptions:
  - [TagNotFoundException](#tagnotfoundexception)

#### PATCH request

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

#### DELETE request

- Description: delete the tag by id 'tagId'
- Success status code: `204 NO CONTENT`
- Exceptions:
  - [TagNotFoundException](#tagnotfoundexception)

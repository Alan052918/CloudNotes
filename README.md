# CloudNotes

[![Java CI with Maven](https://github.com/Alan052918/CloudNotes/actions/workflows/maven.yml/badge.svg)](https://github.com/Alan052918/CloudNotes/actions/workflows/maven.yml)
![note Module Coverage](.github/badges/jacocoNote.svg)
![note Module Branches](.github/badges/branchesNote.svg)

A simple RESTful service to organize notes with folders and hashtags, built with Spring Boot.

Key points for 9053 evaluation:

- Spring Boot
  - Spring Web MVC: controller, service, repository architecture
  - Spring Data JPA: entity, JQL query
    - OneToMany, ManyToOne, ManyToMany relations
  - Spring HATEOAS: entity model, collection model, add links to API (make it RESTful)
  - Spring Validation: validate request forms
- Maven
  - Maven multi-module project: dependency management, plugin management
  - Build and run Spring Boot Application
- MySQL
- Docker Compose
- API design
- Exception handling

Service oriented architecture:

- [x] Transport: HTTP JSON
- [x] Logging
- [ ] Metrics
- [ ] Tracing
- [ ] Circuit breaking
- [ ] Rate limiting
- [ ] Audit logging
- [ ] Service discovery
- [ ] Service registry
- [ ] Caching strategy
- [ ] Deploy strategy
- [ ] Contract testing
- [ ] Security
- [ ] Alerting

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
- [ ] TODO: add more submodules and service discovery to make this project fully microservice.
- [ ] TODO: add unit tests and integration tests with Spring Testing

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
  - tags: `List<Tag>`
- Tag: A tag can be attached to multiple notes, a note can also have multiple tags
  - id: `Long`
  - name: `String`
  - createdAt: `ZonedDateTime`
  - updatedAt: `ZonedDateTime`
  - notes: `List<Note>`

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

# CloudNotes

[![Java CI with Maven](https://github.com/Alan052918/CloudNotes/actions/workflows/maven.yml/badge.svg)](https://github.com/Alan052918/CloudNotes/actions/workflows/maven.yml)
[![Coverage](.github/badges/jacocoNote.svg)](https://github.com/Alan052918/CloudNotes/actions/workflows/build.yml)
[![Coverage](.github/badges/branchesNote.svg)](https://github.com/Alan052918/CloudNotes/actions/workflows/build.yml)

A simple RESTful service to organize notes with folders and hashtags, built with Spring Boot.

- Spring Boot
  - Spring Web MVC: controller, service, repository architecture
  - Spring Data JPA: entity, JQL query
    - OneToMany, ManyToOne, ManyToMany relations
  - Spring HATEOAS: entity model, collection model, add links to API (make it RESTful)
  - Spring Validation: validate request forms
  - Spring Testing, JUnit 5, Mockito: controller tests, service tests, repository tests
- Maven
  - Maven multi-module project: dependency management, plugin management
  - Build and run Spring Boot Application
- MySQL
- Docker Compose
- API design
- Exception handling
- Unit/integration testing

## Table of Contents

- [CloudNotes](#cloudnotes)
  - [Table of Contents](#table-of-contents)
  - [Getting Started](#getting-started)
  - [Model](#model)
  - [Next Steps](#next-steps)

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

## Next Steps

Service oriented architecture:

- [x] Transport: HTTP JSON
- [x] Logging
- [ ] Audit logging
- [ ] Security
- [ ] Metrics
- [ ] Tracing
- [ ] Circuit breaking
- [ ] Rate limiting
- [ ] Service discovery
- [ ] Service registry
- [ ] Caching strategy
- [ ] Deploy strategy
- [ ] Contract testing
- [ ] Alerting

Miscellaneous:

- [x] Add unit tests and integration tests with Spring Testing
- [ ] Containerize note application
- [ ] Add more submodules and service discovery to make this project fully microservice.

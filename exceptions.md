# Custom Exceptions

## BadRequestException

- Description: generic exception for handling bad requests
- Response status code: `400 BAD REQUEST`

## FolderNameConflictException

- Description: disallow two sub-folders under the same parent folder with the same name
- Response status code: `409 CONFLICT`

## FolderNotFoundException

- Description: no folder by the given id found in database
- Response status code: `404 NOT FOUND`

## NoteNameConflictException

- Description: disallow two notes under the same folder with the same name
- Response status code: `409 CONFLICT`

## NoteNotFoundException

- Description: no note by the given id found in database
- Response status code: `404 NOT FOUND`

## RootPreservationException

- Description: disallow deleting the root folder (named 'root', ancester of all folders and notes), or create a new folder named 'root'
- Response status code: `400 BAD REQUEST`

## TagNameConflictException

- Description: disallow two tags with the same name
- Response status code: `409 CONFLICT`

## TagNotFoundException

- Description: no tag by the given id found in database
- Response status code: `404 NOT FOUND`

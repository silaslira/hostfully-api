# Hostfully API

## Table of Contents

- [Overview](#overview)
- [Getting Started](#getting-started)
- [API Endpoints](#api-endpoints)
    - [Reservation](#reservation)
    - [Property](#property)
    - [Block](#block)

## Overview

This API allows users to manage reservations, properties, and blocks. Below are the available operations and endpoints.

## Useful Links
### [Swagger UI](http://localhost:8080/swagger-ui/index.html)
### [Postman Collection](/src/main/resources/static/Hostfully.postman_collection.json)

## Getting Started

1. Build the project:
```bash
./gradlew clean build
```

2. Run the project:
```bash
./gradlew bootRun
```

## Format Code
```bash
./gradlew spotlessJavaApply
```

## API Endpoints

### Reservation

#### `GET /reservation/{reservationId}`

- **Summary:** Find reservation by id
- **Operation ID:** findById
- **Parameters:**
    - `reservationId` (path, required): Id of reservation to be searched
- **Responses:**
    - 404: Reservation not found
    - 200: Reservation found (Schema: ReservationDto)

#### `PUT /reservation/{reservationId}`

- **Summary:** Update reservation
- **Operation ID:** update
- **Parameters:**
    - `reservationId` (path, required): Id of reservation to be updated
- **Request Body:**
    - Content Type: application/json
    - Schema: PersistReservationDto
- **Responses:**
    - 400: Invalid reservation passed to persist
    - 201: Reservation updated (Schema: ReservationDto)

#### `PUT /reservation/{reservationId}/rebook`

- **Summary:** Rebook reservation
- **Operation ID:** rebook
- **Parameters:**
    - `reservationId` (path, required): Id of reservation to be rebooked
- **Responses:**
    - 201: Reservation rebooked (Schema: ReservationDto)
    - 204: No Content

#### `PUT /reservation/{reservationId}/cancel`

- **Summary:** Cancel reservation
- **Operation ID:** cancel
- **Parameters:**
    - `reservationId` (path, required): Id of reservation to be cancelled
- **Responses:**
    - 201: Reservation cancelled (Schema: ReservationDto)
    - 204: No Content

#### `POST /reservation`

- **Summary:** Create reservation
- **Operation ID:** create
- **Request Body:**
    - Content Type: application/json
    - Schema: PersistReservationDto
- **Responses:**
    - 400: Invalid reservation passed to persist
    - 201: Reservation created (Schema: ReservationDto)

### Property

#### `GET /property/{propertyId}`

- **Summary:** Find property by id
- **Operation ID:** findById_1
- **Parameters:**
    - `propertyId` (path, required): Id of property to be searched
- **Responses:**
    - 200: Property found (Schema: PropertyDto)
    - 404: Property not found

#### `PUT /property/{propertyId}`

- **Summary:** Update property
- **Operation ID:** update_1
- **Parameters:**
    - `propertyId` (path, required): Id of property to be updated
- **Request Body:**
    - Content Type: application/json
    - Schema: PersistPropertyDto
- **Responses:**
    - 400: Invalid property passed to persist
    - 201: Property updated (Schema: PropertyDto)

#### `DELETE /property/{propertyId}`

- **Summary:** Delete property
- **Operation ID:** delete
- **Parameters:**
    - `propertyId` (path, required): Id of property to be deleted
- **Responses:**
    - 201: Property deleted (Schema: PropertyDto)
    - 204: No Content

#### `GET /property`

- **Summary:** Get all properties available
- **Operation ID:** findAll
- **Responses:**
    - 200: Properties retrieved successfully (Schema: PropertyDto)

#### `POST /property`

- **Summary:** Create property
- **Operation ID:** create_1
- **Request Body:**
    - Content Type: application/json
    - Schema: PersistPropertyDto
- **Responses:**
    - 400: Invalid property passed to persist
    - 201: Property created (Schema: PropertyDto)

### Block

#### `GET /block/{blockId}`

- **Summary:** Find block by id
- **Operation ID:** findById_2
- **Parameters:**
    - `blockId` (path, required): Id of block to be searched
- **Responses:**
    - 200: Block found (Schema: BlockDto)
    - 404: Block not found

#### `PUT /block/{blockId}`

- **Summary:** Update block
- **Operation ID:** update_2
- **Parameters:**
    - `blockId` (path, required): Id of block to be updated
- **Request Body:**
    - Content Type: application/json
    - Schema: PersistBlockDto
- **Responses:**
    - 201: Block updated (Schema: BlockDto)
    - 400: Invalid block passed to persist

#### `DELETE /block/{blockId}`

- **Summary:** Delete block
- **Operation ID:** delete_1
- **Parameters:**
    - `blockId` (path, required): Id of block to be deleted
- **Responses:**
    - 201: Block deleted (Schema: BlockDto)
    - 204: No Content

#### `POST /block`

- **Summary:** Create block
- **Operation ID:** create_2
- **Request Body:**
    - Content Type: application/json
    - Schema: PersistBlockDto
- **Responses:**
    - 201: Block created (Schema: BlockDto)
    - 400: Invalid block passed to persist


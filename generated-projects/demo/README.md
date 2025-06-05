# my-api

An e-bookstore management system built with Spring Boot 3.1.5. The application handles books, authors, and user reviews to enable efficient e-bookstore operations.

## Table of Contents

- [Features](#features)
- [Technology Stack](#technology-stack)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Configuration](#configuration)
  - [Running the Application](#running-the-application)
- [Entities](#entities)
- [API Endpoints](#api-endpoints)
- [Testing](#testing)
- [License](#license)

---

## Features

- Manage books and authors.
- User registration and authentication.
- Users can post reviews for books.
- RESTful API design.
- PostgreSQL integration.
- Input validation.
- Containerized testing using Testcontainers.

---

## Technology Stack

- **Java:** 17
- **Spring Boot:** 3.1.5
- **Dependencies:**
  - Spring Web
  - Spring Data JPA
  - Lombok
  - Validation (Jakarta Bean Validation)
  - PostgreSQL Driver
  - Testcontainers (for integration testing)

---

## Getting Started

### Prerequisites

- Java 17+
- Maven 3.9+
- Docker (for Testcontainers in integration testing)
- PostgreSQL running locally

### Configuration

Create or modify `src/main/resources/application.properties`:
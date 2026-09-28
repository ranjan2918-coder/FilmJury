# FilmJury — Short Film Contest Submission and Scoring

## 1. Project Overview

FilmJury is a robust Spring Boot backend system designed to manage a college short-film contest. 
The system provides a complete solution to support film entry submission, judge management, 
and the evaluation criteria. It allows judges to securely submit scores while preventing duplicate 
scorecards, automatically calculating average final scores, and dynamically generating a leaderboard 
for the contest rankings.

## 2. Problem Statement

A college short-film contest collects film entries and allows multiple judges to independently score 
those entries based on predefined criteria. The system must prevent duplicate scoring by the same 
judge for the same entry, ensure scores fall within valid bounds, and calculate the final score as 
the average of the submitted judge scores.

FilmJury replaces informal, manual scoring methods with a structured backend system to ensure 
integrity, accuracy, and fairness in the contest results.

## 3. Objectives

- Manage film entries (Create, Read, Update, Delete)
- Manage judges and their profiles
- Manage evaluation criteria with maximum score limits
- Record judge scores for specific entries and criteria
- Prevent duplicate scorecards from the same judge for the same entry and criterion
- Calculate average scores for final entry evaluations
- Display a real-time leaderboard of entries
- Validate input data rigorously
- Handle errors clearly with structured API responses
- Store all contest data securely in a MySQL database

## 4. Features

- **Entry CRUD:** Full management of film entries including title, genre, and video link.
- **Judge CRUD:** Management of judges with unique email constraints.
- **Criterion CRUD:** Management of scoring criteria with dynamically defined maximum scores.
- **ScoreCard Creation:** Secure submission of scores mapping a judge, an entry, and a criterion.
- **Score Validation:** Ensures submitted scores do not exceed the maximum allowed by the criterion.
- **Duplicate Score Prevention:** Database-level and application-level checks to prevent multiple scorecards for the exact same judge, entry, and criterion.
- **Average Score Calculation:** Aggregates all valid scorecards for an entry to compute a final, averaged score.
- **Leaderboard:** Dynamic ranking system for all entries based on their final calculated scores.
- **REST APIs:** Comprehensive API endpoints for full system integration.
- **MySQL Persistence:** Data stored using Spring Data JPA and Hibernate.
- **Global Exception Handling:** Centralized error management returning clean HTTP status codes and JSON messages.
- **Validation:** Integration of Jakarta Bean Validation for robust request data checking.
- **Static Frontend:** A complete Single Page Application (HTML/CSS/JS) to interact with the backend APIs.
- **Data Seeder:** Automatically populates the database with sample entries, judges, criteria, and scorecards upon startup.

## 5. Technology Stack

| Technology | Purpose |
|---|---|
| Java 21 | Backend programming language |
| Spring Boot | Application framework |
| Spring Web | REST APIs |
| Spring Data JPA | Database access and abstraction |
| Hibernate | Object-Relational Mapping (ORM) |
| MySQL | Relational Database |
| Maven | Build and dependency management |
| HTML/CSS/JavaScript | Frontend Single Page Application |
| Postman | API testing |

## 6. System Architecture

Frontend (HTML/JS)
↓
REST Controller (Handles HTTP requests and responses)
↓
Service Layer (Executes business logic and rule validation)
↓
Repository Layer (Spring Data JPA interfaces for DB operations)
↓
MySQL Database (Persistent storage of contest data)

- **Frontend:** Provides a graphical interface for users and consumes the REST APIs.
- **REST Controller:** Acts as the entry point for API calls, validates incoming requests, and delegates tasks.
- **Service Layer:** Contains the core business logic, such as calculating averages and preventing duplicate scorecards.
- **Repository Layer:** Handles database queries and persistence using Hibernate.
- **MySQL Database:** Stores the relational data with constraints for data integrity.

## 7. Project Structure

```text
src/
├── main/
│   ├── java/
│   │   └── com/filmjury/
│   │       ├── config/
│   │       │   └── DataSeeder.java
│   │       ├── controller/
│   │       │   ├── CriterionController.java
│   │       │   ├── EntryController.java
│   │       │   ├── JudgeController.java
│   │       │   └── ScoreCardController.java
│   │       ├── dto/
│   │       │   └── ScoreCardRequest.java
│   │       ├── entity/
│   │       │   ├── Criterion.java
│   │       │   ├── Entry.java
│   │       │   ├── Judge.java
│   │       │   └── ScoreCard.java
│   │       ├── exception/
│   │       │   ├── DuplicateScorecardException.java
│   │       │   ├── GlobalExceptionHandler.java
│   │       │   ├── InvalidScoreException.java
│   │       │   └── ResourceNotFoundException.java
│   │       ├── repository/
│   │       │   ├── CriterionRepository.java
│   │       │   ├── EntryRepository.java
│   │       │   ├── JudgeRepository.java
│   │       │   └── ScoreCardRepository.java
│   │       ├── service/
│   │       │   ├── CriterionService.java
│   │       │   ├── EntryService.java
│   │       │   ├── JudgeService.java
│   │       │   └── ScoreCardService.java
│   │       └── FilmJuryApplication.java
│   └── resources/
│       ├── static/
│       │   ├── app.js
│       │   ├── index.html
│       │   └── styles.css
│       └── application.properties
└── test/
    └── java/
        └── com/filmjury/
            └── FilmJuryApplicationTests.java
```

## 8. Core Entities

### Entry
Stores information about the submitted short films.
- Fields: `id`, `title`, `genre`, `videoLink`

### Judge
Stores the profile information of the individuals scoring the entries.
- Fields: `id`, `name`, `email` (Unique constraint)

### Criterion
Stores the metrics against which films are evaluated.
- Fields: `id`, `name` (Unique constraint), `description`, `maxScore`

### ScoreCard
The junction entity that maps a Judge's score to a specific Entry based on a particular Criterion.
- Fields: `id`, `score`
- Relationships: 
  - `@ManyToOne` to **Entry**
  - `@ManyToOne` to **Judge**
  - `@ManyToOne` to **Criterion**
- Includes a `@UniqueConstraint` on the combination of `(judge_id, entry_id, criterion_id)`.

## 9. Business Rules

### Rule 1 — Average Score
Final score is calculated as the average of all the scores submitted by judges for an entry.

Example:
Judge 1 = 80
Judge 2 = 90
Judge 3 = 70

Final Score = 80.0

### Rule 2 — One ScoreCard Per Judge Per Entry
A judge cannot submit more than one scorecard for the exact same entry under the same criterion. The system actively checks for existence and prevents duplicate grading.

### Rule 3 — Score Validation
A submitted score must be valid (greater than or equal to 0) and must not exceed the `maxScore` defined by the referenced Criterion.

## 10. REST API

| Method | Endpoint | Purpose |
|---|---|---|
| POST | `/api/entries` | Create a new entry |
| GET | `/api/entries` | Get all entries |
| GET | `/api/entries/{id}` | Get a specific entry |
| PUT | `/api/entries/{id}` | Update an existing entry |
| DELETE | `/api/entries/{id}` | Delete an entry (Cascades to linked ScoreCards) |
| GET | `/api/entries/{id}/score` | Get the final calculated average score for an entry |
| GET | `/api/entries/leaderboard` | Get the ranked leaderboard of all entries |
| POST | `/api/judges` | Create a new judge |
| GET | `/api/judges` | Get all judges |
| GET | `/api/judges/{id}` | Get a specific judge |
| PUT | `/api/judges/{id}` | Update an existing judge |
| DELETE | `/api/judges/{id}` | Delete a judge (Cascades to linked ScoreCards) |
| POST | `/api/criteria` | Create a new criterion |
| GET | `/api/criteria` | Get all criteria |
| GET | `/api/criteria/{id}` | Get a specific criterion |
| PUT | `/api/criteria/{id}` | Update an existing criterion |
| DELETE | `/api/criteria/{id}` | Delete a criterion (Cascades to linked ScoreCards) |
| POST | `/api/scorecards` | Submit a new scorecard |
| GET | `/api/scorecards` | Get all scorecards |
| GET | `/api/scorecards/{id}` | Get a specific scorecard |
| GET | `/api/scorecards/entry/{entryId}`| Get all scorecards for a specific entry |

## 11. Database

The application utilizes **MySQL**.

Database tables (Entities) mapped via Spring Data JPA / Hibernate:
- `entries`: Contains film submission data.
- `judges`: Contains the judge registry.
- `criteria`: Contains the rubric criteria and their maximum potential scores.
- `scorecards`: Records individual scores mapping judges to entries and criteria.

The `scorecards` table implements a database-level composite unique constraint on `(judge_id, entry_id, criterion_id)` to ensure data integrity and prevent multiple submissions per category per judge.

## 12. Validation and Exception Handling

The system implements strict validation and global exception handling:
- **Invalid input handling:** Request fields are validated via Jakarta annotations (`@NotBlank`, `@Email`, `@Positive`, etc.). Bad data triggers a `MethodArgumentNotValidException` parsed into a clean JSON response.
- **Missing resources:** Throws a custom `ResourceNotFoundException` returning a 404 HTTP status code.
- **Duplicate scorecard handling:** Submitting duplicate scorecards triggers a custom `DuplicateScorecardException` which maps to a 400 Bad Request error.
- **Invalid score bounds:** If a score is higher than the max score allowed, an `InvalidScoreException` is thrown resulting in a 400 error.
- **Global exception handling:** Handled centrally by a `@RestControllerAdvice` class (`GlobalExceptionHandler`), wrapping all errors into a standardized JSON response format. The application also contains a `DataIntegrityViolationException` handler as a safety net for relational conflicts.

## 13. How to Run the Project

### Step 1 — Requirements
- Java (JDK 21)
- Maven
- XAMPP (for MySQL database)
- IntelliJ IDEA, Eclipse, or VS Code

### Step 2 — Start XAMPP
1. Open the XAMPP Control Panel.
2. Click **Start** next to MySQL.
3. *Important: Do NOT use the Windows MySQL80 service to avoid conflicts.*
4. Make sure the MySQL port matches the configuration in `application.properties` (port 3306).

### Step 3 — Create Database
The database name used by the application is `filmjury_db`.
Since the project utilizes Hibernate with `createDatabaseIfNotExist=true` and `ddl-auto=update`, the Spring Boot application will automatically create the database schemas and required tables upon startup.

### Step 4 — Configure application.properties
Ensure your `src/main/resources/application.properties` connection properties are correct:
- `spring.datasource.url` (Requires correct DB name and port)
- `spring.datasource.username` (Typically `root`)
- `spring.datasource.password` (Must match your XAMPP installation, typically blank)

### Step 5 — Run
Open a terminal in the root directory of the project and execute:

For Windows:
```cmd
mvnw.cmd spring-boot:run
```

For Mac/Linux:
```bash
./mvnw spring-boot:run
```

## 14. Testing

Extensive testing has been carried out on the FilmJury project:
- **Maven Context Tests:** Spring context load tests (`FilmJuryApplicationTests`) to verify successful dependency injection, configurations, and database connection.
- **REST API Testing:** Comprehensive manual verification of all implemented controllers using Postman/cURL.
- **Duplicate scorecard test:** Confirmed that submitting identical `judgeId`, `entryId`, and `criterionId` combinations correctly prevents database insertion and yields a `DuplicateScorecardException`.
- **Score validation test:** Confirmed that a submitted score exceeding the criterion's maximum triggers an `InvalidScoreException`.
- **Average score test:** Confirmed that the final score correctly aggregates integer scores across multiple judges, divides correctly, and rounds to one decimal place.
- **Leaderboard test:** Confirmed that the leaderboard properly calculates final scores across multiple entries and correctly sorts them in descending order.

*Note: For the test suite to pass locally, XAMPP MySQL must be running as the context load tests depend on a successful DB connection.*

## 15. Sample API Request

### Create a new Entry
**POST** `/api/entries`
```json
{
  "title": "A Walk in the Park",
  "genre": "Documentary",
  "videoLink": "https://example.com/walk-park"
}
```

### Submit a ScoreCard
**POST** `/api/scorecards`
```json
{
  "judgeId": 1,
  "entryId": 2,
  "criterionId": 1,
  "score": 85
}
```

# Finance Manager Application

<p align="center">
  <img src="https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java 17" />
  <img src="https://img.shields.io/badge/Spring_Boot-3.5.14-brightgreen?style=for-the-badge&logo=spring&logoColor=white" alt="Spring Boot 3.5.14" />
  <img src="https://img.shields.io/badge/Database-H2%20(In--Memory)-blue?style=for-the-badge&logo=databricks&logoColor=white" alt="H2 Database" />
  <img src="https://img.shields.io/badge/Build-Passing-success?style=for-the-badge&logo=github&logoColor=white" alt="Build Status" />
  <img src="https://img.shields.io/badge/License-MIT-purple?style=for-the-badge" alt="License MIT" />
</p>

---

## Project Overview
**Finance Manager** is a backend Spring Boot application designed to manage personal finances. It allows users to track their incomes and expenses, organize transactions by categories, and set financial goals. 

The application utilizes **Spring Data JPA** for data persistence with an in-memory **H2 Database**, validated using **Jakarta Bean Validation**, and secured with **Spring Security**.

---

## Project Structure

```text
manager/
├── .mvn/
├── src/
│   ├── main/
│   │   ├── java/com/finance/manager/
│   │   │   ├── entity/             # JPA Entities
│   │   │   │   ├── User.java
│   │   │   │   ├── Category.java
│   │   │   │   ├── CategoryType.java
│   │   │   │   └── Goal.java
│   │   │   ├── repository/         # Data Repositories
│   │   │   │   ├── UserRepository.java
│   │   │   │   ├── CategoryRepository.java
│   │   │   │   ├── TransactionRepository.java
│   │   │   │   └── GoalRepository.java
│   │   │   └── ManagerApplication.java
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── static/
│   │       └── templates/
│   └── test/
│       └── java/com/finance/manager/
│           └── ManagerApplicationTests.java
├── mvnw
├── mvnw.cmd                        # Windows Batch script
└── pom.xml
```

---

## Implemented Domain Entities & Repositories

The project implements the core persistence layer consisting of the following domain elements:

### 1. User
Represents a registered user of the system.
* **Fields**:
  * `id` (Long, PK, Auto-increment)
  * `username` (String, unique, must be a valid email format, cannot be blank)
  * `password` (String, cannot be blank)
  * `fullName` (String, cannot be blank)
  * `phoneNumber` (String, cannot be blank)
  * `categories` (OneToMany relationship with `Category` entity)
* **Repository**: [`UserRepository`](file:///c:/Users/DELL/Projects/manager/src/main/java/com/finance/manager/repository/UserRepository.java)
  * Supports `findByUsername(String username)`
  * Supports `existsByUsername(String username)`

### 2. Category
Defines categories for classification of transactions.
* **Fields**:
  * `id` (Long, PK, Auto-increment)
  * `name` (String, cannot be blank)
  * `type` (Enum `CategoryType`: `INCOME`, `EXPENSE`)
  * `isCustom` (boolean, indicates a user-defined category)
  * `user` (ManyToOne relationship with `User`)
* **Constraints**: A unique index on `(name, type)` ensures categories are distinct.
* **Repository**: [`CategoryRepository`](file:///c:/Users/DELL/Projects/manager/src/main/java/com/finance/manager/repository/CategoryRepository.java)
  * Supports `findByUser(User user)`
  * Supports `findByNameAndUser(String name, User user)`

### 3. Transaction
Represents a financial record of income or expense.
* **Fields**:
  * `id` (Long, PK, Auto-increment)
  * `amount` (BigDecimal, must be positive, cannot be null)
  * `date` (LocalDate, cannot be null, must be in the past or present)
  * `category` (ManyToOne relationship with `Category`)
  * `description` (String)
  * `user` (ManyToOne relationship with `User`)
* **Repository**: [`TransactionRepository`](file:///c:/Users/DELL/Projects/manager/src/main/java/com/finance/manager/repository/TransactionRepository.java)
  * Supports `findByUserOrderByDateDesc(User user)` (fetches latest transactions first)

### 4. Goal
Enables users to define and track saving/spending targets.
* **Fields**:
  * `id` (Long, PK, Auto-increment)
  * `goalName` (String, cannot be blank)
  * `targetAmount` (BigDecimal, must be positive, cannot be null)
  * `targetDate` (LocalDate, must be in the future, cannot be null)
  * `startDate` (LocalDate, automatically defaults to today upon persistence via `@PrePersist`)
  * `user` (ManyToOne relationship with `User`)
* **Repository**: [`GoalRepository`](file:///c:/Users/DELL/Projects/manager/src/main/java/com/finance/manager/repository/GoalRepository.java)
  * Supports `findByUser(User user)`

---

## Configuration & Database
* **Database**: Uses an in-memory **H2 Database** configured in [`application.properties`](file:///c:/Users/DELL/Projects/manager/src/main/resources/application.properties).
* **Console**: H2 Console is enabled and can be accessed at:
  * **Path**: `http://localhost:8000/h2-console`
  * **JDBC URL**: `jdbc:h2:mem:financedb`
  * **Username**: `sa`
  * **Password**: `password`

---

## Execution & Command Batches

You can build, test, and run the project using Maven Wrapper commands:

### Using Batch Script (Windows CMD / PowerShell)
Run the project using the Windows batch command script `mvnw.cmd` in the root directory:
```cmd
# Clean & compile project
.\mvnw.cmd clean compile

# Run tests
.\mvnw.cmd test

# Start the Spring Boot application
.\mvnw.cmd spring-boot:run
```

### Using Shell Script (Linux / macOS Terminal)
Run the shell commands:
```bash
# Clean & compile project
./mvnw clean compile

# Run tests
./mvnw test

# Start the Spring Boot application
./mvnw spring-boot:run
```

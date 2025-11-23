# Buggy Spring Boot Application for Static Analysis Testing

This is an intentionally defective Spring Boot application created to test static code analysis tools like SonarQube, Checkstyle, SpotBugs, PMD, and other code quality tools.

## Overview

This project contains a fully runnable Spring Boot application with numerous intentional defects across different categories including:
- Security vulnerabilities
- Code smells
- Performance issues
- Maintainability problems
- Bug-prone patterns

## Project Structure

```
src/main/java/com/example/buggyapp/
├── BuggyApplication.java         # Main application class
├── controller/
│   ├── UserController.java       # REST endpoints with security vulnerabilities
│   └── ProductController.java    # REST endpoints with various defects
├── service/
│   ├── UserService.java          # Business logic with code smells
│   └── ProductService.java       # Service with performance issues
├── repository/
│   ├── UserRepository.java       # Data access with SQL injection vulnerabilities
│   └── ProductRepository.java    # Repository with inefficient queries
├── model/
│   ├── User.java                 # Entity with multiple defects
│   └── Product.java              # Entity with validation issues
├── util/
│   ├── StringUtils.java          # Utilities with security and quality issues
│   ├── DateUtils.java            # Date handling with deprecated APIs
│   └── FileHelper.java           # File operations with security vulnerabilities
└── config/
    ├── SecurityConfig.java       # Insecure security configuration
    └── AppConfig.java            # Configuration with hardcoded secrets
```

## Categories of Defects

### 1. Security Vulnerabilities

#### SQL Injection
- `UserRepository.java`: Concatenating user input in SQL queries
- `ProductService.java`: Building dynamic queries with string concatenation

#### Path Traversal
- `UserController.java`: `/logs` and `/profile/image` endpoints allow arbitrary file access
- `FileHelper.java`: No path validation in file operations

#### Command Injection
- `UserController.java`: `/export` endpoint executes shell commands with user input

#### XSS (Cross-Site Scripting)
- `UserController.java`: `/search` endpoint reflects user input without sanitization

#### Information Disclosure
- `application.properties`: Stack traces and error details exposed
- `UserController.java`: Exposing passwords and session IDs
- `User.java`: Password exposed in toString() method

#### Authentication & Authorization Issues
- `SecurityConfig.java`: CSRF disabled, all requests permitted
- `UserController.java`: No authentication checks on sensitive operations
- Password stored in plain text (NoOpPasswordEncoder)

#### Hardcoded Credentials
- `application.properties`: Hardcoded database credentials
- `AppConfig.java`: Hardcoded API keys and encryption keys
- `StringUtils.java`: Hardcoded cryptographic secret

#### Weak Cryptography
- `StringUtils.java`: MD5 hashing (deprecated), weak XOR encryption
- Weak random number generation for tokens

#### CORS Misconfiguration
- `UserController.java`: Allowing all origins with `@CrossOrigin(origins = "*")`
- `AppConfig.java`: Overly permissive CORS configuration

#### Insecure File Handling
- `FileHelper.java`: Resource leaks, insecure deserialization
- `UserController.java`: Insecure direct object references

### 2. Code Smells

#### Dead Code
- `Product.java`: `unusedField`
- `UserService.java`: `unusedPrivateMethod()`
- `StringUtils.java`: `unusedMethod()`

#### Duplicate Code
- `ProductService.java`: Duplicate logic in `createProduct()` and `updateProduct()`
- `ProductController.java`: Code duplication across CRUD operations

#### Long Methods
- `UserService.java`: `createUser()` does too many things
- `ProductService.java`: Methods with multiple responsibilities

#### Too Many Parameters
- `UserService.java`: `updateUser()` has 7 parameters
- `ProductService.java`: Constructor and method parameters

#### Magic Numbers
- `Product.java`: Hard-coded column length (100, 5000)
- `DateUtils.java`: Magic numbers in date calculations
- `ProductController.java`: Hard-coded discount percentages

#### Boolean Parameters
- `ProductService.java`: `getProducts(boolean includeOutOfStock)`
- `DateUtils.java`: `formatDate(Date date, boolean includeTime)`

#### Complex Boolean Logic
- `Product.java`: `isAvailable()` has deeply nested conditions
- `StringUtils.java`: `isValidPassword()` with nested if statements
- `ProductService.java`: `validateProduct()` with excessive branching

### 3. Thread Safety Issues

#### Non-thread-safe Static Fields
- `DateUtils.java`: Static `SimpleDateFormat` instances
- `UserService.java`: Static mutable collections
- `StringUtils.java`: Static counter modified without synchronization

#### Shared Mutable State
- `UserService.java`: `activeUsers` static list
- `DateUtils.java`: `dateCache` static list

### 4. Resource Management

#### Resource Leaks
- `FileHelper.java`: Not closing FileInputStream, FileWriter
- `UserService.java`: FileWriter not closed properly
- `ProductService.java`: Database connections not closed

#### Missing try-with-resources
- Multiple classes open streams without try-with-resources

### 5. Exception Handling

#### Empty Catch Blocks
- `BuggyApplication.java`: Empty catch in main method
- `FileHelper.java`: Empty catch blocks
- `UserService.java`: Various empty catches

#### Catching Generic Exceptions
- `ProductService.java`: Catching `Exception` instead of specific types
- `DateUtils.java`: Catching generic exceptions

#### Throwing Generic Exceptions
- `UserService.java`: Throwing `Exception` instead of specific type

#### Returning Null on Error
- Multiple methods return null instead of using Optional or throwing exceptions

### 6. Performance Issues

#### N+1 Query Problem
- `ProductService.java`: `updateAllProductPrices()` saves in a loop

#### Inefficient Database Queries
- `ProductRepository.java`: Loading all products when only count needed
- `ProductService.java`: Filtering in application layer instead of database

#### String Concatenation in Loops
- `StringUtils.java`: `repeat()` method
- `UserService.java`: `generateUserReport()`

#### Loading Large Datasets
- `FileHelper.java`: Reading entire file into memory
- `ProductController.java`: No pagination on list endpoints

### 7. Deprecated API Usage

#### Date/Time API
- `User.java`: Using `java.util.Date` instead of `java.time`
- `DateUtils.java`: Using `Calendar` and deprecated Date constructors
- `SimpleDateFormat` instead of `DateTimeFormatter`

#### Other Deprecations
- `SecurityConfig.java`: `NoOpPasswordEncoder`

### 8. OOP Violations

#### Missing Private Constructor
- `StringUtils.java`: Utility class without private constructor

#### Mutable Public Fields
- `User.java`: `role` field is public

#### Missing @Override
- `Product.java`: `toString()` missing annotation

#### Improper equals/hashCode
- `User.java`: Broken equals() and hashCode() implementations

#### Returning Mutable Collections
- `ProductService.java`: Returning mutable list directly
- `DateUtils.java`: Returning mutable date references

### 9. Input Validation

#### Missing Null Checks
- Multiple methods don't validate null parameters
- `Product.java`: Setters allow negative values

#### No Input Sanitization
- Controllers accept raw input without validation
- Mass assignment vulnerabilities

### 10. Maintainability Issues

#### Inconsistent Naming
- Mixture of naming conventions

#### Poor Error Messages
- Generic error messages without context

#### Hardcoded Values
- File paths, URLs, configuration values hardcoded

#### Missing Documentation
- No JavaDoc comments

## Running the Application

### Prerequisites
- Java 17 or higher
- Gradle (or use the included wrapper)

### Build
```bash
./gradlew build
```

### Run
```bash
./gradlew bootRun
```

The application will start on `http://localhost:8080`

### Available Endpoints

- GET `/api/users` - Get all users
- POST `/api/users` - Create user
- GET `/api/users/{id}` - Get user by ID
- DELETE `/api/users/{id}` - Delete user
- GET `/api/products` - Get all products
- POST `/api/products` - Create product
- GET `/api/products/search?query=` - Search products
- GET `/h2-console` - H2 database console

### Database
The application uses an in-memory H2 database:
- URL: `jdbc:h2:mem:testdb`
- Username: `admin`
- Password: `admin123`

## Testing with Static Analysis Tools

### SonarQube
```bash
./gradlew sonar \
  -Dsonar.projectKey=buggy-app \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.login=your-token
```

### SpotBugs
Add to build.gradle:
```gradle
plugins {
    id 'com.github.spotbugs' version '5.0.13'
}
```

### Checkstyle
Add to build.gradle:
```gradle
plugins {
    id 'checkstyle'
}
```

### PMD
Add to build.gradle:
```gradle
plugins {
    id 'pmd'
}
```

## Expected Findings

Static analysis tools should detect:
- 50+ security vulnerabilities
- 100+ code smells
- 30+ bugs
- 20+ performance issues
- 40+ maintainability issues

## License

This project is for educational and testing purposes only. Do not use any code from this project in production environments.

## Disclaimer

All defects in this codebase are intentional and created specifically for testing static analysis tools. This code should never be used as a reference for how to write production code.

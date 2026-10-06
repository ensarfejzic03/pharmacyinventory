# Pharmacy Medication Tracking System

A pharmacy inventory management system built with Java, Spring Boot, Thymeleaf, JPA/Hibernate, and MySQL.

The system allows pharmacy staff to manage medications, monitor inventory, record stock transactions, view alerts, generate reports, and manage user access based on assigned roles.

## Features

- User login and logout
- BCrypt password hashing
- Custom session-based authentication and role-based authorization
- Medication management
- Inventory tracking
- Stock In and Stock Out transactions
- Low-stock alerts
- Expiration-date alerts
- Medication search
- Sort inventory by expiration date
- Usage and inventory reports
- Dashboard statistics
- User account management
- Custom exception handling and transaction validation
- Automated service unit tests
- Duplicate email validation


## User Roles

The system supports four user roles:

- Admin
- Pharmacist
- Inventory Manager
- Technician

Each role has different permissions within the system.

### Admin

- Manage medications
- View inventory
- Update stock
- View reports
- View alerts
- Create users
- Delete users

### Pharmacist

- Manage medications
- View inventory
- Update stock
- View reports
- View alerts

### Inventory Manager

- View medications
- View inventory
- Record Stock In and Stock Out transactions
- View reports
- View alerts

### Technician

- View medications
- View inventory
- View transaction history
- View alerts

## Technologies Used

- Java 17
- Spring Boot
- Spring MVC
- Spring Data JPA
- Hibernate
- Thymeleaf
- MySQL
- Maven
- HTML
- CSS
- BCrypt
- JUnit 5
- Mockito

## Architecture

The application follows a layered architecture:

- **Controller** - Handles HTTP requests, session checks, role-based access, and page navigation
- **Service** - Contains business logic, validation, and transaction handling
- **Repository** - Handles database access using Spring Data JPA
- **Entity** - Represents database tables and relationships
- **Exception** - Contains custom exceptions for application-specific error handling

## Main Pages

### Dashboard

Displays system statistics including:

- Total medications
- Low-stock medications
- Medications expiring soon
- Total transactions

### Medications

Allows users to:

- View medication records
- Search medications
- Sort inventory by expiration date
- Add medications
- Edit medications

Add and edit permissions depend on the user's role.

### Inventory

Displays:

- Medication name
- Category
- Current quantity
- Reorder threshold
- Stock status
- Expiration date
- Storage location
- Last updated date

### Transactions

Allows authorized users to record:

- Stock In
- Stock Out

Each transaction records:

- Medication
- User
- Transaction type
- Quantity changed
- Transaction date

### Alerts

Displays:

- Medications below their reorder threshold
- Medications expiring within 90 days

### Reports

Includes:

- Most Used Medications
- Monthly Usage Trends
- Medications Below Reorder Threshold
- High-Risk Medications

### Manage Users

Admin users can:

- Create new user accounts
- Assign roles
- Delete users
- View existing users

Duplicate email addresses are prevented.

## Security

Passwords are stored using BCrypt hashing.

The system also includes:

- Login validation
- Session-based authentication
- Role-based page access
- Prevention of deleting the currently logged-in Admin account

## Testing

The project includes unit tests using JUnit 5 and Mockito.

Current tests cover:

- Stock Out quantity updates
- Insufficient stock handling
- Invalid transaction amounts
- Invalid transaction types
- Missing inventory handling

## Database

The application uses a MySQL database named:

```text
pharmacy_db
```

## How to Run

1. Clone or download the repository from GitHub.
2. Create a MySQL database named `pharmacy_db`.
3. Configure the database connection in `application.properties`.
4. Run the Spring Boot application.
5. Open the application in a browser at `http://localhost:8080`.
# University ERP System

## Overview

The University ERP (Enterprise Resource Planning) System is a comprehensive Java-based application designed to manage academic operations for a university. It provides role-based access for administrators, instructors, and students, facilitating efficient management of courses, enrollments, grades, and user accounts. The system features a graphical user interface built with Java Swing and uses MySQL databases for data persistence.

## Features

### Authentication & User Management
- **Secure Login**: User authentication with hashed passwords
- **User Registration**: Signup functionality for new users
- **Password Management**: Change password feature
- **Role-Based Access Control**: Different dashboards and permissions for Admin, Instructor, and Student roles

### Administrator Features
- **Dashboard**: Overview of system status and key metrics
- **Course Management**: Create, update, and manage course offerings
- **User Management**: Administer user accounts and roles
- **Assignment Management**: Handle course assignments and allocations
- **Backup & Maintenance**: Database backup functionality and maintenance mode toggle
- **System Maintenance**: Enable/disable maintenance mode with banner notifications

### Instructor Features
- **Dashboard**: Personalized instructor overview
- **My Sections**: View and manage assigned course sections
- **Gradebook**: Enter and manage student grades
- **Class Statistics**: View performance analytics for classes

### Student Features
- **Dashboard**: Student-specific overview
- **Course Registration**: Enroll in available courses
- **Timetable**: View class schedules
- **Grades**: Access current grades and grade history
- **Transcript**: View academic transcript with GPA calculation

### Data Management
- **Database Integration**: MySQL/MariaDB support with connection pooling (HikariCP)
- **Data Access Objects (DAO)**: Structured data layer for all entities
- **Seed Data**: Automated database setup with sample data
- **Data Integrity**: Constraints and validation for data consistency

### System Features
- **Maintenance Mode**: System-wide maintenance toggle with user notifications
- **Error Handling**: Comprehensive error handling and user feedback
- **Modular Architecture**: Clean separation of UI, service, and data layers
- **Extensible Design**: Easy to add new features and modules

## Technologies Used

- **Programming Language**: Java 21
- **User Interface**: Java Swing
- **Database**: MySQL/MariaDB
- **Connection Pooling**: HikariCP
- **Testing**: JUnit 5
- **Build Tool**: Manual compilation (no Maven/Gradle)
- **Version Control**: Git

## Project Structure

```
src/
├── edu/univ/erp/
│   ├── access/          # Access control utilities
│   ├── api/             # API endpoints (admin, auth, catalog, etc.)
│   ├── auth/            # Authentication services
│   ├── data/            # Database layer (DAOs, connectors, SQL files)
│   ├── domain/          # Domain objects (POJOs)
│   ├── service/         # Business logic services
│   ├── ui/              # User interface (Swing components)
│   └── util/            # Utility classes
├── resources/           # Database configuration properties
└── config.properties    # Application configuration
```

## Getting Started

For detailed setup and run instructions, please refer to [Univ ERP StartGuide](../Univ ERP StartGuide.pdf).

## Testing

Comprehensive testing information, including test plans and execution instructions, can be found in [ERP Test Plan Summary](../ERP Test Plan Summary.pdf).

## Documentation

- [Implementation Report](../Univ ERP Project Report.pdf): Detailed implementation description
- [Diagrams](../Univ ERP FlowDiag.pdf): System architecture and flow diagrams
- [Setup Guide](../Univ ERP StartGuide.pdf): Installation and usage instructions

## Key Components

### Domain Layer
- Course, StudentProfile, InstructorProfile
- GradebookEntry, TranscriptItem, TimetableItem
- UserSession, ClassStatistic

### Service Layer
- AdminService: Administrative operations
- InstructorService: Instructor-specific functionality
- StudentService: Student operations

### Data Layer
- DatabaseConnector: Connection management
- Various DAOs: Data access for entities
- SQL scripts: Database schema and seed data

### UI Layer
- Role-specific dashboards
- Specialized frames for different operations
- Maintenance banner for system notifications

## Security Features

- Password hashing for secure storage
- Role-based access control
- Session management
- Input validation and SQL injection prevention

## Future Enhancements

- Web-based interface (migration from Swing)
- REST API for external integrations
- Advanced reporting and analytics
- Mobile application support
- Multi-language support
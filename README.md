# SmartClass 3D

Smart Empty Classroom Finder in the Campus is a database-connected Java web application for locating available classrooms, managing live room status, viewing timetables, and handling classroom booking approvals.

## Project team

| Student | Enrollment number |
|---|---|
| IRFAN SHAIK | 92500119195 |
| SHAIK MD FAROOQ | 92500119161 |
| P.RUSHIK | 92500119183 |

Subject: Advanced Java Technology (AJT - 01CE0308)

## What works

- Student, lecturer, and administrator login with BCrypt password verification.
- Role-based session authorization and session fixation protection.
- Live database-derived classroom state: available, occupied, reserved, or maintenance.
- Search and filters by classroom, building, status, capacity, and facility.
- Interactive data-driven 3D campus canvas with rotation, zoom, color status, and building selection.
- Student booking requests and cancellations.
- Administrator booking approval/rejection with overlap checks and a serializable transaction.
- Lecturer/admin manual room status with automatic expiration.
- Full administrator CRUD for users, buildings, classrooms, and timetable entries.
- Favorites, audit logging, validation, CSRF protection, and secure response headers.
- Responsive JSP interface; all runtime data comes from MariaDB.

## Technology stack

- Java 17, Maven, Jakarta Servlet 6, JSP 3.1, JSTL 3
- Spring Context and Spring JDBC 6.2 for dependency injection and persistence access
- JDBC, HikariCP, MySQL Connector/J, MariaDB/MySQL
- Jackson JSON, BCrypt password hashing, JUnit 5
- HTML5, CSS3, vanilla JavaScript, Canvas 2D
- Apache Tomcat 10.1 and XAMPP/phpMyAdmin

The project intentionally does not require Spring Boot, Hibernate, Node.js, npm, or any CDN at runtime.

## Architecture

```text
Browser (JSP/CSS/JS/Canvas)
        |
Servlets + filters (/login, /dashboard, /api/*)
        |
SmartClassService (business rules and transactions)
        |
Spring JdbcTemplate + HikariCP
        |
MariaDB smart_classroom_finder
```

Spring creates the DataSource, JdbcTemplate, ObjectMapper, and service in `AppConfig`. `AppContextListener` starts and closes the Spring context with the web application.

## Database

The schema contains 11 related tables, 2 views, 2 triggers, and 2 stored procedures. Core tables are `users`, `departments`, `buildings`, `classrooms`, `timetable`, `bookings`, `room_status_updates`, `facilities`, `classroom_facilities`, `favorites`, and `audit_logs`.

### Import with phpMyAdmin

1. Start Apache and MySQL in XAMPP.
2. Open `http://localhost/phpmyadmin`.
3. Import `database/schema.sql` first.
4. Select the `smart_classroom_finder` database.
5. Import `database/seed.sql` second.

Re-importing seed data into an already populated database can create duplicate-key errors. Run the schema first because it recreates a clean database.

### Import with PowerShell

```powershell
powershell -ExecutionPolicy Bypass -File .\tools\setup-database.ps1
```

Defaults are XAMPP MySQL at `C:\xampp\mysql\bin\mysql.exe`, port 3306, user `root`, and no password. Parameters are documented inside the script.

## Build

Requirements: JDK 17 and Maven 3.9+.

```powershell
mvn clean test
mvn clean package
```

Expected result: 15 JUnit tests, zero failures, and:

```text
target/smart-classroom-finder-1.0.0-SNAPSHOT.war
```

## Deploy to Tomcat 10.1

```powershell
powershell -ExecutionPolicy Bypass -File .\tools\deploy.ps1 `
  -TomcatHome "C:\path\to\apache-tomcat-10.1.x"
```

Open `http://localhost:8080/smartclass3d/login`.

On the tested Windows system, Tomcat used the NIO2 connector in `conf/server.xml`:

```xml
<Connector port="8080" protocol="org.apache.coyote.http11.Http11Nio2Protocol"
           connectionTimeout="20000" redirectPort="8443" />
```

For a normal Tomcat installation, the default HTTP connector may also work.

## Configuration

Environment variables or Java system properties may override defaults:

| Name | Default |
|---|---|
| `DB_URL` | `jdbc:mysql://127.0.0.1:3306/smart_classroom_finder?...` |
| `DB_USER` | `root` |
| `DB_PASSWORD` | empty |
| `APP_TIMEZONE` | `Asia/Kolkata` |

## Demo accounts

| Role | Email | Password |
|---|---|---|
| Administrator | `admin@smartclass.com` | `Admin@123` |
| Lecturer | `lecturer@smartclass.com` | `Lecturer@123` |
| Student | `student@smartclass.com` | `Student@123` |

These accounts are for demonstration only. Change them before any real deployment.

## Verification

```powershell
python .\tools\verify_runtime.py
node .\tools\browser_qa.js
```

Verified on Apache Tomcat 10.1.59, Eclipse Temurin JDK 17, MariaDB 10.4, and Google Chrome. The automated runtime check covers all roles, database room loading, role denial, favorites, booking create/approve/cancel, live status update/release, CSRF rejection, admin create/update/deactivate, and audit logs.

## Documentation

- `docs/project-report.md` - full project report source
- `docs/architecture.md` - system and package architecture
- `docs/database-design.md` - database design and status algorithm
- `docs/api-documentation.md` - route and API reference
- `docs/test-cases.md` - verified test evidence
- `docs/rubric-checklist.md` - lecturer rubric mapping
- `docs/viva-guide.md` - concise viva preparation
- `docs/screenshots/` - verified application screenshots

## Submission notes

The generated report PDF is named with the first student's enrollment number as requested by the rubric. A GitHub URL and a public deployed URL require the team's own GitHub/hosting accounts and therefore must be added after uploading.

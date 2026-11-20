# SMARTCLASS 3D

## Smart Empty Classroom Finder in the Campus

Advanced Java Technology (AJT - 01CE0308) Project Report

### Submitted by

| Name | Enrollment number |
|---|---|
| IRFAN SHAIK | 92500119195 |
| SHAIK MD FAROOQ | 92500119161 |
| P.RUSHIK | 92500119183 |

Faculty of Engineering and Technology - Computer Engineering - 2026

## Abstract

Students and lecturers often spend time physically checking rooms before finding an unused classroom. SmartClass 3D replaces that uncertain process with one database-connected campus dashboard. It combines the current weekly timetable, approved booking intervals, and time-limited lecturer overrides to calculate the live status of every room. Users can search and filter rooms, inspect capacity and facilities, request a booking, and interact with a lightweight 3D campus visualization. Administrators manage data and booking decisions through a role-protected interface. The system is implemented as a layered Java web application using Servlets, JSP, JSTL, Spring dependency injection, JDBC, MariaDB, and Apache Tomcat.

## 1. Problem statement

Campus rooms are shared resources, but timetable data alone does not describe unexpected classes, approved project sessions, cancellations, or maintenance. Manual inquiry is slow and can lead to double booking. The project must provide a reliable and understandable view of empty and occupied classrooms while preserving authority: students request space, lecturers report live use, and administrators manage records and approve requests.

## 2. Objectives

- Display live, database-derived room availability.
- Provide separate student, lecturer, and administrator experiences.
- Combine timetable, booking, and manual status information consistently.
- Support complete CRUD for important campus records.
- Prevent invalid or overlapping bookings.
- Present the campus through a responsive interactive 3D-style view.
- Demonstrate AJT concepts: Servlets, JSP, JSTL/EL, sessions, Spring DI, JDBC, validation, and exception handling.

## 3. Functional requirements

### Student

Authenticate with a selected role, view the live overview, search/filter rooms, inspect details and facilities, favorite rooms, create booking requests, review personal requests, and cancel pending/approved requests.

### Lecturer

Use the finder and timetable, mark a classroom occupied/reserved/under maintenance for a selected duration, and release the manual status when the room is available.

### Administrator

View all bookings, approve or reject pending requests, maintain users/buildings/rooms/timetable entries, update room state, and inspect audit history.

## 4. Non-functional requirements

The UI must be responsive and usable in current browsers. Passwords must be hashed. SQL must be parameterized. Sessions and role permissions must protect private routes. Mutating requests must resist cross-site request forgery. Database failures must produce controlled responses. The project must build as a reproducible WAR for Tomcat 10.1 and Java 17.

## 5. Technology selection

Java 17 provides the application language and record support. Jakarta Servlet 6 handles routing and filters, JSP/JSTL/EL renders server views, and Spring Context performs dependency injection. Spring JDBC and prepared statements keep persistence explicit for AJT assessment. HikariCP pools connections. MariaDB in XAMPP stores all operational data. BCrypt hashes credentials and Jackson serializes API responses. The frontend uses semantic HTML, custom responsive CSS, vanilla JavaScript, and Canvas 2D. Maven compiles, tests, and packages the WAR; JUnit 5 verifies core validation and session behavior.

## 6. System architecture

The presentation layer contains protected JSP views and static frontend assets. The web layer contains login/dashboard/logout/API servlets plus authentication and security filters. `SmartClassService` is the business layer for validation, authorization, status calculation, booking workflows, transactions, and audit records. The persistence layer uses Spring JdbcTemplate and HikariCP to access MariaDB. `AppContextListener` owns the Spring application context for the Tomcat lifecycle.

The browser requests `/api/bootstrap` after login and every 10 seconds. The response includes the user, room records, statistics, buildings, bookings, timetable, and a CSRF token. Administrators additionally receive user and audit-log data. This design keeps the page responsive without full reloads.

## 7. Database design

The schema uses 11 related InnoDB tables: departments, users, buildings, classrooms, facilities, classroom_facilities, timetable, bookings, room_status_updates, favorites, and audit_logs. Foreign keys define the relationships, while unique keys prevent duplicate identities, building codes, and favorite/facility links. Indexes support live status, time intervals, roles, and active-state filters. Two views, two procedures, and two triggers demonstrate database-side organization and validation.

Data never comes from browser hardcoding. Login reads `users`; room cards join classrooms, buildings, departments, facilities, favorites, bookings, timetable, and status updates. Admin changes write directly to the relevant table and add an audit entry.

## 8. Live availability algorithm

The service applies one deterministic priority order for every classroom:

1. Active manual maintenance status.
2. Most recent active manual occupied or reserved status.
3. Approved booking whose interval contains the current time.
4. Active timetable row matching the current day, date, and time.
5. Available when no higher-priority condition exists.

Each status also returns a human-readable reason. The room color in the card and 3D campus is produced from this database result.

## 9. Booking transaction

A request must identify an active classroom, contain a purpose, begin in the future, end after it starts, and last no more than eight hours. Approval runs at SERIALIZABLE isolation. The service locks the pending booking and classroom, checks approved booking overlap and timetable overlap, updates the booking, commits, and writes an audit entry. Any failure rolls the transaction back and returns a controlled HTTP status such as 409 Conflict.

## 10. Security and validation

- BCrypt hashes and verifies passwords.
- Login invalidates the old session before creating the authenticated session.
- HttpOnly and SameSite=Lax protect the session cookie.
- AuthenticationFilter protects dashboard and API paths.
- Service methods enforce role authorization again.
- CSRF tokens protect login and every mutation.
- Prepared statements parameterize all external data.
- Server-side validation checks strings, IDs, roles, statuses, dates, and intervals.
- Content Security Policy and additional security headers reduce browser attack surface.
- Views use JSTL output escaping for user-controlled display values.

## 11. User interface

The login page provides role choice and demonstration accounts. The dashboard presents current statistics, best available rooms, live time, and a data-driven perspective campus. Drag changes the viewing angle, the mouse wheel changes zoom, and selecting a building opens its rooms. The classroom finder supports text/building/status/capacity filters. Booking, timetable, status, and administration areas share the same responsive design. A list/card interface remains usable on devices where detailed 3D interaction is impractical.

## 12. Testing and results

The final Maven package completed successfully with 15 JUnit tests, zero failures, zero errors, and zero skipped. The WAR was deployed to Apache Tomcat 10.1.59 using Eclipse Temurin JDK 17 and connected to MariaDB 10.4.

The automated runtime test verified student, lecturer, and administrator login; 16 rooms loaded from MariaDB; role denial; favorites; booking create/approve/cancel; CSRF rejection; lecturer status persistence and release; administrator create/update/deactivate; refresh; and audit records. Chrome browser automation verified all role interfaces, finder search, lecturer controls, admin/audit display, visible campus canvas, and zero console or request failures.

## 13. Rubric compliance

The work supplies a problem definition and functional requirements; a well-related database with more than three tables; JDBC connectivity; complete CRUD; validation and exception handling; Servlets; JSP/JSTL/EL; session management; Spring dependency injection; layered architecture; a functional Tomcat WAR; source ZIP; and an enrollment-number-named screenshot report. GitHub and public hosting require the students' account credentials and are final team upload actions.

## 14. Limitations and future scope

Room state is derived from institutional records and manual lecturer input; physical occupancy sensors are outside this academic scope. A future version could consume IoT sensors, add email/push notifications, campus single sign-on, analytics, and a WebSocket channel for instant updates. Public hosting should use HTTPS, environment-managed secrets, a non-root database user, and changed demonstration passwords.

## 15. Conclusion

SmartClass 3D turns scattered scheduling information into a working campus resource system. Its value comes from combining a clear visual experience with database integrity, role accountability, transactions, and a reproducible Java deployment. The completed implementation meets the AJT learning goals while remaining understandable enough to demonstrate and extend.

## Appendix A - Demonstration accounts

| Role | Email | Password |
|---|---|---|
| Administrator | admin@smartclass.com | Admin@123 |
| Lecturer | lecturer@smartclass.com | Lecturer@123 |
| Student | student@smartclass.com | Student@123 |

## Appendix B - Build and run summary

1. Start MySQL/MariaDB in XAMPP.
2. Import `database/schema.sql`, then `database/seed.sql`.
3. Run `mvn clean package` under JDK 17.
4. Copy the generated WAR to Tomcat as `smartclass3d.war`.
5. Start Tomcat with the database configuration.
6. Open `http://localhost:8080/smartclass3d/login`.

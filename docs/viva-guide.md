# Viva guide

## One-minute explanation

SmartClass 3D solves the problem of students wasting time searching for an empty classroom. It combines timetable, approved bookings, and lecturer status updates in MariaDB, calculates each room's live state, and presents it in a searchable dashboard and interactive 3D campus view. Students request rooms, lecturers update room state, and administrators manage master data and approvals.

## Likely questions

**Why Spring?** Spring creates and wires the DataSource, JdbcTemplate, JSON mapper, and business service. It gives clear dependency injection without requiring Spring Boot.

**Why JDBC instead of Hibernate?** The rubric permits JDBC or Hibernate. JDBC was selected to show SQL, prepared statements, transactions, and database design directly.

**How is a room marked occupied?** The SQL priority checks maintenance/manual overrides, active approved bookings, and the timetable for the current day and time. If none applies, it is available.

**How are conflicts prevented?** Admin approval uses a serializable transaction, locks the booking and classroom rows, and checks both timetable and approved booking overlap before committing.

**How is security handled?** BCrypt passwords, session regeneration after login, role checks, CSRF tokens, prepared SQL, input validation, HttpOnly/SameSite cookie, and security headers.

**What is the 3D part?** Canvas renders database buildings as perspective cuboids. It supports drag rotation, wheel zoom, status colors, and click-to-filter. It is lightweight and works without external CDN access.

**What is CRUD?** Create, read, update, and deactivate operations are implemented for users, buildings, rooms, and timetable entries. Deactivation is used instead of destructive deletion.

**How is live data refreshed?** The browser calls `/api/bootstrap` every 10 seconds, updating the cards, statistics, bookings, timetable, and 3D colors without a full reload.

## Demonstration order

1. Log in as student; show live overview, 3D interaction, finder, filters, and booking request.
2. Log in as lecturer; show status update and automatic dashboard change.
3. Log in as administrator; approve/reject booking, demonstrate CRUD, and open audit log.
4. Show phpMyAdmin tables and relationships.
5. Run `mvn clean package` and show BUILD SUCCESS.

# Architecture

## Layered design

1. Presentation: JSP pages, CSS, JavaScript, and the interactive campus canvas.
2. Web: servlet routing, authentication, authorization, CSRF checks, security headers, request parsing, and JSON responses.
3. Service: authentication, live classroom calculation, validation, booking workflow, transaction boundaries, admin CRUD, and audit logging.
4. Persistence: Spring `JdbcTemplate`, prepared statements, HikariCP, and MariaDB.

## Runtime flow

Tomcat starts `AppContextListener`, which creates an `AnnotationConfigApplicationContext`. `AppConfig` constructs the pooled DataSource, JdbcTemplate, ObjectMapper, and `SmartClassService`. The service is stored in ServletContext for the servlets.

Login creates a new HTTP session after invalidating the pre-login session. AuthenticationFilter protects `/dashboard` and `/api/*`. Each mutation requires the CSRF token stored in the session. Authorization is checked again in service methods for sensitive actions.

## Status decision order

For every room, one database query applies this priority:

1. Active manual maintenance override.
2. Latest active manual occupied/reserved override.
3. Approved booking currently in progress.
4. Active timetable entry for the current day/time/date.
5. Available.

The browser reloads `/api/bootstrap` every 10 seconds, so status changes appear without a full page reload.

## Packages

- `com.smartclass.app`: configuration, listener, service, validation, session user, application exceptions.
- `com.smartclass.web`: servlets, filters, web utilities, JSON/error handling.
- `src/main/webapp/WEB-INF/views`: protected JSP pages.
- `database`: repeatable schema and demonstration data.

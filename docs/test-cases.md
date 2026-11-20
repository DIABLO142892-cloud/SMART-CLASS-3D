# Test evidence

## Build tests

`mvn clean package` completed with BUILD SUCCESS on 6 September 2026. JUnit result: 15 tests, 0 failures, 0 errors, 0 skipped.

The unit tests cover required fields, positive integer parsing, role normalization and rejection, status validation, date-time parsing, application exception status, record serialization, role checks, and JavaBean/JSP compatibility.

## Runtime integration checks

`tools/verify_runtime.py` completed successfully against Tomcat and MariaDB:

- Login passed for student, lecturer, and administrator.
- 16 classroom records and 3 campus buildings loaded from MariaDB.
- Student access to admin CRUD returned HTTP 403.
- Favorite toggle and restoration passed.
- Student booking create, administrator approval, and student cancellation passed.
- Missing CSRF token returned HTTP 403.
- Lecturer maintenance status persisted and released successfully.
- Administrator create, update, and deactivate passed.
- Dashboard refresh and audit trail passed.

## Browser checks

`tools/browser_qa.js` completed in installed Google Chrome:

- All three dashboards rendered with 16 room cards.
- Student administration controls remained hidden.
- Classroom search returned the expected room.
- Lecturer status controls were visible.
- Admin CRUD and audit-log views rendered.
- Interactive canvas had the expected visible dimensions.
- No page errors, console errors, or failed requests were observed.

Screenshots are stored in `docs/screenshots`.

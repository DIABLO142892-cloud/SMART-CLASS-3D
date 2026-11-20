# SmartClass 3D submission checklist

## Ready artifacts

- Source ZIP: `SmartClass3D_92500119195_Source.zip`
- Project report: `output/pdf/92500119195_IRFAN_SHAIK.pdf`
- Deployable WAR: `target/smart-classroom-finder-1.0.0-SNAPSHOT.war`
- Database: `database/schema.sql` followed by `database/seed.sql`
- Setup and credentials: `README.md`
- Architecture, API, database, tests, rubric mapping, and viva notes: `docs/`

## Demonstration order

1. Start MariaDB/MySQL and Tomcat.
2. Open `http://localhost:8080/smartclass3d/login`.
3. Log in as student and demonstrate live overview, 3D campus, finder, favorites, and booking request.
4. Log in as lecturer and demonstrate a time-limited room status update and release.
5. Log in as administrator and demonstrate booking approval, CRUD tabs, and the audit log.
6. Explain the live-status priority and SERIALIZABLE booking approval transaction.

## Team actions before submission

- Upload the prepared source to the required GitHub account and add the final repository link to the submission form.
- If public hosting is mandatory, choose the approved host and deploy with HTTPS and non-demo secrets.
- Confirm the submitted filenames and enrollment numbers match the university portal requirements.

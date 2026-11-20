# Web and API reference

Base context after the supplied deployment script: `/smartclass3d`.

## Web routes

| Method | Route | Purpose |
|---|---|---|
| GET/POST | `/login` | Render and submit role-aware login |
| GET | `/dashboard` | Authenticated JSP dashboard |
| POST | `/logout` | CSRF-protected session logout |

## JSON API

All API routes require an authenticated session. Mutating routes require `Content-Type: application/json` and `X-CSRF-Token`.

| Method | Route | Allowed role | Purpose |
|---|---|---|---|
| GET | `/api/bootstrap` | All | User, rooms, stats, buildings, timetable, bookings; admin also receives users and audit logs |
| POST | `/api/bookings` | All | Create pending booking |
| POST | `/api/bookings/{id}/approve` | Admin | Approve after overlap validation |
| POST | `/api/bookings/{id}/reject` | Admin | Reject booking |
| POST | `/api/bookings/{id}/cancel` | Owner | Cancel own pending/approved booking |
| POST | `/api/rooms/{id}/status` | Lecturer/Admin | Create or release live manual status |
| POST | `/api/favorites/{roomId}` | All | Toggle favorite |
| POST | `/api/admin/{resource}` | Admin | Create record |
| PUT | `/api/admin/{resource}/{id}` | Admin | Update record |
| DELETE | `/api/admin/{resource}/{id}` | Admin | Soft-deactivate record |

Admin resources: `users`, `buildings`, `rooms`, and `timetable`.

Success responses use HTTP 200 and JSON. Validation failures use 400, unauthenticated requests redirect to login or return 401, forbidden actions use 403, missing resources use 404, overlap/state conflicts use 409, and database/service failures use 500/503.

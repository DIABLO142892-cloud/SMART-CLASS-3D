# Database design

Database: `smart_classroom_finder`; engine: InnoDB; character set: utf8mb4.

## Main relationships

- A department has many users and classrooms.
- A building has many classrooms.
- A classroom has many timetable entries, bookings, status updates, and facilities.
- A user requests many bookings; an administrator may review them.
- Users and classrooms form a many-to-many favorite relationship.
- Classrooms and facilities form a many-to-many relationship through `classroom_facilities`.
- Audit entries identify the acting user and changed entity.

Foreign keys enforce relationships. Unique constraints protect building codes, user email/college IDs, room numbers within a building, favorites, and room-facility pairs. Indexes support role, active state, timetable, booking interval, and status lookups.

## Data integrity

- Prepared statements prevent SQL injection.
- Booking approval uses a serializable transaction and row locks.
- Timetable and already approved booking overlaps are rejected.
- Delete actions in the UI are recoverable soft deactivations.
- Triggered validation rejects invalid booking intervals.
- Passwords are BCrypt hashes, never plaintext.

`schema.sql` recreates all objects. `seed.sql` inserts 3 departments, 3 buildings, 16 classrooms, 8 facilities, 5 users, weekly timetable entries, sample bookings/status data, and audit entries.

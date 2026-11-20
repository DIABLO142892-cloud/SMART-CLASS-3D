-- ============================================================
-- SmartClass 3D Database Seed Data
-- ============================================================
-- Populates the database with demo data including:
-- - Three departments
-- - Three buildings with floors
-- - At least 18 classrooms
-- - Multiple facilities
-- - Sample timetable records
-- - Sample bookings and status records
-- - Three demo accounts (admin, lecturer, student)
-- ============================================================

-- ============================================================
-- 1. Insert Departments
-- ============================================================
INSERT INTO departments (code, name, active) VALUES 
('CSE', 'Computer Science and Engineering', 1),
('ECE', 'Electronics and Communication Engineering', 1),
('ME', 'Mechanical Engineering', 1);

-- ============================================================
-- 2. Insert Buildings
-- ============================================================
INSERT INTO buildings (code, name, description, total_floors, coordinate_x, coordinate_y, coordinate_z, active) VALUES 
('BLOCK-A', 'Main Academic Building', 'Main building with lecture halls and classrooms', 4, 0, 0, 0, 1),
('BLOCK-B', 'Science Building', 'Science laboratories and research facilities', 3, 150, 0, 0, 1),
('BLOCK-C', 'Student Center', 'Student services, common areas, and tutorial rooms', 2, 300, 0, 0, 1);

-- ============================================================
-- 3. Insert Classrooms (18+ classrooms across 3 buildings)
-- ============================================================
INSERT INTO classrooms (building_id, department_id, room_number, room_name, floor_number, capacity, room_type, active) VALUES 

-- Building A (Main Academic Building - 4 floors)
(1, 1, 'A-101', 'Lecture Hall A', 1, 150, 'Lecture Hall', 1),
(1, 1, 'A-102', 'Lecture Hall B', 1, 120, 'Lecture Hall', 1),
(1, 1, 'A-201', 'Seminar Hall A', 2, 40, 'Seminar Hall', 1),
(1, 1, 'A-202', 'Seminar Hall B', 2, 40, 'Seminar Hall', 1),
(1, 1, 'A-301', 'Computer Lab', 3, 30, 'Computer Lab', 1),
(1, 1, 'A-302', 'Computer Lab', 3, 30, 'Computer Lab', 1),
(1, 1, 'A-401', 'Tutorial Room', 4, 25, 'Tutorial Room', 1),
(1, 1, 'A-402', 'Tutorial Room', 4, 25, 'Tutorial Room', 1),

-- Building B (Science Building - 3 floors)
(2, 2, 'B-101', 'Science Lab Physics', 1, 50, 'Science Lab', 1),
(2, 2, 'B-102', 'Science Lab Chemistry', 1, 50, 'Science Lab', 1),
(2, 2, 'B-201', 'Computer Lab - Science', 2, 35, 'Computer Lab', 1),
(2, 2, 'B-301', 'Multipurpose Lab', 3, 40, 'Science Lab', 1),

-- Building C (Student Center - 2 floors)
(3, 3, 'C-101', 'Tutorial Room', 1, 30, 'Tutorial Room', 1),
(3, 3, 'C-102', 'Seminar Hall', 1, 45, 'Seminar Hall', 1),
(3, 3, 'C-201', 'Group Study Room', 2, 20, 'Tutorial Room', 1),
(3, 3, 'C-202', 'Presentation Room', 2, 60, 'Seminar Hall', 1);

-- ============================================================
-- 4. Insert Facilities
-- ============================================================
INSERT INTO facilities (name, icon, active) VALUES 
('Projector', 'fas fa-projector', 1),
('Smart Board', 'fas fa-chalkboard-teacher', 1),
('Air Conditioning', 'fas fa-snowflake', 1),
('Computer Systems', 'fas fa-laptop-code', 1),
('Wi-Fi', 'fas fa-wifi', 1),
('Microphone', 'fas fa-microphone', 1),
('Accessibility', 'fas fa-wheelchair', 1),
('Whiteboard', 'fas fa-blackboard', 1);

-- ============================================================
-- 5. Insert Classroom-Facility Mappings
-- ============================================================
-- Building A classrooms
INSERT INTO classroom_facilities (classroom_id, facility_id, quantity, working) VALUES 
(1, 1, 1, 1), (1, 2, 1, 1), (1, 3, 1, 1), (1, 4, 30, 1), (1, 5, 1, 1), (1, 6, 1, 1), (1, 7, 1, 1), (1, 8, 1, 1),
(2, 1, 1, 1), (2, 2, 1, 1), (2, 3, 1, 1), (2, 4, 25, 1), (2, 5, 1, 1), (2, 6, 1, 1), (2, 7, 1, 1), (2, 8, 1, 1),
(3, 1, 1, 1), (3, 2, 1, 1), (3, 8, 1, 1),
(4, 1, 1, 1), (4, 2, 1, 1), (4, 8, 1, 1),
(5, 1, 1, 1), (5, 2, 1, 1), (5, 4, 30, 1), (5, 5, 1, 1), (5, 8, 1, 1),
(6, 1, 1, 1), (6, 2, 1, 1), (6, 4, 30, 1), (6, 5, 1, 1), (6, 8, 1, 1),
(7, 1, 1, 1), (7, 2, 1, 1), (7, 8, 1, 1),
(8, 1, 1, 1), (8, 2, 1, 1), (8, 8, 1, 1),

-- Building B classrooms
(9, 1, 1, 1), (9, 3, 1, 1), (9, 4, 20, 1), (9, 5, 1, 1), (9, 6, 1, 1), (9, 7, 1, 1), (9, 8, 1, 1),
(10, 1, 1, 1), (10, 3, 1, 1), (10, 4, 20, 1), (10, 5, 1, 1), (10, 6, 1, 1), (10, 7, 1, 1), (10, 8, 1, 1),
(11, 1, 1, 1), (11, 4, 20, 1), (11, 5, 1, 1), (11, 8, 1, 1),
(12, 1, 1, 1), (12, 3, 1, 1), (12, 5, 1, 1), (12, 7, 1, 1),

-- Building C classrooms
(13, 1, 1, 1), (13, 2, 1, 1), (13, 8, 1, 1),
(14, 1, 1, 1), (14, 2, 1, 1), (14, 8, 1, 1),
(15, 1, 1, 1), (15, 8, 1, 1),
(16, 1, 1, 1), (16, 2, 1, 1), (16, 8, 1, 1);

-- ============================================================
-- 6. Insert Users (Demo Accounts)
-- ============================================================
-- Passwords are: admin@smartclass.com -> Admin@123, lecturer@smartclass.com -> Lecturer@123, student@smartclass.com -> Student@123
-- BCrypt hashes generated using: bcrypt.hashpw("password", bcrypt.gensalt())

INSERT INTO users (college_id, full_name, email, password_hash, role, department_id, active) VALUES 
('ADM001', 'Administrator', 'admin@smartclass.com', 
'$2a$10$1OPOCCoYVbGN/yHtp/rGceDTEw19rubu849gX8XzOveVv/TfWA6kK', 'ADMIN', NULL, 1),
 ('LEC001', 'Dr. Smith', 'lecturer@smartclass.com', 
  '$2a$10$dFIjfFyAtrR3fi.D/6LA0ewdpaJ1tH7jr1KczeZRJGiXe34Xh3wEq', 'LECTURER', 1, 1),
 ('STU001', 'John Doe', 'student@smartclass.com', 
  '$2a$10$DOQHRYwoyDk9IhpubFK9uunS2zBJ7swPIWKT3zKtZcoIQXANjnAnG', 'STUDENT', 1, 1),

-- Additional test users
('STU002', 'Jane Smith', 'jstudent@smartclass.com', 
 '$2a$10$DOQHRYwoyDk9IhpubFK9uunS2zBJ7swPIWKT3zKtZcoIQXANjnAnG', 'STUDENT', 2, 1),
 ('LEC002', 'Dr. Johnson', 'lecturer2@smartclass.com',
 '$2a$10$dFIjfFyAtrR3fi.D/6LA0ewdpaJ1tH7jr1KczeZRJGiXe34Xh3wEq', 'LECTURER', 2, 1);

-- ============================================================
-- 7. Insert Timetable Records (Weekly Schedule)
-- ============================================================
INSERT INTO timetable (classroom_id, lecturer_id, subject_code, subject_name, section_name, day_of_week, start_time, end_time, valid_from, valid_until, status) VALUES 

-- Monday classes
(1, 2, 'CS101', 'Introduction to Computing', 'A', 'Monday', '08:00:00', '09:30:00', '2024-01-01', NULL, 'ACTIVE'),
(1, 2, 'CS101', 'Introduction to Computing', 'A', 'Monday', '10:00:00', '11:30:00', '2024-01-01', NULL, 'ACTIVE'),
(2, 2, 'CS101', 'Introduction to Computing', 'A', 'Monday', '14:00:00', '15:30:00', '2024-01-01', NULL, 'ACTIVE'),

-- Tuesday classes
(3, 5, 'MA101', 'Calculus I', 'B', 'Tuesday', '09:00:00', '10:30:00', '2024-01-01', NULL, 'ACTIVE'),
(4, 5, 'MA101', 'Calculus I', 'B', 'Tuesday', '11:00:00', '12:30:00', '2024-01-01', NULL, 'ACTIVE'),

-- Wednesday classes
(5, 2, 'CS101', 'Introduction to Computing', 'A', 'Wednesday', '08:00:00', '09:30:00', '2024-01-01', NULL, 'ACTIVE'),
(6, 2, 'CS101', 'Introduction to Computing', 'A', 'Wednesday', '10:00:00', '11:30:00', '2024-01-01', NULL, 'ACTIVE'),

-- Thursday classes
(7, 5, 'PH101', 'Physics I', 'A', 'Thursday', '13:00:00', '14:30:00', '2024-01-01', NULL, 'ACTIVE'),

-- Friday classes
(8, 2, 'EE201', 'Electrical Engineering', 'B', 'Friday', '10:00:00', '11:30:00', '2024-01-01', NULL, 'ACTIVE'),

-- Saturday classes (optional)
(9, 5, 'PH101', 'Physics I', 'A', 'Saturday', '09:00:00', '10:30:00', '2024-01-01', NULL, 'ACTIVE'),

-- Building B labs
(11, 2, 'CS201', 'Data Structures', 'A', 'Monday', '11:00:00', '12:30:00', '2024-01-01', NULL, 'ACTIVE'),
(12, 2, 'CS201', 'Data Structures', 'A', 'Tuesday', '14:00:00', '15:30:00', '2024-01-01', NULL, 'ACTIVE'),

-- Building C tutorials
(13, NULL, 'FY101', 'Freshman Year Seminar', 'A', 'Monday', '14:00:00', '15:00:00', '2024-01-01', NULL, 'ACTIVE'),
(14, NULL, 'FY101', 'Freshman Year Seminar', 'A', 'Wednesday', '10:00:00', '11:00:00', '2024-01-01', NULL, 'ACTIVE'),
(15, NULL, 'FY101', 'Freshman Year Seminar', 'B', 'Thursday', '15:00:00', '16:00:00', '2024-01-01', NULL, 'ACTIVE'),
(16, NULL, 'FY101', 'Freshman Year Seminar', 'B', 'Friday', '11:00:00', '12:00:00', '2024-01-01', NULL, 'ACTIVE');

-- ============================================================
-- 8. Insert Sample Bookings
-- ============================================================
INSERT INTO bookings (classroom_id, requested_by, purpose, start_datetime, end_datetime, status, reviewed_by, review_note) VALUES 
(1, 3, 'Final Project Presentation', '2024-01-15 14:00:00', '2024-01-15 16:00:00', 'APPROVED', 1, 'Approved by Admin'),
(5, 4, 'Study Group Session', '2024-01-16 10:00:00', '2024-01-16 12:00:00', 'APPROVED', 1, 'Approved by Admin'),
(9, 3, 'Group Project', '2024-01-17 09:00:00', '2024-01-17 11:00:00', 'PENDING', NULL, NULL),
(13, 4, 'Club Meeting', '2024-01-18 15:00:00', '2024-01-18 17:00:00', 'APPROVED', 1, 'Approved by Admin');

-- ============================================================
-- 9. Insert Sample Room Status Updates
-- ============================================================
INSERT INTO room_status_updates (classroom_id, status, source, note, effective_from, effective_until, updated_by) VALUES 
(1, 'AVAILABLE', 'TIMETABLE', 'No current classes', NOW(), NULL, 2),
(2, 'OCCUPIED', 'BOOKING', 'Lecture in progress', NOW(), DATE_ADD(NOW(), INTERVAL 2 HOUR), 2),
(3, 'RESERVED', 'MANUAL', 'Seminar reserved', NOW(), DATE_ADD(NOW(), INTERVAL 1 DAY), 2),
(4, 'MAINTENANCE', 'MANUAL', 'Under maintenance', NOW(), DATE_ADD(NOW(), INTERVAL 3 DAY), 2),
(5, 'AVAILABLE', 'TIMETABLE', 'Available between classes', NOW(), NULL, 5);

-- ============================================================
-- 10. Insert Audit Log Entries
-- ============================================================
INSERT INTO audit_logs (user_id, action, entity_type, entity_id, details, ip_address) VALUES 
(1, 'LOGIN', 'USER', 1, 'Admin login', '127.0.0.1'),
(1, 'CREATE', 'USER', 5, 'Created classroom A-401', '127.0.0.1'),
(2, 'LOGIN', 'USER', 2, 'Lecturer login', '127.0.0.1'),
(2, 'UPDATE', 'CLASSROOM', 1, 'Updated classroom status', '127.0.0.1'),
(1, 'APPROVE', 'BOOKING', 1, 'Approved booking for classroom A-101', '127.0.0.1');

-- ============================================================
-- 11. Verify Demo Account Passwords
-- ============================================================
-- To verify: SELECT email, password_hash FROM users WHERE role='ADMIN';
-- The hashes above were generated using BCrypt with the passwords:
-- - Admin@123 for admin@smartclass.com
-- - Lecturer@123 for lecturer@smartclass.com  
-- - Student@123 for student@smartclass.com
-- ============================================================

-- ============================================================
-- End of Seed Data
-- ============================================================

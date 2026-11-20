-- ============================================================
-- SmartClass 3D Database Reset Script
-- ============================================================
-- WARNING: This script drops all tables and re-creates them.
-- Use only for development/reset purposes.
-- ============================================================

-- Drop tables in reverse order (due to foreign key constraints)
DROP TABLE IF EXISTS audit_logs;
DROP TABLE IF EXISTS favorites;
DROP TABLE IF EXISTS room_status_updates;
DROP TABLE IF EXISTS bookings;
DROP TABLE IF EXISTS timetable;
DROP TABLE IF EXISTS classroom_facilities;
DROP TABLE IF EXISTS classrooms;
DROP TABLE IF EXISTS facilities;
DROP TABLE IF EXISTS buildings;
DROP TABLE IF EXISTS departments;
DROP TABLE IF EXISTS users;

-- Reset auto-increment counters (MySQL specific)
ALTER TABLE departments AUTO_INCREMENT = 1;
ALTER TABLE users AUTO_INCREMENT = 1;
ALTER TABLE buildings AUTO_INCREMENT = 1;
ALTER TABLE classrooms AUTO_INCREMENT = 1;
ALTER TABLE facilities AUTO_INCREMENT = 1;
ALTER TABLE timetable AUTO_INCREMENT = 1;
ALTER TABLE bookings AUTO_INCREMENT = 1;
ALTER TABLE room_status_updates AUTO_INCREMENT = 1;
ALTER TABLE favorites AUTO_INCREMENT = 1;
ALTER TABLE audit_logs AUTO_INCREMENT = 1;

-- ============================================================
-- Note: After running this script, re-run database/seed.sql
-- to populate the database with sample data.
-- ============================================================
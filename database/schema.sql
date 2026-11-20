-- ============================================================
-- SmartClass 3D Database Schema
-- ==========================================================--
-- Database: smart_classroom_finder
-- ============================================================

-- Create database if not exists
-- CREATE DATABASE IF NOT EXISTS smart_classroom_finder;
-- USE smart_classroom_finder;

-- ============================================================
-- Table: departments
-- ============================================================
CREATE TABLE departments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(10) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    active TINYINT(1) DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_departments_code (code),
    INDEX idx_departments_active (active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- Table: users
-- ============================================================
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    college_id VARCHAR(20) NOT NULL UNIQUE,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'STUDENT',
    department_id INT,
    active TINYINT(1) DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_users_department FOREIGN KEY (department_id) REFERENCES departments(id) ON DELETE SET NULL,
    INDEX idx_users_email (email),
    INDEX idx_users_role (role),
    INDEX idx_users_active (active),
    INDEX idx_users_college_id (college_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- Table: buildings
-- ============================================================
CREATE TABLE buildings (
    id INT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    total_floors INT NOT NULL DEFAULT 1,
    coordinate_x DECIMAL(10,2) DEFAULT 0,
    coordinate_y DECIMAL(10,2) DEFAULT 0,
    coordinate_z DECIMAL(10,2) DEFAULT 0,
    active TINYINT(1) DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_buildings_code (code),
    INDEX idx_buildings_active (active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- Table: classrooms
-- ============================================================
CREATE TABLE classrooms (
    id INT AUTO_INCREMENT PRIMARY KEY,
    building_id INT NOT NULL,
    department_id INT,
    room_number VARCHAR(20) NOT NULL,
    room_name VARCHAR(100) NOT NULL,
    floor_number INT NOT NULL,
    capacity INT NOT NULL,
    room_type VARCHAR(50) NOT NULL,
    coordinate_x DECIMAL(10,2) DEFAULT 0,
    coordinate_y DECIMAL(10,2) DEFAULT 0,
    coordinate_z DECIMAL(10,2) DEFAULT 0,
    active TINYINT(1) DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_classrooms_building FOREIGN KEY (building_id) REFERENCES buildings(id) ON DELETE CASCADE,
    CONSTRAINT fk_classrooms_department FOREIGN KEY (department_id) REFERENCES departments(id) ON DELETE SET NULL,
    INDEX idx_classrooms_building (building_id),
    INDEX idx_classrooms_department (department_id),
    INDEX idx_classrooms_active (active),
    INDEX idx_classrooms_room_type (room_type),
    INDEX idx_classrooms_floor (floor_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- Table: facilities
-- ============================================================
CREATE TABLE facilities (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    icon VARCHAR(50),
    active TINYINT(1) DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_facilities_name (name),
    INDEX idx_facilities_active (active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- Table: classroom_facilities
-- ============================================================
CREATE TABLE classroom_facilities (
    classroom_id INT NOT NULL,
    facility_id INT NOT NULL,
    quantity INT DEFAULT 1,
    working TINYINT(1) DEFAULT 1,
    PRIMARY KEY (classroom_id, facility_id),
    CONSTRAINT fk_cf_classroom FOREIGN KEY (classroom_id) REFERENCES classrooms(id) ON DELETE CASCADE,
    CONSTRAINT fk_cf_facility FOREIGN KEY (facility_id) REFERENCES facilities(id) ON DELETE CASCADE,
    INDEX idx_cf_facility (facility_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- Table: timetable
-- ============================================================
CREATE TABLE timetable (
    id INT AUTO_INCREMENT PRIMARY KEY,
    classroom_id INT NOT NULL,
    lecturer_id INT,
    subject_code VARCHAR(20) NOT NULL,
    subject_name VARCHAR(100) NOT NULL,
    section_name VARCHAR(20),
    day_of_week VARCHAR(20) NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    valid_from DATE NOT NULL,
    valid_until DATE,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_timetable_classroom FOREIGN KEY (classroom_id) REFERENCES classrooms(id) ON DELETE CASCADE,
    CONSTRAINT fk_timetable_lecturer FOREIGN KEY (lecturer_id) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_timetable_classroom (classroom_id),
    INDEX idx_timetable_day (day_of_week),
    INDEX idx_timetable_valid_from (valid_from),
    INDEX idx_timetable_valid_until (valid_until),
    INDEX idx_timetable_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- Table: bookings
-- ============================================================
CREATE TABLE bookings (
    id INT AUTO_INCREMENT PRIMARY KEY,
    classroom_id INT NOT NULL,
    requested_by INT,
    purpose VARCHAR(100),
    start_datetime DATETIME NOT NULL,
    end_datetime DATETIME NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING',
    reviewed_by INT,
    review_note TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_bookings_classroom FOREIGN KEY (classroom_id) REFERENCES classrooms(id) ON DELETE CASCADE,
    CONSTRAINT fk_bookings_requested_by FOREIGN KEY (requested_by) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT fk_bookings_reviewed_by FOREIGN KEY (reviewed_by) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_bookings_classroom (classroom_id),
    INDEX idx_bookings_status (status),
    INDEX idx_bookings_datetime (start_datetime, end_datetime)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- Table: room_status_updates
-- ============================================================
CREATE TABLE room_status_updates (
    id INT AUTO_INCREMENT PRIMARY KEY,
    classroom_id INT NOT NULL,
    status VARCHAR(20) NOT NULL,
    source VARCHAR(20) NOT NULL, -- TIMETABLE, BOOKING, MANUAL, SENSOR
    note TEXT,
    effective_from DATETIME NOT NULL,
    effective_until DATETIME,
    updated_by INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_status_classroom FOREIGN KEY (classroom_id) REFERENCES classrooms(id) ON DELETE CASCADE,
    INDEX idx_status_classroom (classroom_id),
    INDEX idx_status_source (source),
    INDEX idx_status_effective_from (effective_from),
    INDEX idx_status_effective_until (effective_until)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- Table: favorites
-- ============================================================
CREATE TABLE favorites (
    user_id INT NOT NULL,
    classroom_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, classroom_id),
    CONSTRAINT fk_favorites_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_favorites_classroom FOREIGN KEY (classroom_id) REFERENCES classrooms(id) ON DELETE CASCADE,
    INDEX idx_favorites_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- Table: audit_logs
-- ============================================================
CREATE TABLE audit_logs (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    action VARCHAR(50) NOT NULL,
    entity_type VARCHAR(50) NOT NULL,
    entity_id INT NOT NULL,
    details TEXT,
    ip_address VARCHAR(45),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_audit_user (user_id),
    INDEX idx_audit_entity (entity_type, entity_id),
    INDEX idx_audit_created (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- View: classroom_status_view
-- ============================================================
CREATE VIEW classroom_status_view AS
SELECT 
    c.id AS classroom_id,
    c.room_number,
    c.room_name,
    b.name AS building_name,
    b.code AS building_code,
    c.floor_number,
    c.capacity,
    c.room_type,
    COALESCE(
        (SELECT sus.status 
         FROM room_status_updates sus 
         WHERE sus.classroom_id = c.id 
           AND sus.effective_from <= NOW() 
           AND (sus.effective_until IS NULL OR sus.effective_until > NOW())
         ORDER BY sus.effective_from DESC LIMIT 1),
        'AVAILABLE'
    ) AS current_status
FROM classrooms c
JOIN buildings b ON c.building_id = b.id;

-- ============================================================
-- View: classroom_availability_view
-- ============================================================
CREATE VIEW classroom_availability_view AS
SELECT 
    c.id AS classroom_id,
    c.room_number,
    c.room_name,
    b.name AS building_name,
    COALESCE(
        TIMESTAMPDIFF(
            MINUTE,
            NOW(),
            COALESCE(
                (SELECT MIN(bk.start_datetime) 
                 FROM bookings bk 
                 WHERE bk.classroom_id = c.id 
                   AND bk.status = 'APPROVED' 
                   AND bk.start_datetime > NOW()),
                (SELECT MIN(tt.valid_from) 
                 FROM timetable tt 
                 WHERE tt.classroom_id = c.id 
                   AND tt.status = 'ACTIVE' 
                   AND tt.valid_from > NOW())
            )
        ),
    0
) AS minutes_until_occupied
FROM classrooms c
JOIN buildings b ON c.building_id = b.id
WHERE c.active = 1;

-- ============================================================
-- Triggers
-- ============================================================

-- Trigger: Update classroom updated_at on status change
DELIMITER //
CREATE TRIGGER tr_classroom_updated_at
BEFORE UPDATE ON classrooms
FOR EACH ROW
BEGIN
    SET NEW.updated_at = NOW();
END //
DELIMITER ;

-- Trigger: Log user password changes
DELIMITER //
CREATE TRIGGER tr_users_updated_at
BEFORE UPDATE ON users
FOR EACH ROW
BEGIN
    SET NEW.updated_at = NOW();
    -- Audit log would be inserted here on password change
END //
DELIMITER ;

-- ============================================================
-- Indexes for Performance
-- ============================================================

-- Composite indexes for common queries
CREATE INDEX idx_bookings_classroom_status ON bookings(classroom_id, status);
CREATE INDEX idx_timetable_classroom_day ON timetable(classroom_id, day_of_week);
CREATE INDEX idx_room_status_classroom_effective ON room_status_updates(classroom_id, effective_from, effective_until);

-- Full-text search index on subject names
ALTER TABLE timetable ADD FULLTEXT INDEX idx_timetable_subject (subject_name, section_name);

-- ============================================================
-- Stored Procedures
-- ============================================================

-- Get classroom status with priority order
DELIMITER //
CREATE PROCEDURE sp_get_classroom_status(IN p_classroom_id INT, OUT p_status VARCHAR(20), OUT p_source VARCHAR(20))
proc_body: BEGIN
    DECLARE v_effective_from DATETIME;
    DECLARE v_effective_until DATETIME;
    
    -- 1. Check active manual override
    SELECT status, source, effective_from, effective_until 
    INTO p_status, p_source, v_effective_from, v_effective_until
    FROM room_status_updates
    WHERE classroom_id = p_classroom_id
      AND effective_from <= NOW()
      AND (effective_until IS NULL OR effective_until > NOW())
    ORDER BY effective_from DESC LIMIT 1;
    
    IF p_status IS NOT NULL THEN
        SET p_source = IFNULL(p_source, 'MANUAL');
        LEAVE proc_body;
    END IF;
    
    -- 2. Check maintenance
    SELECT status, source INTO p_status, p_source
    FROM room_status_updates
    WHERE classroom_id = p_classroom_id
      AND status = 'MAINTENANCE'
      AND effective_from <= NOW()
      AND (effective_until IS NULL OR effective_until > NOW())
    LIMIT 1;
    
    IF p_status IS NOT NULL THEN
        LEAVE proc_body;
    END IF;
    
    -- 3. Check approved booking
    SELECT b.status, 'BOOKING' INTO p_status, p_source
    FROM bookings b
    WHERE b.classroom_id = p_classroom_id
      AND b.status = 'APPROVED'
      AND b.start_datetime <= NOW()
      AND b.end_datetime > NOW()
    LIMIT 1;
    
    IF p_status IS NOT NULL THEN
        LEAVE proc_body;
    END IF;
    
    -- 4. Check timetable
    SELECT tt.status, 'TIMETABLE' INTO p_status, p_source
    FROM timetable tt
    WHERE tt.classroom_id = p_classroom_id
      AND tt.status = 'ACTIVE'
      AND tt.valid_from <= CURDATE()
      AND (tt.valid_until IS NULL OR tt.valid_until >= CURDATE())
    LIMIT 1;
    
    -- 5. Default to AVAILABLE
    IF p_status IS NULL THEN
        SET p_status = 'AVAILABLE';
        SET p_source = 'TIMETABLE';
    END IF;
END proc_body //
DELIMITER ;

-- Insert sample classroom status
DELIMITER //
CREATE PROCEDURE sp_insert_sample_status()
BEGIN
    -- Insert some sample status updates
    INSERT INTO room_status_updates (classroom_id, status, source, note, effective_from, effective_until)
    VALUES 
    (1, 'AVAILABLE', 'TIMETABLE', 'No scheduled classes', NOW(), NULL),
    (2, 'OCCUPIED', 'BOOKING', 'Lecture in progress', NOW(), DATE_ADD(NOW(), INTERVAL 2 HOUR)),
    (3, 'RESERVED', 'MANUAL', 'Seminar reserved', NOW(), DATE_ADD(NOW(), INTERVAL 1 DAY)),
    (4, 'MAINTENANCE', 'MANUAL', 'Under maintenance', NOW(), DATE_ADD(NOW(), INTERVAL 3 DAY));
END //
DELIMITER ;
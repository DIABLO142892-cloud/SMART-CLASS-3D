package com.smartclass.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class SmartClassService {
    private final JdbcTemplate jdbc;
    private final DataSource dataSource;
    private final ObjectMapper mapper;
    private final ZoneId zone;

    public SmartClassService(JdbcTemplate jdbc, DataSource dataSource, ObjectMapper mapper, ZoneId zone) {
        this.jdbc = jdbc;
        this.dataSource = dataSource;
        this.mapper = mapper;
        this.zone = zone;
    }

    public ObjectMapper mapper() { return mapper; }

    public UserSession authenticate(String email, String password, String expectedRole) {
        String normalizedEmail = Validation.required(email, "Email", 100).toLowerCase(Locale.ROOT);
        String role = Validation.role(expectedRole);
        List<Map<String, Object>> rows = jdbc.queryForList("""
            SELECT u.id, u.college_id, u.full_name, u.email, u.password_hash, u.role,
                   COALESCE(d.name, 'Campus Administration') department
            FROM users u LEFT JOIN departments d ON d.id=u.department_id
            WHERE LOWER(u.email)=? AND u.role=? AND u.active=1
            """, normalizedEmail, role);
        if (rows.size() != 1) return null;
        Map<String, Object> row = rows.get(0);
        if (!BCrypt.checkpw(password == null ? "" : password, String.valueOf(row.get("password_hash")))) return null;
        return new UserSession(number(row.get("id")), string(row.get("college_id")),
            string(row.get("full_name")), string(row.get("email")), string(row.get("role")),
            string(row.get("department")));
    }

    public Map<String, Object> bootstrap(UserSession user, String csrfToken) {
        try {
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("user", user);
            result.put("csrfToken", csrfToken);
            result.put("serverTime", LocalDateTime.now(zone).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            result.put("timezone", zone.getId());
            List<Map<String, Object>> rooms = rooms(user.id());
            result.put("rooms", rooms);
            result.put("buildings", clean(jdbc.queryForList("""
                SELECT id, code, name, description, total_floors totalFloors,
                       coordinate_x coordinateX, coordinate_y coordinateY, coordinate_z coordinateZ, active
                FROM buildings ORDER BY name
                """)));
            result.put("departments", clean(jdbc.queryForList("SELECT id, code, name, active FROM departments ORDER BY name")));
            result.put("facilities", clean(jdbc.queryForList("SELECT id, name, icon, active FROM facilities ORDER BY name")));
            result.put("bookings", bookings(user));
            result.put("timetable", clean(jdbc.queryForList("""
                SELECT t.id, t.classroom_id classroomId, c.room_number roomNumber,
                       t.lecturer_id lecturerId, COALESCE(u.full_name,'Unassigned') lecturerName,
                       t.subject_code subjectCode, t.subject_name subjectName, t.section_name sectionName,
                       t.day_of_week dayOfWeek, TIME_FORMAT(t.start_time,'%H:%i') startTime,
                       TIME_FORMAT(t.end_time,'%H:%i') endTime,
                       DATE_FORMAT(t.valid_from,'%Y-%m-%d') validFrom,
                       DATE_FORMAT(t.valid_until,'%Y-%m-%d') validUntil, t.status
                FROM timetable t JOIN classrooms c ON c.id=t.classroom_id
                LEFT JOIN users u ON u.id=t.lecturer_id ORDER BY FIELD(t.day_of_week,'Monday','Tuesday','Wednesday','Thursday','Friday','Saturday','Sunday'),t.start_time
                """)));
            if (user.hasRole("ADMIN")) {
                result.put("users", clean(jdbc.queryForList("""
                    SELECT u.id, u.college_id collegeId, u.full_name fullName, u.email, u.role,
                           u.department_id departmentId, COALESCE(d.code,'—') department, u.active
                    FROM users u LEFT JOIN departments d ON d.id=u.department_id ORDER BY u.role,u.full_name
                    """)));
                result.put("auditLogs", clean(jdbc.queryForList("""
                    SELECT a.id, COALESCE(u.full_name,'System') userName, a.action,
                           a.entity_type entityType, a.entity_id entityId, a.details,
                           a.ip_address ipAddress,
                           DATE_FORMAT(a.created_at,'%Y-%m-%dT%H:%i:%s') createdAt
                    FROM audit_logs a LEFT JOIN users u ON u.id=a.user_id
                    ORDER BY a.created_at DESC, a.id DESC LIMIT 50
                    """)));
            }
            long available = rooms.stream().filter(r -> "AVAILABLE".equals(r.get("status"))).count();
            Map<String, Object> stats = new LinkedHashMap<>();
            stats.put("total", rooms.size());
            stats.put("available", available);
            stats.put("occupied", rooms.stream().filter(r -> "OCCUPIED".equals(r.get("status"))).count());
            stats.put("reserved", rooms.stream().filter(r -> "RESERVED".equals(r.get("status"))).count());
            stats.put("maintenance", rooms.stream().filter(r -> "MAINTENANCE".equals(r.get("status"))).count());
            result.put("stats", stats);
            return result;
        } catch (DataAccessException ex) {
            throw new AppException(503, "Database is unavailable. Start MySQL and import schema.sql and seed.sql.");
        }
    }

    private List<Map<String, Object>> rooms(int userId) {
        return clean(jdbc.queryForList("""
            SELECT c.id, c.building_id buildingId, b.code buildingCode, b.name buildingName,
                   c.department_id departmentId, COALESCE(d.code,'GENERAL') department,
                   c.room_number roomNumber, c.room_name roomName, c.floor_number floorNumber,
                   c.capacity, c.room_type roomType, c.coordinate_x coordinateX,
                   c.coordinate_y coordinateY, c.coordinate_z coordinateZ, c.active,
                   GROUP_CONCAT(DISTINCT f.name ORDER BY f.name SEPARATOR ', ') facilities,
                   EXISTS(SELECT 1 FROM favorites fav WHERE fav.classroom_id=c.id AND fav.user_id=?) favorite,
                   CASE
                     WHEN EXISTS(SELECT 1 FROM room_status_updates s WHERE s.classroom_id=c.id AND s.source='MANUAL' AND s.status='MAINTENANCE' AND s.effective_from<=NOW() AND (s.effective_until IS NULL OR s.effective_until>NOW())) THEN 'MAINTENANCE'
                     WHEN EXISTS(SELECT 1 FROM room_status_updates s WHERE s.classroom_id=c.id AND s.source='MANUAL' AND s.status IN ('OCCUPIED','RESERVED') AND s.effective_from<=NOW() AND (s.effective_until IS NULL OR s.effective_until>NOW())) THEN
                       (SELECT s.status FROM room_status_updates s WHERE s.classroom_id=c.id AND s.source='MANUAL' AND s.status IN ('OCCUPIED','RESERVED') AND s.effective_from<=NOW() AND (s.effective_until IS NULL OR s.effective_until>NOW()) ORDER BY s.effective_from DESC,s.id DESC LIMIT 1)
                     WHEN EXISTS(SELECT 1 FROM bookings bk WHERE bk.classroom_id=c.id AND bk.status='APPROVED' AND bk.start_datetime<=NOW() AND bk.end_datetime>NOW()) THEN 'OCCUPIED'
                     WHEN EXISTS(SELECT 1 FROM timetable t WHERE t.classroom_id=c.id AND t.status='ACTIVE' AND UPPER(t.day_of_week)=UPPER(DAYNAME(NOW())) AND t.start_time<=CURTIME() AND t.end_time>CURTIME() AND t.valid_from<=CURDATE() AND (t.valid_until IS NULL OR t.valid_until>=CURDATE())) THEN 'OCCUPIED'
                     ELSE 'AVAILABLE' END status,
                   CASE
                     WHEN EXISTS(SELECT 1 FROM room_status_updates s WHERE s.classroom_id=c.id AND s.source='MANUAL' AND s.status='MAINTENANCE' AND s.effective_from<=NOW() AND (s.effective_until IS NULL OR s.effective_until>NOW())) THEN 'Manual maintenance update'
                     WHEN EXISTS(SELECT 1 FROM room_status_updates s WHERE s.classroom_id=c.id AND s.source='MANUAL' AND s.status IN ('OCCUPIED','RESERVED') AND s.effective_from<=NOW() AND (s.effective_until IS NULL OR s.effective_until>NOW())) THEN 'Lecturer manual update'
                     WHEN EXISTS(SELECT 1 FROM bookings bk WHERE bk.classroom_id=c.id AND bk.status='APPROVED' AND bk.start_datetime<=NOW() AND bk.end_datetime>NOW()) THEN 'Approved booking in progress'
                     WHEN EXISTS(SELECT 1 FROM timetable t WHERE t.classroom_id=c.id AND t.status='ACTIVE' AND UPPER(t.day_of_week)=UPPER(DAYNAME(NOW())) AND t.start_time<=CURTIME() AND t.end_time>CURTIME() AND t.valid_from<=CURDATE() AND (t.valid_until IS NULL OR t.valid_until>=CURDATE())) THEN 'Scheduled class in progress'
                     ELSE 'No active class or booking' END statusReason,
                   (SELECT DATE_FORMAT(MIN(bk.start_datetime),'%Y-%m-%dT%H:%i:%s') FROM bookings bk WHERE bk.classroom_id=c.id AND bk.status='APPROVED' AND bk.start_datetime>NOW()) availableUntil
            FROM classrooms c JOIN buildings b ON b.id=c.building_id
            LEFT JOIN departments d ON d.id=c.department_id
            LEFT JOIN classroom_facilities cf ON cf.classroom_id=c.id AND cf.working=1
            LEFT JOIN facilities f ON f.id=cf.facility_id AND f.active=1
            GROUP BY c.id,b.id,d.id ORDER BY b.name,c.floor_number,c.room_number
            """, userId));
    }

    private List<Map<String, Object>> bookings(UserSession user) {
        String where = user.hasRole("ADMIN") ? "" : " WHERE bk.requested_by=" + user.id();
        return clean(jdbc.queryForList("""
            SELECT bk.id, bk.classroom_id classroomId, c.room_number roomNumber, b.name buildingName,
                   bk.requested_by requestedBy, COALESCE(u.full_name,'Unknown') requestedByName,
                   bk.purpose, DATE_FORMAT(bk.start_datetime,'%Y-%m-%dT%H:%i:%s') startDateTime,
                   DATE_FORMAT(bk.end_datetime,'%Y-%m-%dT%H:%i:%s') endDateTime,
                   bk.status, bk.reviewed_by reviewedBy, bk.review_note reviewNote,
                   DATE_FORMAT(bk.created_at,'%Y-%m-%dT%H:%i:%s') createdAt
            FROM bookings bk JOIN classrooms c ON c.id=bk.classroom_id JOIN buildings b ON b.id=c.building_id
            LEFT JOIN users u ON u.id=bk.requested_by
            """ + where + " ORDER BY bk.created_at DESC"));
    }

    public int createBooking(UserSession user, Map<String, Object> input, String ip) {
        int roomId = Validation.positiveInt(input.get("classroomId"), "classroomId");
        String purpose = Validation.required(input.get("purpose"), "purpose", 100);
        LocalDateTime start = Validation.dateTime(input.get("startDateTime"), "startDateTime");
        LocalDateTime end = Validation.dateTime(input.get("endDateTime"), "endDateTime");
        if (!end.isAfter(start)) throw new AppException(400, "End time must be after start time");
        if (start.isBefore(LocalDateTime.now(zone).minusMinutes(1))) throw new AppException(400, "Booking must start in the future");
        if (java.time.Duration.between(start, end).toHours() > 8) throw new AppException(400, "Booking cannot exceed 8 hours");
        int id = insert("INSERT INTO bookings(classroom_id,requested_by,purpose,start_datetime,end_datetime,status) VALUES(?,?,?,?,?,'PENDING')",
            roomId, user.id(), purpose, Timestamp.valueOf(start), Timestamp.valueOf(end));
        audit(user.id(), "CREATE", "BOOKING", id, purpose, ip);
        return id;
    }

    public void reviewBooking(UserSession user, int bookingId, String action, Map<String, Object> input, String ip) {
        if (!user.hasRole("ADMIN")) throw new AppException(403, "Only an administrator can review bookings");
        String next = switch (action.toLowerCase(Locale.ROOT)) {
            case "approve" -> "APPROVED";
            case "reject" -> "REJECTED";
            default -> throw new AppException(404, "Unknown booking action");
        };
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            try {
                int roomId;
                Timestamp start;
                Timestamp end;
                try (PreparedStatement ps = connection.prepareStatement("SELECT classroom_id,start_datetime,end_datetime FROM bookings WHERE id=? AND status='PENDING' FOR UPDATE")) {
                    ps.setInt(1, bookingId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) throw new AppException(409, "Booking is not pending or no longer exists");
                        roomId = rs.getInt(1); start = rs.getTimestamp(2); end = rs.getTimestamp(3);
                    }
                }
                try (PreparedStatement lock = connection.prepareStatement("SELECT id FROM classrooms WHERE id=? FOR UPDATE")) {
                    lock.setInt(1, roomId); lock.executeQuery();
                }
                if ("APPROVED".equals(next) && hasConflict(connection, bookingId, roomId, start, end)) {
                    throw new AppException(409, "Cannot approve: this room has an overlapping class or approved booking");
                }
                try (PreparedStatement ps = connection.prepareStatement("UPDATE bookings SET status=?,reviewed_by=?,review_note=? WHERE id=?")) {
                    ps.setString(1, next); ps.setInt(2, user.id()); ps.setString(3, string(input.get("reviewNote"))); ps.setInt(4, bookingId);
                    ps.executeUpdate();
                }
                connection.commit();
                audit(user.id(), next, "BOOKING", bookingId, string(input.get("reviewNote")), ip);
            } catch (RuntimeException | SQLException ex) {
                connection.rollback();
                if (ex instanceof AppException app) throw app;
                throw ex;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException ex) {
            throw new AppException(500, "Could not review booking");
        }
    }

    private boolean hasConflict(Connection connection, int bookingId, int roomId, Timestamp start, Timestamp end) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement("""
            SELECT EXISTS(SELECT 1 FROM bookings WHERE classroom_id=? AND id<>? AND status='APPROVED' AND start_datetime<? AND end_datetime>?)
                 OR EXISTS(SELECT 1 FROM timetable WHERE classroom_id=? AND status='ACTIVE' AND UPPER(day_of_week)=UPPER(DAYNAME(?))
                    AND valid_from<=DATE(?) AND (valid_until IS NULL OR valid_until>=DATE(?)) AND start_time<TIME(?) AND end_time>TIME(?))
            """)) {
            ps.setInt(1, roomId); ps.setInt(2, bookingId); ps.setTimestamp(3, end); ps.setTimestamp(4, start);
            ps.setInt(5, roomId); ps.setTimestamp(6, start); ps.setTimestamp(7, start); ps.setTimestamp(8, start);
            ps.setTimestamp(9, end); ps.setTimestamp(10, start);
            try (ResultSet rs = ps.executeQuery()) { rs.next(); return rs.getBoolean(1); }
        }
    }

    public void cancelBooking(UserSession user, int bookingId, String ip) {
        int changed = jdbc.update("UPDATE bookings SET status='CANCELLED' WHERE id=? AND requested_by=? AND status IN ('PENDING','APPROVED') AND start_datetime>NOW()", bookingId, user.id());
        if (changed == 0) throw new AppException(409, "Only your future pending or approved booking can be cancelled");
        audit(user.id(), "CANCEL", "BOOKING", bookingId, "Cancelled by requester", ip);
    }

    public void updateRoomStatus(UserSession user, int roomId, Map<String, Object> input, String ip) {
        if (!(user.hasRole("LECTURER") || user.hasRole("ADMIN"))) throw new AppException(403, "Only lecturers or administrators can update room status");
        String status = Validation.roomStatus(input.get("status"));
        jdbc.update("UPDATE room_status_updates SET effective_until=NOW() WHERE classroom_id=? AND source='MANUAL' AND effective_from<=NOW() AND (effective_until IS NULL OR effective_until>NOW())", roomId);
        if (!"AVAILABLE".equals(status)) {
            int minutes = Math.min(1440, Math.max(5, input.get("durationMinutes") == null ? 60 : Validation.positiveInt(input.get("durationMinutes"), "durationMinutes")));
            jdbc.update("INSERT INTO room_status_updates(classroom_id,status,source,note,effective_from,effective_until,updated_by) VALUES(?,?, 'MANUAL',?,NOW(),DATE_ADD(NOW(),INTERVAL ? MINUTE),?)",
                roomId, status, string(input.get("note")), minutes, user.id());
        }
        audit(user.id(), "STATUS_" + status, "CLASSROOM", roomId, string(input.get("note")), ip);
    }

    public boolean toggleFavorite(UserSession user, int roomId) {
        int removed = jdbc.update("DELETE FROM favorites WHERE user_id=? AND classroom_id=?", user.id(), roomId);
        if (removed > 0) return false;
        jdbc.update("INSERT INTO favorites(user_id,classroom_id) VALUES(?,?)", user.id(), roomId);
        return true;
    }

    public int adminCreate(UserSession user, String resource, Map<String, Object> in, String ip) {
        requireAdmin(user);
        int id = switch (resource) {
            case "buildings" -> insert("INSERT INTO buildings(code,name,description,total_floors,coordinate_x,coordinate_z,active) VALUES(?,?,?,?,?,?,1)",
                Validation.required(in.get("code"),"code",20), Validation.required(in.get("name"),"name",100), string(in.get("description")),
                Validation.positiveInt(in.get("totalFloors"),"totalFloors"), decimal(in.get("coordinateX")), decimal(in.get("coordinateZ")));
            case "rooms" -> insert("INSERT INTO classrooms(building_id,department_id,room_number,room_name,floor_number,capacity,room_type,coordinate_x,coordinate_z,active) VALUES(?,?,?,?,?,?,?,?,?,1)",
                Validation.positiveInt(in.get("buildingId"),"buildingId"), nullableInt(in.get("departmentId")), Validation.required(in.get("roomNumber"),"roomNumber",20),
                Validation.required(in.get("roomName"),"roomName",100), Validation.positiveInt(in.get("floorNumber"),"floorNumber"), Validation.positiveInt(in.get("capacity"),"capacity"),
                Validation.required(in.get("roomType"),"roomType",50), decimal(in.get("coordinateX")), decimal(in.get("coordinateZ")));
            case "timetable" -> insert("INSERT INTO timetable(classroom_id,lecturer_id,subject_code,subject_name,section_name,day_of_week,start_time,end_time,valid_from,valid_until,status) VALUES(?,?,?,?,?,?,?,?,?,?,'ACTIVE')",
                Validation.positiveInt(in.get("classroomId"),"classroomId"), nullableInt(in.get("lecturerId")), Validation.required(in.get("subjectCode"),"subjectCode",20),
                Validation.required(in.get("subjectName"),"subjectName",100), string(in.get("sectionName")), Validation.required(in.get("dayOfWeek"),"dayOfWeek",20),
                Validation.required(in.get("startTime"),"startTime",8), Validation.required(in.get("endTime"),"endTime",8), Validation.required(in.get("validFrom"),"validFrom",10), nullableString(in.get("validUntil")));
            case "users" -> insert("INSERT INTO users(college_id,full_name,email,password_hash,role,department_id,active) VALUES(?,?,?,?,?,?,1)",
                Validation.required(in.get("collegeId"),"collegeId",20), Validation.required(in.get("fullName"),"fullName",100),
                Validation.required(in.get("email"),"email",100).toLowerCase(Locale.ROOT), BCrypt.hashpw(Validation.required(in.get("password"),"password",100), BCrypt.gensalt(10)),
                Validation.role(in.get("role")), nullableInt(in.get("departmentId")));
            default -> throw new AppException(404, "Unknown admin resource");
        };
        audit(user.id(), "CREATE", resource.toUpperCase(Locale.ROOT), id, "Created from admin dashboard", ip);
        return id;
    }

    public void adminUpdate(UserSession user, String resource, int id, Map<String, Object> in, String ip) {
        requireAdmin(user);
        int changed = switch (resource) {
            case "buildings" -> jdbc.update("UPDATE buildings SET code=?,name=?,description=?,total_floors=?,active=? WHERE id=?",
                Validation.required(in.get("code"),"code",20),Validation.required(in.get("name"),"name",100),string(in.get("description")),Validation.positiveInt(in.get("totalFloors"),"totalFloors"),bool(in.get("active")),id);
            case "rooms" -> jdbc.update("UPDATE classrooms SET building_id=?,department_id=?,room_number=?,room_name=?,floor_number=?,capacity=?,room_type=?,active=? WHERE id=?",
                Validation.positiveInt(in.get("buildingId"),"buildingId"),nullableInt(in.get("departmentId")),Validation.required(in.get("roomNumber"),"roomNumber",20),Validation.required(in.get("roomName"),"roomName",100),Validation.positiveInt(in.get("floorNumber"),"floorNumber"),Validation.positiveInt(in.get("capacity"),"capacity"),Validation.required(in.get("roomType"),"roomType",50),bool(in.get("active")),id);
            case "timetable" -> jdbc.update("UPDATE timetable SET classroom_id=?,lecturer_id=?,subject_code=?,subject_name=?,section_name=?,day_of_week=?,start_time=?,end_time=?,valid_from=?,valid_until=?,status=? WHERE id=?",
                Validation.positiveInt(in.get("classroomId"),"classroomId"),nullableInt(in.get("lecturerId")),Validation.required(in.get("subjectCode"),"subjectCode",20),Validation.required(in.get("subjectName"),"subjectName",100),string(in.get("sectionName")),Validation.required(in.get("dayOfWeek"),"dayOfWeek",20),Validation.required(in.get("startTime"),"startTime",8),Validation.required(in.get("endTime"),"endTime",8),Validation.required(in.get("validFrom"),"validFrom",10),nullableString(in.get("validUntil")),Validation.required(in.get("status"),"status",20),id);
            case "users" -> updateUser(id, in);
            default -> throw new AppException(404, "Unknown admin resource");
        };
        if (changed == 0) throw new AppException(404, "Record not found");
        audit(user.id(), "UPDATE", resource.toUpperCase(Locale.ROOT), id, "Updated from admin dashboard", ip);
    }

    private int updateUser(int id, Map<String, Object> in) {
        String password = string(in.get("password"));
        if (password.isBlank()) return jdbc.update("UPDATE users SET college_id=?,full_name=?,email=?,role=?,department_id=?,active=? WHERE id=?",
            Validation.required(in.get("collegeId"),"collegeId",20),Validation.required(in.get("fullName"),"fullName",100),Validation.required(in.get("email"),"email",100).toLowerCase(Locale.ROOT),Validation.role(in.get("role")),nullableInt(in.get("departmentId")),bool(in.get("active")),id);
        return jdbc.update("UPDATE users SET college_id=?,full_name=?,email=?,password_hash=?,role=?,department_id=?,active=? WHERE id=?",
            Validation.required(in.get("collegeId"),"collegeId",20),Validation.required(in.get("fullName"),"fullName",100),Validation.required(in.get("email"),"email",100).toLowerCase(Locale.ROOT),BCrypt.hashpw(password,BCrypt.gensalt(10)),Validation.role(in.get("role")),nullableInt(in.get("departmentId")),bool(in.get("active")),id);
    }

    public void adminDelete(UserSession user, String resource, int id, String ip) {
        requireAdmin(user);
        if (resource.equals("users") && id == user.id()) throw new AppException(409, "You cannot deactivate your own account");
        String sql = switch (resource) {
            case "buildings" -> "UPDATE buildings SET active=0 WHERE id=?";
            case "rooms" -> "UPDATE classrooms SET active=0 WHERE id=?";
            case "timetable" -> "UPDATE timetable SET status='INACTIVE' WHERE id=?";
            case "users" -> "UPDATE users SET active=0 WHERE id=?";
            default -> throw new AppException(404, "Unknown admin resource");
        };
        if (jdbc.update(sql,id)==0) throw new AppException(404,"Record not found");
        audit(user.id(), "DEACTIVATE", resource.toUpperCase(Locale.ROOT), id, "Deactivated from admin dashboard", ip);
    }

    private void requireAdmin(UserSession user) {
        if (!user.hasRole("ADMIN")) throw new AppException(403,"Administrator access required");
    }

    private int insert(String sql, Object... args) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            for (int i=0;i<args.length;i++) ps.setObject(i+1,args[i]);
            return ps;
        }, keyHolder);
        Number key = keyHolder.getKey();
        if (key == null) throw new AppException(500,"Database did not return the new record id");
        return key.intValue();
    }

    private void audit(int userId, String action, String entity, int entityId, String details, String ip) {
        jdbc.update("INSERT INTO audit_logs(user_id,action,entity_type,entity_id,details,ip_address) VALUES(?,?,?,?,?,?)", userId,action,entity,entityId,details,ip);
    }

    private static List<Map<String, Object>> clean(List<Map<String, Object>> rows) {
        List<Map<String,Object>> result=new ArrayList<>();
        for (Map<String,Object> row:rows) {
            Map<String,Object> copy=new LinkedHashMap<>();
            row.forEach((k,v)->copy.put(k, sqlValue(v)));
            result.add(copy);
        }
        return result;
    }

    private static Object sqlValue(Object value) {
        if (value instanceof Timestamp t) return t.toLocalDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        if (value instanceof java.sql.Date d) return d.toLocalDate().toString();
        if (value instanceof Time t) return t.toLocalTime().toString();
        return value;
    }
    private static int number(Object value) { return ((Number)value).intValue(); }
    private static String string(Object value) { return value==null?"":value.toString(); }
    private static String nullableString(Object value) { String v=string(value).trim(); return v.isEmpty()?null:v; }
    private static Integer nullableInt(Object value) { String v=string(value).trim(); return v.isEmpty()?null:Integer.valueOf(v); }
    private static double decimal(Object value) { String v=string(value).trim(); return v.isEmpty()?0:Double.parseDouble(v); }
    private static int bool(Object value) { return value==null || Boolean.parseBoolean(value.toString()) || "1".equals(value.toString()) ? 1:0; }
}

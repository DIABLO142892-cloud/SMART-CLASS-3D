from __future__ import annotations

from pathlib import Path

from reportlab.lib import colors
from reportlab.lib.enums import TA_CENTER, TA_LEFT
from reportlab.lib.pagesizes import A4
from reportlab.lib.styles import ParagraphStyle, getSampleStyleSheet
from reportlab.lib.units import mm
from reportlab.platypus import (
    HRFlowable,
    Image,
    KeepTogether,
    PageBreak,
    Paragraph,
    SimpleDocTemplate,
    Spacer,
    Table,
    TableStyle,
)


ROOT = Path(__file__).resolve().parents[1]
OUTPUT = ROOT / "output" / "pdf" / "92500119195_IRFAN_SHAIK.pdf"
SCREENSHOTS = ROOT / "docs" / "screenshots"

INK = colors.HexColor("#0B2F2A")
GREEN = colors.HexColor("#19C98A")
PALE = colors.HexColor("#E8F7F1")
CREAM = colors.HexColor("#F7F7F2")
MUTED = colors.HexColor("#5D6C68")
GOLD = colors.HexColor("#EAB84D")
RED = colors.HexColor("#EA6A5E")
LINE = colors.HexColor("#D7E1DD")


styles = getSampleStyleSheet()
styles.add(ParagraphStyle(
    name="ReportTitle", parent=styles["Title"], fontName="Helvetica-Bold",
    fontSize=27, leading=32, textColor=INK, alignment=TA_CENTER, spaceAfter=12,
))
styles.add(ParagraphStyle(
    name="ReportSubtitle", parent=styles["Normal"], fontName="Helvetica",
    fontSize=13, leading=19, textColor=MUTED, alignment=TA_CENTER,
))
styles.add(ParagraphStyle(
    name="Section", parent=styles["Heading1"], fontName="Helvetica-Bold",
    fontSize=18, leading=22, textColor=INK, spaceBefore=2, spaceAfter=10,
))
styles.add(ParagraphStyle(
    name="Subsection", parent=styles["Heading2"], fontName="Helvetica-Bold",
    fontSize=11.5, leading=14, textColor=INK, spaceBefore=8, spaceAfter=5,
))
styles.add(ParagraphStyle(
    name="BodyReport", parent=styles["BodyText"], fontName="Helvetica",
    fontSize=9.2, leading=13.2, textColor=colors.HexColor("#213632"), spaceAfter=6,
))
styles.add(ParagraphStyle(
    name="BulletReport", parent=styles["BodyText"], fontName="Helvetica",
    fontSize=9.0, leading=12.5, textColor=colors.HexColor("#213632"),
    leftIndent=11, firstLineIndent=-7, bulletIndent=2, spaceAfter=3,
))
styles.add(ParagraphStyle(
    name="Small", parent=styles["BodyText"], fontName="Helvetica",
    fontSize=7.8, leading=10.5, textColor=MUTED,
))
styles.add(ParagraphStyle(
    name="Caption", parent=styles["BodyText"], fontName="Helvetica-Oblique",
    fontSize=7.5, leading=10, textColor=MUTED, alignment=TA_CENTER, spaceBefore=3,
))
styles.add(ParagraphStyle(
    name="Callout", parent=styles["BodyText"], fontName="Helvetica-Bold",
    fontSize=10.5, leading=14, textColor=INK, alignment=TA_CENTER,
))
styles.add(ParagraphStyle(
    name="TableHead", parent=styles["BodyText"], fontName="Helvetica-Bold",
    fontSize=8.0, leading=10, textColor=colors.white,
))
styles.add(ParagraphStyle(
    name="TableCell", parent=styles["BodyText"], fontName="Helvetica",
    fontSize=7.6, leading=10.2, textColor=colors.HexColor("#213632"),
))


def P(text: str, style: str = "BodyReport") -> Paragraph:
    return Paragraph(text, styles[style])


def bullet(text: str) -> Paragraph:
    return Paragraph(f"- {text}", styles["BulletReport"])


def table(rows, widths, header=True, font_size=7.6):
    converted = []
    for row_index, row in enumerate(rows):
        style = "TableHead" if header and row_index == 0 else "TableCell"
        converted.append([cell if hasattr(cell, "wrap") else P(str(cell), style) for cell in row])
    t = Table(converted, colWidths=widths, repeatRows=1 if header else 0, hAlign="LEFT")
    commands = [
        ("VALIGN", (0, 0), (-1, -1), "TOP"),
        ("GRID", (0, 0), (-1, -1), 0.35, LINE),
        ("LEFTPADDING", (0, 0), (-1, -1), 6),
        ("RIGHTPADDING", (0, 0), (-1, -1), 6),
        ("TOPPADDING", (0, 0), (-1, -1), 5),
        ("BOTTOMPADDING", (0, 0), (-1, -1), 5),
    ]
    if header:
        commands += [
            ("BACKGROUND", (0, 0), (-1, 0), INK),
            ("ROWBACKGROUNDS", (0, 1), (-1, -1), [colors.white, CREAM]),
        ]
    else:
        commands.append(("ROWBACKGROUNDS", (0, 0), (-1, -1), [colors.white, CREAM]))
    t.setStyle(TableStyle(commands))
    return t


def stat_cards(items):
    data = [[P(f"<font size='16'><b>{value}</b></font><br/><font color='#5D6C68'>{label}</font>", "Callout")
             for label, value in items]]
    t = Table(data, colWidths=[(A4[0] - 36 * mm) / len(items)] * len(items), hAlign="LEFT")
    t.setStyle(TableStyle([
        ("BACKGROUND", (0, 0), (-1, -1), PALE),
        ("BOX", (0, 0), (-1, -1), 0.6, GREEN),
        ("INNERGRID", (0, 0), (-1, -1), 0.4, colors.white),
        ("VALIGN", (0, 0), (-1, -1), "MIDDLE"),
        ("TOPPADDING", (0, 0), (-1, -1), 10),
        ("BOTTOMPADDING", (0, 0), (-1, -1), 10),
    ]))
    return t


def screenshot(name: str, caption: str, max_height=92 * mm):
    path = SCREENSHOTS / name
    if not path.exists():
        return [P(f"Screenshot unavailable: {name}", "Caption")]
    img = Image(str(path))
    max_width = A4[0] - 36 * mm
    scale = min(max_width / img.imageWidth, max_height / img.imageHeight)
    img.drawWidth = img.imageWidth * scale
    img.drawHeight = img.imageHeight * scale
    img.hAlign = "CENTER"
    return [img, P(caption, "Caption")]


def header_footer(canvas, doc):
    canvas.saveState()
    if doc.page > 1:
        canvas.setStrokeColor(LINE)
        canvas.setLineWidth(0.5)
        canvas.line(18 * mm, A4[1] - 14 * mm, A4[0] - 18 * mm, A4[1] - 14 * mm)
        canvas.setFont("Helvetica-Bold", 7.8)
        canvas.setFillColor(INK)
        canvas.drawString(18 * mm, A4[1] - 10.5 * mm, "SMARTCLASS 3D")
        canvas.setFont("Helvetica", 7.4)
        canvas.setFillColor(MUTED)
        canvas.drawRightString(A4[0] - 18 * mm, A4[1] - 10.5 * mm, "AJT 01CE0308 | Project Report")
        canvas.line(18 * mm, 13 * mm, A4[0] - 18 * mm, 13 * mm)
        canvas.drawString(18 * mm, 8.5 * mm, "Smart Empty Classroom Finder in the Campus")
        canvas.drawRightString(A4[0] - 18 * mm, 8.5 * mm, f"Page {doc.page}")
    canvas.restoreState()


def section_title(number: str, title: str):
    return [P(f"{number}. {title}", "Section"), HRFlowable(width="100%", thickness=1.2, color=GREEN, spaceAfter=10)]


story = []

# Cover
story += [
    Spacer(1, 25 * mm),
    P("ADVANCED JAVA TECHNOLOGY", "ReportSubtitle"),
    Spacer(1, 7 * mm),
    P("SmartClass 3D", "ReportTitle"),
    P("Smart Empty Classroom Finder in the Campus", "ReportSubtitle"),
    Spacer(1, 12 * mm),
    HRFlowable(width="68%", thickness=3, color=GREEN, hAlign="CENTER"),
    Spacer(1, 15 * mm),
    P("A database-connected, role-based campus operations website with live classroom availability and an interactive 3D-style view.", "Callout"),
    Spacer(1, 20 * mm),
    table([
        ["Student", "Enrollment number"],
        ["IRFAN SHAIK", "92500119195"],
        ["SHAIK MD FAROOQ", "92500119161"],
        ["P.RUSHIK", "92500119183"],
    ], [95 * mm, 55 * mm]),
    Spacer(1, 17 * mm),
    P("Subject: AJT - 01CE0308", "Callout"),
    P("Faculty of Engineering and Technology | Computer Engineering | 2026", "ReportSubtitle"),
    Spacer(1, 18 * mm),
    P("FINAL VERIFIED PROJECT REPORT", "Callout"),
    PageBreak(),
]

# Executive summary
story += section_title("1", "Executive summary")
story += [
    P("SmartClass 3D solves a practical campus problem: students and lecturers should not need to walk from room to room to discover whether a classroom is free. The system combines timetable rows, approved bookings, and time-limited lecturer overrides to calculate a live state for every classroom. It exposes that result through a polished dashboard, a searchable finder, and a lightweight interactive campus visualization."),
    P("The delivered system is a complete Java web application rather than a static prototype. Authentication, role permissions, CRUD operations, booking approval, validation, audit history, and all displayed room data are connected to MariaDB through Spring JDBC. The application is packaged as a WAR and has been exercised on Apache Tomcat 10.1 with Java 17."),
    Spacer(1, 4 * mm),
    stat_cards([("DATABASE TABLES", "11"), ("CAMPUS ROOMS", "16"), ("USER ROLES", "3"), ("JUNIT TESTS", "15")]),
    Spacer(1, 8 * mm),
    P("Project objectives", "Subsection"),
    bullet("Display current, database-derived availability with a clear reason for every room."),
    bullet("Provide separate student, lecturer, and administrator workflows."),
    bullet("Prevent invalid and overlapping reservations through validated transactions."),
    bullet("Demonstrate Servlets, JSP, JSTL/EL, sessions, Spring DI, JDBC, layered design, and exception handling."),
    bullet("Deliver reproducible source, database scripts, setup instructions, evidence, and a Tomcat WAR."),
    Spacer(1, 5 * mm),
    P("Verification result", "Subsection"),
    P("The final browser QA loaded all 16 rooms for all three roles with zero console or request failures. The API verification passed authentication, authorization denial, favorites, booking create/approve/cancel, CSRF rejection, lecturer status persistence/release, administrator CRUD, refresh, and audit logging."),
    PageBreak(),
]

# Requirements
story += section_title("2", "Problem definition and requirements")
story += [
    P("Campus timetable data alone cannot describe unexpected sessions, project meetings, cancellations, or maintenance. Manual inquiry is slow, room status becomes inconsistent, and two groups may try to use the same space. The system therefore needs one authoritative database and carefully separated permissions."),
    table([
        ["Role", "Required capabilities", "Authority boundary"],
        ["Student", "Login, dashboard, live finder, filters, room details, favorites, create and cancel booking requests.", "Cannot approve bookings, change room status, or manage campus data."],
        ["Lecturer", "All finder functions plus mark occupied/reserved/maintenance for a duration and release a room.", "Cannot administer users or approve student requests."],
        ["Administrator", "All operational views, approve/reject bookings, users/buildings/rooms/timetable CRUD, audit log.", "Protected by both route filters and service-level role checks."],
    ], [27 * mm, 84 * mm, 63 * mm]),
    Spacer(1, 7 * mm),
    P("Non-functional requirements", "Subsection"),
    bullet("Responsive current-browser interface with a non-3D list fallback."),
    bullet("BCrypt passwords, protected sessions, CSRF tokens, security headers, and parameterized SQL."),
    bullet("Controlled validation and error responses for database, authorization, and conflict failures."),
    bullet("Reproducible Maven WAR targeting Java 17 and Jakarta Servlet 6 / Tomcat 10.1."),
    P("Scope", "Subsection"),
    P("Room state is derived from institutional data and lecturer input. Physical IoT occupancy sensors are intentionally outside this AJT academic scope, but the API and status model can accept that source later."),
    PageBreak(),
]

# Technology and architecture
story += section_title("3", "Technology stack and layered architecture")
story += [
    table([
        ["Layer", "Technology", "Responsibility"],
        ["Presentation", "JSP 3.1, JSTL/EL, HTML, custom CSS", "Secure server-rendered login/dashboard shell and responsive views."],
        ["Browser behavior", "Vanilla JavaScript and Canvas 2D", "10-second live polling, filtering, modal workflows, and data-driven campus perspective."],
        ["Web", "Jakarta Servlet 6 and filters", "Login, dashboard, logout, REST-style API, session gate, security headers."],
        ["Business", "Spring Context and SmartClassService", "Dependency injection, role rules, validation, status algorithm, transactions, auditing."],
        ["Persistence", "Spring JDBC, HikariCP, MySQL driver", "Prepared statements, pooled connections, explicit SQL, transactional approval."],
        ["Database/runtime", "MariaDB 10.4, XAMPP, Tomcat 10.1, Java 17", "Durable campus data and WAR deployment."],
        ["Quality", "Maven and JUnit 5", "Compilation, automated tests, repeatable packaging."],
    ], [29 * mm, 53 * mm, 92 * mm]),
    Spacer(1, 8 * mm),
    P("Request flow", "Subsection"),
    table([
        [P("Browser", "Callout"), P("Servlet / Filter", "Callout"), P("Spring Service", "Callout"), P("MariaDB", "Callout")],
        [P("JSP shell + app.js", "Small"), P("Session, CSRF, JSON", "Small"), P("Rules + transactions", "Small"), P("11 related tables", "Small")],
    ], [43.5 * mm] * 4, header=False),
    Spacer(1, 5 * mm),
    P("After login, the browser requests <b>/api/bootstrap</b> immediately and every 10 seconds. The response includes the authenticated user, room states, statistics, buildings, bookings, timetable data, and a CSRF token; administrators additionally receive users and audit entries. This avoids full-page reloads while retaining a server-controlled security model."),
    PageBreak(),
]

# Database
story += section_title("4", "Database design and JDBC implementation")
story += [
    P("The schema contains 11 related InnoDB tables, two views, two stored procedures, and two triggers. Foreign keys enforce relationships, unique keys prevent duplicate identities and mappings, and indexes target active-state, role, day/time, and booking interval queries."),
    table([
        ["Table", "Purpose", "Key relationships"],
        ["departments", "Academic department master", "Referenced by users and classrooms"],
        ["users", "Identity, BCrypt hash, role, active flag", "department_id -> departments"],
        ["buildings", "Campus location and 3D coordinates", "Parent of classrooms"],
        ["classrooms", "Room, floor, capacity, type, coordinates", "building_id; department_id"],
        ["facilities / classroom_facilities", "Facility catalog and joined to rooms", "Many-to-many bridge"],
        ["timetable", "Weekly/date-bound scheduled sessions", "classroom_id; lecturer_id"],
        ["bookings", "Student/lecturer reservations and decision", "classroom, requester, approver"],
        ["room_status_updates", "Time-limited manual overrides", "classroom_id; updated_by"],
        ["favorites", "User-room shortcut", "Unique user_id + classroom_id"],
        ["audit_logs", "Security and data-change history", "user_id + entity metadata"],
    ], [40 * mm, 72 * mm, 62 * mm]),
    Spacer(1, 6 * mm),
    P("JDBC behavior", "Subsection"),
    P("Spring <b>JdbcTemplate</b> executes parameterized statements through a HikariCP DataSource. Login reads the users table; room cards join buildings, rooms, facilities, timetable, bookings, overrides, and favorites. Administrative changes update the appropriate entity and write an audit record. Database errors are translated into controlled application exceptions rather than exposed as raw SQL failures."),
    PageBreak(),
]

# Algorithms
story += section_title("5", "Live status and booking transaction")
story += [
    P("Live availability uses one deterministic priority order. This makes the same room color and reason appear in every dashboard and avoids disagreement between timetable, booking, and manual updates."),
    table([
        ["Priority", "Source", "Result"],
        ["1", "Active manual maintenance override", "MAINTENANCE"],
        ["2", "Most recent active manual occupied/reserved override", "OCCUPIED or RESERVED"],
        ["3", "Approved booking containing the current time", "RESERVED"],
        ["4", "Active timetable row matching date/day/time", "OCCUPIED"],
        ["5", "No higher-priority source", "AVAILABLE"],
    ], [22 * mm, 96 * mm, 56 * mm]),
    Spacer(1, 8 * mm),
    P("Booking validation", "Subsection"),
    bullet("The classroom must exist and be active; the purpose must be present."),
    bullet("The request must begin in the future, end after it starts, and last no more than eight hours."),
    bullet("Administrator approval uses SERIALIZABLE isolation, locks the pending request and room, checks approved-booking and timetable overlap, then commits."),
    bullet("A conflict returns HTTP 409 and the transaction rolls back; successful actions produce audit entries."),
    Spacer(1, 5 * mm),
    P("Real-time behavior", "Subsection"),
    P("Students see decisions on the next refresh. Lecturer overrides have an explicit duration and can be released. The same database calculation feeds statistics, room cards, details, and building colors in the campus visualization."),
    PageBreak(),
]

# Security and AJT
story += section_title("6", "Security, validation, and AJT concepts")
story += [
    table([
        ["Control", "Implementation"],
        ["Password protection", "BCrypt hashes in MariaDB; plaintext credentials are never stored."],
        ["Session fixation defense", "The previous session is invalidated before the authenticated session is created."],
        ["Role authorization", "AuthenticationFilter protects routes; service methods repeat role checks for mutations."],
        ["CSRF", "Per-session token is required on login and every state-changing API call."],
        ["Cookie and browser security", "HttpOnly, SameSite=Lax, Content Security Policy, and additional security headers."],
        ["Injection resistance", "JdbcTemplate placeholders and PreparedStatement values for all external input."],
        ["Validation", "Server checks strings, IDs, roles, statuses, dates, durations, ownership, and overlap."],
        ["Safe display", "JSTL output escaping is used for user-controlled JSP values."],
    ], [47 * mm, 127 * mm]),
    Spacer(1, 8 * mm),
    P("AJT evidence", "Subsection"),
    table([
        ["Rubric concept", "Project evidence"],
        ["Servlet", "LoginServlet, DashboardServlet, LogoutServlet, and ApiServlet."],
        ["JSP / JSTL / EL", "Role-aware login and dashboard views with JSTL core/functions and EL."],
        ["Session management", "UserSession, timeout, CSRF, logout invalidation, and protected routes."],
        ["Spring", "Java configuration, dependency injection, lifecycle-managed DataSource and service."],
        ["Layered architecture", "Presentation -> web -> business -> persistence -> MariaDB."],
        ["Exception handling", "Validation, authorization, conflict, not-found, and database responses."],
    ], [50 * mm, 124 * mm]),
    PageBreak(),
]

# Screenshots 1
story += section_title("7", "User interface evidence")
story += [P("The interface uses a consistent green campus-operations design, clear role identity, responsive cards, accessible labels, and live status color semantics.")]
story += screenshot("01-login.png", "Figure 1. Role-aware login page and demo-account guidance.", 80 * mm)
story += [Spacer(1, 6 * mm)]
story += screenshot("02-student-dashboard.png", "Figure 2. Student live overview with statistics, best options, and interactive campus visualization.", 80 * mm)
story += [PageBreak()]

# Screenshots 2
story += section_title("8", "Role workflows and finder evidence")
story += screenshot("03-classroom-finder.png", "Figure 3. Search and filter result for A-101, loaded from MariaDB.", 78 * mm)
story += [Spacer(1, 5 * mm)]
story += screenshot("04-lecturer-dashboard.png", "Figure 4. Lecturer status-control experience for live room operations.", 78 * mm)
story += [PageBreak()]

# Admin screenshot
story += section_title("9", "Administration and audit evidence")
story += screenshot("05-admin-dashboard.png", "Figure 5. Administrator control centre with protected audit-log data.", 108 * mm)
story += [
    Spacer(1, 6 * mm),
    P("Administrative tabs provide rooms, buildings, timetable, users, and audit data. Create/update/deactivate operations are role-protected and leave an attributable audit record. Booking decisions use the same administration role and transactional conflict checks."),
    PageBreak(),
]

# Testing
story += section_title("10", "Testing and verified results")
story += [
    stat_cards([("JUNIT", "15/15"), ("BROWSER ROLES", "3/3"), ("LIVE ROOMS", "16"), ("RUNTIME CHECK", "PASS")]),
    Spacer(1, 8 * mm),
    table([
        ["Test area", "Evidence", "Result"],
        ["Compilation and unit tests", "Java 17 Maven package; validation and session suites", "15 passed; 0 failures"],
        ["Student workflow", "Login, room bootstrap, favorite toggle, create/cancel booking", "PASS"],
        ["Lecturer workflow", "Login, manual maintenance status persists, release restores state", "PASS"],
        ["Administrator workflow", "Login, approve request, create/update/deactivate entity, audit", "PASS"],
        ["Authorization", "Student request to admin API", "HTTP 403"],
        ["CSRF", "Mutation without a valid token", "HTTP 403"],
        ["Database", "Schema and seed imports; 11 tables, 2 views, 2 procedures", "PASS"],
        ["Browser QA", "Chrome: login, student, lecturer, admin; 16 rooms each", "No console/request errors"],
        ["Deployment", "Tomcat 10.1.59, Temurin JDK 17, MariaDB 10.4", "HTTP 200 login"],
    ], [41 * mm, 96 * mm, 37 * mm]),
    Spacer(1, 8 * mm),
    P("The automated runtime verifier also exercised booking approval under real database state, timetable/status refresh, role denial, CSRF rejection, and audit generation. The database was reset to the clean seed after testing so the submission starts with predictable demo content."),
    PageBreak(),
]

# Setup
story += section_title("11", "Deployment and demonstration guide")
story += [
    P("Prerequisites: Windows with Java 17, Apache Maven 3.9+, XAMPP MariaDB/MySQL, and Apache Tomcat 10.1."),
    table([
        ["Step", "Action"],
        ["1", "Start MariaDB/MySQL. Create smart_classroom_finder and import database/schema.sql, then database/seed.sql."],
        ["2", "From the Maven project, run mvn clean package using JDK 17."],
        ["3", "Copy target/smart-classroom-finder-1.0.0-SNAPSHOT.war to Tomcat webapps as smartclass3d.war."],
        ["4", "Set DB_URL, DB_USER, DB_PASSWORD, and APP_TIMEZONE when non-default database settings are used."],
        ["5", "Start Tomcat and open http://localhost:8080/smartclass3d/login."],
        ["6", "Demonstrate all three roles, finder filters, booking workflow, lecturer status, admin CRUD, and audit log."],
    ], [18 * mm, 156 * mm]),
    Spacer(1, 9 * mm),
    P("Demo credentials", "Subsection"),
    table([
        ["Role", "Email", "Password"],
        ["Administrator", "admin@smartclass.com", "Admin@123"],
        ["Lecturer", "lecturer@smartclass.com", "Lecturer@123"],
        ["Student", "student@smartclass.com", "Student@123"],
    ], [42 * mm, 82 * mm, 50 * mm]),
    Spacer(1, 8 * mm),
    P("Important: demonstration passwords are intentionally easy to present. Change them and use a dedicated non-root database account before any public deployment."),
    PageBreak(),
]

# Rubric
story += section_title("12", "Rubric compliance matrix")
story += [
    table([
        ["Rubric area", "Delivered evidence", "Status"],
        ["Problem / functional requirements", "Defined campus problem, objectives, role flows, and non-functional requirements.", "Complete"],
        ["Database design", "11 normalized related tables with keys, indexes, views, procedures, and triggers.", "Complete"],
        ["Connectivity", "HikariCP DataSource, Spring JdbcTemplate, MySQL driver, tested MariaDB connection.", "Complete"],
        ["CRUD", "Users, buildings, classrooms, timetable, bookings, favorites, and status operations.", "Complete"],
        ["Validation / exceptions", "Server validation, conflict detection, rollbacks, role checks, controlled HTTP responses.", "Complete"],
        ["Servlet + JSP/JSTL", "Jakarta servlets, filters, role-aware JSP, JSTL core/functions, EL, session handling.", "Complete"],
        ["Spring", "Java configuration and dependency-injected service/persistence components.", "Complete"],
        ["Layered architecture", "Presentation, web, business, persistence, and database responsibilities separated.", "Complete"],
        ["Functional result", "Live Tomcat runtime verification and Chrome QA for all roles.", "Complete"],
        ["Submission artifacts", "Source ZIP, README, SQL scripts, WAR, screenshots, and enrollment-named report.", "Complete"],
        ["GitHub/public hosting", "README and source are ready; final upload requires the students' account credentials.", "Team action"],
    ], [43 * mm, 105 * mm, 26 * mm]),
    Spacer(1, 8 * mm),
    P("The local deliverables satisfy the assessable technical requirements. Uploading to a GitHub account or external host is intentionally not performed without the owners' credentials and destination choice."),
    PageBreak(),
]

# Conclusion
story += section_title("13", "Conclusion and future scope")
story += [
    P("SmartClass 3D turns scattered campus scheduling information into a working resource-management system. It provides live, explainable availability; protects each role; prevents conflicting approvals; keeps a security audit; and demonstrates the required Advanced Java Technology concepts in a single deployable application."),
    P("The completed system is appropriate for classroom demonstration and assessment. Its source, database, WAR, automated checks, screenshots, setup guide, and report are packaged together so another evaluator can reproduce the result."),
    Spacer(1, 7 * mm),
    P("Future enhancements", "Subsection"),
    bullet("Connect physical occupancy or environmental sensors through the existing room-status model."),
    bullet("Use WebSocket or server-sent events for push updates instead of 10-second polling."),
    bullet("Add institutional single sign-on, notification delivery, analytics, and QR room check-in."),
    bullet("Deploy behind HTTPS with rotated secrets and a least-privilege database account."),
    Spacer(1, 10 * mm),
    P("Key viva points", "Subsection"),
    bullet("Why explicit Spring JDBC: it demonstrates SQL, prepared statements, joins, and transactions clearly for AJT."),
    bullet("Why priority-based status: multiple truthful sources can overlap, so one deterministic order is required."),
    bullet("Why role checks exist twice: filters protect routes while service checks protect business mutations."),
    bullet("Why approval is transactional: checking and updating must behave as one atomic unit to prevent double booking."),
    Spacer(1, 18 * mm),
    HRFlowable(width="70%", thickness=2, color=GREEN, hAlign="CENTER"),
    Spacer(1, 8 * mm),
    P("SMARTCLASS 3D - READY FOR DEMONSTRATION", "Callout"),
]


def build():
    OUTPUT.parent.mkdir(parents=True, exist_ok=True)
    doc = SimpleDocTemplate(
        str(OUTPUT), pagesize=A4,
        leftMargin=18 * mm, rightMargin=18 * mm,
        topMargin=19 * mm, bottomMargin=17 * mm,
        title="SmartClass 3D - Smart Empty Classroom Finder",
        author="Irfan Shaik, Shaik Md Farooq, P.Rushik",
        subject="Advanced Java Technology Project Report",
    )
    doc.build(story, onFirstPage=header_footer, onLaterPages=header_footer)
    print(OUTPUT)


if __name__ == "__main__":
    build()

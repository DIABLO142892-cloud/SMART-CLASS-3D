#!/usr/bin/env python3
"""End-to-end smoke test for the deployed SmartClass 3D application."""

from __future__ import annotations

import json
import re
import sys
from datetime import datetime, timedelta
from http.cookiejar import CookieJar
from urllib.error import HTTPError
from urllib.parse import urlencode
from urllib.request import HTTPCookieProcessor, Request, build_opener

BASE = "http://127.0.0.1:8080/smartclass3d"


class Client:
    def __init__(self) -> None:
        self.opener = build_opener(HTTPCookieProcessor(CookieJar()))
        self.csrf = ""

    def request(self, path: str, method: str = "GET", data=None, csrf: bool = True):
        headers = {}
        body = None
        if data is not None:
            body = json.dumps(data).encode()
            headers["Content-Type"] = "application/json"
        if csrf and self.csrf:
            headers["X-CSRF-Token"] = self.csrf
        request = Request(BASE + path, data=body, headers=headers, method=method)
        try:
            with self.opener.open(request, timeout=12) as response:
                raw = response.read().decode("utf-8")
                return response.status, response.geturl(), json.loads(raw) if raw else {}
        except HTTPError as error:
            raw = error.read().decode("utf-8")
            try:
                result = json.loads(raw)
            except json.JSONDecodeError:
                result = {"error": raw[:300]}
            return error.code, error.geturl(), result

    def login(self, role: str, email: str, password: str) -> dict:
        with self.opener.open(BASE + "/login", timeout=12) as response:
            html = response.read().decode("utf-8")
        match = re.search(r'name="_csrf"\s+value="([^"]+)"', html)
        assert match, "Login CSRF token was not rendered"
        form = urlencode({"_csrf": match.group(1), "role": role, "email": email, "password": password}).encode()
        request = Request(BASE + "/login", data=form, headers={"Content-Type": "application/x-www-form-urlencoded"})
        with self.opener.open(request, timeout=12) as response:
            assert response.geturl().endswith("/dashboard"), f"Login did not reach dashboard: {response.geturl()}"
        status, _, bootstrap = self.request("/api/bootstrap", csrf=False)
        assert status == 200, f"Bootstrap failed: {status} {bootstrap}"
        assert bootstrap["user"]["role"] == role
        assert len(bootstrap["rooms"]) >= 16
        self.csrf = bootstrap["csrfToken"]
        return bootstrap


def check(label: str, condition: bool, detail: str = "") -> None:
    if not condition:
        raise AssertionError(f"{label}: {detail}")
    print(f"PASS  {label}{': ' + detail if detail else ''}")


def next_sunday_night() -> tuple[str, str]:
    start = datetime.now().replace(hour=22, minute=0, second=0, microsecond=0)
    days = (6 - start.weekday()) % 7
    if days == 0 and start <= datetime.now() + timedelta(minutes=5):
        days = 7
    start += timedelta(days=days)
    end = start + timedelta(hours=1)
    return start.isoformat(timespec="seconds"), end.isoformat(timespec="seconds")


def main() -> int:
    student = Client()
    student_data = student.login("STUDENT", "student@smartclass.com", "Student@123")
    check("student login", True)
    check("database-backed rooms", len(student_data["rooms"]) >= 16, f"{len(student_data['rooms'])} rooms")
    check("three campus buildings", len(student_data["buildings"]) >= 3, f"{len(student_data['buildings'])} buildings")

    status, _, body = student.request("/api/admin/buildings", "POST", {"code": "NOPE"})
    check("student admin access denied", status == 403, f"HTTP {status}")
    status, _, body = student.request("/api/favorites/1", "POST", {})
    check("favorite toggle", status == 200 and "favorite" in body)
    status, _, body = student.request("/api/favorites/1", "POST", {})
    check("favorite toggle restore", status == 200 and "favorite" in body)

    start, end = next_sunday_night()
    room = next((item for item in student_data["rooms"] if not item.get("availableUntil")), student_data["rooms"][-1])
    room_id = int(room["id"])
    status, _, created = student.request("/api/bookings", "POST", {
        "classroomId": room_id,
        "purpose": "Automated end-to-end verification",
        "startDateTime": start,
        "endDateTime": end,
    })
    check("student booking creation", status == 200 and int(created.get("id", 0)) > 0, str(created))
    booking_id = int(created["id"])

    status, _, _ = student.request("/api/bookings", "POST", {
        "classroomId": room_id, "purpose": "Missing CSRF test", "startDateTime": start, "endDateTime": end
    }, csrf=False)
    check("CSRF protection", status == 403, f"HTTP {status}")

    lecturer = Client()
    lecturer_data = lecturer.login("LECTURER", "lecturer@smartclass.com", "Lecturer@123")
    check("lecturer login", lecturer_data["user"]["role"] == "LECTURER")
    status, _, _ = lecturer.request(f"/api/rooms/{room_id}/status", "POST", {
        "status": "MAINTENANCE", "durationMinutes": 5, "note": "Automated verification"
    })
    check("lecturer live status update", status == 200, f"HTTP {status}")
    status, _, refreshed = lecturer.request("/api/bootstrap", csrf=False)
    changed_room = next(room for room in refreshed["rooms"] if int(room["id"]) == room_id)
    check("live status persisted", changed_room["status"] == "MAINTENANCE", changed_room["status"])
    status, _, _ = lecturer.request(f"/api/rooms/{room_id}/status", "POST", {"status": "AVAILABLE"})
    check("live status release", status == 200, f"HTTP {status}")

    admin = Client()
    admin_data = admin.login("ADMIN", "admin@smartclass.com", "Admin@123")
    check("administrator login", admin_data["user"]["role"] == "ADMIN")
    status, _, _ = admin.request(f"/api/bookings/{booking_id}/approve", "POST", {"reviewNote": "Verified"})
    check("administrator booking approval", status == 200, f"HTTP {status}")

    code = "QA" + datetime.now().strftime("%H%M%S")
    status, _, created_building = admin.request("/api/admin/buildings", "POST", {
        "code": code, "name": "Runtime QA Building", "description": "Temporary CRUD check",
        "totalFloors": 2, "coordinateX": 25, "coordinateZ": 25
    })
    check("admin create", status == 200 and int(created_building.get("id", 0)) > 0, str(created_building))
    building_id = int(created_building["id"])
    status, _, _ = admin.request(f"/api/admin/buildings/{building_id}", "PUT", {
        "code": code, "name": "Runtime QA Building Updated", "description": "Update verified",
        "totalFloors": 3, "active": 1
    })
    check("admin update", status == 200, f"HTTP {status}")
    status, _, _ = admin.request(f"/api/admin/buildings/{building_id}", "DELETE", {})
    check("admin deactivate", status == 200, f"HTTP {status}")

    status, _, _ = student.request(f"/api/bookings/{booking_id}/cancel", "POST", {})
    check("student booking cancellation", status == 200, f"HTTP {status}")

    status, _, final_data = admin.request("/api/bootstrap", csrf=False)
    check("final dashboard refresh", status == 200 and len(final_data["rooms"]) >= 16)
    check("audit trail generated", len(final_data.get("auditLogs", [])) > 0, f"{len(final_data.get('auditLogs', []))} entries returned")
    print(f"PASS  verified booking id: {booking_id}")
    print("RESULT: SmartClass 3D runtime verification completed successfully.")
    return 0


if __name__ == "__main__":
    try:
        raise SystemExit(main())
    except Exception as exc:
        print(f"FAIL  {exc}", file=sys.stderr)
        raise

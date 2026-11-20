package com.smartclass.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartclass.app.AppException;
import com.smartclass.app.SmartClassService;
import com.smartclass.app.UserSession;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Map;

public final class WebSupport {
    public static final String USER = "currentUser";
    public static final String CSRF = "csrfToken";
    private static final SecureRandom RANDOM = new SecureRandom();

    private WebSupport() {}

    public static SmartClassService service(ServletContext context) {
        Object service = context.getAttribute("smartClassService");
        if (!(service instanceof SmartClassService smart)) throw new IllegalStateException("Application service was not initialized");
        return smart;
    }

    public static UserSession user(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session == null ? null : (UserSession) session.getAttribute(USER);
    }

    public static String newCsrf() {
        byte[] bytes = new byte[24]; RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    public static boolean validCsrf(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return false;
        Object stored = session.getAttribute(CSRF);
        if (!(stored instanceof String expected)) return false;
        String supplied = request.getHeader("X-CSRF-Token");
        if (supplied == null) supplied = request.getParameter("_csrf");
        return supplied != null && MessageDigest.isEqual(
                expected.getBytes(StandardCharsets.UTF_8),
                supplied.getBytes(StandardCharsets.UTF_8));
    }

    public static void json(HttpServletResponse response, ObjectMapper mapper, int status, Object body) throws IOException {
        response.setStatus(status);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        mapper.writeValue(response.getWriter(), body);
    }

    public static void error(HttpServletResponse response, ObjectMapper mapper, Throwable error) throws IOException {
        if (error instanceof AppException app) json(response, mapper, app.status(), Map.of("error", app.getMessage()));
        else json(response, mapper, 500, Map.of("error", "Unexpected server error"));
    }
}

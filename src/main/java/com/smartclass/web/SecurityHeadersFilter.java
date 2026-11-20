package com.smartclass.web;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SecurityHeadersFilter implements Filter {
    @Override public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse http=(HttpServletResponse) response;
        http.setHeader("X-Content-Type-Options","nosniff");
        http.setHeader("X-Frame-Options","DENY");
        http.setHeader("Referrer-Policy","same-origin");
        http.setHeader("Permissions-Policy","camera=(), microphone=(), geolocation=()");
        http.setHeader("Content-Security-Policy","default-src 'self'; script-src 'self'; style-src 'self' 'unsafe-inline'; img-src 'self' data:; font-src 'self'; connect-src 'self'; object-src 'none'; base-uri 'self'; frame-ancestors 'none'");
        chain.doFilter(request,response);
    }
}

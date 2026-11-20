package com.smartclass.web;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;

public class AuthenticationFilter implements Filter {
    @Override public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest http=(HttpServletRequest)request;
        if (WebSupport.user(http)!=null) { chain.doFilter(request,response); return; }
        HttpServletResponse out=(HttpServletResponse)response;
        if (http.getRequestURI().contains("/api/")) out.sendError(401);
        else out.sendRedirect(http.getContextPath()+"/login");
    }
}

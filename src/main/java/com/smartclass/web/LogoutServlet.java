package com.smartclass.web;

import jakarta.servlet.http.*;
import java.io.IOException;

public class LogoutServlet extends HttpServlet {
    @Override protected void doPost(HttpServletRequest request,HttpServletResponse response) throws IOException {
        if(!WebSupport.validCsrf(request)){response.sendError(403);return;}
        HttpSession session=request.getSession(false); if(session!=null) session.invalidate();
        response.sendRedirect(request.getContextPath()+"/login?logout=1");
    }
}

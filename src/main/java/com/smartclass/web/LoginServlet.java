package com.smartclass.web;

import com.smartclass.app.SmartClassService;
import com.smartclass.app.UserSession;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import java.io.IOException;

public class LoginServlet extends HttpServlet {
    @Override protected void doGet(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException {
        if(WebSupport.user(request)!=null){response.sendRedirect(request.getContextPath()+"/dashboard");return;}
        HttpSession session=request.getSession(true);
        if(session.getAttribute(WebSupport.CSRF)==null) session.setAttribute(WebSupport.CSRF,WebSupport.newCsrf());
        request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request,response);
    }

    @Override protected void doPost(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException {
        if(!WebSupport.validCsrf(request)){
            // A page kept open across a Tomcat restart/redeployment contains a token
            // from the old session. Do not accept its credentials; rotate the token
            // and return the user to a safe, freshly rendered login form.
            HttpSession session=request.getSession(true);
            session.setAttribute(WebSupport.CSRF,WebSupport.newCsrf());
            response.sendRedirect(response.encodeRedirectURL(request.getContextPath()+"/login?expired=1"));
            return;
        }
        SmartClassService service=WebSupport.service(getServletContext());
        UserSession user=service.authenticate(request.getParameter("email"),request.getParameter("password"),request.getParameter("role"));
        if(user==null){request.setAttribute("error","Incorrect email, password, or role.");doGet(request,response);return;}
        HttpSession old=request.getSession(false); if(old!=null) old.invalidate();
        HttpSession session=request.getSession(true); session.setAttribute(WebSupport.USER,user); session.setAttribute(WebSupport.CSRF,WebSupport.newCsrf());
        session.setMaxInactiveInterval(30*60);
        response.setHeader("Set-Cookie","JSESSIONID="+session.getId()+"; Path="+request.getContextPath()+"; HttpOnly; SameSite=Lax");
        response.sendRedirect(request.getContextPath()+"/dashboard");
    }
}

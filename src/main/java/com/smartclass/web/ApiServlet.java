package com.smartclass.web;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartclass.app.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.*;

public class ApiServlet extends HttpServlet {
    private SmartClassService service; private ObjectMapper mapper;
    @Override public void init(){service=WebSupport.service(getServletContext());mapper=service.mapper();}

    @Override protected void doGet(HttpServletRequest request,HttpServletResponse response) throws IOException {
        try {
            if(!"/bootstrap".equals(path(request))) throw new AppException(404,"API endpoint not found");
            WebSupport.json(response,mapper,200,service.bootstrap(WebSupport.user(request),(String)request.getSession().getAttribute(WebSupport.CSRF)));
        } catch(Throwable ex){WebSupport.error(response,mapper,ex);}
    }

    @Override protected void doPost(HttpServletRequest request,HttpServletResponse response) throws IOException { mutate(request,response,"POST"); }
    @Override protected void doPut(HttpServletRequest request,HttpServletResponse response) throws IOException { mutate(request,response,"PUT"); }
    @Override protected void doDelete(HttpServletRequest request,HttpServletResponse response) throws IOException { mutate(request,response,"DELETE"); }

    private void mutate(HttpServletRequest req,HttpServletResponse res,String method) throws IOException {
        try {
            if(!WebSupport.validCsrf(req)) throw new AppException(403,"Invalid or expired security token");
            UserSession user=WebSupport.user(req); String[] parts=Arrays.stream(path(req).split("/")).filter(s->!s.isBlank()).toArray(String[]::new);
            Map<String,Object> input=body(req); String ip=req.getRemoteAddr(); Object result=Map.of("ok",true);
            if(method.equals("POST")&&parts.length==1&&parts[0].equals("bookings")) result=Map.of("ok",true,"id",service.createBooking(user,input,ip));
            else if(method.equals("POST")&&parts.length==3&&parts[0].equals("bookings")&&(parts[2].equals("approve")||parts[2].equals("reject"))) service.reviewBooking(user,id(parts[1]),parts[2],input,ip);
            else if(method.equals("POST")&&parts.length==3&&parts[0].equals("bookings")&&parts[2].equals("cancel")) service.cancelBooking(user,id(parts[1]),ip);
            else if(method.equals("POST")&&parts.length==3&&parts[0].equals("rooms")&&parts[2].equals("status")) service.updateRoomStatus(user,id(parts[1]),input,ip);
            else if(method.equals("POST")&&parts.length==2&&parts[0].equals("favorites")) result=Map.of("ok",true,"favorite",service.toggleFavorite(user,id(parts[1])));
            else if(parts.length>=2&&parts[0].equals("admin")) {
                if(method.equals("POST")&&parts.length==2) result=Map.of("ok",true,"id",service.adminCreate(user,parts[1],input,ip));
                else if(method.equals("PUT")&&parts.length==3) service.adminUpdate(user,parts[1],id(parts[2]),input,ip);
                else if(method.equals("DELETE")&&parts.length==3) service.adminDelete(user,parts[1],id(parts[2]),ip);
                else throw new AppException(404,"API endpoint not found");
            } else throw new AppException(404,"API endpoint not found");
            WebSupport.json(res,mapper,200,result);
        } catch(Throwable ex){WebSupport.error(res,mapper,ex);}
    }

    private Map<String,Object> body(HttpServletRequest req) throws IOException {
        if(req.getContentLengthLong()==0) return new LinkedHashMap<>();
        return mapper.readValue(req.getInputStream(),new TypeReference<>(){});
    }
    private static int id(String value){try{return Integer.parseInt(value);}catch(NumberFormatException ex){throw new AppException(400,"Invalid id");}}
    private static String path(HttpServletRequest req){String p=req.getPathInfo();return p==null?"/":p;}
}

package com.xmcc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class SysLogOutController {
    /**
     * 用户退出
     * @param request
     * @param response
     */
    @RequestMapping("/logout.page")
    public void logout(HttpServletRequest request, HttpServletResponse response){
        request.getSession().removeAttribute("user");
        try {
            response.sendRedirect("signin.jsp");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

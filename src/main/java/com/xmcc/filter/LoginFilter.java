package com.xmcc.filter;

import com.xmcc.model.SysUser;
import com.xmcc.utils.RequestHolder;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class LoginFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        // 请求路径
        String url = request.getRequestURI();
        if (url.contains("login")||url.contains("signin")||url.contains("/js/")||url.contains("/css/")||url.contains("/bootstrap3.3.5/")
                ||url.contains("/assets/")||url.contains("/mustache/")||url.contains("/ztree/")){
            filterChain.doFilter(request,response);
        }else {
            SysUser sysUser = (SysUser) request.getSession().getAttribute("user");

            if (sysUser == null){
                response.sendRedirect("/signin.jsp");
                return;
            }else {
                // 将 user 和 request 绑定在 ThreadLocal 中，因为每一个请求都会经过 filter
                RequestHolder.add(sysUser);
                RequestHolder.add(request);
                filterChain.doFilter(request,response);
            }
        }


    }

    @Override
    public void destroy() {

    }
}

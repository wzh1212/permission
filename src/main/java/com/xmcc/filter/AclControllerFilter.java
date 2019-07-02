package com.xmcc.filter;

import com.xmcc.model.SysUser;
import com.xmcc.service.SysCoreService;
import com.xmcc.utils.ApplicationContextHelper;
import com.xmcc.utils.JsonData;
import com.xmcc.utils.JsonMapper;
import com.xmcc.utils.RequestHolder;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AclControllerFilter implements Filter {

    // 设置 全局常量
    private final static String noAuthUrl = "/sys/user/noAuth.page";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        // 获取 路径地址
        String url = request.getRequestURI();

        // 如果是访问权限提示页面，或者是 login 页面，那么直接放行
        if (url.contains("signin") || url.contains("login") || url.contains(noAuthUrl)){
            // 放行
            filterChain.doFilter(request,response);
            return;
        }
        // 取出用户
        SysUser user = RequestHolder.getUser();
        // 判断用户
        // 用户没有登录
        if (user == null){
            // 用户在没有权限的情况下的处理方案
            noAuth(request,response);
            return;
        }

        // 用户登录之后，没有相关权限
        SysCoreService sysCoreService = ApplicationContextHelper.popBean(SysCoreService.class);
        if (!sysCoreService.hasAcl(url)){
            noAuth(request,response);
            return;
        }

        filterChain.doFilter(request,response);
        return;
    }

    private void noAuth(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 请求分为两种：页面请求 、json请求
        String url = request.getRequestURI();
        if (url.endsWith(".json")){
            JsonData jsonData = JsonData.fail("没有访问权限，如需要，请联系管理员");
            response.setHeader("Content-Type","application/json");
            response.getWriter().print(JsonMapper.obj2String(jsonData));

        }else {
            response.setHeader("Content-Type", "text/html");
            response.getWriter().print("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n"
                    + "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" + "<head>\n" + "<meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\"/>\n"
                    + "<title>跳转中...</title>\n" + "</head>\n" + "<body>\n" + "跳转中，请稍候...\n" + "<script type=\"text/javascript\">//<![CDATA[\n"
                    + "window.location.href='" + noAuthUrl + "?ret='+encodeURIComponent(window.location.href);\n" + "//]]></script>\n" + "</body>\n" + "</html>\n");
        }

    }

    @Override
    public void destroy() {

    }
}

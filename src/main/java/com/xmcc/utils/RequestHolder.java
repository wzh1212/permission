package com.xmcc.utils;

import com.xmcc.model.SysUser;

import javax.servlet.http.HttpServletRequest;

public class RequestHolder {

    // 用来 绑定当前登录的用户
    private static final ThreadLocal<SysUser> userHolder = new ThreadLocal<>();

    // 绑定当前 request 对象（需要从 request 对象把 IP地址 拿出来）
    private static final ThreadLocal<HttpServletRequest> requestHolder = new ThreadLocal<>();


    public static void add(SysUser sysUser){
        userHolder.set(sysUser);
    }

    public static void add(HttpServletRequest request){
        requestHolder.set(request);
    }

    public static SysUser getUser(){
        return userHolder.get();
    }

    public static HttpServletRequest getRequest(){
        return  requestHolder.get();
    }

    // 解绑
    public static void remove(){
        userHolder.remove();
        requestHolder.remove();
    }
}

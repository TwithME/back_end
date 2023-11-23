package com.example.twithme.security;


import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class CorsFilter extends GenericFilterBean {

    Set<String> headerKeys;

    public CorsFilter() {
        headerKeys = new HashSet<>();
        headerKeys.add("X-AUTH-TOKEN");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Credentials", "false");
        response.setHeader("Access-Control-Allow-Methods","*");
        response.setHeader("Access-Control-Max-Age", "3000");
        response.setHeader("Access-Control-Allow-Headers",
                "Content-Type, X-AUTH-TOKEN, Authorization");
        response.setHeader("Access-Control-Expose-Headers", "Authorization, X-AUTH-TOKEN");

        if("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
        }
        else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
//        filterChain.doFilter(servletRequest, servletResponse);
    }
}

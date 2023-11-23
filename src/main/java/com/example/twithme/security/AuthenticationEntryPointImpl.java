package com.example.twithme.security;

import com.example.twithme.common.exception.dto.HttpRes;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                         AuthenticationException ex) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
        httpServletResponse.setContentType("application/json");
        httpServletResponse.setCharacterEncoding("utf-8");
        HttpRes<String> httpRes = new HttpRes<>(HttpStatus.UNAUTHORIZED.value(), "인증에 실패하였습니다.");
        httpServletResponse.getWriter().write(objectMapper.writeValueAsString(httpRes));
    }
}

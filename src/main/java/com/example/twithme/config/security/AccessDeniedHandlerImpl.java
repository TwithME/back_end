package com.example.twithme.config.security;

import com.example.twithme.common.model.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class AccessDeniedHandlerImpl implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                       AccessDeniedException accessDeniedException) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

//        httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
        httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
        httpServletResponse.setContentType("application/json");
        httpServletResponse.setCharacterEncoding("utf-8");
//        HttpRes<String> httpRes = new HttpRes<>(HttpStatus.UNAUTHORIZED.value(), "인증에 실패하였습니다.");
        ApiResponse<String> apiResponse = new ApiResponse<>(HttpStatus.FORBIDDEN.value(), "권한이 존재하지 않습니다.");
        httpServletResponse.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }
}
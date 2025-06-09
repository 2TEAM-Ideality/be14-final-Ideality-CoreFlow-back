package com.ideality.coreflow.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ideality.coreflow.common.response.APIResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper(); // json 변환용

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");

        APIResponse<?> res = APIResponse.fail("잘못된 인증 정보입니다.");

        response.getWriter().write(objectMapper.writeValueAsString(APIResponse.error(authException.getMessage())));
    }
}

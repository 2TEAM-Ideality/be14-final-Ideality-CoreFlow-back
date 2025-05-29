package com.ideality.coreflow.security.jwt;

import com.ideality.coreflow.common.tenant.config.TenantContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String bearerToken = request.getHeader("Authorization");

            // 토큰 꺼내기
            if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
                String token = bearerToken.substring(7);

                // 토큰 유효성 검증
                if (jwtUtil.validateToken(token)) {
                    // 블랙리스트 검사 로직 들어갈 예정

                    // -------------------

                    String employeeNum = jwtProvider.getEmployeeNum(token);
                    String companySchema = jwtProvider.getCompanySchema(token);

                    // 테넌트 설정
                    TenantContext.setTenant(companySchema);

                    // 인증 객체 생성
                    List<GrantedAuthority> authorities = jwtProvider.getRoles(token)
                            .stream()
                            .map(role -> new SimpleGrantedAuthority(role))
                            .collect(Collectors.toList());

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    employeeNum,
                                    null,
                                    authorities);

                    authentication.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.info("JWT 인증 성공 - employeeNum: {}", employeeNum);
                } else {
                    log.warn("JWT 토큰이 유효하지 않음. 인증 처리 생략");
                }
            }
            filterChain.doFilter(request, response);
        } finally {
            // 스레드 로컬 클리어
            TenantContext.clear();
        }
    }
}

package com.ideality.coreflow.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ideality.coreflow.auth.command.application.dto.RequestLogin;
import com.ideality.coreflow.auth.command.application.dto.RequestResetPassword;
import com.ideality.coreflow.auth.command.application.dto.RequestTokenReissue;
import com.ideality.coreflow.common.exception.BaseException;
import com.ideality.coreflow.common.exception.ErrorCode;
import com.ideality.coreflow.infra.redis.util.RedisUtil;
import com.ideality.coreflow.infra.tenant.config.TenantContext;
import com.ideality.coreflow.security.custom.CachedBodyHttpServletRequest;
import com.ideality.coreflow.tenant.command.application.service.TenantService;
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
    private final ObjectMapper objectMapper;
    private final TenantService tenantService;
    private final RedisUtil redisUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        if ("POST".equalsIgnoreCase(request.getMethod()) && "/api/auth/login".equals(request.getRequestURI())) {

            CachedBodyHttpServletRequest cachedRequest = new CachedBodyHttpServletRequest(request);

            // JSON -> LoginRequest 객체 파싱
            try {
                RequestLogin requestLogin = objectMapper.readValue(cachedRequest.getInputStream(), RequestLogin.class);
                // TenantContext에 schema 설정
                String schema = tenantService.findSchemaNameByCompanyCode(requestLogin.getCompanyCode());
                TenantContext.setTenant(schema);

                // 필터 체인에 래핑된 요청 전단 (RequestBody를 다시 읽을 수 있도록)
                filterChain.doFilter(cachedRequest, response);
            } catch (Exception e) {
                // 실패하면 에러
                throw new BaseException(ErrorCode.INVALID_LOGIN_REQUEST);
            } finally {
                // 요청 처리 완료 후 클리어
                TenantContext.clear();
            }
        } else if ("POST".equalsIgnoreCase(request.getMethod()) && "/api/auth/reissue".equals(request.getRequestURI())) {

            CachedBodyHttpServletRequest cachedRequest = new CachedBodyHttpServletRequest(request);

            // JSON -> RequestOkenReissue 객체 파싱
            try {
                RequestTokenReissue requestTokenReissue = objectMapper.readValue(cachedRequest.getInputStream(), RequestTokenReissue.class);
                // TenantContext에 schema 설정
                String schema = requestTokenReissue.getCompanySchema();
                TenantContext.setTenant(schema);

                // 필터 체인에 래핑된 요청 전단 (RequestBody를 다시 읽을 수 있도록)
                filterChain.doFilter(cachedRequest, response);
            } catch (Exception e) {
                // 실패하면 에러
                throw new BaseException(ErrorCode.INVALID_TOKEN);
            } finally {
                // 요청 처리 완료 후 클리어
                TenantContext.clear();
            }
        } else if ("POST".equalsIgnoreCase(request.getMethod()) && "/api/auth/password/reset".equals(request.getRequestURI())) {

            CachedBodyHttpServletRequest cachedRequest = new CachedBodyHttpServletRequest(request);

            // JSON -> RequestResetPassword 객체 파싱
            try {
                RequestResetPassword requestResetPassword = objectMapper.readValue(cachedRequest.getInputStream(), RequestResetPassword.class);
                // TenantContext에 schema 설정
                String schema = tenantService.findSchemaNameByCompanyCode(requestResetPassword.getCompanyCode());
                TenantContext.setTenant(schema);

                // 필터 체인에 래핑된 요청 전단 (RequestBody를 다시 읽을 수 있도록)
                filterChain.doFilter(cachedRequest, response);
            } catch (Exception e) {
                // 실패하면 에러
                throw new BaseException(ErrorCode.INVALID_TOKEN);
            } finally {
                // 요청 처리 완료 후 클리어
                TenantContext.clear();
            }
        } else {
            try {
                String bearerToken = request.getHeader("Authorization");

                // 토큰 꺼내기
                if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
                    String token = bearerToken.substring(7);

                    // 토큰 유효성 검증
                    if (jwtUtil.validateAccessToken(token)) {
                        // 블랙리스트 검사
                        String blacklistKey = "Blacklist:" + token;
                        if (redisUtil.hasKey(blacklistKey)) {
                            log.warn("Access Token이 blacklist에 있음 - 인증 중단");
                            filterChain.doFilter(request, response);
                            return;
                        }

                        String id = jwtProvider.getUserId(token);
                        String employeeNum = jwtProvider.getEmployeeNum(token);
                        String companySchema = jwtProvider.getCompanySchema(token);

                        log.info("jwt 필터에서 테넌트 설정");
                        // 테넌트 설정
                        TenantContext.setTenant(companySchema);
                        log.info("테넌트 설정 완료: {}", TenantContext.getTenant());

                        // 인증 객체 생성
                        List<GrantedAuthority> authorities = jwtProvider.getRoles(token)
                                .stream()
                                .map(role -> new SimpleGrantedAuthority(role))
                                .collect(Collectors.toList());

                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(
                                        id,
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
}

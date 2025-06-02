package com.ideality.coreflow.auth.command.application.service;

import com.ideality.coreflow.auth.command.application.dto.request.SignUpRequest;
import com.ideality.coreflow.auth.command.domain.aggregate.LoginType;
import com.ideality.coreflow.auth.command.application.dto.request.LoginRequest;
import com.ideality.coreflow.auth.command.application.dto.response.TokenResponse;
import com.ideality.coreflow.common.exception.BaseException;
import com.ideality.coreflow.common.exception.ErrorCode;
import com.ideality.coreflow.email.command.application.dto.UserLoginInfo;
import com.ideality.coreflow.email.command.application.service.EmailSendService;
import com.ideality.coreflow.security.jwt.JwtUtil;
import com.ideality.coreflow.user.command.application.dto.LoginDTO;
import com.ideality.coreflow.user.command.application.dto.UserInfoDTO;
import com.ideality.coreflow.user.command.application.service.RoleService;
import com.ideality.coreflow.user.command.application.service.UserOfRoleService;
import com.ideality.coreflow.user.command.application.service.UserService;
import com.ideality.coreflow.user.query.dto.DeptNameAndMonthDTO;
import com.ideality.coreflow.user.query.service.UserQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthFacadeService {

    private final UserService userService;
    private final AuthService authService;
    private final UserQueryService userQueryService;
    private final PasswordEncoder passwordEncoder;
    private final EmailSendService emailSendService;
    private final RoleService roleService;
    private final UserOfRoleService userOfRoleService;
    private final JwtUtil jwtUtil;

    // 로그인
    @Transactional
    public TokenResponse login(LoginRequest loginRequest) {

        log.info("request identifier: {}", loginRequest.getIdentifier());

        LoginType loginType = LoginType.fromIdentifier(loginRequest.getIdentifier());
        log.info("loginType: {}", loginType);

        LoginDTO userInfo = userService.findUserInfoByIdentifier(loginRequest.getIdentifier(), loginType);
        log.info("로그인 유저 정보 조회: {}", userInfo);

        List<String> userOfRoles = userQueryService.findGeneralRolesByUserId(userInfo.getId());
        log.info("해당 유저 역할 정보 조회: {}", userOfRoles);

        return authService.login(userInfo, loginRequest.getPassword(), userOfRoles);
    }

    // 회원가입
    @Transactional
    public void signUp(SignUpRequest signUpRequest) {

        log.info("등록된 이메일인지 확인");
        // 등록된 이메일인지 확인
        if (userService.isExistEmail(signUpRequest.getEmail())) {
            throw new BaseException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        // 사번 생성
        String employeeNum = generateEmployeeNum(signUpRequest.getDeptName(), signUpRequest.getHireDate());
        log.info("사번 생성 {}", employeeNum);

        // 랜덤 비밀번호 생성
        String password = authService.generatePassword();
        String encodedPassword = passwordEncoder.encode(password);

        // 회원 정보 등록
        log.info("회원 정보 등록");

        UserInfoDTO userInfo = UserInfoDTO.builder()
                                .employeeNum(employeeNum)
                                .password(encodedPassword)
                                .name(signUpRequest.getName())
                                .email(signUpRequest.getEmail())
                                .birth(signUpRequest.getBirth())
                                .hireDate(signUpRequest.getHireDate())
                                .deptName(signUpRequest.getDeptName())
                                .jobRankName(signUpRequest.getJobRankName())
                                .jobRoleName(signUpRequest.getJobRoleName())
                                .build();
        long userId = userService.registUser(userInfo);
        log.info("userId: {}", userId);
        log.info("회원 가입 완료");

        // 생성 권한 넣기
        // 프로젝트 생성 역할 id 가져오기
        if (signUpRequest.isCreation()) {
            long roleId = roleService.findRoleByName("Creator");
            // 해당 회원에게 권한 넣기
            userOfRoleService.registAuthorities(userId, roleId);
        }


        log.info("메일 발송");
        UserLoginInfo userLoginInfo = UserLoginInfo.builder()
                                        .employeeNum(employeeNum)
                                        .email(signUpRequest.getEmail())
                                        .password(password)
                                        .build();
        emailSendService.sendEmailUserLoginInfo(userLoginInfo);
    }

    private String generateEmployeeNum(String deptName, LocalDate hireDate) {

        // 입사 연월 추출
        String yearMonth = hireDate.format(DateTimeFormatter.ofPattern("yyMM"));

        // 부서명 -> 부서 약어
        String deptCode = "";

        DeptNameAndMonthDTO deptNameAndMonthDTO = new DeptNameAndMonthDTO(deptName, yearMonth);

        // 해당 입사월 + 부서의 기존 유저 수
        long count = userQueryService.countByHireMonthAndDeptName(deptNameAndMonthDTO);
        long sequence = count + 1;

        return String.format("%s%s%03d", deptCode, yearMonth, sequence);
    }

    @Transactional
    public void logout(String accessToken) {
        Long userId = jwtUtil.getUserIdFromToken(accessToken);

        // AccessToken 블랙리스트 처리
        long expiration = jwtUtil.getExpiration(accessToken);
        String blacklistKey = "Blacklist:" + accessToken;
    }
}

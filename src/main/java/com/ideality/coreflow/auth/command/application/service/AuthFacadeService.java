package com.ideality.coreflow.auth.command.application.service;

import com.ideality.coreflow.auth.command.application.dto.RequestSignUp;
import com.ideality.coreflow.auth.command.application.dto.RequestSignUpPartner;
import com.ideality.coreflow.auth.command.domain.aggregate.LoginType;
import com.ideality.coreflow.auth.command.application.dto.RequestLogin;
import com.ideality.coreflow.auth.command.application.dto.ResponseToken;
import com.ideality.coreflow.common.exception.BaseException;
import com.ideality.coreflow.common.exception.ErrorCode;
import com.ideality.coreflow.email.command.application.dto.UserLoginInfo;
import com.ideality.coreflow.email.command.application.service.EmailSendService;
import com.ideality.coreflow.user.command.application.dto.LoginDTO;
import com.ideality.coreflow.user.command.application.dto.UserInfoDTO;
import com.ideality.coreflow.user.command.application.service.RoleService;
import com.ideality.coreflow.user.command.application.service.UserOfRoleService;
import com.ideality.coreflow.user.command.application.service.UserService;
import com.ideality.coreflow.user.query.dto.DeptNameAndYearDTO;
import com.ideality.coreflow.user.query.service.UserQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
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

    // 로그인
    @Transactional
    public ResponseToken login(RequestLogin requestLogin) {

        log.info("request identifier: {}", requestLogin.getIdentifier());

        LoginType loginType = LoginType.fromIdentifier(requestLogin.getIdentifier());
        log.info("loginType: {}", loginType);

        LoginDTO loginInfo = userService.findLoginInfoByIdentifier(requestLogin.getIdentifier(), loginType);
        log.info("로그인 유저 정보 조회: {}", loginInfo);

        List<String> userOfRoles = userQueryService.findGeneralRolesByUserId(loginInfo.getId());
        log.info("해당 유저 역할 정보 조회: {}", userOfRoles);

        return authService.login(loginInfo, requestLogin.getPassword(), userOfRoles);
    }

    // 회원가입
    @Transactional
    public void signUp(RequestSignUp requestSignUp) {

        log.info("등록된 이메일인지 확인");
        // 등록된 이메일인지 확인
        if (userService.isExistEmail(requestSignUp.getEmail())) {
            throw new BaseException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        // 사번 생성 로직
        DeptNameAndYearDTO deptNameAndYearDTO = DeptNameAndYearDTO.builder()
                                                    .deptName(requestSignUp.getDeptName())
                                                    .hireDate(requestSignUp.getHireDate())
                                                    .build();
        // 같은해 입사 부서 인원 조회
        long sequence = userQueryService.countByHireYearAndDeptName(deptNameAndYearDTO) + 1;
        String employeeNum = authService.generateEmployeeNum(requestSignUp.getHireDate(), requestSignUp.getDeptId(), sequence);
        log.info("사번 생성 {}", employeeNum);

        // 랜덤 비밀번호 생성
        String password = authService.generatePassword();
        String encodedPassword = passwordEncoder.encode(password);

        // 회원 정보 등록
        log.info("회원 정보 등록");

        UserInfoDTO userInfo = UserInfoDTO.builder()
                                .employeeNum(employeeNum)
                                .password(encodedPassword)
                                .name(requestSignUp.getName())
                                .email(requestSignUp.getEmail())
                                .birth(requestSignUp.getBirth())
                                .hireDate(requestSignUp.getHireDate())
                                .deptName(requestSignUp.getDeptName())
                                .jobRankName(requestSignUp.getJobRankName())
                                .jobRoleName(requestSignUp.getJobRoleName())
                                .build();
        long userId = userService.registUser(userInfo);
        log.info("userId: {}", userId);
        log.info("회원 가입 완료");

        // 생성 권한 넣기
        // 프로젝트 생성 역할 id 가져오기
        long roleId = roleService.findRoleByName("Creator");

        // 해당 회원에게 권한 넣기 (false면 안들어감)
        userOfRoleService.updateCreation(requestSignUp.isCreation(), userId, roleId);

        log.info("메일 발송");
        UserLoginInfo userLoginInfo = UserLoginInfo.builder()
                                        .employeeNum(employeeNum)
                                        .email(requestSignUp.getEmail())
                                        .password(password)
                                        .build();
        emailSendService.sendEmailUserLoginInfo(userLoginInfo);
    }

    // 로그아웃
    @Transactional
    public void logout(String accessToken) {
        authService.logout(accessToken);
    }

    @Transactional
    public ResponseToken reissueAccessToken(String refreshToken, Long userId) {
        // 토큰 유효성 검증
        authService.validateRefreshToken(refreshToken, userId);

        String employeeNum = userService.findEmployeeNumById(userId);
        log.info("employeeNum 조회: {}", employeeNum);

        List<String> userOfRoles = userQueryService.findGeneralRolesByUserId(userId);
        log.info("해당 유저 역할 정보 조회: {}", userOfRoles);

        return authService.reissuAccessToken(userId, employeeNum, userOfRoles);
    }

    @Transactional
    public void signUpPartner(RequestSignUpPartner request) {

        log.info("등록된 이메일인지 확인");
        // 등록된 이메일인지 확인
        if (userService.isExistEmail(request.getEmail())) {
            throw new BaseException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        // 협력 업체 순번 조회 (count)
        long sequence = userQueryService.countByJobRoleName(request.getRoleName()) + 1;

        // 부서 id 조회
        long deptId = 0;

        // -----------------------
        // 파트너 넘버 생성
        String partnerNum = authService.generatePartnerNum(LocalDate.now(), deptId, sequence);

        // 랜덤 비밀번호 생성
        String password = authService.generatePassword();
        String encodedPassword = passwordEncoder.encode(password);

        UserInfoDTO userInfo = UserInfoDTO.builder()
                .employeeNum(partnerNum)
                .password(encodedPassword)
                .name(request.getName())
                .email(request.getEmail())
                .hireDate(LocalDate.now())
                .deptName(request.getRoleName())
                .jobRankName(request.getRoleName())
                .jobRoleName(request.getRoleName())
                .build();
        long userId = userService.registUser(userInfo);
        log.info("userId: {}", userId);
        log.info("협력업체 계정 생성");
    }
}

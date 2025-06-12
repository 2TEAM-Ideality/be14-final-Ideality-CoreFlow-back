package com.ideality.coreflow.auth.command.application.service;

import com.ideality.coreflow.auth.command.application.dto.*;
import com.ideality.coreflow.auth.command.domain.aggregate.LoginType;
import com.ideality.coreflow.common.exception.BaseException;
import com.ideality.coreflow.common.exception.ErrorCode;
import com.ideality.coreflow.email.command.application.service.EmailSendService;
import com.ideality.coreflow.email.command.domail.aggregate.EmailType;
import com.ideality.coreflow.org.query.service.DeptQueryService;
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
import java.util.Map;

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
    private final DeptQueryService deptQueryService;

    // 로그인
    @Transactional
    public ResponseTokenAndUserInfo login(RequestLogin requestLogin) {

        log.info("request identifier: {}", requestLogin.getIdentifier());

        LoginType loginType = LoginType.fromIdentifier(requestLogin.getIdentifier());
        log.info("loginType: {}", loginType);

        UserInfoDTO loginInfo = userService.findLoginInfoByIdentifier(requestLogin.getIdentifier(), loginType);
        log.info("로그인 유저 정보 조회: {}", loginInfo);

        List<String> userOfRoles = userQueryService.findGeneralRolesByUserId(loginInfo.getId());
        log.info("해당 유저 역할 정보 조회: {}", userOfRoles);

        ResponseToken responseToken = authService.login(loginInfo, requestLogin.getPassword(), userOfRoles);

        return ResponseTokenAndUserInfo.builder()
                .id(loginInfo.getId())
                .employeeNum(loginInfo.getEmployeeNum())
                .name(loginInfo.getName())
                .email(loginInfo.getEmail())
                .birth(loginInfo.getBirth())
                .hireDate(loginInfo.getHireDate())
                .isResign(loginInfo.getIsResign())
                .resignDate(loginInfo.getResignDate())
                .profileImage(loginInfo.getProfileImage())
                .deptName(loginInfo.getDeptName())
                .jobRankName(loginInfo.getJobRankName())
                .jobRoleName(loginInfo.getJobRoleName())
                .accessToken(responseToken.getAccessToken())
                .refreshToken(responseToken.getRefreshToken())
                .schemaName(responseToken.getSchemaName())
                .roles(userOfRoles)
                .isTemp(responseToken.isTemp())
                .build();
    }

    // 회원가입
    @Transactional
    public ResponseSignUp signUp(RequestSignUp requestSignUp) {

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
        long roleId = roleService.findRoleByName("CREATOR");

        // 해당 회원에게 권한 넣기 (false면 안들어감)
        userOfRoleService.updateAuthorities(requestSignUp.isCreation(), userId, roleId);

        log.info("메일 발송");
        log.info("메일 발송");
        Map<String, String> data = Map.of(
                "email", requestSignUp.getEmail(),
                "employeeNum", employeeNum,
                "password", password
        );
        emailSendService.sendEmail(EmailType.USER_LOGIN_INFO, data);

        return ResponseSignUp.builder()
                .id(userId)
                .name(requestSignUp.getName())
                .deptName(requestSignUp.getDeptName())
                .jobRankName(requestSignUp.getJobRankName())
                .jobRoleName(requestSignUp.getJobRoleName())
                .isCreation(requestSignUp.isCreation())
                .isResign(false)
                .build();
    }

    // 로그아웃
    @Transactional
    public void logout(String accessToken) {
        authService.logout(accessToken);
    }

    @Transactional
    public ResponseTokenAndUserInfo reissueAccessToken(String refreshToken, Long userId) {
        // 토큰 유효성 검증
        authService.validateRefreshToken(refreshToken, userId);

        UserInfoDTO userInfo = userService.findUserById(userId);

        List<String> userOfRoles = userQueryService.findGeneralRolesByUserId(userId);
        log.info("해당 유저 역할 정보 조회: {}", userOfRoles);

        ResponseToken token = authService.reissuAccessToken(userId, userInfo.getEmployeeNum(), userOfRoles);

        return ResponseTokenAndUserInfo.builder()
                .id(userInfo.getId())
                .employeeNum(userInfo.getEmployeeNum())
                .name(userInfo.getName())
                .email(userInfo.getEmail())
                .birth(userInfo.getBirth())
                .hireDate(userInfo.getHireDate())
                .isResign(userInfo.getIsResign())
                .resignDate(userInfo.getResignDate())
                .profileImage(userInfo.getProfileImage())
                .deptName(userInfo.getDeptName())
                .jobRankName(userInfo.getJobRankName())
                .jobRoleName(userInfo.getJobRoleName())
                .accessToken(token.getAccessToken())
                .refreshToken(refreshToken)
                .schemaName(token.getSchemaName())
                .roles(userOfRoles)
                .isTemp(token.isTemp())
                .build();
    }

    @Transactional
    public ResponseSignUp signUpPartner(RequestSignUpPartner request) {

        log.info("등록된 이메일인지 확인");
        // 등록된 이메일인지 확인
        if (userService.isExistEmail(request.getEmail())) {
            throw new BaseException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        // 협력 업체 순번 조회 (count)
        long sequence = userQueryService.countByJobRoleName(request.getRoleName()) + 1;

        // 부서 id 조회
        long deptId = deptQueryService.findDeptIdByName("협력업체");

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

        // 생성 권한 넣기
        // 협력업체 역할 id 가져오기
        long roleId = roleService.findRoleByName("PARTNER");

        // 해당 회원에게 외부 역할 넣기 (false면 안들어감)
        userOfRoleService.updateAuthorities(true, userId, roleId);

        log.info("메일 발송");
        Map<String, String> data = Map.of(
                "email", request.getEmail(),
                "employeeNum", partnerNum,
                "password", password
        );
        emailSendService.sendEmail(EmailType.USER_LOGIN_INFO, data);

        return ResponseSignUp.builder()
                .id(userId)
                .name(request.getName())
                .deptName(request.getRoleName())
                .jobRankName(request.getRoleName())
                .jobRoleName(request.getRoleName())
                .isCreation(false)
                .isResign(false)
                .build();
    }

    @Transactional
    public void modifyPassword(UpdatePwdDTO updatePwdInfo) {
        String pwd = userService.findPwdById(updatePwdInfo.getId());

        // 현재 비밀번호 검증
        authService.validatePwd(updatePwdInfo, pwd);

        log.info("이전 비밀번호: {}", updatePwdInfo.getPrevPassword());
        log.info("새 비밀번호: {}", updatePwdInfo.getNewPassword());
        String newPassword = passwordEncoder.encode(updatePwdInfo.getNewPassword());

        UserInfoDTO updateUserInfo = UserInfoDTO.builder()
                .id(updatePwdInfo.getId())
                .password(newPassword)
                .build();

        userService.updateUser(updateUserInfo);
    }

    @Transactional
    public void resetPasswordRequest(RequestResetPassword request) {

        UserInfoDTO userInfo = userService.findUserByEmployeeNum(request.getEmployeeNum());

        log.info(userInfo.toString());

        if (authService.verificationUserInfo(userInfo, request)) {
            String verificationCode = authService.generateVerificationCode(request.getEmail());
            // 인증 메일 보내기
            Map<String, String> data = Map.of(
                    "email", request.getEmail(),
                    "verificationCode", verificationCode
            );
            emailSendService.sendEmail(EmailType.VERIFICATION_CODE, data);
        } else {
            throw new BaseException(ErrorCode.INVALID_USER_INFO);
        }
    }

    @Transactional
    public void resetPasswordVerify(RequestResetPasswordVerify request) {
        if (authService.validateCode(request)) {
            log.info("인증 완료");
            String newPassword = authService.generatePassword();
            String encodedPassword = passwordEncoder.encode(newPassword);

            long userId = userService.findUserIdByEmail(request.getEmail());

            UserInfoDTO updateUserInfo = UserInfoDTO.builder()
                    .id(userId)
                    .password(encodedPassword)
                    .build();

            userService.updateUser(updateUserInfo);
            log.info("새 비밀번호로 업데이트");

            Map<String, String> data = Map.of(
                    "email", request.getEmail(),
                    "password", newPassword
            );

            log.info("새 비밀번호 발송");
            emailSendService.sendEmail(EmailType.NEW_PASSWORD, data);
        } else {
            throw new BaseException(ErrorCode.INVALID_VERIFICATION_CODE);
        }
    }
}

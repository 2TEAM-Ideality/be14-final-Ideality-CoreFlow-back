package com.ideality.coreflow.user.command.application.service.impl;

import com.ideality.coreflow.auth.command.domain.aggregate.LoginType;
import com.ideality.coreflow.common.exception.BaseException;
import com.ideality.coreflow.common.exception.ErrorCode;
import com.ideality.coreflow.infra.s3.S3Service;
import com.ideality.coreflow.infra.tenant.config.TenantContext;
import com.ideality.coreflow.tenant.command.application.dto.ResponseInitialAdmin;
import com.ideality.coreflow.user.command.application.dto.RequestUpdateProfile;
import com.ideality.coreflow.user.command.application.dto.UserInfoDTO;
import com.ideality.coreflow.user.command.application.service.UserService;
import com.ideality.coreflow.user.command.domain.aggregate.OrgType;
import com.ideality.coreflow.user.command.domain.aggregate.User;
import com.ideality.coreflow.user.command.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final S3Service s3Service;

    @Override
    public UserInfoDTO findLoginInfoByIdentifier(String identifier, LoginType loginType) {

        log.info("Transactional Propagation.REQUIRES_NEW");
        log.info("userService에요");
        log.info("tenant: {}", TenantContext.getTenant());
        TenantContext.setTenant(TenantContext.getTenant());

        User user;

        if (loginType == LoginType.EMPLOYEE_NUM) {
            user = userRepository.findByEmployeeNum(identifier)
                    .orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND));
        } else {
            user = userRepository.findByEmail(identifier)
                    .orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND));
        }

        if (user.getIsResign()) {
            throw new BaseException(ErrorCode.RESIGNED_USER);
        }

        return UserInfoDTO.builder()
                .id(user.getId())
                .employeeNum(user.getEmployeeNum())
                .password(user.getPassword())
                .name(user.getName())
                .email(user.getEmail())
                .birth(user.getBirth())
                .hireDate(user.getHireDate())
                .isResign(user.getIsResign())
                .resignDate(user.getResignDate())
                .profileImage(user.getProfileImage())
                .deptName(user.getDeptName())
                .jobRankName(user.getJobRankName())
                .jobRoleName(user.getJobRoleName())
                .build();
    }

    @Override
    public Boolean isExistEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public Long registUser(UserInfoDTO userInfo) {
        User user = User.builder()
                .employeeNum(userInfo.getEmployeeNum())
                .password(userInfo.getPassword())
                .name(userInfo.getName())
                .email(userInfo.getEmail())
                .birth(userInfo.getBirth())
                .hireDate(userInfo.getHireDate())
                .isResign(false)
                .deptName(userInfo.getDeptName())
                .jobRankName(userInfo.getJobRankName())
                .jobRoleName(userInfo.getJobRoleName())
                .build();

        return userRepository.save(user).getId();
    }

    @Override
    public String findEmployeeNumById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND)).getEmployeeNum();
    }

    @Override
    public void updateUser(RequestUpdateProfile req) {
        User user = userRepository.findById(req.getId()).orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND));

        // 기존 이미지 삭제
        if (user.getProfileImage() != null) {
            s3Service.deleteFileByUrl(user.getProfileImage());
        }

        // 새 이미지 업로드
        String imageUrl = s3Service.uploadImage(req.getProfileImage(), "profile-image");

        // 유저 엔티티 수정
        user.updateProfileImage(imageUrl);
        userRepository.save(user);
    }

    @Override
    public String findPwdById(long id) {
        return userRepository.findById(id).orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND)).getPassword();
    }

    @Override
    public UserInfoDTO findUserByEmployeeNum(String employeeNum) {
        User user = userRepository.findByEmployeeNum(employeeNum).orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND));

        return UserInfoDTO.builder()
                .id(user.getId())
                .password(user.getPassword())
                .email(user.getEmail())
                .name(user.getName())
                .employeeNum(user.getEmployeeNum())
                .build();
    }

    @Override
    public long findUserIdByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND)).getId();
    }

    @Override
    public void updateUserOrg(OrgType type, String prevName, String newName) {

        List<User> users = List.of();
        
        switch (type) {
            case DEPT -> {
                users = userRepository.findByDeptNameAndIsResign(prevName, false);
            }
            case JOB_RANK -> {
                users = userRepository.findByJobRankNameAndIsResign(prevName, false);
            }
            case JOB_ROLE -> {
                users = userRepository.findByJobRoleNameAndIsResign(prevName, false);
            }
        }
        
        for (User user : users) {
            user.updateOrg(type, newName);
        }
    }

    @Override
    public UserInfoDTO findUserById(long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND));
        log.info("isResign={}", user.getIsResign());
        return UserInfoDTO.builder()
                .id(user.getId())
                .employeeNum(user.getEmployeeNum())
                .name(user.getName())
                .email(user.getEmail())
                .birth(user.getBirth())
                .hireDate(user.getHireDate())
                .isResign(user.getIsResign())
                .resignDate(user.getResignDate())
                .profileImage(user.getProfileImage())
                .deptName(user.getDeptName())
                .jobRankName(user.getJobRankName())
                .jobRoleName(user.getJobRoleName())
                .build();
    }

    @Override
    public void deleteUserProfileImg(long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND));
        s3Service.deleteFileByUrl(user.getProfileImage());
        user.deleteProfile();
        userRepository.save(user);
    }

    @Override
    public void existsUserId(List<Long> leaderUserIds) {
        for (Long userId : leaderUserIds) {
            userRepository.findById(userId).orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));
        }
    }

    @Override
    public ResponseInitialAdmin createInitialAdmin(String schemaName) {
        log.info("관리자 생성");
        log.info("테넌트:{}", TenantContext.getTenant());
        TenantContext.setTenant(schemaName);
        log.info("테넌트:{}", TenantContext.getTenant());


        User user = User.builder()
                .employeeNum("admin_"+schemaName)
                .password(passwordEncoder.encode("000000"))
                .name("admin")
                .email("admin")
                .birth(LocalDate.now())
                .hireDate(LocalDate.now())
                .isResign(false)
                .deptName("admin")
                .jobRankName("admin")
                .jobRoleName("admin")
                .build();

        userRepository.save(user);

        return ResponseInitialAdmin.builder()
                .id(user.getId())
                .employeeNum(user.getEmployeeNum())
                .password(user.getPassword())
                .schemaName(schemaName)
                .build();
    }
}

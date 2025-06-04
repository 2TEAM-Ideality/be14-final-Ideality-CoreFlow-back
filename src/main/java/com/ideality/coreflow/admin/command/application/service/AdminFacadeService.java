package com.ideality.coreflow.admin.command.application.service;

import com.ideality.coreflow.admin.command.application.dto.RequestModifyJobRank;
import com.ideality.coreflow.admin.command.application.dto.RequestUserUpdateByAdmin;
import com.ideality.coreflow.org.command.application.service.JobRankService;
import com.ideality.coreflow.org.command.application.service.JobRoleService;
import com.ideality.coreflow.user.command.application.dto.UserInfoDTO;
import com.ideality.coreflow.user.command.application.service.RoleService;
import com.ideality.coreflow.user.command.application.service.UserOfRoleService;
import com.ideality.coreflow.user.command.application.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminFacadeService {

    private final UserService userService;
    private final RoleService roleService;
    private final UserOfRoleService userOfRoleService;
    private final JobRankService jobRankService;
    private final JobRoleService jobRoleService;

    @Transactional
    public void modifyUserInfoByAdmin(Long userId, RequestUserUpdateByAdmin request) {

        UserInfoDTO updateUserInfo = UserInfoDTO.builder()
                .id(userId)
                .name(request.getName())
                .email(request.getEmail())
                .isResign(request.getIsResign())
                .profileImage(request.getProfileImage())
                .deptName(request.getDeptName())
                .jobRankName(request.getJobRankName())
                .jobRoleName(request.getJobRoleName())
                .build();

        // 유저 정보 수정
        userService.updateUser(updateUserInfo);

        if (request.getIsCreation() != null) {
            long roleId = roleService.findRoleByName("Creator");

            userOfRoleService.updateAuthorities(request.getIsCreation(), userId, roleId);
        }
    }

    @Transactional
    public void registJobRank(String name) {
        jobRankService.registJobRank(name);
    }

    @Transactional
    public void deleteJobRank(long id) {
        jobRankService.deleteJobRank(id);
    }

    @Transactional
    public void modifyJobRank(RequestModifyJobRank request) {
        jobRankService.updateJobRank(request.getPrevJobRankName(), request.getNewJobRankName());
    }
}

package com.ideality.coreflow.admin.command.application.service;

import com.ideality.coreflow.admin.command.application.dto.RequestModifyJobROle;
import com.ideality.coreflow.admin.command.application.dto.RequestModifyJobRank;
import com.ideality.coreflow.admin.command.application.dto.RequestUserUpdateByAdmin;
import com.ideality.coreflow.org.command.application.service.JobRankService;
import com.ideality.coreflow.org.command.application.service.JobRoleService;
import com.ideality.coreflow.user.command.application.dto.RequestUpdateProfile;
import com.ideality.coreflow.user.command.application.dto.UserInfoDTO;
import com.ideality.coreflow.user.command.application.service.RoleService;
import com.ideality.coreflow.user.command.application.service.UserOfRoleService;
import com.ideality.coreflow.user.command.application.service.UserService;
import com.ideality.coreflow.user.command.domain.aggregate.OrgType;
import com.ideality.coreflow.user.command.domain.aggregate.RoleName;
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

//        UserInfoDTO updateUserInfo = UserInfoDTO.builder()
//                .id(userId)
//                .name(request.getName())
//                .email(request.getEmail())
//                .birth(request.getBirth())
//                .hireDate(request.getHireDate())
//                .isResign(request.getIsResign())
//                .resignDate(request.getResignDate())
//                .profileImage(request.getProfileImage())
//                .deptName(request.getDeptName())
//                .jobRankName(request.getJobRankName())
//                .jobRoleName(request.getJobRoleName())
//                .build();

        // 유저 정보 수정
//        userService.updateUser(new RequestUpdateProfile(updateUserInfo.getId(), ));

        if (request.getIsCreation() != null) {
            long roleId = roleService.findRoleByName(RoleName.CREATOR);

            userOfRoleService.updateAuthorities(request.getIsCreation(), userId, roleId);
        }
    }

    @Transactional
    public void registJobRank(String name) {
        jobRankService.registJobRank(name);
    }

    @Transactional
    public void modifyJobRank(RequestModifyJobRank request) {
        jobRankService.updateJobRank(request.getPrevJobRankName(), request.getNewJobRankName());
        userService.updateUserOrg(OrgType.JOB_RANK, request.getPrevJobRankName(), request.getNewJobRankName());
    }

    @Transactional
    public void deleteJobRank(long id) {
        jobRankService.deleteJobRank(id);
    }

    @Transactional
    public void registJobRole(String name) {
        jobRoleService.registJobRole(name);
    }

    @Transactional
    public void modifyJobRole(RequestModifyJobROle request) {
        jobRoleService.updateJobRole(request.getPrevJobRoleName(), request.getNewJobRoleName());
        userService.updateUserOrg(OrgType.JOB_ROLE, request.getPrevJobRoleName(), request.getNewJobRoleName());
    }

    @Transactional
    public void deleteJobRole(long id) {
        jobRoleService.deleteJobRole(id);
    }
}

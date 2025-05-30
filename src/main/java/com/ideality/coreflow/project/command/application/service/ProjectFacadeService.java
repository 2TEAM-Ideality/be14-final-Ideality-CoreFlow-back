package com.ideality.coreflow.project.command.application.service;

import com.ideality.coreflow.common.exception.BaseException;
import com.ideality.coreflow.project.command.application.dto.RequestTaskDTO;
import com.ideality.coreflow.project.command.application.dto.TaskParticipantDTO;
import com.ideality.coreflow.project.command.domain.aggregate.Work;
import com.ideality.coreflow.project.query.service.DeptQueryService;
import com.ideality.coreflow.user.command.domain.aggregate.User;
import com.ideality.coreflow.user.query.dto.ParticipantUserDTO;
import com.ideality.coreflow.user.query.service.UserQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.ideality.coreflow.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProjectFacadeService {

    private final TaskService taskService;
    private final DeptService deptService;
    private final RelationService relationService;
    private final WorkDeptService workDeptService;
    private final ParticipantService participantService;

    private final DeptQueryService deptQueryService;
    private final UserQueryService userQueryService;

    @Transactional
    public Long createTask(RequestTaskDTO requestTaskDTO) {
        /* 설명. 순서에 대한 생각
         *  task 생성 요청(권한 확인 : 401) -> work 테이블 생성 -> 프로젝트 id와 함께
         *  -> 작업간 관계, 작업 별 참여 부서 추가 -> 부서 회원 조회
         *  -> 부서 회원들을 전부 참여 인원에 task로서 추가
         *  -> 알림에 insert -> 각 회원 별 알림 수신자만큼 데이터 추가
        * */

        /* 설명. 태스크 부터 */
        Long taskId = taskService.createTask(requestTaskDTO);
        /* 설명. 작업 간 관계 저장 */
        taskService.validateWorkId(requestTaskDTO.getPrevWorkId(), requestTaskDTO.getNextWorkId());
        relationService.appendRelation
                (requestTaskDTO.getPrevWorkId(), requestTaskDTO.getNextWorkId(), taskId);

        /* 설명. 부서에 대한 validaion check */
        deptService.validateDeptName(requestTaskDTO.getDeptName());

        /* 설명. 부서 id 조회 */
        Long deptId = deptQueryService.findIdByName(requestTaskDTO.getDeptName());

        /* 설명. 작업 별 부서 id 추가 */
        workDeptService.createWorkDept(taskId, deptId);

        /* 설명. 회원에서 부서 이름으로 모든 회원 조회 */
        List<ParticipantUserDTO> userByDept = userQueryService.selectByDeptName(requestTaskDTO.getDeptName());

        List<TaskParticipantDTO> taskParticipants = userByDept.stream()
                .map(user -> new TaskParticipantDTO(taskId, user.getUserId(), user.getRoleId()))
                .toList();

        /* 설명. 참여 인원에 insert */
        participantService.createParticipants(taskParticipants);

        return taskId;
    }
}

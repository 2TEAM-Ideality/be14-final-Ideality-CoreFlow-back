package com.ideality.coreflow.project.command.application.service;

import com.ideality.coreflow.project.command.application.dto.RequestTaskDTO;
import com.ideality.coreflow.project.command.application.dto.TaskParticipantDTO;
import com.ideality.coreflow.project.query.service.DeptQueryService;
import com.ideality.coreflow.user.query.dto.ParticipantUserDTO;
import com.ideality.coreflow.user.query.service.UserQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;


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
        log.info("태스트 생성 완료");
        /* 설명. 작업 간 관계 저장 */
        taskService.validateWorkId(requestTaskDTO.getPrevWorkId(), requestTaskDTO.getNextWorkId());
        log.info("태스크 유효 체크");
        relationService.appendRelation
                (requestTaskDTO.getPrevWorkId(), requestTaskDTO.getNextWorkId(), taskId);
        log.info("태스트별 관계 설정 완료");
        /* 설명. 부서에 대한 validaion check */
        deptService.validateDeptName(requestTaskDTO.getDeptName());
        log.info("부서 유효 체크");
        /* 설명. 부서 id 조회 */
        Long deptId = deptQueryService.findIdByName(requestTaskDTO.getDeptName());
        log.info("부서 조회 끝");
        /* 설명. 작업 별 부서 id 추가 */
        workDeptService.createWorkDept(taskId, deptId);
        log.info("부서 추가");
        /* 설명. 회원에서 부서 이름으로 모든 회원 조회 */
        List<ParticipantUserDTO> userByDept = userQueryService.selectByDeptName(requestTaskDTO.getDeptName());

        List<TaskParticipantDTO> taskParticipants = userByDept.stream()
                .map(user -> new TaskParticipantDTO(taskId, user.getUserId(), user.getRoleId()))
                .toList();

        /* 설명. 참여 인원에 insert */
        participantService.createParticipants(taskParticipants);
        log.info("참여자 생성");
        return taskId;
    }
}

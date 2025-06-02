package com.ideality.coreflow.project.command.application.service.facade;

import com.ideality.coreflow.project.command.application.dto.RequestTaskDTO;
import com.ideality.coreflow.project.command.application.dto.TaskParticipantDTO;
import com.ideality.coreflow.project.command.application.service.*;
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

    private final ProjectService projectService;
    private final TaskService taskService;
    private final RelationService relationService;
    private final WorkDeptService workDeptService;
    private final ParticipantService participantService;

    private final DeptQueryService deptQueryService;
    private final UserQueryService userQueryService;

    @Transactional
    public Long createTask(RequestTaskDTO requestTaskDTO) {
        /* 설명.
         *  실제 순서 수정 -> fall fast 원칙으로
         *  유효성 검사를 수행 한 다음에, db에 write를 수행할 것
        *  */

        /* 설명. 부서 id 조회 -> 조회 해오면서, 예외처리까지
         *  이것 또한 fall-fast 원칙에 맞게 수행
        * */
//        projectService.existsById(requestTaskDTO.getProjectId());
//        taskService.validateWorkId(requestTaskDTO.getPrevWorkId(), requestTaskDTO.getNextWorkId());
//        log.info("유효성 검사 완료");
        Long deptId = deptQueryService.findIdByName(requestTaskDTO.getDeptName());
        log.info("부서 조회 끝");

        /* 설명. 태스크 부터 */
        Long taskId = taskService.createTask(requestTaskDTO);
        relationService.appendRelation
                (requestTaskDTO.getPrevWorkId(), requestTaskDTO.getNextWorkId(), taskId);
        log.info("태스크 및 태스트별 관계 설정 완료");

        /* 설명. 작업 별 부서 id 추가 */
        workDeptService.createWorkDept(taskId, deptId);
        log.info("부서 추가");
        /* 설명. 회원에서 부서 이름으로 모든 회원 조회
         *  부서에 대한 예외처리가 수행되어 있고 -> 태스크가 생성이 되지 않고
         *  조회를 해오는 것 -> 불 필요한 조회 리소스 낭비
        * */
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

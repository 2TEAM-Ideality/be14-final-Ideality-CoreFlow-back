package com.ideality.coreflow.project.command.application.service.facade;

import com.ideality.coreflow.project.command.application.dto.RequestTaskDTO;
import com.ideality.coreflow.project.command.application.dto.TaskParticipantDTO;
import com.ideality.coreflow.project.command.application.service.*;
import com.ideality.coreflow.project.command.domain.aggregate.TargetType;
import com.ideality.coreflow.project.query.service.DeptQueryService;
import com.ideality.coreflow.user.query.service.UserQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


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
        List<Long> deptIds = requestTaskDTO.getDeptList().stream()
                .map(deptQueryService::findIdByName)
                .toList();
        log.info("부서 조회 끝");

        /* 설명. 태스크 부터 */
        Long taskId = taskService.createTask(requestTaskDTO);
        relationService.appendRelation
                (requestTaskDTO.getSource(), requestTaskDTO.getTarget(), taskId);
        log.info("태스크 및 태스트별 관계 설정 완료");

//        for (int i = 0; i < requestTaskDTO.getDeptList().size(); i++) {
//            String deptName = requestTaskDTO.getDeptList().get(i);
//            Long deptId = deptIds.get(i);
//
//            /* 설명. 작업 별 부서 id 추가 */
//            workDeptService.createWorkDept(taskId, deptId);
//            log.info("부서 추가");
//
//            // 참여자 등록
//            List<Long> users = userQueryService.selectAllUserByDeptName(deptName);
//            List<TaskParticipantDTO> taskParticipants = users.stream()
//                    .map(userId -> new TaskParticipantDTO(taskId, userId))
//                    .toList();
//            participantService.createParticipants(taskParticipants);
//            log.info("참여자 가져옴");
//            // 팀장 권한 부여
//            Long teamLeaderId = userQueryService.selectLeaderByDeptName(deptName);
//            participantService.updateParticipantsLeader(teamLeaderId, taskId);
//        }

        /* 설명. 불필요한 읽기 및 쓰기를 방지, 하나의 for문은 쓰기만 처리하게 하기 */
        List<String> deptNames = requestTaskDTO.getDeptList().stream().distinct().toList();

        Map<String, Long> deptIdMap = deptNames.stream()
                .collect(Collectors.toMap(name -> name, deptQueryService::findIdByName));

        Map<String, List<Long>> deptUsersMap = deptNames.stream()
                .collect(Collectors.toMap(name -> name, userQueryService::selectAllUserByDeptName));

        Map<String, Long> deptLeaderMap = deptNames.stream()
                .collect(Collectors.toMap(name -> name, userQueryService::selectLeaderByDeptName));

        for (String deptName : requestTaskDTO.getDeptList()) {
            Long deptId = deptIdMap.get(deptName);
            workDeptService.createWorkDept(taskId, deptId);

            List<Long> userIds = deptUsersMap.get(deptName);
            List<TaskParticipantDTO> participants = userIds.stream()
                    .map(userId -> new TaskParticipantDTO(taskId, userId))
                    .toList();
            participantService.createParticipants(participants);
            log.info("여기까지는 정상");
            Long leaderId = deptLeaderMap.get(deptName);
            log.info("leader id: {}", leaderId.toString());
            participantService.updateTeamLeader(leaderId, taskId, TargetType.TASK);
        }
        return taskId;
    }

    @Transactional
    public Long updateStatusProgress(Long taskId) {
        Long updateTaskId = taskService.updateStatusProgress(taskId);
        return updateTaskId;
    }
}

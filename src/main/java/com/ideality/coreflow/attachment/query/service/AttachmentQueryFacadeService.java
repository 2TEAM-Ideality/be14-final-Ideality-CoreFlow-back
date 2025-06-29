package com.ideality.coreflow.attachment.query.service;

import com.ideality.coreflow.approval.query.service.ApprovalQueryService;
import com.ideality.coreflow.attachment.query.dto.GetDeptInfoDTO;
import com.ideality.coreflow.attachment.query.dto.GetTaskInfoDTO;
import com.ideality.coreflow.attachment.query.dto.ReportAttachmentDTO;
import com.ideality.coreflow.attachment.query.dto.ResponseCommentAttachmentDTO;
import com.ideality.coreflow.comment.query.service.CommentQueryService;
import com.ideality.coreflow.common.exception.BaseException;
import com.ideality.coreflow.common.exception.ErrorCode;
import com.ideality.coreflow.project.query.service.ParticipantQueryService;
import com.ideality.coreflow.project.query.service.TaskQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.ideality.coreflow.common.exception.ErrorCode.TASK_NOT_FOUND;

@Service
@Slf4j
@RequiredArgsConstructor
public class AttachmentQueryFacadeService {
    private final AttachmentQueryService attachmentQueryService;
    private final CommentQueryService commentQueryService;
    private final TaskQueryService taskQueryService;
    private final ParticipantQueryService participantQueryService;
    private final ApprovalQueryService approvalQueryService;

    public List<ResponseCommentAttachmentDTO> getAttachmentListByComment(Long taskId, Long userId) {

        /* 설명. 일단 프로젝트 참여자인지 확인 */
        Long projectId = taskQueryService.getProjectId(taskId);

        /* 설명. 잘못된 접근에 대한 예외처리 */
        if (projectId == null) {
            throw new BaseException(TASK_NOT_FOUND);
        }

        /* 설명. 프로젝트 참여자 -> 댓글을 작성할 수 있는 권한이 있는가 */
        boolean isParticipant = participantQueryService.isParticipant(userId, projectId);

        if (!isParticipant) {
            throw new BaseException(ErrorCode.NOT_COMMENT_WRITER);
        }

        /* 설명. 하나의 태스크 안에 있는 모든 댓글별 첨부파일 조회 */
        List<Long> commentIds = commentQueryService.selectAllCommentsByAttachment(taskId);

        return attachmentQueryService.getAttachmentsByCommentId(commentIds);
    }


    // 프로젝트에 관한 모든 파일
    public List<ReportAttachmentDTO> getAttachmentsByProjectId(Long projectId) {
        List<ReportAttachmentDTO> response = attachmentQueryService.getAttachmentsByProjectId(projectId);

        // 1. 기존 결재 관련 TaskName 매핑
        List<Long> uploaderIdList = response.stream()
                .map(ReportAttachmentDTO::getUploaderId)
                .toList();

        List<String> taskNameList = approvalQueryService.selectProjectDeliverable(uploaderIdList);
        for (int i = 0; i < response.size(); i++) {
            response.get(i).setTaskName(taskNameList.get(i));
        }

        // 2. 댓글 기준 TaskName 매핑
        List<GetTaskInfoDTO> commentInfoList = commentQueryService.selectAllCommentsByTaskList(projectId); // commentId + taskId + taskName
        List<Long> commentIds = commentInfoList.stream()
                .map(GetTaskInfoDTO::getCommentId)
                .toList();

        List<ReportAttachmentDTO> commentAttachments = attachmentQueryService.getAttachmentsByCommentIdForProject(commentIds);

        // 3. 댓글 첨부파일 → TaskName 매핑
        Map<Long, String> commentIdToTaskName = commentInfoList.stream()
                .collect(Collectors.toMap(GetTaskInfoDTO::getCommentId, GetTaskInfoDTO::getTaskName));
        Map<Long, Long> commentIdToTaskId = commentInfoList.stream()
                .collect(Collectors.toMap(GetTaskInfoDTO::getCommentId, GetTaskInfoDTO::getTaskId));

        for (ReportAttachmentDTO dto : commentAttachments) {
            Long commentId = dto.getTargetId();
            dto.setTaskName(commentIdToTaskName.get(commentId));
            dto.setTaskId(commentIdToTaskId.get(commentId)); // 프론트 라우팅용
        }

        // 4. 모든 결과 병합
        response.addAll(commentAttachments);

        // 5. TaskId 기준 DeptName 조회 후 매핑
        List<Long> allTaskIds = response.stream()
                .map(ReportAttachmentDTO::getTaskId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        List<GetDeptInfoDTO> rawList = taskQueryService.selectAllDeptByTaskId(allTaskIds);

        Map<Long, List<String>> taskIdToDeptNames = rawList.stream()
                .collect(Collectors.groupingBy(
                        GetDeptInfoDTO::getTaskId,
                        Collectors.mapping(GetDeptInfoDTO::getDeptName, Collectors.toList())
                ));

        for (ReportAttachmentDTO dto : response) {
            Long taskId = dto.getTaskId();
            if (taskId != null) {
                List<String> deptNames = taskIdToDeptNames.get(taskId);
                dto.setDeptName(deptNames); // setDeptNameList는 List<String>을 받는 메서드
            }
        }

        return response;
    }

}

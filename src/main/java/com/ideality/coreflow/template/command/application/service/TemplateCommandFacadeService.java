package com.ideality.coreflow.template.command.application.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.ideality.coreflow.project.command.application.service.ProjectService;
import com.ideality.coreflow.project.query.dto.ResponseTaskDTO;
import com.ideality.coreflow.project.query.dto.TaskDeptDTO;
import com.ideality.coreflow.project.query.service.TaskQueryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ideality.coreflow.attachment.command.application.service.AttachmentCommandService;
import com.ideality.coreflow.attachment.command.domain.aggregate.FileTargetType;
import com.ideality.coreflow.common.exception.BaseException;
import com.ideality.coreflow.common.exception.ErrorCode;
import com.ideality.coreflow.infra.s3.S3Service;
import com.ideality.coreflow.template.command.application.dto.RequestCreateTemplateDTO;
import com.ideality.coreflow.template.command.application.dto.RequestProjectTemplateDTO;
import com.ideality.coreflow.template.command.application.dto.RequestUpdateTemplateDTO;
import com.ideality.coreflow.template.command.domain.aggregate.Template;
import com.ideality.coreflow.template.query.dto.EdgeDTO;
import com.ideality.coreflow.template.query.dto.NodeDTO;
import com.ideality.coreflow.template.query.dto.TemplateDataDTO;
import com.ideality.coreflow.template.query.dto.TemplateNodeDataDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class TemplateCommandFacadeService {

	private final ProjectService projectService;
	private final TaskQueryService taskQueryService;
	private final TemplateCommandService templateCommandService;
	private final AttachmentCommandService attachmentCommandService;
	private final S3Service s3Service;
	private final ObjectMapper objectMapper;	// Jackson 라이브러리 제공 클래스 (Json 직렬화, 역직렬화에서 사용)


	// 템플릿 생성
	@Transactional
	public void createTemplate(RequestCreateTemplateDTO requestDTO) {

		// 1. 템플릿 생성
		Template template = templateCommandService.createTemplate(requestDTO);

		// 2. JSON 직렬화
		String json = buildTemplateJsonData(requestDTO.getNodeList(), requestDTO.getEdgeList());

		// 3. 참여 부서 추출 및 저장
		Set<Long> deptIds = extractUniqueDeptIds(requestDTO.getNodeList());
		saveTemplateDepts(template.getId(), deptIds);

		// 4. S3에 업로드 & AttachmentEntity 생성 및 DB 저장
		uploadAndSaveAttachment(template.getId(), json, requestDTO.getCreatedBy());

	}


	// TODO. 프로젝트 템플릿화
	@Transactional
	public void createTemplateByProject(RequestProjectTemplateDTO requestDTO) {
		// ID로 해당 프로젝트 찾기
		// 프로젝트 상태 - 완료인지 확인
		if (!projectService.isCompleted(requestDTO.getProjectId())) {
			throw new BaseException(ErrorCode.PROJECT_NOT_COMPLETED);
		}
		// 프로젝트 id 로 해당하는 태스크 목록 가져오기
		// 거기서 템플릿에서 활용되는 것만 뽑아서 템플릿으로 생성해야 함.
		List<ResponseTaskDTO> taskList = taskQueryService.selectTasks(requestDTO.getProjectId());

		// TODO.  duration, taskCount 넘겨주어야 함.

		int durtaion = 30;

		Template template = templateCommandService.createTemplateByProject(requestDTO, taskList.size(), durtaion);

		// 노드 리스트 만들기
		List<NodeDTO> nodeList = taskListToNode(taskList);
		// 엣지 리스트 정보 가져오기
		List<EdgeDTO> edgeList = taskQueryService.getEdgeList(taskList);

		// JSON 직렬화
		String json = buildTemplateJsonData(nodeList, edgeList);

		// 3. 참여 부서 추출 및 저장
		Set<Long> deptIds = extractUniqueDeptIds(nodeList);
		saveTemplateDepts(template.getId(), deptIds);

		// 4. S3에 업로드 & AttachmentEntity 생성 및 DB 저장
		uploadAndSaveAttachment(template.getId(), json, requestDTO.getCreatedBy());

	}



	// 템플릿 수정
	@Transactional
	public void updateTemplate(Long templateId, RequestUpdateTemplateDTO requestDTO) {
		// TODO. 요청자를 updatedBy 로 변경하는 부분 추가해야 함.

		// 1. 템플릿 정보 변경
		templateCommandService.updateTemplateInfo(
			templateId,
			requestDTO.getName(),
			requestDTO.getDescription(),
			requestDTO.getDuration(),
			requestDTO.getTaskCount(),
			requestDTO.getUpdatedBy()
		);

		// 2. Json 직렬화
		String json = buildTemplateJsonData(requestDTO.getNodeList(), requestDTO.getEdgeList());

		// 3. 참여 부서 관계
		templateCommandService.deleteAllTemplateDepts(templateId);

		// 3-2. 새로운 부서 ID 추출 및 저장
		Set<Long> deptIds = extractUniqueDeptIds(requestDTO.getNodeList());
		saveTemplateDepts(templateId, deptIds);

		//3. S3 업로드
		uploadAndSaveAttachment(templateId, json, requestDTO.getUpdatedBy());
	}

	// 템플릿 삭제
	@Transactional
	public void deleteTemplate(Long templateId) {

		// 1. 템플릿 삭제 여부 변경
		templateCommandService.deleteTemplate(templateId);

		// 2. 첨부파일 삭제 여부 변경
		attachmentCommandService.deleteAttachment(templateId, FileTargetType.TEMPLATE);
	}

	// 참여 부서 추출
	private Set<Long> extractUniqueDeptIds(List<NodeDTO> nodeList) {
		return nodeList.stream()
			.flatMap(node ->
				Optional.ofNullable(node.getData().getDeptList())
					.orElse(List.of())
					.stream()
					.map(TaskDeptDTO::getId)
			)
			.collect(Collectors.toSet());
	}

	// 참여 부서 저장
	private void saveTemplateDepts(Long templateId, Set<Long> deptIds) {
		for (Long deptId : deptIds) {
			templateCommandService.saveTemplateDept(templateId, deptId);
		}
	}

	// 노드 리스트 & 엣지 리스트 JSON화
	private String buildTemplateJsonData(List<NodeDTO> nodeList, List<EdgeDTO> edgeList) {
		TemplateDataDTO data = TemplateDataDTO.builder()
			.nodeList(nodeList)
			.edgeList(edgeList)
			.build();

		return serializeJsonOrThrow(data);

	}

	// 첨부파일 관련 저장
	private void uploadAndSaveAttachment(Long templateId, String json, Long createdBy) {
		// 같은 타겟 아이디로 첨부파일이 있는 지 확인
		if(attachmentCommandService.findAttachmentByTargetId(templateId)){
			throw new BaseException(ErrorCode.DUPLICATED_TARGET_ID);
		}
		String fileName = templateId + ".json";
		String folder = "template-json";

		// s3 업로드
		String fileUrl = uploadToS3OrThrow(json, folder, fileName);

		// 첨부파일 테이블 저장
		attachmentCommandService.createAttachmentForTemplate(
			templateId, fileName, fileUrl, createdBy, json
		);
	}

	// S3 업로드
	private String uploadToS3OrThrow(String json, String folder, String fileName) {
		try {
			return s3Service.uploadJson(json, folder, fileName);
		} catch (Exception e) {
			log.error("S3 업로드 실패", e);
			throw new BaseException(ErrorCode.S3_UPLOAD_FAILED);
		}
	}


	// JSON 직렬화
	private String serializeJsonOrThrow(TemplateDataDTO requestDTO) {
		try {
			return objectMapper.writeValueAsString(Map.of(
				"nodeList", requestDTO.getNodeList(),
				"edgeList", requestDTO.getEdgeList()
			));
		} catch (JsonProcessingException e) {
			log.error("JSON 직렬화 실패", e);
			throw new BaseException(ErrorCode.JSON_SERIALIZATION_ERROR);
		}
	}

	// 태스크 리스트 -> 노드 리스트 변환
	private List<NodeDTO> taskListToNode(List<ResponseTaskDTO> taskList) {
		return taskList.stream()
			.map(task -> {

				TemplateNodeDataDTO data = TemplateNodeDataDTO.builder()
					.label(task.getLabel())
					.description(task.getDescription())
					.slackTime(task.getSlackTime())
					.startBaseLine(String.valueOf(task.getStartBaseLine()))
					.endBaseLine(String.valueOf(task.getEndBaseLine()))
					.deptList(task.getDepts())
					.build();

				// Node의 ID는 문자열이어야 함 TODO. 프로젝트 템플릿화 태스크 아이디는 다르게 할 지 ?
				return NodeDTO.builder()
					.id(String.valueOf(task.getId()))
					.type("custom")
					.data(data)
					.build();
			})
			.collect(Collectors.toList());
	}

}
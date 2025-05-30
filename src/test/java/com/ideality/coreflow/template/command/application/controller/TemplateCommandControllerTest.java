package com.ideality.coreflow.template.command.application.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ideality.coreflow.template.command.application.dto.RequestCreateTemplateDTO;
import com.ideality.coreflow.template.command.application.dto.RequestUpdateTemplateDTO;
import com.ideality.coreflow.template.command.application.service.TemplateCommandFacadeService;
import com.ideality.coreflow.template.command.application.service.TemplateCommandService;
import com.ideality.coreflow.template.query.dto.EdgeDTO;
import com.ideality.coreflow.template.query.dto.PositionDTO;
import com.ideality.coreflow.template.query.dto.TemplateInfoDTO;
import com.ideality.coreflow.template.query.dto.TemplateNodeDTO;
import com.ideality.coreflow.template.query.dto.TemplateNodeDataDTO;

@WebMvcTest(controllers = TemplateCommandController.class)
class TemplateCommandControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private TemplateCommandService templateCommandService;

	@MockitoBean
	private TemplateCommandFacadeService templateCommandFacadeService;

	@Autowired
	private ObjectMapper objectMapper ;


	@Test
	@DisplayName("템플릿 생성 테스트 : POST /api/template")
	void testCreateTemplate() throws Exception {
		RequestCreateTemplateDTO request = RequestCreateTemplateDTO.builder()
			.name("템플릿 이름")
			.taskCount(10)
			.description("템플릿 테스트용 ")
			.duration(50)
			.taskCount(10)
			.createdAt(LocalDateTime.now())
			.createdBy(1L)
			.nodeList(List.of(
				new TemplateNodeDTO(
					"1",
					"custom",
					new PositionDTO(0, 0),
					new TemplateNodeDataDTO(
						"label",
						"도식화",
						List.of("기획팀", "디자인팀"),
						"2025-06-01T00:00:00",
						"2025-06-01T00:00:00"
					)
				)
			))
			.edgeList(List.of(
						new EdgeDTO("e1", "1", "2", "default")
					))
			.build();

		mockMvc.perform(post("/api/template")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.message").value("템플릿이 성공적으로 생성되었습니다.✅"));

	}


	@Test
	@DisplayName("템플릿 수정 테스트 : PUT /api/template")
	void testUpdateTemplate() throws Exception {

		Long templateId = 1L;

		RequestUpdateTemplateDTO request = RequestUpdateTemplateDTO.builder()
			.name("템플릿 이름")
			.taskCount(10)
			.description("업데이트 테스트")
			.duration(50)
			.taskCount(10)
			.updatedAt(LocalDateTime.now())
			.updatedBy(1L)
			.nodeList(List.of(
				new TemplateNodeDTO(
					"1",
					"custom",
					new PositionDTO(0, 0),
					new TemplateNodeDataDTO(
						"label",
						"도식화",
						List.of("기획팀", "디자인팀"),
						"2025-06-01T00:00:00",
						"2025-06-01T00:00:00"
					)
				)
			))
			.edgeList(List.of(
				new EdgeDTO("e1", "1", "2", "default")
			))
			.build();

		mockMvc.perform(put("/api/template/{templateId}", templateId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message").value("템플릿이 성공적으로 수정되었습니다.✅"));
	}

	@Test
	@DisplayName("템플릿 삭제 테스트 : DELETE /api/template")
	void testDeleteTemplate() throws Exception {
		Long templateId = 1L;

		mockMvc.perform(delete("/api/template/{templateId}", templateId))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message").value("템플릿이 성공적으로 삭제되었습니다.✅"));
	}


}
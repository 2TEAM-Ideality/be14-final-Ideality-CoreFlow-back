package com.ideality.coreflow.template.query.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ideality.coreflow.template.query.dto.ResponseTemplateDetailDTO;
import com.ideality.coreflow.template.query.dto.ResponseTemplateListDTO;
import com.ideality.coreflow.template.query.dto.TemplateInfoDTO;
import com.ideality.coreflow.template.query.service.TemplateQueryFacadeService;
import com.ideality.coreflow.template.query.service.TemplateQueryService;

// @SpringBootTest  -> 전체 컨텍스트를 로드 (모든 빈을 스캔)
@WebMvcTest(controllers = TemplateController.class)	// 특정 컨트롤러만 스캔
class TemplateControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private TemplateQueryService templateQueryService;

	@MockitoBean
	private TemplateQueryFacadeService templateQueryFacadeService;

	@Autowired
	private ObjectMapper objectMapper;


	@Test
	@DisplayName("템플릿 목록 조회 : GET /api/template/list")
	void testGetTemplates() throws Exception {
		ResponseTemplateListDTO dto = new ResponseTemplateListDTO();
		// 테스트 후 받을 값 형식 정의
		given(templateQueryService.getAllTemplates()).willReturn(List.of(dto));

		mockMvc.perform(get("/api/template/list"))
			.andExpect(status().isOk())	// http 200 ok 여부
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data").isArray());
	}
	
	@Test
	@DisplayName("템플릿 상세 정보 조회 : GET /api/template/{templateId}")
	void testGetTemplateById() throws Exception {
		// given: 내부 객체들 구성
		TemplateInfoDTO infoDTO = TemplateInfoDTO.builder()
			.id(1)
			.name("템플릿 A")
			.description("상세 설명")
			.createdAt(LocalDateTime.now())
			.createdBy("홍길동")
			.duration(5)
			.taskCount(3)
			.usingProjects(2)
			.url("https://example.com/template.json")
			.build();
		Map<String, Object> dataMap = Map.of(
			"nodes", List.of(Map.of("id", "1", "label", "노드 A")),
			"edges", List.of(Map.of("source", "1", "target", "2"))
		);

		ResponseTemplateDetailDTO detailDTO = ResponseTemplateDetailDTO.builder()
			.templateInfo(infoDTO)
			.templateData(dataMap)
			.build();

		given(templateQueryFacadeService.getTemplateDetail(1L)).willReturn(detailDTO);

		// when, then
		mockMvc.perform(get("/api/template/{templateId}", 1L))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.templateInfo.id").value(1))
			.andExpect(jsonPath("$.data.templateInfo.name").value("템플릿 A"))
			.andExpect(jsonPath("$.data.templateInfo.createdBy").value("홍길동"))
			.andExpect(jsonPath("$.data.templateData.nodes").isArray())
			.andExpect(jsonPath("$.data.templateData.edges").isArray());
	}


}
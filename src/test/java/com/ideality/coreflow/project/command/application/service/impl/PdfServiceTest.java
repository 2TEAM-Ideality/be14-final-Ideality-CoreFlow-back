package com.ideality.coreflow.project.command.application.service.impl;


import com.ideality.coreflow.approval.query.dto.ProjectApprovalDTO;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.thymeleaf.TemplateEngine;


import java.util.List;

class PdfServiceTest {

    private PdfServiceImpl pdfService;

    @BeforeEach
    void setUp() {
        pdfService = new PdfServiceImpl(new TemplateEngine());
    }

    @DisplayName("delayReasonChart() - 지연 사유 차트를 Base64로 반환한다")
    @Test
    void testDelayReasonChart() throws IOException {
        List<ProjectApprovalDTO> delayList = List.of(
                new ProjectApprovalDTO("자재 부족", 3),
                new ProjectApprovalDTO("인력 부족", 5)
        );

        String base64 = pdfService.delayReasonChart(delayList);

        assertNotNull(base64);
        assertTrue(base64.length() > 100);
    }

    @DisplayName("delayTaskChart() - 지연 태스크 차트를 Base64로 반환한다")
    @Test
    void testDelayTaskChart() throws IOException {
        List<CompletedTaskDTO> taskList = List.of(
                new CompletedTaskDTO("기획", 2),
                new CompletedTaskDTO("디자인", 4)
        );

        String base64 = pdfService.delayTaskChart(taskList);

        assertNotNull(base64);
        assertTrue(base64.length() > 100);
    }

    @DisplayName("createOTDChart() - 부서별 OTD 차트를 Base64로 반환한다")
    @Test
    void testCreateOTDChart() throws IOException {
        Map<String, Double> otdMap = Map.of(
                "개발팀", 91.3,
                "디자인팀", 87.5
        );

        String base64 = pdfService.createOTDChart(otdMap);

        assertNotNull(base64);
        assertTrue(base64.length() > 100);
    }

    @DisplayName("createReportPdf() - 템플릿 렌더링 실패 시 response.reset() 호출")
    @Test
    void testCreateReportPdf_withTemplateError() throws Exception {
        HttpServletResponse mockResponse = mock(HttpServletResponse.class);
        TemplateEngine mockEngine = mock(TemplateEngine.class);
        when(mockEngine.process(any(), any())).thenThrow(new RuntimeException("템플릿 렌더링 실패"));

        PdfServiceImpl serviceWithMockEngine = new PdfServiceImpl(mockEngine);

        ProjectReportDTO dummyDto = new ProjectReportDTO();
        dummyDto.setProjectName("테스트 프로젝트");
        dummyDto.setStartDate(LocalDate.now());
        dummyDto.setEndDate(LocalDate.now().plusDays(5));

        serviceWithMockEngine.createReportPdf(dummyDto, mockResponse);

        verify(mockResponse, times(1)).reset();
    }
}


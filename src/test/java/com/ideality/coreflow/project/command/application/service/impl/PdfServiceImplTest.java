package com.ideality.coreflow.project.command.application.service.impl;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;


@SpringBootTest
@AutoConfigureMockMvc
class PdfServiceImplTest {

	@Autowired
	MockMvc mvc;

	@Test
	void reportPdf_생성_검증() throws Exception {
		MockHttpServletResponse res = mvc.perform(get("/api/projects/report")
				.param("projectId", "123")
				.accept("application/pdf"))
			.andExpect(status().isOk())
			.andExpect(content().contentType("application/pdf"))
			.andReturn().getResponse();

		byte[] pdf = res.getContentAsByteArray();
		try (PDDocument doc = PDDocument.load(pdf)) {
			assertTrue(doc.getNumberOfPages() > 0, "페이지가 하나 이상 있어야 합니다.");
			// 필요하면 첫 페이지에 한글 텍스트가 포함되어 있는지 확인
			PDFTextStripper stripper = new PDFTextStripper();
			String text = stripper.getText(doc);
			assertThat(text, containsString("프로젝트 분석 리포트"));
		}
	}

}
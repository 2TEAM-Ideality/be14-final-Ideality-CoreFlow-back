package com.ideality.coreflow.project.command.application.controller;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.nio.file.Files;



@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pdf")
public class PdfController {

    private final TemplateEngine templateEngine;


    @GetMapping("/report/test")
    public void downloadReport(HttpServletResponse response) throws Exception {




        try {
            // 데이터 준비
            Context context = new Context();
            context.setVariable("name", "25ss 퍼셀 자켓");
            context.setVariable("manager", "홍길동 기획팀 팀장");
            context.setVariable("createdAt", "2025-06-09");
            context.setVariable("period", "2025-03-01 ~ 2025-03-31");
            context.setVariable("progress", "98%");

            // 이미지 파일을 Base64로 인코딩하여 context에 추가
            byte[] imageBytes = Files.readAllBytes(new File("src/main/resources/static/ReportLogo.png").toPath());
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);
            context.setVariable("logoBase64", base64Image);

            // 각 페이지 템플릿 렌더링
            String reportHtml = templateEngine.process("report", context);
            reportHtml = reportHtml.replace("&nbsp;", "&#160;"); // 안전 처리


            // 응답 헤더 설정
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=report.pdf");

            try (OutputStream os = response.getOutputStream()) {
                PdfRendererBuilder builder = new PdfRendererBuilder();
                builder.useFastMode();
                builder.useFont(new File("src/main/resources/fonts/NotoSansKR-Regular.ttf"), "Noto Sans KR");
                builder.withHtmlContent(reportHtml, null);
                builder.toStream(os);
                builder.run();
            }
        } catch (Exception e) {
            if (!response.isCommitted()) {
                try {
                    response.reset();
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    response.getWriter().write("PDF 생성 실패: " + e.getMessage());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                System.err.println("PDF 생성 중 예외 발생 (응답 커밋됨): " + e.getMessage());
            }
            e.printStackTrace();
        }
    }


}
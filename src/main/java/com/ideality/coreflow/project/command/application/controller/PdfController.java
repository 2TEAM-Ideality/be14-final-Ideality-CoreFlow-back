package com.ideality.coreflow.project.command.application.controller;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pdf")
public class PdfController {

    private final TemplateEngine templateEngine;

    @GetMapping("/report/download")
    public void downloadReport(HttpServletResponse response) throws Exception {
        try {

            // 1. Thymeleaf에 넘길 데이터 생성
            Context context = new Context();
            context.setVariable("name", "25ss 퍼셀 자켓");
            context.setVariable("manager", "홍길동");
            context.setVariable("period", "2025-03-01 ~ 2025-03-31");
            context.setVariable("progress", "98%");

            // 2. HTML 렌더링
            String html = templateEngine.process("report", context);

            // 3. PDF 응답 설정
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=report.pdf");

            // 4. PDF 변환 및 응답 출력
            try (OutputStream os = response.getOutputStream()) {
                PdfRendererBuilder builder = new PdfRendererBuilder();
                builder.useFastMode();
                builder.useFont(new File("src/main/resources/fonts/NotoSansKR-Regular.ttf"), "Noto Sans KR");
                builder.withHtmlContent(html, null);
                builder.toStream(os);
                builder.run();
            }
        } catch (Exception e) {
            response.reset();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("PDF 생성 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }

//
//        // 1. Thymeleaf에 넘길 데이터 생성
//        Context context = new Context();
//        context.setVariable("name", "25ss 퍼셀 자켓");
//        context.setVariable("manager", "홍길동");
//        context.setVariable("period", "2025-03-01 ~ 2025-03-31");
//        context.setVariable("progress", "98%");
//
//        // 2. HTML 렌더링
//        String html = templateEngine.process("report", context);
//
//        // 3. PDF 응답 설정
//        response.setContentType("application/pdf");
//        response.setHeader("Content-Disposition", "attachment; filename=report.pdf");
//
//        // 4. PDF 변환 및 응답 출력
//        try (OutputStream os = response.getOutputStream()) {
//            PdfRendererBuilder builder = new PdfRendererBuilder();
//            builder.useFastMode(); // 성능 향상 옵션
//            builder.withHtmlContent(html, null);
//            builder.toStream(os);
//            builder.run();
//        }
//
//    }
}
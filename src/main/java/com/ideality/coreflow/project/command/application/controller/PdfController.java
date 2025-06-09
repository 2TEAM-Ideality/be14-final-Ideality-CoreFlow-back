package com.ideality.coreflow.project.command.application.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.PieChart;
import org.knowm.xchart.PieChartBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

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
            context.setVariable("director", "홍길동 기획팀 팀장");
            context.setVariable("createdAt", "2025-06-09");
            context.setVariable("period", "2025-03-01 ~ 2025-03-31");
            context.setVariable("progress", "98%");

            Map<String, Object> designTeam = new LinkedHashMap<>();
            designTeam.put("name", "디자인 팀");
            designTeam.put("count", 3);
            designTeam.put("manager", "홍길동 디자인팀 과장");
            designTeam.put("members", List.of("홍길동 디자인팀 과장", "홍길동 디자인팀 과장", "홍길동 디자인팀 과장"));

            Map<String, Object> planningTeam = new LinkedHashMap<>();
            planningTeam.put("name", "기획팀");
            planningTeam.put("count", 2);
            planningTeam.put("manager", "홍길동 디자인팀 과장");
            planningTeam.put("members", List.of("홍길동 디자인팀 과장", "홍길동 디자인팀 과장"));

            Map<String, Object> productionTeam = new LinkedHashMap<>();
            productionTeam.put("name", "생산팀");
            productionTeam.put("count", 5);
            productionTeam.put("manager", "홍길동 디자인팀 과장");
            productionTeam.put("members", List.of("홍길동 디자인팀 과장", "홍길동 디자인팀 과장", "홍길동 디자인팀 과장", "홍길동 디자인팀 과장", "홍길동 디자인팀 과장"));

            List<Map<String, Object>> teamList = List.of(designTeam, planningTeam, productionTeam);

            context.setVariable("teamList", teamList);


            // 이미지 파일을 Base64로 인코딩하여 context에 추가
            byte[] imageBytes = Files.readAllBytes(new File("src/main/resources/static/ReportLogo.png").toPath());
            String coverLogo = Base64.getEncoder().encodeToString(imageBytes);
            context.setVariable("coverLogo", coverLogo);

            // ContentLogo
            byte[] contentLogoBytes = Files.readAllBytes(new File("src/main/resources/static/ContentLogoFull.png").toPath());
            String contentLogo = Base64.getEncoder().encodeToString(contentLogoBytes);
            context.setVariable("contentLogo", contentLogo);

            // ------------------- 02. 작업 공정 ----------------------------
            // 작업 공정 목록


            List<Map<String, Object>> taskList = List.of(
                Map.of(
                    "taskName", "패턴 생성",
                    "progress", 95,
                    "baseStart", "2025-03-05",
                    "baseEnd", "2025-03-08",
                    "realStart", "2025-03-05",
                    "realEnd", "2025-03-09",
                    "delay", 1,
                    "status", "지연"
                ),
                Map.of(
                    "taskName", "샘플 제작",
                    "progress", 100,
                    "baseStart", "2025-03-09",
                    "baseEnd", "2025-03-12",
                    "realStart", "2025-03-09",
                    "realEnd", "2025-03-12",
                    "delay", 0,
                    "status", "정상"
                ),
                Map.of(
                    "taskName", "피팅 테스트",
                    "progress", 98,
                    "baseStart", "2025-03-13",
                    "baseEnd", "2025-03-15",
                    "realStart", "2025-03-13",
                    "realEnd", "2025-03-17",
                    "delay", 2,
                    "status", "지연"
                ),
                Map.of(
                    "taskName", "피팅 테스트",
                    "progress", 98,
                    "baseStart", "2025-03-13",
                    "baseEnd", "2025-03-15",
                    "realStart", "2025-03-13",
                    "realEnd", "2025-03-17",
                    "delay", 2,
                    "status", "지연"
                ),
                Map.of(
                    "taskName", "피팅 테스트",
                    "progress", 98,
                    "baseStart", "2025-03-13",
                    "baseEnd", "2025-03-15",
                    "realStart", "2025-03-13",
                    "realEnd", "2025-03-17",
                    "delay", 2,
                    "status", "지연"
                ),
                Map.of(
                    "taskName", "피팅 테스트",
                    "progress", 98,
                    "baseStart", "2025-03-13",
                    "baseEnd", "2025-03-15",
                    "realStart", "2025-03-13",
                    "realEnd", "2025-03-17",
                    "delay", 2,
                    "status", "지연"
                ),
                Map.of(
                    "taskName", "피팅 테스트",
                    "progress", 98,
                    "baseStart", "2025-03-13",
                    "baseEnd", "2025-03-15",
                    "realStart", "2025-03-13",
                    "realEnd", "2025-03-17",
                    "delay", 2,
                    "status", "지연"
                ),
                Map.of(
                    "taskName", "피팅 테스트",
                    "progress", 98,
                    "baseStart", "2025-03-13",
                    "baseEnd", "2025-03-15",
                    "realStart", "2025-03-13",
                    "realEnd", "2025-03-17",
                    "delay", 2,
                    "status", "지연"
                ),
                Map.of(
                    "taskName", "피팅 테스트",
                    "progress", 98,
                    "baseStart", "2025-03-13",
                    "baseEnd", "2025-03-15",
                    "realStart", "2025-03-13",
                    "realEnd", "2025-03-17",
                    "delay", 2,
                    "status", "지연"
                ),
                Map.of(
                    "taskName", "피팅 테스트",
                    "progress", 98,
                    "baseStart", "2025-03-13",
                    "baseEnd", "2025-03-15",
                    "realStart", "2025-03-13",
                    "realEnd", "2025-03-17",
                    "delay", 2,
                    "status", "지연"
                ),
                Map.of(
                    "taskName", "피팅 테스트",
                    "progress", 98,
                    "baseStart", "2025-03-13",
                    "baseEnd", "2025-03-15",
                    "realStart", "2025-03-13",
                    "realEnd", "2025-03-17",
                    "delay", 2,
                    "status", "지연"
                ),
                Map.of(
                    "taskName", "피팅 테스트",
                    "progress", 98,
                    "baseStart", "2025-03-13",
                    "baseEnd", "2025-03-15",
                    "realStart", "2025-03-13",
                    "realEnd", "2025-03-17",
                    "delay", 2,
                    "status", "지연"
                )
                ,Map.of(
                    "taskName", "피팅 테스트",
                    "progress", 98,
                    "baseStart", "2025-03-13",
                    "baseEnd", "2025-03-15",
                    "realStart", "2025-03-13",
                    "realEnd", "2025-03-17",
                    "delay", 2,
                    "status", "지연"
                ),
                Map.of(
                    "taskName", "피팅 테스트",
                    "progress", 98,
                    "baseStart", "2025-03-13",
                    "baseEnd", "2025-03-15",
                    "realStart", "2025-03-13",
                    "realEnd", "2025-03-17",
                    "delay", 2,
                    "status", "지연"
                ),
                Map.of(
                    "taskName", "피팅 테스트",
                    "progress", 98,
                    "baseStart", "2025-03-13",
                    "baseEnd", "2025-03-15",
                    "realStart", "2025-03-13",
                    "realEnd", "2025-03-17",
                    "delay", 2,
                    "status", "지연"
                ),
                Map.of(
                    "taskName", "피팅 테스트",
                    "progress", 98,
                    "baseStart", "2025-03-13",
                    "baseEnd", "2025-03-15",
                    "realStart", "2025-03-13",
                    "realEnd", "2025-03-17",
                    "delay", 2,
                    "status", "지연"
                ),
                Map.of(
                    "taskName", "피팅 테스트",
                    "progress", 98,
                    "baseStart", "2025-03-13",
                    "baseEnd", "2025-03-15",
                    "realStart", "2025-03-13",
                    "realEnd", "2025-03-17",
                    "delay", 2,
                    "status", "지연"
                ),
                Map.of(
                    "taskName", "피팅 테스트",
                    "progress", 98,
                    "baseStart", "2025-03-13",
                    "baseEnd", "2025-03-15",
                    "realStart", "2025-03-13",
                    "realEnd", "2025-03-17",
                    "delay", 2,
                    "status", "지연"
                ),
                Map.of(
                    "taskName", "피팅 테스트",
                    "progress", 98,
                    "baseStart", "2025-03-13",
                    "baseEnd", "2025-03-15",
                    "realStart", "2025-03-13",
                    "realEnd", "2025-03-17",
                    "delay", 2,
                    "status", "지연"
                ),
                Map.of(
                    "taskName", "피팅 테스트",
                    "progress", 98,
                    "baseStart", "2025-03-13",
                    "baseEnd", "2025-03-15",
                    "realStart", "2025-03-13",
                    "realEnd", "2025-03-17",
                    "delay", 2,
                    "status", "지연"
                ),
                Map.of(
                    "taskName", "피팅 테스트",
                    "progress", 98,
                    "baseStart", "2025-03-13",
                    "baseEnd", "2025-03-15",
                    "realStart", "2025-03-13",
                    "realEnd", "2025-03-17",
                    "delay", 2,
                    "status", "지연"
                ),
                Map.of(
                    "taskName", "피팅 테스트",
                    "progress", 98,
                    "baseStart", "2025-03-13",
                    "baseEnd", "2025-03-15",
                    "realStart", "2025-03-13",
                    "realEnd", "2025-03-17",
                    "delay", 2,
                    "status", "지연"
                ),
                Map.of(
                    "taskName", "피팅 테스트",
                    "progress", 98,
                    "baseStart", "2025-03-13",
                    "baseEnd", "2025-03-15",
                    "realStart", "2025-03-13",
                    "realEnd", "2025-03-17",
                    "delay", 2,
                    "status", "지연"
                ),
                Map.of(
                    "taskName", "피팅 테스트",
                    "progress", 98,
                    "baseStart", "2025-03-13",
                    "baseEnd", "2025-03-15",
                    "realStart", "2025-03-13",
                    "realEnd", "2025-03-17",
                    "delay", 2,
                    "status", "지연"
                ),
                Map.of(
                    "taskName", "피팅 테스트",
                    "progress", 98,
                    "baseStart", "2025-03-13",
                    "baseEnd", "2025-03-15",
                    "realStart", "2025-03-13",
                    "realEnd", "2025-03-17",
                    "delay", 2,
                    "status", "지연"
                ),
                Map.of(
                    "taskName", "피팅 테스트",
                    "progress", 98,
                    "baseStart", "2025-03-13",
                    "baseEnd", "2025-03-15",
                    "realStart", "2025-03-13",
                    "realEnd", "2025-03-17",
                    "delay", 2,
                    "status", "지연"
                ),
                Map.of(
                    "taskName", "피팅 테스트",
                    "progress", 98,
                    "baseStart", "2025-03-13",
                    "baseEnd", "2025-03-15",
                    "realStart", "2025-03-13",
                    "realEnd", "2025-03-17",
                    "delay", 2,
                    "status", "지연"
                ),
                Map.of(
                    "taskName", "피팅 테스트",
                    "progress", 98,
                    "baseStart", "2025-03-13",
                    "baseEnd", "2025-03-15",
                    "realStart", "2025-03-13",
                    "realEnd", "2025-03-17",
                    "delay", 2,
                    "status", "지연"
                ),
                Map.of(
                    "taskName", "피팅 테스트",
                    "progress", 98,
                    "baseStart", "2025-03-13",
                    "baseEnd", "2025-03-15",
                    "realStart", "2025-03-13",
                    "realEnd", "2025-03-17",
                    "delay", 2,
                    "status", "지연"
                ),
                Map.of(
                    "taskName", "피팅 테스트",
                    "progress", 98,
                    "baseStart", "2025-03-13",
                    "baseEnd", "2025-03-15",
                    "realStart", "2025-03-13",
                    "realEnd", "2025-03-17",
                    "delay", 2,
                    "status", "지연"
                ),
                Map.of(
                    "taskName", "피팅 테스트",
                    "progress", 98,
                    "baseStart", "2025-03-13",
                    "baseEnd", "2025-03-15",
                    "realStart", "2025-03-13",
                    "realEnd", "2025-03-17",
                    "delay", 2,
                    "status", "지연"
                ),
                Map.of(
                    "taskName", "피팅 테스트",
                    "progress", 98,
                    "baseStart", "2025-03-13",
                    "baseEnd", "2025-03-15",
                    "realStart", "2025-03-13",
                    "realEnd", "2025-03-17",
                    "delay", 2,
                    "status", "지연"
                ),
                Map.of(
                    "taskName", "피팅 테스트",
                    "progress", 98,
                    "baseStart", "2025-03-13",
                    "baseEnd", "2025-03-15",
                    "realStart", "2025-03-13",
                    "realEnd", "2025-03-17",
                    "delay", 2,
                    "status", "지연"
                ),
                Map.of(
                    "taskName", "피팅 테스트",
                    "progress", 98,
                    "baseStart", "2025-03-13",
                    "baseEnd", "2025-03-15",
                    "realStart", "2025-03-13",
                    "realEnd", "2025-03-17",
                    "delay", 2,
                    "status", "지연"
                ),
                Map.of(
                    "taskName", "피팅 테스트",
                    "progress", 98,
                    "baseStart", "2025-03-13",
                    "baseEnd", "2025-03-15",
                    "realStart", "2025-03-13",
                    "realEnd", "2025-03-17",
                    "delay", 2,
                    "status", "지연"
                ),
                Map.of(
                    "taskName", "피팅 테스트",
                    "progress", 98,
                    "baseStart", "2025-03-13",
                    "baseEnd", "2025-03-15",
                    "realStart", "2025-03-13",
                    "realEnd", "2025-03-17",
                    "delay", 2,
                    "status", "지연"
                ),
                Map.of(
                    "taskName", "피팅 테스트",
                    "progress", 98,
                    "baseStart", "2025-03-13",
                    "baseEnd", "2025-03-15",
                    "realStart", "2025-03-13",
                    "realEnd", "2025-03-17",
                    "delay", 2,
                    "status", "지연"
                ),
                Map.of(
                    "taskName", "피팅 테스트",
                    "progress", 98,
                    "baseStart", "2025-03-13",
                    "baseEnd", "2025-03-15",
                    "realStart", "2025-03-13",
                    "realEnd", "2025-03-17",
                    "delay", 2,
                    "status", "지연"
                ),
                Map.of(
                    "taskName", "피팅 테스트",
                    "progress", 98,
                    "baseStart", "2025-03-13",
                    "baseEnd", "2025-03-15",
                    "realStart", "2025-03-13",
                    "realEnd", "2025-03-17",
                    "delay", 2,
                    "status", "지연"
                )
            );
            context.setVariable("taskList", taskList);
            List<List<Map<String, Object>>> pagedTaskList = new ArrayList<>();
            for (int i = 0; i < taskList.size(); i += 15) {
                pagedTaskList.add(taskList.subList(i, Math.min(i + 15, taskList.size())));
            }
            context.setVariable("pagedTaskList", pagedTaskList);

            // 총계 데이터
            Map<String, Object> total = Map.of(
                "progress", 98,
                "baseStart", "2025-03-01",
                "baseEnd", "2025-03-21",
                "realStart", "2025-03-01",
                "realEnd", "2025-03-31",
                "delay", 10,
                "status", "지연"
            );
            context.setVariable("total", total);

            // 지연 분석 챕터
            String pieChartBase64 = buildPieChartBase64();
            context.setVariable("pieChartBase64", pieChartBase64);

            // 지연 사유서 내역
            List<Map<String, String>> delayReportList = List.of(
                Map.of(
                    "taskName", "패턴 생성",
                    "createdAt", "2025-03-08",
                    "approver", "홍길동 기획팀 팀장",
                    "fileUrl", "https://your-s3-bucket.s3.ap-northeast-2.amazonaws.com/delay_report_1.pdf"
                ),
                Map.of(
                    "taskName", "피팅 테스트",
                    "createdAt", "2025-03-08",
                    "approver", "홍길동 기획팀 팀장",
                    "fileUrl", "https://your-s3-bucket.s3.ap-northeast-2.amazonaws.com/delay_report_2.pdf"
                )
            );
            context.setVariable("delayReportList", delayReportList);

            // 페이지당 15개씩 분할
            List<List<Map<String, String>>> pagedDelayReportList = new ArrayList<>();
            for (int i = 0; i < delayReportList.size(); i += 15) {
                pagedDelayReportList.add(delayReportList.subList(i, Math.min(i + 15, delayReportList.size())));
            }

            context.setVariable("pagedDelayReportList", pagedDelayReportList);
            // byte[] previewIcon = Files.readAllBytes(new File("src/main/resources/static/preview.png").toPath());
            // context.setVariable("previewIconBase64", Base64.getEncoder().encodeToString(previewIcon));


            // 성과 지표 챕터
            List<Map<String, String>> outputList = List.of(
                Map.of("task", "자켓 봉제, 프린트", "filename", "제품기획서.pdf", "uploader", "기획팀 김하늘", "uploadDate", "2025-05-01", "fileUrl", "https://github.com/2TEAM-Ideality/be14-final-Ideality-CoreFlow-back"),
                Map.of("task", "자켓 봉제, 프린트", "filename", "원단스펙시트.xlsx", "uploader", "소재팀 이지수", "uploadDate", "2025-05-02", "fileUrl", "https://github.com/2TEAM-Ideality/be14-final-Ideality-CoreFlow-back"),
                Map.of("task", "자켓 봉제, 프린트", "filename", "부자재내역서.xlsx", "uploader", "소재팀 정인수", "uploadDate", "2025-05-02", "fileUrl", "https://github.com/2TEAM-Ideality/be14-final-Ideality-CoreFlow-back"),
                Map.of("task", "자켓 봉제, 프린트", "filename", "디자인도면.ai", "uploader", "디자인팀 박소영", "uploadDate", "2025-05-03", "fileUrl", "https://github.com/2TEAM-Ideality/be14-final-Ideality-CoreFlow-back"),
                Map.of("task", "자켓 봉제, 프린트", "filename", "작업지시서.docx", "uploader", "생산팀 최유진", "uploadDate", "2025-05-04", "fileUrl", "https://github.com/2TEAM-Ideality/be14-final-Ideality-CoreFlow-back"),
                Map.of("task", "자켓 봉제, 프린트", "filename", "샘플검수결과서.xlsx", "uploader", "영업팀 한지훈", "uploadDate", "2025-05-05", "fileUrl", "https://github.com/2TEAM-Ideality/be14-final-Ideality-CoreFlow-back"),
                Map.of("task", "자켓 봉제, 프린트", "filename", "패턴파일.dxf", "uploader", "품질팀 오세진", "uploadDate", "2025-05-06", "fileUrl", "https://github.com/2TEAM-Ideality/be14-final-Ideality-CoreFlow-back"),
                Map.of("task", "자켓 봉제, 프린트", "filename", "패턴테크 강민재", "uploader", "패턴팀 강민재", "uploadDate", "2025-05-07", "fileUrl", "https://github.com/2TEAM-Ideality/be14-final-Ideality-CoreFlow-back")
            );

            List<List<Map<String, String>>> pagedOutputList = new ArrayList<>();
            for (int i = 0; i < outputList.size(); i += 10) {
                pagedOutputList.add(outputList.subList(i, Math.min(i + 10, outputList.size())));
            }

            context.setVariable("pagedOutputList", pagedOutputList);




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


    // xchart 테스트용
    private String buildPieChartBase64() throws Exception {
        PieChart chart = new PieChartBuilder()
            .width(400)
            .height(300)
            .title("지연 현황 분석")
            .build();

        // ✅ 배경색 커스터마이징
        chart.getStyler().setChartBackgroundColor(Color.WHITE);
        chart.getStyler().setPlotBackgroundColor(Color.WHITE);
        chart.getStyler().setPlotBorderVisible(false);

        // ✅ 색상 설정
        Color[] sliceColors = new Color[] {
            new Color(252, 179, 112),    // 정상
            new Color(251, 234, 117),    // 지연
            new Color(157, 229, 179),    // 완료
            new Color(116, 222, 239),
            new Color(228, 134, 250)
        };
        chart.getStyler().setSeriesColors(sliceColors);
        // ✅ 사용자 글꼴 적용
        Font customFont = Font.createFont(Font.TRUETYPE_FONT, new File("src/main/resources/fonts/NotoSansKR-Regular.ttf"))
            .deriveFont(Font.PLAIN, 12f);
        chart.getStyler().setChartTitleFont(customFont);
        chart.getStyler().setLegendFont(customFont);
        chart.getStyler().setAnnotationTextFont(customFont);

        // ✅ 데이터 추가
        chart.addSeries("정상", 10);
        chart.addSeries("지연", 5);
        chart.addSeries("완료", 15);
        chart.addSeries("테스트", 12);
        chart.addSeries("색깔", 11);


        // ✅ 이미지로 변환
        BufferedImage image = BitmapEncoder.getBufferedImage(chart);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);

        return Base64.getEncoder().encodeToString(baos.toByteArray());
    }







}
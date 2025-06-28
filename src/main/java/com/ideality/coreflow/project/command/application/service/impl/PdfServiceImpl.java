package com.ideality.coreflow.project.command.application.service.impl;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.imageio.ImageIO;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.ui.TextAnchor;
import org.jfree.data.category.DefaultCategoryDataset;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.PieChart;
import org.knowm.xchart.PieChartBuilder;
import org.knowm.xchart.style.PieStyler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import com.ideality.coreflow.approval.query.dto.ProjectApprovalDTO;
import com.ideality.coreflow.attachment.query.dto.ReportAttachmentDTO;
import com.ideality.coreflow.common.exception.BaseException;
import com.ideality.coreflow.common.exception.ErrorCode;
import com.ideality.coreflow.project.command.application.service.PdfService;
import com.ideality.coreflow.project.query.dto.CompletedTaskDTO;
import com.ideality.coreflow.project.query.dto.ProjectDetailResponseDTO;
import com.ideality.coreflow.project.query.dto.ProjectOTD;
import com.ideality.coreflow.project.query.dto.UserInfoDTO;
import com.ideality.coreflow.project.query.dto.ProjectParticipantDTO;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PdfServiceImpl implements PdfService {

	private static final String FONT_PATH = "src/main/resources/fonts/NotoSansKR-Regular.ttf";

	private final TemplateEngine templateEngine;

	@Transactional
	@Override
	public void createReportPdf(
		HttpServletResponse response,
		ProjectDetailResponseDTO projectDetail,   // 프로젝트 기본 정보
		List<ProjectParticipantDTO> projectParticipantList,   // 프로젝트 참여자 목록
		List<CompletedTaskDTO> completedTaskList,    // 프로젝트 태스크
		List<ProjectApprovalDTO> delayList,			 // 지연 정보
		List<ProjectOTD> projectOTDList,
		List<ReportAttachmentDTO> attachmentList	 // 산출물 목록
	) {
		// 가져온 정보로 프로젝트 분석 리포트 PDF 만들기
		log.info("프로젝트 분석 리포트 만들기");

		// 데이터 확인
		if(delayList.size() == 0){
			log.info("지연 사유서 내역 없음");
		}
		if(attachmentList.size() == 0){
			log.info("산출물 내역 없음");
		}

		try{
			// 데이터 준비
			Context context = new Context();

			// 설명. 챕터 1 - 프로젝트 개요
			context.setVariable("projectName", projectDetail.getName());
			context.setVariable("director", projectDetail.getDirector().getName() + " " + projectDetail.getDirector().getDeptName() + " " + projectDetail.getDirector().getJobRoleName());
			context.setVariable("reportCreatedAt" , LocalDate.now());
			context.setVariable("projectPeriod", projectDetail.getStartReal() + " ~ " + projectDetail.getEndReal());
			context.setVariable("projectProgress", projectDetail.getProgressRate());
			context.setVariable("projectDescription", projectDetail.getDescription());
			context.setVariable("projectDelayDays" , projectDetail.getDelayDays());
			// 참여 리더
			Map<String, List<String>> participantMap = new HashMap<>();

			for (UserInfoDTO leader : projectDetail.getLeaders()) {
				String dept = leader.getDeptName();
				String info = leader.getName() + " " + leader.getJobRoleName();

				participantMap.computeIfAbsent(dept, k -> new ArrayList<>()).add(info);
			}
			for(ProjectParticipantDTO dto : projectParticipantList){
				String dept = dto.getDeptName();
				String info = dto.getUserName() + " " + dto.getJobRoleName();
				participantMap.computeIfAbsent(dept, k -> new ArrayList<>()).add(info);
			}
			context.setVariable("participantList", participantMap);


			// 커버 로고 이미지 파일
			byte[] coverLogoBytes = Files.readAllBytes(new File("src/main/resources/static/ReportLogo.png").toPath());
			String coverLogo = Base64.getEncoder().encodeToString(coverLogoBytes);
			context.setVariable("coverLogo", coverLogo);

			// 컨텐츠 로고 이미지 
			byte[] contentLogoBytes = Files.readAllBytes(new File("src/main/resources/static/ContentLogoFull.png").toPath());
			String contentLogo = Base64.getEncoder().encodeToString(contentLogoBytes);
			context.setVariable("contentLogo", contentLogo);

			// 설명. 챕터 2 - 작업 공정 목록
			context.setVariable("taskList", completedTaskList);
			List<List<CompletedTaskDTO>> pagedTaskList = new ArrayList<>();

			for (int i = 0; i < completedTaskList.size(); i += 15) {
				pagedTaskList.add(completedTaskList.subList(i, Math.min(i + 15, completedTaskList.size())));
			}
			context.setVariable("pagedTaskList", pagedTaskList); // 페이지별로 나눈 리스트

			// 총계 데이터
			String isDelay = projectDetail.getDelayDays() > 0 ?  "지연" : "기한 내 납기 준수";
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

			Map<String, Object> total = Map.of(
				"progress", Optional.ofNullable(projectDetail.getProgressRate()).orElse(0.0),
				"baseStart", Optional.ofNullable(projectDetail.getStartBase())
					.map(d -> d.format(formatter))
					.orElse("-"),
				"baseEnd", Optional.ofNullable(projectDetail.getEndBase())
					.map(d -> d.format(formatter))
					.orElse("-"),
				"realStart", Optional.ofNullable(projectDetail.getStartReal())
					.map(d -> d.format(formatter))
					.orElse("미입력"),
				"realEnd", Optional.ofNullable(projectDetail.getEndReal())
					.map(d -> d.format(formatter))
					.orElse("미입력"),
				"delay", Optional.ofNullable(projectDetail.getDelayDays()).orElse(0),
				"status", Optional.ofNullable(isDelay).orElse("N/A")
			);
			context.setVariable("total", total);

			// 설명. 챕터 3 - 지연 분석 챕터 ---------------------------------------------------------------
			// 지연 태스크 분석
			String delayTaskChart = delayTaskChart(completedTaskList);
			context.setVariable("delayTaskChart", delayTaskChart);

			// 지연 사유 분석
			String delayReasonChart = delayReasonChart(delayList);
			context.setVariable("delayReasonChart", delayReasonChart);

			// 지연 사유서 내역
			List<List<ProjectApprovalDTO>> pagedDelayReportList = new ArrayList<>();
			for (int i = 0; i < delayList.size(); i += 15) {
				pagedDelayReportList.add(delayList.subList(i, Math.min(i + 15, delayList.size())));
			}
			context.setVariable("pagedDelayReportList", pagedDelayReportList);


			// 설명. 챕터 4 - 성과 지표 ---------------------------------------------------------------
			//
			// 산출물 내역
			List<List<ReportAttachmentDTO>> pagedOutputList = new ArrayList<>();
			for (int i = 0; i < attachmentList.size(); i += 15) {
				pagedOutputList.add(attachmentList.subList(i, Math.min(i + 15, attachmentList.size())));
			}
			context.setVariable("pagedOutputList", pagedOutputList);

			List<CompletedTaskDTO> delayedTaskList = new ArrayList<>();

			// 총 태스크 수
			context.setVariable("totalTask", completedTaskList.size());
			// 납기 대상 작업
			// 기한 내 완료 작업 수
			int completedOnTime = 0;
			int notCompletedOnTime = 0;
			for(CompletedTaskDTO dto : completedTaskList){
				if(dto.getDelayDays() == 0){
					completedOnTime ++;
				}else{
					notCompletedOnTime ++ ;
					delayedTaskList.add(dto);
				}
			}
			context.setVariable("completedOnTime", completedOnTime);		// 기한 내 완료 작업
			context.setVariable("notCompletedOnTime", notCompletedOnTime);	// 기한 내 미완료 작업
			int totalCompleted = completedTaskList.size();
			double OTD = totalCompleted > 0 ? (completedOnTime * 100.0) / totalCompleted : 0.0;
			context.setVariable("OTD", Math.round(OTD * 100.0) / 100.0 + "%");  	// 납기 준수율

			double meanDelay = completedTaskList.size() > 0
				? (double) projectDetail.getDelayDays() / completedTaskList.size()
				: 0.0;
			context.setVariable("meanDelay", "+ " + Math.round(meanDelay * 100.0) / 100.0 + " 일");	// 평균 지연일
			context.setVariable("totalDelay", projectDetail.getDelayDays() + " 일");   // 총 지연일
			context.setVariable("delayedTaskList", delayedTaskList); 			 // 지연 태스크 목록


			// 전체 프로젝트에서 납기준수율 추출
			String newChartBase64 = createOTDChart(projectOTDList, projectDetail.getId());
			context.setVariable("compareOtdChart", newChartBase64);

			// -----------------------------------------------------------------------

			// 설명. 각 페이지 템플릿 렌더링
			String reportHtml = templateEngine.process("report", context);
			reportHtml = reportHtml.replace("&nbsp;", "&#160;"); // 안전 처리

			String timeSuffix = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
			// String rawFileName = "프로젝트분석보고서_" + projectDetail.getName() + "_" + timeSuffix + ".pdf";
			String rawFileName = projectDetail.getName() +"_분석 리포트"+ ".pdf";
			log.info("PDF FILE NAME : {}" , rawFileName);
			String encodedFileName = URLEncoder.encode(rawFileName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");

			response.setHeader("Content-Disposition",
				"attachment; filename=\"" + encodedFileName + "\"; filename*=UTF-8''" + encodedFileName);
			response.setContentType("application/pdf");


			try(OutputStream os = response.getOutputStream()){
				PdfRendererBuilder builder = new PdfRendererBuilder();
				builder.useFastMode();
				builder.useFont(new File(FONT_PATH), "Noto Sans KR");
				builder.withHtmlContent(reportHtml, null);
				// 설명. 그 결과를 HTTP 응답 스트림(OutputStream)으로 직접 보내줌
				builder.toStream(os);

				builder.run();
			}

		} catch (Exception e) {
		if (!response.isCommitted()) {
			try {
				response.reset();
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				response.getWriter().write("PDF 생성 중 오류가 발생했습니다. 관리자에게 문의하세요.");
			} catch (IOException ioEx) {
				log.error("응답 오류 메시지 전송 실패", ioEx);
			}
		} else {
			log.error("PDF 생성 중 예외 발생 (응답 커밋됨): {}", e.getMessage());
		}
		log.error("PDF 생성 실패", e);
		throw new BaseException(ErrorCode.PDF_CREATE_FAILED);
		}

	}

	public static final Color[] SLICE_COLORS = new Color[] {
		new Color(252, 179, 112),
		new Color(251, 234, 117),
		new Color(157, 229, 179),
		new Color(116, 222, 239),
		new Color(228, 134, 250)
	};

	public static void applyDefaultChartStyle(PieChart chart) throws IOException, FontFormatException {
		chart.getStyler().setChartBackgroundColor(Color.WHITE);
		chart.getStyler().setPlotBackgroundColor(Color.WHITE);
		chart.getStyler().setPlotBorderVisible(false);
		chart.getStyler().setSeriesColors(SLICE_COLORS);

		// ✅ 크기 통일 관련 설정
		chart.getStyler().setLegendPosition(PieStyler.LegendPosition.OutsideE);  // 범례 오른쪽 상단
		chart.getStyler().setLegendPadding(4);
		chart.getStyler().setLegendSeriesLineLength(15);
		chart.getStyler().setPlotContentSize(0.85); // 그래프 원 고정 크기
		chart.getStyler().setCircular(true);

		// ✅ 라벨 간섭 줄이기
		chart.getStyler().setLabelsDistance(0.4);

		Font customFont = Font.createFont(Font.TRUETYPE_FONT, new File(FONT_PATH)).deriveFont(Font.PLAIN, 11f);
		chart.getStyler().setChartTitleFont(customFont);
		chart.getStyler().setLegendFont(customFont);
		chart.getStyler().setAnnotationTextFont(customFont);
	}



	// 지연 사유 분석 차트 생성 메서드
	private String delayReasonChart(List<ProjectApprovalDTO> delayList) throws IOException, FontFormatException {
		// 차트 기본 생성
		PieChart chart = new PieChartBuilder()
			.width(400)
			.height(300)
			.title("지연 사유 분석")
			.build();
		
		if(delayList == null || delayList.isEmpty()) {
			chart.addSeries("지연 사유 없음", 1);
			chart.getStyler().setSeriesColors(new Color[]{new Color(230, 230, 230)});  // 밝은 회색
			chart.getStyler().setChartBackgroundColor(Color.WHITE);
			chart.getStyler().setPlotBackgroundColor(Color.WHITE);
		}else{
			Map<String, Integer> delayReasonList = new HashMap<>();
			for (ProjectApprovalDTO dto : delayList) {
				if (!delayReasonList.containsKey(dto.getDelayReason())) {
					delayReasonList.put(dto.getDelayReason(), 1);
				} else {
					Integer value = delayReasonList.get(dto.getDelayReason());
					delayReasonList.put(dto.getDelayReason(), value + 1);
				}
			}
			// 라벨에 "사유명 - n건" 형식으로 추가
			for (Map.Entry<String, Integer> entry : delayReasonList.entrySet()) {
				String label = entry.getKey() + " - " + entry.getValue() + "건";
				chart.addSeries(label, entry.getValue());
			}

			applyDefaultChartStyle(chart);
		}

		// 이미지로 변환
		return getString(chart);
	}


	// 지연 태스크 분석 차트 생성
	private String delayTaskChart(List<CompletedTaskDTO> completedTaskList) throws IOException, FontFormatException {
		// 차트 기본 생성
		PieChart chart = new PieChartBuilder()
			.width(400)
			.height(300)
			.title("지연 태스크 분석")
			.build();

		// 완료된 태스크 목록에서 지연 발생한 태스크 추출
		List<CompletedTaskDTO> delayedTaskList = new ArrayList<>();
		for(CompletedTaskDTO dto : completedTaskList){
			if(dto.getDelayDays() > 0){
				delayedTaskList.add(dto);
			}
		}

		// 지연 태스크가 없을 경우
		if(delayedTaskList == null || delayedTaskList.isEmpty()) {
			chart.addSeries("지연 태스크 없음", 1);
			chart.getStyler().setSeriesColors(new Color[]{new Color(230, 230, 230)});  // 밝은 회색
			chart.getStyler().setChartBackgroundColor(Color.WHITE);
			chart.getStyler().setPlotBackgroundColor(Color.WHITE);
		}else{
			// delayDays(지연일) 기준으로 내림차순 정렬
			delayedTaskList.sort((a, b) -> Integer.compare(b.getDelayDays(), a.getDelayDays()));

			Map<String, Integer> nameCountMap = new HashMap<>();
			for (CompletedTaskDTO dto : delayedTaskList) {
				String taskName = dto.getTaskName();

				// 동일 이름 카운팅 → 중복 방지용 인덱스 추가
				if (nameCountMap.containsKey(taskName)) {
					int count = nameCountMap.get(taskName) + 1;
					nameCountMap.put(taskName, count);
					taskName += " (" + count + ")";
				} else {
					nameCountMap.put(taskName, 1);
				}

				chart.addSeries(taskName + " - " + dto.getDelayDays() + "일", dto.getDelayDays());
			}
			applyDefaultChartStyle(chart);
		}
		// 이미지로 변환
		return getString(chart);

	}




	public String createOTDChart(List<ProjectOTD> otdList, Long currentProjectId) throws IOException, FontFormatException {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		// 1. 데이터 추가
		for (ProjectOTD dto : otdList) {
			dataset.addValue(dto.getOtdRate(), "OTD", dto.getProjectName());
		}

		// 2. 차트 생성
		JFreeChart chart = ChartFactory.createBarChart(
			"프로젝트 납기 준수율 비교",
			"OTD(%)",
			"프로젝트",
			dataset,
			PlotOrientation.HORIZONTAL,
			false, true, false
		);

		// 3. 커스텀 렌더러 설정 (각 막대마다 색 다르게)
		CategoryPlot plot = chart.getCategoryPlot();
		BarRenderer renderer = new BarRenderer() {
			@Override
			public Paint getItemPaint(int row, int column) {
				String projectName = (String) dataset.getColumnKey(column);
				for (ProjectOTD dto : otdList) {
					if (dto.getProjectName().equals(projectName)) {
						if (dto.getProjectId().equals(currentProjectId)) {
							return new Color(26, 188, 156); // 현재 프로젝트만 청록색
						}
					}
				}
				return new Color(211, 211, 211); // 나머지는 회색
			}
		};
		plot.setRenderer(renderer);

		// 4. 레이블 설정 (% 표시)
		Font font = Font.createFont(Font.TRUETYPE_FONT, new File("src/main/resources/fonts/NotoSansKR-Regular.ttf"))
			.deriveFont(Font.PLAIN, 12f);
		renderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator("{2}%", NumberFormat.getInstance()));
		renderer.setDefaultItemLabelsVisible(true);
		renderer.setDefaultItemLabelFont(font);
		renderer.setDefaultPositiveItemLabelPosition(
			new ItemLabelPosition(ItemLabelAnchor.CENTER, TextAnchor.CENTER_RIGHT));

		// 5. 폰트 적용
		chart.getTitle().setFont(font);
		plot.getDomainAxis().setTickLabelFont(font);
		plot.getRangeAxis().setTickLabelFont(font);

		// 6. 이미지로 변환
		BufferedImage image = chart.createBufferedImage(800, 500);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(image, "png", baos);
		return Base64.getEncoder().encodeToString(baos.toByteArray());
	}








	private static String getString(PieChart chart) throws IOException {
		BufferedImage image = BitmapEncoder.getBufferedImage(chart);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(image, "png", baos);

		return Base64.getEncoder().encodeToString(baos.toByteArray());
	}

}

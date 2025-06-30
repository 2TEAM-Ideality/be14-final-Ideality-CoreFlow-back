package com.ideality.coreflow.project.command.application.service.impl;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.CategoryTextAnnotation;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StackedBarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.ui.RectangleAnchor;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.chart.ui.TextAnchor;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.PieChart;
import org.knowm.xchart.PieChartBuilder;
import org.knowm.xchart.style.PieStyler;
import org.knowm.xchart.style.Styler;
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
import org.springframework.core.io.ClassPathResource;

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
			String period = projectDetail.getStartReal().format(DateTimeFormatter.ISO_DATE)
				+ " ~ "
				+ projectDetail.getEndReal().format(DateTimeFormatter.ISO_DATE);

			// 실제 진행 소요일
			long totalDays = ChronoUnit.DAYS.between(
				projectDetail.getStartReal(),
				projectDetail.getEndReal()
			) + 1;
			String periodData = period + " (총 " + totalDays + "일)";

			context.setVariable("projectPeriodData", periodData);

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


			// 커버 로고 이미지
			InputStream coverLogoStream = new ClassPathResource("static/ReportLogo.png").getInputStream();
			byte[] coverLogoBytes = coverLogoStream.readAllBytes();
			String coverLogo = Base64.getEncoder().encodeToString(coverLogoBytes);
			context.setVariable("coverLogo", coverLogo);

// 컨텐츠 로고 이미지
			InputStream contentLogoStream = new ClassPathResource("static/ContentLogoFull.png").getInputStream();
			byte[] contentLogoBytes = contentLogoStream.readAllBytes();
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

			LocalDate startReal = projectDetail.getStartReal();
			LocalDate endReal = projectDetail.getEndReal();

			long realDuration = 0;
			if (startReal != null && endReal != null) {
				realDuration = ChronoUnit.DAYS.between(startReal, endReal) + 1;
			}

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
				"status", Optional.ofNullable(isDelay).orElse("N/A"),
				"realDuration", realDuration
				);
			context.setVariable("total", total);

			// 설명. 챕터 3 - 지연 분석 챕터 ---------------------------------------------------------------
			// 지연 태스크 분석
			String delayTaskChart = delayTaskChart(completedTaskList);
			context.setVariable("delayTaskChart", delayTaskChart);

			List<CompletedTaskDTO> sortedDelayedTasks = completedTaskList.stream()
				.filter(task -> task.getDelayDays() != null && task.getDelayDays() > 0)
				.sorted(Comparator.comparingInt(CompletedTaskDTO::getDelayDays).reversed())
				.collect(Collectors.toList());

			context.setVariable("legendItems", sortedDelayedTasks);

			// 지연 사유 분석
			String delayReasonChart = delayReasonChart(delayList);
			context.setVariable("delayReasonChart", delayReasonChart);

			String delayReasonSummary = "";
			if (delayList != null && !delayList.isEmpty()) {
				Map<String, Long> reasonCountMap = delayList.stream()
					.collect(Collectors.groupingBy(ProjectApprovalDTO::getDelayReason, Collectors.counting()));

				long reasnoTotal = delayList.size();
				Map.Entry<String, Long> mostCommon = reasonCountMap.entrySet().stream()
					.max(Map.Entry.comparingByValue())
					.orElse(null);

				if (mostCommon != null) {
					double percent = (double) mostCommon.getValue() * 100 / reasnoTotal;
					delayReasonSummary = String.format("가장 많은 지연 사유는 '%s'로 전체의 %.0f%% 차지", mostCommon.getKey(), percent);
				}
			}
			context.setVariable("delayReasonSummary", delayReasonSummary);


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
			context.setVariable("notCompletedOnTime", notCompletedOnTime );	// 기한 내 미완료 작업
			int totalCompleted = completedTaskList.size();
			double OTD = totalCompleted > 0 ? (completedOnTime * 100.0) / totalCompleted : 0.0;
			context.setVariable("OTD", Math.round(OTD * 100.0) / 100.0);  	// 납기 준수율

			double meanDelay = completedTaskList.size() > 0
				? (double) projectDetail.getDelayDays() / completedTaskList.size()
				: 0.0;
			context.setVariable("meanDelay", Math.round(meanDelay * 100.0) / 100.0);	// 평균 지연일
			context.setVariable("totalDelay", projectDetail.getDelayDays());   // 총 지연일
			context.setVariable("delayedTaskList", delayedTaskList); 			 // 지연 태스크 목록

			// 주요 병목 공정 차트
			context.setVariable("widthPercent", 30);
			// String bottleneckChartBase64 = bottleneckChart(delayedTaskList);
			// context.setVariable("bottleneckChart", bottleneckChartBase64);

			Random rnd = new Random();
			// 지연 태스크 비율
			// 1) 총 지연일 합계
			long totalDelayDays = delayedTaskList.stream()
				.mapToLong(CompletedTaskDTO::getDelayDays)
				.sum();

			// 2) (태스크명, 지연일, percent) 맵 리스트 생성
			List<Map<String, Object>> delayPercentList = new ArrayList<>();
			for (CompletedTaskDTO dto : delayedTaskList) {
				double percent = totalDelayDays > 0
					? Math.round(dto.getDelayDays() * 10000.0 / totalDelayDays) / 100.0  // 소수 둘째자리까지 반올림
					: 0.0;
				Map<String,Object> m = new HashMap<>();
				m.put("taskName", dto.getTaskName());
				m.put("delayDays", dto.getDelayDays());
				m.put("percent", percent);
				delayPercentList.add(m);
			}

			context.setVariable("delayPercentList", delayPercentList);
			log.info("▶ delayPercentList = {}", delayPercentList);

			// 2. 전체 지연일 합계
			long totalDelayAll = delayList.stream()
				.mapToLong(ProjectApprovalDTO::getDelayDays)
				.sum();

			// 3. 부서별 그룹핑
			Map<String, List<ProjectApprovalDTO>> delaysByDept = delayList.stream()
				.collect(Collectors.groupingBy(ProjectApprovalDTO::getRequesterDeptName));

			// 4. 부서별 통계 리스트 생성
			List<Map<String, Object>> deptDelayStats = new ArrayList<>();
			for (Map.Entry<String, List<ProjectApprovalDTO>> entry : delaysByDept.entrySet()) {
				String dept = entry.getKey();
				List<ProjectApprovalDTO> group = entry.getValue();

				// 4-1) 담당 태스크, 세부일정 목록(중복 제거)
				// List<String> tasks = group.stream()
				// 	.map(ProjectApprovalDTO::getTaskName)
				// 	.distinct()
				// 	.collect(Collectors.toList());
				// List<String> details = group.stream()
				// 	.map(ProjectApprovalDTO::getSubTaskName)   // 예: getDetailName()
				// 	.distinct()
				// 	.collect(Collectors.toList());

				// 4-2) 총 지연일, 평균 지연일 계산
				long deptTotalDelay = group.stream()
					.mapToLong(ProjectApprovalDTO::getDelayDays)
					.sum();
				double deptAvgDelay = group.isEmpty() ? 0
					: Math.round((double) deptTotalDelay / group.size() * 100.0) / 100.0;

				// 4-3) 전체 대비 비율
				double pct = totalDelayAll == 0 ? 0
					: Math.round(deptTotalDelay * 10000.0 / totalDelayAll) / 100.0;

				// 일단 details는 빈 리스트라도 무조건 포함시키기
				// List<String> details = new ArrayList<>();

				Map<String,Object> stat = new HashMap<>();
				stat.put("deptName", dept);
				// stat.put("tasks", tasks);
				// stat.put("details", details);  // ✅ 필수: Thymeleaf에서 오류 방지
				stat.put("totalDelay", deptTotalDelay);
				stat.put("avgDelay", deptAvgDelay);
				stat.put("percentOfTotal", pct);

				deptDelayStats.add(stat);
			}

			// 5. Thymeleaf 에 변수로 넘기기
			context.setVariable("deptDelayStats", deptDelayStats);

			// 설명. 부서별 지연 분석 차트
			String deptDelayChart = createDeptDelayChart(deptDelayStats);
			context.setVariable("deptDelayChart", deptDelayChart);

			// 설명. delayPercentList / deptDelayStats 기반 분석 텍스트 생성
			String delayAnalysisText = generateDelayAnalysisText(delayPercentList, deptDelayStats);

			// Thymeleaf Context에 전달
			context.setVariable("delayAnalysisText", delayAnalysisText);

			String delayDeptSummary = "";
			if (!deptDelayStats.isEmpty()) {
				// 부서별 평균 지연일의 전체 평균
				double avgOfAvgDelays = deptDelayStats.stream()
					.mapToDouble(d -> (double) d.get("avgDelay"))
					.average()
					.orElse(0.0);

				// 최대/최소 지연 비율 부서 찾기
				Map<String, Object> maxDept = deptDelayStats.stream()
					.max(Comparator.comparingDouble(d -> (double) d.get("percentOfTotal")))
					.orElse(null);

				Map<String, Object> minDept = deptDelayStats.stream()
					.min(Comparator.comparingDouble(d -> (double) d.get("percentOfTotal")))
					.orElse(null);

				if (maxDept != null && minDept != null) {
					delayDeptSummary = String.format(
						"부서별 평균 지연일은 %.1f일이며, 지연 비율이 가장 높은 부서는 '%s'(%.1f%%), 가장 낮은 부서는 '%s'(%.1f%%)입니다.",
						avgOfAvgDelays,
						maxDept.get("deptName"), maxDept.get("percentOfTotal"),
						minDept.get("deptName"), minDept.get("percentOfTotal")
					);
				}
			}
			context.setVariable("delayDeptSummary", delayDeptSummary);



			// 설명. 납기 준수율 OTD
			//  전체 프로젝트에서 납기준수율 추출
			context.setVariable("projectCount", projectOTDList.size());
			log.info("OTD 프로젝트 리스트: {}", projectOTDList.stream()
				.map(dto -> dto.getProjectName() + "=" + dto.getOtdRate())
				.collect(Collectors.toList()));
			String compareOtdChart = createOTDChart(projectOTDList, projectDetail.getId());
			context.setVariable("compareOtdChart", compareOtdChart);

			// 평균과 비교한 설명 문구 추가
			double avgOtd = projectOTDList.stream()
				.mapToDouble(ProjectOTD::getOtdRate)
				.average()
				.orElse(0.0);

			ProjectOTD currentProject = projectOTDList.stream()
				.filter(dto -> dto.getProjectId().equals(projectDetail.getId()))
				.findFirst()
				.orElse(null);

			// 설명. 평가 문구
			// OTD(납기 준수율) 평가 기준:
			// - 현재 프로젝트 OTD가 평균보다 5.0%p 이상 높으면 → "우수한 성과"
			// - 현재 프로젝트 OTD가 평균보다 5.0%p 이상 낮으면 → "개선이 필요함"
			// - 그 외, 평균과 ±5.0%p 이내이면 → "평균과 유사한 수준"

			String comment = "";
			String  evalType = "";
			if (currentProject != null) {
				double currentOtd = currentProject.getOtdRate();
				double diff = currentOtd - avgOtd;
				if (diff > 5.0) {
					comment = String.format(
						"해당 프로젝트의 납기 준수율은 평균(%.1f%%)보다 %.1f%%p 높아, 우수한 성과를 보였습니다.",
						avgOtd, diff
					);
					evalType = "EXCELLENT";
				} else if (diff < -5.0) {
					comment = String.format(
						"해당 프로젝트의 납기 준수율은 평균(%.1f%%)보다 %.1f%%p 낮아, 개선이 필요합니다.",
						avgOtd, -diff
					);
					evalType = "NEEDS_IMPROVEMENT";
				} else {
					comment = String.format(
						"해당 프로젝트의 납기 준수율은 평균(%.1f%%)과 유사한 수준(%.1f%%)입니다.",
						avgOtd, currentOtd
					);
					evalType = "AVERAGE";
				}
			}
			context.setVariable("otdComparisonComment", comment);
			context.setVariable("evalType", evalType.trim());

			// 설명. 최종 평가 요약
			String overallSummary = "";

			try {
				String topTaskSummary = delayPercentList.stream()
					.sorted((a, b) -> Double.compare((double) b.get("percent"), (double) a.get("percent")))
					.limit(1)
					.map(m -> String.format("‘%s’(%.1f%%)", m.get("taskName"), m.get("percent")))
					.findFirst()
					.orElse("N/A");

				String topReason = delayReasonSummary.isEmpty() ? "주요 지연 사유 정보 없음" : delayReasonSummary;

				String deptSummary = delayDeptSummary.isEmpty() ? "부서별 지연 정보 없음" : delayDeptSummary;

				String otdEval = comment.isEmpty() ? "납기 준수율 평가 정보 없음" : comment;

				overallSummary = String.join("<br/>", List.of(
					String.format("전체 납기 준수율은 %.2f%%이며,", (double) context.getVariable("OTD")),
					String.format("평균 지연일은 %.2f일로 분석되었습니다.", (double) context.getVariable("meanDelay")),
					String.format("주요 병목 공정은 %s 태스크입니다.", topTaskSummary),
					topReason,
					deptSummary,
					otdEval
				));
			} catch (Exception e) {
				log.warn("전체 평가 요약 생성 중 오류 발생", e);
				overallSummary = "전체 프로젝트 평가 요약 정보를 생성하는 중 오류가 발생했습니다.";
			}

			context.setVariable("overallSummary", overallSummary);

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

	// 부서별 지연 분석 요약
	public static String generateDelayAnalysisText(
		List<Map<String, Object>> delayPercentList,
		List<Map<String, Object>> deptDelayStats
	) {
		int totalDelayTasks = delayPercentList.size();
		long totalDelayDays = delayPercentList.stream()
			.mapToLong(m -> ((Number) m.get("delayDays")).longValue())
			.sum();

		// 주요 병목 태스크 추출
		delayPercentList.sort((a, b) -> Double.compare(
			((Number) b.get("percent")).doubleValue(),
			((Number) a.get("percent")).doubleValue()
		));
		StringBuilder topTasksText = new StringBuilder();
		for (int i = 0; i < Math.min(3, delayPercentList.size()); i++) {
			Map<String, Object> task = delayPercentList.get(i);
			topTasksText.append("‘")
				.append(task.get("taskName"))
				.append("’ (")
				.append(task.get("percent"))
				.append("%)");
			if (i < Math.min(3, delayPercentList.size()) - 1) {
				topTasksText.append(", ");
			}
		}

		// 가장 많이 지연된 부서 및 평균 지연일이 높은 부서
		Map<String, Object> mostDelayedDept = deptDelayStats.stream()
			.max(Comparator.comparingLong(d -> ((Number) d.get("totalDelay")).longValue()))
			.orElse(null);

		Map<String, Object> highestAvgDept = deptDelayStats.stream()
			.max(Comparator.comparingDouble(d -> ((Number) d.get("avgDelay")).doubleValue()))
			.orElse(null);

		double avgDelayAll = deptDelayStats.stream()
			.mapToDouble(d -> ((Number) d.get("avgDelay")).doubleValue())
			.average().orElse(0.0);

		// 결과 줄글 생성
		StringBuilder result = new StringBuilder();
		result.append("프로젝트 전체 완료된 태스크 중 총 ")
			.append(totalDelayTasks).append("건에서 지연이 발생하였으며, 총 ")
			.append(totalDelayDays).append("일의 지연이 집계되었습니다.<br/>");
		result.append("그 중 ").append(topTasksText).append(" 태스크들이 주요 병목 요인으로 나타났습니다.<br/><br/>");

		if (mostDelayedDept != null) {
			result.append("부서별로는 '").append(mostDelayedDept.get("deptName"))
				.append("' 부서가 총 ").append(mostDelayedDept.get("totalDelay"))
				.append("일의 지연으로 전체 지연의 ").append(mostDelayedDept.get("percentOfTotal"))
				.append("%를 차지하였습니다.<br/>");
		}

		if (highestAvgDept != null) {
			result.append("또한, '").append(highestAvgDept.get("deptName"))
				.append("' 부서는 평균 지연일이 ").append(highestAvgDept.get("avgDelay"))
				.append("일로 가장 높게 나타났습니다.<br/><br/>");
		}

		result.append("전체 부서의 평균 지연일은 약 ")
			.append(Math.round(avgDelayAll * 100.0) / 100.0)
			.append("일로, 이 기준 이상 지연이 발생한 부서에 대해 일정 점검 및 개선 조치가 필요합니다.");


		return result.toString();
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
		chart.getStyler().setLegendSeriesLineLength(40);
		chart.getStyler().setPlotContentSize(0.85); // 그래프 원 고정 크기
		chart.getStyler().setCircular(true);

		// ✅ 라벨 간섭 줄이기
		chart.getStyler().setLabelsDistance(0.4);

		Font customFont = Font.createFont(Font.TRUETYPE_FONT, new File(FONT_PATH)).deriveFont(Font.PLAIN, 11f);
		chart.getStyler().setChartTitleFont(customFont);
		chart.getStyler().setLegendFont(customFont);
		chart.getStyler().setAnnotationTextFont(customFont);
	}

	// 주요 병목 공정 막대 그래프
	/**
	 * 지연된 태스크 리스트를 받아, 전체 지연 시간 중
	 * 각 태스크가 차지하는 비율(%)을 스택형 막대로 그려 Base64 문자열로 반환
	 */
	private String bottleneckChart(List<CompletedTaskDTO> delayedTaskList) throws IOException, FontFormatException {
		// 1) 총 지연 시간 합계
		long totalDelay = delayedTaskList.stream()
			.mapToLong(CompletedTaskDTO::getDelayDays)
			.sum();

		// 2) 데이터셋 준비 (row = 태스크명, column = "병목")
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		for (CompletedTaskDTO dto : delayedTaskList) {
			double pct = totalDelay > 0
				? dto.getDelayDays() * 100.0 / totalDelay
				: 0.0;
			dataset.addValue(pct, dto.getTaskName(), "병목");
		}

		// 3) 차트 생성
		JFreeChart chart = ChartFactory.createStackedBarChart(
			"주요 병목 공정",     // chart title
			"",                 // domain axis label
			"비율(%)",          // range axis label
			dataset,
			PlotOrientation.HORIZONTAL,
			true,  // legend
			false,
			false
		);

		// 4) 스타일링
		CategoryPlot plot = (CategoryPlot) chart.getPlot();
		plot.setBackgroundPaint(Color.WHITE);
		plot.setRangeGridlinePaint(Color.LIGHT_GRAY);

		// X축(비율) 범위 0~100 고정
		NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setRange(0, 100);
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

		// 스택 렌더러: 각 시리즈(태스크)마다 색 지정
		StackedBarRenderer renderer = new StackedBarRenderer();
		Color[] colors = new Color[] {
			new Color(252,179,112),
			new Color(251,234,117),
			new Color(157,229,179),
			new Color(116,222,239),
			new Color(228,134,250)
		};
		for (int i = 0; i < delayedTaskList.size(); i++) {
			renderer.setSeriesPaint(i, colors[i % colors.length]);
		}
		plot.setRenderer(renderer);

		// 5) 이미지로 변환
		BufferedImage img = chart.createBufferedImage(600, 300);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(img, "png", baos);
		return Base64.getEncoder().encodeToString(baos.toByteArray());
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
	//


	// 지연 태스크 분석 차트 생성
	private String delayTaskChart(List<CompletedTaskDTO> completedTaskList) throws IOException, FontFormatException {
		// 차트 기본 생성
		PieChart chart = new PieChartBuilder()
			.width(400)
			.height(300)
			.title("")
			.build();

		// 스타일 커스터마이징: 범례 크기 조정
		chart.getStyler().setLegendVisible(true);
		chart.getStyler().setLegendPadding(30);
		chart.getStyler().setLegendSeriesLineLength(80); // 범례 색 박스 길이 조절
		chart.getStyler().setLegendLayout(Styler.LegendLayout.Vertical);
		chart.getStyler().setLegendPosition(Styler.LegendPosition.OutsideE); // 오른쪽 외부로
		chart.getStyler().setAnnotationTextFont(new Font("Noto Sans KR", Font.BOLD, 28)); // ✅ 올바른 메서드
		chart.getStyler().setLabelsDistance(0.65); // 텍스트 위치
		chart.getStyler().setLabelsFont(new Font("Noto Sans KR", Font.BOLD, 12));
		chart.getStyler().setAnnotationTextFont(new Font("Noto Sans KR", Font.BOLD, 20));
		chart.getStyler().setAnnotationTextPanelPadding((int)3);
		chart.getStyler().setLegendFont(new Font("Noto Sans KR", Font.PLAIN, 18));

		// 지연 태스크 추출
		List<CompletedTaskDTO> delayedTaskList = completedTaskList.stream()
			.filter(dto -> dto.getDelayDays() > 0)
			.sorted((a, b) -> Integer.compare(b.getDelayDays(), a.getDelayDays()))
			.collect(Collectors.toList());

		if (delayedTaskList.isEmpty()) {
			chart.addSeries("지연 태스크 없음", 1);
			chart.getStyler().setSeriesColors(new Color[]{new Color(230, 230, 230)});
			chart.getStyler().setChartBackgroundColor(Color.WHITE);
			chart.getStyler().setPlotBackgroundColor(Color.WHITE);
		} else {
			Map<String, Integer> nameCountMap = new HashMap<>();
			for (CompletedTaskDTO dto : delayedTaskList) {
				String taskName = dto.getTaskName();
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

		return getString(chart);
	}

	// private String delayTaskChart(List<CompletedTaskDTO> completedTaskList) throws IOException {
	// 	// 1. 데이터셋 구성
	// 	DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
	//
	// 	Map<String, Integer> nameCountMap = new HashMap<>();
	// 	List<String> labels = new ArrayList<>(); // 색상 매핑용
	// 	int total = 0;
	//
	// 	for (CompletedTaskDTO dto : completedTaskList) {
	// 		if (dto.getDelayDays() <= 0) continue;
	//
	// 		String taskName = dto.getTaskName();
	// 		if (nameCountMap.containsKey(taskName)) {
	// 			int count = nameCountMap.get(taskName) + 1;
	// 			nameCountMap.put(taskName, count);
	// 			taskName += " (" + count + ")";
	// 		} else {
	// 			nameCountMap.put(taskName, 1);
	// 		}
	//
	// 		String label = taskName + " - " + dto.getDelayDays() + "일";
	// 		dataset.setValue(label, dto.getDelayDays());
	// 		labels.add(label);
	// 		total += dto.getDelayDays();
	// 	}
	//
	// 	if (dataset.getItemCount() == 0) {
	// 		dataset.setValue("지연 태스크 없음", 1);
	// 		labels.add("지연 태스크 없음");
	// 	}
	//
	// 	// 2. 차트 생성
	// 	JFreeChart chart = ChartFactory.createPieChart(
	// 		"",
	// 		dataset,
	// 		true,
	// 		false,
	// 		false
	// 	);
	//
	// 	// 3. 스타일 커스터마이징
	// 	PiePlot plot = (PiePlot) chart.getPlot();
	// 	plot.setBackgroundPaint(Color.WHITE);
	// 	plot.setOutlineVisible(false);
	// 	plot.setInteriorGap(0.04);
	//
	// 	// 파이 내부 라벨 + 퍼센트
	// 	plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0} ({2})"));
	// 	plot.setLabelFont(new Font("Noto Sans KR", Font.BOLD, 16));
	// 	plot.setLabelBackgroundPaint(new Color(255, 255, 255, 0)); // 투명
	//
	// 	// 범례 폰트
	// 	chart.getLegend().setItemFont(new Font("Noto Sans KR", Font.PLAIN, 20));
	// 	chart.getLegend().setFrame(BlockBorder.NONE);
	// 	chart.getLegend().setPosition(RectangleEdge.RIGHT);
	//
	//
	//
	// 	// 🎨 사용자 정의 색상 적용
	// 	Color[] customColors = new Color[] {
	// 		new Color(252, 179, 112),
	// 		new Color(251, 234, 117),
	// 		new Color(157, 229, 179),
	// 		new Color(116, 222, 239),
	// 		new Color(228, 134, 250)
	// 	};
	//
	// 	for (int i = 0; i < labels.size(); i++) {
	// 		Color color = customColors[i % customColors.length]; // 색 순환 적용
	// 		plot.setSectionPaint(labels.get(i), color);
	// 	}
	//
	// 	// 4. 이미지 인코딩
	// 	BufferedImage image = chart.createBufferedImage(600, 400);
	// 	ByteArrayOutputStream baos = new ByteArrayOutputStream();
	// 	javax.imageio.ImageIO.write(image, "png", baos);
	// 	return Base64.getEncoder().encodeToString(baos.toByteArray());
	// }





	public String createOTDChart(List<ProjectOTD> otdList, Long currentProjectId) throws IOException, FontFormatException {
		// 데이터 준비
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		Set<String> existingNames = new HashSet<>();
		Map<String, ProjectOTD> nameToDtoMap = new HashMap<>();

		for (ProjectOTD dto : otdList) {
			String originalName = Optional.ofNullable(dto.getProjectName()).orElse("이름 없음");
			String uniqueName = originalName;
			int suffix = 2;
			while (existingNames.contains(uniqueName)) {
				uniqueName = originalName + " (" + suffix++ + ")";
			}
			existingNames.add(uniqueName);
			nameToDtoMap.put(uniqueName, dto);
			dataset.addValue(dto.getOtdRate(), "OTD", uniqueName);
		}

		// 차트 생성
		// "프로젝트 납기 준수율 비교",
		JFreeChart chart = ChartFactory.createBarChart(
			"",
			"프로젝트",
			"OTD(%)",
			dataset,
			PlotOrientation.HORIZONTAL,
			false, true, false
		);

		CategoryPlot plot = chart.getCategoryPlot();

		// 사용자 정의 렌더러 (막대 색상 및 라벨 조건부 출력)
		BarRenderer renderer = new BarRenderer() {
			@Override
			public Paint getItemPaint(int row, int column) {
				String projectName = (String) dataset.getColumnKey(column);
				ProjectOTD dto = nameToDtoMap.get(projectName);
				if (dto != null && dto.getProjectId().equals(currentProjectId)) {
					return new Color(26, 188, 156); // 강조 색
				}
				return new Color(211, 211, 211); // 기본 회색
			}
		};

		// 폰트 설정
		Font labelFont = Font.createFont(Font.TRUETYPE_FONT, new File(FONT_PATH)).deriveFont(Font.PLAIN, 12f);
		Font koreanFont = labelFont;

		// 조건부 라벨: 강조된 항목만 %
		renderer.setDefaultItemLabelGenerator(new CategoryItemLabelGenerator() {
			@Override
			public String generateLabel(CategoryDataset dataset, int row, int column) {
				String projectName = (String) dataset.getColumnKey(column);
				ProjectOTD dto = nameToDtoMap.get(projectName);
				if (dto != null && dto.getProjectId().equals(currentProjectId)) {
					return String.format("%.1f%%", dto.getOtdRate());
				}
				return null;
			}

			@Override
			public String generateColumnLabel(CategoryDataset dataset, int column) {
				return (String) dataset.getColumnKey(column);
			}

			@Override
			public String generateRowLabel(CategoryDataset dataset, int row) {
				return (String) dataset.getRowKey(row);
			}
		});
		renderer.setDefaultItemLabelsVisible(true);
		renderer.setDefaultItemLabelFont(labelFont);
		renderer.setDefaultItemLabelPaint(Color.BLACK);
		renderer.setDefaultPositiveItemLabelPosition(
			new ItemLabelPosition(ItemLabelAnchor.OUTSIDE3, TextAnchor.CENTER_LEFT)
		);
		renderer.setBarPainter(new StandardBarPainter());
		renderer.setShadowVisible(false);
		renderer.setDrawBarOutline(false);
		renderer.setMaximumBarWidth(0.08);
		plot.setRenderer(renderer);

		// 평균선 계산 및 시각화 (파란색으로 수정)
		double avgOtd = otdList.stream()
			.mapToDouble(ProjectOTD::getOtdRate)
			.average()
			.orElse(0.0);

		ValueMarker avgMarker = new ValueMarker(avgOtd);
		avgMarker.setPaint(new Color(77, 145, 255)); // #4D91FF
		avgMarker.setStroke(new BasicStroke(1.5f));
		// 라벨은 제거
		plot.addRangeMarker(avgMarker);

		// 2. 평균 텍스트를 차트 오른쪽 상단에 주석으로 표시
		CategoryTextAnnotation avgAnnotation = new CategoryTextAnnotation(
			String.format("평균 OTD: %.1f%%", avgOtd),
			dataset.getColumnKey(dataset.getColumnCount() - 1), // 가장 오른쪽 바 기준
			plot.getRangeAxis().getUpperBound() * 0.97          // Y축 최상단에서 살짝 아래
		);
		avgAnnotation.setFont(koreanFont.deriveFont(Font.BOLD));
		avgAnnotation.setPaint(new Color(77, 145, 255)); // 파란색
		avgAnnotation.setTextAnchor(TextAnchor.TOP_RIGHT);
		plot.addAnnotation(avgAnnotation);

		// 현재 프로젝트 기준선
		ProjectOTD current = otdList.stream()
			.filter(dto -> dto.getProjectId().equals(currentProjectId))
			.findFirst().orElse(null);

		if (current != null) {
			double currentOtd = current.getOtdRate();
			ValueMarker marker = new ValueMarker(currentOtd);
			marker.setPaint(Color.RED);
			marker.setStroke(new BasicStroke(2f));
			plot.addRangeMarker(marker); // 수직선
		}

		// Y축 항목 라벨 강조 (Bold)
		CategoryAxis domainAxis = plot.getDomainAxis();
		for (String name : nameToDtoMap.keySet()) {
			ProjectOTD dto = nameToDtoMap.get(name);
			if (dto.getProjectId().equals(currentProjectId)) {
				domainAxis.setTickLabelFont(name, koreanFont.deriveFont(Font.BOLD));
			} else {
				domainAxis.setTickLabelFont(name, koreanFont);
			}
		}

		// 전역 폰트 적용
		chart.getTitle().setFont(koreanFont);
		domainAxis.setLabelFont(koreanFont);
		plot.getRangeAxis().setLabelFont(koreanFont);
		plot.getRangeAxis().setTickLabelFont(koreanFont);

		// 배경 및 스타일
		plot.setBackgroundPaint(Color.WHITE);
		plot.setRangeGridlinePaint(new Color(180, 180, 180));
		plot.setOutlineVisible(false);

		// 이미지 → Base64 변환
		BufferedImage image = chart.createBufferedImage(800, 500);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(image, "png", baos);
		return Base64.getEncoder().encodeToString(baos.toByteArray());
	}

	// 설명. 부서별 지연 분석
	public String createDeptDelayChart(List<Map<String, Object>> deptDelayStats) throws IOException, FontFormatException {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		double totalDelaySum = 0.0;
		int deptCount = 0;

		// ① 데이터 준비 + 총합
		for (Map<String, Object> stat : deptDelayStats) {
			String deptName = ((String) stat.get("deptName")).trim();
			Number totalDelay = (Number) stat.get("totalDelay");

			if (deptName != null && !deptName.isEmpty()) {
				double delay = totalDelay.doubleValue();
				dataset.addValue(delay, "지연일", deptName);
				totalDelaySum += delay;
				deptCount++;
			}
		}

		// 평균 계산
		double avgDelay = deptCount > 0 ? totalDelaySum / deptCount : 0.0;
		double finalTotal = totalDelaySum;

		// ② 차트 생성
		JFreeChart chart = ChartFactory.createBarChart(
			"",
			"부서명",
			"지연일 수",
			dataset,
			PlotOrientation.VERTICAL,
			false, true, false
		);

		CategoryPlot plot = chart.getCategoryPlot();
		plot.setBackgroundPaint(Color.WHITE);
		plot.setRangeGridlinePaint(Color.GRAY);
		plot.setOutlineVisible(false);

		Font koreanFont = Font.createFont(Font.TRUETYPE_FONT, new File(FONT_PATH)).deriveFont(12f);

		// ③ 렌더러
		BarRenderer renderer = new BarRenderer();
		renderer.setBarPainter(new StandardBarPainter());
		renderer.setSeriesPaint(0, new Color(252, 179, 112));
		renderer.setMaximumBarWidth(0.1);
		renderer.setShadowVisible(false); // 그림자 제거

		renderer.setDefaultItemLabelGenerator(new CategoryItemLabelGenerator() {
			@Override
			public String generateLabel(CategoryDataset dataset, int row, int column) {
				Number value = dataset.getValue(row, column);
				if (value != null && finalTotal > 0) {
					double pct = value.doubleValue() * 100 / finalTotal;
					return String.format("%.1f%%", pct);
				}
				return "";
			}

			@Override
			public String generateRowLabel(CategoryDataset dataset, int row) {
				return (String) dataset.getRowKey(row);
			}

			@Override
			public String generateColumnLabel(CategoryDataset dataset, int column) {
				return (String) dataset.getColumnKey(column);
			}
		});

		renderer.setDefaultItemLabelsVisible(true);
		renderer.setDefaultItemLabelFont(koreanFont);
		renderer.setDefaultItemLabelPaint(Color.BLACK);
		plot.setRenderer(renderer);

		// ⑤ 축 폰트
		plot.getDomainAxis().setTickLabelFont(koreanFont);
		plot.getDomainAxis().setLabelFont(koreanFont);
		plot.getRangeAxis().setTickLabelFont(koreanFont);
		plot.getRangeAxis().setLabelFont(koreanFont);

		// ⑥ 평균선 추가
		ValueMarker avgMarker = new ValueMarker(avgDelay);
		avgMarker.setPaint(new Color(77, 145, 255)); // 파란색
		avgMarker.setStroke(new BasicStroke(2.0f));
		plot.addRangeMarker(avgMarker);

		// ⑦ 평균 주석 표시
		CategoryTextAnnotation annotation = new CategoryTextAnnotation(
			String.format("평균: %.1f", avgDelay),
			(String) dataset.getColumnKey(0), // 첫 번째 바 위에 표시
			avgDelay + 0.5                    // 살짝 위로
		);
		annotation.setFont(koreanFont.deriveFont(Font.BOLD));
		annotation.setPaint(new Color(77, 145, 255));
		annotation.setTextAnchor(TextAnchor.BOTTOM_LEFT);
		plot.addAnnotation(annotation);

		// ⑧ 타이틀 폰트
		if (chart.getTitle() != null) {
			chart.getTitle().setFont(koreanFont);
		}

		// ⑨ 이미지 변환
		BufferedImage image = chart.createBufferedImage(600, 400);
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

package com.ideality.coreflow.calendar.query.service;

import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ideality.coreflow.calendar.command.application.dto.FrequencyInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.ideality.coreflow.calendar.query.dto.ResponseScheduleDTO;
import com.ideality.coreflow.calendar.query.dto.ScheduleDetailDTO;
import com.ideality.coreflow.calendar.query.dto.TodayScheduleDTO;
import com.ideality.coreflow.common.exception.BaseException;
import com.ideality.coreflow.common.exception.ErrorCode;
import com.ideality.coreflow.user.query.service.UserQueryService;

import lombok.RequiredArgsConstructor;

@Slf4j
@Service
@RequiredArgsConstructor
public class CalendarQueryFacadeService {

	private final CalendarQueryService calendarService;
	private final UserQueryService userQueryService;

	// 개인 일정 상세 정보 조회
	public ScheduleDetailDTO getPersonalDetail(Long userId, Long scheduleId) {
		if(!userQueryService.selectUserById(userId)){
			throw new BaseException(ErrorCode.USER_NOT_FOUND);
		}

		ScheduleDetailDTO result = calendarService.getPersonalDetail(userId, scheduleId);
		if (result == null) {
			throw new BaseException(ErrorCode.SCHEDULE_NOT_FOUND);
		}
		return result;
	}

	// 개인 일정 목록 조회
	public List<ResponseScheduleDTO> getAllPersonalSchedule(Long userId) {
		// 유저 확인
		if(!userQueryService.selectUserById(userId)){
			throw new BaseException(ErrorCode.USER_NOT_FOUND);
		}
		return calendarService.getAllPersonalSchedule(userId);
	}

	// 오늘의 개인 일정 목록 조회
	public List<TodayScheduleDTO> getTodayPersonal(Long userId) {
		if(!userQueryService.selectUserById(userId)){
			throw new BaseException(ErrorCode.USER_NOT_FOUND);
		}

		// 오늘의 날짜. 및 시간
		LocalDateTime now = LocalDateTime.now();    // 2019-11-12T16:34:30.388

		// 오늘 일정 목록 가져오기
		List<TodayScheduleDTO> scheduleList = calendarService.getTodayPersonal(userId, now);

		// leftDateTime, isToday 계산해서 DTO에 추가
		return scheduleList.stream().peek(schedule -> {
			schedule.setIsToday(
					!now.toLocalDate().isBefore(schedule.getStartAt().toLocalDate()) &&
							!now.toLocalDate().isAfter(schedule.getEndAt().toLocalDate())
			);
			schedule.setLeftDateTime(Math.abs(Duration.between(now, schedule.getStartAt()).toMinutes()));
		}).toList();
	}

	// TODO. 해당 월에 대한 개인 일정 목록 조회
	public List<ResponseScheduleDTO> getScheduleByMonth(Long userId, int year, int month){
		if (!userQueryService.selectUserById(userId)) {
			throw new BaseException(ErrorCode.USER_NOT_FOUND);
		}

		// 1. 기본 일정 리스트 가져오기
		List<ResponseScheduleDTO> schedules = calendarService.getScheduleByMonth(userId, year, month);

		// 2. 반복 일정 + 규칙 가져오기
		List<ScheduleDetailDTO> repeatingSchedules = calendarService.selectRepeatingSchedulesWithRules(userId);

		// 3. 반복 일정 확장
		// 확장 범위
		LocalDateTime startOfMonth = LocalDate.of(year, month, 1).atStartOfDay();
		LocalDateTime endOfMonth = startOfMonth.plusMonths(1).minusSeconds(1);

		// 해당 반복 규칙에 따라 월별 반복 일정들을 생성하여 반환
		for (ScheduleDetailDTO repeat : repeatingSchedules) {
			schedules.addAll(expandSchedule(repeat, startOfMonth, endOfMonth));
		}
		return schedules;
	}

	private List<ResponseScheduleDTO> expandSchedule(ScheduleDetailDTO originSchedule, LocalDateTime startOfMonth, LocalDateTime endOfMonth) {
		log.info("해당 범위의 반복 일정 만들기: {}, {}", startOfMonth, endOfMonth);
		// 반복 규칙을 가진 특정 일정으로 -> 해당 범위의 반복 일정 만들기
		List<ResponseScheduleDTO> result = new ArrayList<>();

		FrequencyInfo rule = originSchedule.getFrequencyInfo();
		// rule이 null이거나 반복 유형이 없으면 아무것도 생성하지 않고 빈 리스트 반환.
		if (rule == null || rule.getFrequencyType() == null) return result;

		// 기반 일정
		LocalDateTime originalStart = originSchedule.getStartAt();
		LocalDateTime originalEnd = originSchedule.getEndAt();

		// 일정 길이 계산
		Duration duration = Duration.between(originalStart, originalEnd);

		// 반복 종료일 계산 - 반복의 종료일이 지정되어 있다면, 그 날짜까지만 확장함.
		// 없으면 그냥 해당 달의 말일까지 확장
		LocalDateTime repeatUntil = rule.getEndDate() ;
		log.info("반복 종료일: {}", rule.getEndDate());

		// 설명. 반복 유형별 일정 확장

		switch (rule.getFrequencyType()) {
			// 설명. 1. DAILY : 매일, 혹은 N일마다 반복
			case DAILY -> {
				log.info("DAILY 반복 만들기");
				LocalDateTime current = originalStart;

				// 시작일 기준으로 매 repeatInterval일마다 하나씩 생성 (해당 월 내 일정만 추출)
				while (!current.isAfter(repeatUntil) && !current.isAfter(endOfMonth)) {
					if (!current.isBefore(startOfMonth)) {
						if (isSameDate(current, originalStart)) {
							current = current.plusDays(rule.getRepeatInterval());
							continue;
						}
						result.add(toResponse(originSchedule, current, duration));
					}
					current = current.plusDays(rule.getRepeatInterval());
				}

			}
			// 설명. 2. WEEKLY : 매주 특정 요일에 반복
			// byDay를 기반으로 어떤 요일에 반복되는지 파싱 (MON,WED,FRI)
			// repeatInterval=2이면 격주 반복도 가능
			case WEEKLY -> {
				log.info("WEEKLY 반복 만들기");

				List<DayOfWeek> dayList = parseDays(rule.getByDay());
				if (dayList.isEmpty()) return result;

				LocalDate firstRepeatDate = originalStart.toLocalDate();
				LocalTime time = originalStart.toLocalTime();

				while (!firstRepeatDate.atTime(time).isAfter(repeatUntil)) {
					for (DayOfWeek day : dayList) {
						LocalDate weekStartDate = firstRepeatDate;
						LocalDate targetDate = weekStartDate.with(TemporalAdjusters.nextOrSame(day));
						LocalDateTime targetDateTime = targetDate.atTime(time);

						if (isSameDate(targetDateTime, originalStart)) continue;
						if (shouldSkip(targetDateTime, originalStart, repeatUntil, startOfMonth, endOfMonth)) continue;

						result.add(toResponse(originSchedule, targetDateTime, duration));
					}
					firstRepeatDate = firstRepeatDate.plusWeeks(rule.getRepeatInterval());
				}
			}

			// 설명. 3. MONTHLY : 매달 N일에 반복되는 일정
			// byMonthDay 필드에 따라 반복일 결정
			case MONTHLY -> {
				log.info("MONTHLY 반복 만들기");

				Integer byMonthDay = rule.getByMonthDay();
				Integer bySetPos = rule.getBySetPos();
				String byDay = rule.getByDay();

				LocalDateTime current = startOfMonth.withDayOfMonth(1);
				while (!current.isAfter(endOfMonth) && !current.isAfter(repeatUntil)) {
					LocalDateTime target;

					if (bySetPos != null && byDay != null) {
						// 매달 n번째 요일 (예: 셋째 주 목요일)
						DayOfWeek dayOfWeek = convertToDayOfWeek(byDay);
						target = getNthWeekdayOfMonth(current.getYear(), current.getMonthValue(), dayOfWeek, bySetPos, originalStart.toLocalTime());
					} else if (byMonthDay != null) {
						// 매달 n일 (예: 15일)
						int day = Math.min(byMonthDay, current.toLocalDate().lengthOfMonth());
						target = current.withDayOfMonth(day)
							.withHour(originalStart.getHour())
							.withMinute(originalStart.getMinute());
					} else {
						// 아무 규칙도 없다면 종료
						break;
					}

					if (!target.isBefore(startOfMonth) && !target.isAfter(endOfMonth) && !target.isAfter(repeatUntil)) {
						if (isSameDate(target, originalStart)) {
							current = current.plusMonths(rule.getRepeatInterval());
							continue;
						}
						result.add(toResponse(originSchedule, target, duration));
					}

					current = current.plusMonths(rule.getRepeatInterval());
				}
			}
		}


		return result;
	}

	private LocalDateTime getNthWeekdayOfMonth(int year, int month, DayOfWeek dayOfWeek, int nth, LocalTime time) {
		LocalDate firstDay = LocalDate.of(year, month, 1);
		LocalDate resultDate = firstDay.with(TemporalAdjusters.dayOfWeekInMonth(nth, dayOfWeek));
		return resultDate.atTime(time);
	}


	// toResponse(repeat, 반복일시, duration) : 기존 일정의 내용을 그대로 유지하되, 새로운 일시로 Response DTO 생성
	private ResponseScheduleDTO toResponse(ScheduleDetailDTO originSchedule, LocalDateTime start, Duration duration) {

		ResponseScheduleDTO schedule = ResponseScheduleDTO.builder()
				.originalScheduleId(originSchedule.getId())
				.name(originSchedule.getName())
				.content(originSchedule.getContent())
				.isRepeat(originSchedule.getIsRepeat())
				.startAt(start)
				.endAt(start.plus(duration))
				.build();
		return schedule;
	}

	private List<DayOfWeek> parseDays(String byDay) {
		if (byDay == null || byDay.isBlank()) return List.of();

		return Arrays.stream(byDay.split(","))
				.map(String::trim)
				.map(this::convertToDayOfWeek)
				.toList();
	}

	private DayOfWeek convertToDayOfWeek(String day) {
		return switch (day.toUpperCase()) {
			case "MON" -> DayOfWeek.MONDAY;
			case "TUE" -> DayOfWeek.TUESDAY;
			case "WED" -> DayOfWeek.WEDNESDAY;
			case "THU" -> DayOfWeek.THURSDAY;
			case "FRI" -> DayOfWeek.FRIDAY;
			case "SAT" -> DayOfWeek.SATURDAY;
			case "SUN" -> DayOfWeek.SUNDAY;
			default -> throw new IllegalArgumentException("Invalid day: " + day);
		};
	}

	// 중복 체크
	private boolean isSameDate(LocalDateTime a, LocalDateTime b) {
		return a.toLocalDate().equals(b.toLocalDate());
	}

	private boolean shouldSkip(LocalDateTime target, LocalDateTime original, LocalDateTime repeatUntil, LocalDateTime startOfMonth, LocalDateTime endOfMonth) {
		return isSameDate(target, original) ||
				target.isBefore(original) ||
				target.isBefore(startOfMonth) ||
				target.isAfter(repeatUntil) ||
				target.isAfter(endOfMonth);
	}

}
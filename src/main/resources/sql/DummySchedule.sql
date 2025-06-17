Use company_a;
-- 반복 규칙 없는 개인 일정
INSERT INTO schedule (
    name,
    content,
    start_at,
    end_at,
    user_id,
    dept_id,
    is_repeat,
    event_type
) VALUES (
             '보고서 마무리',
             '프로젝트 리포트 pdf 생성해서 보고서 제출하기',
             '2025-06-09 00:00:00',
             '2025-06-09 00:00:00',
             1,
             NULL,
             FALSE,
             'PERSONAL',
             NULL
         );


-- ------------------------ 반복 규칙 있는 개일 일정 생성 -----------------------------
-- 3일마다 반복
START TRANSACTION;

-- 1. schedule 테이블 INSERT
INSERT INTO schedule (
    name, content, start_at, end_at, user_id, dept_id,
    is_repeat, is_deleted, event_type
) VALUES (
             '출근 루틴 체크',
             '3일마다 출근 루틴 체크하기 - 나태해지지 말자!',
             '2025-06-09 07:00:00',
             '2025-06-09 08:00:00',
             1,
             NULL,
             TRUE,
             FALSE,
             'PERSONAL'
         );

-- 2. repeat_rule 테이블 INSERT
INSERT INTO repeat_rule (
    schedule_id,
    frequency,
    repeat_interval,
    end_date,
    by_day,
    by_month_day,
    by_set_pos
) VALUES (
             LAST_INSERT_ID(),
             'DAILY',
             3,
             '2025-12-31',
             NULL,
             NULL,
             NULL
         );

COMMIT;


-- 주간 반복
START TRANSACTION;
-- 1. schedule 생성
INSERT INTO schedule (
    name, content, start_at, end_at, user_id, dept_id,
    is_repeat, is_deleted, event_type
) VALUES (
             '주간 정기 스터디',
             '매주 월요일마다 진행',
             '2025-06-09 09:00:00',
             '2025-06-09 10:00:00',
             1, NULL,
             TRUE, FALSE,
             'PERSONAL'
         );

-- 2. repeat_rule 생성
INSERT INTO repeat_rule (
    schedule_id, frequency, repeat_interval, end_date,
    by_day, by_month_day, by_set_pos
) VALUES (
             LAST_INSERT_ID(), 'WEEKLY', 1, '2025-12-31',
             'MON', NULL, NULL
         );

COMMIT;


-- 월간 반복
START TRANSACTION;

-- 1. schedule 생성
INSERT INTO schedule (
    name, content, start_at, end_at, user_id, dept_id,
    is_repeat, is_deleted, event_type
) VALUES (
             '월급 들어오는 날',
             '매달 15일 월급 들어옴',
             '2025-06-15 09:00:00',
             '2025-06-15 10:00:00',
             1, NULL,
             TRUE, FALSE,
             'PERSONAL'
         );

-- 2. repeat_rule 생성
INSERT INTO repeat_rule (
    schedule_id, frequency, repeat_interval, end_date,
    by_day, by_month_day, by_set_pos
) VALUES (
             LAST_INSERT_ID(),      -- 방금 생성된 schedule.id
             'MONTHLY',
             1,
             '2025-12-31',
             NULL,
             15,
             NULL
         );

COMMIT;

-- 매달 특정 주차/특정 요일 반복 일정
START TRANSACTION;

-- 1. schedule 테이블에 기본 일정 삽입
INSERT INTO schedule (
    name, content, start_at, end_at, user_id, dept_id,
    is_repeat, is_deleted, event_type
) VALUES (
             '월급 들어오는 날',
             '매달 셋째 주 목요일에 월급 들어옴',
             '2025-06-20 09:00:00',
             '2025-06-20 10:00:00',
             1,
             NULL,
             TRUE,
             FALSE,
             'PERSONAL'
         );

-- 2. repeat_rule 테이블에 반복 규칙 삽입
INSERT INTO repeat_rule (
    schedule_id,
    frequency,
    repeat_interval,
    end_date,
    by_day,
    by_month_day,
    by_set_pos
) VALUES (
             LAST_INSERT_ID(),
             'MONTHLY',
             1,
             '2025-12-31',
             'THU',
             NULL,
             3
         );

COMMIT;

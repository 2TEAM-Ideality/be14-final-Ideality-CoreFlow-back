-- 데이터 초기화
DELETE FROM relation;
DELETE FROM work_dept;
DELETE FROM participant;
DELETE FROM work;
DELETE FROM project;
DELETE FROM user;
DELETE FROM role;
DELETE FROM dept;

-- 부서
INSERT INTO dept (id, name)
VALUES (1, '기획팀'),
       (2, '개발팀'),
       (3, '디자인팀');

-- 역할
INSERT INTO role (id, name)
VALUES (1, 'PLANNER'),
       (2, 'DEVELOPER');

-- 사용자
INSERT INTO user (id, employee_num, password, name, email, birth, hire_date, is_resign,
                  profile_image, dept_name, job_rank_name, job_role_name, resign_date)
VALUES
    (1, 'EMP001', 'pass123', '홍길동', 'hong@example.com', '1990-01-01', '2020-01-01', FALSE,
     NULL, '기획팀', '대리', '팀원', NULL),
    (2, 'EMP002', 'pass123', '김철수', 'kim@example.com', '1992-05-05', '2021-06-01', FALSE,
     NULL, '개발팀', '주임', '팀원', NULL);

-- 프로젝트
INSERT INTO project (id, code, name, description, created_at, start_base, end_base, start_expect, end_expect,
                     progress_rate, passed_rate, delay_days, status)
VALUES
    (1, 'PRJ001', '신제품 출시 프로젝트', '프로젝트 설명입니다.', NOW(),
     '2025-06-01', '2025-07-31', '2025-06-01', '2025-07-31',
     0, 0, 0, 'PENDING');

-- 작업 (상위 작업과 하위 세부일정)
INSERT INTO work (id, name, description, created_at, start_base, end_base, start_expect, end_expect,
                  progress_rate, passed_rate, delay_days, status, slack_time, project_id, parent_task_id)
VALUES
-- 상위 작업
(1, '상위 작업 A', '상위 작업 설명입니다.', NOW(), '2025-06-01', '2025-06-10', '2025-06-01', '2025-06-10',
 0, 0, 0, 'PENDING', 0, 1, NULL),
-- 세부일정 1
(2, '세부일정 A-1', '세부일정 A-1 설명입니다.', NOW(), '2025-06-01', '2025-06-03', '2025-06-01', '2025-06-03',
 0, 0, 0, 'PENDING', 0, 1, 1),
-- 세부일정 2
(3, '세부일정 A-2', '세부일정 A-2 설명입니다.', NOW(), '2025-06-04', '2025-06-06', '2025-06-04', '2025-06-06',
 0, 0, 0, 'PENDING', 0, 1, 1);

-- 작업 관계 (A-1 → A-2)
INSERT INTO relation (prev_work_id, next_work_id)
VALUES (2, 3);

-- 작업 부서 관계
INSERT INTO work_dept (work_id, dept_id, is_deleted)
VALUES
    (2, 2, FALSE),  -- 세부일정 A-1은 개발팀
    (3, 3, FALSE);  -- 세부일정 A-2는 디자인팀

-- 참여자
INSERT INTO participant (target_type, target_id, user_id, role_id)
VALUES
    ('WORK', 2, 1, 1), -- A-1에 유저 1 참여
    ('WORK', 3, 2, 2); -- A-2에 유저 2 참여

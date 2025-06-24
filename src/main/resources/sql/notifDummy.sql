USE company_a;

INSERT INTO project (id, name, description, created_at, start_base, end_base, start_expect, end_expect, progress_rate, passed_rate, delay_days, status)
VALUES (1000, '지연 전파 테스트 프로젝트', '지연 전파 기능 확인용 테스트 프로젝트', NOW(), '2025-07-01', '2025-07-17', '2025-07-01', '2025-07-31', 0, 0, 0, 'PENDING');

-- Task 1 (선행)
INSERT INTO work (id, name, description, created_at, start_base, end_base, start_expect, end_expect, progress_rate, passed_rate, delay_days, status, slack_time, parent_task_id, project_id)
VALUES (1001, '태스크 1', '프로젝트 시작 태스크', NOW(), '2025-07-01', '2025-07-05', '2025-07-01', '2025-07-05', 0, 0, 0, 'PENDING', 1, NULL, 1000);

-- Task 2 (Task 1 이후)
INSERT INTO work (id, name, description, created_at, start_base, end_base, start_expect, end_expect, progress_rate, passed_rate, delay_days, status, slack_time, parent_task_id, project_id)
VALUES (1002, '태스크 2', '분기 1 작업', NOW(), '2025-07-06', '2025-07-12', '2025-07-06', '2025-07-12', 0, 0, 0, 'PENDING', 2, NULL, 1000);

-- Task 3 (Task 1 이후)
INSERT INTO work (id, name, description, created_at, start_base, end_base, start_expect, end_expect, progress_rate, passed_rate, delay_days, status, slack_time, parent_task_id, project_id)
VALUES (1003, '태스크 3', '분기 2 작업', NOW(), '2025-07-06', '2025-07-09', '2025-07-06', '2025-07-09', 0, 0, 0, 'PENDING', 1, NULL, 1000);

-- Task 4 (Task 2, 3 이후)
INSERT INTO work (id, name, description, created_at, start_base, end_base, start_expect, end_expect, progress_rate, passed_rate, delay_days, status, slack_time, parent_task_id, project_id)
VALUES (1004, '태스크 4', '종료 작업', NOW(), '2025-07-13', '2025-07-17', '2025-07-13', '2025-07-17', 0, 0, 0, 'PENDING', 3, NULL, 1000);

-- Task 1 Details
INSERT INTO work (id, name, description, created_at, start_base, end_base, start_expect, end_expect, progress_rate, passed_rate, delay_days, status, slack_time, parent_task_id, project_id)
VALUES
    (1011, '세부일정 1-1', '태스크1 - 세부1', NOW(), '2025-07-01', '2025-07-02', '2025-07-01', '2025-07-02', 0, 0, 0, 'PENDING', 0, 1001, 1000),
    (1012, '세부일정 1-2', '태스크1 - 세부2', NOW(), '2025-07-03', '2025-07-05', '2025-07-03', '2025-07-05', 0, 0, 0, 'PENDING', 0, 1001, 1000);

-- Task 2 Details
INSERT INTO work (id, name, description, created_at, start_base, end_base, start_expect, end_expect, progress_rate, passed_rate, delay_days, status, slack_time, parent_task_id, project_id)
VALUES
    (1021, '세부일정 2-1', '태스크2 - 세부1', NOW(), '2025-07-06', '2025-07-08', '2025-07-06', '2025-07-08', 0, 0, 0, 'PENDING', 0, 1002, 1000),
    (1022, '세부일정 2-2', '태스크2 - 세부2', NOW(), '2025-07-09', '2025-07-12', '2025-07-09', '2025-07-12', 0, 0, 0, 'PENDING', 0, 1002, 1000);

-- Task 3 Details
INSERT INTO work (id, name, description, created_at, start_base, end_base, start_expect, end_expect, progress_rate, passed_rate, delay_days, status, slack_time, parent_task_id, project_id)
VALUES
    (1031, '세부일정 3-1', '태스크3 - 세부1', NOW(), '2025-07-06', '2025-07-07', '2025-07-06', '2025-07-07', 0, 0, 0, 'PENDING', 0, 1003, 1000),
    (1032, '세부일정 3-2', '태스크3 - 세부2', NOW(), '2025-07-08', '2025-07-09', '2025-07-08', '2025-07-09', 0, 0, 0, 'PENDING', 0, 1003, 1000);

-- Task 4 Details
INSERT INTO work (id, name, description, created_at, start_base, end_base, start_expect, end_expect, progress_rate, passed_rate, delay_days, status, slack_time, parent_task_id, project_id)
VALUES
    (1041, '세부일정 4-1', '태스크4 - 세부1', NOW(), '2025-07-13', '2025-07-15', '2025-07-13', '2025-07-15', 0, 0, 0, 'PENDING', 0, 1004, 1000),
    (1042, '세부일정 4-2', '태스크4 - 세부2', NOW(), '2025-07-16', '2025-07-17', '2025-07-16', '2025-07-17', 0, 0, 0, 'PENDING', 0, 1004, 1000);

-- Task 1 → Task 2, Task 3
INSERT INTO relation (prev_work_id, next_work_id) VALUES
                                                      (1001, 1002),
                                                      (1001, 1003);

-- Task 2, 3 → Task 4
INSERT INTO relation (prev_work_id, next_work_id) VALUES
                                                      (1002, 1004),
                                                      (1003, 1004);


INSERT INTO participant (target_type, target_id, user_id, role_id)
VALUES
    ('TASK', 1001, 2, 2),
    ('TASK', 1001, 3, 3),
    ('TASK', 1002, 4, 2),
    ('TASK', 1002, 5, 3),
    ('TASK', 1003, 6, 2),
    ('TASK', 1003, 7, 3),
    ('TASK', 1004, 8, 2),
    ('TASK', 1004, 1, 3),
    ('PROJECT', 1000, 9, 1);
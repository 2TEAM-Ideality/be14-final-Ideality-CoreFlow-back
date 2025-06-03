INSERT INTO work
(name, description, created_at, start_base, end_base, start_expect, end_expect, project_id)
VALUES
(
 "도식화",
 "옷 제품 기획",
 CURRENT_TIMESTAMP,
 "2025-06-01",
 "2025-06-05",
 "2025-06-01",
 "2025-06-05",
 1
),
(
    "샘플 평가",
    "디자인이 괜찮은지 평가",
    CURRENT_TIMESTAMP,
    "2025-06-09",
    "2025-06-10",
    "2025-06-09",
    "2025-06-10",
    1
),
(
 "그레이딩",
 "음",
 CURRENT_TIMESTAMP,
 "2025-06-11",
 "2025-06-13",
 "2025-06-11",
 "2025-06-13",
 1
),
(
 "원부자재 발주",
 "부자재 발주",
 CURRENT_TIMESTAMP,
 "2025-06-11",
 "2025-06-13",
 "2025-06-11",
 "2025-06-13",
 1
);

-- 작업 별 참여 부서
INSERT INTO work_dept
(work_id, dept_id)
VALUES
(
 1,
 1
),
(
 2,
 1
),2
    ),
(
    2,
    3
),
(
    2,
    4
),
(
 3,
 2
),
(
 4,
 4
);
(
    2,


-- 작업 별 관계
INSERT INTO relation
(prev_work_id, next_work_id)
VALUES
(
 1,
 2
),
(
 2,
 3
),
(
 2,
 4
);

-- 도식화 (work.id = 1)
INSERT INTO participant (target_type, target_id, user_id, role_id)
VALUES
    ('TASK', 1, 1, 2),
    ('TASK', 1, 3, 3),
    -- 샘플 평가 (work.id = 2)
    ('TASK', 2, 1, 2),
    ('TASK', 2, 2, 3),
    ('TASK', 2, 3, 2),
    ('TASK', 2, 4, 3),
    ('TASK', 2, 5, 2),
    ('TASK', 2, 6, 3),
    ('TASK', 2, 7, 2),
    ('TASK', 2, 8, 3),
    -- 그레이딩 (work.id = 3)
    ('TASK', 3, 3, 2),
    ('TASK', 3, 4, 3),
    -- 원부자재 발주 (work.id = 4)
    ('TASK', 4, 7, 2),
    ('TASK', 4, 8, 2);



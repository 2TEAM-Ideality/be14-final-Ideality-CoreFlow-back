-- 완료 프로젝트
INSERT INTO project (
    name,
    description,
    created_at,
    start_base,
    end_base,
    start_expect,
    end_expect,
    start_real,
    end_real,
    progress_rate,
    passed_rate,
    delay_days,
    status,
    template_id
)
VALUES (
           '스마트 공정 개선 프로젝트',
           '스마트 제조를 위한 공정 분석 및 개선을 목표로 하는 프로젝트입니다.',
           NOW(),
           '2025-04-01',
           '2025-06-01',
           '2025-04-01',
           '2025-06-10',
           '2025-04-03',
           '2025-06-09',
           100,
           100,
           8,
           'COMPLETED',
           NULL  -- template_id가 필요한 경우 적절한 ID로 교체
       );

-- 작업 데이터
INSERT INTO work (name, description, created_at, start_base, end_base, start_expect, end_expect, progress_rate, passed_rate, delay_days, status, slack_time, project_id)
VALUES
    ('디자인 기획', '제품 디자인을 기획하는 초기 단계', '2025-06-10 05:04:49', '2025-06-10', '2025-07-01', '2025-06-10', '2025-07-01', 100, 100, 0, 'COMPLETED', 0, 2),
    ('원부자재 소싱', '필요한 원단과 부자재 소싱 진행', '2025-06-10 05:04:49', '2025-07-02', '2025-07-15', '2025-07-02', '2025-07-15', 100, 100, 0, 'COMPLETED', 1, 2),
    ('샘플 제작', '샘플 의류 제작 및 검수 단계', '2025-06-10 05:04:49', '2025-07-16', '2025-07-25', '2025-07-16', '2025-07-25', 100, 100, 0, 'COMPLETED', 2, 2),
    ('최종 생산', '승인된 샘플 기반으로 본생산 진행', '2025-06-10 05:04:49', '2025-07-26', '2025-08-15', '2025-07-26', '2025-08-15', 100, 100, 0, 'COMPLETED', 3, 2);

-- 작업 간 관계 설정
INSERT INTO relation (prev_work_id, next_work_id)
VALUES
    (1, 2),
    (2, 3),
    (3, 4);

-- 작업별 참여 부서 설정
INSERT INTO work_dept (work_id, dept_id)
VALUES
    (1, 1), -- 디자인 기획 → 기획팀
    (1, 2), -- 디자인 기획 → 디자인팀
    (2, 3), -- 원부자재 소싱 → 소싱팀
    (3, 2), -- 샘플 제작 → 디자인팀
    (4, 4); -- 최종 생산 → 생산팀


-- 정효주 (id=9): Director
INSERT INTO participant (target_type, target_id, user_id, role_id)
VALUES ('PROJECT', 2, 9, 1); -- DIRECTOR

-- 장하오 (id=1): 기획팀 팀장
INSERT INTO participant (target_type, target_id, user_id, role_id)
VALUES ('PROJECT', 2, 1, 2); -- TEAM_LEADER

-- 성한빈 (id=3): 디자인팀 팀장
INSERT INTO participant (target_type, target_id, user_id, role_id)
VALUES ('PROJECT', 2, 3, 2); -- TEAM_LEADER

-- 류현진 (id=5): 소싱팀 팀장
INSERT INTO participant (target_type, target_id, user_id, role_id)
VALUES ('PROJECT', 2, 5, 2); -- TEAM_LEADER

-- 한석현 (id=7): 생산팀 팀장
INSERT INTO participant (target_type, target_id, user_id, role_id)
VALUES ('PROJECT', 2, 7, 2); -- TEAM_LEADER

-- 김신위 (id=2): 기획팀 팀원
INSERT INTO participant (target_type, target_id, user_id, role_id)
VALUES ('PROJECT', 2, 2, 3); -- TEAM_MEMBER

-- 이혜영 (id=4): 디자인팀 팀원
INSERT INTO participant (target_type, target_id, user_id, role_id)
VALUES ('PROJECT', 2, 4, 3); -- TEAM_MEMBER

-- 권민수 (id=6): 소싱팀 팀원
INSERT INTO participant (target_type, target_id, user_id, role_id)
VALUES ('PROJECT', 2, 6, 3); -- TEAM_MEMBER

-- 김도영 (id=8): 생산팀 팀원
INSERT INTO participant (target_type, target_id, user_id, role_id)
VALUES ('PROJECT', 2, 8, 3); -- TEAM_MEMBER



-- 지연 내역이 있는 작업으로 수정
-- 샘플 제작: 기준 종료일 2025-07-25 → 실제 종료일 2025-07-28 (3일 지연)
UPDATE work
SET
    end_real = '2025-07-28',
    delay_days = 3
WHERE id = 10;

-- 최종 생산: 기준 종료일 2025-08-15 → 실제 종료일 2025-08-20 (5일 지연)
UPDATE work
SET
    end_real = '2025-08-20',
    delay_days = 5
WHERE id = 11;


-- 지연 결재

INSERT INTO delay_reason (id, reason) VALUES
                                          (1, '자재 수급 지연'),
                                          (2, '인력 부족');


-- [1] 샘플 제작 (work_id = 10, delay_days = 3)
INSERT INTO approval (id, user_id, title, type, status, content, created_at, approved_at, work_id)
VALUES (1001, 1, '샘플 제작 지연 승인 요청', 'DELAY', 'APPROVED', '샘플 제작이 자재 수급 지연으로 인해 3일 지연되었습니다.', '2025-07-28 09:00:00', '2025-07-28 12:00:00', 10);

INSERT INTO approval_participant (id, approval_id, user_id, role, created_at)
VALUES
    (2001, 1001, 2, 'APPROVER', '2025-07-28 09:01:00'),
    (2002, 1001, 3, 'VIEWER', '2025-07-28 09:02:00');

INSERT INTO delay_approval (id, delay_days, action_detail, delay_reason_id, approval_id)
VALUES (3001, 3, '공급업체와 일정 조율 후 생산 재개함.', 1, 1001);

-- [2] 최종 생산 (work_id = 11, delay_days = 5)
INSERT INTO approval (id, user_id, title, type, status, content, created_at, approved_at, work_id)
VALUES (1002, 1, '최종 생산 지연 승인 요청', 'DELAY', 'APPROVED', '최종 생산이 인력 부족으로 인해 5일 지연되었습니다.', '2025-08-20 10:00:00', '2025-08-20 14:00:00', 11);

INSERT INTO approval_participant (id, approval_id, user_id, role, created_at)
VALUES
    (2003, 1002, 2, 'APPROVER', '2025-08-20 10:01:00'),
    (2004, 1002, 3, 'VIEWER', '2025-08-20 10:02:00');

INSERT INTO delay_approval (id, delay_days, action_detail, delay_reason_id, approval_id)
VALUES (3002, 5, '생산팀 인력 충원 후 일정 보완.', 2, 1002);

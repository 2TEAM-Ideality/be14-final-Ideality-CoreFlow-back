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



-- 산출물 더미 데이터
INSERT INTO attachment (origin_name, stored_name, url, file_type, size, upload_at, target_type, target_id, uploader_id, is_deleted, task_id)
VALUES
    -- 디자인 기획 (task_id: 8)
    ('디자인 초안.png', 'file_1001_02.png', '/files/file_1001_02.png', 'IMAGE', '870KB', '2025-07-28 09:11:00', 'PROJECT', 2, 1, FALSE, 8),

    -- 원부자재 소싱 (task_id: 9)
    ('샘플 가이드.pdf', 'file_1001_01.pdf', '/files/file_1001_01.pdf', 'PDF', '1.2MB', '2025-07-28 09:10:00', 'PROJECT', 2, 1, FALSE, 9),
    ('공급 계약서.docx', 'file_1001_03.docx', '/files/file_1001_03.docx', 'DOC', '450KB', '2025-07-28 09:12:00', 'PROJECT', 2, 1, FALSE, 9),
    ('원단 목록.xlsx', 'file_1001_04.xlsx', '/files/file_1001_04.xlsx', 'EXCEL', '320KB', '2025-07-28 09:13:00', 'PROJECT', 2, 1, FALSE, 9),
    ('공급업체 인보이스.pdf', 'file_1001_06.pdf', '/files/file_1001_06.pdf', 'PDF', '680KB', '2025-07-28 09:15:00', 'PROJECT', 2, 1, FALSE, 9),
    ('원자재 인증서.png', 'file_1001_07.png', '/files/file_1001_07.png', 'IMAGE', '720KB', '2025-07-28 09:16:00', 'PROJECT', 2, 1, FALSE, 9),

    -- 샘플 제작 (task_id: 10)
    ('검수 사진1.jpg', 'file_1002_06.jpg', '/files/file_1002_06.jpg', 'IMAGE', '850KB', '2025-08-20 10:15:00', 'PROJECT', 2, 1, FALSE, 10),
    ('검수 사진2.jpg', 'file_1002_07.jpg', '/files/file_1002_07.jpg', 'IMAGE', '900KB', '2025-08-20 10:16:00', 'PROJECT', 2, 1, FALSE, 10),
    ('품질 체크리스트.xlsx', 'file_1002_05.xlsx', '/files/file_1002_05.xlsx', 'EXCEL', '310KB', '2025-08-20 10:14:00', 'PROJECT', 2, 1, FALSE, 10),

    -- 최종 생산 (task_id: 11)
    ('생산 일정표.jpg', 'file_1001_05.jpg', '/files/file_1001_05.jpg', 'IMAGE', '640KB', '2025-07-28 09:14:00', 'PROJECT', 2, 1, FALSE, 11),
    ('제조 리포트.pdf', 'file_1002_01.pdf', '/files/file_1002_01.pdf', 'PDF', '2.0MB', '2025-08-20 10:10:00', 'PROJECT', 2, 1, FALSE, 11),
    ('인력 계획표.xlsx', 'file_1002_02.xlsx', '/files/file_1002_02.xlsx', 'EXCEL', '560KB', '2025-08-20 10:11:00', 'PROJECT', 2, 1, FALSE, 11),
    ('교대 근무표.docx', 'file_1002_03.docx', '/files/file_1002_03.docx', 'DOC', '210KB', '2025-08-20 10:12:00', 'PROJECT', 2, 1, FALSE, 11),
    ('최종 생산 보고서.pdf', 'file_1002_04.pdf', '/files/file_1002_04.pdf', 'PDF', '1.5MB', '2025-08-20 10:13:00', 'PROJECT', 2, 1, FALSE, 11),
    ('지연 사유 설명서.docx', 'file_1002_08.docx', '/files/file_1002_08.docx', 'DOC', '500KB', '2025-08-20 10:17:00', 'PROJECT', 2, 1, FALSE, 11);



-- 다른 프로젝트와의 비료를 위한 완료된 프로젝트
-- 프로젝트 1
INSERT INTO project (
    name, description, created_at,
    start_base, end_base,
    start_expect, end_expect,
    start_real, end_real,
    progress_rate, passed_rate,
    delay_days, status, template_id
)
VALUES (
           '신제품 패키징 개선 프로젝트',
           '친환경 및 비용 절감을 위한 패키징 개선 프로젝트입니다.',
           '2025-05-01 10:00:00',
           '2025-03-01', '2025-04-30',
           '2025-03-02', '2025-05-01',
           '2025-03-03', '2025-05-02',
           100.0, 100.0,
           2,
           'COMPLETED',
           NULL
       );

-- 프로젝트 2
INSERT INTO project (
    name, description, created_at,
    start_base, end_base,
    start_expect, end_expect,
    start_real, end_real,
    progress_rate, passed_rate,
    delay_days, status, template_id
)
VALUES (
           'ERP 시스템 고도화 프로젝트',
           '내부 업무 효율화를 위한 ERP 기능 고도화 프로젝트입니다.',
           '2025-04-20 09:30:00',
           '2025-02-15', '2025-04-15',
           '2025-02-20', '2025-04-18',
           '2025-02-21', '2025-04-14',
           100.0, 100.0,
           0,
           'COMPLETED',
           NULL
       );

-- 프로젝트 3
INSERT INTO project (
    name, description, created_at,
    start_base, end_base,
    start_expect, end_expect,
    start_real, end_real,
    progress_rate, passed_rate,
    delay_days, status, template_id
)
VALUES (
           '공급망 리스크 분석 프로젝트',
           '리스크 진단 및 공급 안정성 확보를 위한 프로젝트입니다.',
           '2025-06-01 14:45:00',
           '2025-03-01', '2025-04-30',
           '2025-03-03', '2025-05-01',
           '2025-03-01', '2025-04-29',
           100.0, 100.0,
           0,
           'COMPLETED',
           NULL
       );

-- 프로젝트 4
INSERT INTO project (
    name, description, created_at,
    start_base, end_base,
    start_expect, end_expect,
    start_real, end_real,
    progress_rate, passed_rate,
    delay_days, status, template_id
)
VALUES (
           '품질관리 프로세스 혁신 프로젝트',
           '전사 품질관리 체계 개선을 위한 혁신 프로젝트입니다.',
           '2025-05-18 13:20:00',
           '2025-03-15', '2025-05-14',
           '2025-03-16', '2025-05-16',
           '2025-03-16', '2025-05-20',
           100.0, 100.0,
           6,
           'COMPLETED',
           NULL
       );

-- 프로젝트 5
INSERT INTO project (
    name, description, created_at,
    start_base, end_base,
    start_expect, end_expect,
    start_real, end_real,
    progress_rate, passed_rate,
    delay_days, status, template_id
)
VALUES (
           '제조라인 자동화 도입 프로젝트',
           '스마트팩토리 구현을 위한 자동화 설비 도입 프로젝트입니다.',
           '2025-06-10 16:10:00',
           '2025-04-01', '2025-05-31',
           '2025-04-01', '2025-06-01',
           '2025-04-02', '2025-06-05',
           100.0, 100.0,
           5,
           'COMPLETED',
           NULL
       );



-- project_id = 3 (신제품 패키징 개선 프로젝트)
INSERT INTO work (
    name, description, created_at,
    start_base, end_base,
    start_expect, end_expect,
    progress_rate, passed_rate,
    delay_days, status, slack_time, project_id
)
VALUES
-- 1. 기존 패키징 분석
('기존 패키징 분석', '현재 사용 중인 패키징 자재의 환경 영향 및 비용 분석', '2025-03-01 09:00:00',
 '2025-03-01', '2025-03-07',
 '2025-03-01', '2025-03-07',
 100, 100, 0, 'COMPLETED', 0, 3),

-- 2. 대체 자재 조사
('대체 자재 조사', '친환경 자재 및 대체 포장 방법 조사', '2025-03-08 09:00:00',
 '2025-03-08', '2025-03-15',
 '2025-03-08', '2025-03-15',
 100, 100, 0, 'COMPLETED', 0, 3),

-- 3. 디자인 개선안 시안
('디자인 개선안 시안', '친환경성을 반영한 포장 디자인 시안 작성', '2025-03-16 09:00:00',
 '2025-03-16', '2025-03-22',
 '2025-03-16', '2025-03-22',
 100, 100, 0, 'COMPLETED', 0, 3),

-- 4. 테스트 샘플 제작
('테스트 샘플 제작', '시안 기반 패키징 테스트 샘플 제작', '2025-03-23 09:00:00',
 '2025-03-23', '2025-04-01',
 '2025-03-23', '2025-04-01',
 100, 100, 0, 'COMPLETED', 0, 3),

-- 5. 사용자 피드백 수집
('사용자 피드백 수집', '내부 평가 및 사용자 피드백 수집을 통한 개선', '2025-04-02 09:00:00',
 '2025-04-02', '2025-04-10',
 '2025-04-02', '2025-04-10',
 100, 100, 0, 'COMPLETED', 0, 3),

-- 6. 최종 디자인 확정
('최종 디자인 확정', '최종 포장 디자인 확정 및 생산 준비', '2025-04-11 09:00:00',
 '2025-04-11', '2025-04-30',
 '2025-04-11', '2025-04-30',
 100, 100, 0, 'COMPLETED', 0, 3);

-- 프로젝트 3 작업 간 관계
INSERT INTO relation (prev_work_id, next_work_id) VALUES (16, 17);
INSERT INTO relation (prev_work_id, next_work_id) VALUES (17, 18);
INSERT INTO relation (prev_work_id, next_work_id) VALUES (18, 19);
INSERT INTO relation (prev_work_id, next_work_id) VALUES (19, 20);
INSERT INTO relation (prev_work_id, next_work_id) VALUES (20, 21);

-- 작업별 참여 부서
INSERT INTO work_dept (work_id, dept_id) VALUES (16, 1);
INSERT INTO work_dept (work_id, dept_id) VALUES (17, 3);
INSERT INTO work_dept (work_id, dept_id) VALUES (18, 2);
INSERT INTO work_dept (work_id, dept_id) VALUES (19, 4);
INSERT INTO work_dept (work_id, dept_id) VALUES (20, 1);
INSERT INTO work_dept (work_id, dept_id) VALUES (20, 2);
INSERT INTO work_dept (work_id, dept_id) VALUES (21, 2);
INSERT INTO work_dept (work_id, dept_id) VALUES (21, 4);

-- 참여자
-- Director
INSERT INTO participant (target_type, target_id, user_id, role_id)
VALUES ('PROJECT', 3, 9, 1); -- 정효주 (id=9)

-- 기획팀
INSERT INTO participant (target_type, target_id, user_id, role_id)
VALUES ('PROJECT', 3, 1, 2), -- 장하오 (팀장)
       ('PROJECT', 3, 2, 3); -- 김신위 (팀원)

-- 디자인팀
INSERT INTO participant (target_type, target_id, user_id, role_id)
VALUES ('PROJECT', 3, 3, 2), -- 성한빈 (팀장)
       ('PROJECT', 3, 4, 3); -- 이혜영 (팀원)

-- 소싱팀
INSERT INTO participant (target_type, target_id, user_id, role_id)
VALUES ('PROJECT', 3, 5, 2), -- 류현진 (팀장)
       ('PROJECT', 3, 6, 3); -- 권민수 (팀원)

-- 생산팀
INSERT INTO participant (target_type, target_id, user_id, role_id)
VALUES ('PROJECT', 3, 7, 2), -- 한석현 (팀장)
       ('PROJECT', 3, 8, 3); -- 김도영 (팀원)

-- 산출물 데이터
-- 기존 패키징 분석 (work_id: 16)
INSERT INTO attachment (origin_name, stored_name, url, file_type, size, upload_at, target_type, target_id, uploader_id, is_deleted, task_id)
VALUES ('기존 자재 리포트.pdf', 'file_3001_01.pdf', '/files/file_3001_01.pdf', 'PDF', '850KB', '2025-03-07 09:10:00', 'PROJECT', 3, 1, FALSE, 16);

-- 대체 자재 조사 (work_id: 17)
INSERT INTO attachment (origin_name, stored_name, url, file_type, size, upload_at, target_type, target_id, uploader_id, is_deleted, task_id)
VALUES ('대체 자재 조사.xlsx', 'file_3001_02.xlsx', '/files/file_3001_02.xlsx', 'EXCEL', '420KB', '2025-03-15 14:20:00', 'PROJECT', 3, 5, FALSE, 17);

-- 디자인 개선안 시안 (work_id: 18)
INSERT INTO attachment (origin_name, stored_name, url, file_type, size, upload_at, target_type, target_id, uploader_id, is_deleted, task_id)
VALUES ('디자인 시안 v1.jpg', 'file_3001_03.jpg', '/files/file_3001_03.jpg', 'IMAGE', '560KB', '2025-03-22 10:00:00', 'PROJECT', 3, 3, FALSE, 18);

-- 테스트 샘플 제작 (work_id: 19)
INSERT INTO attachment (origin_name, stored_name, url, file_type, size, upload_at, target_type, target_id, uploader_id, is_deleted, task_id)
VALUES ('테스트 샘플 결과.pdf', 'file_3001_04.pdf', '/files/file_3001_04.pdf', 'PDF', '1.2MB', '2025-04-01 13:30:00', 'PROJECT', 3, 4, FALSE, 19);

-- 사용자 피드백 수집 (work_id: 20)
INSERT INTO attachment (origin_name, stored_name, url, file_type, size, upload_at, target_type, target_id, uploader_id, is_deleted, task_id)
VALUES ('피드백 설문 결과.xlsx', 'file_3001_05.xlsx', '/files/file_3001_05.xlsx', 'EXCEL', '300KB', '2025-04-10 17:40:00', 'PROJECT', 3, 2, FALSE, 20);

-- 최종 디자인 확정 (work_id: 21)
INSERT INTO attachment (origin_name, stored_name, url, file_type, size, upload_at, target_type, target_id, uploader_id, is_deleted, task_id)
VALUES ('최종 디자인 도면.pdf', 'file_3001_06.pdf', '/files/file_3001_06.pdf', 'PDF', '1.5MB', '2025-04-30 09:00:00', 'PROJECT', 3, 3, FALSE, 21);

-- 기존 패키징 분석 (work_id: 16)
INSERT INTO attachment (origin_name, stored_name, url, file_type, size, upload_at, target_type, target_id, uploader_id, is_deleted, task_id)
VALUES ('기존 자재 리포트.pdf', 'file_3001_01.pdf', '/files/file_3001_01.pdf', 'PDF', '850KB', '2025-03-07 09:10:00', 'PROJECT', 3, 1, FALSE, 16);

-- 대체 자재 조사 (work_id: 17)
INSERT INTO attachment (origin_name, stored_name, url, file_type, size, upload_at, target_type, target_id, uploader_id, is_deleted, task_id)
VALUES ('대체 자재 조사.xlsx', 'file_3001_02.xlsx', '/files/file_3001_02.xlsx', 'EXCEL', '420KB', '2025-03-15 14:20:00', 'PROJECT', 3, 5, FALSE, 17);

-- 디자인 개선안 시안 (work_id: 18)
INSERT INTO attachment (origin_name, stored_name, url, file_type, size, upload_at, target_type, target_id, uploader_id, is_deleted, task_id)
VALUES ('디자인 시안 v1.jpg', 'file_3001_03.jpg', '/files/file_3001_03.jpg', 'IMAGE', '560KB', '2025-03-22 10:00:00', 'PROJECT', 3, 3, FALSE, 18);

-- 테스트 샘플 제작 (work_id: 19)
INSERT INTO attachment (origin_name, stored_name, url, file_type, size, upload_at, target_type, target_id, uploader_id, is_deleted, task_id)
VALUES ('테스트 샘플 결과.pdf', 'file_3001_04.pdf', '/files/file_3001_04.pdf', 'PDF', '1.2MB', '2025-04-01 13:30:00', 'PROJECT', 3, 4, FALSE, 19);

-- 사용자 피드백 수집 (work_id: 20)
INSERT INTO attachment (origin_name, stored_name, url, file_type, size, upload_at, target_type, target_id, uploader_id, is_deleted, task_id)
VALUES ('피드백 설문 결과.xlsx', 'file_3001_05.xlsx', '/files/file_3001_05.xlsx', 'EXCEL', '300KB', '2025-04-10 17:40:00', 'PROJECT', 3, 2, FALSE, 20);

-- 최종 디자인 확정 (work_id: 21)
INSERT INTO attachment (origin_name, stored_name, url, file_type, size, upload_at, target_type, target_id, uploader_id, is_deleted, task_id)
VALUES ('최종 디자인 도면.pdf', 'file_3001_06.pdf', '/files/file_3001_06.pdf', 'PDF', '1.5MB', '2025-04-30 09:00:00', 'PROJECT', 3, 3, FALSE, 21);


-- ----------------------------프로젝트 3-------------------------------------------
-- ERP 시스템 고도화 프로젝트 (project_id = 2)
INSERT INTO work (
    id, name, description, created_at,
    start_base, end_base,
    start_expect, end_expect,
    start_real, end_real,
    progress_rate, passed_rate,
    delay_days, status, slack_time, project_id
)
VALUES
-- ID는 34부터 시작
(34, '요구사항 분석', '각 부서의 업무 흐름을 바탕으로 ERP 기능 요구사항을 수집 및 분석합니다.',
 '2025-02-15 09:00:00', '2025-02-15', '2025-02-28', '2025-02-15', '2025-02-28',
 '2025-02-15', '2025-02-28', 100, 100, 0, 'COMPLETED', 0, 4),

(35, '시스템 아키텍처 설계', '요구사항을 반영한 ERP 시스템의 전체 구조를 설계합니다.',
 '2025-03-01 09:00:00', '2025-03-01', '2025-03-10', '2025-03-01', '2025-03-10',
 '2025-03-01', '2025-03-10', 100, 100, 0, 'COMPLETED', 0, 4),

(36, '모듈별 기능 설계', '기능별 ERP 모듈 상세 스펙을 정의하고 설계 문서를 작성합니다.',
 '2025-03-11 09:00:00', '2025-03-11', '2025-03-20', '2025-03-11', '2025-03-20',
 '2025-03-11', '2025-03-20', 100, 100, 0, 'COMPLETED', 0, 4),

(37, '개발 및 커스터마이징', '설계한 기능에 따라 ERP 시스템을 개발하고 일부 기능은 맞춤화합니다.',
 '2025-03-21 09:00:00', '2025-03-21', '2025-04-05', '2025-03-21', '2025-04-05',
 '2025-03-21', '2025-04-05', 100, 100, 0, 'COMPLETED', 0, 4),

(38, '테스트 및 QA', '전체 시스템의 기능을 점검하고 버그 및 문제점을 해결합니다.',
 '2025-04-06 09:00:00', '2025-04-06', '2025-04-10', '2025-04-06', '2025-04-10',
 '2025-04-06', '2025-04-10', 100, 100, 0, 'COMPLETED', 0, 4),

(39, '사용자 교육 및 배포', '최종 사용자를 대상으로 교육을 진행하고 시스템을 정식 배포합니다.',
 '2025-04-11 09:00:00', '2025-04-11', '2025-04-14', '2025-04-11', '2025-04-14',
 '2025-04-11', '2025-04-14', 100, 100, 0, 'COMPLETED', 0, 4);


-- 4번 프로젝트 작업 릴레이션
-- 작업 릴레이션 설정 (ERP 시스템 고도화 프로젝트)
INSERT INTO relation (prev_work_id, next_work_id) VALUES
                                                      (34, 35),  -- 요구사항 분석 → 시스템 아키텍처 설계
                                                      (35, 36),  -- 시스템 아키텍처 설계 → 모듈별 기능 설계
                                                      (36, 37),  -- 모듈별 기능 설계 → 개발 및 커스터마이징
                                                      (37, 38),  -- 개발 및 커스터마이징 → 테스트 및 QA
                                                      (38, 39);  -- 테스트 및 QA → 사용자 교육 및 배포

INSERT INTO dept (name, id, dept_code, is_deleted) VALUES
                                                        ('개발팀', 6, 'DEV', 0),
                                                        ('QA팀', 7, 'QA', 0),
                                                        ('운영팀', 8, 'OPS', 0);

INSERT INTO work_dept (work_id, dept_id) VALUES
                                             (34, 1), -- 요구사항 분석 → 기획팀
                                             (35, 5), -- 시스템 아키텍처 설계 → 개발팀
                                             (36, 5), -- 모듈별 기능 설계 → 개발팀
                                             (37, 5), -- 개발 및 커스터마이징 → 개발팀
                                             (38, 6), -- 테스트 및 QA → QA팀
                                             (39, 1), -- 사용자 교육 및 배포 → 기획팀
                                             (39, 7); -- 사용자 교육 및 배포 → 운영팀

-- Director
INSERT INTO participant (target_type, target_id, user_id, role_id)
VALUES ('PROJECT', 4, 9, 1); -- 정효주 (Director)

-- 팀장들
INSERT INTO participant (target_type, target_id, user_id, role_id) VALUES
                                                                       ('PROJECT', 4, 1, 2), -- 장하오: 기획팀 팀장
                                                                       ('PROJECT', 4, 11, 2), -- 서기웅: 개발팀 팀장
                                                                       ('PROJECT', 4, 13, 2), -- 박소현: QA팀 팀장
                                                                       ('PROJECT', 4, 14, 2); -- 송은재: 운영팀 팀장

-- 팀원들
INSERT INTO participant (target_type, target_id, user_id, role_id) VALUES
                                                                       ('PROJECT', 4, 2, 3), -- 김신위: 기획팀 팀원
                                                                       ('PROJECT', 4, 12, 3), -- 이도현: 개발팀 팀원
                                                                       ('PROJECT', 4, 15, 3), -- 김선우: QA팀 팀원
                                                                       ('PROJECT', 4, 16, 3); -- 문서윤: 운영팀 팀원


-- 작업 지연 처리
UPDATE work SET end_real = '2025-04-07', delay_days = 2 WHERE id = 37;
UPDATE work SET end_real = '2025-04-11', delay_days = 1 WHERE id = 38;

-- 지연 사유
INSERT INTO delay_reason (id, reason) VALUES
                                          (3, '외부 벤더 API 연동 지연'),
                                          (4, 'QA 도중 발견된 치명적 버그');

-- 지연 사유 등록 (미리 없으면)
INSERT INTO delay_reason (id, reason) VALUES (3, '요구사항 변경에 따른 재설계');
select * from delay_reason;

-- 승인 요청
INSERT INTO approval (id, user_id, title, type, status, content, created_at, approved_at, work_id)
VALUES (1003, 1, '모듈별 기능 설계 지연 승인 요청', 'DELAY', 'APPROVED',
        '요구사항 변경으로 인해 기능 설계를 2일 지연하였습니다.',
        '2025-03-22 10:00:00', '2025-03-22 12:00:00', 36);

-- 승인자/참고자
INSERT INTO approval_participant (id, approval_id, user_id, role, created_at)
VALUES
    (2005, 1003, 9, 'APPROVER', '2025-03-22 10:01:00'),
    (2006, 1003, 3, 'VIEWER', '2025-03-22 10:02:00');

-- 지연 상세
INSERT INTO delay_approval (id, delay_days, action_detail, delay_reason_id, approval_id)
VALUES (3003, 2, '요구사항 변경에 따라 재설계 진행 후 확정.', 3, 1003);

-- work 테이블 반영
UPDATE work
SET end_real = '2025-03-22', delay_days = 2
WHERE id = 36;

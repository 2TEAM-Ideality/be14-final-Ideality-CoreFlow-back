USE company_a;

INSERT INTO APPROVAL (user_id, title, type, status, content, created_at, approved_at, work_id)
VALUES
(1, '지연 요청1', 'DELAY', 'PENDING', '지연되었슴다', now(), null, 1),
(2, '지연 요청2', 'DELAY', 'PENDING', '지연되었슴다2', now(), null, 4);

USE company_a;
INSERT INTO APPROVAL_PARTICIPANT (approval_id, user_id, role, created_at)
VALUES
    (2, 1, 'APPROVER', now()),
    (2, 2, 'VIEWER', now()),
    (3, 1, 'APPROVER', now()),
    (3, 2, 'VIEWER', now()),
    (3, 3, 'VIEWER', now());

INSERT INTO DELAY_REASON (reason)
VALUES
    ('자재 공급 지연'),
    ('인력 부족'),
    ('설비 고장'),
    ('품질 이슈 재작업'),
    ('외주 업체 문제'),
    ('의사 결정 지연'),
    ('기타');

INSERT INTO DELAY_APPROVAL (delay_days, action_detail, delay_reason_id, approval_id)
VALUES
    (2, '자재 부족 대체', 1, 3),
    (3, '인력부족 대체', 2, 2);
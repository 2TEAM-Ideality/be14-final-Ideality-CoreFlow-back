INSERT INTO job_rank (name)
VALUES ("사원"),
       ("대리"),
       ("과장"),
       ("차장"),
       ("부장");

INSERT INTO job_role (name)
VALUES ("팀원"),
       ("팀장"),
       ("파트장"),
       ("본부장");

INSERT INTO dept (name, dept_code)
VALUES ("기획팀", "PM"),
       ("디자인팀", "DES"),
       ("소싱팀", "MD"),
       ("생산팀", "MFG");

INSERT INTO user
(employee_num,
 password,
 name,
 email,
 birth,
 hire_date,
 dept_name,
 job_rank_name,
 job_role_name
)
VALUES
    (
        'abcd',
        '1234',
        '장하오',
        'haojang@naver.com',
        '2001-01-01',
        CURRENT_DATE,
        '기획팀',
        '과장',
        '팀장'
    ),
    (
        'abce',
        '1234',
        '김신위',
        'xinyu@naver.com',
        '2003-01-01',
        CURRENT_DATE,
        '기획팀',
        '사원',
        '팀원'
    ),
    (
        'bcde',
        '1234',
        '성한빈',
        'hanbin@naver.com',
        '2001-02-02',
        CURRENT_DATE,
        '디자인팀',
        '과장',
        '팀장'
    ),
    (
        'bcdf',
        '1234',
        '이혜영',
        'hailey@naver.com',
        '2002-06-02',
        CURRENT_DATE,
        '디자인팀',
        '사원',
        '팀원'
    ),
    (
        'cdef',
        '1234',
        '류현진',
        'ryuryu@naver.com',
        '1987-03-03',
        CURRENT_DATE,
        '소싱팀',
        '부장',
        '팀장'
    ),
    (
        'cdeg',
        '1234',
        '권민수',
        'minsu@naver.com',
        '1997-04-04',
        CURRENT_DATE,
        '소싱팀',
        '대리',
        '팀원'
    ),
    (
        'defg',
        '1234',
        '한석현',
        'hancoal@naver.com',
        '1999-05-05',
        CURRENT_DATE,
        '생산팀',
        '과장',
        '팀장'
    ),
    (
        'abde',
        '1234',
        '김도영',
        'doyoungkim@naver.com',
        '2000-03-03',
        CURRENT_DATE,
        '생산팀',
        '사원',
        '팀원'
    ),
    (
        'abba',
        '1234',
        '정효주',
        'hyoju@naver.com',
        '2000-03-09',
        CURRENT_DATE,
        '기획팀',
        '차장',
        '본부장'
    );

INSERT INTO role
(name, type)
VALUES
    (
        'DIRECTOR', 'PROJECT'
    ),
    (
        'TEAM_LEADER', 'PROJECT'
    ),
    (
        'TEAM_MEMBER', 'PROJECT'
    ),
    (
        'ADMIN', 'GENERAL'
    ),
    (
        'VIEWER', 'PROJECT'
    ),
    (
        'ASSIGNEE', 'PROJECT'
    ),
    (
        'PARTICIPANT', 'PROJECT'
    )
;

INSERT INTO project
(
    name,
    description,
    created_at,
    start_base,
    end_base,
    start_expect,
    end_expect
)
VALUES
    (
        '26/FW-한화이글스-콜라보-프로젝트',
        '한화이글스와 우리 회사의 옷 콜라보를 위한 의류 개발',
        CURRENT_TIMESTAMP,
        CURRENT_DATE,
        '2025-12-28',
        CURRENT_DATE,
        '2025-12-28'
    );

-- 완료된 프로젝트
INSERT INTO project (
    id,
    name,
    description,
    created_at,
    start_base,
    end_base,
    start_expect,
    end_expect,
    passed_rate,
    progress_rate,
    delay_days,
    status,
    template_id
) VALUES
-- 프로젝트 5
(5, '신제품 출시 프로젝트', '신제품 A의 시장 출시를 위한 프로젝트입니다.', '2025-06-07 11:35:00', '2025-05-01', '2025-07-01', '2025-05-01', '2025-07-01', 100, 100, 0, 'COMPLETED', 2),

-- 프로젝트 6
(6, '내부 시스템 리뉴얼', '사내 시스템 UI/UX 리뉴얼 및 기능 개선', '2025-06-07 11:36:00', '2025-04-15', '2025-06-30', '2025-04-15', '2025-06-30', 100, 100, 0, 'COMPLETED', 1),

-- 프로젝트 7
(7, '파트너십 협업 개발', 'B사와의 협업을 통한 기능 공동 개발', '2025-06-07 11:37:00', '2025-03-10', '2025-05-31', '2025-03-10', '2025-05-31', 100, 100, 0, 'COMPLETED', NULL);


INSERT INTO user_of_role
(
    user_id,
    role_id
)
VALUES
    (
        9,
        1
    );

INSERT INTO participant
(
    target_type,
    target_id,
    user_id,
    role_id
)
VALUES
    (
        'PROJECT',
        1,
        9,
        1
    ),
    (
        'PROJECT',
        1,
        1,
        2
    ),
    (
        'PROJECT',
        1,
        3,
        2
    ),
    (
        'PROJECT',
        1,
        5,
        2
    ),
    (
        'PROJECT',
        1,
        7,
        2
    );

INSERT INTO notification
(
    target_type,
    target_id,
    content,
    status,
    dispatch_at,
    created_at
)
VALUES
    (
        'PROJECT',
        1,
        '정효주 Director가 회원님을 팀장으로 초대하였습니다.',
        'SENT',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    );

INSERT INTO notification_recipients
(
    notification_id,
    user_id
)
VALUES
    (
        1,
        3
    ),
    (
        1,
        5
    ),
    (
        1,
        7
    );



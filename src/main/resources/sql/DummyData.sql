USE company_a;

INSERT INTO job_rank (name)
VALUES ("사원"),
       ("대리"),
       ("과장"),
       ("차장"),
       ("부장"),
       ("협력업체");

INSERT INTO job_role (name)
VALUES ("팀원"),
       ("파트장"),
       ("본부장"),
       ("협력업체");


INSERT INTO dept (name, dept_code)
VALUES ("기획팀", "PM"),
       ("디자인팀", "DES"),
       ("소싱팀", "MD"),
       ("생산팀", "MFG"),
       ("협력업체", "PART");

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
    '$2a$12$dD0aS4kIMvZnWIWxVXr.3us3W97791wgVLi2gyY4kuCU6/KQMHrcG',
    '장하오',
    'haojang@naver.com',
    '2001-01-01',
    CURRENT_DATE,
    '기획팀',
    '과장',
    '파트장'
),
(
    'abce',
    '$2a$12$dD0aS4kIMvZnWIWxVXr.3us3W97791wgVLi2gyY4kuCU6/KQMHrcG',
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
    '파트장'
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
    '파트장'
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
    '파트장'
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
),
(
    'CREATOR', 'GENERAL'
),
(
    'PARTNER', 'GENERAL'
)
;

INSERT INTO role
(name, type)
VALUES
    (
        'ASSIGNEE', 'PROJECT'
    ),
    (
        'PARTICIPANT', 'PROJECT'
    );

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
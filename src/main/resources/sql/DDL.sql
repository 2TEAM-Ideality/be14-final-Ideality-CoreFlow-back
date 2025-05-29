DROP DATABASE IF EXISTS company_a;

CREATE DATABASE company_a;
USE company_a;

-- 회원
CREATE TABLE user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    employee_num VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    birth DATE,
    hire_date DATE NOT NULL,
    is_resign BOOLEAN NOT NULL DEFAULT FALSE,
    resign_date DATE,
    profile_image TEXT,
    dept_name VARCHAR(255) NOT NULL,
    job_rank_name VARCHAR(255) NOT NULL,                -- 직위 명
    job_role_name VARCHAR(255) NOT NULL DEFAULT '사원'   -- 직책 명
);

-- 직책
CREATE TABLE job_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL UNIQUE
);

-- 직위
CREATE TABLE job_rank (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL UNIQUE
);

-- 부서
CREATE TABLE dept (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL UNIQUE,
    dept_code VARCHAR(10) NOT NULL,
    is_deleted BOOLEAN DEFAULT FALSE,
    parent_dept_id BIGINT,
    CONSTRAINT FOREIGN KEY (parent_dept_id) REFERENCES dept(id)
);

-- 역할
CREATE TABLE role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL UNIQUE,
    type VARCHAR(255) NOT NULL,
    CHECK ( type in ('PROJECT', 'GENERAL'))
);

-- 회원 별 역할
CREATE TABLE user_of_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    CONSTRAINT FOREIGN KEY (user_id) REFERENCES user(id),
    CONSTRAINT FOREIGN KEY (role_id) REFERENCES role(id) ON DELETE CASCADE
);

-- 휴일
CREATE TABLE holiday (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    date DATE NOT NULL,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(255) NOT NULL,
    is_repeat VARCHAR(255) NOT NULL DEFAULT 'ONCE',
    CHECK (type IN ('COMPANY', 'NATIONAL')),
    CHECK (is_repeat IN ('YEARLY', 'ONCE'))
);

-- 알림
CREATE TABLE notification (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    target_type VARCHAR(255) NOT NULL,
    target_id BIGINT NOT NULL,
    content VARCHAR(255),
    status VARCHAR(255) NOT NULL DEFAULT 'PENDING',
    dispatch_at DATETIME NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME,
    is_auto_delete BOOLEAN NOT NULL DEFAULT FALSE,
    CHECK (target_type IN ('PAYMENT', 'PROJECT', 'WORK','SCHEDULE')),
    CHECK (status IN ('PENDING', 'SENT', 'READ'))
);

-- 알림 수신자
CREATE TABLE notification_recipients (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    notification_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    CONSTRAINT FOREIGN KEY (notification_id) REFERENCES notification(id) ON DELETE CASCADE,
    CONSTRAINT FOREIGN KEY (user_id) REFERENCES user(id)
);

-- 템플릿
CREATE TABLE template (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME,
    duration INT NOT NULL,
    task_count INT NOT NULL,
    created_by BIGINT NOT NULL,
    updated_by BIGINT NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT FOREIGN KEY (created_by) REFERENCES user(id),
    CONSTRAINT FOREIGN KEY (updated_by) REFERENCES user(id)
);

-- 참여 인원
CREATE TABLE participant (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    target_type VARCHAR(255) NOT NULL,
    target_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL
);

-- 프로젝트
CREATE TABLE project (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    created_at DATETIME NOT NULL,
    start_base DATE NOT NULL,
    end_base DATE NOT NULL,
    start_expect DATE NOT NULL,
    end_expect DATE NOT NULL,
    start_real DATE,
    end_real DATE,
    progress_rate DOUBLE NOT NULL DEFAULT 0,
    passed_rate DOUBLE NOT NULL DEFAULT 0,
    delay_days INT NOT NULL DEFAULT 0,
    status VARCHAR(255) DEFAULT 'PENDING',
    template_id BIGINT,
    CONSTRAINT FOREIGN KEY (template_id) REFERENCES template(id),
    CHECK (progress_rate BETWEEN 0 AND 100),
    CHECK (passed_rate BETWEEN 0 AND 100),
    CHECK (status IN ('PENDING', 'PROGRESS', 'COMPLETED', 'DELETED', 'CANCELLED'))
);

-- 작업
CREATE TABLE work (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    created_at DATETIME NOT NULL,
    start_base DATE NOT NULL,
    end_base DATE NOT NULL,
    start_expect DATE NOT NULL,
    end_expect DATE NOT NULL,
    start_real DATE,
    end_real DATE,
    progress_rate DOUBLE NOT NULL DEFAULT 0,
    passed_rate DOUBLE NOT NULL DEFAULT 0,
    delay_days INT NOT NULL DEFAULT 0,
    status VARCHAR(255) DEFAULT 'PENDING',
    slack_time INT NOT NULL DEFAULT 0,
    parent_task_id BIGINT,
    project_id BIGINT NOT NULL,
    CONSTRAINT FOREIGN KEY (parent_task_id) REFERENCES work(id) ON DELETE CASCADE ,
    CONSTRAINT FOREIGN KEY (project_id) REFERENCES project(id) ON DELETE CASCADE,
    CHECK (progress_rate BETWEEN 0 AND 100),
    CHECK (passed_rate BETWEEN 0 AND 100),
    CHECK (status IN ('PENDING', 'PROGRESS', 'COMPLETED', 'DELETED'))
);

-- 작업간 관계
CREATE TABLE relation (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    prev_work_id BIGINT NOT NULL,
    next_work_id BIGINT NOT NULL,
    CONSTRAINT FOREIGN KEY (prev_work_id) REFERENCES work(id) ON DELETE CASCADE,
    CONSTRAINT FOREIGN KEY (next_work_id) REFERENCES work(id) ON DELETE CASCADE
);

-- 작업별 참여 부서
CREATE TABLE work_dept (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    work_id BIGINT NOT NULL,
    dept_id BIGINT NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT FOREIGN KEY (work_id) REFERENCES work(id) ON DELETE CASCADE,
    CONSTRAINT FOREIGN KEY (dept_id) REFERENCES dept(id)
);

-- 일정
CREATE TABLE schedule (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    content VARCHAR(255),
    start_at DATETIME NOT NULL,
    end_at DATETIME NOT NULL,
    user_id BIGINT NOT NULL,
    dept_id BIGINT,
    is_repeat BOOLEAN NOT NULL DEFAULT FALSE,
    event_type VARCHAR(255) NOT NULL DEFAULT 'PERSONAL',
    CONSTRAINT FOREIGN KEY (user_id) REFERENCES user(id),
    CONSTRAINT FOREIGN KEY (dept_id) REFERENCES dept(id),
    CHECK (event_type IN ('DEPARTMENT', 'PERSONAL'))
);

-- 반복 규칙
CREATE TABLE repeat_rule (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    schedule_id BIGINT NOT NULL,
    frequency VARCHAR(255) NOT NULL,
    repeat_interval INT NOT NULL DEFAULT 1,
    end_date DATE,
    by_day VARCHAR(255),
    by_month_day INT,
    by_set_pos INT,
    CONSTRAINT FOREIGN KEY (schedule_id) REFERENCES schedule(id) ON DELETE CASCADE,
    CHECK (frequency IN ('DAILY', 'WEEKLY', 'MONTHLY'))
);

-- 회원 별 프로젝트 일정 표시
CREATE TABLE user_project_schedule (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    project_id BIGINT NOT NULL,
    is_visible BOOLEAN NOT NULL DEFAULT FALSE
);

-- 댓글
CREATE TABLE comment (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    content VARCHAR(255) NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    is_notice BOOLEAN NOT NULL DEFAULT FALSE,
    type VARCHAR(255) NOT NULL DEFAULT 'COMMENT',
    work_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    parent_comment_id BIGINT,
    CONSTRAINT FOREIGN KEY (work_id) REFERENCES work(id),
    CONSTRAINT FOREIGN KEY (user_id) REFERENCES user(id),
    CONSTRAINT FOREIGN KEY (parent_comment_id) REFERENCES comment(id),
    CHECK (type IN ('COMMENT', 'NOTICE'))
);

-- 결재
CREATE TABLE payment (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    type VARCHAR(255) NOT NULL DEFAULT 'GENERAL',
    status VARCHAR(255) NOT NULL DEFAULT 'PENDING',
    content TEXT NOT NULL,
    created_at DATETIME NOT NULL,
    approved_at DATETIME,
    work_id BIGINT,
    CONSTRAINT FOREIGN KEY (user_id) REFERENCES user(id),
    CONSTRAINT FOREIGN KEY (work_id) REFERENCES work(id) ,
    CHECK (type IN ('GENERAL', 'DELIVERABLE','DELAY')),
    CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED', 'CANCELLED'))
);

-- 결재 참여자
CREATE TABLE payment_participant (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    payment_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    role VARCHAR(255) NOT NULL,
    created_at DATETIME NOT NULL,
    CONSTRAINT FOREIGN KEY (payment_id) REFERENCES payment(id),
    CONSTRAINT FOREIGN KEY (user_id) REFERENCES user(id),
    CHECK (role IN ('APPROVER', 'VIEWER'))
);

-- 지연 사유
CREATE TABLE delay_reason (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    reason VARCHAR(255) NOT NULL
);

-- 지연 결재
CREATE TABLE delay_payment (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    delay_days INT NOT NULL,
    action_detail TEXT NOT NULL,
    delay_reason_id BIGINT NOT NULL,
    payment_id BIGINT NOT NULL,
    CONSTRAINT FOREIGN KEY (delay_reason_id) REFERENCES delay_reason(id),
    CONSTRAINT FOREIGN KEY (payment_id) REFERENCES payment(id)
);

-- 첨부파일
CREATE TABLE attachment (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    origin_name VARCHAR(255) NOT NULL,
    stored_name VARCHAR(255) NOT NULL,
    url VARCHAR(255) NOT NULL,
    file_type VARCHAR(255) NOT NULL,
    size VARCHAR(255) NOT NULL,
    upload_at DATETIME NOT NULL,
    target_type VARCHAR(255) NOT NULL,
    target_id BIGINT NOT NULL,
    uploader_id BIGINT NOT NULL,
    CONSTRAINT FOREIGN KEY (uploader_id) REFERENCES user(id),
    CHECK (target_type IN ('PAYMENT', 'COMMENT', 'PROJECT', 'TEMPLATE'))
);
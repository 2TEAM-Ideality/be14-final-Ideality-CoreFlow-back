DROP DATABASE IF EXISTS master;

CREATE DATABASE master;
USE master;

CREATE TABLE erp_master (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    company_code VARCHAR(10) UNIQUE NOT NULL,   -- 회사 코드
    company_name VARCHAR(100) NOT NULL,         -- 회사 명
    company_schema VARCHAR(100) NOT NULL        -- 매핑된 스키마명
);

INSERT INTO erp_master (company_code, company_name, company_schema)
VALUES
    ('master', 'erp_master', 'master'),
    ('aaa', 'A사', 'company_a');

-- 회원
CREATE TABLE user (
                      id BIGINT PRIMARY KEY AUTO_INCREMENT,
                      employee_num VARCHAR(255) NOT NULL UNIQUE,
                      password VARCHAR(255) NOT NULL,
                      name VARCHAR(255) NOT NULL,
                      email VARCHAR(255) NOT NULL UNIQUE,
                      birth DATE,
                      hire_date DATE,
                      is_resign BOOLEAN DEFAULT FALSE,
                      resign_date DATE,
                      profile_image MEDIUMTEXT,
                      dept_name VARCHAR(255),
                      job_rank_name VARCHAR(255),                -- 직위 명
                      job_role_name VARCHAR(255)   -- 직책 명
);

INSERT INTO user (id, employee_num, password, name, email)
VALUES ('1234', '1234', '$2a$12$dD0aS4kIMvZnWIWxVXr.3us3W97791wgVLi2gyY4kuCU6/KQMHrcG', 'master', 'test@master.com');

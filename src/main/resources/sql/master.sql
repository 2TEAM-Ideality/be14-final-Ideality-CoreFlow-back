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
VALUES ('aaa', 'A사', 'company_a');

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

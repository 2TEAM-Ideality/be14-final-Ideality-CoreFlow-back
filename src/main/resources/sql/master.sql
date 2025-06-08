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
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
                      profile_image MEDIUMTEXT,
                      dept_name VARCHAR(255) NOT NULL,
                      job_rank_name VARCHAR(255) NOT NULL,
                      job_role_name VARCHAR(255) NOT NULL DEFAULT '사원'
) ENGINE=INNODB CHARACTER SET utf8;

-- 직책
CREATE TABLE job_role (
                          id BIGINT PRIMARY KEY AUTO_INCREMENT,
                          name VARCHAR(255) NOT NULL UNIQUE
) ENGINE=INNODB CHARACTER SET utf8;

-- 직위
CREATE TABLE job_rank (
                          id BIGINT PRIMARY KEY AUTO_INCREMENT,
                          name VARCHAR(255) NOT NULL UNIQUE
) ENGINE=INNODB CHARACTER SET utf8;

-- 부서
CREATE TABLE dept (
                      id BIGINT PRIMARY KEY AUTO_INCREMENT,
                      name VARCHAR(255) NOT NULL UNIQUE,
                      dept_code VARCHAR(10) NOT NULL,
                      is_deleted BOOLEAN DEFAULT FALSE,
                      parent_dept_id BIGINT,
                      CONSTRAINT FOREIGN KEY (parent_dept_id) REFERENCES dept(id)
) ENGINE=INNODB CHARACTER SET utf8;

-- 역할
CREATE TABLE role (
                      id BIGINT PRIMARY KEY AUTO_INCREMENT,
                      name VARCHAR(255) NOT NULL UNIQUE,
                      type VARCHAR(255) NOT NULL,
                      CHECK ( type in ('PROJECT', 'GENERAL'))
) ENGINE=INNODB CHARACTER SET utf8;

-- 회원 별 역할
CREATE TABLE user_of_role (
                              id BIGINT PRIMARY KEY AUTO_INCREMENT,
                              user_id BIGINT NOT NULL,
                              role_id BIGINT NOT NULL,
                              CONSTRAINT FOREIGN KEY (user_id) REFERENCES user(id),
                              CONSTRAINT FOREIGN KEY (role_id) REFERENCES role(id) ON DELETE CASCADE
) ENGINE=INNODB CHARACTER SET utf8;

-- 휴일
CREATE TABLE holiday (
                         id BIGINT PRIMARY KEY AUTO_INCREMENT,
                         date DATE NOT NULL,
                         name VARCHAR(255) NOT NULL,
                         type VARCHAR(255) NOT NULL,
                         is_repeat VARCHAR(255) NOT NULL DEFAULT 'ONCE',
                         CHECK (type IN ('COMPANY', 'NATIONAL', 'SATURDAY', 'SUNDAY')),
                         CHECK (is_repeat IN ('YEARLY', 'ONCE'))
) ENGINE=INNODB CHARACTER SET utf8;

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
                              CHECK (target_type IN ('APPROVAL', 'PROJECT', 'WORK','SCHEDULE')),
                              CHECK (status IN ('PENDING', 'SENT', 'READ'))
) ENGINE=INNODB CHARACTER SET utf8;

-- 알림 수신자
CREATE TABLE notification_recipients (
                                         id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                         notification_id BIGINT NOT NULL,
                                         user_id BIGINT NOT NULL,
                                         CONSTRAINT FOREIGN KEY (notification_id) REFERENCES notification(id) ON DELETE CASCADE,
                                         CONSTRAINT FOREIGN KEY (user_id) REFERENCES user(id)
) ENGINE=INNODB CHARACTER SET utf8;

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
                          updated_by BIGINT,
                          is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
                          deleted_at DATETIME DEFAULT NULL,
                          CONSTRAINT FOREIGN KEY (created_by) REFERENCES user(id),
                          CONSTRAINT FOREIGN KEY (updated_by) REFERENCES user(id)
) ENGINE=INNODB CHARACTER SET utf8;

-- 템플릿별 참여 부서
CREATE TABLE template_dept(
                              id BIGINT PRIMARY KEY AUTO_INCREMENT,
                              template_id BIGINT NOT NULL,
                              dept_id BIGINT NOT NULL,
                              CONSTRAINT FOREIGN KEY (template_id) REFERENCES template(id) ON DELETE CASCADE,
                              CONSTRAINT FOREIGN KEY (dept_id) REFERENCES dept(id)
) ENGINE=INNODB CHARACTER SET utf8;

-- 참여 인원
CREATE TABLE participant (
                             id BIGINT PRIMARY KEY AUTO_INCREMENT,
                             target_type VARCHAR(255) NOT NULL,
                             target_id BIGINT NOT NULL,
                             user_id BIGINT NOT NULL,
                             role_id BIGINT NOT NULL,
                             CHECK ( target_type IN ('PROJECT', 'TASK', 'DETAILED'))
) ENGINE=INNODB CHARACTER SET utf8;

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
) ENGINE=INNODB CHARACTER SET utf8;

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
                      CONSTRAINT FOREIGN KEY (parent_task_id) REFERENCES work(id) ON DELETE CASCADE,
                      CONSTRAINT FOREIGN KEY (project_id) REFERENCES project(id) ON DELETE CASCADE,
                      CHECK (progress_rate BETWEEN 0 AND 100),
                      CHECK (passed_rate BETWEEN 0 AND 100),
                      CHECK (status IN ('PENDING', 'PROGRESS', 'COMPLETED', 'DELETED'))
) ENGINE=INNODB CHARACTER SET utf8;

-- 작업간 관계
CREATE TABLE relation (
                          id BIGINT PRIMARY KEY AUTO_INCREMENT,
                          prev_work_id BIGINT NOT NULL,
                          next_work_id BIGINT NOT NULL,
                          CONSTRAINT FOREIGN KEY (prev_work_id) REFERENCES work(id) ON DELETE CASCADE,
                          CONSTRAINT FOREIGN KEY (next_work_id) REFERENCES work(id) ON DELETE CASCADE
) ENGINE=INNODB CHARACTER SET utf8;

-- 작업별 참여 부서
CREATE TABLE work_dept (
                           id BIGINT PRIMARY KEY AUTO_INCREMENT,
                           work_id BIGINT NOT NULL,
                           dept_id BIGINT NOT NULL,
                           is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
                           CONSTRAINT FOREIGN KEY (work_id) REFERENCES work(id) ON DELETE CASCADE,
                           CONSTRAINT FOREIGN KEY (dept_id) REFERENCES dept(id)
) ENGINE=INNODB CHARACTER SET utf8;

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
                          is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
                          event_type VARCHAR(255) NOT NULL DEFAULT 'PERSONAL',
                          CONSTRAINT FOREIGN KEY (user_id) REFERENCES user(id),
                          CONSTRAINT FOREIGN KEY (dept_id) REFERENCES dept(id),
                          CHECK (event_type IN ('DEPARTMENT', 'PERSONAL'))
) ENGINE=INNODB CHARACTER SET utf8;

-- 반복 규칙
CREATE TABLE repeat_rule (
                             id BIGINT PRIMARY KEY AUTO_INCREMENT,
                             schedule_id BIGINT NOT NULL,
                             frequency VARCHAR(10) NOT NULL,
                             repeat_interval INT NOT NULL DEFAULT 1,
                             end_date DATE,
                             by_day VARCHAR(255),
                             by_month_day INT,
                             by_set_pos INT,
                             CONSTRAINT FOREIGN KEY (schedule_id) REFERENCES schedule(id) ON DELETE CASCADE,
                             CHECK (frequency IN ('DAILY', 'WEEKLY', 'MONTHLY'))
) ENGINE=INNODB CHARACTER SET utf8;

-- 회원 별 프로젝트 일정 표시
CREATE TABLE user_project_schedule (
                                       id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                       user_id BIGINT NOT NULL,
                                       project_id BIGINT NOT NULL,
                                       is_visible BOOLEAN NOT NULL DEFAULT FALSE
) ENGINE=INNODB CHARACTER SET utf8;

-- 댓글
CREATE TABLE comment (
                         id BIGINT PRIMARY KEY AUTO_INCREMENT,
                         content VARCHAR(255) NOT NULL,
                         is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
                         is_notice BOOLEAN NOT NULL DEFAULT FALSE,
                         is_modify BOOLEAN NOT NULL DEFAULT FALSE,
                         type VARCHAR(255) NOT NULL DEFAULT 'COMMENT',
                         created_at DATETIME NOT NULL,
                         work_id BIGINT NOT NULL,
                         user_id BIGINT NOT NULL,
                         parent_comment_id BIGINT,
                         CONSTRAINT FOREIGN KEY (work_id) REFERENCES work(id),
                         CONSTRAINT FOREIGN KEY (user_id) REFERENCES user(id),
                         CONSTRAINT FOREIGN KEY (parent_comment_id) REFERENCES comment(id),
                         CHECK (type IN ('COMMENT', 'NOTICE'))
) ENGINE=INNODB CHARACTER SET utf8;

-- 결재
CREATE TABLE approval (
                          id BIGINT PRIMARY KEY AUTO_INCREMENT,
                          user_id BIGINT NOT NULL,
                          title VARCHAR(255) NOT NULL,
                          type VARCHAR(255) NOT NULL DEFAULT 'GENERAL',
                          status VARCHAR(255) NOT NULL DEFAULT 'PENDING',
                          content TEXT NOT NULL,
                          created_at DATETIME NOT NULL,
                          approved_at DATETIME,
                          work_id BIGINT,
                          reject_reason TEXT,
                          CONSTRAINT FOREIGN KEY (user_id) REFERENCES user(id),
                          CONSTRAINT FOREIGN KEY (work_id) REFERENCES work(id),
                          CHECK (type IN ('GENERAL', 'DELIVERABLE','DELAY')),
                          CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED', 'CANCELLED'))
) ENGINE=INNODB CHARACTER SET utf8;

-- 결재 참여자
CREATE TABLE approval_participant (
                                      id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                      approval_id BIGINT NOT NULL,
                                      user_id BIGINT NOT NULL,
                                      role VARCHAR(255) NOT NULL,
                                      created_at DATETIME NOT NULL,
                                      CONSTRAINT FOREIGN KEY (approval_id) REFERENCES approval(id),
                                      CONSTRAINT FOREIGN KEY (user_id) REFERENCES user(id),
                                      CHECK (role IN ('APPROVER', 'VIEWER'))
) ENGINE=INNODB CHARACTER SET utf8;

-- 지연 사유
CREATE TABLE delay_reason (
                              id BIGINT PRIMARY KEY AUTO_INCREMENT,
                              reason VARCHAR(255) NOT NULL
) ENGINE=INNODB CHARACTER SET utf8;

-- 지연 결재
CREATE TABLE delay_approval (
                                id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                delay_days INT NOT NULL,
                                action_detail TEXT NOT NULL,
                                delay_reason_id BIGINT NOT NULL,
                                approval_id BIGINT NOT NULL,
                                CONSTRAINT FOREIGN KEY (delay_reason_id) REFERENCES delay_reason(id),
                                CONSTRAINT FOREIGN KEY (approval_id) REFERENCES approval(id)
) ENGINE=INNODB CHARACTER SET utf8;

-- 첨부파일
CREATE TABLE attachment (
                            id BIGINT PRIMARY KEY AUTO_INCREMENT,
                            origin_name VARCHAR(255) NOT NULL,
                            stored_name VARCHAR(255) NOT NULL,
                            url TEXT NOT NULL,
                            file_type VARCHAR(255) NOT NULL,
                            size VARCHAR(255) NOT NULL,
                            upload_at DATETIME NOT NULL,
                            target_type VARCHAR(255) NOT NULL,
                            target_id BIGINT NOT NULL,
                            uploader_id BIGINT NOT NULL,
                            is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
                            deleted_at DATETIME DEFAULT NULL,
                            task_id BIGINT NULL,
                            CONSTRAINT FOREIGN KEY (uploader_id) REFERENCES user(id),
                            CHECK (target_type IN ('APPROVAL', 'COMMENT', 'PROJECT', 'TEMPLATE'))
) ENGINE=INNODB CHARACTER SET utf8;

-- 기본 리스트 데이터 (생략 없이 그대로 사용)
INSERT INTO role (name, type) VALUES
                                  ('DIRECTOR', 'PROJECT'),
                                  ('TEAM_LEADER', 'PROJECT'),
                                  ('TEAM_MEMBER', 'PROJECT'),
                                  ('ADMIN', 'GENERAL'),
                                  ('VIEWER', 'PROJECT'),
                                  ('ASSIGNEE', 'PROJECT'),
                                  ('PARTICIPANT', 'PROJECT'),
                                  ('CREATOR', 'GENERAL'),
                                  ('PARTNER', 'GENERAL');

INSERT INTO job_rank (name) VALUES
                                ("사원"), ("대리"), ("과장"), ("차장"), ("부장"), ("협력업체");

INSERT INTO job_role (name) VALUES
                                ("팀원"), ("파트장"), ("본부장"), ("협력업체");

INSERT INTO dept (name, dept_code) VALUES
                                       ("기획팀", "PM"),
                                       ("디자인팀", "DES"),
                                       ("소싱팀", "MD"),
                                       ("생산팀", "MFG"),
                                       ("협력업체", "PART");

INSERT INTO delay_reason (reason) VALUES
                                      ('자재 공급 지연'),
                                      ('인력 부족'),
                                      ('설비 고장'),
                                      ('품질 이슈 재작업'),
                                      ('외주 업체 문제'),
                                      ('의사 결정 지연'),
                                      ('기타');

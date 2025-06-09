USE company_a;

INSERT INTO template
    (name, description, created_at, updated_at, duration, task_count, created_by, updated_by, is_deleted)
VALUES
-- 1. SS 블라우스 생산 공정
('SS 블라우스 생산 템플릿',
 '도식화부터 생산까지 여성 SS 시즌 블라우스의 생산 공정 템플릿입니다.',
 NOW(), NOW(), 30, 8, 1, 1, FALSE),

-- 2. FW 아우터 개발 템플릿
('FW 아우터 개발 템플릿',
 'FW 시즌 남성 아우터 개발을 위한 공정 템플릿입니다. 초기 기획부터 생산 납품까지 포함됩니다.',
 NOW(), NOW(), 45, 10, 1, 1, FALSE),

-- 3. 패턴 개발 및 샘플 템플릿
('패턴/샘플 개발 템플릿',
 '신규 의류 라인의 패턴 설계 및 샘플 제작을 위한 프로세스 템플릿입니다.',
 NOW(), NOW(), 14, 5, 2, 2, FALSE),

-- 4. OEM 협력 공장 발주 템플릿
('OEM 발주 및 협력 템플릿',
 '외부 공장과 협업을 위한 OEM 발주 프로세스 템플릿입니다. 발주, 납기 확인, 품질 검수 등을 포함합니다.',
 NOW(), NOW(), 20, 6, 3, 3, FALSE),

-- 5. 생산 품질검수 및 납품 템플릿
('생산 품질검수 템플릿',
 '대량 생산 이후 품질 검수 및 최종 납품 단계의 공정을 관리하는 템플릿입니다.',
 NOW(), NOW(), 10, 4, 1, 1, FALSE);


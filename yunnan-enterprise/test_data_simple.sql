-- ============================================
-- 简化版测试数据（test_ent_002）
-- 解决字符集冲突，快速测试
-- ============================================

USE project1;

-- 步骤1：先统一字符集（重要！）
SET FOREIGN_KEY_CHECKS = 0;
ALTER TABLE account_info CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE enterprise_info CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE period_info CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE report_info CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE enterprise_report_info CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE report_audit_history CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
SET FOREIGN_KEY_CHECKS = 1;

-- 步骤2：清空旧测试数据
DELETE FROM report_audit_history WHERE enterprise_id LIKE 'TEST%';
DELETE FROM enterprise_report_info WHERE enterprise_id LIKE 'TEST%';
DELETE FROM report_info WHERE report_id LIKE 'ENT%' OR report_id LIKE 'report_%';
DELETE FROM account_info WHERE username LIKE 'test%' OR username LIKE 'city%' OR username LIKE 'province%';
DELETE FROM enterprise_info WHERE enterprise_id LIKE 'TEST%';
DELETE FROM period_info;

-- 步骤3：插入基础数据

-- 3.1 企业信息
INSERT INTO enterprise_info (enterprise_id, org_code, name, region, nature, industry, industry_desc, contact_name, address, postal_code, phone_num, fax_num, email, status, created_at, updated_at) VALUES 
('TEST_ENT_001', '91530100MA6K1234XX', '云南测试科技有限公司', 5301, 1, 1, '软件开发', '张三', '昆明市', '650000', '0871-12345678', '0871-87654321', 'test001@example.com', 3, '2024-01-01 10:00:00', '2024-01-01 10:00:00'),
('TEST_ENT_002', '91530300MA6K5678XX', '曲靖测试制造有限公司', 5303, 2, 2, '机械制造', '李四', '曲靖市', '655000', '0874-12345678', '0874-87654321', 'test002@example.com', 3, '2024-02-01 10:00:00', '2024-02-01 10:00:00');

-- 3.2 账号信息
INSERT INTO account_info (username, password, type, enterprise_id, city_code, last_login_time, status, created_at) VALUES 
('test_ent_001', 'password123', 1, 'TEST_ENT_001', NULL, NULL, 0, '2024-01-01 10:00:00'),
('test_ent_002', 'password123', 1, 'TEST_ENT_002', NULL, NULL, 0, '2024-02-01 10:00:00'),
('city_km_auditor', 'city123', 2, NULL, 5301, NULL, 0, '2024-01-01 09:00:00'),
('city_qj_auditor', 'city123', 2, NULL, 5303, NULL, 0, '2024-01-01 09:00:00'),
('province_auditor', 'province123', 3, NULL, NULL, NULL, 0, '2024-01-01 08:00:00');

-- 3.3 调查期信息
INSERT INTO period_info (investigate_time, period_start_time, period_end_time, enterprise_count, created_at, updated_at) VALUES 
('2025-01', '2025-01-10 08:00:00', '2025-02-28 23:59:59', 200, '2024-12-20 10:00:00', '2024-12-20 10:00:00'),
('2024-12', '2024-12-10 08:00:00', '2024-12-25 23:59:59', 195, '2024-11-20 10:00:00', '2024-11-20 10:00:00'),
('2024-11', '2024-11-10 08:00:00', '2024-11-25 23:59:59', 190, '2024-10-20 10:00:00', '2024-10-20 10:00:00');

-- 获取 period_id
SET @p1 = (SELECT period_id FROM period_info WHERE investigate_time = '2025-01');
SET @p2 = (SELECT period_id FROM period_info WHERE investigate_time = '2024-12');
SET @p3 = (SELECT period_id FROM period_info WHERE investigate_time = '2024-11');

-- 步骤4：test_ent_002 的报表数据

-- 场景1：2025-01 审核通过（人数无变化）
INSERT INTO report_info (report_id, construction_count, investigation_count, reduction_type, reason1, reason1_desc, reason2, reason2_desc, reason3, reason3_desc, other_reason) VALUES 
('ENT002_2025_01_V1', 80, 80, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

INSERT INTO enterprise_report_info (enterprise_id, period_id, report_id, old_report_id, reason_return, status, period_start_time, period_end_time, created_at, updated_at, enterprise_nature, enterprise_industry, enterprise_region) VALUES 
('TEST_ENT_002', @p1, 'ENT002_2025_01_V1', NULL, NULL, 3, '2025-01-10 08:00:00', '2025-02-28 23:59:59', '2025-01-13 11:00:00', '2025-01-18 16:00:00', 2, 2, 5303);

INSERT INTO report_audit_history (enterprise_id, period_id, report_id, audit_level, auditor, audit_result, audit_opinion, audit_time) VALUES 
('TEST_ENT_002', @p1, 'ENT002_2025_01_V1', 1, 'city_qj_auditor', 1, '数据填写完整，人数无变化，符合规范，同意通过。', '2025-01-16 10:00:00'),
('TEST_ENT_002', @p1, 'ENT002_2025_01_V1', 2, 'province_auditor', 1, '数据准确无误，审核通过。', '2025-01-18 16:00:00');

-- 场景2：2024-12 经历驳回后通过
-- V1 被驳回
INSERT INTO report_info (report_id, construction_count, investigation_count, reduction_type, reason1, reason1_desc, reason2, reason2_desc, reason3, reason3_desc, other_reason) VALUES 
('ENT002_2024_12_V1', 85, 78, 5, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

INSERT INTO enterprise_report_info (enterprise_id, period_id, report_id, old_report_id, reason_return, status, period_start_time, period_end_time, created_at, updated_at, enterprise_nature, enterprise_industry, enterprise_region) VALUES 
('TEST_ENT_002', @p2, 'ENT002_2024_12_V1', NULL, '减员原因未填写', 5, '2024-12-10 08:00:00', '2024-12-25 23:59:59', '2024-12-11 14:00:00', '2024-12-13 15:30:00', 2, 2, 5303);

INSERT INTO report_audit_history (enterprise_id, period_id, report_id, audit_level, auditor, audit_result, audit_opinion, audit_time) VALUES 
('TEST_ENT_002', @p2, 'ENT002_2024_12_V1', 1, 'city_qj_auditor', 2, '调查期人数小于建档期人数，存在7人减员，但未填写减员原因。请补充减员类型和减员原因说明后重新提交。', '2024-12-13 15:30:00');

-- V2 修改后通过
INSERT INTO report_info (report_id, construction_count, investigation_count, reduction_type, reason1, reason1_desc, reason2, reason2_desc, reason3, reason3_desc, other_reason) VALUES 
('ENT002_2024_12_V2', 85, 78, 5, 9, '正常退休2人：王某（2024-11-30退休）、李某（2024-12-15退休）', 6, '合同到期未续签5人', NULL, NULL, NULL);

INSERT INTO enterprise_report_info (enterprise_id, period_id, report_id, old_report_id, reason_return, status, period_start_time, period_end_time, created_at, updated_at, enterprise_nature, enterprise_industry, enterprise_region) VALUES 
('TEST_ENT_002', @p2, 'ENT002_2024_12_V2', 'ENT002_2024_12_V1', NULL, 3, '2024-12-10 08:00:00', '2024-12-25 23:59:59', '2024-12-14 09:00:00', '2024-12-18 11:00:00', 2, 2, 5303);

INSERT INTO report_audit_history (enterprise_id, period_id, report_id, audit_level, auditor, audit_result, audit_opinion, audit_time) VALUES 
('TEST_ENT_002', @p2, 'ENT002_2024_12_V2', 1, 'city_qj_auditor', 1, '已补充减员原因，数据完整，符合规范，同意通过。', '2024-12-16 14:00:00'),
('TEST_ENT_002', @p2, 'ENT002_2024_12_V2', 2, 'province_auditor', 1, '数据核实无误，减员原因说明详细，审核通过。', '2024-12-18 11:00:00');

-- 场景3：2024-11 省级驳回后通过
-- V1 被省级驳回
INSERT INTO report_info (report_id, construction_count, investigation_count, reduction_type, reason1, reason1_desc, reason2, reason2_desc, reason3, reason3_desc, other_reason) VALUES 
('ENT002_2024_11_V1', 90, 85, 5, 9, '正常退休5人', NULL, NULL, NULL, NULL, NULL);

INSERT INTO enterprise_report_info (enterprise_id, period_id, report_id, old_report_id, reason_return, status, period_start_time, period_end_time, created_at, updated_at, enterprise_nature, enterprise_industry, enterprise_region) VALUES 
('TEST_ENT_002', @p3, 'ENT002_2024_11_V1', NULL, '说明过于简略', 5, '2024-11-10 08:00:00', '2024-11-25 23:59:59', '2024-11-12 10:00:00', '2024-11-18 14:00:00', 2, 2, 5303);

INSERT INTO report_audit_history (enterprise_id, period_id, report_id, audit_level, auditor, audit_result, audit_opinion, audit_time) VALUES 
('TEST_ENT_002', @p3, 'ENT002_2024_11_V1', 1, 'city_qj_auditor', 1, '数据完整，同意提交省级审核。', '2024-11-14 10:00:00'),
('TEST_ENT_002', @p3, 'ENT002_2024_11_V1', 2, 'province_auditor', 2, '减员原因说明过于简略，请详细说明：1.人员姓名；2.退休日期；3.原岗位等信息。', '2024-11-18 14:00:00');

-- V2 补充详细说明后通过
INSERT INTO report_info (report_id, construction_count, investigation_count, reduction_type, reason1, reason1_desc, reason2, reason2_desc, reason3, reason3_desc, other_reason) VALUES 
('ENT002_2024_11_V2', 90, 85, 5, 9, '正常退休5人，详细信息：1.王某，男，65岁，2024-10-31退休，原岗位生产部主管；2.李某，女，60岁，2024-11-15退休，原岗位质检员；3.张某，男，65岁，2024-11-20退休，原岗位设备维修工；4.赵某，女，60岁，2024-11-22退休，原岗位行政专员；5.刘某，男，65岁，2024-11-25退休，原岗位仓库管理员。', NULL, NULL, NULL, NULL, NULL);

INSERT INTO enterprise_report_info (enterprise_id, period_id, report_id, old_report_id, reason_return, status, period_start_time, period_end_time, created_at, updated_at, enterprise_nature, enterprise_industry, enterprise_region) VALUES 
('TEST_ENT_002', @p3, 'ENT002_2024_11_V2', 'ENT002_2024_11_V1', NULL, 3, '2024-11-10 08:00:00', '2024-11-25 23:59:59', '2024-11-19 09:30:00', '2024-11-22 15:00:00', 2, 2, 5303);

INSERT INTO report_audit_history (enterprise_id, period_id, report_id, audit_level, auditor, audit_result, audit_opinion, audit_time) VALUES 
('TEST_ENT_002', @p3, 'ENT002_2024_11_V2', 1, 'city_qj_auditor', 1, '已按省级要求补充详细说明，数据完整，同意通过。', '2024-11-20 11:00:00'),
('TEST_ENT_002', @p3, 'ENT002_2024_11_V2', 2, 'province_auditor', 1, '补充说明详细完整，审核通过。', '2024-11-22 15:00:00');

-- 验证数据
SELECT '=== 调查期列表 ===' as '';
SELECT period_id, investigate_time, period_start_time, period_end_time FROM period_info;

SELECT '=== 企业B的报表列表 ===' as '';
SELECT 
  er.report_id,
  p.investigate_time,
  er.status,
  er.old_report_id,
  r.construction_count,
  r.investigation_count,
  er.updated_at
FROM enterprise_report_info er
JOIN period_info p ON er.period_id = p.period_id
LEFT JOIN report_info r ON er.report_id = r.report_id
WHERE er.enterprise_id = 'TEST_ENT_002'
ORDER BY p.investigate_time DESC, er.created_at ASC;

SELECT '=== 审核历史记录 ===' as '';
SELECT 
  ah.enterprise_id,
  p.investigate_time,
  ah.report_id,
  ah.audit_level,
  ah.audit_result,
  ah.audit_time
FROM report_audit_history ah
JOIN period_info p ON ah.period_id = p.period_id
WHERE ah.enterprise_id = 'TEST_ENT_002'
ORDER BY p.investigate_time DESC, ah.audit_time ASC;

SELECT '✅ 测试数据导入完成！' as result;


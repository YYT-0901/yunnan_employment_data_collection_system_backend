-- ============================================
-- 企业B (test_ent_002) 完整测试数据
-- 用途：测试多种报表状态和审核流程
-- ============================================

USE project1;

-- 查询实际的 period_id（重要！）
SELECT period_id, investigate_time FROM period_info WHERE investigate_time = '2025-01';
-- 假设 2025-01 的 period_id = 1，如果不是请在下面的SQL中替换

SET @period_id_2025_01 = 1;  -- ⚠️ 根据上面查询结果修改
SET @ent_id = 'TEST_ENT_002';

-- ============================================
-- 场景1：2025-01月 - 完整的审核流程（通过）
-- ============================================

-- 报表数据（人数无变化，顺利通过）
INSERT INTO report_info (
  report_id, construction_count, investigation_count, 
  reduction_type, reason1, reason1_desc, 
  reason2, reason2_desc, reason3, reason3_desc, other_reason
) VALUES 
('ENT002_2025_01_V1', 80, 80, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

-- 企业上报信息（已审核通过）
INSERT INTO enterprise_report_info (
  enterprise_id, period_id, report_id, old_report_id,
  reason_return, status, 
  period_start_time, period_end_time,
  created_at, updated_at,
  enterprise_nature, enterprise_industry, enterprise_region
) VALUES (
  @ent_id, @period_id_2025_01, 'ENT002_2025_01_V1', NULL,
  NULL, 3,  -- status=3 审核通过
  '2025-01-10 08:00:00', '2025-01-31 23:59:59',
  '2025-01-13 11:00:00', '2025-01-18 16:00:00',
  2, 2, 5303
);

-- 市级审核历史（通过）
INSERT INTO report_audit_history (
  enterprise_id, period_id, report_id,
  audit_level, auditor, audit_result, audit_opinion, audit_time
) VALUES (
  @ent_id, @period_id_2025_01, 'ENT002_2025_01_V1',
  1, 'city_qj_auditor', 1, 
  '数据填写完整，符合规范，同意通过。人数无变化，无需填写减员信息，符合要求。', 
  '2025-01-16 10:00:00'
);

-- 省级审核历史（通过）
INSERT INTO report_audit_history (
  enterprise_id, period_id, report_id,
  audit_level, auditor, audit_result, audit_opinion, audit_time
) VALUES (
  @ent_id, @period_id_2025_01, 'ENT002_2025_01_V1',
  2, 'province_auditor', 1, 
  '数据准确无误，审核通过。', 
  '2025-01-18 16:00:00'
);

-- ============================================
-- 场景2：2024-12月 - 有减员，经历一次驳回后通过
-- ============================================

-- 先查询 2024-12 的 period_id
SELECT period_id FROM period_info WHERE investigate_time = '2024-12';
SET @period_id_2024_12 = 2;  -- ⚠️ 根据查询结果修改

-- 第一版报表（数据不完整，被市级驳回）
INSERT INTO report_info (
  report_id, construction_count, investigation_count, 
  reduction_type, reason1, reason1_desc, 
  reason2, reason2_desc, reason3, reason3_desc, other_reason
) VALUES 
('ENT002_2024_12_V1', 85, 78, 5, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

INSERT INTO enterprise_report_info (
  enterprise_id, period_id, report_id, old_report_id,
  reason_return, status, 
  period_start_time, period_end_time,
  created_at, updated_at,
  enterprise_nature, enterprise_industry, enterprise_region
) VALUES (
  @ent_id, @period_id_2024_12, 'ENT002_2024_12_V1', NULL,
  '减员原因未填写', 5,  -- status=5 驳回
  '2024-12-10 08:00:00', '2024-12-25 23:59:59',
  '2024-12-11 14:00:00', '2024-12-13 15:30:00',
  2, 2, 5303
);

-- 市级驳回记录
INSERT INTO report_audit_history (
  enterprise_id, period_id, report_id,
  audit_level, auditor, audit_result, audit_opinion, audit_time
) VALUES (
  @ent_id, @period_id_2024_12, 'ENT002_2024_12_V1',
  1, 'city_qj_auditor', 2, 
  '调查期人数小于建档期人数，存在7人减员，但未填写减员原因。请补充减员类型和减员原因说明后重新提交。', 
  '2024-12-13 15:30:00'
);

-- 第二版报表（补充完整后提交）
INSERT INTO report_info (
  report_id, construction_count, investigation_count, 
  reduction_type, reason1, reason1_desc, 
  reason2, reason2_desc, reason3, reason3_desc, other_reason
) VALUES 
('ENT002_2024_12_V2', 85, 78, 5, 9, '正常退休2人：王某（2024-11-30退休）、李某（2024-12-15退休）', 6, '合同到期未续签5人，包括临时工和合同工', NULL, NULL, NULL);

INSERT INTO enterprise_report_info (
  enterprise_id, period_id, report_id, old_report_id,
  reason_return, status, 
  period_start_time, period_end_time,
  created_at, updated_at,
  enterprise_nature, enterprise_industry, enterprise_region
) VALUES (
  @ent_id, @period_id_2024_12, 'ENT002_2024_12_V2', 'ENT002_2024_12_V1',
  NULL, 3,  -- status=3 审核通过
  '2024-12-10 08:00:00', '2024-12-25 23:59:59',
  '2024-12-14 09:00:00', '2024-12-18 11:00:00',
  2, 2, 5303
);

-- 市级审核（第二版通过）
INSERT INTO report_audit_history (
  enterprise_id, period_id, report_id,
  audit_level, auditor, audit_result, audit_opinion, audit_time
) VALUES (
  @ent_id, @period_id_2024_12, 'ENT002_2024_12_V2',
  1, 'city_qj_auditor', 1, 
  '已补充减员原因，数据完整，符合规范，同意通过。', 
  '2024-12-16 14:00:00'
);

-- 省级审核（通过）
INSERT INTO report_audit_history (
  enterprise_id, period_id, report_id,
  audit_level, auditor, audit_result, audit_opinion, audit_time
) VALUES (
  @ent_id, @period_id_2024_12, 'ENT002_2024_12_V2',
  2, 'province_auditor', 1, 
  '数据核实无误，减员原因说明详细，审核通过。', 
  '2024-12-18 11:00:00'
);

-- ============================================
-- 场景3：2025-02月 - 暂存状态（未提交）
-- ============================================

-- 查询 2025-02 的 period_id
SELECT period_id FROM period_info WHERE investigate_time = '2025-02';
SET @period_id_2025_02 = 3;  -- ⚠️ 根据查询结果修改

-- 报表数据（暂存状态，数据不完整）
INSERT INTO report_info (
  report_id, construction_count, investigation_count, 
  reduction_type, reason1, reason1_desc, 
  reason2, reason2_desc, reason3, reason3_desc, other_reason
) VALUES 
('ENT002_2025_02_V1', 80, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

INSERT INTO enterprise_report_info (
  enterprise_id, period_id, report_id, old_report_id,
  reason_return, status, 
  period_start_time, period_end_time,
  created_at, updated_at,
  enterprise_nature, enterprise_industry, enterprise_region
) VALUES (
  @ent_id, @period_id_2025_02, 'ENT002_2025_02_V1', NULL,
  NULL, 0,  -- status=0 已暂存
  '2025-02-10 08:00:00', '2025-02-28 23:59:59',
  '2025-01-25 16:00:00', '2025-01-25 16:00:00',
  2, 2, 5303
);

-- ============================================
-- 场景4：增加一个被省级驳回的案例
-- ============================================

-- 报表v1（市级通过）
INSERT INTO report_info (
  report_id, construction_count, investigation_count, 
  reduction_type, reason1, reason1_desc, 
  reason2, reason2_desc, reason3, reason3_desc, other_reason
) VALUES 
('ENT002_2024_11_V1', 90, 85, 5, 9, '正常退休5人', NULL, NULL, NULL, NULL, NULL);

-- 查询 2024-11 的 period_id（如果没有就不插入）
-- 假设我们为 2024-11 创建一个调查期
INSERT INTO period_info (investigate_time, period_start_time, period_end_time, enterprise_count)
VALUES ('2024-11', '2024-11-10 08:00:00', '2024-11-25 23:59:59', 190)
ON DUPLICATE KEY UPDATE investigate_time = investigate_time;

SELECT period_id FROM period_info WHERE investigate_time = '2024-11';
SET @period_id_2024_11 = (SELECT period_id FROM period_info WHERE investigate_time = '2024-11');

INSERT INTO enterprise_report_info (
  enterprise_id, period_id, report_id, old_report_id,
  reason_return, status, 
  period_start_time, period_end_time,
  created_at, updated_at,
  enterprise_nature, enterprise_industry, enterprise_region
) VALUES (
  @ent_id, @period_id_2024_11, 'ENT002_2024_11_V1', NULL,
  '减员原因说明过于简略', 5,  -- status=5 省级驳回
  '2024-11-10 08:00:00', '2024-11-25 23:59:59',
  '2024-11-12 10:00:00', '2024-11-18 14:00:00',
  2, 2, 5303
);

-- 市级审核通过
INSERT INTO report_audit_history (
  enterprise_id, period_id, report_id,
  audit_level, auditor, audit_result, audit_opinion, audit_time
) VALUES (
  @ent_id, @period_id_2024_11, 'ENT002_2024_11_V1',
  1, 'city_qj_auditor', 1, 
  '数据完整，同意提交省级审核。', 
  '2024-11-14 10:00:00'
);

-- 省级驳回
INSERT INTO report_audit_history (
  enterprise_id, period_id, report_id,
  audit_level, auditor, audit_result, audit_opinion, audit_time
) VALUES (
  @ent_id, @period_id_2024_11, 'ENT002_2024_11_V1',
  2, 'province_auditor', 2, 
  '减员原因说明过于简略，仅写"正常退休5人"不符合要求。请详细说明：1.具体人员姓名；2.退休日期；3.原岗位；4.是否符合法定退休年龄等信息。', 
  '2024-11-18 14:00:00'
);

-- 第二版报表（补充详细说明）
INSERT INTO report_info (
  report_id, construction_count, investigation_count, 
  reduction_type, reason1, reason1_desc, 
  reason2, reason2_desc, reason3, reason3_desc, other_reason
) VALUES 
('ENT002_2024_11_V2', 90, 85, 5, 9, '正常退休5人，详细信息如下：1.王某，男，65岁，2024-10-31退休，原岗位：生产部主管，达到法定退休年龄；2.李某，女，60岁，2024-11-15退休，原岗位：质检员，达到法定退休年龄；3.张某，男，65岁，2024-11-20退休，原岗位：设备维修工；4.赵某，女，60岁，2024-11-22退休，原岗位：行政专员；5.刘某，男，65岁，2024-11-25退休，原岗位：仓库管理员。以上人员均为正常退休，已按规定办理退休手续。', NULL, NULL, NULL, NULL, NULL);

INSERT INTO enterprise_report_info (
  enterprise_id, period_id, report_id, old_report_id,
  reason_return, status, 
  period_start_time, period_end_time,
  created_at, updated_at,
  enterprise_nature, enterprise_industry, enterprise_region
) VALUES (
  @ent_id, @period_id_2024_11, 'ENT002_2024_11_V2', 'ENT002_2024_11_V1',
  NULL, 3,  -- status=3 审核通过
  '2024-11-10 08:00:00', '2024-11-25 23:59:59',
  '2024-11-19 09:30:00', '2024-11-22 15:00:00',
  2, 2, 5303
);

-- 市级审核（第二版通过）
INSERT INTO report_audit_history (
  enterprise_id, period_id, report_id,
  audit_level, auditor, audit_result, audit_opinion, audit_time
) VALUES (
  @ent_id, @period_id_2024_11, 'ENT002_2024_11_V2',
  1, 'city_qj_auditor', 1, 
  '已按省级要求补充详细说明，数据完整，同意通过。', 
  '2024-11-20 11:00:00'
);

-- 省级审核（第二版通过）
INSERT INTO report_audit_history (
  enterprise_id, period_id, report_id,
  audit_level, auditor, audit_result, audit_opinion, audit_time
) VALUES (
  @ent_id, @period_id_2024_11, 'ENT002_2024_11_V2',
  2, 'province_auditor', 1, 
  '补充说明详细完整，包含人员姓名、年龄、岗位、退休日期等必要信息，符合规范要求，审核通过。', 
  '2024-11-22 15:00:00'
);

-- ============================================
-- 场景5：2024-10月 - 大规模裁员（多种原因）
-- ============================================

INSERT INTO period_info (investigate_time, period_start_time, period_end_time, enterprise_count)
VALUES ('2024-10', '2024-10-10 08:00:00', '2024-10-25 23:59:59', 185)
ON DUPLICATE KEY UPDATE investigate_time = investigate_time;

SET @period_id_2024_10 = (SELECT period_id FROM period_info WHERE investigate_time = '2024-10');

INSERT INTO report_info (
  report_id, construction_count, investigation_count, 
  reduction_type, reason1, reason1_desc, 
  reason2, reason2_desc, reason3, reason3_desc, other_reason
) VALUES 
('ENT002_2024_10_V1', 100, 70, 4, 6, '订单不足，市场需求下滑导致产能闲置，部分生产线停产，涉及15人', 7, '原材料涨价挤压利润，企业缩减岗位，涉及10人', 9, '自然减员5人（退休和离职）', NULL);

INSERT INTO enterprise_report_info (
  enterprise_id, period_id, report_id, old_report_id,
  reason_return, status, 
  period_start_time, period_end_time,
  created_at, updated_at,
  enterprise_nature, enterprise_industry, enterprise_region
) VALUES (
  @ent_id, @period_id_2024_10, 'ENT002_2024_10_V1', NULL,
  NULL, 4,  -- status=4 已归档
  '2024-10-10 08:00:00', '2024-10-25 23:59:59',
  '2024-10-12 15:00:00', '2024-10-26 10:00:00',
  2, 2, 5303
);

-- 市级审核通过
INSERT INTO report_audit_history (
  enterprise_id, period_id, report_id,
  audit_level, auditor, audit_result, audit_opinion, audit_time
) VALUES (
  @ent_id, @period_id_2024_10, 'ENT002_2024_10_V1',
  1, 'city_qj_auditor', 1, 
  '数据完整，减员原因说明详细，符合实际情况，同意通过。', 
  '2024-10-15 14:00:00'
);

-- 省级审核通过
INSERT INTO report_audit_history (
  enterprise_id, period_id, report_id,
  audit_level, auditor, audit_result, audit_opinion, audit_time
) VALUES (
  @ent_id, @period_id_2024_10, 'ENT002_2024_10_V1',
  2, 'province_auditor', 1, 
  '大规模裁员原因说明清晰，符合当前经济形势，数据真实可信，审核通过。', 
  '2024-10-20 16:00:00'
);

-- ============================================
-- 验证数据
-- ============================================

SELECT '=== 企业B的所有报表 ===' as '';
SELECT 
  er.report_id,
  p.investigate_time,
  er.status,
  CASE 
    WHEN er.status = -1 THEN '未填报'
    WHEN er.status = 0 THEN '已暂存'
    WHEN er.status = 1 THEN '待市级审核'
    WHEN er.status = 2 THEN '待省级审核'
    WHEN er.status = 3 THEN '审核通过'
    WHEN er.status = 4 THEN '已归档'
    WHEN er.status = 5 THEN '驳回'
  END as status_name,
  r.construction_count,
  r.investigation_count,
  er.updated_at
FROM enterprise_report_info er
JOIN period_info p ON er.period_id = p.period_id
LEFT JOIN report_info r ON er.report_id = r.report_id
WHERE er.enterprise_id = @ent_id
ORDER BY p.investigate_time DESC, er.created_at ASC;

SELECT '=== 企业B的审核历史统计 ===' as '';
SELECT 
  p.investigate_time,
  ah.report_id,
  COUNT(*) as audit_count,
  SUM(CASE WHEN ah.audit_result = 1 THEN 1 ELSE 0 END) as pass_count,
  SUM(CASE WHEN ah.audit_result = 2 THEN 1 ELSE 0 END) as reject_count
FROM report_audit_history ah
JOIN period_info p ON ah.period_id = p.period_id
WHERE ah.enterprise_id = @ent_id
GROUP BY p.investigate_time, ah.report_id
ORDER BY p.investigate_time DESC;

SELECT '=== 企业B 2024-11 的完整审核历史（经历驳回）===' as '';
SELECT 
  ah.report_id,
  CASE 
    WHEN ah.audit_level = 1 THEN '市级审核'
    WHEN ah.audit_level = 2 THEN '省级审核'
  END as audit_level_name,
  ah.auditor,
  CASE 
    WHEN ah.audit_result = 1 THEN '通过'
    WHEN ah.audit_result = 2 THEN '驳回'
  END as audit_result_name,
  ah.audit_opinion,
  ah.audit_time
FROM report_audit_history ah
WHERE ah.enterprise_id = @ent_id
  AND ah.period_id = @period_id_2024_11
ORDER BY ah.audit_time ASC;


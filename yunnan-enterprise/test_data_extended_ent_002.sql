-- ============================================
-- 扩展的测试数据：test_ent_002
-- 目的：测试各种报表状态和数据锁定逻辑
-- ============================================

USE project1;

SET @ent_id = 'TEST_ENT_002';

-- ============================================
-- 场景1：2025-01月 - 审核通过，人数无变化
-- ============================================

-- 查询 2025-01 的 period_id
SET @period_id_2025_01 = (SELECT period_id FROM period_info WHERE investigate_time = '2025-01');

-- 报表数据（人数无变化）
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

SET @period_id_2024_12 = (SELECT period_id FROM period_info WHERE investigate_time = '2024-12');

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

SET @period_id_2025_02 = (SELECT period_id FROM period_info WHERE investigate_time = '2025-02');

-- 报表数据（暂存状态，数据不完整）
INSERT INTO report_info (
  report_id, construction_count, investigation_count, 
  reduction_type, reason1, reason1_desc, 
  reason2, reason2_desc, reason3, reason3_desc, other_reason
) VALUES 
('ENT002_2025_02_V1', 80, 75, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

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
-- 场景4：2024-11月 - 省级驳回后补充详细说明通过
-- ============================================

-- 创建2024-11的调查期
INSERT INTO period_info (investigate_time, period_start_time, period_end_time, enterprise_count)
VALUES ('2024-11', '2024-11-10 08:00:00', '2024-11-25 23:59:59', 190)
ON DUPLICATE KEY UPDATE investigate_time = investigate_time;

SET @period_id_2024_11 = (SELECT period_id FROM period_info WHERE investigate_time = '2024-11');

-- 第一版报表（省级驳回）
INSERT INTO report_info (
  report_id, construction_count, investigation_count, 
  reduction_type, reason1, reason1_desc, 
  reason2, reason2_desc, reason3, reason3_desc, other_reason
) VALUES 
('ENT002_2024_11_V1', 90, 85, 5, 9, '正常退休5人', NULL, NULL, NULL, NULL, NULL);

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
-- 场景6：2025-03月 - 多种减员原因（待市级审核）
-- ============================================

INSERT INTO period_info (investigate_time, period_start_time, period_end_time, enterprise_count)
VALUES ('2025-03', '2025-03-01 00:00:00', '2025-03-31 23:59:59', 200)
ON DUPLICATE KEY UPDATE investigate_time = investigate_time;

SET @period_id_2025_03 = (SELECT period_id FROM period_info WHERE investigate_time = '2025-03');

INSERT INTO report_info (
  report_id, construction_count, investigation_count, 
  reduction_type, reason1, reason1_desc, 
  reason2, reason2_desc, reason3, reason3_desc, other_reason
) VALUES 
('ENT002_2025_03_V1', 80, 72, 3, 6, '合同到期：5人合同到期后未续签，原因是企业订单减少，岗位需求降低', 9, '正常退休：2人到达法定退休年龄，办理退休手续', 7, '成本上涨：1人因薪资调整压力主动离职', NULL);

INSERT INTO enterprise_report_info (
  enterprise_id, period_id, report_id, old_report_id,
  reason_return, status, 
  period_start_time, period_end_time,
  created_at, updated_at,
  enterprise_nature, enterprise_industry, enterprise_region
) VALUES (
  @ent_id, @period_id_2025_03, 'ENT002_2025_03_V1', NULL,
  NULL, 1,  -- status=1 待市级审核
  '2025-03-01 00:00:00', '2025-03-31 23:59:59',
  '2025-03-05 14:00:00', '2025-03-05 14:00:00',
  2, 2, 5303
);

-- ============================================
-- 场景7：2024-09月 - "其他"减员类型
-- ============================================

INSERT INTO period_info (investigate_time, period_start_time, period_end_time, enterprise_count)
VALUES ('2024-09', '2024-09-10 08:00:00', '2024-09-25 23:59:59', 180)
ON DUPLICATE KEY UPDATE investigate_time = investigate_time;

SET @period_id_2024_09 = (SELECT period_id FROM period_info WHERE investigate_time = '2024-09');

INSERT INTO report_info (
  report_id, construction_count, investigation_count, 
  reduction_type, reason1, reason1_desc, 
  reason2, reason2_desc, reason3, reason3_desc, other_reason
) VALUES 
('ENT002_2024_09_V1', 95, 90, 8, 14, '其他原因：因个人原因主动离职2人，包括家庭搬迁、个人发展等', 9, '正常退休：3人退休', NULL, NULL, '企业部分岗位调整，员工选择内部转岗而非离职，实际减员主要为自然减员');

INSERT INTO enterprise_report_info (
  enterprise_id, period_id, report_id, old_report_id,
  reason_return, status, 
  period_start_time, period_end_time,
  created_at, updated_at,
  enterprise_nature, enterprise_industry, enterprise_region
) VALUES (
  @ent_id, @period_id_2024_09, 'ENT002_2024_09_V1', NULL,
  NULL, 4,  -- status=4 已归档
  '2024-09-10 08:00:00', '2024-09-25 23:59:59',
  '2024-09-12 16:00:00', '2024-09-20 11:00:00',
  2, 2, 5303
);

-- 市级审核通过
INSERT INTO report_audit_history (
  enterprise_id, period_id, report_id,
  audit_level, auditor, audit_result, audit_opinion, audit_time
) VALUES (
  @ent_id, @period_id_2024_09, 'ENT002_2024_09_V1',
  1, 'city_qj_auditor', 1, 
  '减员类型选择"其他"，说明详细合理，同意通过。', 
  '2024-09-15 10:00:00'
);

-- 省级审核通过
INSERT INTO report_audit_history (
  enterprise_id, period_id, report_id,
  audit_level, auditor, audit_result, audit_opinion, audit_time
) VALUES (
  @ent_id, @period_id_2024_09, 'ENT002_2024_09_V1',
  2, 'province_auditor', 1, 
  '数据核实准确，审核通过。', 
  '2024-09-20 11:00:00'
);

-- ============================================
-- 场景8：2024-08月 - 增员情况（人数增加）
-- ============================================

INSERT INTO period_info (investigate_time, period_start_time, period_end_time, enterprise_count)
VALUES ('2024-08', '2024-08-10 08:00:00', '2024-08-25 23:59:59', 175)
ON DUPLICATE KEY UPDATE investigate_time = investigate_time;

SET @period_id_2024_08 = (SELECT period_id FROM period_info WHERE investigate_time = '2024-08');

INSERT INTO report_info (
  report_id, construction_count, investigation_count, 
  reduction_type, reason1, reason1_desc, 
  reason2, reason2_desc, reason3, reason3_desc, other_reason
) VALUES 
('ENT002_2024_08_V1', 85, 95, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

INSERT INTO enterprise_report_info (
  enterprise_id, period_id, report_id, old_report_id,
  reason_return, status, 
  period_start_time, period_end_time,
  created_at, updated_at,
  enterprise_nature, enterprise_industry, enterprise_region
) VALUES (
  @ent_id, @period_id_2024_08, 'ENT002_2024_08_V1', NULL,
  NULL, 4,  -- status=4 已归档
  '2024-08-10 08:00:00', '2024-08-25 23:59:59',
  '2024-08-12 10:00:00', '2024-08-18 15:00:00',
  2, 2, 5303
);

-- 市级审核通过
INSERT INTO report_audit_history (
  enterprise_id, period_id, report_id,
  audit_level, auditor, audit_result, audit_opinion, audit_time
) VALUES (
  @ent_id, @period_id_2024_08, 'ENT002_2024_08_V1',
  1, 'city_qj_auditor', 1, 
  '增员10人，无需填写减员信息，符合要求，同意通过。', 
  '2024-08-15 14:00:00'
);

-- 省级审核通过
INSERT INTO report_audit_history (
  enterprise_id, period_id, report_id,
  audit_level, auditor, audit_result, audit_opinion, audit_time
) VALUES (
  @ent_id, @period_id_2024_08, 'ENT002_2024_08_V1',
  2, 'province_auditor', 1, 
  '数据准确，审核通过。', 
  '2024-08-18 15:00:00'
);

-- ============================================
-- 场景9：2025-04月 - 驳回状态（当前可编辑）
-- ============================================

INSERT INTO period_info (investigate_time, period_start_time, period_end_time, enterprise_count)
VALUES ('2025-04', '2025-04-01 00:00:00', '2025-10-30 23:59:59', 205)
ON DUPLICATE KEY UPDATE investigate_time = investigate_time;

SET @period_id_2025_04 = (SELECT period_id FROM period_info WHERE investigate_time = '2025-04');

INSERT INTO report_info (
  report_id, construction_count, investigation_count, 
  reduction_type, reason1, reason1_desc, 
  reason2, reason2_desc, reason3, reason3_desc, other_reason
) VALUES 
('ENT002_2025_04_V1', 80, 75, 5, 6, '合同到期', NULL, NULL, NULL, NULL, NULL);

INSERT INTO enterprise_report_info (
  enterprise_id, period_id, report_id, old_report_id,
  reason_return, status, 
  period_start_time, period_end_time,
  created_at, updated_at,
  enterprise_nature, enterprise_industry, enterprise_region
) VALUES (
  @ent_id, @period_id_2025_04, 'ENT002_2025_04_V1', NULL,
  '减员原因说明过于简单，请详细说明合同到期人员的具体情况', 5,  -- status=5 驳回
  '2025-04-01 00:00:00', '2025-10-30 23:59:59',
  '2025-04-08 10:00:00', '2025-04-10 14:30:00',
  2, 2, 5303
);

-- 市级驳回记录
INSERT INTO report_audit_history (
  enterprise_id, period_id, report_id,
  audit_level, auditor, audit_result, audit_opinion, audit_time
) VALUES (
  @ent_id, @period_id_2025_04, 'ENT002_2025_04_V1',
  1, 'city_qj_auditor', 2, 
  '减员原因说明过于简单，仅写"合同到期"不符合要求。请详细说明：1.合同到期人员数量；2.未续签原因；3.岗位分布等信息。', 
  '2025-04-10 14:30:00'
);

-- ============================================
-- 场景10：2024-07月 - 无减员（人数持平）
-- ============================================

INSERT INTO period_info (investigate_time, period_start_time, period_end_time, enterprise_count)
VALUES ('2024-07', '2024-07-10 08:00:00', '2024-07-25 23:59:59', 170)
ON DUPLICATE KEY UPDATE investigate_time = investigate_time;

SET @period_id_2024_07 = (SELECT period_id FROM period_info WHERE investigate_time = '2024-07');

INSERT INTO report_info (
  report_id, construction_count, investigation_count, 
  reduction_type, reason1, reason1_desc, 
  reason2, reason2_desc, reason3, reason3_desc, other_reason
) VALUES 
('ENT002_2024_07_V1', 85, 85, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

INSERT INTO enterprise_report_info (
  enterprise_id, period_id, report_id, old_report_id,
  reason_return, status, 
  period_start_time, period_end_time,
  created_at, updated_at,
  enterprise_nature, enterprise_industry, enterprise_region
) VALUES (
  @ent_id, @period_id_2024_07, 'ENT002_2024_07_V1', NULL,
  NULL, 4,  -- status=4 已归档
  '2024-07-10 08:00:00', '2024-07-25 23:59:59',
  '2024-07-12 09:00:00', '2024-07-20 16:00:00',
  2, 2, 5303
);

-- 市级审核通过
INSERT INTO report_audit_history (
  enterprise_id, period_id, report_id,
  audit_level, auditor, audit_result, audit_opinion, audit_time
) VALUES (
  @ent_id, @period_id_2024_07, 'ENT002_2024_07_V1',
  1, 'city_qj_auditor', 1, 
  '人数持平，无减员，数据完整，同意通过。', 
  '2024-07-15 10:00:00'
);

-- 省级审核通过
INSERT INTO report_audit_history (
  enterprise_id, period_id, report_id,
  audit_level, auditor, audit_result, audit_opinion, audit_time
) VALUES (
  @ent_id, @period_id_2024_07, 'ENT002_2024_07_V1',
  2, 'province_auditor', 1, 
  '数据准确，审核通过。', 
  '2024-07-20 16:00:00'
);

-- ============================================
-- 验证所有测试数据
-- ============================================

SELECT '=== test_ent_002 所有报表（按时间倒序）===' as info;
SELECT 
  er.report_id,
  p.investigate_time,
  CASE 
    WHEN er.status = -1 THEN '未填报'
    WHEN er.status = 0 THEN '已暂存'
    WHEN er.status = 1 THEN '待市级审核'
    WHEN er.status = 2 THEN '待省级审核'
    WHEN er.status = 3 THEN '审核通过'
    WHEN er.status = 4 THEN '已归档'
    WHEN er.status = 5 THEN '驳回'
  END as 状态,
  r.construction_count as 建档期人数,
  r.investigation_count as 调查期人数,
  (r.construction_count - IFNULL(r.investigation_count, 0)) as 人数变化,
  CASE 
    WHEN r.reduction_type = 1 THEN '岗位调整'
    WHEN r.reduction_type = 2 THEN '企业搬迁'
    WHEN r.reduction_type = 3 THEN '订单减少'
    WHEN r.reduction_type = 4 THEN '大规模裁员'
    WHEN r.reduction_type = 5 THEN '小规模减员'
    WHEN r.reduction_type = 8 THEN '其他'
    ELSE '无'
  END as 减员类型,
  CASE 
    WHEN r.reason1 = 6 THEN '合同到期'
    WHEN r.reason1 = 7 THEN '成本上涨'
    WHEN r.reason1 = 9 THEN '正常退休'
    WHEN r.reason1 = 14 THEN '其他原因'
    ELSE '无'
  END as 主要原因,
  er.reason_return as 驳回原因
FROM enterprise_report_info er
JOIN period_info p ON er.period_id = p.period_id
LEFT JOIN report_info r ON er.report_id = r.report_id
WHERE er.enterprise_id = @ent_id
ORDER BY p.investigate_time DESC;

SELECT '' as '';
SELECT '=== 测试场景汇总 ===' as info;
SELECT 
  '2025-04' as 调查期, '驳回' as 状态, '80' as 建档期, '75' as 调查期, '-5' as 变化, '市级驳回，减员原因说明不足（当前可编辑）' as 场景说明
UNION ALL SELECT '2025-03', '待市级审核', '80', '72', '-8', '多种减员原因，待审核'
UNION ALL SELECT '2025-02', '已暂存', '80', '75', '-5', '暂存状态，数据不完整（可编辑）'
UNION ALL SELECT '2025-01', '审核通过', '80', '80', '0', '无减员，顺利通过'
UNION ALL SELECT '2024-12', '审核通过', '85', '78', '-7', '经历一次驳回后通过'
UNION ALL SELECT '2024-11', '审核通过', '90', '85', '-5', '省级驳回后补充详细说明通过'
UNION ALL SELECT '2024-10', '已归档', '100', '70', '-30', '大规模裁员，多种原因'
UNION ALL SELECT '2024-09', '已归档', '95', '90', '-5', '"其他"减员类型'
UNION ALL SELECT '2024-08', '已归档', '85', '95', '+10', '增员情况'
UNION ALL SELECT '2024-07', '已归档', '85', '85', '0', '人数持平';


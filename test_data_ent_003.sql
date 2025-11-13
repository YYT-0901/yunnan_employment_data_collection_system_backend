-- ============================================
-- 企业测试账号 (test_ent_003) 完整测试数据
-- 企业名称：昆明科创软件技术有限公司
-- 用途：完整的企业端功能测试，包含多种报表状态
-- ============================================

USE project1;

-- 步骤1：清理可能存在的旧数据
DELETE FROM report_audit_history WHERE enterprise_id = 'TEST_ENT_003';
DELETE FROM enterprise_report_info WHERE enterprise_id = 'TEST_ENT_003';
DELETE FROM report_info WHERE report_id LIKE 'ENT003_%';
DELETE FROM account_info WHERE username = 'test_ent_003';
DELETE FROM enterprise_info WHERE enterprise_id = 'TEST_ENT_003';

-- 步骤2：插入企业基本信息
INSERT INTO enterprise_info (
  enterprise_id, 
  org_code, 
  name, 
  region, 
  nature, 
  industry, 
  industry_desc, 
  contact_name, 
  address, 
  postal_code, 
  phone_num, 
  fax_num, 
  email, 
  status, 
  created_at, 
  updated_at
) VALUES (
  'TEST_ENT_003',                    -- 企业ID
  '91530100MA6K9999XX',              -- 组织机构代码
  '昆明科创软件技术有限公司',        -- 企业名称
  5301,                              -- 昆明市
  1,                                 -- 企业性质：1-国有企业
  1,                                 -- 行业类别：1-信息技术
  '软件开发与技术服务',              -- 行业描述
  '王经理',                          -- 联系人
  '昆明市高新区科技路88号',          -- 地址
  '650000',                          -- 邮编
  '0871-88888888',                   -- 电话
  '0871-88888889',                   -- 传真
  'test003@example.com',             -- 邮箱
  3,                                 -- 状态：3-审核通过
  '2023-06-01 10:00:00',             -- 创建时间
  '2023-06-01 10:00:00'              -- 更新时间
);

-- 步骤3：插入账号信息
INSERT INTO account_info (
  username, 
  password, 
  type, 
  enterprise_id, 
  city_code, 
  last_login_time, 
  status, 
  created_at
) VALUES (
  'test_ent_003',                    -- 用户名
  'password123',                     -- 密码
  1,                                 -- 账号类型：1-企业账号
  'TEST_ENT_003',                    -- 关联企业ID
  NULL,                              -- 市级代码（企业账号为NULL）
  '2025-11-04 09:00:00',             -- 最后登录时间
  0,                                 -- 状态：0-正常
  '2023-06-01 10:00:00'              -- 创建时间
);

-- 步骤4：确保有足够的调查期数据
INSERT INTO period_info (investigate_time, period_start_time, period_end_time, enterprise_count, created_at, updated_at) 
VALUES ('2025-01', '2025-01-10 08:00:00', '2025-02-28 23:59:59', 200, '2024-12-20 10:00:00', '2024-12-20 10:00:00')
ON DUPLICATE KEY UPDATE investigate_time = investigate_time;

INSERT INTO period_info (investigate_time, period_start_time, period_end_time, enterprise_count, created_at, updated_at) 
VALUES ('2024-12', '2024-12-10 08:00:00', '2024-12-31 23:59:59', 195, '2024-11-20 10:00:00', '2024-11-20 10:00:00')
ON DUPLICATE KEY UPDATE investigate_time = investigate_time;

INSERT INTO period_info (investigate_time, period_start_time, period_end_time, enterprise_count, created_at, updated_at) 
VALUES ('2024-11', '2024-11-10 08:00:00', '2024-11-30 23:59:59', 190, '2024-10-20 10:00:00', '2024-10-20 10:00:00')
ON DUPLICATE KEY UPDATE investigate_time = investigate_time;

INSERT INTO period_info (investigate_time, period_start_time, period_end_time, enterprise_count, created_at, updated_at) 
VALUES ('2024-10', '2024-10-10 08:00:00', '2024-10-31 23:59:59', 185, '2024-09-20 10:00:00', '2024-09-20 10:00:00')
ON DUPLICATE KEY UPDATE investigate_time = investigate_time;

-- 获取各调查期的period_id
SET @period_2025_01 = (SELECT period_id FROM period_info WHERE investigate_time = '2025-01');
SET @period_2024_12 = (SELECT period_id FROM period_info WHERE investigate_time = '2024-12');
SET @period_2024_11 = (SELECT period_id FROM period_info WHERE investigate_time = '2024-11');
SET @period_2024_10 = (SELECT period_id FROM period_info WHERE investigate_time = '2024-10');

-- ============================================
-- 场景1：2025-01月 - 暂存状态（企业正在填报）
-- ============================================
INSERT INTO report_info (
  report_id, 
  construction_count,    -- 建档期人数
  investigation_count,   -- 调查期人数
  reduction_type,        -- 减员类型
  reason1, reason1_desc,
  reason2, reason2_desc,
  reason3, reason3_desc,
  other_reason
) VALUES (
  'ENT003_2025_01_V1',
  120,                   -- 建档期120人
  NULL,                  -- 调查期人数未填写
  NULL,                  -- 减员类型未选择
  NULL, NULL,
  NULL, NULL,
  NULL, NULL,
  NULL
);

INSERT INTO enterprise_report_info (
  enterprise_id, 
  period_id, 
  report_id, 
  old_report_id,
  reason_return, 
  status,                -- 0-已暂存
  period_start_time, 
  period_end_time,
  created_at, 
  updated_at,
  enterprise_nature, 
  enterprise_industry, 
  enterprise_region
) VALUES (
  'TEST_ENT_003',
  @period_2025_01,
  'ENT003_2025_01_V1',
  NULL,
  NULL,
  0,                     -- 状态：0-已暂存
  '2025-01-10 08:00:00',
  '2025-02-28 23:59:59',
  '2025-01-15 14:30:00',
  '2025-01-15 14:30:00',
  1,                     -- 企业性质：1-国有企业
  1,                     -- 行业：1-信息技术
  5301                   -- 昆明市
);

-- ============================================
-- 场景2：2024-12月 - 待市级审核（刚提交）
-- ============================================
INSERT INTO report_info (
  report_id, 
  construction_count, 
  investigation_count, 
  reduction_type,
  reason1, reason1_desc,
  reason2, reason2_desc,
  reason3, reason3_desc,
  other_reason
) VALUES (
  'ENT003_2024_12_V1',
  115,                   -- 建档期115人
  115,                   -- 调查期115人（无变化）
  NULL,                  -- 人数无变化，无需填写减员类型
  NULL, NULL,
  NULL, NULL,
  NULL, NULL,
  NULL
);

INSERT INTO enterprise_report_info (
  enterprise_id, 
  period_id, 
  report_id, 
  old_report_id,
  reason_return, 
  status,
  period_start_time, 
  period_end_time,
  created_at, 
  updated_at,
  enterprise_nature, 
  enterprise_industry, 
  enterprise_region
) VALUES (
  'TEST_ENT_003',
  @period_2024_12,
  'ENT003_2024_12_V1',
  NULL,
  NULL,
  1,                     -- 状态：1-待市级审核
  '2024-12-10 08:00:00',
  '2024-12-31 23:59:59',
  '2024-12-18 16:20:00',
  '2024-12-18 16:20:00',
  1,
  1,
  5301
);

-- ============================================
-- 场景3：2024-11月 - 经历驳回后重新提交并通过
-- ============================================

-- 第一版（被驳回）
INSERT INTO report_info (
  report_id, 
  construction_count, 
  investigation_count, 
  reduction_type,
  reason1, reason1_desc,
  reason2, reason2_desc,
  reason3, reason3_desc,
  other_reason
) VALUES (
  'ENT003_2024_11_V1',
  110,                   -- 建档期110人
  103,                   -- 调查期103人（减少7人）
  5,                     -- 减员类型：5-其他原因
  6,                     -- 原因1：6-合同到期
  '合同到期未续签',      -- 原因1说明（太简略）
  NULL, NULL,
  NULL, NULL,
  NULL
);

INSERT INTO enterprise_report_info (
  enterprise_id, 
  period_id, 
  report_id, 
  old_report_id,
  reason_return, 
  status,
  period_start_time, 
  period_end_time,
  created_at, 
  updated_at,
  enterprise_nature, 
  enterprise_industry, 
  enterprise_region
) VALUES (
  'TEST_ENT_003',
  @period_2024_11,
  'ENT003_2024_11_V1',
  NULL,
  '减员原因说明不够详细，请补充具体人数和原因',
  5,                     -- 状态：5-驳回
  '2024-11-10 08:00:00',
  '2024-11-30 23:59:59',
  '2024-11-13 10:00:00',
  '2024-11-16 15:30:00',
  1,
  1,
  5301
);

-- 市级审核记录（驳回）
INSERT INTO report_audit_history (
  enterprise_id, 
  period_id, 
  report_id,
  audit_level,           -- 1-市级审核
  auditor, 
  audit_result,          -- 2-驳回
  audit_opinion, 
  audit_time
) VALUES (
  'TEST_ENT_003',
  @period_2024_11,
  'ENT003_2024_11_V1',
  1,
  'city_km_auditor',
  2,
  '调查期人数较建档期减少7人，但减员原因说明过于简略。请详细说明：1.每种减员原因对应的具体人数；2.合同到期未续签的具体原因；3.是否有其他减员原因。请补充完整后重新提交。',
  '2024-11-16 15:30:00'
);

-- 第二版（修改后重新提交，审核通过）
INSERT INTO report_info (
  report_id, 
  construction_count, 
  investigation_count, 
  reduction_type,
  reason1, reason1_desc,
  reason2, reason2_desc,
  reason3, reason3_desc,
  other_reason
) VALUES (
  'ENT003_2024_11_V2',
  110,                   -- 建档期110人
  103,                   -- 调查期103人
  5,                     -- 减员类型：5-其他原因
  6,                     -- 原因1：6-合同到期
  '合同到期未续签5人：项目制员工，因项目结项合同到期，公司暂无新项目安排，与5名项目人员协商一致未续签劳动合同。涉及岗位：前端开发2人、后端开发2人、测试工程师1人。',
  8,                     -- 原因2：8-员工主动离职
  '员工主动离职2人：1名员工因个人家庭原因搬迁至外地，1名员工因职业规划转行至其他行业。均已按规定办理离职手续。',
  NULL, NULL,
  NULL
);

INSERT INTO enterprise_report_info (
  enterprise_id, 
  period_id, 
  report_id, 
  old_report_id,
  reason_return, 
  status,
  period_start_time, 
  period_end_time,
  created_at, 
  updated_at,
  enterprise_nature, 
  enterprise_industry, 
  enterprise_region
) VALUES (
  'TEST_ENT_003',
  @period_2024_11,
  'ENT003_2024_11_V2',
  'ENT003_2024_11_V1',   -- 关联旧版本
  NULL,
  3,                     -- 状态：3-审核通过
  '2024-11-10 08:00:00',
  '2024-11-30 23:59:59',
  '2024-11-17 09:00:00',
  '2024-11-22 16:00:00',
  1,
  1,
  5301
);

-- 市级审核记录（通过）
INSERT INTO report_audit_history (
  enterprise_id, 
  period_id, 
  report_id,
  audit_level,
  auditor, 
  audit_result,
  audit_opinion, 
  audit_time
) VALUES (
  'TEST_ENT_003',
  @period_2024_11,
  'ENT003_2024_11_V2',
  1,
  'city_km_auditor',
  1,
  '已补充详细的减员原因说明，数据完整准确，减员原因合理。同意通过并提交省级审核。',
  '2024-11-19 14:00:00'
);

-- 省级审核记录（通过）
INSERT INTO report_audit_history (
  enterprise_id, 
  period_id, 
  report_id,
  audit_level,
  auditor, 
  audit_result,
  audit_opinion, 
  audit_time
) VALUES (
  'TEST_ENT_003',
  @period_2024_11,
  'ENT003_2024_11_V2',
  2,
  'province_auditor',
  1,
  '减员原因说明详细，符合软件行业特点，数据真实可信。审核通过。',
  '2024-11-22 16:00:00'
);

-- ============================================
-- 场景4：2024-10月 - 完整审核通过（已归档）
-- ============================================
INSERT INTO report_info (
  report_id, 
  construction_count, 
  investigation_count, 
  reduction_type,
  reason1, reason1_desc,
  reason2, reason2_desc,
  reason3, reason3_desc,
  other_reason
) VALUES (
  'ENT003_2024_10_V1',
  105,                   -- 建档期105人
  110,                   -- 调查期110人（增加5人）
  NULL,                  -- 无减员
  NULL, NULL,
  NULL, NULL,
  NULL, NULL,
  NULL
);

INSERT INTO enterprise_report_info (
  enterprise_id, 
  period_id, 
  report_id, 
  old_report_id,
  reason_return, 
  status,
  period_start_time, 
  period_end_time,
  created_at, 
  updated_at,
  enterprise_nature, 
  enterprise_industry, 
  enterprise_region
) VALUES (
  'TEST_ENT_003',
  @period_2024_10,
  'ENT003_2024_10_V1',
  NULL,
  NULL,
  4,                     -- 状态：4-已归档
  '2024-10-10 08:00:00',
  '2024-10-31 23:59:59',
  '2024-10-15 11:00:00',
  '2024-10-25 15:00:00',
  1,
  1,
  5301
);

-- 市级审核记录（通过）
INSERT INTO report_audit_history (
  enterprise_id, 
  period_id, 
  report_id,
  audit_level,
  auditor, 
  audit_result,
  audit_opinion, 
  audit_time
) VALUES (
  'TEST_ENT_003',
  @period_2024_10,
  'ENT003_2024_10_V1',
  1,
  'city_km_auditor',
  1,
  '人数增加5人，企业发展良好，数据填写规范。同意通过。',
  '2024-10-18 10:00:00'
);

-- 省级审核记录（通过）
INSERT INTO report_audit_history (
  enterprise_id, 
  period_id, 
  report_id,
  audit_level,
  auditor, 
  audit_result,
  audit_opinion, 
  audit_time
) VALUES (
  'TEST_ENT_003',
  @period_2024_10,
  'ENT003_2024_10_V1',
  2,
  'province_auditor',
  1,
  '数据准确，企业用工情况稳定，审核通过。',
  '2024-10-25 15:00:00'
);

-- ============================================
-- 确保市级和省级审核账号存在
-- ============================================
INSERT INTO account_info (username, password, type, enterprise_id, city_code, last_login_time, status, created_at) 
VALUES ('city_km_auditor', 'city123', 2, NULL, 5301, NULL, 0, '2024-01-01 09:00:00')
ON DUPLICATE KEY UPDATE username = username;

INSERT INTO account_info (username, password, type, enterprise_id, city_code, last_login_time, status, created_at) 
VALUES ('province_auditor', 'province123', 3, NULL, NULL, NULL, 0, '2024-01-01 08:00:00')
ON DUPLICATE KEY UPDATE username = username;

-- ============================================
-- 数据验证查询
-- ============================================

SELECT '==================== 企业基本信息 ====================' as '';
SELECT 
  enterprise_id,
  name,
  region,
  CASE nature
    WHEN 1 THEN '国有企业'
    WHEN 2 THEN '民营企业'
    WHEN 3 THEN '外资企业'
    WHEN 4 THEN '合资企业'
    ELSE '其他'
  END as nature_name,
  CASE industry
    WHEN 1 THEN '信息技术'
    WHEN 2 THEN '制造业'
    WHEN 3 THEN '服务业'
    ELSE '其他'
  END as industry_name,
  contact_name,
  phone_num,
  email
FROM enterprise_info 
WHERE enterprise_id = 'TEST_ENT_003';

SELECT '==================== 账号信息 ====================' as '';
SELECT 
  username,
  password,
  CASE type
    WHEN 1 THEN '企业账号'
    WHEN 2 THEN '市级账号'
    WHEN 3 THEN '省级账号'
  END as account_type,
  CASE status
    WHEN 0 THEN '正常'
    WHEN 1 THEN '禁用'
  END as status_name,
  last_login_time,
  created_at
FROM account_info 
WHERE username = 'test_ent_003';

SELECT '==================== 报表列表 ====================' as '';
SELECT 
  er.report_id,
  p.investigate_time,
  CASE er.status
    WHEN -1 THEN '❌ 未填报'
    WHEN 0 THEN '📝 已暂存'
    WHEN 1 THEN '⏳ 待市级审核'
    WHEN 2 THEN '⏳ 待省级审核'
    WHEN 3 THEN '✅ 审核通过'
    WHEN 4 THEN '📦 已归档'
    WHEN 5 THEN '❌ 驳回'
  END as status_name,
  er.old_report_id,
  r.construction_count,
  r.investigation_count,
  CASE 
    WHEN r.investigation_count IS NULL THEN '未填写'
    WHEN r.investigation_count = r.construction_count THEN '无变化'
    WHEN r.investigation_count > r.construction_count THEN CONCAT('增加', r.investigation_count - r.construction_count, '人')
    ELSE CONCAT('减少', r.construction_count - r.investigation_count, '人')
  END as personnel_change,
  er.reason_return,
  DATE_FORMAT(er.created_at, '%Y-%m-%d %H:%i') as created_time,
  DATE_FORMAT(er.updated_at, '%Y-%m-%d %H:%i') as updated_time
FROM enterprise_report_info er
JOIN period_info p ON er.period_id = p.period_id
LEFT JOIN report_info r ON er.report_id = r.report_id
WHERE er.enterprise_id = 'TEST_ENT_003'
ORDER BY p.investigate_time DESC;

SELECT '==================== 审核历史明细 ====================' as '';
SELECT 
  p.investigate_time,
  ah.report_id,
  CASE ah.audit_level
    WHEN 1 THEN '🏛️ 市级审核'
    WHEN 2 THEN '🏛️ 省级审核'
  END as audit_level_name,
  ah.auditor,
  CASE ah.audit_result
    WHEN 1 THEN '✅ 通过'
    WHEN 2 THEN '❌ 驳回'
  END as audit_result_name,
  ah.audit_opinion,
  DATE_FORMAT(ah.audit_time, '%Y-%m-%d %H:%i') as audit_time
FROM report_audit_history ah
JOIN period_info p ON ah.period_id = p.period_id
WHERE ah.enterprise_id = 'TEST_ENT_003'
ORDER BY p.investigate_time DESC, ah.audit_time ASC;

SELECT '==================== 统计汇总 ====================' as '';
SELECT 
  COUNT(*) as total_reports,
  SUM(CASE WHEN status = 0 THEN 1 ELSE 0 END) as saved_count,
  SUM(CASE WHEN status = 1 THEN 1 ELSE 0 END) as pending_city_audit,
  SUM(CASE WHEN status = 2 THEN 1 ELSE 0 END) as pending_province_audit,
  SUM(CASE WHEN status = 3 THEN 1 ELSE 0 END) as approved_count,
  SUM(CASE WHEN status = 4 THEN 1 ELSE 0 END) as archived_count,
  SUM(CASE WHEN status = 5 THEN 1 ELSE 0 END) as rejected_count
FROM enterprise_report_info
WHERE enterprise_id = 'TEST_ENT_003';

SELECT '✅ ==================== 测试数据导入完成！ ====================' as result;
SELECT '账号信息：' as '';
SELECT 'username: test_ent_003' as '';
SELECT 'password: password123' as '';
SELECT '企业名称：昆明科创软件技术有限公司' as '';
SELECT '已创建4个调查期的测试数据，涵盖多种状态场景' as '';



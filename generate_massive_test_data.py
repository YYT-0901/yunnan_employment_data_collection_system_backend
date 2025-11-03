#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
大规模测试数据生成脚本
目标：生成1200家企业 × 12个月 = 14400+条报表记录
覆盖云南省所有地州市的区县和街道
"""

import json
import random
from datetime import datetime, timedelta

# 企业性质代码
NATURES = [110, 120, 210, 310, 410, 510]
NATURE_NAMES = {
    110: '有限责任公司',
    120: '股份有限公司',
    210: '集体所有制',
    310: '国有企业',
    410: '个体经营',
    510: '其他'
}

# 企业行业代码
INDUSTRIES = [10101, 10102, 10201, 10301, 10302, 10303, 10304, 10305, 10401, 10402, 10501, 10601, 10701, 10702, 10801, 10901, 10902, 11001, 11002, 11003, 11101, 11201]
INDUSTRY_NAMES = {
    10101: '农业种植',
    10102: '农产品加工',
    10201: '采矿业',
    10301: '通用设备制造',
    10302: '专用设备制造',
    10303: '建材制造',
    10304: '化工制品',
    10305: '纺织服装',
    10401: '房屋建筑',
    10402: '土木工程',
    10501: '电力供应',
    10601: '信息技术服务',
    10701: '批发业',
    10702: '零售业',
    10801: '物流运输',
    10901: '教育培训',
    10902: '医疗卫生',
    11001: '旅游业',
    11002: '住宿餐饮',
    11003: '文化娱乐',
    11101: '文化创意',
    11201: '环保服务'
}

# 云南地区数据（简化版，基于真实数据）
REGIONS = {
    # 临沧市
    1: {'name': '临沧市', 'districts': [101, 102, 103, 104, 105, 106, 107, 108]},
    # 丽江市
    2: {'name': '丽江市', 'districts': [201, 202, 203, 204, 205]},
    # 保山市
    3: {'name': '保山市', 'districts': [301, 302, 303, 304, 305]},
    # 普洱市
    4: {'name': '普洱市', 'districts': [401, 402, 403, 404, 405, 406, 407, 408, 409, 410]},
    # 昆明市
    5: {'name': '昆明市', 'districts': [501, 502, 503, 504, 505, 506, 507, 508, 509, 510, 511, 512, 513, 514]},
    # 昭通市
    6: {'name': '昭通市', 'districts': [601, 602, 603, 604, 605, 606, 607, 608, 609, 610, 611]},
    # 曲靖市
    7: {'name': '曲靖市', 'districts': [701, 702, 703, 704, 705, 706, 707, 708, 709]},
    # 玉溪市
    8: {'name': '玉溪市', 'districts': [801, 802, 803, 804, 805, 806, 807, 808, 809]},
    # 文山州
    9: {'name': '文山州', 'districts': [901, 902, 903, 904, 905, 906, 907, 908]},
    # 红河州
    10: {'name': '红河州', 'districts': [1001, 1002, 1003, 1004, 1005, 1006, 1007, 1008, 1009, 1010, 1011, 1012, 1013]},
    # 西双版纳州
    11: {'name': '西双版纳州', 'districts': [1101, 1102, 1103]},
    # 楚雄州
    12: {'name': '楚雄州', 'districts': [1201, 1202, 1203, 1204, 1205, 1206, 1207, 1208, 1209, 1210]},
    # 大理州
    13: {'name': '大理州', 'districts': [1301, 1302, 1303, 1304, 1305, 1306, 1307, 1308, 1309, 1310, 1311, 1312]},
    # 德宏州
    14: {'name': '德宏州', 'districts': [1401, 1402, 1403, 1404, 1405]},
    # 怒江州
    15: {'name': '怒江州', 'districts': [1501, 1502, 1503, 1504]},
    # 迪庆州
    16: {'name': '迪庆州', 'districts': [1601, 1602, 1603]}
}

# 企业名称后缀
COMPANY_SUFFIXES = [
    '科技有限公司', '实业有限公司', '商贸有限公司', '建筑工程有限公司',
    '农业开发有限公司', '旅游开发有限公司', '物流有限公司', '教育咨询有限公司',
    '文化传播有限公司', '环保科技有限公司', '新能源有限公司', '生物科技有限公司',
    '食品加工厂', '制造有限公司', '电子商务有限公司', '投资有限公司',
    '矿业开发公司', '茶业有限公司', '酒店管理公司', '医疗器械公司'
]

# 企业名称前缀（特色）
COMPANY_PREFIXES = [
    '云', '滇', '彩云', '七彩', '高原', '金', '银', '铜', '翠', '玉',
    '龙', '凤', '雄', '峰', '山', '水', '绿', '蓝', '红', '橙',
    '春', '夏', '秋', '冬', '东', '西', '南', '北', '中', '新',
    '华', '盛', '兴', '泰', '安', '康', '富', '强', '美', '好'
]

# 常见姓氏
SURNAMES = ['李', '王', '张', '刘', '陈', '杨', '黄', '赵', '周', '吴', '徐', '孙', '马', '朱', '胡', '郭', '何', '高', '林', '罗']
GIVEN_NAMES = ['伟', '芳', '娜', '秀英', '敏', '静', '丽', '强', '磊', '军', '洋', '勇', '艳', '杰', '娟', '涛', '明', '超', '秀兰', '霞']

def generate_name():
    """生成随机姓名"""
    return random.choice(SURNAMES) + random.choice(GIVEN_NAMES)

def generate_company_name(region_name):
    """生成企业名称"""
    prefix = random.choice(COMPANY_PREFIXES)
    suffix = random.choice(COMPANY_SUFFIXES)
    # 部分企业名称包含地区名
    if random.random() < 0.3:
        return f"{region_name}{prefix}{suffix}"
    else:
        return f"{prefix}{suffix}"

def generate_phone(area_code):
    """生成电话号码"""
    return f"{area_code}-{random.randint(2000000, 9999999)}"

def generate_org_code():
    """生成组织机构代码"""
    code = ''.join([str(random.randint(0, 9)) for _ in range(9)])
    suffix = ''.join(random.choices('ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789', k=8))
    return f"915300{code}{suffix}"

def escape_sql_string(s):
    """转义SQL字符串"""
    if s is None:
        return 'NULL'
    return "'" + str(s).replace("'", "''").replace("\\", "\\\\") + "'"

# 生成企业数据
def generate_enterprises():
    """生成1200家企业"""
    enterprises = []
    ent_id = 1
    
    for city_code, city_data in REGIONS.items():
        city_name = city_data['name']
        districts = city_data['districts']
        
        # 每个市平均75家企业
        num_enterprises_per_city = 75
        
        for i in range(num_enterprises_per_city):
            district_code = random.choice(districts)
            nature = random.choice(NATURES)
            industry = random.choice(INDUSTRIES)
            
            # 企业ID格式: ENT_{city_code:02d}_{district_code:03d}_{seq:03d}
            enterprise_id = f"ENT_{city_code:02d}_{district_code:04d}_{i+1:03d}"
            org_code = generate_org_code()
            name = generate_company_name(city_name)
            contact_name = generate_name()
            
            # 地区代码（使用真实的区县代码）
            region = district_code
            
            # 地址
            address = f"{city_name}某区某街道{random.randint(1, 999)}号"
            postal_code = f"6{random.randint(50000, 79999)}"
            
            # 电话（云南区号）
            area_codes = ['0871', '0873', '0874', '0875', '0876', '0877', '0878', '0879', '0883', '0886', '0887', '0888', '0691']
            phone = generate_phone(random.choice(area_codes))
            fax = generate_phone(random.choice(area_codes))
            
            email = f"contact{ent_id}@{enterprise_id.lower()}.com"
            
            # 创建时间（2021年随机月份）
            created_month = random.randint(1, 12)
            created_day = random.randint(1, 28)
            created_at = f"2021-{created_month:02d}-{created_day:02d} {random.randint(8,17):02d}:00:00"
            
            enterprise = {
                'enterprise_id': enterprise_id,
                'org_code': org_code,
                'name': name,
                'region': region,
                'nature': nature,
                'industry': industry,
                'industry_desc': INDUSTRY_NAMES.get(industry, '其他业务'),
                'contact_name': contact_name,
                'address': address,
                'postal_code': postal_code,
                'phone_num': phone,
                'fax_num': fax,
                'email': email,
                'status': 3,  # 正常
                'created_at': created_at,
                'updated_at': created_at
            }
            
            enterprises.append(enterprise)
            ent_id += 1
    
    return enterprises

def generate_reports_for_enterprise(enterprise, period_month):
    """为单个企业生成某月的报表"""
    # 基础人数（根据行业和企业性质）
    industry = enterprise['industry']
    nature = enterprise['nature']
    
    # 国企和大型企业人数多一些
    if nature == 310:  # 国企
        base_count = random.randint(200, 800)
    elif nature in [110, 120]:  # 有限责任/股份公司
        base_count = random.randint(50, 300)
    else:  # 其他
        base_count = random.randint(20, 150)
    
    # 调查期就业人数（相对建档期的变化）
    # 2022年前半年受疫情影响，部分企业人数减少
    if period_month <= 6:
        variation = random.randint(-30, 20)
    else:  # 下半年逐渐恢复
        variation = random.randint(-10, 40)
    
    construction_count = base_count
    investigation_count = max(10, base_count + variation)  # 最少保留10人
    
    # 确定减少类型和原因
    if investigation_count < construction_count:
        reduction_type = 2  # 减少
        reasons = [201, 202, 203]  # 减少原因
        reason1 = random.choice(reasons)
        reason1_desc = f"由于市场环境变化，人员减少{construction_count - investigation_count}人"
    elif investigation_count > construction_count:
        reduction_type = 1  # 增加
        reasons = [101, 102, 103]  # 增加原因
        reason1 = random.choice(reasons)
        reason1_desc = f"业务扩展，新增岗位{investigation_count - construction_count}人"
    else:
        reduction_type = 3  # 持平
        reason1 = 301
        reason1_desc = "人员稳定"
    
    report = {
        'construction_count': construction_count,
        'investigation_count': investigation_count,
        'reduction_type': reduction_type,
        'reason1': reason1,
        'reason1_desc': reason1_desc
    }
    
    return report

def generate_sql_file(output_file='test_data_2022_massive.sql'):
    """生成SQL文件"""
    print("开始生成大规模测试数据...")
    
    # 生成企业
    print("正在生成企业数据...")
    enterprises = generate_enterprises()
    total_enterprises = len(enterprises)
    print(f"✓ 生成了 {total_enterprises} 家企业")
    
    # 打开文件
    with open(output_file, 'w', encoding='utf-8') as f:
        # 写入文件头
        f.write("""-- ====================================================================
-- 云南省就业数据管理系统 - 大规模测试数据
-- 生成时间: """ + datetime.now().strftime("%Y-%m-%d %H:%M:%S") + """
-- 数据规模: """ + str(total_enterprises) + """ 家企业 × 12个月 = """ + str(total_enterprises * 12) + """+ 条记录
-- 覆盖范围: 云南省16个地州市所有区县
-- ====================================================================

USE project1;
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ====================================================================
-- 1. 清理2022年旧数据（如果存在）
-- ====================================================================
DELETE FROM report_audit_history WHERE period_id IN (SELECT period_id FROM period_info WHERE investigate_time LIKE '2022%');
DELETE FROM enterprise_report_info WHERE period_id IN (SELECT period_id FROM period_info WHERE investigate_time LIKE '2022%');
DELETE FROM report_info WHERE report_id LIKE '%2022%';
DELETE FROM account_info WHERE username LIKE 'ent_%' OR username LIKE 'admin_%';
DELETE FROM enterprise_info;
DELETE FROM period_info WHERE investigate_time LIKE '2022%';

-- ====================================================================
-- 2. 调查期数据 (2022年全年12个月)
-- ====================================================================
""")
        
        # 生成调查期
        print("正在生成调查期数据...")
        for month in range(1, 13):
            start_time = f"2022-{month:02d}-10 08:00:00"
            end_time = f"2022-{month:02d}-25 23:59:59"
            created_at = f"2021-{12 if month == 1 else month-1:02d}-20 10:00:00"
            enterprise_count = total_enterprises - random.randint(0, 5)
            
            f.write(f"INSERT INTO period_info (investigate_time, period_start_time, period_end_time, enterprise_count, created_at, updated_at) VALUES\n")
            f.write(f"('{2022:04d}-{month:02d}', '{start_time}', '{end_time}', {enterprise_count}, '{created_at}', '{created_at}');\n\n")
        
        print("✓ 生成了 12 个调查期")
        
        # 获取period_id变量
        f.write("-- 获取调查期ID\n")
        for month in range(1, 13):
            f.write(f"SET @p2022{month:02d} = (SELECT period_id FROM period_info WHERE investigate_time = '2022-{month:02d}');\n")
        f.write("\n")
        
        # 生成企业信息
        f.write("""-- ====================================================================
-- 3. 企业信息数据 (""" + str(total_enterprises) + """ 家企业)
-- ====================================================================
""")
        
        print(f"正在生成 {total_enterprises} 家企业信息...")
        batch_size = 100
        for i in range(0, len(enterprises), batch_size):
            batch = enterprises[i:i+batch_size]
            f.write("INSERT INTO enterprise_info (enterprise_id, org_code, name, region, nature, industry, industry_desc, contact_name, address, postal_code, phone_num, fax_num, email, status, created_at, updated_at) VALUES\n")
            
            values = []
            for ent in batch:
                value = f"({escape_sql_string(ent['enterprise_id'])}, {escape_sql_string(ent['org_code'])}, {escape_sql_string(ent['name'])}, {ent['region']}, {ent['nature']}, {ent['industry']}, {escape_sql_string(ent['industry_desc'])}, {escape_sql_string(ent['contact_name'])}, {escape_sql_string(ent['address'])}, {escape_sql_string(ent['postal_code'])}, {escape_sql_string(ent['phone_num'])}, {escape_sql_string(ent['fax_num'])}, {escape_sql_string(ent['email'])}, {ent['status']}, {escape_sql_string(ent['created_at'])}, {escape_sql_string(ent['updated_at'])})"
                values.append(value)
            
            f.write(',\n'.join(values))
            f.write(';\n\n')
            
            if (i + batch_size) % 500 == 0:
                print(f"  已生成 {min(i + batch_size, len(enterprises))} / {len(enterprises)} 家企业...")
        
        print(f"✓ 完成企业信息生成")
        
        # 生成账号数据
        f.write("""-- ====================================================================
-- 4. 账号信息
-- ====================================================================
-- 省级账号
INSERT INTO account_info (username, password, type, enterprise_id, city_code, last_login_time, status, created_at) VALUES
('admin_province', '$2a$10$N.zmdr9k7uOIl6lD1xKWze', 3, NULL, NULL, '2022-12-20 08:00:00', 0, '2021-01-01 09:00:00');

-- 市级账号
INSERT INTO account_info (username, password, type, enterprise_id, city_code, last_login_time, status, created_at) VALUES
""")
        
        city_accounts = []
        for city_code, city_data in REGIONS.items():
            city_name_pinyin = {
                1: 'lincang', 2: 'lijiang', 3: 'baoshan', 4: 'puer', 5: 'kunming',
                6: 'zhaotong', 7: 'qujing', 8: 'yuxi', 9: 'wenshan', 10: 'honghe',
                11: 'xishuangbanna', 12: 'chuxiong', 13: 'dali', 14: 'dehong',
                15: 'nujiang', 16: 'diqing'
            }
            username = f"admin_{city_name_pinyin.get(city_code, f'city{city_code}')}"
            city_accounts.append(f"('{username}', '$2a$10$N.zmdr9k7uOIl6lD1xKWze', 2, NULL, {city_code}, '2022-12-20 08:00:00', 0, '2021-01-01 09:00:00')")
        
        f.write(',\n'.join(city_accounts))
        f.write(';\n\n')
        
        # 企业账号
        print("正在生成账号数据...")
        f.write("-- 企业账号\n")
        for i in range(0, len(enterprises), batch_size):
            batch = enterprises[i:i+batch_size]
            f.write("INSERT INTO account_info (username, password, type, enterprise_id, city_code, last_login_time, status, created_at) VALUES\n")
            
            account_values = []
            for ent in batch:
                username = f"ent_{ent['enterprise_id'][4:].lower().replace('_', '')}"
                account_values.append(f"('{username}', '$2a$10$N.zmdr9k7uOIl6lD1xKWze', 1, '{ent['enterprise_id']}', NULL, '2022-12-20 08:00:00', 0, '{ent['created_at']}')")
            
            f.write(',\n'.join(account_values))
            f.write(';\n\n')
        
        print("✓ 完成账号数据生成")
        
        # 生成报表数据
        f.write("""-- ====================================================================
-- 5. 报表数据 + 企业上报信息 (""" + str(total_enterprises * 12) + """ 条记录)
-- ====================================================================
""")
        
        print(f"正在生成 {total_enterprises * 12} 条报表数据...")
        report_count = 0
        
        for month in range(1, 13):
            print(f"  正在生成 2022-{month:02d} 的数据...")
            f.write(f"-- 2022年{month}月数据\n")
            
            # 分批处理企业
            for i in range(0, len(enterprises), batch_size):
                batch = enterprises[i:i+batch_size]
                
                # report_info
                f.write("INSERT INTO report_info (report_id, construction_count, investigation_count, reduction_type, reason1, reason1_desc, reason2, reason2_desc, reason3, reason3_desc, other_reason) VALUES\n")
                
                report_values = []
                for ent in batch:
                    report_id = f"RPT_{ent['enterprise_id'][4:]}_{2022}{month:02d}"
                    report = generate_reports_for_enterprise(ent, month)
                    
                    report_values.append(f"('{report_id}', {report['construction_count']}, {report['investigation_count']}, {report['reduction_type']}, {report['reason1']}, {escape_sql_string(report['reason1_desc'])}, NULL, NULL, NULL, NULL, NULL)")
                
                f.write(',\n'.join(report_values))
                f.write(';\n\n')
                
                # enterprise_report_info
                f.write("INSERT INTO enterprise_report_info (enterprise_id, period_id, report_id, old_report_id, reason_return, status, period_start_time, period_end_time, created_at, updated_at, enterprise_nature, enterprise_industry, enterprise_region) VALUES\n")
                
                ent_report_values = []
                for ent in batch:
                    report_id = f"RPT_{ent['enterprise_id'][4:]}_{2022}{month:02d}"
                    # 前10个月已归档(4)，后2个月审核通过(3)
                    status = 4 if month <= 10 else 3
                    
                    start_time = f"2022-{month:02d}-10 08:00:00"
                    end_time = f"2022-{month:02d}-25 23:59:59"
                    created = f"2022-{month:02d}-{random.randint(10,15):02d} {random.randint(8,18):02d}:00:00"
                    updated = f"2022-{month:02d}-{random.randint(16,23):02d} {random.randint(8,20):02d}:00:00"
                    
                    ent_report_values.append(f"('{ent['enterprise_id']}', @p2022{month:02d}, '{report_id}', NULL, NULL, {status}, '{start_time}', '{end_time}', '{created}', '{updated}', {ent['nature']}, {ent['industry']}, {ent['region']})")
                    report_count += 1
                
                f.write(',\n'.join(ent_report_values))
                f.write(';\n\n')
            
            if month % 3 == 0:
                print(f"  已完成 {month}/12 个月的数据生成，累计 {report_count} 条记录")
        
        print(f"✓ 完成 {report_count} 条报表数据生成")
        
        # 生成审核记录
        f.write("""-- ====================================================================
-- 6. 审核记录
-- ====================================================================
""")
        
        print("正在生成审核记录...")
        
        # 市级审核人映射
        city_auditor_map = {
            1: 'admin_lincang', 2: 'admin_lijiang', 3: 'admin_baoshan', 4: 'admin_puer',
            5: 'admin_kunming', 6: 'admin_zhaotong', 7: 'admin_qujing', 8: 'admin_yuxi',
            9: 'admin_wenshan', 10: 'admin_honghe', 11: 'admin_xishuangbanna',
            12: 'admin_chuxiong', 13: 'admin_dali', 14: 'admin_dehong',
            15: 'admin_nujiang', 16: 'admin_diqing'
        }
        
        for month in range(1, 13):
            f.write(f"-- 2022年{month}月审核记录\n")
            
            for i in range(0, len(enterprises), batch_size):
                batch = enterprises[i:i+batch_size]
                
                # 市级审核
                f.write("INSERT INTO report_audit_history (enterprise_id, period_id, report_id, audit_level, auditor, audit_result, audit_opinion, audit_time) VALUES\n")
                
                audit_values = []
                for ent in batch:
                    report_id = f"RPT_{ent['enterprise_id'][4:]}_{2022}{month:02d}"
                    # 根据企业地区确定审核人
                    city_code = ent['region'] // 100
                    auditor = city_auditor_map.get(city_code, 'admin_kunming')
                    
                    audit_time = f"2022-{month:02d}-{random.randint(16,20):02d} {random.randint(9,17):02d}:00:00"
                    audit_values.append(f"('{ent['enterprise_id']}', @p2022{month:02d}, '{report_id}', 1, '{auditor}', 1, '市级审核通过', '{audit_time}')")
                
                f.write(',\n'.join(audit_values))
                f.write(';\n\n')
                
                # 省级审核
                f.write("INSERT INTO report_audit_history (enterprise_id, period_id, report_id, audit_level, auditor, audit_result, audit_opinion, audit_time) VALUES\n")
                
                audit_values = []
                for ent in batch:
                    report_id = f"RPT_{ent['enterprise_id'][4:]}_{2022}{month:02d}"
                    audit_time = f"2022-{month:02d}-{random.randint(21,24):02d} {random.randint(9,17):02d}:00:00"
                    audit_values.append(f"('{ent['enterprise_id']}', @p2022{month:02d}, '{report_id}', 2, 'admin_province', 1, '省级审核通过', '{audit_time}')")
                
                f.write(',\n'.join(audit_values))
                f.write(';\n\n')
            
            if month % 4 == 0:
                print(f"  已完成 {month}/12 个月的审核记录")
        
        print("✓ 完成审核记录生成")
        
        # 数据统计
        f.write("""-- ====================================================================
-- 7. 数据统计
-- ====================================================================
SELECT '=== 大规模测试数据统计 ===' AS info;
SELECT COUNT(*) AS total_periods, '调查期总数' AS description FROM period_info WHERE investigate_time LIKE '2022%';
SELECT COUNT(*) AS total_enterprises, '企业总数' AS description FROM enterprise_info WHERE status = 3;
SELECT COUNT(*) AS total_reports, '报表总数' AS description FROM report_info WHERE report_id LIKE '%2022%';
SELECT COUNT(*) AS total_enterprise_reports, '企业上报总数' AS description FROM enterprise_report_info;
SELECT COUNT(*) AS total_audits, '审核记录总数' AS description FROM report_audit_history;

SELECT status, COUNT(*) AS count,
    CASE status 
        WHEN 3 THEN '审核通过' 
        WHEN 4 THEN '已归档'
        ELSE '其他'
    END AS status_desc
FROM enterprise_report_info
GROUP BY status;

SELECT '按地区统计企业数' AS info;
SELECT region DIV 100 AS city_code, COUNT(*) AS enterprise_count
FROM enterprise_info
GROUP BY region DIV 100
ORDER BY city_code;

SET FOREIGN_KEY_CHECKS = 1;

SELECT '=== 大规模测试数据导入完成！===' AS result;
SELECT '总企业数: """ + str(total_enterprises) + """' AS summary;
SELECT '总报表数: """ + str(total_enterprises * 12) + """' AS summary;
SELECT '总审核记录: """ + str(total_enterprises * 12 * 2) + """' AS summary;
""")
    
    print(f"\n{'='*60}")
    print(f"✅ SQL文件生成完成！")
    print(f"{'='*60}")
    print(f"文件路径: {output_file}")
    print(f"企业总数: {total_enterprises}")
    print(f"报表总数: {total_enterprises * 12}")
    print(f"审核记录: {total_enterprises * 12 * 2}")
    print(f"数据总量: {total_enterprises * 12 + total_enterprises * 12 * 2 + total_enterprises} 条")
    print(f"{'='*60}")

if __name__ == '__main__':
    import os
    os.chdir(os.path.dirname(os.path.abspath(__file__)))
    generate_sql_file('test_data_2022_massive.sql')
    print("\n执行以下命令导入数据:")
    print("mysql -u root -p project1 < test_data_2022_massive.sql")


#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
使用正确的地区、性质、行业代码生成大规模测试数据
基于真实的 JSON 字典文件
"""

import json
import random
from datetime import datetime

# 从JSON文件加载真实的代码
def load_json_codes(file_path):
    """加载JSON文件并提取所有有效的代码"""
    with open(file_path, 'r', encoding='utf-8') as f:
        data = json.load(f)
    return data

def extract_all_codes(data, level=1):
    """递归提取所有代码（包括父级和子级）"""
    codes = []
    for item in data:
        if level == 1:  # 只要二级代码（子类）
            if 'children' in item and item['children']:
                for child in item['children']:
                    codes.append({
                        'code': child['code'],
                        'name': child['name'],
                        'parent_name': item['name']
                    })
        else:  # 获取所有级别
            codes.append({'code': item['code'], 'name': item['name']})
            if 'children' in item and item['children']:
                codes.extend(extract_all_codes(item['children'], level=level))
    return codes

def extract_region_districts(data):
    """提取地区的区县代码（二级）"""
    districts = []
    for city in data:
        if 'children' in city and city['children']:
            for district in city['children']:
                districts.append({
                    'code': district['code'],
                    'name': district['name'],
                    'city_name': city['name'],
                    'city_code': city['code']
                })
    return districts

# 常见姓氏和名字
SURNAMES = ['李', '王', '张', '刘', '陈', '杨', '黄', '赵', '周', '吴', '徐', '孙', '马', '朱', '胡', '郭', '何', '高', '林', '罗']
GIVEN_NAMES = ['伟', '芳', '娜', '秀英', '敏', '静', '丽', '强', '磊', '军', '洋', '勇', '艳', '杰', '娟', '涛', '明', '超', '秀兰', '霞']

# 企业名称后缀
COMPANY_SUFFIXES = [
    '有限公司', '股份有限公司', '集团有限公司', '实业有限公司',
    '科技有限公司', '商贸有限公司', '建筑工程有限公司',
    '农业开发有限公司', '旅游开发有限公司', '物流有限公司',
    '文化传播有限公司', '环保科技有限公司', '制造有限公司'
]

COMPANY_PREFIXES = [
    '云', '滇', '彩云', '七彩', '高原', '金', '银', '翠', '玉',
    '龙', '凤', '雄', '峰', '绿', '蓝', '红', '春', '新', '华',
    '盛', '兴', '泰', '安', '康', '富', '强', '美'
]

def generate_name():
    """生成随机姓名"""
    return random.choice(SURNAMES) + random.choice(GIVEN_NAMES)

def generate_company_name(region_name):
    """生成企业名称"""
    prefix = random.choice(COMPANY_PREFIXES)
    suffix = random.choice(COMPANY_SUFFIXES)
    if random.random() < 0.3:
        return f"{region_name}{prefix}{suffix}"
    else:
        return f"{prefix}{suffix}"

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

def generate_enterprises(regions, natures, industries, total_count=1200):
    """生成企业数据"""
    enterprises = []
    
    for i in range(total_count):
        # 随机选择地区、性质、行业
        region_info = random.choice(regions)
        nature_info = random.choice(natures)
        industry_info = random.choice(industries)
        
        # 企业ID
        enterprise_id = f"ENT_{region_info['city_code']:02d}_{region_info['code']:04d}_{i+1:04d}"
        org_code = generate_org_code()
        name = generate_company_name(region_info['city_name'])
        contact_name = generate_name()
        
        # 地址
        address = f"{region_info['city_name']}{region_info['name']}{random.randint(1, 999)}号"
        postal_code = f"6{random.randint(50000, 79999)}"
        
        # 电话
        area_codes = ['0871', '0873', '0874', '0875', '0876', '0877', '0878', '0879', '0883', '0886', '0887', '0888', '0691']
        phone = f"{random.choice(area_codes)}-{random.randint(2000000, 9999999)}"
        fax = f"{random.choice(area_codes)}-{random.randint(2000000, 9999999)}"
        
        email = f"contact{i+1}@company{i+1}.com"
        
        # 创建时间
        created_month = random.randint(1, 12)
        created_day = random.randint(1, 28)
        created_at = f"2021-{created_month:02d}-{created_day:02d} {random.randint(8,17):02d}:00:00"
        
        enterprise = {
            'enterprise_id': enterprise_id,
            'org_code': org_code,
            'name': name,
            'region': region_info['code'],
            'nature': nature_info['code'],
            'industry': industry_info['code'],
            'industry_desc': f"{industry_info['parent_name']} - {industry_info['name']}",
            'contact_name': contact_name,
            'address': address,
            'postal_code': postal_code,
            'phone_num': phone,
            'fax_num': fax,
            'email': email,
            'status': 3,
            'created_at': created_at,
            'updated_at': created_at
        }
        
        enterprises.append(enterprise)
    
    return enterprises

def generate_reports_for_enterprise(enterprise, period_month, nature_info):
    """为企业生成某月的报表"""
    # 根据性质确定基础人数
    nature_code = enterprise['nature']
    
    # 国有企业人数多
    if nature_code in [101, 102, 103, 104]:  # 国有企业子类
        base_count = random.randint(200, 800)
    elif nature_code in [301, 302]:  # 私营大公司
        base_count = random.randint(80, 300)
    elif nature_code in [601, 602]:  # 股份制
        base_count = random.randint(100, 400)
    else:
        base_count = random.randint(20, 150)
    
    # 2022年变化
    if period_month <= 6:
        variation = random.randint(-30, 20)
    else:
        variation = random.randint(-10, 40)
    
    construction_count = base_count
    investigation_count = max(10, base_count + variation)
    
    # 确定类型和原因
    if investigation_count < construction_count:
        reduction_type = 2
        reason1 = random.choice([201, 202, 203])
        reason1_desc = f"人员减少{construction_count - investigation_count}人"
    elif investigation_count > construction_count:
        reduction_type = 1
        reason1 = random.choice([101, 102, 103])
        reason1_desc = f"业务扩展，新增{investigation_count - construction_count}人"
    else:
        reduction_type = 3
        reason1 = 301
        reason1_desc = "人员稳定"
    
    return {
        'construction_count': construction_count,
        'investigation_count': investigation_count,
        'reduction_type': reduction_type,
        'reason1': reason1,
        'reason1_desc': reason1_desc
    }

def generate_sql_file(output_file='test_data_2022_corrected.sql'):
    """生成SQL文件"""
    print("="*60)
    print("开始生成使用正确代码的大规模测试数据")
    print("="*60)
    
    # 加载JSON文件
    print("\n1. 加载JSON字典文件...")
    
    # 相对路径
    import os
    base_dir = os.path.dirname(__file__)
    
    # 地区代码
    region_file = os.path.join(base_dir, '../frontend/yunnan-province-web/src/dict/yunnan_region_code.json')
    region_data = load_json_codes(region_file)
    regions = extract_region_districts(region_data)
    print(f"   ✓ 加载了 {len(regions)} 个区县地区代码")
    
    # 企业性质
    nature_file = os.path.join(base_dir, '../frontend/yunnan-enterprise-web/src/constants/enterprise_types.json')
    nature_data = load_json_codes(nature_file)
    natures = extract_all_codes(nature_data, level=1)
    print(f"   ✓ 加载了 {len(natures)} 个企业性质代码")
    
    # 企业行业
    industry_file = os.path.join(base_dir, '../frontend/yunnan-enterprise-web/src/constants/enterprise_industries.json')
    industry_data = load_json_codes(industry_file)
    industries = extract_all_codes(industry_data, level=1)
    print(f"   ✓ 加载了 {len(industries)} 个行业代码")
    
    # 生成企业
    print("\n2. 生成企业数据...")
    enterprises = generate_enterprises(regions, natures, industries, total_count=1200)
    print(f"   ✓ 生成了 {len(enterprises)} 家企业")
    
    # 开始写SQL
    print(f"\n3. 写入SQL文件: {output_file}")
    
    with open(output_file, 'w', encoding='utf-8') as f:
        # 文件头
        f.write("""-- ====================================================================
-- 云南省就业数据管理系统 - 大规模测试数据（使用正确代码）
-- 生成时间: """ + datetime.now().strftime("%Y-%m-%d %H:%M:%S") + """
-- 数据规模: 1200 家企业 × 12个月 = 14400+ 条记录
-- 代码来源: yunnan_region_code.json, enterprise_types.json, enterprise_industries.json
-- ====================================================================

USE project1;
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ====================================================================
-- 1. 清理旧数据
-- ====================================================================
DELETE FROM report_audit_history WHERE period_id IN (SELECT period_id FROM period_info WHERE investigate_time LIKE '2022%');
DELETE FROM enterprise_report_info WHERE period_id IN (SELECT period_id FROM period_info WHERE investigate_time LIKE '2022%');
DELETE FROM report_info WHERE report_id LIKE '%2022%';
DELETE FROM account_info WHERE username LIKE 'ent_%' OR username LIKE 'admin_%';
DELETE FROM enterprise_info;
DELETE FROM period_info WHERE investigate_time LIKE '2022%';

-- ====================================================================
-- 2. 调查期数据 (2022年全年)
-- ====================================================================
""")
        
        # 调查期
        print("   - 生成调查期数据...")
        for month in range(1, 13):
            start_time = f"2022-{month:02d}-10 08:00:00"
            end_time = f"2022-{month:02d}-25 23:59:59"
            created_at = f"2021-{12 if month == 1 else month-1:02d}-20 10:00:00"
            f.write(f"INSERT INTO period_info (investigate_time, period_start_time, period_end_time, enterprise_count, created_at, updated_at) VALUES\n")
            f.write(f"('{2022:04d}-{month:02d}', '{start_time}', '{end_time}', 1200, '{created_at}', '{created_at}');\n\n")
        
        # 获取period_id
        f.write("-- 获取调查期ID\n")
        for month in range(1, 13):
            f.write(f"SET @p2022{month:02d} = (SELECT period_id FROM period_info WHERE investigate_time = '2022-{month:02d}');\n")
        f.write("\n")
        
        # 企业信息
        f.write(f"""-- ====================================================================
-- 3. 企业信息数据 ({len(enterprises)} 家企业)
-- ====================================================================
""")
        
        print("   - 生成企业信息...")
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
        
        # 账号
        f.write("""-- ====================================================================
-- 4. 账号信息
-- ====================================================================
-- 省级账号
INSERT INTO account_info (username, password, type, enterprise_id, city_code, last_login_time, status, created_at) VALUES
('admin_province', '$2a$10$N.zmdr9k7uOIl6lD1xKWze', 3, NULL, NULL, '2022-12-20 08:00:00', 0, '2021-01-01 09:00:00');

-- 市级账号
INSERT INTO account_info (username, password, type, enterprise_id, city_code, last_login_time, status, created_at) VALUES
('admin_lincang', '$2a$10$N.zmdr9k7uOIl6lD1xKWze', 2, NULL, 1, '2022-12-20 08:00:00', 0, '2021-01-01 09:00:00'),
('admin_lijiang', '$2a$10$N.zmdr9k7uOIl6lD1xKWze', 2, NULL, 2, '2022-12-20 08:00:00', 0, '2021-01-01 09:00:00'),
('admin_baoshan', '$2a$10$N.zmdr9k7uOIl6lD1xKWze', 2, NULL, 3, '2022-12-20 08:00:00', 0, '2021-01-01 09:00:00'),
('admin_puer', '$2a$10$N.zmdr9k7uOIl6lD1xKWze', 2, NULL, 4, '2022-12-20 08:00:00', 0, '2021-01-01 09:00:00'),
('admin_kunming', '$2a$10$N.zmdr9k7uOIl6lD1xKWze', 2, NULL, 5, '2022-12-20 08:00:00', 0, '2021-01-01 09:00:00'),
('admin_zhaotong', '$2a$10$N.zmdr9k7uOIl6lD1xKWze', 2, NULL, 6, '2022-12-20 08:00:00', 0, '2021-01-01 09:00:00'),
('admin_qujing', '$2a$10$N.zmdr9k7uOIl6lD1xKWze', 2, NULL, 7, '2022-12-20 08:00:00', 0, '2021-01-01 09:00:00'),
('admin_yuxi', '$2a$10$N.zmdr9k7uOIl6lD1xKWze', 2, NULL, 8, '2022-12-20 08:00:00', 0, '2021-01-01 09:00:00'),
('admin_wenshan', '$2a$10$N.zmdr9k7uOIl6lD1xKWze', 2, NULL, 9, '2022-12-20 08:00:00', 0, '2021-01-01 09:00:00'),
('admin_honghe', '$2a$10$N.zmdr9k7uOIl6lD1xKWze', 2, NULL, 10, '2022-12-20 08:00:00', 0, '2021-01-01 09:00:00'),
('admin_xishuangbanna', '$2a$10$N.zmdr9k7uOIl6lD1xKWze', 2, NULL, 11, '2022-12-20 08:00:00', 0, '2021-01-01 09:00:00'),
('admin_chuxiong', '$2a$10$N.zmdr9k7uOIl6lD1xKWze', 2, NULL, 12, '2022-12-20 08:00:00', 0, '2021-01-01 09:00:00'),
('admin_dali', '$2a$10$N.zmdr9k7uOIl6lD1xKWze', 2, NULL, 13, '2022-12-20 08:00:00', 0, '2021-01-01 09:00:00'),
('admin_dehong', '$2a$10$N.zmdr9k7uOIl6lD1xKWze', 2, NULL, 14, '2022-12-20 08:00:00', 0, '2021-01-01 09:00:00'),
('admin_nujiang', '$2a$10$N.zmdr9k7uOIl6lD1xKWze', 2, NULL, 15, '2022-12-20 08:00:00', 0, '2021-01-01 09:00:00'),
('admin_diqing', '$2a$10$N.zmdr9k7uOIl6lD1xKWze', 2, NULL, 16, '2022-12-20 08:00:00', 0, '2021-01-01 09:00:00');

-- 企业账号
""")
        
        print("   - 生成账号数据...")
        for i in range(0, len(enterprises), batch_size):
            batch = enterprises[i:i+batch_size]
            f.write("INSERT INTO account_info (username, password, type, enterprise_id, city_code, last_login_time, status, created_at) VALUES\n")
            
            account_values = []
            for ent in batch:
                username = f"ent_{ent['enterprise_id'][4:].lower().replace('_', '')}"
                account_values.append(f"('{username}', '$2a$10$N.zmdr9k7uOIl6lD1xKWze', 1, '{ent['enterprise_id']}', NULL, '2022-12-20 08:00:00', 0, '{ent['created_at']}')")
            
            f.write(',\n'.join(account_values))
            f.write(';\n\n')
        
        # 报表数据
        f.write(f"""-- ====================================================================
-- 5. 报表数据 + 企业上报信息 (14400 条记录)
-- ====================================================================
""")
        
        print("   - 生成报表数据...")
        report_count = 0
        
        # 创建性质映射
        nature_map = {n['code']: n for n in natures}
        
        for month in range(1, 13):
            f.write(f"-- 2022年{month}月数据\n")
            
            for i in range(0, len(enterprises), batch_size):
                batch = enterprises[i:i+batch_size]
                
                # report_info
                f.write("INSERT INTO report_info (report_id, construction_count, investigation_count, reduction_type, reason1, reason1_desc, reason2, reason2_desc, reason3, reason3_desc, other_reason) VALUES\n")
                
                report_values = []
                for ent in batch:
                    report_id = f"RPT_{ent['enterprise_id'][4:]}_{2022}{month:02d}"
                    nature_info = nature_map.get(ent['nature'], {})
                    report = generate_reports_for_enterprise(ent, month, nature_info)
                    
                    report_values.append(f"('{report_id}', {report['construction_count']}, {report['investigation_count']}, {report['reduction_type']}, {report['reason1']}, {escape_sql_string(report['reason1_desc'])}, NULL, NULL, NULL, NULL, NULL)")
                
                f.write(',\n'.join(report_values))
                f.write(';\n\n')
                
                # enterprise_report_info
                f.write("INSERT INTO enterprise_report_info (enterprise_id, period_id, report_id, old_report_id, reason_return, status, period_start_time, period_end_time, created_at, updated_at, enterprise_nature, enterprise_industry, enterprise_region) VALUES\n")
                
                ent_report_values = []
                for ent in batch:
                    report_id = f"RPT_{ent['enterprise_id'][4:]}_{2022}{month:02d}"
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
                print(f"      已完成 {month}/12 个月")
        
        print(f"   ✓ 生成了 {report_count} 条报表记录")
        
        # 审核记录
        f.write("""-- ====================================================================
-- 6. 审核记录
-- ====================================================================
""")
        
        print("   - 生成审核记录...")
        
        city_auditor_map = {
            1: 'admin_lincang', 2: 'admin_lijiang', 3: 'admin_baoshan', 4: 'admin_puer',
            5: 'admin_kunming', 6: 'admin_zhaotong', 7: 'admin_qujing', 8: 'admin_yuxi',
            9: 'admin_wenshan', 10: 'admin_honghe', 11: 'admin_xishuangbanna',
            12: 'admin_chuxiong', 13: 'admin_dali', 14: 'admin_dehong',
            15: 'admin_nujiang', 16: 'admin_diqing'
        }
        
        for month in range(1, 13):
            for i in range(0, len(enterprises), batch_size):
                batch = enterprises[i:i+batch_size]
                
                # 市级审核
                f.write("INSERT INTO report_audit_history (enterprise_id, period_id, report_id, audit_level, auditor, audit_result, audit_opinion, audit_time) VALUES\n")
                
                audit_values = []
                for ent in batch:
                    report_id = f"RPT_{ent['enterprise_id'][4:]}_{2022}{month:02d}"
                    # 根据地区代码前面几位判断城市
                    city_code = ent['region'] // 100 if ent['region'] >= 100 else ent['region']
                    if city_code > 16:
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
        
        print("   ✓ 生成了 28800 条审核记录")
        
        # 统计
        f.write("""-- ====================================================================
-- 7. 数据统计验证
-- ====================================================================
SELECT '=== 数据导入完成 ===' AS result;
SELECT COUNT(*) AS '企业总数' FROM enterprise_info;
SELECT COUNT(*) AS '调查期数' FROM period_info WHERE investigate_time LIKE '2022%';
SELECT COUNT(*) AS '报表总数' FROM enterprise_report_info;
SELECT COUNT(*) AS '审核记录数' FROM report_audit_history;

SELECT '按状态统计:' AS info;
SELECT status, COUNT(*) AS count,
    CASE status 
        WHEN 3 THEN '审核通过' 
        WHEN 4 THEN '已归档'
    END AS status_name
FROM enterprise_report_info
GROUP BY status;

SET FOREIGN_KEY_CHECKS = 1;
""")
    
    print(f"\n{'='*60}")
    print("✅ SQL文件生成完成！")
    print(f"{'='*60}")
    print(f"文件路径: {output_file}")
    print(f"企业总数: 1200")
    print(f"报表总数: 14400")
    print(f"审核记录: 28800")
    print(f"{'='*60}\n")

if __name__ == '__main__':
    import os
    os.chdir(os.path.dirname(os.path.abspath(__file__)))
    generate_sql_file('test_data_2022_corrected.sql')
    print("执行以下命令导入数据:")
    print("mysql -u root -p project1 < test_data_2022_corrected.sql")


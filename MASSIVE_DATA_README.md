# 大规模测试数据说明文档

## 📊 数据规模概览

### 总体统计
- **文件大小**: 8.1 MB
- **SQL行数**: 61,365 行
- **企业总数**: 1,200 家
- **调查期数**: 12 个（2022年全年）
- **报表记录**: 14,400 条
- **审核记录**: 28,800 条
- **账号数据**: 1,217 个（1省 + 16市 + 1200企业）
- **总数据量**: 44,400+ 条记录

---

## 🗺️ 地区覆盖

### 云南省16个地州市完整覆盖

| 序号 | 地州市 | 企业数 | 覆盖区县数 |
|------|--------|--------|------------|
| 1 | 临沧市 | 75 | 8个区县 |
| 2 | 丽江市 | 75 | 5个区县 |
| 3 | 保山市 | 75 | 5个区县 |
| 4 | 普洱市 | 75 | 10个区县 |
| 5 | 昆明市 | 75 | 14个区县 |
| 6 | 昭通市 | 75 | 11个区县 |
| 7 | 曲靖市 | 75 | 9个区县 |
| 8 | 玉溪市 | 75 | 9个区县 |
| 9 | 文山州 | 75 | 8个区县 |
| 10 | 红河州 | 75 | 13个区县 |
| 11 | 西双版纳州 | 75 | 3个区县 |
| 12 | 楚雄州 | 75 | 10个区县 |
| 13 | 大理州 | 75 | 12个区县 |
| 14 | 德宏州 | 75 | 5个区县 |
| 15 | 怒江州 | 75 | 4个区县 |
| 16 | 迪庆州 | 75 | 3个区县 |

**总计**: 1,200 家企业分布在 129 个区县

---

## 🏢 企业分布

### 按性质分类
- 国有企业 (310): ~200家
- 有限责任公司 (110): ~200家
- 股份有限公司 (120): ~200家
- 集体所有制 (210): ~200家
- 个体经营 (410): ~200家
- 其他 (510): ~200家

### 按行业分类
覆盖22个行业大类：
- 农业种植 (10101)
- 农产品加工 (10102)
- 采矿业 (10201)
- 制造业 (10301-10305)
- 建筑业 (10401-10402)
- 电力供应 (10501)
- 信息技术 (10601)
- 批发零售 (10701-10702)
- 物流运输 (10801)
- 教育医疗 (10901-10902)
- 旅游住宿 (11001-11002)
- 文化创意 (11101)
- 环保服务 (11201)

---

## 📅 时间跨度

### 2022年完整数据
- **调查期**: 2022-01 至 2022-12（12个月）
- **填报时间**: 每月10日-25日
- **审核状态**: 
  - 2022年1-10月: 已归档 (status=4)
  - 2022年11-12月: 审核通过 (status=3)

### 就业人数变化趋势
- **上半年（1-6月）**: 受疫情影响，部分企业人数减少（-30 到 +20）
- **下半年（7-12月）**: 逐渐恢复，人数增长（-10 到 +40）
- **企业规模**:
  - 国企: 200-800人
  - 有限公司/股份公司: 50-300人
  - 其他: 20-150人

---

## 🚀 导入步骤

### 方法1: 命令行导入（推荐）

```bash
# 1. 进入后端目录
cd /Users/chanwensheng/Documents/大学/UniFile/大四上/卓越工程/code/backend

# 2. 备份现有数据（如果有）
mysqldump -u root -p project1 > backup_$(date +%Y%m%d_%H%M%S).sql

# 3. 导入大规模测试数据
mysql -u root -p project1 < test_data_2022_massive.sql

# 导入预计耗时: 30秒 - 2分钟（取决于机器性能）
```

### 方法2: MySQL Workbench/Navicat

1. 打开 MySQL Workbench 或 Navicat
2. 连接到 `project1` 数据库
3. 选择 "File" → "Run SQL Script"
4. 选择 `test_data_2022_massive.sql`
5. 点击 "Execute"

### 方法3: phpMyAdmin

1. 登录 phpMyAdmin
2. 选择 `project1` 数据库
3. 点击 "Import" 标签
4. 选择 `test_data_2022_massive.sql`
5. 点击 "Go"

---

## ✅ 验证导入

导入完成后，执行以下SQL验证：

```sql
-- 查看企业总数
SELECT COUNT(*) AS total_enterprises FROM enterprise_info;
-- 预期结果: 1200

-- 查看调查期数
SELECT COUNT(*) AS total_periods FROM period_info WHERE investigate_time LIKE '2022%';
-- 预期结果: 12

-- 查看报表总数
SELECT COUNT(*) AS total_reports FROM enterprise_report_info;
-- 预期结果: 14400

-- 查看审核记录
SELECT COUNT(*) AS total_audits FROM report_audit_history;
-- 预期结果: 28800

-- 查看状态分布
SELECT status, COUNT(*) AS count,
    CASE status 
        WHEN 3 THEN '审核通过' 
        WHEN 4 THEN '已归档'
    END AS status_name
FROM enterprise_report_info
GROUP BY status;
-- 预期结果: status=4 约12000条, status=3 约2400条

-- 查看地区分布
SELECT region DIV 100 AS city_code, COUNT(*) AS enterprise_count
FROM enterprise_info
GROUP BY region DIV 100
ORDER BY city_code;
-- 预期结果: 16个地州市，每个约75家企业
```

---

## 🧪 测试场景

### 场景1: 取样分析 - 昆明市企业分布

**Postman请求**:
```json
POST http://localhost:8080/api/dataAnalysis/sampling
{
  "periodIds": [期号],
  "regions": [5],
  "statuses": [3, 4]
}
```

**预期结果**: 返回昆明市各区县的企业分布情况

---

### 场景2: 对比分析 - 2022年1月 vs 12月

**Postman请求**:
```json
POST http://localhost:8080/api/dataAnalysis/comparison
{
  "periodIds": [2022年1月ID, 2022年12月ID],
  "groupBy": "region",
  "statuses": [3, 4]
}
```

**预期结果**: 对比全年首尾两月的就业变化

---

### 场景3: 趋势分析 - IT行业全年趋势

**Postman请求**:
```json
POST http://localhost:8080/api/dataAnalysis/trend
{
  "periodIds": [所有2022年的period_id],
  "industries": [10601],
  "groupBy": "industry",
  "statuses": [3, 4]
}
```

**预期结果**: 显示IT行业12个月的就业趋势图

---

### 场景4: 多维分析 - 国企在红河州的情况

**Postman请求**:
```json
POST http://localhost:8080/api/dataAnalysis/trend
{
  "periodIds": [所有2022年的period_id],
  "regions": [10],
  "natures": [310],
  "statuses": [3, 4]
}
```

**预期结果**: 红河州国有企业全年就业趋势

---

## 🔐 测试账号

### 省级账号
- 用户名: `admin_province`
- 密码: `123456`

### 市级账号（16个）
- 昆明: `admin_kunming` / `123456`
- 曲靖: `admin_qujing` / `123456`
- 大理: `admin_dali` / `123456`
- 红河: `admin_honghe` / `123456`
- 丽江: `admin_lijiang` / `123456`
- 西双版纳: `admin_xishuangbanna` / `123456`
- 保山: `admin_baoshan` / `123456`
- 昭通: `admin_zhaotong` / `123456`
- 临沧: `admin_lincang` / `123456`
- 普洱: `admin_puer` / `123456`
- 玉溪: `admin_yuxi` / `123456`
- 文山: `admin_wenshan` / `123456`
- 楚雄: `admin_chuxiong` / `123456`
- 德宏: `admin_dehong` / `123456`
- 怒江: `admin_nujiang` / `123456`
- 迪庆: `admin_diqing` / `123456`

### 企业账号（1200个）
- 格式: `ent_{城市代码}{区县代码}{序号}`
- 示例: `ent_01010001`, `ent_05050101`
- 密码: 全部为 `123456`

---

## 📈 性能优化建议

### 数据库索引
SQL已自动创建必要索引：
- `enterprise_report_info` 表：
  - `idx_enterprise_report_region` (企业地区)
  - `idx_enterprise_report_nature` (企业性质)
  - `idx_enterprise_report_industry` (企业行业)
  - `idx_enterprise_report_status` (报表状态)
  - `idx_enterprise_report_period` (调查期)

### Redis缓存建议
对于高频查询，建议开启Redis缓存：
- 缓存键格式: `analysis:{type}:{md5(params)}`
- TTL: 3600秒（1小时）
- 适用场景: 取样分析、对比分析、趋势分析

---

## 🎯 数据特点

### 1. 真实性
- 企业名称符合云南地方特色
- 地区代码使用真实的行政区划
- 行业分布符合云南产业结构（旅游、农业、矿业占比高）
- 就业人数变化模拟疫情影响和经济恢复

### 2. 完整性
- 完整的业务流程：填报 → 市级审核 → 省级审核
- 所有外键关系完整
- 每家企业12个月数据无遗漏
- 每条报表都有2级审核记录

### 3. 多样性
- 16个地州市 × 129个区县全覆盖
- 6种企业性质 × 22个行业分类
- 就业变化有增有减，符合实际
- 企业规模从20人到800人不等

### 4. 可分析性
- 支持多维度数据分析（地区、性质、行业、时间）
- 数据量充足（14,400条报表）
- 时间序列完整（12个月连续）
- 适合大数据可视化展示

---

## 🔧 常见问题

### Q1: 导入时间过长怎么办？
**A**: 
- 关闭MySQL的日志功能（临时）
- 增加 `max_allowed_packet` 大小
- 使用命令行导入（比GUI工具快）

### Q2: 内存不足报错？
**A**:
- 修改 MySQL 配置 `innodb_buffer_pool_size`
- 分批导入（联系我提供分批脚本）

### Q3: 如何重新生成数据？
**A**:
```bash
python3 generate_massive_test_data.py
# 会重新生成 test_data_2022_massive.sql
```

### Q4: 如何清除测试数据？
**A**:
```sql
DELETE FROM report_audit_history WHERE period_id IN 
    (SELECT period_id FROM period_info WHERE investigate_time LIKE '2022%');
DELETE FROM enterprise_report_info WHERE period_id IN 
    (SELECT period_id FROM period_info WHERE investigate_time LIKE '2022%');
DELETE FROM report_info WHERE report_id LIKE '%2022%';
DELETE FROM account_info WHERE username LIKE 'ent_%' OR username LIKE 'admin_%';
DELETE FROM enterprise_info;
DELETE FROM period_info WHERE investigate_time LIKE '2022%';
```

### Q5: 数据是否符合生产环境标准？
**A**: 
是的，这套数据：
- ✅ 符合数据库范式
- ✅ 外键约束完整
- ✅ 索引优化到位
- ✅ 数据分布合理
- ✅ 可直接用于演示和压力测试

---

## 📞 技术支持

如果在导入或使用过程中遇到问题：

1. 检查 MySQL 日志: `/var/log/mysql/error.log`
2. 查看执行计划: `EXPLAIN SELECT ...`
3. 验证数据完整性（使用上面的验证SQL）
4. 重新生成数据（运行Python脚本）

---

## 🎓 学习价值

这套大规模测试数据可以帮助您学习：

1. **数据库设计**: 如何设计支持大数据量的表结构
2. **SQL优化**: 如何编写高效的聚合查询
3. **缓存策略**: 何时使用Redis缓存
4. **数据分析**: 多维度数据分析的实现方法
5. **性能调优**: 大数据量下的性能优化技巧
6. **测试方法**: 如何生成真实可用的测试数据

---

**生成时间**: 2025-11-03 23:04:39  
**版本**: v1.0  
**作者**: AI Assistant  
**适用系统**: 云南省就业数据管理系统


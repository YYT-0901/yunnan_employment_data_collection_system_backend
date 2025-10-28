# 企业数据填报系统 - 完整编码指南

> 作者：AI + 你的团队  
> 日期：2025-01-27  
> 目标：帮助新手理解整个系统的设计思路和编码逻辑

---

## 📚 目录

1. [系统架构设计](#1-系统架构设计)
2. [数据流转过程](#2-数据流转过程)
3. [核心功能实现](#3-核心功能实现)
4. [代码结构说明](#4-代码结构说明)
5. [接口文档](#5-接口文档)
6. [常见问题](#6-常见问题)

---

## 1. 系统架构设计

### 1.1 三层架构（老手的标准做法）

```
┌─────────────────────────────────────────┐
│          前端（Vue3）                    │
│  - 页面展示                              │
│  - 用户交互                              │
│  - 调用后端API                           │
└───────────────┬─────────────────────────┘
                │ HTTP请求（JSON）
                ↓
┌─────────────────────────────────────────┐
│    Controller 层（处理HTTP请求）         │
│  - 参数校验                              │
│  - 权限验证（从Token获取enterprise_id）  │
│  - 调用Service层                         │
│  - 返回ResponseVO                        │
└───────────────┬─────────────────────────┘
                │
                ↓
┌─────────────────────────────────────────┐
│    Service 层（业务逻辑）                │
│  - 窗口时间检查                          │
│  - 数据校验                              │
│  - 状态流转                              │
│  - 事务管理                              │
│  - 调用Mapper操作数据库                  │
└───────────────┬─────────────────────────┘
                │
                ↓
┌─────────────────────────────────────────┐
│    Mapper 层（数据访问）                 │
│  - SQL操作                               │
│  - MyBatis映射                           │
└───────────────┬─────────────────────────┘
                │
                ↓
┌─────────────────────────────────────────┐
│           MySQL 数据库                   │
└─────────────────────────────────────────┘
```

### 1.2 为什么要这样分层？

**老手的思考**：
- **Controller层薄**：只处理HTTP相关的事情，不写业务逻辑
- **Service层厚**：所有业务逻辑都在这里，方便单元测试
- **Mapper层纯**：只做数据库操作，不掺杂业务逻辑

**好处**：
1. 职责清晰，代码好维护
2. 可以单独测试每一层
3. 修改业务逻辑时不影响其他层

---

## 2. 数据流转过程

### 2.1 企业填报流程示例

让我们跟踪一个完整的请求：**企业提交报表**

```
步骤1：前端发起请求
POST /api/enterprise/report/submit
Headers: {
  "Cookie": "token=xxx",
  "Idempotency-Key": "uuid-123"
}
Body: {
  "reporting_period": "2025-01",
  "initial_employees": 100,
  "current_employees": 95,
  "reduction_type_code": "natural_attrition",
  ...
}

          ↓

步骤2：Controller 层处理
1. 从Cookie获取token
2. 从Redis获取TokenInfoVO
3. 提取enterprise_id = "ENT001"
4. 校验幂等键是否存在
5. 调用 Service.submit(command, idempotencyKey)

          ↓

步骤3：Service 层处理
1. 检查Redis幂等键（防重复）
2. 查询period_info，检查窗口时间
3. 调用原有submit()方法
   a. 使用DictionaryService转换code→id
   b. 保存report_info
   c. 更新enterprise_report_info status=1
4. 记录幂等键到Redis（24小时有效）

          ↓

步骤4：Mapper 层执行SQL
UPDATE enterprise_report_info 
SET status = 1, updated_at = NOW() 
WHERE enterprise_id = 'ENT001' 
  AND period_id = 1001 
  AND report_id = '...'

          ↓

步骤5：返回响应
{
  "status": "success",
  "code": 200,
  "info": "请求成功",
  "data": "提交成功，等待市级审核"
}
```

### 2.2 关键设计点

#### 2.2.1 Token验证机制

```java
// 核心方法：getCurrentEnterpriseId()
private String getCurrentEnterpriseId(HttpServletRequest request) {
    // 步骤1：从Cookie获取token
    String token = getTokenFromCookie(request);
    
    // 步骤2：从Redis获取TokenInfo（Redis key: yunnan:token:enterprise:{token}）
    TokenInfoVO tokenInfo = redisComponent.getEnterpriseTokenInfo(token);
    
    // 步骤3：提取enterprise_id
    return tokenInfo.getEnterpriseInfo().getEnterpriseId();
}
```

**为什么这样设计？**
- 前端不能直接传`enterprise_id`，否则可以伪造
- Token存在Redis，登录时写入，登出时删除
- Token有效期7天，自动续期

#### 2.2.2 幂等性保证

```java
// Redis key格式
String redisKey = "report:submit:" + idempotencyKey;

// 检查
if (redisUtils.get(redisKey) != null) {
    return; // 已经提交过了
}

// 记录（有效期24小时）
redisUtils.setex(redisKey, "submitted", 86400);
```

**为什么需要幂等？**
- 网络抖动可能导致前端重复请求
- 用户手快点击多次"提交"按钮
- 幂等键保证同一请求只处理一次

#### 2.2.3 字典映射机制

```java
// 前端传字符串code
"reduction_type_code": "natural_attrition"

// DictionaryService转换为Integer id
Integer id = dictionaryService.typeCodeToId("natural_attrition");
// 结果：id = 5

// 数据库存Integer
UPDATE report_info SET reduction_type = 5 WHERE ...
```

**为什么要映射？**
- 数据库用Integer节省空间，建索引快
- 前端用String可读性好，不容易出错
- 字典文件统一管理，修改方便

---

## 3. 核心功能实现

### 3.1 获取当前可填报的调查期

**业务需求**：
- 查询所有正在进行中的调查期
- 条件：`NOW() BETWEEN period_start_time AND period_end_time`
- 可能返回多个（如有重叠窗口）

**实现思路**：
```java
public Map<String, Object> getCurrentPeriods() {
    Date now = new Date();
    
    // 查询所有调查期
    List<PeriodInfo> allPeriods = periodInfoService.findListByParam(query);
    
    // 过滤进行中的
    List<Map<String, Object>> currentPeriods = new ArrayList<>();
    for (PeriodInfo period : allPeriods) {
        if (!now.before(period.getPeriodStartTime()) && 
            now.before(period.getPeriodEndTime())) {
            // 符合条件，加入列表
            currentPeriods.add(buildPeriodMap(period));
        }
    }
    
    return result;
}
```

### 3.2 暂存报表

**业务规则**：
- ✅ 数据可以不完整
- ✅ 检查窗口时间（超过截止不能暂存）
- ✅ 检查状态（已提交的不能暂存）
- ✅ 更新status=0

**代码流程**：
```java
@PostMapping("/report/draft")
public ResponseVO saveDraft(HttpServletRequest request, 
                           @RequestBody ReportCommand command) {
    // 1. 从Token获取enterprise_id
    String enterpriseId = getCurrentEnterpriseId(request);
    command.setEnterpriseId(enterpriseId);
    
    // 2. 参数校验
    validateCommandBasics(command);
    
    // 3. 调用Service（Service会检查窗口、状态）
    app.saveDraft(command);
    
    return getSuccessResponseVO("暂存成功");
}
```

### 3.3 提交报表

**业务规则**：
- ✅ 数据必须完整（严格校验）
- ✅ 检查窗口时间
- ✅ 检查状态（只有-1,0,5可以提交）
- ✅ 需要幂等键
- ✅ 更新status=1（待市级审核）

**关键代码**：
```java
@PostMapping("/report/submit")
public ResponseVO submit(HttpServletRequest request,
                        @RequestBody ReportCommand command,
                        @RequestHeader("Idempotency-Key") String idempotencyKey) {
    // 1. 校验幂等键
    if (StringUtils.isBlank(idempotencyKey)) {
        throw new BusinessException("请求头缺少 Idempotency-Key");
    }
    
    // 2. 获取enterprise_id
    String enterpriseId = getCurrentEnterpriseId(request);
    command.setEnterpriseId(enterpriseId);
    
    // 3. 调用Service（包含幂等性检查、窗口检查、严格校验）
    app.submit(command, idempotencyKey);
    
    return getSuccessResponseVO("提交成功，等待市级审核");
}
```

### 3.4 驳回后重新提交

**业务规则**：
- ✅ 只有status=5（驳回）可以重新提交
- ✅ 检查窗口时间
- ✅ 生成新report_id
- ✅ old_report_id指向旧版本
- ✅ 需要幂等键

**版本链示例**：
```
第一次提交：
report_id: v1-uuid
old_report_id: NULL
status: 5 (市级驳回)

第一次重新提交：
report_id: v2-uuid
old_report_id: v1-uuid
status: 5 (省级驳回)

第二次重新提交：
report_id: v3-uuid
old_report_id: v2-uuid
status: 1 (待市级审核)
```

**核心代码**：
```java
@Transactional
public void resubmit(ReportCommand cmd, String idempotencyKey) {
    // 1. 检查幂等键
    // 2. 检查窗口时间
    // 3. 查询旧报表，检查status=5
    
    // 4. 生成新版本
    String oldReportId = oldReport.getReportId();
    String newReportId = assembler.newReportId();
    
    // 5. 插入新report_info
    ReportInfo newReportInfo = assembler.toReportInfo(cmd);
    newReportInfo.setReportId(newReportId);
    reportInfoService.add(newReportInfo);
    
    // 6. 插入新enterprise_report_info（关联旧版本）
    EnterpriseReportInfo newEnterpriseReport = ...;
    newEnterpriseReport.setOldReportId(oldReportId); // 关键！
    enterpriseReportInfoService.add(newEnterpriseReport);
    
    // 7. 记录幂等键
    redisUtils.setex(redisKey, "resubmitted", 86400);
}
```

---

## 4. 代码结构说明

### 4.1 核心文件清单

```
yunnan-enterprise/
├── controller/
│   └── report/
│       └── ReportController.java ✅ 已完成
│           - 所有接口的入口
│           - Token验证
│           - 参数校验
│
├── service/
│   └── report/
│       ├── ReportApplicationService.java ✅ 已完成
│       │   - getCurrentPeriods()
│       │   - submit(cmd, key)
│       │   - resubmit(cmd, key)
│       │   - getAuditHistory()
│       │
│       └── PeriodUtils.java ✅ 已有
│           - toPeriodId("2025-01") → Integer
│           - fromPeriodId(1001) → "2025-01"
│
├── dto/
│   └── report/
│       ├── ReportCommand.java ✅ 已有
│       │   - 前端传来的数据
│       │   - 字段用String code
│       │
│       └── ReportV0.java ✅ 已有
│           - 返回给前端的数据
│
├── assembler/
│   └── ReportAssembler.java ✅ 已有
│       - DTO → PO 转换
│       - PO → VO 转换
│       - code ← → id 映射
│
├── dictionary/
│   └── DictionaryService.java ✅ 已有
│       - typeCodeToId("natural_attrition") → 5
│       - typeIdToCode(5) → "natural_attrition"
│
└── constants/
    └── ReportConstants.java ✅ 已有
        - MAX_EMPLOYEES
        - OTHER_CODE = "OTHER"

yunnan-common/
├── entity/
│   └── po/
│       ├── PeriodInfo.java ✅ 已更新
│       │   - periodId: Long (自增主键)
│       │   - investigateTime: String (YYYY-MM)
│       │   - periodStartTime: Date
│       │   - periodEndTime: Date
│       │
│       ├── ReportInfo.java ✅ 已有
│       │   - reductionType: Integer
│       │   - reason1/2/3: Integer
│       │
│       ├── EnterpriseReportInfo.java ✅ 已有
│       │   - status: Integer
│       │   - oldReportId: String
│       │
│       └── ReportAuditHistory.java ✅ 已创建
│           - auditLevel: 1市级 2省级
│           - auditResult: 1通过 2驳回
│
└── service/
    ├── PeriodInfoService.java ✅ 已有
    ├── ReportInfoService.java ✅ 已有
    └── EnterpriseReportInfoService.java ✅ 已有
```

### 4.2 关键类的作用

| 类名 | 职责 | 老手的理解 |
|-----|------|----------|
| **ReportController** | HTTP请求处理 | 门卫，验证身份后放行 |
| **ReportApplicationService** | 业务逻辑 | 大脑，决策和协调 |
| **ReportAssembler** | 数据转换 | 翻译官，前端语言↔数据库语言 |
| **DictionaryService** | 字典映射 | 字典，查询code和id的对应关系 |
| **ReportInfo** | 数据库映射 | 数据库表的Java对象 |

---

## 5. 接口文档

### 5.1 获取当前可填报的调查期

**接口**：`GET /api/enterprise/periods/current`

**请求**：
```bash
curl -X GET http://localhost:8080/api/enterprise/periods/current \
  -H "Cookie: token=xxx"
```

**响应**：
```json
{
  "status": "success",
  "code": 200,
  "info": "请求成功",
  "data": {
    "periods": [
      {
        "period_id": 1001,
        "investigate_time": "2025-01",
        "period_start_time": "2025-01-10T08:00:00",
        "period_end_time": "2025-01-25T23:59:59",
        "window_status": "进行中"
      }
    ],
    "count": 1
  }
}
```

### 5.2 获取/创建当前报表

**接口**：`GET /api/enterprise/report?reporting_period=2025-01`

**请求**：
```bash
curl -X GET "http://localhost:8080/api/enterprise/report?reporting_period=2025-01" \
  -H "Cookie: token=xxx"
```

**响应**：
```json
{
  "status": "success",
  "code": 200,
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "enterprise_id": "ENT001",
    "reporting_period": "2025-01",
    "status": "-1",
    "initial_employees": null,
    "current_employees": null,
    "reduction_type_code": null,
    "updated_at": "2025-01-27 10:00:00"
  }
}
```

### 5.3 暂存报表

**接口**：`POST /api/enterprise/report/draft`

**请求**：
```bash
curl -X POST http://localhost:8080/api/enterprise/report/draft \
  -H "Cookie: token=xxx" \
  -H "Content-Type: application/json" \
  -d '{
    "reporting_period": "2025-01",
    "initial_employees": 100,
    "current_employees": 95,
    "reduction_type_code": "natural_attrition"
  }'
```

**响应**：
```json
{
  "status": "success",
  "code": 200,
  "data": "暂存成功"
}
```

### 5.4 提交报表

**接口**：`POST /api/enterprise/report/submit`

**请求**：
```bash
curl -X POST http://localhost:8080/api/enterprise/report/submit \
  -H "Cookie: token=xxx" \
  -H "Idempotency-Key: uuid-123-456" \
  -H "Content-Type: application/json" \
  -d '{
    "reporting_period": "2025-01",
    "initial_employees": 100,
    "current_employees": 95,
    "reduction_type_code": "natural_attrition",
    "primary_reason_code": "natural_attrition"
  }'
```

**响应**：
```json
{
  "status": "success",
  "code": 200,
  "data": "提交成功，等待市级审核"
}
```

### 5.5 驳回后重新提交

**接口**：`POST /api/enterprise/report/resubmit`

**请求**：
```bash
curl -X POST http://localhost:8080/api/enterprise/report/resubmit \
  -H "Cookie: token=xxx" \
  -H "Idempotency-Key: uuid-789" \
  -H "Content-Type: application/json" \
  -d '{
    "reporting_period": "2025-01",
    "initial_employees": 100,
    "current_employees": 95,
    "reduction_type_code": "natural_attrition",
    "primary_reason_code": "natural_attrition",
    "primary_reason_desc": "正常退休，包括王某、李某等5人，详细说明..."
  }'
```

**响应**：
```json
{
  "status": "success",
  "code": 200,
  "data": "重新提交成功，等待审核"
}
```

### 5.6 查看审核历史

**接口**：`GET /api/enterprise/report/audit-history?reporting_period=2025-01`

**请求**：
```bash
curl -X GET "http://localhost:8080/api/enterprise/report/audit-history?reporting_period=2025-01" \
  -H "Cookie: token=xxx"
```

**响应**：
```json
{
  "status": "success",
  "code": 200,
  "data": {
    "audit_history": [
      {
        "audit_level": 1,
        "audit_level_name": "市级审核",
        "auditor": "city_user_001",
        "audit_result": 2,
        "audit_result_name": "驳回",
        "audit_opinion": "请补充详细的人员减少原因说明...",
        "audit_time": "2025-01-16 10:30:00"
      }
    ],
    "message": "审核历史功能待实现"
  }
}
```

### 5.7 获取报表列表

**接口**：`GET /api/enterprise/reports?page_no=1&page_size=20`

**请求**：
```bash
curl -X GET "http://localhost:8080/api/enterprise/reports?page_no=1&page_size=20" \
  -H "Cookie: token=xxx"
```

**响应**：
```json
{
  "status": "success",
  "code": 200,
  "data": [
    {
      "id": "report-uuid-1",
      "reporting_period": "2025-01",
      "status": "1",
      "initial_employees": 100,
      "current_employees": 95,
      "updated_at": "2025-01-15 14:30:00"
    }
  ]
}
```

END


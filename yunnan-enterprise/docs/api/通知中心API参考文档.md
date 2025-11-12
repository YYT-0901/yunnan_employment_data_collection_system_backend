# 通知中心 API 参考文档

本文档提供通知中心 API 的详细技术参考。

## 目录

- [端点](#端点)
  - [获取通知列表](#获取通知列表)
  - [获取通知详情](#获取通知详情)
  - [获取未读通知数量](#获取未读通知数量)
- [数据模型](#数据模型)
- [错误代码](#错误代码)
- [业务规则](#业务规则)

---

## 端点

### 获取通知列表

检索企业可见的通知列表，支持分页查询。

#### HTTP 请求

```
GET /api/notice/list
```

#### 查询参数

| 参数 | 类型 | 必需 | 默认值 | 描述 |
|------|------|------|--------|------|
| `enterprise_id` | string | 是 | - | 企业标识符。示例：`ENTERPRISE_001` |
| `page_no` | integer | 否 | `1` | 页码，从 1 开始。最小值：1 |
| `page_size` | integer | 否 | `10` | 每页返回的项目数。最小值：1，最大值：100 |

#### 请求示例

```bash
curl -X GET \
  'http://localhost:8082/api/notice/list?enterprise_id=ENTERPRISE_001&page_no=1&page_size=10' \
  -H 'Content-Type: application/json'
```

#### 响应

##### 成功响应

**状态码：** `200 OK`

**响应体：**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [
      {
        "noticeId": 1,
        "title": "【紧急】系统升级维护通知",
        "content": "<p>各位企业用户：</p><p>为了提升系统性能...</p>",
        "attachment": null,
        "attachmentName": null,
        "isImportant": 1,
        "publisher": "系统管理员",
        "publishTime": "2025-10-26 10:00:00",
        "readCount": 156,
        "isRead": false
      },
      {
        "noticeId": 2,
        "title": "数据填报操作指南",
        "content": "<p>为帮助大家更好地完成数据填报...</p>",
        "attachment": "/uploads/documents/guide.pdf",
        "attachmentName": "操作指南.pdf",
        "isImportant": 0,
        "publisher": "市级管理员",
        "publishTime": "2025-10-25 09:30:00",
        "readCount": 89,
        "isRead": true
      }
    ],
    "pageNo": 1,
    "pageSize": 10,
    "pageTotal": 2,
    "totalCount": 15
  }
}
```

**响应字段：**

| 字段 | 类型 | 描述 |
|------|------|------|
| `code` | integer | 响应状态码 |
| `message` | string | 响应消息 |
| `data` | object | 响应数据 |
| `data.list` | array | 通知对象数组，参见 [NoticeVO](#noticevo) |
| `data.pageNo` | integer | 当前页码 |
| `data.pageSize` | integer | 每页项目数 |
| `data.pageTotal` | integer | 总页数 |
| `data.totalCount` | integer | 总记录数 |

##### 错误响应

**状态码：** `400 Bad Request`

缺少必需参数时：

```json
{
  "code": 400,
  "message": "Required request parameter 'enterprise_id' is not present",
  "data": null
}
```

**状态码：** `500 Internal Server Error`

服务器内部错误时：

```json
{
  "code": 500,
  "message": "服务器返回错误，请联系管理员",
  "data": null
}
```

#### 业务规则

1. 仅返回状态为"正常"的通知（`status=1`）
2. 仅返回企业可见（`notice_status=2`）或全部人可见（`notice_status=1`）的通知
3. 仅返回在生效期内的通知（当前时间在 `start_time` 和 `end_time` 之间）
4. 结果按发布时间倒序排列（最新的在前）
5. 如果 `page_size` 超过 100，将自动限制为 100

---

### 获取通知详情

检索指定通知的详细信息，并自动标记为已读。

#### HTTP 请求

```
GET /api/notice/{noticeId}
```

#### 路径参数

| 参数 | 类型 | 必需 | 描述 |
|------|------|------|------|
| `noticeId` | long | 是 | 通知的唯一标识符 |

#### 查询参数

| 参数 | 类型 | 必需 | 描述 |
|------|------|------|------|
| `username` | string | 是 | 当前用户的用户名 |
| `enterprise_id` | string | 是 | 企业标识符 |

#### 请求示例

```bash
curl -X GET \
  'http://localhost:8082/api/notice/1?username=test_user&enterprise_id=ENTERPRISE_001' \
  -H 'Content-Type: application/json'
```

#### 响应

##### 成功响应

**状态码：** `200 OK`

**响应体：**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "noticeId": 1,
    "title": "【紧急】系统升级维护通知",
    "content": "<p>各位企业用户：</p><p>为了提升系统性能和用户体验，我们将于<strong>本周六（2025年10月26日）晚上22:00-24:00</strong>进行系统升级维护。</p><p><strong style=\"color:red;\">维护期间系统将暂停服务</strong>，请各企业提前完成数据填报工作。</p><p>如有疑问，请联系技术支持：400-123-4567</p>",
    "attachment": null,
    "attachmentName": null,
    "isImportant": 1,
    "publisher": "系统管理员",
    "publishTime": "2025-10-26 10:00:00",
    "readCount": 157,
    "isRead": true
  }
}
```

**响应字段：**

| 字段 | 类型 | 描述 |
|------|------|------|
| `code` | integer | 响应状态码 |
| `message` | string | 响应消息 |
| `data` | object | 通知对象，参见 [NoticeVO](#noticevo) |

##### 错误响应

**状态码：** `500 Internal Server Error`

通知不存在时：

```json
{
  "code": 500,
  "message": "通知不存在",
  "data": null
}
```

无权查看时：

```json
{
  "code": 500,
  "message": "无权查看此通知",
  "data": null
}
```

#### 业务规则

1. 查询指定 ID 的通知详情
2. 验证用户是否有权限查看该通知
3. 自动在 `notice_read_info` 表中记录阅读状态
4. 如果用户已读过该通知，不会重复记录
5. 阅读记录失败不影响通知内容的返回

---

### 获取未读通知数量

获取当前用户的未读通知数量。

#### HTTP 请求

```
GET /api/notice/unread/count
```

#### 查询参数

| 参数 | 类型 | 必需 | 描述 |
|------|------|------|------|
| `username` | string | 是 | 用户名 |

#### 请求示例

```bash
curl -X GET \
  'http://localhost:8082/api/notice/unread/count?username=test_user' \
  -H 'Content-Type: application/json'
```

#### 响应

##### 成功响应

**状态码：** `200 OK`

**响应体：**

```json
{
  "code": 200,
  "message": "success",
  "data": 7
}
```

**响应字段：**

| 字段 | 类型 | 描述 |
|------|------|------|
| `code` | integer | 响应状态码 |
| `message` | string | 响应消息 |
| `data` | integer | 未读通知数量 |

##### 错误响应

**状态码：** `500 Internal Server Error`

```json
{
  "code": 500,
  "message": "服务器返回错误，请联系管理员",
  "data": null
}
```

#### 业务规则

1. 统计所有有效的企业可见通知
2. 排除用户已读过的通知（在 `notice_read_info` 表中有记录）
3. 返回未读通知的数量

---

## 数据模型

### NoticeVO

通知视图对象，用于返回通知信息。

#### 字段

| 字段 | 类型 | 必需 | 描述 |
|------|------|------|------|
| `noticeId` | long | 是 | 通知的唯一标识符 |
| `title` | string | 是 | 通知标题。最大长度：200 字符 |
| `content` | string | 否 | 通知内容，HTML 格式 |
| `attachment` | string | 否 | 附件文件路径。如果没有附件则为 `null` |
| `attachmentName` | string | 否 | 附件的原始文件名 |
| `isImportant` | integer | 是 | 重要性标识。`0` = 普通通知，`1` = 重要通知 |
| `publisher` | string | 是 | 发布者名称 |
| `publishTime` | string | 是 | 发布时间，格式：`yyyy-MM-dd HH:mm:ss` |
| `readCount` | integer | 是 | 该通知的总阅读次数 |
| `isRead` | boolean | 否 | 当前用户是否已读该通知 |

#### 示例

```json
{
  "noticeId": 1,
  "title": "系统维护通知",
  "content": "<p>系统将于本周六进行维护...</p>",
  "attachment": "/uploads/file.pdf",
  "attachmentName": "维护公告.pdf",
  "isImportant": 1,
  "publisher": "系统管理员",
  "publishTime": "2025-10-26 10:00:00",
  "readCount": 100,
  "isRead": false
}
```

### PaginationResultVO

分页结果对象。

#### 字段

| 字段 | 类型 | 描述 |
|------|------|------|
| `list` | array | 数据项数组 |
| `pageNo` | integer | 当前页码，从 1 开始 |
| `pageSize` | integer | 每页项目数 |
| `pageTotal` | integer | 总页数 |
| `totalCount` | integer | 总记录数 |

#### 示例

```json
{
  "list": [...],
  "pageNo": 1,
  "pageSize": 10,
  "pageTotal": 5,
  "totalCount": 50
}
```

---

## 错误代码

### HTTP 状态码

| 状态码 | 说明 |
|--------|------|
| `200` | 成功 |
| `400` | 错误请求 - 请求参数无效或缺失 |
| `401` | 未授权 - 需要身份验证 |
| `403` | 禁止访问 - 权限不足 |
| `404` | 未找到 - 请求的资源不存在 |
| `500` | 内部服务器错误 |

### 应用错误代码

| 错误码 | 错误消息 | 描述 | 解决方案 |
|--------|---------|------|----------|
| `400` | `Required request parameter 'xxx' is not present` | 缺少必需的请求参数 | 检查请求中是否包含所有必需参数 |
| `400` | `Invalid parameter value` | 参数值无效 | 验证参数值的格式和范围 |
| `500` | `通知不存在` | 指定 ID 的通知不存在 | 确认通知 ID 是否正确 |
| `500` | `无权查看此通知` | 用户无权查看该通知 | 检查通知的可见性设置和用户权限 |
| `500` | `服务器返回错误，请联系管理员` | 服务器内部错误 | 查看服务器日志，联系技术支持 |

### 错误响应格式

所有错误响应都遵循统一格式：

```json
{
  "code": 400,
  "message": "错误描述信息",
  "data": null
}
```

---

## 业务规则

### 通知可见性规则

通知是否对企业用户可见取决于以下条件：

1. **通知状态（status）：** 必须为 `1`（正常）
2. **可见范围（notice_status）：**
   - `1` - 全部人可见
   - `2` - 企业可见
   - `3` - 市级可见（企业不可见）
   - `4` - 省级可见（企业不可见）
3. **生效时间：** 当前时间必须在 `start_time` 和 `end_time` 之间
   - 如果 `start_time` 为 `null`，则立即生效
   - 如果 `end_time` 为 `null`，则永不过期

### 已读状态规则

1. 查看通知详情时，系统自动记录为已读
2. 已读记录存储在 `notice_read_info` 表中
3. 同一用户对同一通知只记录一次已读
4. 已读状态不影响通知的再次查看

### 排序规则

通知列表按以下顺序排序：

1. 发布时间倒序（最新的在前）
2. 重要通知不会自动置顶，需要前端根据 `isImportant` 字段处理

### 分页规则

1. `page_no` 从 1 开始计数
2. 如果 `page_no` 超出实际页数，返回空列表
3. `page_size` 最大值为 100，超出将自动限制
4. 建议的 `page_size` 值：10、20、50

---

## 使用限制

### 并发限制

- 建议每个企业每分钟不超过 60 个请求
- 过多的请求可能会被限流

### 数据限制

- 通知标题最大长度：200 字符
- 通知内容最大长度：10000 字符
- 附件最大大小：10MB

### 缓存建议

- 通知列表：建议缓存 1-5 分钟
- 通知详情：建议缓存 5-10 分钟
- 未读数量：建议每分钟更新一次

---

**最后更新时间：** 2025-10-26  
**API 版本：** v1.0  
**文档版本：** 1.0


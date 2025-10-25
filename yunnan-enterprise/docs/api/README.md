**# 通知中心 API 文档

通知中心 API 为企业端提供程序化访问通知系统的能力。使用此 API 可以检索通知、查看详情和跟踪已读状态。

## 概览

本文档介绍了通知中心 API 端点、参数和响应格式。API 遵循 REST 原则并返回 JSON 格式的数据。

**基础 URL：**
```
http://localhost:8082
```

**API 版本：** v1.0

## 快速开始

### 前提条件

开始之前，请确保您具备：

- 有效的企业 ID
- 系统访问权限
- RESTful API 基础知识

### 发送第一个请求

使用 curl 检索通知列表：

```bash
curl -X GET \
  'http://localhost:8082/api/notice/list?enterprise_id=ENTERPRISE_001&page_no=1&page_size=10' \
  -H 'Content-Type: application/json'
```

### 响应示例

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [
      {
        "noticeId": 1,
        "title": "系统维护通知",
        "isImportant": 1,
        "publisher": "系统管理员",
        "publishTime": "2025-10-26 10:00:00"
      }
    ],
    "pageNo": 1,
    "pageSize": 10,
    "pageTotal": 1,
    "totalCount": 10
  }
}
```

## API 端点

通知中心 API 提供以下端点：

| 端点 | 方法 | 描述 |
|------|------|------|
| `/api/notice/list` | GET | 检索分页的通知列表 |
| `/api/notice/{id}` | GET | 检索特定通知的详细信息 |
| `/api/notice/unread/count` | GET | 获取未读通知数量 |

有关每个端点的详细信息，请参阅 [API 参考文档](./通知中心API参考文档.md)。

## 身份验证

> **注意：** 身份验证功能正在开发中。所有请求都需要企业 ID。

## 速率限制

目前未强制执行速率限制。我们建议：

- 每个企业每分钟最多 60 个请求
- 使用分页来减少数据传输
- 适当时缓存响应

## 错误处理

所有 API 响应都包含一个 `code` 字段，用于指示状态：

- `200` - 成功
- `400` - 错误请求（参数无效）
- `404` - 未找到
- `500` - 内部服务器错误

错误响应示例：

```json
{
  "code": 400,
  "message": "Required request parameter 'enterprise_id' is not present",
  "data": null
}
```

## 数据格式

### 日期和时间

所有时间戳均采用 `yyyy-MM-dd HH:mm:ss` 格式（北京时间，GMT+8）：

```
2025-10-26 14:30:00
```

### 分页

分页参数：

- `page_no` - 页码（从 1 开始）
- `page_size` - 每页项目数（默认值：10，最大值：100）

分页响应：

```json
{
  "pageNo": 1,
  "pageSize": 10,
  "pageTotal": 5,
  "totalCount": 50
}
```

## 最佳实践

### 1. 有效使用分页

仅请求您需要的数据：

```bash
# 推荐：请求 10 个项目
?page_size=10

# 不推荐：请求过多项目
?page_size=1000
```

### 2. 妥善处理错误

始终检查响应代码：

```javascript
if (response.code === 200) {
  // 处理数据
} else {
  // 处理错误
  console.error(response.message);
}
```

### 3. 适当时使用缓存

缓存通知列表以减少 API 调用：

```javascript
// 缓存 5 分钟
const CACHE_DURATION = 5 * 60 * 1000;
```

### 4. 定期更新未读计数

每 1-5 分钟轮询未读计数端点：

```javascript
setInterval(() => {
  fetchUnreadCount();
}, 60000); // 每分钟
```

## 代码示例

### JavaScript (Axios)

```javascript
import axios from 'axios';

const API_BASE_URL = 'http://localhost:8082';

// 获取通知列表
async function fetchNotifications(enterpriseId, pageNo = 1, pageSize = 10) {
  try {
    const response = await axios.get(`${API_BASE_URL}/api/notice/list`, {
      params: {
        enterprise_id: enterpriseId,
        page_no: pageNo,
        page_size: pageSize
      }
    });
    return response.data;
  } catch (error) {
    console.error('获取通知列表失败:', error);
    throw error;
  }
}

// 获取通知详情
async function getNoticeDetail(noticeId, username, enterpriseId) {
  try {
    const response = await axios.get(`${API_BASE_URL}/api/notice/${noticeId}`, {
      params: {
        username: username,
        enterprise_id: enterpriseId
      }
    });
    return response.data;
  } catch (error) {
    console.error('获取通知详情失败:', error);
    throw error;
  }
}

// 获取未读数量
async function getUnreadCount(username) {
  try {
    const response = await axios.get(`${API_BASE_URL}/api/notice/unread/count`, {
      params: { username: username }
    });
    return response.data.data;
  } catch (error) {
    console.error('获取未读数量失败:', error);
    throw error;
  }
}
```

### Python (Requests)

```python
import requests

API_BASE_URL = 'http://localhost:8082'

def fetch_notifications(enterprise_id, page_no=1, page_size=10):
    """获取通知列表"""
    try:
        response = requests.get(
            f'{API_BASE_URL}/api/notice/list',
            params={
                'enterprise_id': enterprise_id,
                'page_no': page_no,
                'page_size': page_size
            }
        )
        response.raise_for_status()
        return response.json()
    except requests.exceptions.RequestException as e:
        print(f'获取通知列表失败: {e}')
        raise

def get_notice_detail(notice_id, username, enterprise_id):
    """获取通知详情"""
    try:
        response = requests.get(
            f'{API_BASE_URL}/api/notice/{notice_id}',
            params={
                'username': username,
                'enterprise_id': enterprise_id
            }
        )
        response.raise_for_status()
        return response.json()
    except requests.exceptions.RequestException as e:
        print(f'获取通知详情失败: {e}')
        raise

def get_unread_count(username):
    """获取未读通知数量"""
    try:
        response = requests.get(
            f'{API_BASE_URL}/api/notice/unread/count',
            params={'username': username}
        )
        response.raise_for_status()
        return response.json()['data']
    except requests.exceptions.RequestException as e:
        print(f'获取未读数量失败: {e}')
        raise
```

## 支持

如果您遇到问题或有疑问：

- 查看 [API 参考文档](./通知中心API参考文档.md) 获取详细文档
- 查看下方[常见问题](#常见问题)
- 联系技术支持：support@example.com

## 常见问题

### 问题："No static resource api/notice/list"

**原因：** Spring Boot 中未激活 `db` 配置文件。

**解决方案：** 在 `application.yml` 中添加以下内容：

```yaml
spring:
  profiles:
    active: db
```

### 问题：连接被拒绝

**原因：** 后端服务未运行。

**解决方案：** 启动后端服务：

```bash
cd backend/yunnan-enterprise
mvn spring-boot:run
```

### 问题：通知列表为空

**原因：** 数据库中没有测试数据。

**解决方案：** 使用提供的 SQL 脚本插入测试数据（请参阅[测试](#测试)）。

## 测试

### 设置测试数据

执行以下 SQL 插入测试数据：

```sql
INSERT INTO notice_info (
  title, content, is_important, notice_status, 
  publisher, publish_time, start_time, 
  read_count, status, created_at, updated_at
) VALUES (
  '系统维护通知',
  '<p>系统将于本周六进行维护...</p>',
  1, 2, '系统管理员', NOW(), NOW(),
  0, 1, NOW(), NOW()
);
```

### 使用 Postman 测试

1. 导入 API 集合
2. 设置环境变量：
   - `base_url`: `http://localhost:8082`
   - `enterprise_id`: `ENTERPRISE_001`
   - `username`: `test_user`
3. 运行集合

### 使用 curl 测试

测试通知列表端点：

```bash
curl -X GET \
  'http://localhost:8082/api/notice/list?enterprise_id=ENTERPRISE_001&page_no=1&page_size=10' \
  -H 'Content-Type: application/json' \
  | jq '.'
```

## 更新日志

### 版本 1.0 (2025-10-26)

- 初始发布
- GET `/api/notice/list` - 检索通知列表
- GET `/api/notice/{id}` - 获取通知详情
- GET `/api/notice/unread/count` - 获取未读数量

---

**最后更新时间：** 2025-10-26  
**API 版本：** v1.0  
**文档版本：** 1.0


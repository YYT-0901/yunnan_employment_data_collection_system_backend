# 云南省企业就业失业数据采集系统 - 账号管理API接口文档

## 基础说明

### 基础响应格式

所有接口采用统一的响应格式：

| 参数   | 说明         | 类型   |
| ------ | ------------ | ------ |
| status | 状态名称     | String |
| code   | 响应状态码   | int    |
| info   | 响应状态信息 | String |
| data   | 返回的数据   | object |

**成功响应示例**

```json
{
	"status": "success",
	"code": 200,
	"info": "请求成功",
	"data": null
}
```

**错误响应示例**

```json
{
	"status": "error",
	"code": 500,
	"info": "服务器返回错误,请联系管理员",
	"data": null
}
```

### 基础URL

- **开发环境**：`http://localhost:8080/api`
- **生产环境**：根据实际部署配置

### 认证方式

所有接口需要在省级管理员登录后访问，登录接口返回的token会自动保存到Cookie中，后续请求会自动携带。

---

## 一、账号创建相关接口

### 1.1 创建企业账号

- **请求方法**：POST
- **请求路径**：`/api/account/createAccount`
- **功能描述**：省级管理员创建企业账号。系统会自动生成唯一的企业ID、用户名和密码。创建企业账号时会同时创建企业信息记录和对应的登录账号。企业账号创建后，状态为"创建未备案"，企业可以使用生成的账号登录系统，但需要完善备案信息后才能进行数据填报操作。
- **权限要求**：需要省级管理员权限（通过省级管理员登录获取token）
- **请求头**：
  - `Content-Type: application/json`
  - Cookie中需要包含登录token（由省级管理员登录接口返回）

**请求参数（Body）**

| 参数名                  | 类型    | 是否必填 | 说明                                                         |
| :---------------------- | :------ | :------- | :----------------------------------------------------------- |
| type                    | INT     | 是       | 账号类型，创建企业账号时必须为`1`                            |
| enterpriseInfo          | Object  | 是       | 企业信息对象，包含企业的基本信息和联系方式                   |
| enterpriseInfo.name     | String  | 是       | **企业名称**（唯一必填字段），最大长度255字符。支持中文、英文。创建后会保存到数据库的`name_zh`字段 |
| enterpriseInfo.orgCode  | String  | 否       | 组织机构代码（统一社会信用代码），最大长度255字符。可选，可在备案时补充 |
| enterpriseInfo.region   | Integer | 否       | 所属地区编码，关联地区字典。可选，可在备案时补充            |
| enterpriseInfo.nature   | Integer | 否       | 所属性质编码，关联企业性质字典。可选，可在备案时补充        |
| enterpriseInfo.industry | Integer | 否       | 所属行业编码，关联行业字典。可选，可在备案时补充            |
| enterpriseInfo.industryDesc | String | 否       | 主要经营业务详情，文本类型，无长度限制。可选，可在备案时补充 |
| enterpriseInfo.contactName | String | 否       | 联系人名称，最大长度50字符。可选，可在备案时补充            |
| enterpriseInfo.address  | String  | 否       | 联系人地址，最大长度500字符。可选，可在备案时补充           |
| enterpriseInfo.postalCode | String | 否       | 邮政编码，最大长度10字符。可选，可在备案时补充              |
| enterpriseInfo.phoneNum | String  | 否       | 联系电话，最大长度20字符。可选，可在备案时补充              |
| enterpriseInfo.faxNum   | String  | 否       | 传真号，最大长度20字符。可选，可在备案时补充                |
| enterpriseInfo.email    | String  | 否       | 电子邮箱，最大长度100字符，需符合标准Email格式。可选，可在备案时补充。系统可能会向此邮箱发送账号信息 |

**字段说明**

- **type字段**：
  - 固定值：`1`（表示企业账号）
  - 如果传入其他值，系统会返回错误码600

- **enterpriseInfo对象**：
  - 必须提供，且至少包含`name`字段
  - `name`字段是唯一必填项，其他所有字段均为可选

- **可选字段说明**：
  - 所有可选字段都可以留空，系统不会进行验证
  - 可选字段可以在企业登录后通过备案流程补充完善
  - 建议尽可能填写完整信息，便于后续管理和联系

**请求示例（完整信息创建企业账号）**

```json
{
	"type": 1,
	"enterpriseInfo": {
		"name": "云南XX科技有限公司",
		"orgCode": "91530000MA6K3H9X7C",
		"region": 1,
		"nature": 7,
		"industry": 65,
		"industryDesc": "软件开发、信息技术咨询、系统集成服务、计算机网络技术服务",
		"contactName": "张XX",
		"address": "云南省昆明市五华区XX街道XX号XX大厦X层X室",
		"postalCode": "650000",
		"phoneNum": "13800138000",
		"faxNum": "0871-65123456",
		"email": "zhang@xxcompany.com"
	}
}
```

**请求示例（最小化创建，仅填写必填字段）**

```json
{
	"type": 1,
	"enterpriseInfo": {
		"name": "云南XX科技有限公司"
	}
}
```

**请求示例（部分信息创建）**

```json
{
	"type": 1,
	"enterpriseInfo": {
		"name": "云南XX贸易有限公司",
		"contactName": "李XX",
		"phoneNum": "13900139000",
		"email": "li@xxcompany.com"
	}
}
```

**响应参数（data字段）**

| 参数名   | 类型   | 说明                                                         |
| :------- | :----- | :----------------------------------------------------------- |
| username | String | 系统自动生成的用户名，长度为10位的随机字符串。由大小写英文字母和数字组成，例如：`RInQF54wd5`、`Xk9mN2pQwR` |
| password | String | 系统自动生成的密码，长度为10位的随机字符串。由大小写英文字母和数字组成，可能包含特殊字符，例如：`O8tlZlHzXS`、`#Uw3ik9P#UPx` |

**成功响应示例**

```json
{
	"status": "success",
	"code": 200,
	"info": "企业账号创建成功",
	"data": {
		"username": "RInQF54wd5",
		"password": "O8tlZlHzXS"
	}
}
```

**错误响应示例（账号类型错误）**

```json
{
	"status": "error",
	"code": 600,
	"info": "账号类型参数错误",
	"data": null
}
```

**错误响应示例（企业名称为空）**

```json
{
	"status": "error",
	"code": 400,
	"info": "企业名称不能为空",
	"data": null
}
```

**错误响应示例（企业信息对象缺失）**

```json
{
	"status": "error",
	"code": 601,
	"info": "企业账号必须提供企业信息",
	"data": null
}
```

**错误响应示例（服务器错误）**

```json
{
	"status": "error",
	"code": 500,
	"info": "服务器返回错误,请联系管理员",
	"data": null
}
```

**错误码说明**

| 错误码 | HTTP状态码 | 说明                             | 解决方案                                     |
| :----- | :--------- | :------------------------------- | :------------------------------------------- |
| 400    | 400        | 请求参数错误或格式不合法         | 检查请求参数格式，确保JSON格式正确           |
| 401    | 401        | 用户未登录或登录令牌失效         | 重新登录获取新的token                        |
| 403    | 403        | 无权限访问该接口（非省级管理员） | 使用省级管理员账号登录                       |
| 500    | 500        | 服务器内部错误                   | 联系系统管理员，查看服务器日志               |
| 600    | 200        | 账号类型参数错误，type必须为1     | 确保type字段值为1                            |
| 601    | 200        | 企业账号必须提供企业信息         | 确保请求体中包含enterpriseInfo对象           |
| 603    | 200        | 企业名称不能为空                 | 在enterpriseInfo对象中提供name字段，且不能为空 |

**业务逻辑详细说明**

#### 1. 企业ID生成规则

- **格式**：`E` + 20位随机数字
- **示例**：`E48260021273246716765`、`E12345678901234567890`
- **唯一性**：系统确保每次生成的企业ID都是唯一的
- **存储位置**：保存到`enterprise_info`表的`enterprise_id`字段，作为主键

#### 2. 用户名和密码生成规则

- **长度**：均为10位字符
- **字符集**：大小写英文字母（A-Z, a-z）和数字（0-9），密码可能包含特殊字符
- **生成方式**：使用随机字符串生成算法，确保唯一性
- **存储方式**：
  - 用户名：保存到`account_info`表的`username`字段，作为主键
  - 密码：明文保存到`account_info`表的`password`字段（建议后续实现密码加密）

#### 3. 企业信息存储规则

- **企业名称**：
  - 前端传入的`name`字段会保存到数据库的`name_zh`字段（中文名称字段）
  - 如果后续需要支持中英文双语，可以添加`name_en`字段
  
- **联系人名称**：
  - 前端传入的`contactName`字段会保存到数据库的`contact_name_zh`字段
  
- **其他字段**：
  - 直接映射到对应的数据库字段
  - 可选字段如果为空，则数据库存储为`NULL`

#### 4. 企业状态设置

- **初始状态**：`0`（创建未备案）
- **状态说明**：
  - `0`：创建未备案 - 企业账号已创建，但尚未提交备案信息
  - `1`：已备案未审核 - 企业已提交备案信息，等待审核
  - `2`：已退回 - 备案信息审核不通过，已退回给企业
  - `3`：正常（已备案已审核） - 备案信息已审核通过，企业可以正常使用系统
  - `4`：倒闭 - 企业已倒闭

#### 5. 账号状态设置

- **初始状态**：`0`（正常/启用）
- **状态说明**：
  - `0`：正常 - 账号可以正常登录和使用
  - `1`：停用 - 账号被禁用，无法登录系统

#### 6. 数据关联关系

- **账号与企业信息关联**：
  - 通过`enterprise_id`字段关联`account_info`表和`enterprise_info`表
  - 一个企业只能有一个账号（一对一关系）
  
- **创建时间设置**：
  - `enterprise_info.created_at`：企业创建时间，设置为当前系统时间
  - `enterprise_info.updated_at`：企业信息修改时间，初始值等于创建时间
  - `account_info.created_at`：账号创建时间，设置为当前系统时间

#### 7. 事务处理

- 创建企业账号涉及两个表的插入操作：
  1. 插入`enterprise_info`表（企业信息）
  2. 插入`account_info`表（账号信息）
- 两个操作在同一个事务中执行，确保数据一致性
- 如果任何一步失败，整个操作会回滚，不会出现数据不一致的情况

#### 8. 后续操作建议

**创建成功后的操作**：
1. **保存账号信息**：
   - 前端应提示管理员保存生成的用户名和密码
   - 建议将账号信息通过安全渠道发送给企业联系人（如加密邮件）

2. **企业首次登录**：
   - 企业使用返回的用户名和密码登录企业端系统
   - 登录地址：`http://localhost:8082/api/account/login`（企业端）

3. **完善备案信息**：
   - 企业登录后，需要完善备案信息
   - 只有完成备案并通过审核后，企业才能进行数据填报操作

4. **密码安全**：
   - 系统生成的密码为随机字符串，建议企业首次登录后修改密码
   - 如果系统实现了密码修改功能，建议企业及时修改

**注意事项**：
- 账号信息仅在创建时返回一次，后续无法再次查询到明文密码
- 如果忘记密码，需要管理员重置密码（如果实现了重置功能）
- 建议在创建账号时同时记录企业的联系方式，便于后续沟通

#### 9. 数据验证规则

- **企业名称（name）**：
  - 必填，不能为空字符串
  - 最大长度255字符
  - 支持中英文、数字、特殊字符

- **组织机构代码（orgCode）**（如果提供）：
  - 最大长度255字符
  - 建议格式：18位统一社会信用代码

- **电子邮箱（email）**（如果提供）：
  - 需要符合标准Email格式
  - 最大长度100字符
  - 格式验证：`^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$`

- **联系电话（phoneNum）**（如果提供）：
  - 最大长度20字符
  - 建议格式：手机号或固定电话

- **邮政编码（postalCode）**（如果提供）：
  - 最大长度10字符
  - 建议格式：6位数字

#### 10. 性能考虑

- 创建账号操作涉及数据库插入，响应时间通常在100-500毫秒内
- 大量并发创建时，建议使用队列处理，避免数据库压力过大
- 企业ID和用户名的唯一性检查通过数据库主键约束保证

#### 11. 安全建议

- **密码存储**：当前为明文存储，建议后续实现密码加密（如BCrypt）
- **账号传输**：建议通过HTTPS协议传输，避免账号信息泄露
- **权限控制**：确保只有省级管理员可以创建账号
- **操作日志**：建议记录所有账号创建操作，便于审计

#### 12. 扩展功能（TODO）

根据代码注释，以下功能待实现：
- **邮件通知**：创建账号后，自动向企业联系人邮箱发送账号信息（`// TODO 发送邮箱到企业人email`）

---

**接口调用完整示例**

```bash
# 使用cURL调用示例
curl -X POST "http://localhost:8080/api/account/createAccount" \
  -H "Content-Type: application/json" \
  -H "Cookie: token=your_admin_token_here" \
  -d '{
    "type": 1,
    "enterpriseInfo": {
      "name": "云南XX科技有限公司",
      "orgCode": "91530000MA6K3H9X7C",
      "region": 1,
      "nature": 7,
      "industry": 65,
      "industryDesc": "软件开发、信息技术咨询",
      "contactName": "张XX",
      "address": "云南省昆明市五华区XX街道XX号",
      "postalCode": "650000",
      "phoneNum": "13800138000",
      "email": "zhang@xxcompany.com"
    }
  }'
```

```javascript
// JavaScript/Axios调用示例
const response = await axios.post('/api/account/createAccount', {
  type: 1,
  enterpriseInfo: {
    name: '云南XX科技有限公司',
    orgCode: '91530000MA6K3H9X7C',
    region: 1,
    nature: 7,
    industry: 65,
    industryDesc: '软件开发、信息技术咨询',
    contactName: '张XX',
    address: '云南省昆明市五华区XX街道XX号',
    postalCode: '650000',
    phoneNum: '13800138000',
    email: 'zhang@xxcompany.com'
  }
}, {
  headers: {
    'Content-Type': 'application/json'
  },
  withCredentials: true  // 自动携带Cookie
});

console.log('创建成功，用户名：', response.data.data.username);
console.log('创建成功，密码：', response.data.data.password);
```

---

## 二、账号查询相关接口

### 2.1 查询企业账号列表

- **请求方法**：GET
- **请求路径**：`/api/account/loadAllEnterpriseAccount`
- **功能描述**：分页查询所有企业账号列表，包含企业基本信息和账号状态。支持按用户名、企业ID等条件模糊查询。接口会自动过滤，只返回企业账号（type=1）的数据。
- **请求头**：
  - Cookie中需要包含登录token

**请求参数（Query）**

| 参数名       | 类型    | 是否必填 | 说明                                                         |
| :----------- | :------ | :------- | :----------------------------------------------------------- |
| pageNo       | INT     | 否       | 页码，从1开始，默认值为1                                      |
| pageSize     | INT     | 否       | 每页条数，默认值为10，建议范围10-100                         |
| username     | String  | 否       | 用户名模糊查询，支持部分匹配                                 |
| enterpriseId | String  | 否       | 企业ID精确查询                                               |
| status       | INT     | 否       | 账号状态筛选：0-正常，1-停用                                 |
| orderBy      | String  | 否       | 排序字段，格式：`字段名 ASC/DESC`，默认按创建时间倒序         |

**请求示例**

```
GET /api/account/loadAllEnterpriseAccount?pageNo=1&pageSize=10&username=&enterpriseId=
```

```
GET /api/account/loadAllEnterpriseAccount?pageNo=1&pageSize=20&username=RIn&status=0
```

**响应参数（data字段）**

| 参数名              | 类型    | 说明                                                         |
| :------------------ | :------ | :----------------------------------------------------------- |
| list                | Array   | 企业账号列表数据                                             |
| list[].username     | String  | 用户名                                                       |
| list[].password     | String  | 密码（通常前端不显示，仅用于管理员查看）                     |
| list[].type         | INT     | 账号类型：1-企业账号，2-市账号                               |
| list[].enterpriseId | String  | 企业ID                                                       |
| list[].cityCode     | INT     | 市编码（仅市账号有此字段）                                   |
| list[].status       | INT     | 账号状态：0-正常，1-停用                                     |
| list[].lastLoginTime | DATETIME | 最后登录时间，格式：`yyyy-MM-dd HH:mm:ss`，未登录则为null |
| list[].createdAt    | DATETIME | 账号创建时间，格式：`yyyy-MM-dd HH:mm:ss`                   |
| list[].name         | String  | 企业名称，从关联的企业信息中获取，未关联则为null            |
| list[].orgCode      | String  | 组织机构代码                                                 |
| list[].region       | INT     | 所属地区编码                                                 |
| list[].nature       | INT     | 所属性质编码                                                 |
| list[].industry     | INT     | 所属行业编码                                                 |
| list[].industryDesc | String  | 主要经营业务详情                                             |
| list[].contactName  | String  | 联系人名称                                                   |
| list[].address      | String  | 联系人地址                                                   |
| list[].postalCode   | String  | 邮政编码                                                     |
| list[].phoneNum     | String  | 联系电话                                                     |
| list[].faxNum       | String  | 传真号                                                       |
| list[].email        | String  | 电子邮箱                                                     |
| pageNo              | INT     | 当前页码                                                     |
| pageSize            | INT     | 每页条数                                                     |
| pageTotal           | INT     | 总页数                                                       |
| totalCount           | INT     | 总记录数                                                     |

**成功响应示例**

```json
{
	"status": "success",
	"code": 200,
	"info": "查询成功",
	"data": {
		"list": [
			{
				"username": "RInQF54wd5",
				"password": "O8tlZlHzXS",
				"type": 1,
				"enterpriseId": "E48260021273246716765",
				"status": 0,
				"lastLoginTime": null,
				"createdAt": "2025-11-03 01:58:33",
				"name": "企业A",
				"orgCode": null,
				"region": null,
				"nature": null,
				"industry": null,
				"industryDesc": null,
				"contactName": null,
				"address": null,
				"postalCode": null,
				"phoneNum": null,
				"faxNum": null,
				"email": null
			},
			{
				"username": "123135061",
				"password": "#Uw3ik9P#UPx",
				"type": 1,
				"enterpriseId": "1",
				"status": 0,
				"lastLoginTime": null,
				"createdAt": "2025-10-27 21:48:56",
				"name": "12313",
				"orgCode": null,
				"region": null,
				"nature": null,
				"industry": null,
				"industryDesc": null,
				"contactName": "张敏杰",
				"address": null,
				"postalCode": null,
				"phoneNum": "15711065395",
				"faxNum": null,
				"email": "zhang.warcraft@gmail.com"
			}
		],
		"pageNo": 1,
		"pageSize": 10,
		"pageTotal": 1,
		"totalCount": 2
	}
}
```

**错误响应示例**

```json
{
	"status": "error",
	"code": 401,
	"info": "用户未登录或登录令牌失效",
	"data": null
}
```

```json
{
	"status": "error",
	"code": 500,
	"info": "服务器返回错误,请联系管理员",
	"data": null
}
```

**错误码说明**

| 错误码 | 说明                             |
| :----- | :------------------------------- |
| 401    | 用户未登录或登录令牌失效         |
| 403    | 无权限访问该接口                 |
| 500    | 服务器内部错误                   |

**业务逻辑说明**

1. **数据过滤**：接口自动设置`type=1`，只返回企业账号，不包含市账号
2. **关联查询**：通过`LEFT JOIN`关联`enterprise_info`表，获取企业详细信息
3. **字段映射**：企业名称使用`COALESCE(name_zh, name_en, '')`，优先显示中文名称，如果没有则显示英文名称
4. **分页处理**：支持标准分页，返回总记录数和总页数，便于前端分页控件显示

---

## 三、账号管理相关接口

### 3.1 修改账号状态

- **请求方法**：POST
- **请求路径**：`/api/account/changeStatus`
- **功能描述**：省级管理员修改企业账号或市账号的状态，可以启用或停用账号。停用后的账号无法登录系统。
- **请求头**：
  - `Content-Type: application/json`
  - Cookie中需要包含登录token

**请求参数（Body）**

| 参数名   | 类型   | 是否必填 | 说明                         |
| :------- | :----- | :------- | :--------------------------- |
| username | String | 是       | 要修改状态的账号用户名       |
| status   | INT    | 是       | 账号状态：0-正常（启用），1-停用（禁用） |

**请求示例**

```json
{
	"username": "RInQF54wd5",
	"status": 1
}
```

**响应参数（data字段）**

成功时`data`字段为`null`，无需返回额外数据。

**成功响应示例**

```json
{
	"status": "success",
	"code": 200,
	"info": "账号状态修改成功",
	"data": null
}
```

**错误响应示例**

```json
{
	"status": "error",
	"code": 400,
	"info": "用户名不能为空",
	"data": null
}
```

```json
{
	"status": "error",
	"code": 404,
	"info": "账号不存在",
	"data": null
}
```

```json
{
	"status": "error",
	"code": 400,
	"info": "状态值无效，只能为0或1",
	"data": null
}
```

**错误码说明**

| 错误码 | 说明                             |
| :----- | :------------------------------- |
| 400    | 请求参数错误或格式不合法         |
| 401    | 用户未登录或登录令牌失效         |
| 403    | 无权限访问该接口                 |
| 404    | 账号不存在                       |
| 500    | 服务器内部错误                   |

**业务逻辑说明**

1. **状态值**：
   - `0`：正常（启用），账号可以正常登录和使用
   - `1`：停用（禁用），账号无法登录系统
   
2. **状态修改影响**：
   - 停用账号后，该账号立即无法登录系统
   - 启用账号后，账号恢复正常使用
   - 修改状态不影响账号的历史数据和关联的企业信息

3. **权限控制**：
   - 只有省级管理员可以修改账号状态
   - 不能修改自己的账号状态（防止误操作导致无法登录）

---

## 四、公共错误码说明

### HTTP状态码

| 状态码 | 说明                     |
| :----- | :----------------------- |
| 200    | 请求成功                 |
| 400    | 请求参数错误或格式不合法 |
| 401    | 用户未登录或登录令牌失效 |
| 403    | 无权限访问该接口         |
| 404    | 请求的资源不存在         |
| 500    | 服务器内部错误           |

### 业务错误码

| 错误码 | 说明                             |
| :----- | :------------------------------- |
| 600    | 账号类型参数错误                 |
| 601    | 企业账号必须提供企业信息         |
| 602    | 市账号必须提供市编码             |
| 603    | 企业名称不能为空                 |
| 604    | 组织机构代码格式不正确           |
| 605    | 电子邮箱格式不正确               |
| 606    | 联系电话格式不正确               |
| 607    | 邮政编码格式不正确               |

---

## 五、数据字典说明

### 5.1 账号类型（type）

| 值 | 说明     |
| :-- | :------- |
| 1  | 企业账号 |
| 2  | 市账号   |

### 5.2 账号状态（status）

| 值 | 说明   |
| :-- | :----- |
| 0  | 正常   |
| 1  | 停用   |

### 5.3 企业状态（enterpriseInfo.status）

| 值 | 说明                         |
| :-- | :--------------------------- |
| 0  | 创建未备案                   |
| 1  | 已备案未审核                 |
| 2  | 已退回                       |
| 3  | 正常（已备案已审核）         |
| 4  | 倒闭                         |

---

## 六、接口调用示例

### 6.1 完整创建企业账号流程

**步骤1：省级管理员登录**

```http
POST /api/account/login
Content-Type: application/json

{
	"username": "admin",
	"password": "123456"
}
```

**步骤2：创建企业账号**

```http
POST /api/account/createAccount
Content-Type: application/json
Cookie: token=xxx

{
	"type": 1,
	"enterpriseInfo": {
		"name": "云南XX科技有限公司",
		"orgCode": "91530000MA6K3H9X7C",
		"region": 1,
		"nature": 7,
		"industry": 65,
		"industryDesc": "软件开发、信息技术咨询",
		"contactName": "张XX",
		"address": "云南省昆明市五华区XX街道XX号",
		"postalCode": "650000",
		"phoneNum": "13800138000",
		"email": "zhang@xxcompany.com"
	}
}
```

**响应**

```json
{
	"status": "success",
	"code": 200,
	"info": "企业账号创建成功",
	"data": {
		"username": "RInQF54wd5",
		"password": "O8tlZlHzXS"
	}
}
```

**步骤3：查询企业账号列表验证**

```http
GET /api/account/loadAllEnterpriseAccount?pageNo=1&pageSize=10
Cookie: token=xxx
```

### 6.2 使用cURL调用示例

**创建企业账号**

```bash
curl -X POST "http://localhost:8080/api/account/createAccount" \
  -H "Content-Type: application/json" \
  -H "Cookie: token=your_token_here" \
  -d '{
    "type": 1,
    "enterpriseInfo": {
      "name": "云南XX科技有限公司"
    }
  }'
```

**查询企业账号列表**

```bash
curl -X GET "http://localhost:8080/api/account/loadAllEnterpriseAccount?pageNo=1&pageSize=10" \
  -H "Cookie: token=your_token_here"
```

**修改账号状态**

```bash
curl -X POST "http://localhost:8080/api/account/changeStatus" \
  -H "Content-Type: application/json" \
  -H "Cookie: token=your_token_here" \
  -d '{
    "username": "RInQF54wd5",
    "status": 1
  }'
```

---

## 七、注意事项

### 7.1 安全建议

1. **密码管理**：
   - 系统自动生成的密码为随机字符串，建议通过安全渠道（如加密邮件）发送给企业
   - 首次登录后建议企业修改密码（如果实现了密码修改功能）
   - 管理员不应在前端界面明文显示密码，建议使用"显示/隐藏"功能

2. **权限控制**：
   - 所有账号管理接口仅限省级管理员访问
   - 建议定期检查管理员账号权限
   - 记录所有账号操作日志（如果实现了日志功能）

3. **数据验证**：
   - 创建账号前应验证企业信息的唯一性（如组织机构代码、联系电话、邮箱等）
   - 建议对输入数据进行严格的前后端双重验证

### 7.2 性能建议

1. **分页查询**：
   - 建议`pageSize`不超过100，避免一次查询过多数据
   - 大量数据查询时建议使用索引字段进行过滤

2. **缓存策略**：
   - 账号列表查询可以考虑使用Redis缓存，减少数据库压力
   - 企业信息字典数据建议缓存

### 7.3 业务规则

1. **企业账号创建**：
   - 创建企业账号时会同时创建企业信息记录
   - 企业信息可以后续通过备案流程完善
   - 企业账号创建后，企业可以使用账号登录系统

2. **账号状态管理**：
   - 停用账号不会删除账号数据和关联的企业信息
   - 停用账号后可以随时重新启用
   - 建议在停用前通知企业相关人员

3. **数据关联**：
   - 企业账号与企业信息通过`enterprise_id`字段关联
   - 删除企业账号需要谨慎，建议先检查是否有关联的报表数据


# 企业端后端单测清单（含预期结果）

围绕企业端后端的主要模块，列出建议覆盖的单元测试点及预期结果，便于按模块补齐测试。

## 报表核心

| 模块/类 | 关键职责 | 单测重点 | 预期结果 |
| --- | --- | --- | --- |
| `service/report/ReportApplicationService.java` | 报表取数/创建、草稿/提交/重提、幂等、历史/趋势、可编辑判定 | 1) `getByEnterpriseAndPeriod` 新建/已有报表的编辑与回填逻辑。<br>2) `getPreviousPeriodEmployeeCount` 历史人数筛选。<br>3) `saveDraft` 草稿保存/状态。<br>4) `submit(cmd)` 状态切换。<br>5) `submit(cmd,key)` 幂等键与截止校验。<br>6) `resubmit` 仅驳回可重提、幂等。<br>7) `getCurrentPeriods` 窗口过滤。<br>8) `getReportList`/`getReportHistory` 可编辑标志与期次时间。<br>9) `getEmploymentTrend` 人数过滤与排序。 | 1) 新报表有历史则 `initial_employees`=上一期调查期人数并锁定；无历史不锁；已有报表仅 status=0/5 且无新版本可编辑，其他状态锁定。<br>2) 仅返回 status=4 且 period_id 小于当前期的最近一条人数，若无则 null。<br>3) 无历史则建档期人数覆盖为当前调查期人数；report_info/enterprise_report_info 持久化，status=0。<br>4) 提交后最新记录 status=1 且 report_info 数据存在。<br>5) 缺幂等键抛业务异常；同 key 重复调用不重复写库；now>=period_end 抛业务异常。<br>6) 非 status=5 抛业务异常；生成新 report_id，old_report_id 指向旧版；同幂等键重复调用不重复写库。<br>7) 返回列表仅含 `period_start<=now<period_end` 的期次。<br>8) period 起止时间被补齐，可编辑标志按状态/版本计算正确。<br>9) 调查期人数为空的记录被过滤，趋势按期次升序。 |
| `assembler/ReportAssembler.java` | DTO/PO/VO 转换、减员原因映射 | 1) code↔id 映射。<br>2) 原因说明落库。<br>3) VO 时间/旧版回填。 | 1) 给定 code 取得对应 id，反向亦然。<br>2) 非 OTHER 原因说明写入 reasonX_desc；OTHER 类型写入 other_reason；说明不丢失。<br>3) 时间格式 `yyyy-MM-dd HH:mm:ss`，oldId 透出。 |
| `service/report/PeriodUtils.java` | 期次格式互转 | 合法/非法期次转换 | 合法 `2025-01` -> `202501`；非法格式抛 IllegalArgumentException；`202501` -> `2025-01`，其他长度保持原样。 |

## 报表接口

| 模块/类 | 关键职责 | 单测重点 | 预期结果 |
| --- | --- | --- | --- |
| `controller/report/ReportController.java` | 报表接口参数校验、Token 取企业 | 1) `get` 缺 `reporting_period`。<br>2) `saveDraft`/`submit`/`resubmit` 数值校验。<br>3) `submit`/`resubmit` 幂等键必填。<br>4) 登录态校验。 | 1) 缺 period 抛异常。<br>2) 人数负数、current>initial、超上限抛异常。<br>3) 缺 Idempotency-Key 抛业务异常。<br>4) 无/失效 token 抛业务异常。 |
| `dto/report/ReportCommand.java` | Bean Validation | 必填/范围校验 | `reporting_period` 为空、人数为负、说明超长时校验失败并返回提示。 |

## 通知中心

| 模块/类 | 关键职责 | 单测重点 | 预期结果 |
| --- | --- | --- | --- |
| `service/notice/NoticeApplicationService.java` | 通知列表/详情/未读 | 1) 列表过滤与分页。<br>2) 详情权限与已读记录。<br>3) 未读计数行为。 | 1) 仅返回 status=1、noticeStatus 1/2 且在生效期的通知；分页参数被透传。<br>2) 不存在/无权限抛异常；重复已读不报错，插入异常不影响返回。<br>3) 当前实现返回有效通知总数（未扣已读），测试需记录此行为。 |
| `controller/notice/NoticeController.java` | 路由/参数绑定 | 参数绑定与返回包装 | 路由/参数正确传递到 service，ResponseVO 包装成功。 |

## 账号与企业资料

| 模块/类 | 关键职责 | 单测重点 | 预期结果 |
| --- | --- | --- | --- |
| `controller/AccountController.java` | 登录/登出、企业信息获取 | 1) 登录参数校验。<br>2) 登录后 Cookie/旧 token 处理。<br>3) 退出清理。<br>4) 获取企业信息登录态校验。 | 1) 用户名/密码为空抛业务异常。<br>2) 登录成功写 Cookie，旧 token 清理；返回 TokenInfoVO。<br>3) 退出清理 Cookie/Redis。<br>4) token 缺失或过期抛业务异常。 |
| `controller/info/InfoController.java` | 企业资料提交/更新/查询 | 1) token 解析 enterpriseId。<br>2) `submitProfile` 写入覆盖字段。<br>3) `updateProfile` 仅 status=3 可改。<br>4) `check-status` 返回正确状态或报错。 | 1) 空/无效 token 抛错。<br>2) 覆盖 enterpriseId/status/updatedAt 并成功更新。<br>3) status≠3 返回错误；status=3 时保留不可变字段后成功更新。<br>4) 企业不存在返回错误，存在返回 status。 |

## 词典与工具

| 模块/类 | 关键职责 | 单测重点 | 预期结果 |
| --- | --- | --- | --- |
| `dictionary/DictionaryService.java` | JSON 词典加载、映射 | 1) PostConstruct 加载。<br>2) “other” 规范化。<br>3) code↔id 映射。 | 1) 加载成功返回非空列表；读取失败抛异常。<br>2) “other” 规范为 `OTHER`。<br>3) code↔id 映射一致。 |
| `controller/DictionaryController.java` | 词典接口 | 列表返回 | 返回完整词典列表，ResponseVO 为成功状态。 |

## 认证拦截

| 模块/类 | 关键职责 | 单测重点 | 预期结果 |
| --- | --- | --- | --- |
| `interceptor/AuthInterceptor.java` + `config/WebConfig.java` | 登录拦截/放行配置 | 1) 无 Cookie/过期 token。<br>2) 有效 token。<br>3) 放行路径。 | 1) 返回 401 且 JSON 包含错误信息。<br>2) 请求放行，request 注入 enterpriseInfo 和 token。<br>3) 静态/放行路径不被拦截。 |

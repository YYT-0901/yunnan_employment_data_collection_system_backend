package com.yunnanprovince.aspect;

import com.yunnancommon.annotation.OperationLog;
import com.yunnancommon.enums.AccountTypeEnum;
import com.yunnancommon.service.EnterpriseInfoService;
import com.yunnanprovince.config.AppConfig;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.sql.Types;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

@Aspect
@Component
public class OperationLogAspect {

    private final JdbcTemplate jdbcTemplate;
    private final AppConfig appConfig;
    private final EnterpriseInfoService enterpriseInfoService;

    public OperationLogAspect(JdbcTemplate jdbcTemplate, AppConfig appConfig, EnterpriseInfoService enterpriseInfoService) {
        this.jdbcTemplate = jdbcTemplate;
        this.appConfig = appConfig;
        this.enterpriseInfoService = enterpriseInfoService;
    }

    // 本地缓存的退回原因/问题字段字典，作为对 DictUtils 反射失败时的兜底
    private static final java.util.Map<String, String> LOCAL_RETURN_REASON = new java.util.concurrent.ConcurrentHashMap<>();
    private static final java.util.Map<String, String> LOCAL_RETURN_FIELDS = new java.util.concurrent.ConcurrentHashMap<>();
    private static volatile boolean LOCAL_DICT_LOADED = false;

    private void ensureLocalDictLoaded() {
        if (LOCAL_DICT_LOADED) return;
        synchronized (OperationLogAspect.class) {
            if (LOCAL_DICT_LOADED) return;
            // 加载退回原因分类
            try {
                org.springframework.core.io.ClassPathResource res = new org.springframework.core.io.ClassPathResource("dict/return_reason_category.json");
                try (java.io.InputStream is = res.getInputStream()) {
                    com.fasterxml.jackson.databind.ObjectMapper om = new com.fasterxml.jackson.databind.ObjectMapper();
                    com.fasterxml.jackson.databind.JsonNode root = om.readTree(is);
                    if (root != null && root.isArray()) {
                        for (com.fasterxml.jackson.databind.JsonNode n : root) {
                            com.fasterxml.jackson.databind.JsonNode code = n.get("code");
                            com.fasterxml.jackson.databind.JsonNode name = n.get("name");
                            if (code != null && name != null) LOCAL_RETURN_REASON.put(code.asText(), name.asText());
                        }
                    }
                }
            } catch (Exception ignore) {}
            // 加载退回问题字段
            try {
                org.springframework.core.io.ClassPathResource res = new org.springframework.core.io.ClassPathResource("dict/return_problem_fields.json");
                try (java.io.InputStream is = res.getInputStream()) {
                    com.fasterxml.jackson.databind.ObjectMapper om = new com.fasterxml.jackson.databind.ObjectMapper();
                    com.fasterxml.jackson.databind.JsonNode root = om.readTree(is);
                    if (root != null && root.isArray()) {
                        for (com.fasterxml.jackson.databind.JsonNode n : root) {
                            com.fasterxml.jackson.databind.JsonNode key = n.get("key");
                            com.fasterxml.jackson.databind.JsonNode label = n.get("label");
                            if (key != null && label != null) LOCAL_RETURN_FIELDS.put(key.asText(), label.asText());
                        }
                    }
                }
            } catch (Exception ignore) {}
            LOCAL_DICT_LOADED = true;
        }
    }

    @Pointcut("@annotation(com.yunnancommon.annotation.OperationLog)")
    public void opLogPointcut() {}

    @Around("opLogPointcut() && @annotation(op)")
    public Object around(ProceedingJoinPoint pjp, OperationLog op) throws Throwable {
        long start = System.currentTimeMillis();
        Integer responseStatus = 200;
        String errorMessage = null;
        Object result = null;
        // 预取更新前数据（用于仅输出变更字段）
        com.yunnancommon.entity.po.EnterpriseInfo oldInfoForUpdate = null;
        HttpServletRequest preReq = currentRequest();
        String preUrl = preReq != null ? preReq.getRequestURI() : null;
        if (preUrl != null && preUrl.contains("/account/updateEnterprise/")) {
            String eidPre = null;
            Object uriVars = preReq.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
            if (uriVars instanceof java.util.Map<?,?> map) {
                Object eidObj = map.get("enterpriseId");
                if (eidObj != null) eidPre = String.valueOf(eidObj);
            }
            if (eidPre == null) {
                eidPre = extractIdFromUri(preUrl, "/updateEnterprise/");
            }
            if (eidPre != null && !eidPre.isEmpty()) {
                try { oldInfoForUpdate = enterpriseInfoService.getEnterpriseInfoByEnterpriseId(eidPre); } catch (Exception ignore) {}
            }
        }

        try {
            result = pjp.proceed();
            return result;
        } catch (Throwable ex) {
            responseStatus = 500;
            errorMessage = ex.getMessage();
            throw ex;
        } finally {
            long cost = System.currentTimeMillis() - start;
            HttpServletRequest request = currentRequest();
            if (request != null) {
                String requestUrl = request.getRequestURI();
                String requestMethod = request.getMethod();
                // Compute effective method for logging
                String override = request.getHeader("X-HTTP-Method-Override");
                if (override == null || override.isEmpty()) {
                    override = request.getHeader("X-Method-Override");
                }
                if (override == null || override.isEmpty()) {
                    String[] m = request.getParameterMap().get("_method");
                    if (m != null && m.length > 0) override = m[0];
                }
                String methodToSave = (override != null && !override.isEmpty()) ? override.toUpperCase() : requestMethod;
                // If frontend uses POST to call delete endpoint, log as DELETE
                if (methodToSave.equalsIgnoreCase("POST") && requestUrl != null && (requestUrl.contains("/account/deleteEnterprise/") || requestUrl.contains("/deleteEnterprise"))) {
                    methodToSave = "DELETE";
                }

                // 1) 优先从 request attribute 获取 (Controller 显式设置的值)，其次取请求参数
                Object attrEid = request.getAttribute("enterpriseId");
                String enterpriseId = (attrEid != null) ? String.valueOf(attrEid) : request.getParameter("enterpriseId");

                // 2) 路径变量中提取（用于 update/delete 等）
                if (enterpriseId == null || enterpriseId.isEmpty()) {
                    Object uriVars = request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
                    if (uriVars instanceof java.util.Map<?,?> map) {
                        Object eidObj = map.get("enterpriseId");
                        if (eidObj != null) enterpriseId = String.valueOf(eidObj);
                    }
                    enterpriseId = extractIdFromUri(requestUrl, "/updateEnterprise/");
                    if (enterpriseId == null) {
                        enterpriseId = extractIdFromUri(requestUrl, "/deleteEnterprise/");
                    }
                }
                // 3) createAccount 成功返回时，从返回对象解析（反射，以避免编译依赖）
                if ((enterpriseId == null || enterpriseId.isEmpty()) && requestUrl.contains("/account/createAccount") && result != null) {
                    try {
                        Object data = null;
                        try { data = result.getClass().getMethod("getData").invoke(result); } catch (NoSuchMethodException ignored) {}
                        if (data != null) {
                            // 3.1 直接方法
                            try { enterpriseId = String.valueOf(data.getClass().getMethod("getEnterpriseId").invoke(data)); }
                            catch (NoSuchMethodException ignored) {}
                            // 3.2 若 data 是 Map
                            if ((enterpriseId == null || enterpriseId.isEmpty()) && data instanceof java.util.Map<?,?> dm) {
                                Object eid = dm.get("enterpriseId");
                                if (eid != null) enterpriseId = String.valueOf(eid);
                            }
                            // 3.3 若 data 内嵌 enterpriseInfo
                            if (enterpriseId == null || enterpriseId.isEmpty()) {
                                try {
                                    Object ei = data.getClass().getMethod("getEnterpriseInfo").invoke(data);
                                    if (ei != null) {
                                        try { enterpriseId = String.valueOf(ei.getClass().getMethod("getEnterpriseId").invoke(ei)); }
                                        catch (NoSuchMethodException ignored) {}
                                    }
                                } catch (NoSuchMethodException ignored) {}
                            }
                        }
                    } catch (Exception ignore) {
                        // 解析失败不影响业务
                    }
                }

                // 额外从 request attribute 读取 createAccount 时设置的类型
                String typeAttr = null;
                Object t = request.getAttribute("type");
                if (t != null) typeAttr = String.valueOf(t);

                // 尝试从方法参数中获取 CreateAccountDto 以补充详细信息
                String extraDetail = parseCreateAccountDetail(pjp.getArgs());

                String requestParams = buildFriendlyParams(requestUrl, request.getParameterMap(), enterpriseId, typeAttr);
                if (extraDetail != null) {
                    requestParams = requestParams + "；" + extraDetail;
                }
                String updateDetail = null;
                if (requestUrl != null && requestUrl.contains("/account/updateEnterprise/")) {
                    updateDetail = parseUpdateEnterpriseDetail(oldInfoForUpdate, pjp.getArgs());
                }
                if (updateDetail != null && !updateDetail.isEmpty()) {
                    requestParams = updateDetail; // 仅显示更新信息，不再输出企业ID
                }
                String username = appConfig != null ? appConfig.getUsername() : null;

                // 插入 log_info 最小字段集
                String sql = "INSERT INTO log_info (username, user_type, enterprise_id, operation_module, operation_desc, " +
                        "request_url, request_method, request_params, response_status, execution_time, error_message, operation_time) " +
                        "VALUES (?,?,?,?,?,?,?,?,?,?,?,NOW())";
                
                // 若是删除操作，enterpriseId 所指记录已不存在，插入外键会报错。因此若是DELETE且enterpriseId不为空，存库时置NULL
                // (依然保留在 requestParams 里做记录)
                String dbEnterpriseId = enterpriseId;
                if ("DELETE".equalsIgnoreCase(methodToSave) && dbEnterpriseId != null) {
                    dbEnterpriseId = null;
                }

                Object[] args = new Object[]{
                        username,
                        AccountTypeEnum.PROVINCE.getCode(),
                        dbEnterpriseId,
                        op.module(),
                        op.operation(),
                        requestUrl,
                        methodToSave,
                        requestParams,
                        responseStatus,
                        cost,
                        errorMessage
                };
                int[] types = new int[]{
                        Types.VARCHAR,
                        Types.INTEGER,
                        Types.VARCHAR,
                        Types.VARCHAR,
                        Types.VARCHAR,
                        Types.VARCHAR,
                        Types.VARCHAR,
                        Types.LONGVARCHAR,
                        Types.INTEGER,
                        Types.BIGINT,
                        Types.LONGVARCHAR
                };
                try {
                    jdbcTemplate.update(sql, args, types);
                } catch (Exception ignore) {
                    // 不影响业务流程
                }
            }
        }
    }

    private HttpServletRequest currentRequest() {
        RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
        if (attrs instanceof ServletRequestAttributes sra) {
            return sra.getRequest();
        }
        return null;
    }

    private String buildParamString(Map<String, String[]> paramMap) {
        if (paramMap == null || paramMap.isEmpty()) return "";
        StringJoiner sj = new StringJoiner("&");
        for (Map.Entry<String, String[]> e : paramMap.entrySet()) {
            String key = e.getKey();
            String[] vals = e.getValue();
            if (vals == null) continue;
            for (String v : vals) {
                sj.add(key + "=" + v);
            }
        }
        return sj.toString();
    }

    private String buildFriendlyParams(String uri, Map<String, String[]> paramMap, String enterpriseIdFromPath, String typeFromAttr) {
        if (paramMap == null) return "";
        // 针对修改企业状态，输出大白话
        if (uri != null && uri.contains("/account/changeEnterpriseStatus")) {
            String enterpriseId = first(paramMap.get("enterpriseId"));
            String status = first(paramMap.get("status"));
            String statusName = mapStatusName(status);
            // 基础行：企业ID；目标状态
            String base = (enterpriseId != null ? ("企业ID：" + enterpriseId) : "")
                    + (status != null ? ( (enterpriseId != null ? "；" : "") + "目标状态：" + statusName + "(" + status + ")") : "");

            // 若为退回(2)，拼接退回详情（无需diff）
            if ("2".equals(status)) {
                StringBuilder sb = new StringBuilder();
                if (!base.isEmpty()) sb.append(base).append("\n");
                sb.append("【退回企业】");

                // 主要问题类型（单选）
                String categoryCode = first(paramMap.get("reasonCategory"));
                String categoryName = resolveReturnReasonCategory(categoryCode);
                if (categoryName != null && !categoryName.isEmpty()) {
                    sb.append("\n主要问题类型：").append(categoryName);
                }

                // 具体问题字段（多选）: 支持多值参数或逗号分隔
                String[] fieldsArr = paramMap.get("reasonFields");
                java.util.List<String> fieldKeys = new java.util.ArrayList<>();
                if (fieldsArr != null && fieldsArr.length > 0) {
                    for (String fv : fieldsArr) {
                        if (fv == null || fv.isEmpty()) continue;
                        if (fv.contains(",")) {
                            for (String k : fv.split(",")) { if (!k.isEmpty()) fieldKeys.add(k.trim()); }
                        } else {
                            fieldKeys.add(fv.trim());
                        }
                    }
                }
                if (!fieldKeys.isEmpty()) {
                    java.util.List<String> labels = new java.util.ArrayList<>();
                    for (String k : fieldKeys) {
                        String label = resolveReturnProblemFieldLabel(k);
                        if (label != null && !label.isEmpty()) labels.add(label);
                    }
                    if (!labels.isEmpty()) {
                        sb.append("\n具体问题字段：").append(String.join("、", labels));
                    }
                }

                // 详细说明（选填）
                String detail = first(paramMap.get("detail"));
                if (detail != null && !detail.trim().isEmpty()) {
                    sb.append("\n详细说明：").append(detail.trim());
                }
                return sb.toString();
            }

            // 非退回的常规状态修改
            StringJoiner sj = new StringJoiner("；");
            if (enterpriseId != null) sj.add("企业ID：" + enterpriseId);
            if (status != null) sj.add("目标状态：" + statusName + "(" + status + ")");
            return sj.toString();
        }
        // 更新企业信息：从路径变量兜底企业ID
        if (uri != null && uri.contains("/account/updateEnterprise/")) {
            String eid = enterpriseIdFromPath;
            if (eid == null) eid = extractIdFromUri(uri, "/updateEnterprise/");
            StringJoiner sj = new StringJoiner("；");
            if (eid != null) sj.add("企业ID：" + eid);
            sj.add("更新企业信息");
            return sj.toString();
        }
        // 删除企业：从路径变量兜底企业ID
        if (uri != null && uri.contains("/account/deleteEnterprise/")) {
            String eid = enterpriseIdFromPath;
            if (eid == null) eid = extractIdFromUri(uri, "/deleteEnterprise/");
            StringJoiner sj = new StringJoiner("；");
            if (eid != null) sj.add("企业ID：" + eid);
            sj.add("删除企业");
            return sj.toString();
        }
        // 创建账号：提示类型 + 企业ID（若能解析到）
        if (uri != null && uri.contains("/account/createAccount")) {
            String type = first(paramMap.get("type"));
            if (type == null) type = typeFromAttr; // JSON Body 情况
            String typeName = mapAccountType(type);
            StringJoiner sj = new StringJoiner("；");
            if (typeName != null) sj.add("账号类型：" + typeName + (type != null ? "(" + type + ")" : ""));
            if (enterpriseIdFromPath != null) sj.add("企业ID：" + enterpriseIdFromPath);
            return sj.toString();
        }
        // 默认退回原始参数串
        return buildParamString(paramMap);
    }

    private String first(String[] arr) {
        return (arr != null && arr.length > 0) ? arr[0] : null;
    }

    // 企业状态映射：0-创建 1-备案 2-退回 3-正常 4-停用
    private String mapStatusName(String code) {
        if (code == null) return "";
        return switch (code) {
            case "0" -> "创建";
            case "1" -> "备案";
            case "2" -> "退回";
            case "3" -> "正常";
            case "4" -> "停用";
            default -> code;
        };
    }

    private String mapAccountType(String code) {
        if (code == null) return null;
        return switch (code) {
            case "0" -> "省账号";
            case "1" -> "企业账号";
            case "2" -> "市账号";
            default -> null;
        };
    }

    private String parseCreateAccountDetail(Object[] args) {
        if (args == null) return null;
        for (Object arg : args) {
            if (arg instanceof com.yunnancommon.entity.dto.CreateAccountDto dto) {
                if (dto.getEnterpriseInfo() != null) {
                    com.yunnancommon.entity.po.EnterpriseInfo info = dto.getEnterpriseInfo();
                    StringJoiner sj = new StringJoiner("\n");
                    sj.add("【企业详情】");
                    if (info.getName() != null) sj.add("企业名称：" + info.getName());
                    if (info.getOrgCode() != null) sj.add("组织机构代码：" + info.getOrgCode());
                    // 使用 getRegionPathName 获取完整层级路径（如：丽江市 / 宁蒗彝族自治县 / 战河镇）
                    // 注意：此处传入的是详细地区编码 (region)，DictUtils 会自动向上回溯
                    if (info.getRegion() != null) sj.add("所属地区：" + com.yunnancommon.utils.DictUtils.getRegionPathName(String.valueOf(info.getRegion())));
                    if (info.getNatureCode() != null || info.getNature() != null) sj.add("所属性质：" + com.yunnancommon.utils.DictUtils.getNaturePathName(info.getNatureCode(), info.getNature()));
                    if (info.getIndustryCode() != null || info.getIndustry() != null) sj.add("所属行业：" + com.yunnancommon.utils.DictUtils.getIndustryPathName(info.getIndustryCode(), info.getIndustry()));
                    if (info.getIndustryDesc() != null) sj.add("经营业务详情：" + info.getIndustryDesc());
                    if (info.getContactName() != null) sj.add("联系人名称：" + info.getContactName());
                    if (info.getPhoneNum() != null) sj.add("联系电话：" + info.getPhoneNum());
                    if (info.getPostalCode() != null) sj.add("邮政编码：" + info.getPostalCode());
                    if (info.getFaxNum() != null) sj.add("传真号：" + info.getFaxNum());
                    if (info.getAddress() != null) sj.add("联系人地址：" + info.getAddress());
                    if (info.getEmail() != null) sj.add("邮箱：" + info.getEmail());
                    if (info.getStatus() != null) sj.add("修改状态：" + mapStatusName(String.valueOf(info.getStatus())));
                    
                    return sj.toString();
                }
            }
        }
        return null;
    }

    private String parseUpdateEnterpriseDetail(com.yunnancommon.entity.po.EnterpriseInfo oldInfo,
                                              Object[] args) {
        if (args == null) return null;
        com.yunnancommon.entity.po.EnterpriseInfo info = null;
        for (Object arg : args) {
            if (arg instanceof com.yunnancommon.entity.po.EnterpriseInfo i) { info = i; break; }
        }
        if (info == null) return null;

        StringJoiner sj = new StringJoiner("\n");
        sj.add("【更新企业信息】");
        boolean hasAny = false;

        if (oldInfo != null) {
            // 仅输出真正变化的字段
            if (!equalsStr(oldInfo.getName(), info.getName()) && !isBlank(info.getName())) {
                sj.add("企业名称：" + info.getName()); hasAny = true; }
            if (!equalsStr(oldInfo.getOrgCode(), info.getOrgCode()) && !isBlank(info.getOrgCode())) {
                sj.add("组织机构代码：" + info.getOrgCode()); hasAny = true; }
            if (!equalsInt(oldInfo.getRegion(), info.getRegion()) && info.getRegion() != null) {
                sj.add("所属地区：" + com.yunnancommon.utils.DictUtils.getRegionPathName(String.valueOf(info.getRegion()))); hasAny = true; }
            if ((!equalsInt(oldInfo.getNatureCode(), info.getNatureCode())) || (!equalsInt(oldInfo.getNature(), info.getNature()))) {
                if (info.getNatureCode() != null || info.getNature() != null) {
                    sj.add("所属性质：" + com.yunnancommon.utils.DictUtils.getNaturePathName(info.getNatureCode(), info.getNature())); hasAny = true; }
            }
            if ((!equalsInt(oldInfo.getIndustryCode(), info.getIndustryCode())) || (!equalsInt(oldInfo.getIndustry(), info.getIndustry()))) {
                if (info.getIndustryCode() != null || info.getIndustry() != null) {
                    sj.add("所属行业：" + com.yunnancommon.utils.DictUtils.getIndustryPathName(info.getIndustryCode(), info.getIndustry())); hasAny = true; }
            }
            if (!equalsStr(oldInfo.getIndustryDesc(), info.getIndustryDesc()) && !isBlank(info.getIndustryDesc())) {
                sj.add("经营业务详情：" + info.getIndustryDesc()); hasAny = true; }
            if (!equalsStr(oldInfo.getContactName(), info.getContactName()) && !isBlank(info.getContactName())) {
                sj.add("联系人名称：" + info.getContactName()); hasAny = true; }
            if (!equalsStr(oldInfo.getPhoneNum(), info.getPhoneNum()) && !isBlank(info.getPhoneNum())) {
                sj.add("联系电话：" + info.getPhoneNum()); hasAny = true; }
            if (!equalsStr(oldInfo.getPostalCode(), info.getPostalCode()) && !isBlank(info.getPostalCode())) {
                sj.add("邮政编码：" + info.getPostalCode()); hasAny = true; }
            if (!equalsStr(oldInfo.getFaxNum(), info.getFaxNum()) && !isBlank(info.getFaxNum())) {
                sj.add("传真号：" + info.getFaxNum()); hasAny = true; }
            if (!equalsStr(oldInfo.getAddress(), info.getAddress()) && !isBlank(info.getAddress())) {
                sj.add("联系人地址：" + info.getAddress()); hasAny = true; }
            if (!equalsStr(oldInfo.getEmail(), info.getEmail()) && !isBlank(info.getEmail())) {
                sj.add("邮箱：" + info.getEmail()); hasAny = true; }
            if (!equalsInt(oldInfo.getStatus(), info.getStatus()) && info.getStatus() != null) {
                sj.add("修改状态：" + mapStatusName(String.valueOf(info.getStatus()))); hasAny = true; }
        } else {
            // 兜底：仅列出非空字段
            if (!isBlank(info.getName())) { sj.add("企业名称：" + info.getName()); hasAny = true; }
            if (!isBlank(info.getOrgCode())) { sj.add("组织机构代码：" + info.getOrgCode()); hasAny = true; }
            if (info.getRegion() != null) { sj.add("所属地区：" + com.yunnancommon.utils.DictUtils.getRegionPathName(String.valueOf(info.getRegion()))); hasAny = true; }
            if (info.getNatureCode() != null || info.getNature() != null) { sj.add("所属性质：" + com.yunnancommon.utils.DictUtils.getNaturePathName(info.getNatureCode(), info.getNature())); hasAny = true; }
            if (info.getIndustryCode() != null || info.getIndustry() != null) { sj.add("所属行业：" + com.yunnancommon.utils.DictUtils.getIndustryPathName(info.getIndustryCode(), info.getIndustry())); hasAny = true; }
            if (!isBlank(info.getIndustryDesc())) { sj.add("经营业务详情：" + info.getIndustryDesc()); hasAny = true; }
            if (!isBlank(info.getContactName())) { sj.add("联系人名称：" + info.getContactName()); hasAny = true; }
            if (!isBlank(info.getPhoneNum())) { sj.add("联系电话：" + info.getPhoneNum()); hasAny = true; }
            if (!isBlank(info.getPostalCode())) { sj.add("邮政编码：" + info.getPostalCode()); hasAny = true; }
            if (!isBlank(info.getFaxNum())) { sj.add("传真号：" + info.getFaxNum()); hasAny = true; }
            if (!isBlank(info.getAddress())) { sj.add("联系人地址：" + info.getAddress()); hasAny = true; }
            if (!isBlank(info.getEmail())) { sj.add("邮箱：" + info.getEmail()); hasAny = true; }
            if (info.getStatus() != null) { sj.add("修改状态：" + mapStatusName(String.valueOf(info.getStatus()))); hasAny = true; }
        }

        return hasAny ? sj.toString() : null;
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private boolean equalsStr(String a, String b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        return a.equals(b);
    }

    private boolean equalsInt(Integer a, Integer b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        return a.intValue() == b.intValue();
    }

    private String extractIdFromUri(String uri, String marker) {
        if (uri == null || marker == null) return null;
        int idx = uri.indexOf(marker);
        if (idx < 0) return null;
        String tail = uri.substring(idx + marker.length());

        // 去除查询参数
        int qMark = tail.indexOf('?');
        if (qMark >= 0) tail = tail.substring(0, qMark);

        int slash = tail.indexOf('/');
        return slash >= 0 ? tail.substring(0, slash) : tail;
    }

    // 通过反射调用 DictUtils#getReturnReasonCategoryName，若不可用则回退到本地字典映射
    private String resolveReturnReasonCategory(String code) {
        try {
            Class<?> clazz = Class.forName("com.yunnancommon.utils.DictUtils");
            java.lang.reflect.Method m = clazz.getMethod("getReturnReasonCategoryName", String.class);
            Object val = m.invoke(null, code);
            String out = val != null ? String.valueOf(val) : null;
            if (out != null && !out.isEmpty() && (code == null || !out.equals(code))) {
                return out;
            }
        } catch (Throwable ignore) {
        }
        ensureLocalDictLoaded();
        String mapped = LOCAL_RETURN_REASON.get(code);
        return mapped != null ? mapped : (code == null ? "" : code);
    }

    // 通过反射调用 DictUtils#getReturnProblemFieldLabel，若不可用则回退到本地字典映射
    private String resolveReturnProblemFieldLabel(String key) {
        try {
            Class<?> clazz = Class.forName("com.yunnancommon.utils.DictUtils");
            java.lang.reflect.Method m = clazz.getMethod("getReturnProblemFieldLabel", String.class);
            Object val = m.invoke(null, key);
            String out = val != null ? String.valueOf(val) : null;
            if (out != null && !out.isEmpty() && (key == null || !out.equals(key))) {
                return out;
            }
        } catch (Throwable ignore) {
        }
        ensureLocalDictLoaded();
        String mapped = LOCAL_RETURN_FIELDS.get(key);
        return mapped != null ? mapped : (key == null ? "" : key);
    }
}

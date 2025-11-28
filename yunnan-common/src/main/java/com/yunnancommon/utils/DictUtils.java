package com.yunnancommon.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * 字典映射工具类
 * 用于处理各种字典值的映射转换
 */
@Slf4j
public class DictUtils {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    // 企业性质字典映射
    private static final Map<String, String> NATURE_DICT = new HashMap<>();
    // 行业字典映射
    private static final Map<String, String> INDUSTRY_DICT = new HashMap<>();
    // 地区字典映射
    private static final Map<String, String> REGION_DICT = new HashMap<>();
    // 就业减少类型映射 (索引从1开始)
    private static final Map<Integer, String> REDUCTION_TYPE_DICT = new HashMap<>();
    // 就业减少原因映射
    private static final Map<String, String> REDUCTION_REASON_DICT = new HashMap<>();
    // 退回原因分类（单选）：code -> name
    private static final Map<String, String> RETURN_REASON_CATEGORY = new HashMap<>();
    // 退回具体问题字段（多选）：key -> label
    private static final Map<String, String> RETURN_PROBLEM_FIELDS = new HashMap<>();

    // 云南地区全量字典映射 code -> name
    private static final Map<Integer, RegionNode> REGION_FULL_DICT = new HashMap<>();
    // 企业性质全量字典映射 (支持二级)
    private static final Map<Integer, RegionNode> NATURE_FULL_DICT = new HashMap<>();
    // 企业行业全量字典映射 (支持二级)
    private static final Map<Integer, RegionNode> INDUSTRY_FULL_DICT = new HashMap<>();

    private static class RegionNode {
        String name;
        Integer parentId;
        public RegionNode(String name, Integer parentId) {
            this.name = name;
            this.parentId = parentId;
        }
    }

    // 静态初始化块，加载所有字典文件
    static {
        try {
            // 加载企业性质字典
            loadNatureDict();
            // 加载行业字典
            loadIndustryDict();
            // 加载地区字典(旧)
            loadRegionDict();
            // 加载云南全量地区字典(新)
            loadYunnanRegionDict();
            // 加载性质/行业全量层级（若资源存在则启用）
            try { loadNatureFullDict(); } catch (Exception ignore) {}
            try { loadIndustryFullDict(); } catch (Exception ignore) {}
            // 加载就业减少类型字典
            loadReductionTypeDict();
            // 加载就业减少原因字典
            loadReductionReasonDict();
            // 加载退回原因分类与问题字段字典（若资源存在则启用）
            try { loadReturnReasonCategory(); } catch (Exception ignore) {}
            try { loadReturnProblemFields(); } catch (Exception ignore) {}
        } catch (Exception e) {
            log.error("加载字典文件失败", e);
        }
    }

    /**
     * 加载企业性质字典
     */
    private static void loadNatureDict() throws Exception {
        try (InputStream is = DictUtils.class.getClassLoader().getResourceAsStream("dict/nature_dict.json")) {
            JsonNode root = objectMapper.readTree(is);
            if (root.isObject()) {
                root.fields().forEachRemaining(entry -> {
                    NATURE_DICT.put(entry.getKey(), entry.getValue().asText());
                });
            }
        }
    }

    /**
     * 加载行业字典
     */
    private static void loadIndustryDict() throws Exception {
        try (InputStream is = DictUtils.class.getClassLoader().getResourceAsStream("dict/industry_dict.json")) {
            JsonNode root = objectMapper.readTree(is);
            if (root.isObject()) {
                root.fields().forEachRemaining(entry -> {
                    INDUSTRY_DICT.put(entry.getKey(), entry.getValue().asText());
                });
            }
        }
    }

    /**
     * 加载地区字典
     */
    private static void loadRegionDict() throws Exception {
        try (InputStream is = DictUtils.class.getClassLoader().getResourceAsStream("dict/region_dict.json")) {
            JsonNode root = objectMapper.readTree(is);
            if (root.isObject()) {
                root.fields().forEachRemaining(entry -> {
                    REGION_DICT.put(entry.getKey(), entry.getValue().asText());
                });
            }
        }
    }

    /**
     * 加载云南地区全量字典
     */
    private static void loadYunnanRegionDict() throws Exception {
        try (InputStream is = DictUtils.class.getClassLoader().getResourceAsStream("dict/yunnan_region_code.json")) {
            JsonNode root = objectMapper.readTree(is);
            if (root != null && root.isArray()) {
                parseRegionNode(root, 0);
            }
        }
    }

    private static void parseRegionNode(JsonNode nodes, Integer parentId) {
        if (nodes == null || !nodes.isArray()) return;
        for (JsonNode node : nodes) {
            if (node == null) continue;
            JsonNode codeNode = node.get("code");
            JsonNode nameNode = node.get("name");
            if (codeNode == null || nameNode == null) continue;
            int code = codeNode.asInt();
            String name = nameNode.asText();
            REGION_FULL_DICT.put(code, new RegionNode(name, parentId));
            JsonNode children = node.get("children");
            if (children != null && children.isArray() && children.size() > 0) {
                parseRegionNode(children, code);
            }
        }
    }

    private static void parseGenericHierarchy(JsonNode nodes, Integer parentId, Map<Integer, RegionNode> target) {
        if (nodes == null || !nodes.isArray()) return;
        for (JsonNode node : nodes) {
            if (node == null) continue;
            JsonNode codeNode = node.get("code");
            JsonNode nameNode = node.get("name");
            if (codeNode == null || nameNode == null) continue;
            int code = codeNode.asInt();
            String name = cleanName(nameNode.asText());
            target.put(code, new RegionNode(name, parentId));
            JsonNode children = node.get("children");
            if (children != null && children.isArray() && children.size() > 0) {
                parseGenericHierarchy(children, code, target);
            }
        }
    }

    /**
     * 加载企业性质全量层级（dict/enterprise_type.json）
     */
    private static void loadNatureFullDict() throws Exception {
        try (InputStream is = DictUtils.class.getClassLoader().getResourceAsStream("dict/enterprise_type.json")) {
            if (is == null) return; // 资源缺失则跳过
            JsonNode root = objectMapper.readTree(is);
            if (root != null && root.isArray()) {
                parseGenericHierarchy(root, 0, NATURE_FULL_DICT);
            }
        }
    }

    /**
     * 加载企业行业全量层级（dict/enterprise_industry.json）
     */
    private static void loadIndustryFullDict() throws Exception {
        try (InputStream is = DictUtils.class.getClassLoader().getResourceAsStream("dict/enterprise_industry.json")) {
            if (is == null) return; // 资源缺失则跳过
            JsonNode root = objectMapper.readTree(is);
            if (root != null && root.isArray()) {
                parseGenericHierarchy(root, 0, INDUSTRY_FULL_DICT);
            }
        }
    }

    /**
     * 加载就业减少类型字典
     */
    private static void loadReductionTypeDict() throws Exception {
        try (InputStream is = DictUtils.class.getClassLoader().getResourceAsStream("dict/employment_reduction_types.json")) {
            JsonNode root = objectMapper.readTree(is);
            if (root.isArray()) {
                for (int i = 0; i < root.size(); i++) {
                    JsonNode item = root.get(i);
                    // 索引从1开始
                    REDUCTION_TYPE_DICT.put(i + 1, item.get("label").asText());
                }
            }
        }
    }

    /**
     * 加载就业减少原因字典
     */
    private static void loadReductionReasonDict() throws Exception {
        try (InputStream is = DictUtils.class.getClassLoader().getResourceAsStream("dict/reduction_reason.json")) {
            JsonNode root = objectMapper.readTree(is);
            if (root.isObject()) {
                root.fields().forEachRemaining(entry -> {
                    REDUCTION_REASON_DICT.put(entry.getKey(), entry.getValue().asText());
                });
            }
        }
    }

    // 加载退回原因分类（array: [{code,name}]）
    private static void loadReturnReasonCategory() throws Exception {
        try (InputStream is = DictUtils.class.getClassLoader().getResourceAsStream("dict/return_reason_category.json")) {
            if (is == null) return;
            JsonNode root = objectMapper.readTree(is);
            if (root != null && root.isArray()) {
                for (JsonNode n : root) {
                    JsonNode code = n.get("code");
                    JsonNode name = n.get("name");
                    if (code != null && name != null) {
                        RETURN_REASON_CATEGORY.put(code.asText(), name.asText());
                    }
                }
            }
        }
    }

    // 加载退回问题字段（array: [{key,label}]）
    private static void loadReturnProblemFields() throws Exception {
        try (InputStream is = DictUtils.class.getClassLoader().getResourceAsStream("dict/return_problem_fields.json")) {
            if (is == null) return;
            JsonNode root = objectMapper.readTree(is);
            if (root != null && root.isArray()) {
                for (JsonNode n : root) {
                    JsonNode key = n.get("key");
                    JsonNode label = n.get("label");
                    if (key != null && label != null) {
                        RETURN_PROBLEM_FIELDS.put(key.asText(), label.asText());
                    }
                }
            }
        }
    }

    /**
     * 获取就业减少原因名称
     * @param reasonId 原因ID
     * @return 减少原因名称，如果未找到返回空字符串
     */
    public static String getReductionReasonName(String reasonId) {
        return REDUCTION_REASON_DICT.getOrDefault(reasonId, "");
    }

    /**
     * 获取就业减少原因名称（支持整数类型的ID）
     * @param reasonId 原因ID
     * @return 减少原因名称，如果未找到返回空字符串
     */
    public static String getReductionReasonName(int reasonId) {
        return getReductionReasonName(String.valueOf(reasonId));
    }

    /**
     * 获取企业性质名称
     * @param natureId 企业性质ID
     * @return 企业性质名称，如果未找到返回空字符串
     */
    public static String getEnterpriseNatureName(String natureId) {
        return NATURE_DICT.getOrDefault(natureId, "");
    }

    /**
     * 获取行业名称
     * @param industryId 行业ID
     * @return 行业名称，如果未找到返回空字符串
     */
    public static String getEnterpriseIndustryName(String industryId) {
        return INDUSTRY_DICT.getOrDefault(industryId, "");
    }

    /**
     * 获取地区名称
     * @param regionId 地区ID
     * @return 地区名称，如果未找到返回空字符串
     */
    public static String getRegionName(String regionId) {
        return REGION_DICT.getOrDefault(regionId, "");
    }

    /**
     * 获取城市名称（与地区名称相同）
     * @param cityCode 城市代码
     * @return 城市名称，如果未找到返回空字符串
     */
    public static String getCityName(String cityCode) {
        return getRegionName(cityCode);
    }

    /**
     * 获取就业减少类型名称
     * @param typeIndex 类型索引（从1开始）
     * @return 减少类型名称，如果未找到返回空字符串
     */
    public static String getReductionTypeName(int typeIndex) {
        return REDUCTION_TYPE_DICT.getOrDefault(typeIndex, "");
    }

    /**
     * 获取地区完整路径名称 (例如：丽江市 / 宁蒗彝族自治县 / 战河镇)
     */
    public static String getRegionPathName(String regionCodeStr) {
        try {
            Integer code = Integer.parseInt(regionCodeStr);
            if (!REGION_FULL_DICT.containsKey(code)) {
                return getRegionName(regionCodeStr); // 降级到旧字典
            }

            java.util.LinkedList<String> names = new java.util.LinkedList<>();
            Integer currentCode = code;
            while (currentCode != null && currentCode != 0) {
                RegionNode node = REGION_FULL_DICT.get(currentCode);
                if (node == null) break;
                names.addFirst(node.name);
                currentCode = node.parentId;
            }
            return String.join(" / ", names);
        } catch (Exception e) {
            return regionCodeStr;
        }
    }

    public static String getNaturePathName(Integer topCode, Integer subCode) {
        // 优先用全量层级
        if (subCode != null && NATURE_FULL_DICT.containsKey(subCode)) {
            return buildPath(subCode, NATURE_FULL_DICT);
        }
        if (topCode != null && NATURE_FULL_DICT.containsKey(topCode)) {
            return NATURE_FULL_DICT.get(topCode).name;
        }
        // 回退到一级字典 + 编码
        String topName = topCode == null ? null : NATURE_DICT.get(String.valueOf(topCode));
        if (topName == null || topName.isEmpty()) topName = topCode == null ? "" : String.valueOf(topCode);
        return subCode == null ? topName : (topName + " / " + subCode);
    }

    public static String getIndustryPathName(Integer topCode, Integer subCode) {
        // 优先用全量层级
        if (subCode != null && INDUSTRY_FULL_DICT.containsKey(subCode)) {
            return buildPath(subCode, INDUSTRY_FULL_DICT);
        }
        if (topCode != null && INDUSTRY_FULL_DICT.containsKey(topCode)) {
            return INDUSTRY_FULL_DICT.get(topCode).name;
        }
        // 回退到一级字典 + 编码
        String topName = topCode == null ? null : INDUSTRY_DICT.get(String.valueOf(topCode));
        if (topName == null || topName.isEmpty()) topName = topCode == null ? "" : String.valueOf(topCode);
        return subCode == null ? topName : (topName + " / " + subCode);
    }

    private static String buildPath(Integer code, Map<Integer, RegionNode> dict) {
        java.util.LinkedList<String> names = new java.util.LinkedList<>();
        Integer current = code;
        while (current != null && current != 0) {
            RegionNode node = dict.get(current);
            if (node == null) break;
            names.addFirst(node.name);
            current = node.parentId;
        }
        return String.join(" / ", names);
    }

    private static String cleanName(String s) {
        if (s == null) return null;
        String out = s;
        // 去掉行业/分类里常见的前缀 "A. ", "B. ", "08. " 等
        out = out.replaceFirst("^[A-Z]\\.\\s*", "");
        out = out.replaceFirst("^[0-9]{1,2}\\.\\s*", "");
        return out;
    }

    /**
     * 根据退回原因分类 code 获取中文名称
     */
    public static String getReturnReasonCategoryNameByCode(String code) {
        if (code == null) return "";
        return RETURN_REASON_CATEGORY.getOrDefault(code, "");
    }

    /**
     * 根据退回具体问题字段 key 获取中文标签
     */
    public static String getReturnProblemFieldNameByKey(String key) {
        if (key == null) return "";
        return RETURN_PROBLEM_FIELDS.getOrDefault(key, "");
    }
}
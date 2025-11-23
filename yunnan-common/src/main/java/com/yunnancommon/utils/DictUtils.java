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


    // 静态初始化块，加载所有字典文件
    static {
        try {
            // 加载企业性质字典
            loadNatureDict();
            // 加载行业字典
            loadIndustryDict();
            // 加载地区字典
            loadRegionDict();
            // 加载就业减少类型字典
            loadReductionTypeDict();
            // 加载就业减少原因字典
            loadReductionReasonDict();
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
     * 获取就业减少类型名称（支持字符串类型的索引）
     * @param typeIndexStr 类型索引字符串
     * @return 减少类型名称，如果未找到返回空字符串
     */
    public static String getReductionTypeName(String typeIndexStr) {
        try {
            int index = Integer.parseInt(typeIndexStr);
            return getReductionTypeName(index);
        } catch (NumberFormatException e) {
            return "";
        }
    }
}
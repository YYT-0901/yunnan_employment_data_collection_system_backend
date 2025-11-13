package com.yunnancommon.service;

import java.util.List;
import java.util.Map;

/**
 * Druid 查询服务接口
 * 
 * 功能：
 * 1. 提供对 Druid 数据仓库的查询能力
 * 2. 支持多维分析（地区、行业、性质）
 * 3. 自动将 code 转换为 name（中文名称）
 */
public interface DruidQueryService {
    
    /**
     * 测试 Druid 连接是否正常
     */
    boolean testConnection();

    /**
     * 执行原始 SQL 查询（用于测试和调试）
     */
    List<Map<String, Object>> executeQuery(String sql);

    /**
     * 按地区统计失业率（一级分类）
     */
    List<Map<String, Object>> getUnemploymentByRegion();

    /**
     * 按行业统计失业率（一级分类）
     */
    List<Map<String, Object>> getUnemploymentByIndustry();

    /**
     * 按性质统计失业率（一级分类）
     */
    List<Map<String, Object>> getUnemploymentByNature();

    /**
     * 多维分析：按地区、行业、性质统计（用于 3D 可视化）
     */
    List<Map<String, Object>> getMultiDimensionalAnalysis();
    
    // ========== Phase 5 新增方法 ==========
    
    /**
     * 取样分析（支持现有接口）
     * 返回各地区的企业数量分布
     * 
     * @param periodIds 调查期ID列表
     * @param regions 地区代码列表（一级分类）
     * @param industries 行业代码列表（一级分类）
     * @param natures 性质代码列表（一级分类）
     * @return 地区统计数据，包含：regionCode, regionName, enterpriseCount, percentage
     */
    List<Map<String, Object>> getSamplingData(
        List<Long> periodIds,
        List<Integer> regions,
        List<Integer> industries,
        List<Integer> natures
    );
    
    /**
     * 对比/趋势分析（支持现有接口）
     * 返回按指定维度分组的统计数据
     * 
     * @param periodIds 调查期ID列表
     * @param regions 地区代码列表
     * @param industries 行业代码列表
     * @param natures 性质代码列表
     * @param groupBy 分组维度（region/industry/nature）
     * @return 统计数据，包含六大指标
     */
    List<Map<String, Object>> getAnalysisData(
        List<Long> periodIds,
        List<Integer> regions,
        List<Integer> industries,
        List<Integer> natures,
        String groupBy
    );
    
    /**
     * 多维分析（高级查询，支持多条件筛选）
     * 用于 3D 可视化，返回所有维度的组合数据
     * 
     * @param periodIds 调查期ID列表（可选）
     * @param regions 地区代码列表（可选）
     * @param industries 行业代码列表（可选）
     * @param natures 性质代码列表（可选）
     * @return 多维数据点，每个数据点包含所有维度信息
     */
    List<Map<String, Object>> getMultiDimensionalData(
        List<Long> periodIds,
        List<Integer> regions,
        List<Integer> industries,
        List<Integer> natures
    );
}

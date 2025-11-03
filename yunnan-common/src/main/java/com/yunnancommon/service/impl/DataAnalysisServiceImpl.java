// 路径: backend/yunnan-common/src/main/java/com/yunnancommon/service/impl/DataAnalysisServiceImpl.java

package com.yunnancommon.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.yunnancommon.entity.dto.AnalysisQueryDto;
import com.yunnancommon.entity.po.PeriodInfo;
import com.yunnancommon.entity.vo.AnalysisResultVO;
import com.yunnancommon.entity.vo.SamplingResultVO;
import com.yunnancommon.mapper.EnterpriseReportInfoMapper;
import com.yunnancommon.mapper.PeriodInfoMapper;
import com.yunnancommon.service.DataAnalysisService;
import com.yunnancommon.utils.RegionUtils;

@Service("dataAnalysisService")
public class DataAnalysisServiceImpl implements DataAnalysisService {

    private static final Logger logger = LoggerFactory.getLogger(DataAnalysisServiceImpl.class);

    @Resource
    private EnterpriseReportInfoMapper enterpriseReportInfoMapper;

    @Resource
    private PeriodInfoMapper periodInfoMapper;

    /**
     * 取样分析实现
     *
     * 【实现步骤】
     * 1. 参数预处理：设置默认状态为已审核（3,4）
     * 2. 展开地区codes：如果用户选择了某个市，展开其下所有区县和街道
     * 3. 查询数据库：调用Mapper获取各地区企业数量
     * 4. 计算占比：企业数量 / 总企业数 × 100%
     * 5. 转换名称：将地区code转换为地区name
     * 6. 排序返回：按企业数量降序排列
     */
    @Override
    public List<SamplingResultVO> getSamplingAnalysis(AnalysisQueryDto query) {
        logger.info("开始执行取样分析，参数：{}", query);

        // 1. 参数预处理
        if (query.getStatuses() == null || query.getStatuses().isEmpty()) {
            // 默认只统计已审核通过的数据
            query.setStatuses(Arrays.asList(3, 4));
        }

        // 2. 展开地区codes（如果用户选择了父级地区，需要包含所有子级）
        if (query.getRegions() != null && !query.getRegions().isEmpty()) {
            List<Integer> expandedRegions = RegionUtils.expandChildCodesInBatch(query.getRegions());
            query.setRegions(expandedRegions);
            logger.debug("地区codes展开后：{}", expandedRegions);
        }

        // 3. 查询数据库
        List<Map<String, Object>> rawData = enterpriseReportInfoMapper.selectSamplingData(query);

        if (rawData == null || rawData.isEmpty()) {
            logger.warn("未查询到任何数据");
            return new ArrayList<>();
        }

        // 4. 计算总企业数（用于计算占比）
        int totalEnterpriseCount = rawData.stream()
                .mapToInt(map -> ((Number) map.get("enterprise_count")).intValue())
                .sum();

        logger.debug("总企业数：{}", totalEnterpriseCount);

        // 5. 转换为VO并计算占比
        List<SamplingResultVO> result = rawData.stream()
                .map(map -> {
                    Integer regionCode = (Integer) map.get("region_code");
                    Integer enterpriseCount = ((Number) map.get("enterprise_count")).intValue();

                    // 计算占比
                    Double percentage = totalEnterpriseCount > 0
                            ? (enterpriseCount * 100.0 / totalEnterpriseCount)
                            : 0.0;

                    // 转换地区名称
                    String regionName = RegionUtils.getNameByCode(regionCode);

                    SamplingResultVO vo = new SamplingResultVO();
                    vo.setRegionCode(regionCode);
                    vo.setRegionName(regionName);
                    vo.setEnterpriseCount(enterpriseCount);
                    vo.setPercentage(Math.round(percentage * 100.0) / 100.0); // 保留2位小数

                    return vo;
                })
                .sorted(Comparator.comparing(SamplingResultVO::getEnterpriseCount).reversed()) // 降序
                .collect(Collectors.toList());

        logger.info("取样分析完成，返回{}条记录", result.size());
        return result;
    }

    /**
     * 对比分析实现
     *
     * 【实现步骤】
     * 1. 参数验证：必须有2个periodId和groupBy
     * 2. 参数预处理：设置默认状态、展开地区codes
     * 3. 分别查询两个调查期的数据
     * 4. 计算六大指标
     * 5. 转换维度名称
     * 6. 返回结果（包含两期数据）
     */
    @Override
    public List<AnalysisResultVO> getComparisonAnalysis(AnalysisQueryDto query) {
        logger.info("开始执行对比分析，参数：{}", query);

        // 1. 参数验证
        if (query.getPeriodIds() == null || query.getPeriodIds().size() != 2) {
            throw new IllegalArgumentException("对比分析需要选择2个调查期");
        }

        if (query.getGroupBy() == null || query.getGroupBy().isEmpty()) {
            throw new IllegalArgumentException("对比分析需要指定分组维度（region/nature/industry）");
        }

        // 2. 参数预处理
        preprocessQuery(query);

        // 3. 查询数据库（两个period的数据会一起返回）
        List<Map<String, Object>> rawData = enterpriseReportInfoMapper.selectAnalysisData(query);

        if (rawData == null || rawData.isEmpty()) {
            logger.warn("未查询到任何数据");
            return new ArrayList<>();
        }

        // 4. 转换为VO并计算指标
        List<AnalysisResultVO> result = convertToAnalysisResultVO(rawData, query.getGroupBy());

        logger.info("对比分析完成，返回{}条记录", result.size());
        return result;
    }

    /**
     * 趋势分析实现
     *
     * 【实现步骤】
     * 1. 参数预处理
     * 2. 查询数据库（按period_id分组）
     * 3. 获取period信息（用于显示时间）
     * 4. 计算六大指标
     * 5. 按时间排序返回
     */
    @Override
    public List<AnalysisResultVO> getTrendAnalysis(AnalysisQueryDto query) {
        logger.info("开始执行趋势分析，参数：{}", query);

        // 1. 参数预处理
        preprocessQuery(query);

        // 2. 查询数据库
        List<Map<String, Object>> rawData = enterpriseReportInfoMapper.selectTrendData(query);

        if (rawData == null || rawData.isEmpty()) {
            logger.warn("未查询到任何数据");
            return new ArrayList<>();
        }

        // 3. 获取period信息（用于显示时间）
        Map<Long, PeriodInfo> periodMap = getPeriodInfoMap(query.getPeriodIds());

        // 4. 转换为VO并补充period信息
        List<AnalysisResultVO> result = rawData.stream()
                .map(map -> {
                    AnalysisResultVO vo = convertMapToVO(map, query.getGroupBy());

                    // 补充period信息
                    Long periodId = ((Number) map.get("period_id")).longValue();
                    vo.setPeriodId(periodId);

                    PeriodInfo periodInfo = periodMap.get(periodId);
                    if (periodInfo != null) {
                        vo.setPeriodName(periodInfo.getInvestigateTime()); // 假设这个字段存储"2024-01"格式
                    }

                    return vo;
                })
                .sorted(Comparator.comparing(AnalysisResultVO::getPeriodId)) // 按时间排序
                .collect(Collectors.toList());

        logger.info("趋势分析完成，返回{}条记录", result.size());
        return result;
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 参数预处理
     *
     * 【处理内容】
     * 1. 设置默认状态
     * 2. 展开地区codes
     * 3. 展开性质codes（如果需要）
     * 4. 展开行业codes（如果需要）
     */
    private void preprocessQuery(AnalysisQueryDto query) {
        // 设置默认状态
        if (query.getStatuses() == null || query.getStatuses().isEmpty()) {
            // 只统计审核通过(3)和已归档(4)的数据
            query.setStatuses(Arrays.asList(3, 4));
        }

        // 展开地区codes
        if (query.getRegions() != null && !query.getRegions().isEmpty()) {
            List<Integer> expandedRegions = RegionUtils.expandChildCodesInBatch(query.getRegions());
            query.setRegions(expandedRegions);
            logger.debug("地区codes展开后：{}", expandedRegions);
        }

        // TODO: 如果性质和行业也需要展开，可以在这里添加类似逻辑
        // 目前假设性质和行业的筛选不需要展开
    }

    /**
     * 批量转换Map为VO
     */
    private List<AnalysisResultVO> convertToAnalysisResultVO(List<Map<String, Object>> rawData, String groupBy) {
        return rawData.stream()
                .map(map -> convertMapToVO(map, groupBy))
                .collect(Collectors.toList());
    }

    /**
     * 将数据库查询结果Map转换为AnalysisResultVO
     *
     * 【核心计算逻辑】
     * 1. 岗位变化总数 = 建档期 - 调查期
     * 2. 岗位减少总数 = 需要从明细数据计算（这里暂用变化总数代替，实际应单独计算）
     * 3. 岗位变化占比 = (建档期 - 调查期) / 建档期 × 100%
     */
    private AnalysisResultVO convertMapToVO(Map<String, Object> map, String groupBy) {
        AnalysisResultVO vo = new AnalysisResultVO();

        // 1. 提取维度code
        Object dimensionCodeObj = map.get("dimension_code");
        String dimensionCode = dimensionCodeObj != null ? dimensionCodeObj.toString() : null;
        vo.setDimensionCode(dimensionCode);

        // 2. 转换维度名称
        String dimensionName = mapDimensionName(groupBy, dimensionCode);
        vo.setDimensionName(dimensionName);

        // 3. 提取基础指标
        Integer enterpriseCount = getIntValue(map, "enterprise_count");
        Integer constructionTotal = getIntValue(map, "construction_total");
        Integer investigationTotal = getIntValue(map, "investigation_total");

        vo.setEnterpriseCount(enterpriseCount);
        vo.setConstructionTotal(constructionTotal);
        vo.setInvestigationTotal(investigationTotal);

        // 4. 计算派生指标
        // 岗位变化总数 = 建档期 - 调查期
        Integer changeTotal = constructionTotal - investigationTotal;
        vo.setChangeTotal(changeTotal);

        // 岗位减少总数（简化处理：当变化为正时即为减少）
        // 注意：实际应该从明细数据中计算，这里为了简化，使用Max(0, changeTotal)
        Integer reductionTotal = Math.max(0, changeTotal);
        vo.setReductionTotal(reductionTotal);

        // 岗位变化占比 = (建档期 - 调查期) / 建档期 × 100%
        Double changeRatio = calculateChangeRatio(constructionTotal, investigationTotal);
        vo.setChangeRatio(changeRatio);

        return vo;
    }

    /**
     * 计算岗位变化占比（失业率）
     *
     * 【公式】
     * (constructionTotal - investigationTotal) / constructionTotal × 100%
     *
     * 【特殊处理】
     * - 如果constructionTotal为0，返回0.0
     * - 保留2位小数
     */
    private Double calculateChangeRatio(Integer construction, Integer investigation) {
        if (construction == null || construction == 0) {
            return 0.0;
        }

        if (investigation == null) {
            investigation = 0;
        }

        double ratio = ((construction - investigation) * 100.0) / construction;
        return Math.round(ratio * 100.0) / 100.0; // 保留2位小数
    }

    /**
     * 维度名称映射
     *
     * 【功能】
     * 根据groupBy类型，将code转换为对应的name
     */
    private String mapDimensionName(String groupBy, String code) {
        if (code == null || "ALL".equals(code)) {
            return "全省汇总";
        }

        try {
            Integer codeInt = Integer.parseInt(code);

            switch (groupBy) {
                case "region":
                    return RegionUtils.getNameByCode(codeInt);

                case "nature":
                    // TODO: 实现NatureUtils.getNameByCode()
                    return "性质-" + code; // 临时方案

                case "industry":
                    // TODO: 实现IndustryUtils.getNameByCode()
                    return "行业-" + code; // 临时方案

                default:
                    return code;
            }
        } catch (NumberFormatException e) {
            return code;
        }
    }

    /**
     * 从Map中安全获取Integer值
     */
    private Integer getIntValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) {
            return 0;
        }

        if (value instanceof Number) {
            return ((Number) value).intValue();
        }

        return 0;
    }

    /**
     * 获取Period信息Map
     *
     * 【用途】
     * 用于趋势分析时显示时间名称
     */
    private Map<Long, PeriodInfo> getPeriodInfoMap(List<Long> periodIds) {
        if (periodIds == null || periodIds.isEmpty()) {
            return new HashMap<>();
        }
        Map<Long, PeriodInfo> result = new HashMap<>();

        for (Long periodId : periodIds) {
            Object obj = periodInfoMapper.selectByPeriodId(periodId);
            if (obj != null && obj instanceof PeriodInfo) {
                result.put(periodId, (PeriodInfo) obj);
            }
        }

        return result;
    }
}

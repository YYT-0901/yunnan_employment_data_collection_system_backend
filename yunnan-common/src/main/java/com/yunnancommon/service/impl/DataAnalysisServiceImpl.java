// 路径: backend/yunnan-common/src/main/java/com/yunnancommon/service/impl/DataAnalysisServiceImpl.java

package com.yunnancommon.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.yunnancommon.entity.dto.AnalysisQueryDto;
import com.yunnancommon.entity.po.PeriodInfo;
import com.yunnancommon.entity.vo.AnalysisResultVO;
import com.yunnancommon.entity.vo.SamplingRegionDetailVO;
import com.yunnancommon.entity.vo.SamplingResultVO;
import com.yunnancommon.mapper.EnterpriseReportInfoMapper;
import com.yunnancommon.mapper.PeriodInfoMapper;
import com.yunnancommon.service.DataAnalysisService;
import com.yunnancommon.service.DictService;
import com.yunnancommon.utils.RegionUtils;

@Service("dataAnalysisService")
public class DataAnalysisServiceImpl implements DataAnalysisService {

    private static final Logger logger = LoggerFactory.getLogger(DataAnalysisServiceImpl.class);

    @Resource
    private EnterpriseReportInfoMapper enterpriseReportInfoMapper;

    @Resource
    private PeriodInfoMapper periodInfoMapper;

    @Resource
    private DictService dictService;

    /**
     * 取样分析实现
     *
     * 【实现步骤】
     * 1. 参数预处理：设置默认状态为已归档（4）
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
            // 默认只统计已归档的数据
            query.setStatuses(Arrays.asList(4));
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

        // 调试：输出原始SQL查询结果
        logger.info("SQL查询返回 {} 条原始数据", rawData.size());
        if (!rawData.isEmpty()) {
            Map<String, Object> firstRow = rawData.get(0);
            logger.info("第一条数据示例: {}", firstRow);
            logger.info("第一条数据包含的字段: {}", firstRow.keySet());
            logger.info("construction_total值: {}, investigation_total值: {}", 
                    firstRow.get("construction_total"), firstRow.get("investigation_total"));
        }

        Map<Integer, List<Map<String, Object>>> groupedByCity = groupSamplingDataByTopLevel(rawData);

        // 4. 计算总企业数（用于计算占比）
        int totalEnterpriseCount = groupedByCity.values().stream()
                .mapToInt(list -> list.stream()
                        .mapToInt(row -> safeInt(row.get("enterprise_count")))
                        .sum())
                .sum();

        logger.debug("总企业数：{}", totalEnterpriseCount);

        // 5. 构建包含二级地区的VO
        List<SamplingResultVO> result = groupedByCity.entrySet().stream()
                .map(entry -> buildSamplingResult(entry.getKey(), entry.getValue(), totalEnterpriseCount))
                .sorted(Comparator.comparing(SamplingResultVO::getEnterpriseCount).reversed())
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

        // 聚合地区到一级分类
        if ("region".equals(query.getGroupBy())) {
            rawData = aggregateTrendDataByRegion(rawData);
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
            // 只统计已归档(4)的数据
            query.setStatuses(Arrays.asList(4));
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
     * 1. 岗位变化总数 = 调查期 - 建档期（>0 表示增加）
     * 2. 岗位减少总数 = Max(0, 建档期 - 调查期)
     * 3. 岗位变化占比 = (调查期 - 建档期) / 建档期 × 100%
     */
    private AnalysisResultVO convertMapToVO(Map<String, Object> map, String groupBy) {
        AnalysisResultVO vo = new AnalysisResultVO();

        // periodId
        Object periodIdObj = map.get("period_id");
        if (periodIdObj == null) {
            periodIdObj = map.get("PERIOD_ID");
        }
        if (periodIdObj == null) {
            periodIdObj = map.get("periodId");
        }
        if (periodIdObj instanceof Number) {
            vo.setPeriodId(((Number) periodIdObj).longValue());
        }

        // 1. 提取维度code
        Object dimensionCodeObj = map.get("dimension_code");
        if (dimensionCodeObj == null) {
            dimensionCodeObj = map.get("DIMENSION_CODE");
        }
        if (dimensionCodeObj == null) {
            dimensionCodeObj = map.get("dimensionCode");
        }
        if ("region".equals(groupBy) && dimensionCodeObj != null) {
            Integer normalized = parseRegionCode(dimensionCodeObj);
            if (normalized != null) {
                dimensionCodeObj = normalized.toString();
            }
        }
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
        Integer changeTotal = (investigationTotal != null ? investigationTotal : 0)
                - (constructionTotal != null ? constructionTotal : 0);
        vo.setChangeTotal(changeTotal);

        // 岗位减少总数（简化处理：当变化为负时视为减少）
        // 注意：实际应该从明细数据中计算，这里为了简化，使用 Max(0, 建档期-调查期)
        Integer reductionTotal = changeTotal < 0 ? Math.abs(changeTotal) : 0;
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
     * (investigationTotal - constructionTotal) / constructionTotal × 100%
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

        double ratio = ((investigation - construction) * 100.0) / construction;
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
                    if (dictService != null) {
                        String name = dictService.getRegionName(codeInt);
                        if (name != null && !name.isEmpty()) {
                            return name;
                        }
                    }
                    return RegionUtils.getNameByCode(codeInt);

                case "nature":
                    if (dictService != null) {
                        return dictService.getNatureName(codeInt);
                    }
                    return "性质-" + code;

                case "industry":
                    if (dictService != null) {
                        return dictService.getIndustryName(codeInt);
                    }
                    return "行业-" + code;

                default:
                    return code;
            }
        } catch (NumberFormatException e) {
            return code;
        }
    }

    /**
     * 聚合趋势分析原始数据，使地区维度汇总到一级分类
     */
    private List<Map<String, Object>> aggregateTrendDataByRegion(List<Map<String, Object>> rawData) {
        if (rawData == null || rawData.isEmpty()) {
            return rawData;
        }

        Map<String, Map<String, Object>> aggregated = new LinkedHashMap<>();

        for (Map<String, Object> row : rawData) {
            Integer originalCode = parseRegionCode(row.get("dimension_code"));
            if (originalCode == null) {
                continue;
            }

            Integer topLevelCode = RegionUtils.getTopLevelParentCode(originalCode);
            Long periodId = safeParseLong(row.get("period_id"));
            String key = (periodId != null ? periodId.toString() : "0") + "_" + topLevelCode;

            Map<String, Object> aggregatedRow = aggregated.computeIfAbsent(key, k -> {
                Map<String, Object> base = new HashMap<>();
                base.put("period_id", periodId);
                base.put("dimension_code", topLevelCode);
                base.put("enterprise_count", 0);
                base.put("construction_total", 0);
                base.put("investigation_total", 0);
                return base;
            });

            aggregatedRow.put("enterprise_count",
                    safeInt(aggregatedRow.get("enterprise_count")) + safeInt(row.get("enterprise_count")));
            aggregatedRow.put("construction_total",
                    safeInt(aggregatedRow.get("construction_total")) + safeInt(row.get("construction_total")));
            aggregatedRow.put("investigation_total",
                    safeInt(aggregatedRow.get("investigation_total")) + safeInt(row.get("investigation_total")));
        }

        return new ArrayList<>(aggregated.values());
    }

    /**
     * 将原始取样数据按顶级地区分组
     */
    private Map<Integer, List<Map<String, Object>>> groupSamplingDataByTopLevel(List<Map<String, Object>> rawData) {
        Map<Integer, List<Map<String, Object>>> grouped = new LinkedHashMap<>();

        logger.info("开始按市级分组，共 {} 条数据", rawData.size());

        for (Map<String, Object> row : rawData) {
            // 使用完整的 region_code 字段来获取完整的地区代码
            Object regionCodeObj = row.get("region_code");
            Integer originalCode = parseRegionCode(regionCodeObj);
            
            logger.debug("处理数据行 - region_code原始值: {}, 解析后: {}", regionCodeObj, originalCode);
            
            if (originalCode == null) {
                logger.warn("无法解析 region_code: {}", regionCodeObj);
                continue;
            }

            Integer topLevelCode = RegionUtils.getTopLevelParentCode(originalCode);
            logger.debug("originalCode: {} -> topLevelCode: {}", originalCode, topLevelCode);
            
            grouped.computeIfAbsent(topLevelCode, key -> new ArrayList<>()).add(row);
        }

        logger.info("分组完成，共 {} 个市级分组", grouped.size());
        grouped.forEach((code, list) -> {
            logger.info("市级 code={} ({}), 包含 {} 条数据", 
                    code, RegionUtils.getNameByCode(code), list.size());
        });

        return grouped;
    }

    private SamplingResultVO buildSamplingResult(Integer topLevelCode, List<Map<String, Object>> rows,
            int totalEnterpriseCount) {
        logger.info("构建市级结果 - code: {} ({}), 包含 {} 条原始数据", 
                topLevelCode, RegionUtils.getNameByCode(topLevelCode), rows.size());
        
        int cityTotal = rows.stream()
                .mapToInt(row -> safeInt(row.get("enterprise_count")))
                .sum();
        
        // 汇总岗位数据
        int constructionTotal = rows.stream()
                .mapToInt(row -> safeInt(row.get("construction_total")))
                .sum();
        int investigationTotal = rows.stream()
                .mapToInt(row -> safeInt(row.get("investigation_total")))
                .sum();
        
        logger.info("市级 {} 岗位汇总 - 建档期: {}, 调查期: {}", 
                RegionUtils.getNameByCode(topLevelCode), constructionTotal, investigationTotal);

        double percentage = totalEnterpriseCount > 0
                ? (cityTotal * 100.0 / totalEnterpriseCount)
                : 0.0;

        SamplingResultVO vo = new SamplingResultVO();
        vo.setRegionCode(topLevelCode);
        vo.setRegionName(resolveRegionName(topLevelCode));
        vo.setEnterpriseCount(cityTotal);
        vo.setPercentage(Math.round(percentage * 100.0) / 100.0);
        
        // 设置岗位数据
        vo.setConstructionTotal(constructionTotal);
        vo.setInvestigationTotal(investigationTotal);
        
        // 计算派生指标
        int changeTotal = investigationTotal - constructionTotal;
        vo.setChangeTotal(changeTotal);
        
        // 计算变化占比（失业率）
        Double changeRatio = calculateChangeRatio(constructionTotal, investigationTotal);
        vo.setChangeRatio(changeRatio);
        
        List<SamplingRegionDetailVO> children = buildChildDetails(rows, cityTotal);
        vo.setChildren(children);
        
        logger.info("市级 {} 构建完成 - 企业总数: {}, 建档期岗位: {}, 调查期岗位: {}, children数量: {}", 
                vo.getRegionName(), cityTotal, constructionTotal, investigationTotal, children.size());

        return vo;
    }

    private List<SamplingRegionDetailVO> buildChildDetails(List<Map<String, Object>> rows, int cityTotal) {
        // 存储每个二级地区的汇总数据：企业数、建档期岗位、调查期岗位
        Map<Integer, Map<String, Integer>> secondLevelAggregated = new LinkedHashMap<>();

        for (Map<String, Object> row : rows) {
            Integer originalCode = parseRegionCode(row.get("region_code"));
            
            // 调试日志：输出原始数据
            logger.debug("原始数据 - region_code: {}, originalCode: {}, enterprise_count: {}", 
                    row.get("region_code"), originalCode, row.get("enterprise_count"));
            
            if (originalCode == null) {
                continue;
            }
            Integer secondLevelCode = RegionUtils.getSecondLevelParentCode(originalCode);
            
            // 调试日志：输出二级分类code
            logger.debug("originalCode: {} -> secondLevelCode: {}", originalCode, secondLevelCode);
            
            int enterpriseCount = safeInt(row.get("enterprise_count"));
            int constructionTotal = safeInt(row.get("construction_total"));
            int investigationTotal = safeInt(row.get("investigation_total"));
            
            Integer targetCode = secondLevelCode != null ? secondLevelCode : originalCode;
            
            secondLevelAggregated.putIfAbsent(targetCode, new HashMap<>());
            Map<String, Integer> aggregated = secondLevelAggregated.get(targetCode);
            
            aggregated.put("enterpriseCount", aggregated.getOrDefault("enterpriseCount", 0) + enterpriseCount);
            aggregated.put("constructionTotal", aggregated.getOrDefault("constructionTotal", 0) + constructionTotal);
            aggregated.put("investigationTotal", aggregated.getOrDefault("investigationTotal", 0) + investigationTotal);
        }

        List<SamplingRegionDetailVO> result = secondLevelAggregated.entrySet().stream()
                .map(entry -> {
                    Map<String, Integer> data = entry.getValue();
                    int enterpriseCount = data.get("enterpriseCount");
                    int constructionTotal = data.get("constructionTotal");
                    int investigationTotal = data.get("investigationTotal");
                    
                    double percentage = cityTotal > 0
                            ? (enterpriseCount * 100.0 / cityTotal)
                            : 0.0;

                    SamplingRegionDetailVO detailVO = new SamplingRegionDetailVO();
                    detailVO.setRegionCode(entry.getKey());
                    detailVO.setRegionName(resolveRegionName(entry.getKey()));
                    detailVO.setEnterpriseCount(enterpriseCount);
                    detailVO.setPercentage(Math.round(percentage * 100.0) / 100.0);
                    
                    // 设置岗位数据
                    detailVO.setConstructionTotal(constructionTotal);
                    detailVO.setInvestigationTotal(investigationTotal);
                    
                    // 计算派生指标
                    int changeTotal = investigationTotal - constructionTotal;
                    detailVO.setChangeTotal(changeTotal);
                    
                    Double changeRatio = calculateChangeRatio(constructionTotal, investigationTotal);
                    detailVO.setChangeRatio(changeRatio);
                    
                    return detailVO;
                })
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(SamplingRegionDetailVO::getEnterpriseCount).reversed())
                .collect(Collectors.toList());
        
        // 调试日志：输出最终的children数量
        logger.debug("构建的children数量: {}, 详情: {}", result.size(), result);
        
        return result;
    }

    private String resolveRegionName(Integer regionCode) {
        if (regionCode == null) {
            return "未知";
        }

        String regionName = null;
        if (dictService != null) {
            regionName = dictService.getRegionName(regionCode);
        }

        if (regionName == null || "未知".equals(regionName)) {
            regionName = RegionUtils.getNameByCode(regionCode);
        }

        return regionName;
    }

    private Integer safeParseInteger(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof Number) {
            return ((Number) value).intValue();
        }

        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * 解析数据库中的地区编码，兼容带有分隔符的写法
     */
    private Integer parseRegionCode(Object value) {
        Integer direct = safeParseInteger(value);
        if (direct != null) {
            return direct;
        }

        if (value == null) {
            return null;
        }

        String text = value.toString();
        if (text == null) {
            return null;
        }

        text = text.trim();
        if (text.isEmpty()) {
            return null;
        }

        String[] rawSegments = text.split("[^0-9]+");
        List<String> segments = Arrays.stream(rawSegments)
                .filter(segment -> segment != null && !segment.isEmpty())
                .collect(Collectors.toList());

        if (segments.isEmpty()) {
            return null;
        }

        // 如果某一段本身就是合法的地区编码（例如直接存储了县级code），优先返回
        for (int i = segments.size() - 1; i >= 0; i--) {
            try {
                Integer candidate = Integer.parseInt(segments.get(i));
                if (regionExists(candidate)) {
                    return candidate;
                }
            } catch (NumberFormatException ignore) {
            }
        }

        // 尝试拼接所有数字，保持原始位数
        String digitsOnly = String.join("", segments);
        if (!digitsOnly.isEmpty()) {
            try {
                Integer candidate = Integer.parseInt(digitsOnly);
                if (regionExists(candidate)) {
                    return candidate;
                }
            } catch (NumberFormatException ignore) {
            }
        }

        // 最后按层级折算（每级 *100），确保不会返回null
        Integer composed = null;
        for (String segment : segments) {
            try {
                int part = Integer.parseInt(segment);
                composed = composed == null ? part : composed * 100 + part;
            } catch (NumberFormatException ignore) {
                return null;
            }
        }
        return composed;
    }

    private boolean regionExists(Integer code) {
        if (code == null) {
            return false;
        }

        if (dictService != null) {
            String name = dictService.getRegionName(code);
            if (name != null && !"未知".equals(name)) {
                return true;
            }
        }

        String fallback = RegionUtils.getNameByCode(code);
        return fallback != null && !String.valueOf(code).equals(fallback);
    }

    private Long safeParseLong(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof Number) {
            return ((Number) value).longValue();
        }

        try {
            return Long.parseLong(value.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private int safeInt(Object value) {
        Integer parsed = safeParseInteger(value);
        return parsed != null ? parsed : 0;
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

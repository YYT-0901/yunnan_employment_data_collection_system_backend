package com.yunnancommon.service.impl;

import com.yunnancommon.service.DictService;
import com.yunnancommon.service.DruidQueryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Druid 查询服务实现类
 * 
 * 核心功能：
 * 1. 通过 JDBC 连接 Druid
 * 2. 执行 SQL 查询
 * 3. 整合 DictService 自动转换 code 为 name
 * 4. 提供多维分析数据

 */
@Service("druidQueryService")
public class DruidQueryServiceImpl implements DruidQueryService {
    
    private static final Logger logger = LoggerFactory.getLogger(DruidQueryServiceImpl.class);
    
    /**
     * 从 application.yml 读取 Druid JDBC URL
     * 格式：jdbc:avatica:remote:url=http://localhost:8888/druid/v2/sql/avatica/
     */
    @Value("${spring.druid.url}")
    private String druidUrl;
    
    /**
     * 从 application.yml 读取 Druid 驱动类名
     */
    @Value("${spring.druid.driver-class-name}")
    private String driverClassName;
    
    /**
     * 注入字典服务（用于 code → name 转换）
     */
    @Resource
    private DictService dictService;
    
    /**
     * 应用启动时加载 Druid 驱动
     */
    @PostConstruct
    public void init() {
        try {
            logger.info("========== 开始初始化 Druid JDBC 驱动 ==========");
            Class.forName(driverClassName);
            logger.info("✓ Druid JDBC 驱动加载成功: {}", driverClassName);
            logger.info("✓ Druid URL: {}", druidUrl);
            logger.info("========== Druid JDBC 驱动初始化完成 ==========");
        } catch (ClassNotFoundException e) {
            logger.error("✗ Druid JDBC 驱动加载失败", e);
            throw new RuntimeException("无法加载 Druid JDBC 驱动", e);
        }
    }
    
    /**
     * 获取 Druid 数据库连接
     * 
     * @return JDBC 连接对象
     * @throws SQLException 连接失败时抛出
     */
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(druidUrl);
    }
    
    /**
     * 测试 Druid 连接
     * 执行简单的查询来验证连接是否正常
     */
    @Override
    public boolean testConnection() {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            logger.info("开始测试 Druid 连接...");
            
            // 获取连接
            conn = getConnection();
            logger.info("✓ 获取 Druid 连接成功");
            
            // 执行测试查询（查询 Druid 系统表）
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT 1 AS test");
            
            if (rs.next()) {
                int result = rs.getInt("test");
                logger.info("✓ Druid 连接测试成功，查询结果: {}", result);
                return true;
            }
            
            return false;
            
        } catch (Exception e) {
            logger.error("✗ Druid 连接测试失败", e);
            return false;
            
        } finally {
            // 关闭资源（重要！防止连接泄漏）
            closeResources(rs, stmt, conn);
        }
    }
    
    /**
     * 执行原始 SQL 查询
     * 
     * @param sql SQL 查询语句
     * @return 查询结果
     */
    @Override
    public List<Map<String, Object>> executeQuery(String sql) {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        List<Map<String, Object>> results = new ArrayList<>();
        
        try {
            logger.info("执行 Druid 查询: {}", sql);
            
            // 获取连接并执行查询
            conn = getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            
            // 获取列信息
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            
            // 遍历结果集
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    Object value = rs.getObject(i);
                    row.put(columnName, value);
                }
                results.add(row);
            }
            
            logger.info("✓ 查询成功，返回 {} 条记录", results.size());
            return results;
            
        } catch (Exception e) {
            logger.error("✗ 查询失败: {}", sql, e);
            throw new RuntimeException("Druid 查询失败", e);
            
        } finally {
            closeResources(rs, stmt, conn);
        }
    }
    
    /**
     * 按地区统计失业率（一级分类）
     * 
     * 注意：这里假设 Druid 中已经有名为 "enterprise_data" 的数据源
     * 如果还没有数据，这个方法暂时会返回空列表
     */
    @Override
    public List<Map<String, Object>> getUnemploymentByRegion() {
        String sql = "SELECT " +
                     "  region_code, " +
                     "  COUNT(*) as enterprise_count, " +
                     "  AVG(CAST(unemployment_rate AS DOUBLE)) as avg_unemployment_rate " +
                     "FROM enterprise_analysis " +
                     "WHERE region_code IS NOT NULL " +
                     "GROUP BY region_code " +
                     "ORDER BY region_code";
        
        try {
            List<Map<String, Object>> rawResults = executeQuery(sql);
            List<Map<String, Object>> results = new ArrayList<>();
            
            // 转换结果：添加地区名称
            for (Map<String, Object> row : rawResults) {
                Map<String, Object> result = new HashMap<>();
                
                // 获取地区代码
                Integer regionCode = getIntegerValue(row.get("region_code"));
                
                // 使用 DictService 转换为地区名称
                String regionName = dictService.getRegionName(regionCode);
                
                // 构建返回结果
                result.put("regionCode", regionCode);
                result.put("regionName", regionName);
                result.put("enterpriseCount", row.get("enterprise_count"));
                result.put("unemploymentRate", formatRate(row.get("avg_unemployment_rate")));
                
                results.add(result);
            }
            
            logger.info("✓ 按地区统计完成，共 {} 个地区", results.size());
            return results;
            
        } catch (Exception e) {
            logger.warn("⚠ 查询失败（可能是 Druid 中还没有数据）: {}", e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * 按行业统计失业率（一级分类）
     */
    @Override
    public List<Map<String, Object>> getUnemploymentByIndustry() {
        String sql = "SELECT " +
                     "  industry_code, " +
                     "  COUNT(*) as enterprise_count, " +
                     "  AVG(CAST(unemployment_rate AS DOUBLE)) as avg_unemployment_rate " +
                     "FROM enterprise_analysis " +
                     "WHERE industry_code IS NOT NULL " +
                     "GROUP BY industry_code " +
                     "ORDER BY industry_code";
        
        try {
            List<Map<String, Object>> rawResults = executeQuery(sql);
            List<Map<String, Object>> results = new ArrayList<>();
            
            for (Map<String, Object> row : rawResults) {
                Map<String, Object> result = new HashMap<>();
                
                Integer industryCode = getIntegerValue(row.get("industry_code"));
                String industryName = dictService.getIndustryName(industryCode);
                
                result.put("industryCode", industryCode);
                result.put("industryName", industryName);
                result.put("enterpriseCount", row.get("enterprise_count"));
                result.put("unemploymentRate", formatRate(row.get("avg_unemployment_rate")));
                
                results.add(result);
            }
            
            logger.info("✓ 按行业统计完成，共 {} 个行业", results.size());
            return results;
            
        } catch (Exception e) {
            logger.warn("⚠ 查询失败（可能是 Druid 中还没有数据）: {}", e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * 按性质统计失业率（一级分类）
     */
    @Override
    public List<Map<String, Object>> getUnemploymentByNature() {
        String sql = "SELECT " +
                     "  nature_code, " +
                     "  COUNT(*) as enterprise_count, " +
                     "  AVG(CAST(unemployment_rate AS DOUBLE)) as avg_unemployment_rate " +
                     "FROM enterprise_analysis " +
                     "WHERE nature_code IS NOT NULL " +
                     "GROUP BY nature_code " +
                     "ORDER BY nature_code";
        
        try {
            List<Map<String, Object>> rawResults = executeQuery(sql);
            List<Map<String, Object>> results = new ArrayList<>();
            
            for (Map<String, Object> row : rawResults) {
                Map<String, Object> result = new HashMap<>();
                
                Integer natureCode = getIntegerValue(row.get("nature_code"));
                String natureName = dictService.getNatureName(natureCode);
                
                result.put("natureCode", natureCode);
                result.put("natureName", natureName);
                result.put("enterpriseCount", row.get("enterprise_count"));
                result.put("unemploymentRate", formatRate(row.get("avg_unemployment_rate")));
                
                results.add(result);
            }
            
            logger.info("✓ 按性质统计完成，共 {} 种性质", results.size());
            return results;
            
        } catch (Exception e) {
            logger.warn("⚠ 查询失败（可能是 Druid 中还没有数据）: {}", e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * 多维分析：同时按地区、行业、性质统计
     * 这是 3D 可视化的核心数据源
     */
    @Override
    public List<Map<String, Object>> getMultiDimensionalAnalysis() {
        String sql = "SELECT " +
                     "  region_code, " +
                     "  industry_code, " +
                     "  nature_code, " +
                     "  COUNT(*) as enterprise_count, " +
                     "  AVG(CAST(unemployment_rate AS DOUBLE)) as avg_unemployment_rate " +
                     "FROM enterprise_analysis " +
                     "WHERE region_code IS NOT NULL " +
                     "  AND industry_code IS NOT NULL " +
                     "  AND nature_code IS NOT NULL " +
                     "GROUP BY region_code, industry_code, nature_code " +
                     "ORDER BY region_code, industry_code, nature_code";
        
        try {
            List<Map<String, Object>> rawResults = executeQuery(sql);
            List<Map<String, Object>> results = new ArrayList<>();
            
            for (Map<String, Object> row : rawResults) {
                Map<String, Object> result = new HashMap<>();
                
                if (logger.isDebugEnabled()) {
                    logger.debug("多维分析原始行数据: {}", row);
                }
                
                // 获取三个维度的代码
                Integer regionCode = getIntegerValue(row.get("region_code"));
                Integer industryCode = getIntegerValue(row.get("industry_code"));
                Integer natureCode = getIntegerValue(row.get("nature_code"));
                
                // 转换为名称
                String regionName = dictService.getRegionName(regionCode);
                String industryName = dictService.getIndustryName(industryCode);
                String natureName = dictService.getNatureName(natureCode);
                
                // 构建完整结果（包含 code 和 name）
                result.put("regionCode", regionCode);
                result.put("regionName", regionName);
                result.put("industryCode", industryCode);
                result.put("industryName", industryName);
                result.put("natureCode", natureCode);
                result.put("natureName", natureName);
                result.put("enterpriseCount", row.get("enterprise_count"));
                result.put("unemploymentRate", formatRate(row.get("avg_unemployment_rate")));
                
                results.add(result);
            }
            
            logger.info("✓ 多维分析完成，共 {} 个数据点", results.size());
            return results;
            
        } catch (Exception e) {
            logger.warn("⚠ 查询失败（可能是 Druid 中还没有数据）: {}", e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * 安全地将 Object 转换为 Integer
     */
    private Integer getIntegerValue(Object value) {
        if (value == null) return null;
        if (value instanceof Integer) return (Integer) value;
        if (value instanceof Number) return ((Number) value).intValue();
        if (value instanceof String) return Integer.parseInt((String) value);
        return null;
    }
    
    /**
     * 格式化失业率（保留2位小数）
     */
    private Double formatRate(Object value) {
        if (value == null) return null;
        
        double rate;
        if (value instanceof Number) {
            rate = ((Number) value).doubleValue();
        } else {
            rate = Double.parseDouble(value.toString());
        }
        
        // 保留2位小数
        BigDecimal bd = new BigDecimal(rate);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
    
    
    /**
     * 取样分析数据查询
     * 
     * SQL 逻辑：
     * 1. 按 region_code 分组统计企业数量
     * 2. 支持多个筛选条件（periodId, region, industry, nature）
     * 3. 自动转换 region_code → region_name
     */
    @Override
    public List<Map<String, Object>> getSamplingData(
            List<Long> periodIds,
            List<Integer> regions,
            List<Integer> industries,
            List<Integer> natures) {
        
        // 构建 SQL
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");
        sql.append("  region_code, ");
        sql.append("  COUNT(*) as enterprise_count ");
        sql.append("FROM enterprise_analysis ");
        sql.append("WHERE 1=1 ");
        
        // 添加筛选条件
        if (periodIds != null && !periodIds.isEmpty()) {
            String ids = periodIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
            sql.append("AND period_id IN (").append(ids).append(") ");
        }
        
        if (regions != null && !regions.isEmpty()) {
            String codes = regions.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
            sql.append("AND region_code IN (").append(codes).append(") ");
        }
        
        if (industries != null && !industries.isEmpty()) {
            String codes = industries.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
            sql.append("AND industry_code IN (").append(codes).append(") ");
        }
        
        if (natures != null && !natures.isEmpty()) {
            String codes = natures.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
            sql.append("AND nature_code IN (").append(codes).append(") ");
        }
        
        sql.append("GROUP BY region_code ");
        sql.append("ORDER BY enterprise_count DESC");
        
        try {
            // 执行查询
            List<Map<String, Object>> rawResults = executeQuery(sql.toString());
            List<Map<String, Object>> results = new ArrayList<>();
            
            // 计算总数（用于计算百分比）
            int total = rawResults.stream()
                .mapToInt(row -> ((Number) row.get("enterprise_count")).intValue())
                .sum();
            
            // 转换结果：添加地区名称和百分比
            for (Map<String, Object> row : rawResults) {
                Map<String, Object> result = new HashMap<>();
                
                Integer regionCode = getIntegerValue(row.get("region_code"));
                Integer enterpriseCount = getIntegerValue(row.get("enterprise_count"));
                
                // 使用 DictService 转换地区名称
                String regionName = dictService.getRegionName(regionCode);
                
                // 计算百分比
                Double percentage = total > 0 ? (enterpriseCount * 100.0 / total) : 0.0;
                
                result.put("regionCode", regionCode);
                result.put("regionName", regionName);
                result.put("enterpriseCount", enterpriseCount);
                result.put("percentage", formatRate(percentage));
                
                results.add(result);
            }
            
            logger.info("✓ 取样分析数据查询完成，返回 {} 条记录", results.size());
            return results;
            
        } catch (Exception e) {
            logger.warn("⚠ 取样分析查询失败: {}", e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * 对比/趋势分析数据查询
     * 
     * SQL 逻辑：
     * 1. 按 groupBy 维度分组统计
     * 2. 计算六大指标
     * 3. 支持多个 periodId（趋势分析）
     * 4. 自动转换 code → name
     */
    @Override
    public List<Map<String, Object>> getAnalysisData(
            List<Long> periodIds,
            List<Integer> regions,
            List<Integer> industries,
            List<Integer> natures,
            String groupBy) {
        
        // 验证 groupBy 参数
        if (groupBy == null || !(groupBy.equals("region") || groupBy.equals("industry") || groupBy.equals("nature"))) {
            throw new IllegalArgumentException("groupBy 必须是 region, industry 或 nature");
        }
        
        String groupByColumn = groupBy + "_code";
        
        // 构建 SQL
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");
        sql.append("  ").append(groupByColumn).append(", ");
        sql.append("  period_id, ");
        sql.append("  COUNT(*) as enterprise_count, ");
        sql.append("  SUM(CAST(employed_count AS BIGINT)) as total_employed, ");
        sql.append("  SUM(CAST(unemployed_count AS BIGINT)) as total_unemployed, ");
        sql.append("  AVG(CAST(unemployment_rate AS DOUBLE)) as avg_unemployment_rate ");
        sql.append("FROM enterprise_analysis ");
        sql.append("WHERE 1=1 ");
        
        // 添加筛选条件（同 getSamplingData）
        if (periodIds != null && !periodIds.isEmpty()) {
            String ids = periodIds.stream().map(String::valueOf).collect(Collectors.joining(","));
            sql.append("AND period_id IN (").append(ids).append(") ");
        }
        
        if (regions != null && !regions.isEmpty()) {
            String codes = regions.stream().map(String::valueOf).collect(Collectors.joining(","));
            sql.append("AND region_code IN (").append(codes).append(") ");
        }
        
        if (industries != null && !industries.isEmpty()) {
            String codes = industries.stream().map(String::valueOf).collect(Collectors.joining(","));
            sql.append("AND industry_code IN (").append(codes).append(") ");
        }
        
        if (natures != null && !natures.isEmpty()) {
            String codes = natures.stream().map(String::valueOf).collect(Collectors.joining(","));
            sql.append("AND nature_code IN (").append(codes).append(") ");
        }
        
        sql.append("GROUP BY ").append(groupByColumn).append(", period_id ");
        sql.append("ORDER BY period_id, ").append(groupByColumn);
        
        try {
            List<Map<String, Object>> rawResults = executeQuery(sql.toString());
            List<Map<String, Object>> results = new ArrayList<>();
            
            // 转换结果
            for (Map<String, Object> row : rawResults) {
                Map<String, Object> result = new HashMap<>();
                
                Integer code = getIntegerValue(row.get(groupByColumn));
                String name = null;
                
                // 根据 groupBy 类型转换名称
                switch (groupBy) {
                    case "region":
                        name = dictService.getRegionName(code);
                        result.put("regionCode", code);
                        result.put("regionName", name);
                        break;
                    case "industry":
                        name = dictService.getIndustryName(code);
                        result.put("industryCode", code);
                        result.put("industryName", name);
                        break;
                    case "nature":
                        name = dictService.getNatureName(code);
                        result.put("natureCode", code);
                        result.put("natureName", name);
                        break;
                }
                
                result.put("periodId", row.get("period_id"));
                result.put("enterpriseCount", row.get("enterprise_count"));
                result.put("totalEmployed", row.get("total_employed"));
                result.put("totalUnemployed", row.get("total_unemployed"));
                result.put("unemploymentRate", formatRate(row.get("avg_unemployment_rate")));
                
                results.add(result);
            }
            
            logger.info("✓ 分析数据查询完成，groupBy={}, 返回 {} 条记录", groupBy, results.size());
            return results;
            
        } catch (Exception e) {
            logger.warn("⚠ 分析数据查询失败: {}", e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * 多维分析数据查询（用于 3D 可视化）
     * 
     * 返回所有维度组合的数据点
     * 每个数据点包含：地区、行业、性质、企业数、失业率
     */
    @Override
    public List<Map<String, Object>> getMultiDimensionalData(
            List<Long> periodIds,
            List<Integer> regions,
            List<Integer> industries,
            List<Integer> natures) {
        
        // 构建 SQL（与 getMultiDimensionalAnalysis 类似，但支持筛选）
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");
        sql.append("  period_id, ");
        sql.append("  region_code, ");
        sql.append("  industry_code, ");
        sql.append("  nature_code, ");
        sql.append("  COUNT(*) AS enterprise_count, ");
        sql.append("  SUM(CAST(construction_employed AS BIGINT)) AS total_construction_employed, ");
        sql.append("  SUM(CAST(employed_count AS BIGINT)) AS total_employed, ");
        sql.append("  SUM(CAST(unemployed_count AS BIGINT)) AS total_unemployed, ");
        sql.append("  AVG(CAST(unemployment_rate AS DOUBLE)) AS avg_unemployment_rate ");
        sql.append("FROM enterprise_analysis ");
        sql.append("WHERE region_code IS NOT NULL ");
        sql.append("  AND industry_code IS NOT NULL ");
        sql.append("  AND nature_code IS NOT NULL ");
        
        // 添加筛选条件
        if (periodIds != null && !periodIds.isEmpty()) {
            String ids = periodIds.stream().map(String::valueOf).collect(Collectors.joining(","));
            sql.append("AND period_id IN (").append(ids).append(") ");
        }
        
        if (regions != null && !regions.isEmpty()) {
            String codes = regions.stream().map(String::valueOf).collect(Collectors.joining(","));
            sql.append("AND region_code IN (").append(codes).append(") ");
        }
        
        if (industries != null && !industries.isEmpty()) {
            String codes = industries.stream().map(String::valueOf).collect(Collectors.joining(","));
            sql.append("AND industry_code IN (").append(codes).append(") ");
        }
        
        if (natures != null && !natures.isEmpty()) {
            String codes = natures.stream().map(String::valueOf).collect(Collectors.joining(","));
            sql.append("AND nature_code IN (").append(codes).append(") ");
        }
        
        sql.append("GROUP BY period_id, region_code, industry_code, nature_code ");
        sql.append("ORDER BY period_id, region_code, industry_code, nature_code");
        
        try {
            List<Map<String, Object>> rawResults = executeQuery(sql.toString());
            List<Map<String, Object>> results = new ArrayList<>();
            
            // 转换结果
            for (Map<String, Object> row : rawResults) {
                Map<String, Object> result = new HashMap<>();
                
                // 获取三个维度的代码
                Integer regionCode = getIntegerValue(row.get("region_code"));
                Integer industryCode = getIntegerValue(row.get("industry_code"));
                Integer natureCode = getIntegerValue(row.get("nature_code"));
                
                // 转换为名称
                String regionName = dictService.getRegionName(regionCode);
                String industryName = dictService.getIndustryName(industryCode);
                String natureName = dictService.getNatureName(natureCode);
                
                // 构建完整结果
                result.put("periodId", row.get("period_id"));
                result.put("regionCode", regionCode);
                result.put("regionName", regionName);
                result.put("industryCode", industryCode);
                result.put("industryName", industryName);
                result.put("natureCode", natureCode);
                result.put("natureName", natureName);
                result.put("enterpriseCount", row.get("enterprise_count"));
                result.put("constructionEmployed", row.get("total_construction_employed"));
                result.put("totalEmployed", row.get("total_employed"));
                result.put("totalUnemployed", row.get("total_unemployed"));
                result.put("unemploymentRate", formatRate(row.get("avg_unemployment_rate")));
                
                results.add(result);
            }
            
            logger.info("✓ 多维分析数据查询完成，返回 {} 个数据点", results.size());
            return results;
            
        } catch (Exception e) {
            logger.warn("⚠ 多维分析数据查询失败: {}", e.getMessage());
            return new ArrayList<>();
        }
    }
    
    // ==================== 工具方法 ====================
    
    /**
     * 关闭 JDBC 资源
     * 重要：防止连接泄漏
     */
    private void closeResources(ResultSet rs, Statement stmt, Connection conn) {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            logger.warn("关闭 JDBC 资源时出错", e);
        }
    }
}
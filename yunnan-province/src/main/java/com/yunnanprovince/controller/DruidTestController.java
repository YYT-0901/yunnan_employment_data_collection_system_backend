package com.yunnanprovince.controller;

import com.yunnancommon.service.DruidQueryService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Druid 测试控制器（临时用于测试，完成后可删除）
 * 
 * 接口列表：
 * - GET /test/druid/connection     - 测试连接
 * - GET /test/druid/query          - 执行原始查询
 * - GET /test/druid/region         - 按地区统计
 * - GET /test/druid/industry       - 按行业统计
 * - GET /test/druid/nature         - 按性质统计
 * - GET /test/druid/multi          - 多维分析
 * 
 * @author Phase 4
 * @since 2025-11-05
 */
@RestController
@RequestMapping("/test/druid")
public class DruidTestController {
    
    @Resource
    private DruidQueryService druidQueryService;
    
    /**
     * 测试 Druid 连接
     * GET /api/test/druid/connection
     */
    @GetMapping("/connection")
    public Map<String, Object> testConnection() {
        Map<String, Object> result = new HashMap<>();
        
        boolean connected = druidQueryService.testConnection();
        
        result.put("connected", connected);
        result.put("message", connected ? "Druid 连接成功 ✓" : "Druid 连接失败 ✗");
        
        return result;
    }
    
    /**
     * 执行原始 SQL 查询（用于测试和调试）
     * GET /api/test/druid/query?sql=SELECT 1
     */
    @GetMapping("/query")
    public Map<String, Object> executeQuery(@RequestParam String sql) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            List<Map<String, Object>> data = druidQueryService.executeQuery(sql);
            result.put("success", true);
            result.put("count", data.size());
            result.put("data", data);
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 按地区统计失业率
     * GET /api/test/druid/region
     */
    @GetMapping("/region")
    public Map<String, Object> getByRegion() {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> data = druidQueryService.getUnemploymentByRegion();
        
        result.put("count", data.size());
        result.put("data", data);
        
        return result;
    }
    
    /**
     * 按行业统计失业率
     * GET /api/test/druid/industry
     */
    @GetMapping("/industry")
    public Map<String, Object> getByIndustry() {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> data = druidQueryService.getUnemploymentByIndustry();
        
        result.put("count", data.size());
        result.put("data", data);
        
        return result;
    }
    
    /**
     * 按性质统计失业率
     * GET /api/test/druid/nature
     */
    @GetMapping("/nature")
    public Map<String, Object> getByNature() {  // ← 这里加上方法名 getByNature()
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> data = druidQueryService.getUnemploymentByNature();
        
        result.put("count", data.size());
        result.put("data", data);
        
        return result;
    }
    
    /**
     * 多维分析（3D 数据）
     * GET /api/test/druid/multi
     */
    @GetMapping("/multi")
    public Map<String, Object> getMultiDimensional() {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> data = druidQueryService.getMultiDimensionalAnalysis();
        
        result.put("count", data.size());
        result.put("data", data);
        
        return result;
    }
}
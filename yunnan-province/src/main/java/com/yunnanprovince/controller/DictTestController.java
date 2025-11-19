package com.yunnanprovince.controller;

import com.yunnancommon.service.DictService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 字典测试控制器（临时用于测试，完成后可删除）
 */
@RestController
@RequestMapping("/api/test/dict")
public class DictTestController {

    @Resource
    private DictService dictService;

    /**
     * 测试地区转换
     * GET /api/test/dict/region?code=1
     */
    @GetMapping("/region")
    public Map<String, Object> testRegion(@RequestParam Integer code) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", code);
        result.put("name", dictService.getRegionName(code));
        return result;
    }

    /**
     * 测试性质转换
     * GET /api/test/dict/nature?code=1
     */
    @GetMapping("/nature")
    public Map<String, Object> testNature(@RequestParam Integer code) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", code);
        result.put("name", dictService.getNatureName(code));
        return result;
    }

    /**
     * 测试行业转换
     * GET /api/test/dict/industry?code=5
     */
    @GetMapping("/industry")
    public Map<String, Object> testIndustry(@RequestParam Integer code) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", code);
        result.put("name", dictService.getIndustryName(code));
        return result;
    }

    /**
     * 获取所有字典
     * GET /api/test/dict/all
     */
    @GetMapping("/all")
    public Map<String, Object> getAllDicts() {
        Map<String, Object> result = new HashMap<>();
        result.put("regions", dictService.getAllRegions());
        result.put("natures", dictService.getAllNatures());
        result.put("industries", dictService.getAllIndustries());
        return result;
    }
}
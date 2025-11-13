package com.yunnancommon.service;

import com.yunnancommon.service.impl.DictServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

// DictService 单元测试
// 测试范围:
// 1. 字典加载测试
// 2. 单个转换测试
// 3. 多个转换测试
// 4. 边界条件测试


class DictServiceTest {
    private DictService dictService;

    @BeforeEach
    void setUp() {
        DictServiceImpl impl = new DictServiceImpl();
        impl.init();
        dictService = impl;
    }

    @Test
    void testRegionNameConversion() {
        // 测试地区转换
        assertEquals("临沧市", dictService.getRegionName(1));
        assertEquals("昆明市", dictService.getRegionName(8));
        assertEquals("曲靖市", dictService.getRegionName(11));
        assertEquals("未知", dictService.getRegionName(99));
        assertEquals("未知", dictService.getRegionName(null));
    }

    @Test
    void testNatureNameConversion() {
        // 测试性质转换
        assertEquals("国有企业", dictService.getNatureName(1));
        assertEquals("私营企业", dictService.getNatureName(3));
        assertEquals("未知", dictService.getNatureName(99));
    }

    @Test
    void testIndustryNameConversion() {
        // 测试行业转换
        assertEquals("建筑业", dictService.getIndustryName(5));
        assertEquals("金融业", dictService.getIndustryName(10));
        assertEquals("未知", dictService.getIndustryName(99));
    }

    @Test
    void testGetAllRegions() {
        // 测试获取所有地区
        Map<Integer, String> regions = dictService.getAllRegions();
        assertEquals(16, regions.size());
        assertTrue(regions.containsKey(1));
        assertTrue(regions.containsKey(8));
    }

    @Test
    void testGetAllNatures() {
        // 测试获取所有性质
        Map<Integer, String> natures = dictService.getAllNatures();
        assertEquals(8, natures.size());
    }

    @Test
    void testGetAllIndustries() {
        // 测试获取所有行业
        Map<Integer, String> industries = dictService.getAllIndustries();
        assertEquals(20, industries.size());
    }

    @Test
    void testBatchConvertRegions() {
        // 测试批量转换地区
        Map<Integer, String> result = dictService.batchConvertRegions(
            Arrays.asList(1, 8, 11)
        );

        assertEquals(3, result.size());
        assertEquals("临沧市", result.get(1));
        assertEquals("昆明市", result.get(8));
        assertEquals("曲靖市", result.get(11));
    }

    @Test
    void testBatchConvertWithDuplicates() {
        // 测试批量转换（包含重复）
        Map<Integer, String> result = dictService.batchConvertRegions(
            Arrays.asList(1, 1, 8, 8, 11)
        );

        // 应该自动去重
        assertEquals(3, result.size());
    }

    @Test
    void testBatchConvertWithNulls() {
        // 测试批量转换（包含null）
        Map<Integer, String> result = dictService.batchConvertRegions(
            Arrays.asList(1, null, 8)
        );

        // null应该被过滤掉
        assertEquals(2, result.size());
        assertFalse(result.containsKey(null));
    }
}


package com.yunnancommon.service;
import java.util.Map;

// 提供地区、性质、行业一级分类代码转换到名称

public interface DictService {
    String getRegionName(Integer code);
    
    String getNatureName(Integer code);

    String getIndustryName(Integer code);

    Map<Integer, String> getAllRegions();

    Map<Integer, String> getAllNatures();

    Map<Integer, String> getAllIndustries();

    Map<Integer, String> batchConvertRegions(java.util.List<Integer> codes);

    Map<Integer, String> batchConvertNatures(java.util.List<Integer> codes);

    Map<Integer, String> batchConvertIndustries(java.util.List<Integer> codes);

}
package com.yunnancommon.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yunnancommon.service.DictService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service("dictService")
public class DictServiceImpl implements DictService {
    
    private static final Logger logger = LoggerFactory.getLogger(DictServiceImpl.class);

    private final Map<Integer, String> regionDict = new ConcurrentHashMap<>();

    private final Map<Integer, String> natureDict = new ConcurrentHashMap<>();

    private final Map<Integer, String> industryDict = new ConcurrentHashMap<>();

    // JSON解析器
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    public void init() {
        logger.info("========== 开始加载数据字典 ==========");
        long startTime = System.currentTimeMillis();

        try {
            // 1. 加载地区字典
            loadRegionDict();
            logger.info("✓ 地区字典加载成功，共{}条", regionDict.size());

            // 2. 加载性质字典
            loadNatureDict();
            logger.info("✓ 性质字典加载成功，共{}条", natureDict.size());

            // 3. 加载行业字典
            loadIndustryDict();
            logger.info("✓ 行业字典加载成功，共{}条", industryDict.size());

            long endTime = System.currentTimeMillis();
            logger.info("========== 数据字典加载完成，耗时{}ms ==========", endTime - startTime);

        } catch (Exception e) {
            logger.error("✗ 数据字典加载失败，应用将无法启动", e);
            throw new RuntimeException("数据字典加载失败", e);
        }
    }

    // 加载地区字典
    private void loadRegionDict() throws IOException {
        logger.debug("正在加载 region_dict.json...");
        
        // 1. 从classpath加载资源文件
        ClassPathResource resource = new ClassPathResource("dict/region_dict.json");
        
        // 2. 获取输入流
        try (InputStream inputStream = resource.getInputStream()) {
            // 3. 解析JSON为Map<String, String>
            Map<String, String> tempMap = objectMapper.readValue(
                inputStream,
                new TypeReference<Map<String, String>>() {}
            );

            // 4. 转换key为Integer并存入缓存
            tempMap.forEach((key, value) -> {
                try {
                    Integer code = Integer.parseInt(key);
                    regionDict.put(code, value);
                } catch (NumberFormatException e) {
                    logger.warn("地区字典中发现无效的key: {}", key);
                }
            });
        }
    }

    // 加载性质字典
    private void loadNatureDict() throws IOException {
        logger.debug("正在加载 nature_dict.json");

        ClassPathResource resource = new ClassPathResource("dict/nature_dict.json");

        try (InputStream inputStream = resource.getInputStream()) {
            Map<String, String> tempMap = objectMapper.readValue(
                inputStream,
                new TypeReference<Map<String, String>>() {}
            );

            tempMap.forEach((key,value)-> {
                try {
                    Integer code = Integer.parseInt(key);
                    natureDict.put(code,value);
                } catch (NumberFormatException e){
                    logger.warn("性质字典中发现无效的key: {}",key);
                }
            });
        }
    }

    // 加载行业字典
    private void loadIndustryDict() throws IOException {
        logger.debug("正在加载 industry_dict.json...");
        
        ClassPathResource resource = new ClassPathResource("dict/industry_dict.json");
        
        try (InputStream inputStream = resource.getInputStream()) {
            Map<String, String> tempMap = objectMapper.readValue(
                inputStream,
                new TypeReference<Map<String, String>>() {}
            );

            tempMap.forEach((key, value) -> {
                try {
                    Integer code = Integer.parseInt(key);
                    industryDict.put(code, value);
                } catch (NumberFormatException e) {
                    logger.warn("行业字典中发现无效的key: {}", key);
                }
            });
        }
    }

    // 实现接口方法
    @Override
    public String getRegionName(Integer code) {
        if (code == null) {
            return "未知";
        }
        return regionDict.getOrDefault(code, "未知");
    }

    @Override
    public String getNatureName(Integer code) {
        if (code == null) {
            return "未知";
        }
        return natureDict.getOrDefault(code, "未知");
    }

    @Override
    public String getIndustryName(Integer code) {
        if (code == null) {
            return "未知";
        }
        return industryDict.getOrDefault(code, "未知");
    }

    @Override
    public Map<Integer, String> getAllRegions() {
        // 返回不可修改的副本，防止外部修改缓存
        return new HashMap<>(regionDict);
    }

    @Override
    public Map<Integer, String> getAllNatures() {
        return new HashMap<>(natureDict);
    }

    @Override
    public Map<Integer, String> getAllIndustries() {
        return new HashMap<>(industryDict);
    }

    @Override
    public Map<Integer, String> batchConvertRegions(List<Integer> codes) {
        if (codes == null || codes.isEmpty()) {
            return new HashMap<>();
        }

        return codes.stream()
            .filter(code -> code != null)
            .distinct()  // 去重
            .collect(Collectors.toMap(
                code -> code,
                this::getRegionName
            ));
    }

    @Override
    public Map<Integer, String> batchConvertNatures(List<Integer> codes) {
        if (codes == null || codes.isEmpty()) {
            return new HashMap<>();
        }

        return codes.stream()
            .filter(code -> code != null)
            .distinct()
            .collect(Collectors.toMap(
                code -> code,
                this::getNatureName
            ));
    }

    @Override
    public Map<Integer, String> batchConvertIndustries(List<Integer> codes) {
        if (codes == null || codes.isEmpty()) {
            return new HashMap<>();
        }

        return codes.stream()
            .filter(code -> code != null)
            .distinct()
            .collect(Collectors.toMap(
                code -> code,
                this::getIndustryName
            ));
    }
    
}
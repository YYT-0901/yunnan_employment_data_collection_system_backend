package com.yunnanenterprise.dictionary;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yunnanenterprise.constants.ReportConstants;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 词典服务：
 * - 从 classpath 读取 JSON（一次性加载到内存）
 * - 对外提供列表（返回给前端）和 code<->id 的映射（写入数据库用）
 *
 * 说明：
 * - DB 为 int，前端为字符串 code，我们用列表顺序做一个稳定的 1-based 映射。
 * - 将 "other" 统一规范化为 "OTHER" 以匹配前端。
 */

@Service
public class DictionaryService {

    // dict path 
    private static final String TYPES_PATH = "dictionaries/employment_reduction_types.json";
    private static final String CAUSES_PATH = "dictionaries/employment_reduction_causes.json";

    private final ObjectMapper mapper = new ObjectMapper();

    // return to the frontend directly
    private List<DictionaryItem> reductionTypes = Collections.emptyList();
    private List<DictionaryItem> reductionCauses = Collections.emptyList();

    // bidirectional index
    private final Map<String, Integer> typeCodeToId = new ConcurrentHashMap<>();
    private final Map<Integer, String> typeIdToCode = new ConcurrentHashMap<>();
    private final Map<String, Integer> causeCodeToId = new ConcurrentHashMap<>();
    private final Map<Integer, String> causeIdToCode = new ConcurrentHashMap<>();

    @jakarta.annotation.PostConstruct
    public void load() throws Exception {
        this.reductionTypes = loadAndNormalize(TYPES_PATH);
        this.reductionCauses = loadAndNormalize(CAUSES_PATH);
        buildIndex(this.reductionTypes, typeCodeToId, typeIdToCode);
        buildIndex(this.reductionCauses, causeCodeToId, causeIdToCode);
    }

    public List<DictionaryItem> getReductionTypes() {
        return reductionTypes;
    }

    public List<DictionaryItem> getReductionCauses() {
        return reductionCauses;
    }

    public Integer typeCodeToId(String code) {
        if (code == null)
            return null;
        return typeCodeToId.get(code);
    }

    public String typeIdToCode(Integer id) {
        if (id == null)
            return null;
        return typeIdToCode.get(id);
    }

    public Integer causeCodeToId(String code) {
        if (code == null)
            return null;
        return causeCodeToId.get(code);
    }

    public String causeIdToCode(Integer id) {
        if (id == null)
            return null;
        return causeIdToCode.get(id);
    }

    private List<DictionaryItem> loadAndNormalize(String classpathLocation) throws Exception {
        ClassPathResource resource = new ClassPathResource(classpathLocation);
        try (InputStream in = resource.getInputStream()) {
            List<Map<String, Object>> raw = mapper.readValue(in, new TypeReference<List<Map<String, Object>>>() {
            });
            List<DictionaryItem> list = new ArrayList<>();
            for (Map<String, Object> m : raw) {
                String code = Objects.toString(m.getOrDefault("value", ""), "");
                String label = Objects.toString(m.getOrDefault("label", code), code);
                String desc = Objects.toString(m.getOrDefault("description", ""), "");
                // 统一“other”大小写，适配前端 OTHER 判断
                if ("other".equalsIgnoreCase(code)) {
                    code = ReportConstants.OTHER_CODE; // "OTHER"
                }
                list.add(new DictionaryItem(code, label, desc));
            }
            return Collections.unmodifiableList(list);
        }
    }

    private void buildIndex(List<DictionaryItem> items,
            Map<String, Integer> codeToId,
            Map<Integer, String> idToCode) {
        codeToId.clear();
        idToCode.clear();
        for (int i = 0; i < items.size(); i++) {
            int id = i + 1; // 1-based
            String code = items.get(i).getValue();
            codeToId.put(code, id);
            idToCode.put(id, code);
        }
    }

}
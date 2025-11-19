package com.yunnancommon.utils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RegionUtils {
    private static final Logger logger = LoggerFactory.getLogger(RegionUtils.class);

    // code -> RegionNode
    private static Map<Integer, RegionNode> regionMap = new HashMap<>();

    private static List<RegionNode> rootRegions = new ArrayList<>();

    public static class RegionNode {
        private Integer code;
        private String name;
        private Integer parentId;
        private List<RegionNode> children;

        public Integer getCode() {
            return code;
        }

        public void setCode(Integer code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getParentId() {
            return parentId;
        }

        public void setParentId(Integer parentId) {
            this.parentId = parentId;
        }

        public List<RegionNode> getChildren() {
            return children;
        }

        public void setChildren(List<RegionNode> children) {
            this.children = children;
        }
    }

    static {
        loadRegionData();
    }

    // load the region data from the json file
    private static void loadRegionData() {
        try {
            InputStream inputStream = RegionUtils.class.getClassLoader()
                    .getResourceAsStream("dict/yunnan_region_code.json");

            if (inputStream == null) {
                logger.error("can't find the region code file: dict/yunnan_region_code.json");
                return;
            }

            // using the Jaskson to analysis the JSON
            ObjectMapper mapper = new ObjectMapper();
            rootRegions = mapper.readValue(inputStream, new TypeReference<List<RegionNode>>() {
            });

            // recursion and create the Map cache
            for (RegionNode region : rootRegions) {
                buildRegionMap(region);
            }

            logger.info("regionCode loading succesfully, total {} got region, in the cache have {} regionCode",
                    rootRegions.size(), regionMap.size());
        } catch (Exception e) {
            logger.error("failed to load this regionCode", e);
        }
    }

    private static void buildRegionMap(RegionNode node) {
        if (node == null)
            return;

        regionMap.put(node.getCode(), node);

        if (node.getChildren() != null) {
            for (RegionNode child : node.getChildren()) {
                buildRegionMap(child);
            }
        }
    }

    // according the code to get the region name
    public static String getNameByCode(Integer code) {
        if (code == null) {
            return "unknown";
        }

        RegionNode node = regionMap.get(code);
        return node != null ? node.getName() : String.valueOf(code);
    }

    // according the code the to get the full address
    public static String getFullPathByCode(Integer code) {
        if (code == null) {
            return "unknown";
        }

        List<String> path = new ArrayList<>();
        RegionNode current = regionMap.get(code);

        while (current != null) {
            path.add(0, current.getName());
            Integer parentCode = current.getParentId();
            current = parentCode != null ? regionMap.get(parentCode) : null;
        }

        return "云南省/" + String.join("/", path);
    }

    // expand all the child code
    public static List<Integer> expandChildCodes(Integer parentCode) {
        List<Integer> result = new ArrayList<>();

        if (parentCode == null) {
            return result;
        }

        RegionNode node = regionMap.get(parentCode);
        if (node == null) {
            return result;
        }

        result.add(parentCode);

        collectChildCodes(node, result);

        return result;
    }

    private static void collectChildCodes(RegionNode node, List<Integer> result) {
        if (node.getChildren() == null || node.getChildren().isEmpty()) {
            return;
        }

        for (RegionNode child : node.getChildren()) {
            result.add(child.getCode());
            collectChildCodes(child, result);
        }
    }

    // expand the parent code in parallel
    public static List<Integer> expandChildCodesInBatch(List<Integer> parentCodes) {
        if (parentCodes == null || parentCodes.isEmpty()) {
            return new ArrayList<>();
        }

        Set<Integer> resultSet = new HashSet<>();

        for (Integer parentCode : parentCodes) {
            resultSet.addAll(expandChildCodes(parentCode));
        }

        return new ArrayList<>(resultSet);
    }

    /**
     * Get top-level parent code (first-level classification)
     * 
     * @param code - region code (can be any level)
     * @return top-level parent code (city/prefecture level in Yunnan)
     * 
     * @example
     * getTopLevelParentCode(530102001) // returns 8 (昆明市)
     * getTopLevelParentCode(530102) // returns 8 (昆明市)
     * getTopLevelParentCode(8) // returns 8 (already top-level)
     */
    public static Integer getTopLevelParentCode(Integer code) {
        if (code == null) {
            return null;
        }

        RegionNode current = regionMap.get(code);
        if (current == null) {
            return code; // return as-is if not found
        }

        // Traverse up to find the root parent (top-level)
        while (current.getParentId() != null) {
            RegionNode parent = regionMap.get(current.getParentId());
            if (parent == null) {
                break;
            }
            current = parent;
        }

        return current.getCode();
    }

    /**
     * Check if the given code is a top-level (first-level) code
     * 
     * @param code - region code
     * @return true if it's a top-level code
     */
    public static boolean isTopLevel(Integer code) {
        if (code == null) {
            return false;
        }

        RegionNode node = regionMap.get(code);
        if (node == null) {
            return false;
        }

        Integer parentId = node.getParentId();
        return parentId == null || parentId == 0;
    }

    /**
     * 获取离顶级最近的下一级（区/县）code
     * - 如果原本就是市级（顶级），返回自身
     * - 如果是区县，返回区县自身
     * - 如果是街道/乡镇，返回所属区县
     */
    public static Integer getSecondLevelParentCode(Integer code) {
        if (code == null) {
            return null;
        }

        RegionNode current = regionMap.get(code);
        if (current == null) {
            return code;
        }

        while (current.getParentId() != null && current.getParentId() != 0) {
            Integer parentId = current.getParentId();
            RegionNode parent = regionMap.get(parentId);
            if (parent == null) {
                break;
            }

            if (isTopLevel(parent.getCode())) {
                // 当前节点的父级已经是顶级，则当前节点就是二级
                return current.getCode();
            }

            current = parent;
        }

        return current.getCode();
    }

    /**
     * Get all top-level region codes
     * 
     * @return list of all top-level codes (16 cities/prefectures in Yunnan)
     */
    public static List<Integer> getAllTopLevelCodes() {
        List<Integer> result = new ArrayList<>();
        for (RegionNode region : rootRegions) {
            result.add(region.getCode());
        }
        return result;
    }

}

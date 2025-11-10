package com.yunnancommon.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 二级地区取样分析结果
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SamplingRegionDetailVO {
    private Integer regionCode;
    private String regionName;
    private Integer enterpriseCount;
    private Double percentage;
}

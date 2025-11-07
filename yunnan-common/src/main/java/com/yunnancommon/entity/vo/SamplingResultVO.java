package com.yunnancommon.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SamplingResultVO {
    private Integer regionCode;
    private String regionName;
    private Integer enterpriseCount;
    // 该地区企业数量占全省的百分比
    private Double percentage;
}

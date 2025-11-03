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
    private Double percentage;
}

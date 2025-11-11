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
    // 建档期总岗位数
    private Integer constructionTotal;
    // 调查期总岗位数
    private Integer investigationTotal;
    // 岗位变化总数
    private Integer changeTotal;
    // 岗位变化占比
    private Double changeRatio;
}

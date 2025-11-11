package com.yunnancommon.entity.vo;

import java.util.List;

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
    // 建档期总岗位数
    private Integer constructionTotal;
    // 调查期总岗位数
    private Integer investigationTotal;
    // 岗位变化总数
    private Integer changeTotal;
    // 岗位变化占比
    private Double changeRatio;
    // 二级地区明细
    private List<SamplingRegionDetailVO> children;
}

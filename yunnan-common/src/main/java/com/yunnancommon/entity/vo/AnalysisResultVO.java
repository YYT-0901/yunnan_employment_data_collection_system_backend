package com.yunnancommon.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// use to return the main index:
// 1. 企业总数
// 2. 建档期总岗位数
// 3. 调查期总岗位数
// 4. 岗位变化总数
// 5. 岗位减少总数
// 6. 岗位变化占比（失业率）

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnalysisResultVO {
    // region or nature or industry depend on the groupBy
    private String dimensionCode;
    private String dimensionName;
    // 企业总数
    private Integer enterpriseCount;
    // 建档期总岗位数
    private Integer constructionTotal;
    // 调查期总岗位数
    private Integer investigationTotal;
    // 岗位变化总数
    private Integer changeTotal;
    // 岗位数减少总数
    private Integer reductionTotal;
    // 岗位变化占比(失业率)
    private Double changeRatio;

    private Long periodId;

    private String periodName;
}

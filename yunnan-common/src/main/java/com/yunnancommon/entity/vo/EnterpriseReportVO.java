package com.yunnancommon.entity.vo;

import com.yunnancommon.entity.po.EnterpriseReportInfo;
import lombok.Data;

@Data
public class EnterpriseReportVO extends EnterpriseReportInfo {
    private String enterpriseName;
    private Integer constructionCount;
    private Integer investigationCount;
    private String investigateTime;
}

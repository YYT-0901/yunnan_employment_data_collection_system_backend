package com.yunnancommon.entity.vo;

import com.yunnancommon.entity.po.EnterpriseReportInfo;
import lombok.Data;

@Data
public class XmlReportVO extends EnterpriseReportInfo {
    // ReportInfo 字段
    private String reportId;
    private Integer constructionCount;
    private Integer investigationCount;
    private Integer reductionType;
    private Integer reason1;
    private String reason1Desc;
    private Integer reason2;
    private String reason2Desc;
    private Integer reason3;
    private String reason3Desc;
    private String otherReason;

    // EnterpriseInfo 字段
    private String enterpriseName;
}

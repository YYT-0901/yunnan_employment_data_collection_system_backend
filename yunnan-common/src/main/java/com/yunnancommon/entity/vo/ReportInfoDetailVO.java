package com.yunnancommon.entity.vo;

import com.yunnancommon.entity.po.ReportInfo;
import lombok.Data;

@Data
public class ReportInfoDetailVO extends ReportInfo {
    // 企业名称
    private String enterpriseName;
    // 调查期时间
    private String investigateTime;
}

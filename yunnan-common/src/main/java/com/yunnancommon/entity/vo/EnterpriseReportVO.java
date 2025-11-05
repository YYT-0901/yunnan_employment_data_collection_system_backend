package com.yunnancommon.entity.vo;

import com.yunnancommon.entity.po.EnterpriseReportInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class EnterpriseReportVO extends EnterpriseReportInfo {
    private String enterpriseName;
}

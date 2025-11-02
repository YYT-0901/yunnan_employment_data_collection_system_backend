package com.yunnancommon.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 企业报告外键ID
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnterpriseInfoReportDto {
    private String enterpriseId;
    private Long periodId;
    private String reportId;
    private String rejectReason;
}

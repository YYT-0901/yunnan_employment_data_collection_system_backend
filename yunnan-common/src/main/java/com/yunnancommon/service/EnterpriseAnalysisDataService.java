package com.yunnancommon.service;

public interface EnterpriseAnalysisDataService {

    /**
     * 审核通过/归档后，将报表汇总写入 enterprise_analysis_data
     */
    void writeFromReport(String enterpriseId, Long periodId, String reportId);
}

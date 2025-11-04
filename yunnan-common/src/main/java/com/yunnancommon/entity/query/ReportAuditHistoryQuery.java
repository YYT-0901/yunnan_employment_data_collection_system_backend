package com.yunnancommon.entity.query;

public class ReportAuditHistoryQuery extends BaseQuery {

    /**
     * 审核记录ID
     */
    private Long auditId;

    /**
     * 企业ID
     */
    private String enterpriseId;
    private String enterpriseIdFuzzy;

    /**
     * 调查期ID
     */
    private Long periodId;

    /**
     * 报表ID
     */
    private String reportId;
    private String reportIdFuzzy;

    /**
     * 审核层级：1-市级 2-省级
     */
    private Integer auditLevel;

    /**
     * 审核人
     */
    private String auditor;
    private String auditorFuzzy;

    /**
     * 审核结果：1-通过 2-驳回
     */
    private Integer auditResult;

    /**
     * 审核时间范围
     */
    private String auditTimeStart;
    private String auditTimeEnd;

    // Getter and Setter
    public Long getAuditId() {
        return auditId;
    }

    public void setAuditId(Long auditId) {
        this.auditId = auditId;
    }

    public String getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(String enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public String getEnterpriseIdFuzzy() {
        return enterpriseIdFuzzy;
    }

    public void setEnterpriseIdFuzzy(String enterpriseIdFuzzy) {
        this.enterpriseIdFuzzy = enterpriseIdFuzzy;
    }

    public Long getPeriodId() {
        return periodId;
    }

    public void setPeriodId(Long periodId) {
        this.periodId = periodId;
    }

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    public String getReportIdFuzzy() {
        return reportIdFuzzy;
    }

    public void setReportIdFuzzy(String reportIdFuzzy) {
        this.reportIdFuzzy = reportIdFuzzy;
    }

    public Integer getAuditLevel() {
        return auditLevel;
    }

    public void setAuditLevel(Integer auditLevel) {
        this.auditLevel = auditLevel;
    }

    public String getAuditor() {
        return auditor;
    }

    public void setAuditor(String auditor) {
        this.auditor = auditor;
    }

    public String getAuditorFuzzy() {
        return auditorFuzzy;
    }

    public void setAuditorFuzzy(String auditorFuzzy) {
        this.auditorFuzzy = auditorFuzzy;
    }

    public Integer getAuditResult() {
        return auditResult;
    }

    public void setAuditResult(Integer auditResult) {
        this.auditResult = auditResult;
    }

    public String getAuditTimeStart() {
        return auditTimeStart;
    }

    public void setAuditTimeStart(String auditTimeStart) {
        this.auditTimeStart = auditTimeStart;
    }

    public String getAuditTimeEnd() {
        return auditTimeEnd;
    }

    public void setAuditTimeEnd(String auditTimeEnd) {
        this.auditTimeEnd = auditTimeEnd;
    }
}

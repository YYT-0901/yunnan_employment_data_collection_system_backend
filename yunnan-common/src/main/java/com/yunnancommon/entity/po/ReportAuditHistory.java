package com.yunnancommon.entity.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

public class ReportAuditHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 审核记录ID（自增主键）
     */
    private Long auditId;
    
    /**
     * 企业ID
     */
    private String enterpriseId;
    
    /**
     * 调查期ID
     */
    private Long periodId;
    
    /**
     * 被审核的报表ID
     */
    private String reportId;
    
    /**
     * 审核层级：1-市级审核 2-省级审核
     */
    private Integer auditLevel;
    
    /**
     * 审核人username
     */
    private String auditor;
    
    /**
     * 审核结果：1-通过 2-驳回
     */
    private Integer auditResult;
    
    /**
     * 审核意见（完整意见，text类型）
     */
    private String auditOpinion;
    
    /**
     * 审核时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date auditTime;
    
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
    
    public Integer getAuditResult() {
        return auditResult;
    }
    
    public void setAuditResult(Integer auditResult) {
        this.auditResult = auditResult;
    }
    
    public String getAuditOpinion() {
        return auditOpinion;
    }
    
    public void setAuditOpinion(String auditOpinion) {
        this.auditOpinion = auditOpinion;
    }
    
    public Date getAuditTime() {
        return auditTime;
    }
    
    public void setAuditTime(Date auditTime) {
        this.auditTime = auditTime;
    }

}

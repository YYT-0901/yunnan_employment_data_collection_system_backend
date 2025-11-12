package com.yunnanenterprise.dto.report;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 返回给前端的报表视图对象：
 * 字段命名与 ReportFormView.vue 的 v-model 一一对应。
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReportV0 {

    @JsonProperty("id")
    private String id;

    @JsonProperty("old_id")
    private String oldId;

    @JsonProperty("enterprise_id")
    private String enterpriseId;

    @JsonProperty("reporting_period")
    private String reportingPeriod;

    @JsonProperty("status")
    private String status;

    @JsonProperty("initial_employees")
    private Integer initialEmployees;

    @JsonProperty("current_employees")
    private Integer currentEmployees;

    @JsonProperty("reduction_type_code")
    private String reductionTypeCode;

    @JsonProperty("reduction_type_desc")
    private String reductionTypeDesc;

    @JsonProperty("primary_reason_code")
    private String primaryReasonCode;

    @JsonProperty("secondary_reason_code")
    private String secondaryReasonCode;

    @JsonProperty("tertiary_reason_code")
    private String tertiaryReasonCode;

    @JsonProperty("primary_reason_desc")
    private String primaryReasonDesc;

    @JsonProperty("secondary_reason_desc")
    private String secondaryReasonDesc;

    @JsonProperty("tertiary_reason_desc")
    private String tertiaryReasonDesc;

    @JsonProperty("submitted_at")
    private String submittedAt;

    @JsonProperty("updated_at")
    private String updatedAt;

    @JsonProperty("period_start_time")
    private String periodStartTime;

    @JsonProperty("period_end_time")
    private String periodEndTime;

    @JsonProperty("is_initial_employees_locked")
    private Boolean isInitialEmployeesLocked;

    @JsonProperty("is_current_employees_locked")
    private Boolean isCurrentEmployeesLocked;

    @JsonProperty("editable")
    private Boolean editable;

    @JsonProperty("can_resubmit")
    private Boolean canResubmit;

    @JsonProperty("latest_audit_level")
    private Integer latestAuditLevel;

    @JsonProperty("latest_audit_level_name")
    private String latestAuditLevelName;

    @JsonProperty("latest_audit_result")
    private Integer latestAuditResult;

    @JsonProperty("latest_audit_result_name")
    private String latestAuditResultName;

    @JsonProperty("latest_audit_opinion")
    private String latestAuditOpinion;

    @JsonProperty("latest_audit_time")
    private String latestAuditTime;

    @JsonProperty("reason_return")
    private String reasonReturn;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getOldId() { return oldId; }
    public void setOldId(String oldId) { this.oldId = oldId; }

    public String getEnterpriseId() { return enterpriseId; }
    public void setEnterpriseId(String enterpriseId) { this.enterpriseId = enterpriseId; }

    public String getReportingPeriod() { return reportingPeriod; }
    public void setReportingPeriod(String reportingPeriod) { this.reportingPeriod = reportingPeriod; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getInitialEmployees() { return initialEmployees; }
    public void setInitialEmployees(Integer initialEmployees) { this.initialEmployees = initialEmployees; }

    public Integer getCurrentEmployees() { return currentEmployees; }
    public void setCurrentEmployees(Integer currentEmployees) { this.currentEmployees = currentEmployees; }

    public String getReductionTypeCode() { return reductionTypeCode; }
    public void setReductionTypeCode(String reductionTypeCode) { this.reductionTypeCode = reductionTypeCode; }

    public String getReductionTypeDesc() { return reductionTypeDesc; }
    public void setReductionTypeDesc(String reductionTypeDesc) { this.reductionTypeDesc = reductionTypeDesc; }

    public String getPrimaryReasonCode() { return primaryReasonCode; }
    public void setPrimaryReasonCode(String primaryReasonCode) { this.primaryReasonCode = primaryReasonCode; }

    public String getSecondaryReasonCode() { return secondaryReasonCode; }
    public void setSecondaryReasonCode(String secondaryReasonCode) { this.secondaryReasonCode = secondaryReasonCode; }

    public String getTertiaryReasonCode() { return tertiaryReasonCode; }
    public void setTertiaryReasonCode(String tertiaryReasonCode) { this.tertiaryReasonCode = tertiaryReasonCode; }

    public String getPrimaryReasonDesc() { return primaryReasonDesc; }
    public void setPrimaryReasonDesc(String primaryReasonDesc) { this.primaryReasonDesc = primaryReasonDesc; }

    public String getSecondaryReasonDesc() { return secondaryReasonDesc; }
    public void setSecondaryReasonDesc(String secondaryReasonDesc) { this.secondaryReasonDesc = secondaryReasonDesc; }

    public String getTertiaryReasonDesc() { return tertiaryReasonDesc; }
    public void setTertiaryReasonDesc(String tertiaryReasonDesc) { this.tertiaryReasonDesc = tertiaryReasonDesc; }

    public String getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(String submittedAt) { this.submittedAt = submittedAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    public String getPeriodStartTime() {return periodStartTime;}
    public void setPeriodStartTime(String periodStartTime) { this.periodStartTime = periodStartTime;}

    public String getPeriodEndTime() {return periodEndTime;}
    public void setPeriodEndTime(String periodEndTime) { this.periodEndTime = periodEndTime;}

    public Boolean getIsInitialEmployeesLocked() { return isInitialEmployeesLocked; }
    public void setIsInitialEmployeesLocked(Boolean isInitialEmployeesLocked) { 
        this.isInitialEmployeesLocked = isInitialEmployeesLocked; 
    }

    public Boolean getIsCurrentEmployeesLocked() { return isCurrentEmployeesLocked; }
    public void setIsCurrentEmployeesLocked(Boolean isCurrentEmployeesLocked ) {
        this.isCurrentEmployeesLocked = isCurrentEmployeesLocked;
    }

    public Boolean getEditable() { return editable; }
    public void setEditable(Boolean editable) { this.editable = editable; }

    public Boolean getCanResubmit() { return canResubmit; }
    public void setCanResubmit(Boolean canResubmit) { this.canResubmit = canResubmit; }

    public Integer getLatestAuditLevel() { return latestAuditLevel; }
    public void setLatestAuditLevel(Integer latestAuditLevel) { this.latestAuditLevel = latestAuditLevel; }

    public String getLatestAuditLevelName() { return latestAuditLevelName; }
    public void setLatestAuditLevelName(String latestAuditLevelName) { this.latestAuditLevelName = latestAuditLevelName; }

    public Integer getLatestAuditResult() { return latestAuditResult; }
    public void setLatestAuditResult(Integer latestAuditResult) { this.latestAuditResult = latestAuditResult; }

    public String getLatestAuditResultName() { return latestAuditResultName; }
    public void setLatestAuditResultName(String latestAuditResultName) { this.latestAuditResultName = latestAuditResultName; }

    public String getLatestAuditOpinion() { return latestAuditOpinion; }
    public void setLatestAuditOpinion(String latestAuditOpinion) { this.latestAuditOpinion = latestAuditOpinion; }

    public String getLatestAuditTime() { return latestAuditTime; }
    public void setLatestAuditTime(String latestAuditTime) { this.latestAuditTime = latestAuditTime; }

    public String getReasonReturn() { return reasonReturn; }
    public void setReasonReturn(String reasonReturn) { this.reasonReturn = reasonReturn; }
}
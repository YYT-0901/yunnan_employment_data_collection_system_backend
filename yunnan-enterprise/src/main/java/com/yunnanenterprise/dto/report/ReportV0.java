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

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

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

}
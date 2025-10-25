package com.yunnanenterprise.dto.report;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

/**
 * 前端提交/暂存报表时的 JSON 数据模型。
 * 字段名通过 @JsonProperty 与前端一一对齐。
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReportCommand {

    @JsonProperty("id")
    private String id;

    @JsonProperty("enterprise_id")
    private String enterpriseId;

    @JsonProperty("reporting_period")
    @NotBlank(message = "报送期次不能为空")
    private String reportingPeriod;

    @JsonProperty("initial_employees")
    @PositiveOrZero(message = "建档期人数不能为负数")
    private Integer initialEmployees;

    @JsonProperty("current_employees")
    @PositiveOrZero(message = "调查期人数不能为负数")
    private Integer currentEmployees;

    @JsonProperty("reduction_type_code")
    private String reductionTypeCode;

    @JsonProperty("reduction_type_desc")
    @Size(max = 200, message = "其他类型说明长度不能超过200字")
    private String reductionTypeDesc;

    @JsonProperty("primary_reason_code")
    private String primaryReasonCode;

    @JsonProperty("secondary_reason_code")
    private String secondaryReasonCode;

    @JsonProperty("tertiary_reason_code")
    private String tertiaryReasonCode;

    @JsonProperty("primary_reason_desc")
    @Size(max = 200, message = "主要原因说明长度不能超过200字")
    private String primaryReasonDesc;

    @JsonProperty("secondary_reason_desc")
    @Size(max = 200, message = "次要原因说明长度不能超过200字")
    private String secondaryReasonDesc;

    @JsonProperty("tertiary_reason_desc")
    @Size(max = 200, message = "第三原因说明长度不能超过200字")
    private String tertiaryReasonDesc;

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getEnterpriseId() {
        return enterpriseId;
    }
    public void setEnterpriseId(String enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public String getReportingPeriod() {
        return reportingPeriod;
    }
    public void setReportingPeriod(String reportingPeriod) {
        this.reportingPeriod = reportingPeriod;
    }

    public Integer getInitialEmployees() {
        return initialEmployees;
    }
    public void setInitialEmployees(Integer initialEmployees) {
        this.initialEmployees = initialEmployees;
    }

    public Integer getCurrentEmployees() {
        return currentEmployees;
    }
    public void setCurrentEmployees(Integer currentEmployees) {
        this.currentEmployees = currentEmployees;
    }

    public String getReductionTypeCode() {
        return reductionTypeCode;
    }
    public void setReductionTypeCode(String reductionTypeCode) {
        this.reductionTypeCode = reductionTypeCode;
    }

    public String getReductionTypeDesc() {
        return reductionTypeDesc;
    }
    public void setReductionTypeDesc(String reductionTypeDesc) {
        this.reductionTypeDesc = reductionTypeDesc;
    }

    public String getPrimaryReasonCode() {
        return primaryReasonCode;
    }
    public void setPrimaryReasonCode(String primaryReasonCode) {
        this.primaryReasonCode = primaryReasonCode;
    }

    public String getSecondaryReasonCode() {
        return secondaryReasonCode;
    }
    public void setSecondaryReasonCode(String secondaryReasonCode) {
        this.secondaryReasonCode = secondaryReasonCode;
    }

    public String getTertiaryReasonCode() {
        return tertiaryReasonCode;
    }
    public void setTertiaryReasonCode(String tertiaryReasonCode) {
        this.tertiaryReasonCode = tertiaryReasonCode;
    }

    public String getPrimaryReasonDesc() {
        return primaryReasonDesc;
    }
    public void setPrimaryReasonDesc(String primaryReasonDesc) {
        this.primaryReasonDesc = primaryReasonDesc;
    }

    public String getSecondaryReasonDesc() {
        return secondaryReasonDesc;
    }
    public void setSecondaryReasonDesc(String secondaryReasonDesc) {
        this.secondaryReasonDesc = secondaryReasonDesc;
    }

    public String getTertiaryReasonDesc() {
        return tertiaryReasonDesc;
    }
    public void setTertiaryReasonDesc(String tertiaryReasonDesc) {
        this.tertiaryReasonDesc = tertiaryReasonDesc;
    }
}
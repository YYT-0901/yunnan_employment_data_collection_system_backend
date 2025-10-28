// 文件路径：yunnan-enterprise/src/main/java/com/yunnanenterprise/dto/report/ReportSaveDTO.java
package com.yunnanenterprise.dto.report;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * 暂存报表 DTO
 * 
 * 设计思路：
 * 1. 暂存时数据可以不完整，所以大部分字段不加 @NotNull
 * 2. periodId 必填，因为要知道是哪个调查期的报表
 * 3. reportId 可选，如果是第一次暂存则为null，后端会创建
 */
public class ReportSaveDTO {
    
    /**
     * 调查期ID（必填）
     */
    @NotNull(message = "调查期ID不能为空")
    private Long periodId;
    
    /**
     * 报表ID（可选，第一次暂存时为null）
     */
    private String reportId;
    
    /**
     * 建档期就业人数
     */
    @Min(value = 0, message = "建档期就业人数不能为负数")
    private Integer constructionCount;
    
    /**
     * 调查期就业人数
     */
    @Min(value = 0, message = "调查期就业人数不能为负数")
    private Integer investigationCount;
    
    /**
     * 就业人数减少类型
     */
    private Integer reductionType;
    
    /**
     * 减少原因类型1
     */
    private Integer reason1;
    
    /**
     * 减少原因说明1
     */
    private String reason1Desc;
    
    /**
     * 减少原因类型2
     */
    private Integer reason2;
    
    /**
     * 减少原因说明2
     */
    private String reason2Desc;
    
    /**
     * 减少原因类型3
     */
    private Integer reason3;
    
    /**
     * 减少原因说明3
     */
    private String reason3Desc;
    
    /**
     * 其他原因
     */
    private String otherReason;
    
    // Getter and Setter
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
    
    public Integer getConstructionCount() {
        return constructionCount;
    }
    
    public void setConstructionCount(Integer constructionCount) {
        this.constructionCount = constructionCount;
    }
    
    public Integer getInvestigationCount() {
        return investigationCount;
    }
    
    public void setInvestigationCount(Integer investigationCount) {
        this.investigationCount = investigationCount;
    }
    
    public Integer getReductionType() {
        return reductionType;
    }
    
    public void setReductionType(Integer reductionType) {
        this.reductionType = reductionType;
    }
    
    public Integer getReason1() {
        return reason1;
    }
    
    public void setReason1(Integer reason1) {
        this.reason1 = reason1;
    }
    
    public String getReason1Desc() {
        return reason1Desc;
    }
    
    public void setReason1Desc(String reason1Desc) {
        this.reason1Desc = reason1Desc;
    }
    
    public Integer getReason2() {
        return reason2;
    }
    
    public void setReason2(Integer reason2) {
        this.reason2 = reason2;
    }
    
    public String getReason2Desc() {
        return reason2Desc;
    }
    
    public void setReason2Desc(String reason2Desc) {
        this.reason2Desc = reason2Desc;
    }
    
    public Integer getReason3() {
        return reason3;
    }
    
    public void setReason3(Integer reason3) {
        this.reason3 = reason3;
    }
    
    public String getReason3Desc() {
        return reason3Desc;
    }
    
    public void setReason3Desc(String reason3Desc) {
        this.reason3Desc = reason3Desc;
    }
    
    public String getOtherReason() {
        return otherReason;
    }
    
    public void setOtherReason(String otherReason) {
        this.otherReason = otherReason;
    }
}
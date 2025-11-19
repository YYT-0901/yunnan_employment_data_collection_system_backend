package com.yunnancommon.entity.query;

import java.util.Date;

/**
 * @Description:企业上报信息表查询对象
 * @auther:group2
 * @date:2025/10/22
 */
public class EnterpriseReportInfoQuery extends BaseQuery{
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
	 * 数据填报ID
	 */
	private String reportId;

	private String reportIdFuzzy;

	/**
	 * 旧数据填报ID
	 */
	private String oldReportId;

	private String oldReportIdFuzzy;

	/**
	 * 退回原因
	 */
	private String reasonReturn;

	private String reasonReturnFuzzy;

	/**
	 * 状态: -1-未填报 0-已暂存 1-待市级审核 2-待省级审核 3-审核通过 4-已归档 5-驳回
	 */
	private Integer status;

	/**
	 * 上报填报开始时间
	 */
	private Date periodStartTime;

	private String periodStartTimeStart;

	private String periodStartTimeEnd;

	/**
	 * 上报填报结束时间
	 */
	private Date periodEndTime;

	private String periodEndTimeStart;

	private String periodEndTimeEnd;

	/**
	 * 创建时间
	 */
	private Date createdAt;

	private String createdAtStart;

	private String createdAtEnd;

	/**
	 * 更新时间
	 */
	private Date updatedAt;

	private String updatedAtStart;

	private String updatedAtEnd;

	/**
	 * 企业所属性质
	 */
	private Integer enterpriseNature;

	/**
	 * 企业所属行业
	 */
	private Integer enterpriseIndustry;

	/**
	 * 企业所属地区
	 */
	private Integer enterpriseRegion;

	private String enterpriseNameFuzzy;

	// 添加investigateTime字段
	private String investigateTime;

	// 添加getter/setter方法
	public String getInvestigateTime() {
		return investigateTime;
	}

	public void setInvestigateTime(String investigateTime) {
		this.investigateTime = investigateTime;
	}

// ... 现有代码 ...

	public String getEnterpriseNameFuzzy() {
		return enterpriseNameFuzzy;
	}

	public void setEnterpriseNameFuzzy(String enterpriseNameFuzzy) {
		this.enterpriseNameFuzzy = enterpriseNameFuzzy;
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

	public String getOldReportId() {
		return oldReportId;
	}

	public void setOldReportId(String oldReportId) {
		this.oldReportId = oldReportId;
	}

	public String getReasonReturn() {
		return reasonReturn;
	}

	public void setReasonReturn(String reasonReturn) {
		this.reasonReturn = reasonReturn;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Date getPeriodStartTime() {
		return periodStartTime;
	}

	public void setPeriodStartTime(Date periodStartTime) {
		this.periodStartTime = periodStartTime;
	}

	public Date getPeriodEndTime() {
		return periodEndTime;
	}

	public void setPeriodEndTime(Date periodEndTime) {
		this.periodEndTime = periodEndTime;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}

	public Integer getEnterpriseNature() {
		return enterpriseNature;
	}

	public void setEnterpriseNature(Integer enterpriseNature) {
		this.enterpriseNature = enterpriseNature;
	}

	public Integer getEnterpriseIndustry() {
		return enterpriseIndustry;
	}

	public void setEnterpriseIndustry(Integer enterpriseIndustry) {
		this.enterpriseIndustry = enterpriseIndustry;
	}

	public Integer getEnterpriseRegion() {
		return enterpriseRegion;
	}

	public void setEnterpriseRegion(Integer enterpriseRegion) {
		this.enterpriseRegion = enterpriseRegion;
	}

	public String getEnterpriseIdFuzzy() {
		return enterpriseIdFuzzy;
	}

	public void setEnterpriseIdFuzzy(String enterpriseIdFuzzy) {
		this.enterpriseIdFuzzy = enterpriseIdFuzzy;
	}

	public String getReportIdFuzzy() {
		return reportIdFuzzy;
	}

	public void setReportIdFuzzy(String reportIdFuzzy) {
		this.reportIdFuzzy = reportIdFuzzy;
	}

	public String getOldReportIdFuzzy() {
		return oldReportIdFuzzy;
	}

	public void setOldReportIdFuzzy(String oldReportIdFuzzy) {
		this.oldReportIdFuzzy = oldReportIdFuzzy;
	}

	public String getReasonReturnFuzzy() {
		return reasonReturnFuzzy;
	}

	public void setReasonReturnFuzzy(String reasonReturnFuzzy) {
		this.reasonReturnFuzzy = reasonReturnFuzzy;
	}

	public String getPeriodStartTimeStart() {
		return periodStartTimeStart;
	}

	public void setPeriodStartTimeStart(String periodStartTimeStart) {
		this.periodStartTimeStart = periodStartTimeStart;
	}

	public String getPeriodStartTimeEnd() {
		return periodStartTimeEnd;
	}

	public void setPeriodStartTimeEnd(String periodStartTimeEnd) {
		this.periodStartTimeEnd = periodStartTimeEnd;
	}

	public String getPeriodEndTimeStart() {
		return periodEndTimeStart;
	}

	public void setPeriodEndTimeStart(String periodEndTimeStart) {
		this.periodEndTimeStart = periodEndTimeStart;
	}

	public String getPeriodEndTimeEnd() {
		return periodEndTimeEnd;
	}

	public void setPeriodEndTimeEnd(String periodEndTimeEnd) {
		this.periodEndTimeEnd = periodEndTimeEnd;
	}

	public String getCreatedAtStart() {
		return createdAtStart;
	}

	public void setCreatedAtStart(String createdAtStart) {
		this.createdAtStart = createdAtStart;
	}

	public String getCreatedAtEnd() {
		return createdAtEnd;
	}

	public void setCreatedAtEnd(String createdAtEnd) {
		this.createdAtEnd = createdAtEnd;
	}

	public String getUpdatedAtStart() {
		return updatedAtStart;
	}

	public void setUpdatedAtStart(String updatedAtStart) {
		this.updatedAtStart = updatedAtStart;
	}

	public String getUpdatedAtEnd() {
		return updatedAtEnd;
	}

	public void setUpdatedAtEnd(String updatedAtEnd) {
		this.updatedAtEnd = updatedAtEnd;
	}

}
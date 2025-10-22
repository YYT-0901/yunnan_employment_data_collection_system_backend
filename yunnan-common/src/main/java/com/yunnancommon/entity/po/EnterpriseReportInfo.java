package com.yunnancommon.entity.po;

import java.io.Serializable;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import com.yunnancommon.enums.DateTimePatternEnum;
import com.yunnancommon.utils.DateUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @Description:企业上报信息表
 * @auther:group2
 * @date:2025/10/22
 */
public class EnterpriseReportInfo implements Serializable {
	/**
	 * 企业ID
	 */
	private String enterpriseId;

	/**
	 * 调查期ID
	 */
	private Integer periodId;

	/**
	 * 数据填报ID
	 */
	private String reportId;

	/**
	 * 旧数据填报ID
	 */
	private String oldReportId;

	/**
	 * 退回原因
	 */
	private String reasonReturn;

	/**
	 * 状态: -1-未填报 0-已暂存 1-待市级审核 2-待省级审核 3-审核通过 4-已归档 5-驳回
	 */
	@JsonIgnore
	private Integer status;

	/**
	 * 上报填报开始时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date periodStartTime;

	/**
	 * 上报填报结束时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date periodEndTime;

	/**
	 * 创建时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date createdAt;

	/**
	 * 更新时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date updatedAt;

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

	public String getEnterpriseId() {
		return enterpriseId;
	}

	public void setEnterpriseId(String enterpriseId) {
		this.enterpriseId = enterpriseId;
	}

	public Integer getPeriodId() {
		return periodId;
	}

	public void setPeriodId(Integer periodId) {
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

	@Override
	public String toString() {
		return "企业ID:" + (enterpriseId == null ? "空" : enterpriseId) + ",调查期ID:" + (periodId == null ? "空" : periodId) + ",数据填报ID:" + (reportId == null ? "空" : reportId) + ",旧数据填报ID:" + (oldReportId == null ? "空" : oldReportId) + ",退回原因:" + (reasonReturn == null ? "空" : reasonReturn) + ",状态: -1-未填报 0-已暂存 1-待市级审核 2-待省级审核 3-审核通过 4-已归档 5-驳回:" + (status == null ? "空" : status) + ",上报填报开始时间:" + (periodStartTime == null ? "空" : DateUtils.format(periodStartTime, DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern())) + ",上报填报结束时间:" + (periodEndTime == null ? "空" : DateUtils.format(periodEndTime, DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern())) + ",创建时间:" + (createdAt == null ? "空" : DateUtils.format(createdAt, DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern())) + ",更新时间:" + (updatedAt == null ? "空" : DateUtils.format(updatedAt, DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern())) + ",企业所属性质:" + (enterpriseNature == null ? "空" : enterpriseNature) + ",企业所属行业:" + (enterpriseIndustry == null ? "空" : enterpriseIndustry) + ",企业所属地区:" + (enterpriseRegion == null ? "空" : enterpriseRegion);
	}
}
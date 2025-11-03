package com.yunnancommon.entity.po;

import java.io.Serializable;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import com.yunnancommon.enums.DateTimePatternEnum;
import com.yunnancommon.utils.DateUtils;

/**
 * @Description:调查期信息表
 * @auther:SOON JIANG BING
 * @date:2025/11/02
 */
public class PeriodInfo implements Serializable {
	/**
	 * 调查期ID(自增主键)
	 */
	private Long periodId;

	/**
	 * 调查期标识(格式: YYYY-MM, 如2025-01)
	 */
	private String investigateTime;

	/**
	 * 填报开始时间(T0)
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date periodStartTime;

	/**
	 * 填报结束时间(T1)
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date periodEndTime;

	/**
	 * 此调查期之前的企业总数
	 */
	private Integer enterpriseCount;

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

	public Long getPeriodId() {
		return periodId;
	}

	public void setPeriodId(Long periodId) {
		this.periodId = periodId;
	}

	public String getInvestigateTime() {
		return investigateTime;
	}

	public void setInvestigateTime(String investigateTime) {
		this.investigateTime = investigateTime;
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

	public Integer getEnterpriseCount() {
		return enterpriseCount;
	}

	public void setEnterpriseCount(Integer enterpriseCount) {
		this.enterpriseCount = enterpriseCount;
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

	@Override
	public String toString() {
		return "调查期ID(自增主键):" + (periodId == null ? "空" : periodId) + ",调查期标识(格式: YYYY-MM, 如2025-01):" + (investigateTime == null ? "空" : investigateTime) + ",填报开始时间(T0):" + (periodStartTime == null ? "空" : DateUtils.format(periodStartTime, DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern())) + ",填报结束时间(T1):" + (periodEndTime == null ? "空" : DateUtils.format(periodEndTime, DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern())) + ",此调查期之前的企业总数:" + (enterpriseCount == null ? "空" : enterpriseCount) + ",创建时间:" + (createdAt == null ? "空" : DateUtils.format(createdAt, DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern())) + ",更新时间:" + (updatedAt == null ? "空" : DateUtils.format(updatedAt, DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern()));
	}
}
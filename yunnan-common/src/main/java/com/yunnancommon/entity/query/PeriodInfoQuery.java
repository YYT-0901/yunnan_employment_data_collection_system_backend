package com.yunnancommon.entity.query;

import java.util.Date;

/**
 * @Description:调查期信息表查询对象
 * @auther:SOON JIANG BING
 * @date:2025/11/02
 */
public class PeriodInfoQuery extends BaseQuery{
	/**
	 * 调查期ID(自增主键)
	 */
	private Long periodId;

	/**
	 * 调查期标识(格式: YYYY-MM, 如2025-01)
	 */
	private String investigateTime;

	private String investigateTimeFuzzy;

	/**
	 * 填报开始时间(T0)
	 */
	private Date periodStartTime;

	private String periodStartTimeStart;

	private String periodStartTimeEnd;

	/**
	 * 填报结束时间(T1)
	 */
	private Date periodEndTime;

	private String periodEndTimeStart;

	private String periodEndTimeEnd;

	/**
	 * 此调查期之前的企业总数
	 */
	private Integer enterpriseCount;

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

	public String getInvestigateTimeFuzzy() {
		return investigateTimeFuzzy;
	}

	public void setInvestigateTimeFuzzy(String investigateTimeFuzzy) {
		this.investigateTimeFuzzy = investigateTimeFuzzy;
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
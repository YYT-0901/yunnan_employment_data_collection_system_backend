package com.yunnancommon.entity.po;

import java.io.Serializable;

/**
 * @Description:调查期信息表
 * @auther:group2
 * @date:2025/10/22
 */
public class PeriodInfo implements Serializable {
	/**
	 * 调查期ID(月)(时间戳)
	 */
	private Integer periodId;

	/**
	 * 此调查期之前的企业总数
	 */
	private Integer enterpriseCount;

	public Integer getPeriodId() {
		return periodId;
	}

	public void setPeriodId(Integer periodId) {
		this.periodId = periodId;
	}

	public Integer getEnterpriseCount() {
		return enterpriseCount;
	}

	public void setEnterpriseCount(Integer enterpriseCount) {
		this.enterpriseCount = enterpriseCount;
	}

	@Override
	public String toString() {
		return "调查期ID(月)(时间戳):" + (periodId == null ? "空" : periodId) + ",此调查期之前的企业总数:" + (enterpriseCount == null ? "空" : enterpriseCount);
	}
}
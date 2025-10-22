package com.yunnancommon.entity.query;


/**
 * @Description:调查期信息表查询对象
 * @auther:group2
 * @date:2025/10/22
 */
public class PeriodInfoQuery extends BaseQuery{
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

}
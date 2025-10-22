package com.yunnancommon.entity.po;

import java.io.Serializable;

/**
 * @Description:数据填报信息表
 * @auther:group2
 * @date:2025/10/22
 */
public class ReportInfo implements Serializable {
	/**
	 * 数据填报ID
	 */
	private String reportId;

	/**
	 * 建档期就业人数
	 */
	private Integer constructionCount;

	/**
	 * 调查期就业人数
	 */
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

	@Override
	public String toString() {
		return "数据填报ID:" + (reportId == null ? "空" : reportId) + ",建档期就业人数:" + (constructionCount == null ? "空" : constructionCount) + ",调查期就业人数:" + (investigationCount == null ? "空" : investigationCount) + ",就业人数减少类型:" + (reductionType == null ? "空" : reductionType) + ",减少原因类型1:" + (reason1 == null ? "空" : reason1) + ",减少原因说明1:" + (reason1Desc == null ? "空" : reason1Desc) + ",减少原因类型2:" + (reason2 == null ? "空" : reason2) + ",减少原因说明2:" + (reason2Desc == null ? "空" : reason2Desc) + ",减少原因类型3:" + (reason3 == null ? "空" : reason3) + ",减少原因说明3:" + (reason3Desc == null ? "空" : reason3Desc) + ",其他原因:" + (otherReason == null ? "空" : otherReason);
	}
}
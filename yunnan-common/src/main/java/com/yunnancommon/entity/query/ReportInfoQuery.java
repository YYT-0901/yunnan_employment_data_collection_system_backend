package com.yunnancommon.entity.query;


/**
 * @Description:数据填报信息表查询对象
 * @auther:group2
 * @date:2025/10/22
 */
public class ReportInfoQuery extends BaseQuery{
	/**
	 * 数据填报ID
	 */
	private String reportId;

	private String reportIdFuzzy;

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

	private String reason1DescFuzzy;

	/**
	 * 减少原因类型2
	 */
	private Integer reason2;

	/**
	 * 减少原因说明2
	 */
	private String reason2Desc;

	private String reason2DescFuzzy;

	/**
	 * 减少原因类型3
	 */
	private Integer reason3;

	/**
	 * 减少原因说明3
	 */
	private String reason3Desc;

	private String reason3DescFuzzy;

	/**
	 * 其他原因
	 */
	private String otherReason;

	private String otherReasonFuzzy;

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

	public String getReportIdFuzzy() {
		return reportIdFuzzy;
	}

	public void setReportIdFuzzy(String reportIdFuzzy) {
		this.reportIdFuzzy = reportIdFuzzy;
	}

	public String getReason1DescFuzzy() {
		return reason1DescFuzzy;
	}

	public void setReason1DescFuzzy(String reason1DescFuzzy) {
		this.reason1DescFuzzy = reason1DescFuzzy;
	}

	public String getReason2DescFuzzy() {
		return reason2DescFuzzy;
	}

	public void setReason2DescFuzzy(String reason2DescFuzzy) {
		this.reason2DescFuzzy = reason2DescFuzzy;
	}

	public String getReason3DescFuzzy() {
		return reason3DescFuzzy;
	}

	public void setReason3DescFuzzy(String reason3DescFuzzy) {
		this.reason3DescFuzzy = reason3DescFuzzy;
	}

	public String getOtherReasonFuzzy() {
		return otherReasonFuzzy;
	}

	public void setOtherReasonFuzzy(String otherReasonFuzzy) {
		this.otherReasonFuzzy = otherReasonFuzzy;
	}

}
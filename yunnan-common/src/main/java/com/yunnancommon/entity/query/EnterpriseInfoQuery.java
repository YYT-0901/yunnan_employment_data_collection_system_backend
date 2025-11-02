package com.yunnancommon.entity.query;

import java.util.Date;

/**
 * @Description:企业信息表查询对象
 * @auther:group2
 * @date:2025/10/22
 */
public class EnterpriseInfoQuery extends BaseQuery{
	/**
	 * 企业ID
	 */
	private String enterpriseId;

	private String enterpriseIdFuzzy;

	/**
	 * 组织机构代码
	 */
	private String orgCode;

	private String orgCodeFuzzy;

	/**
	 * 企业名称
	 */
	private String name;

	private String nameFuzzy;

	/**
	 * 所属地区
	 */
	private Integer region;

	/**
	 * 所属性质
	 */
	private Integer nature;

	/**
	 * 所属行业
	 */
	private Integer industry;

	/**
	 * 主要经营业务详情
	 */
	private String industryDesc;

	private String industryDescFuzzy;

	/**
	 * 退回原因
	 */
	private String reasonReturn;

	private String reasonReturnFuzzy;

	/**
	 * 联系人名称
	 */
	private String contactName;

	private String contactNameFuzzy;

	/**
	 * 联系人地址
	 */
	private String address;

	private String addressFuzzy;

	/**
	 * 邮政编码
	 */
	private String postalCode;

	private String postalCodeFuzzy;

	/**
	 * 联系电话
	 */
	private String phoneNum;

	private String phoneNumFuzzy;

	/**
	 * 传真号
	 */
	private String faxNum;

	private String faxNumFuzzy;

	/**
	 * 邮箱
	 */
	private String email;

	private String emailFuzzy;

	/**
	 * 状态: 0-创建未备案 1-已备案未审核 2-已退回 3-正常(已备案已审核) 4-倒闭
	 */
	private Integer status;

	/**
	 * 企业创建时间
	 */
	private Date createdAt;

	private String createdAtStart;

	private String createdAtEnd;

	/**
	 * 企业信息修改时间
	 */
	private Date updatedAt;

	private String updatedAtStart;

	private String updatedAtEnd;

	public String getEnterpriseId() {
		return enterpriseId;
	}

	public void setEnterpriseId(String enterpriseId) {
		this.enterpriseId = enterpriseId;
	}

	public String getOrgCode() {
		return orgCode;
	}

	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getRegion() {
		return region;
	}

	public void setRegion(Integer region) {
		this.region = region;
	}

	public Integer getNature() {
		return nature;
	}

	public void setNature(Integer nature) {
		this.nature = nature;
	}

	public Integer getIndustry() {
		return industry;
	}

	public void setIndustry(Integer industry) {
		this.industry = industry;
	}

	public String getIndustryDesc() {
		return industryDesc;
	}

	public void setIndustryDesc(String industryDesc) {
		this.industryDesc = industryDesc;
	}
	public String getReasonReturn() {
		return reasonReturn;
	}

	public void setReasonReturn(String reasonReturn) {
		this.reasonReturn = reasonReturn;
	}

	public String getContactName() {
		return contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getPhoneNum() {
		return phoneNum;
	}

	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}

	public String getFaxNum() {
		return faxNum;
	}

	public void setFaxNum(String faxNum) {
		this.faxNum = faxNum;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
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

	public String getEnterpriseIdFuzzy() {
		return enterpriseIdFuzzy;
	}

	public void setEnterpriseIdFuzzy(String enterpriseIdFuzzy) {
		this.enterpriseIdFuzzy = enterpriseIdFuzzy;
	}

	public String getOrgCodeFuzzy() {
		return orgCodeFuzzy;
	}

	public void setOrgCodeFuzzy(String orgCodeFuzzy) {
		this.orgCodeFuzzy = orgCodeFuzzy;
	}

	public String getNameFuzzy() {
		return nameFuzzy;
	}

	public void setNameFuzzy(String nameFuzzy) {
		this.nameFuzzy = nameFuzzy;
	}

	public String getIndustryDescFuzzy() {
		return industryDescFuzzy;
	}

	public void setIndustryDescFuzzy(String industryDescFuzzy) {
		this.industryDescFuzzy = industryDescFuzzy;
	}

	public String getReasonReturnFuzzy() {
		return reasonReturnFuzzy;
	}

	public void setReasonReturnFuzzy(String reasonReturnFuzzy) {
		this.reasonReturnFuzzy = reasonReturnFuzzy;
	}

	public String getContactNameFuzzy() {
		return contactNameFuzzy;
	}

	public void setContactNameFuzzy(String contactNameFuzzy) {
		this.contactNameFuzzy = contactNameFuzzy;
	}

	public String getAddressFuzzy() {
		return addressFuzzy;
	}

	public void setAddressFuzzy(String addressFuzzy) {
		this.addressFuzzy = addressFuzzy;
	}

	public String getPostalCodeFuzzy() {
		return postalCodeFuzzy;
	}

	public void setPostalCodeFuzzy(String postalCodeFuzzy) {
		this.postalCodeFuzzy = postalCodeFuzzy;
	}

	public String getPhoneNumFuzzy() {
		return phoneNumFuzzy;
	}

	public void setPhoneNumFuzzy(String phoneNumFuzzy) {
		this.phoneNumFuzzy = phoneNumFuzzy;
	}

	public String getFaxNumFuzzy() {
		return faxNumFuzzy;
	}

	public void setFaxNumFuzzy(String faxNumFuzzy) {
		this.faxNumFuzzy = faxNumFuzzy;
	}

	public String getEmailFuzzy() {
		return emailFuzzy;
	}

	public void setEmailFuzzy(String emailFuzzy) {
		this.emailFuzzy = emailFuzzy;
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
package com.yunnancommon.entity.po;

import java.io.Serializable;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import com.yunnancommon.enums.DateTimePatternEnum;
import com.yunnancommon.utils.DateUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @Description:企业信息表
 * @auther:group2
 * @date:2025/11/04
 */
public class EnterpriseInfo implements Serializable {
	/**
	 * 
	 */
	private String enterpriseId;

	/**
	 * 组织机构代码
	 */
	private String orgCode;

	/**
	 * 企业名称
	 */
	private String name;

	/**
	 * 所属地区
	 */
	private Integer region;

	/**
	 * 所属地区一级分类
	 */
	private Integer regionCode;

	/**
	 * 所属性质
	 */
	private Integer nature;

	/**
	 * 所属性质一级分类
	 */
	private Integer natureCode;

	/**
	 * 所属行业
	 */
	private Integer industry;

	/**
	 * 所属行业一级分类
	 */
	private Integer industryCode;

	/**
	 * 主要经营业务详情
	 */
	private String industryDesc;

	/**
	 * 企业备案退回原因
	 */
	private String reasonReturn;

	/**
	 * 联系人名称
	 */
	private String contactName;

	/**
	 * 联系人地址
	 */
	private String address;

	/**
	 * 邮政编码
	 */
	private String postalCode;

	/**
	 * 联系电话
	 */
	private String phoneNum;

	/**
	 * 传真号
	 */
	private String faxNum;

	/**
	 * 邮箱
	 */
	private String email;

	/**
	 * 状态: 0-创建未备案 1-已备案未审核 2-已退回 3-正常(已备案已审核) 4-倒闭
	 */
	@JsonIgnore
	private Integer status;

	/**
	 * 企业创建时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date createdAt;

	/**
	 * 企业信息修改时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date updatedAt;

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

	public Integer getRegionCode() {
		return regionCode;
	}

	public void setRegionCode(Integer regionCode) {
		this.regionCode = regionCode;
	}

	public Integer getNature() {
		return nature;
	}

	public void setNature(Integer nature) {
		this.nature = nature;
	}

	public Integer getNatureCode() {
		return natureCode;
	}

	public void setNatureCode(Integer natureCode) {
		this.natureCode = natureCode;
	}

	public Integer getIndustry() {
		return industry;
	}

	public void setIndustry(Integer industry) {
		this.industry = industry;
	}

	public Integer getIndustryCode() {
		return industryCode;
	}

	public void setIndustryCode(Integer industryCode) {
		this.industryCode = industryCode;
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

	@Override
	public String toString() {
		return ":" + (enterpriseId == null ? "空" : enterpriseId) + ",组织机构代码:" + (orgCode == null ? "空" : orgCode) + ",企业名称:" + (name == null ? "空" : name) + ",所属地区:" + (region == null ? "空" : region) + ",所属地区一级分类:" + (regionCode == null ? "空" : regionCode) + ",所属性质:" + (nature == null ? "空" : nature) + ",所属性质一级分类:" + (natureCode == null ? "空" : natureCode) + ",所属行业:" + (industry == null ? "空" : industry) + ",所属行业一级分类:" + (industryCode == null ? "空" : industryCode) + ",主要经营业务详情:" + (industryDesc == null ? "空" : industryDesc) + ",企业备案退回原因:" + (reasonReturn == null ? "空" : reasonReturn) + ",联系人名称:" + (contactName == null ? "空" : contactName) + ",联系人地址:" + (address == null ? "空" : address) + ",邮政编码:" + (postalCode == null ? "空" : postalCode) + ",联系电话:" + (phoneNum == null ? "空" : phoneNum) + ",传真号:" + (faxNum == null ? "空" : faxNum) + ",邮箱:" + (email == null ? "空" : email) + ",状态: 0-创建未备案 1-已备案未审核 2-已退回 3-正常(已备案已审核) 4-倒闭:" + (status == null ? "空" : status) + ",企业创建时间:" + (createdAt == null ? "空" : DateUtils.format(createdAt, DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern())) + ",企业信息修改时间:" + (updatedAt == null ? "空" : DateUtils.format(updatedAt, DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern()));
	}
}
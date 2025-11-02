package com.yunnancommon.entity.query;

import java.util.Date;

/**
 * @Description:账号信息表查询对象
 * @auther:group2
 * @date:2025/10/22
 */
public class AccountInfoQuery extends BaseQuery{
	/**
	 * 用户名
	 */
	private String username;

	private String usernameFuzzy;

	/**
	 * 密码
	 */
	private String password;

	private String passwordFuzzy;

	/**
	 * 账号类型: 1-企业账号 2-市账号
	 */
	private Integer type;

	/**
	 * 企业ID
	 */
	private String enterpriseId;

	private String enterpriseIdFuzzy;

	/**
	 * 市编码
	 */
	private Integer cityCode;

	/**
	 * 最后登陆时间
	 */
	private Date lastLoginTime;

	private String lastLoginTimeStart;

	private String lastLoginTimeEnd;

	/**
	 * 状态: 0-正常 1-停用
	 */
	private Integer status;

	/**
	 * 账号创建时间
	 */
	private Date createdAt;

	private String createdAtStart;

	private String createdAtEnd;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getEnterpriseId() {
		return enterpriseId;
	}

	public void setEnterpriseId(String enterpriseId) {
		this.enterpriseId = enterpriseId;
	}

	public Integer getCityCode() {
		return cityCode;
	}

	public void setCityCode(Integer cityCode) {
		this.cityCode = cityCode;
	}

	public Date getLastLoginTime() {
		return lastLoginTime;
	}

	public void setLastLoginTime(Date lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
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

	public String getUsernameFuzzy() {
		return usernameFuzzy;
	}

	public void setUsernameFuzzy(String usernameFuzzy) {
		this.usernameFuzzy = usernameFuzzy;
	}

	public String getPasswordFuzzy() {
		return passwordFuzzy;
	}

	public void setPasswordFuzzy(String passwordFuzzy) {
		this.passwordFuzzy = passwordFuzzy;
	}

	public String getEnterpriseIdFuzzy() {
		return enterpriseIdFuzzy;
	}

	public void setEnterpriseIdFuzzy(String enterpriseIdFuzzy) {
		this.enterpriseIdFuzzy = enterpriseIdFuzzy;
	}

	public String getLastLoginTimeStart() {
		return lastLoginTimeStart;
	}

	public void setLastLoginTimeStart(String lastLoginTimeStart) {
		this.lastLoginTimeStart = lastLoginTimeStart;
	}

	public String getLastLoginTimeEnd() {
		return lastLoginTimeEnd;
	}

	public void setLastLoginTimeEnd(String lastLoginTimeEnd) {
		this.lastLoginTimeEnd = lastLoginTimeEnd;
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

}
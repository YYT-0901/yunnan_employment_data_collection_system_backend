package com.yunnancommon.entity.po;

import java.io.Serializable;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import com.yunnancommon.enums.DateTimePatternEnum;
import com.yunnancommon.utils.DateUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @Description:账号信息表
 * @auther:group2
 * @date:2025/10/22
 */
public class AccountInfo implements Serializable {
	/**
	 * 用户名
	 */
	private String username;

	/**
	 * 密码
	 */
	private String password;

	/**
	 * 账号类型: 1-企业账号 2-市账号
	 */
	private Integer type;

	/**
	 * 企业ID
	 */
	private String enterpriseId;

	/**
	 * 市编码
	 */
	private Integer cityCode;

	/**
	 * 最后登陆时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date lastLoginTime;

	/**
	 * 状态: 0-正常 1-停用
	 */
	private Integer status;

	/**
	 * 账号创建时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date createdAt;

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

	@Override
	public String toString() {
		return "用户名:" + (username == null ? "空" : username) + ",密码:" + (password == null ? "空" : password) + ",账号类型: 1-企业账号 2-市账号:" + (type == null ? "空" : type) + ",企业ID:" + (enterpriseId == null ? "空" : enterpriseId) + ",市编码:" + (cityCode == null ? "空" : cityCode) + ",最后登陆时间:" + (lastLoginTime == null ? "空" : DateUtils.format(lastLoginTime, DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern())) + ",状态: 0-正常 1-停用:" + (status == null ? "空" : status) + ",账号创建时间:" + (createdAt == null ? "空" : DateUtils.format(createdAt, DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern()));
	}
}
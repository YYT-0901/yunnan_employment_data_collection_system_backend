package com.yunnancommon.entity.query;

import java.util.Date;

/**
 * @Description:通知阅读记录表查询对象
 * @auther:group2
 * @date:2025/10/22
 */
public class NoticeReadInfoQuery extends BaseQuery{
	/**
	 * 记录ID
	 */
	private Long id;

	/**
	 * 通知ID
	 */
	private Long noticeId;

	/**
	 * 阅读用户
	 */
	private String username;

	private String usernameFuzzy;

	/**
	 * 阅读时间
	 */
	private Date readTime;

	private String readTimeStart;

	private String readTimeEnd;

	/**
	 * 企业ID
	 */
	private String enterpriseId;

	private String enterpriseIdFuzzy;

	/**
	 * 用户类型
	 */
	private Integer userType;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getNoticeId() {
		return noticeId;
	}

	public void setNoticeId(Long noticeId) {
		this.noticeId = noticeId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Date getReadTime() {
		return readTime;
	}

	public void setReadTime(Date readTime) {
		this.readTime = readTime;
	}

	public String getEnterpriseId() {
		return enterpriseId;
	}

	public void setEnterpriseId(String enterpriseId) {
		this.enterpriseId = enterpriseId;
	}

	public Integer getUserType() {
		return userType;
	}

	public void setUserType(Integer userType) {
		this.userType = userType;
	}

	public String getUsernameFuzzy() {
		return usernameFuzzy;
	}

	public void setUsernameFuzzy(String usernameFuzzy) {
		this.usernameFuzzy = usernameFuzzy;
	}

	public String getReadTimeStart() {
		return readTimeStart;
	}

	public void setReadTimeStart(String readTimeStart) {
		this.readTimeStart = readTimeStart;
	}

	public String getReadTimeEnd() {
		return readTimeEnd;
	}

	public void setReadTimeEnd(String readTimeEnd) {
		this.readTimeEnd = readTimeEnd;
	}

	public String getEnterpriseIdFuzzy() {
		return enterpriseIdFuzzy;
	}

	public void setEnterpriseIdFuzzy(String enterpriseIdFuzzy) {
		this.enterpriseIdFuzzy = enterpriseIdFuzzy;
	}

}
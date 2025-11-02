package com.yunnancommon.entity.po;

import java.io.Serializable;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import com.yunnancommon.enums.DateTimePatternEnum;
import com.yunnancommon.utils.DateUtils;

/**
 * @Description:通知阅读记录表
 * @auther:group2
 * @date:2025/10/22
 */
public class NoticeReadInfo implements Serializable {
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

	/**
	 * 阅读时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date readTime;

	/**
	 * 企业ID
	 */
	private String enterpriseId;

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

	@Override
	public String toString() {
		return "记录ID:" + (id == null ? "空" : id) + ",通知ID:" + (noticeId == null ? "空" : noticeId) + ",阅读用户:" + (username == null ? "空" : username) + ",阅读时间:" + (readTime == null ? "空" : DateUtils.format(readTime, DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern())) + ",企业ID:" + (enterpriseId == null ? "空" : enterpriseId) + ",用户类型:" + (userType == null ? "空" : userType);
	}
}
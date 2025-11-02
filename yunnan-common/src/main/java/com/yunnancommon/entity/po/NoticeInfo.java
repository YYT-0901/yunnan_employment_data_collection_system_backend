package com.yunnancommon.entity.po;

import java.io.Serializable;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import com.yunnancommon.enums.DateTimePatternEnum;
import com.yunnancommon.utils.DateUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @Description:通知消息表
 * @auther:group2
 * @date:2025/10/22
 */
public class NoticeInfo implements Serializable {
	/**
	 * 通知ID
	 */
	private Long noticeId;

	/**
	 * 通知标题
	 */
	private String title;

	/**
	 * 通知内容
	 */
	private String content;

	/**
	 * 附件路径
	 */
	private String attachment;

	/**
	 * 附件原名
	 */
	private String attachmentName;

	/**
	 * 是否重要消息: 0-普通 1-重要
	 */
	private Integer isImportant;

	/**
	 * 消息状态: 1-全部人可见 2-企业可见 3-市可见 4-省可见
	 */
	private Integer noticeStatus;

	/**
	 * 发布人
	 */
	private String publisher;

	/**
	 * 发布时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date publishTime;

	/**
	 * 生效开始时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date startTime;

	/**
	 * 生效结束时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date endTime;

	/**
	 * 阅读次数
	 */
	private Integer readCount;

	/**
	 * 状态: 0-删除 1-正常 2-草稿
	 */
	private Integer status;

	/**
	 * 创建时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date createdAt;

	/**
	 * 更新时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date updatedAt;

	public Long getNoticeId() {
		return noticeId;
	}

	public void setNoticeId(Long noticeId) {
		this.noticeId = noticeId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getAttachment() {
		return attachment;
	}

	public void setAttachment(String attachment) {
		this.attachment = attachment;
	}

	public String getAttachmentName() {
		return attachmentName;
	}

	public void setAttachmentName(String attachmentName) {
		this.attachmentName = attachmentName;
	}

	public Integer getIsImportant() {
		return isImportant;
	}

	public void setIsImportant(Integer isImportant) {
		this.isImportant = isImportant;
	}

	public Integer getNoticeStatus() {
		return noticeStatus;
	}

	public void setNoticeStatus(Integer noticeStatus) {
		this.noticeStatus = noticeStatus;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public Date getPublishTime() {
		return publishTime;
	}

	public void setPublishTime(Date publishTime) {
		this.publishTime = publishTime;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public Integer getReadCount() {
		return readCount;
	}

	public void setReadCount(Integer readCount) {
		this.readCount = readCount;
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
		return "通知ID:" + (noticeId == null ? "空" : noticeId) + ",通知标题:" + (title == null ? "空" : title) + ",通知内容:" + (content == null ? "空" : content) + ",附件路径:" + (attachment == null ? "空" : attachment) + ",附件原名:" + (attachmentName == null ? "空" : attachmentName) + ",是否重要消息: 0-普通 1-重要:" + (isImportant == null ? "空" : isImportant) + ",消息状态: 1-全部人可见 2-企业可见 3-市可见 4-省可见:" + (noticeStatus == null ? "空" : noticeStatus) + ",发布人:" + (publisher == null ? "空" : publisher) + ",发布时间:" + (publishTime == null ? "空" : DateUtils.format(publishTime, DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern())) + ",生效开始时间:" + (startTime == null ? "空" : DateUtils.format(startTime, DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern())) + ",生效结束时间:" + (endTime == null ? "空" : DateUtils.format(endTime, DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern())) + ",阅读次数:" + (readCount == null ? "空" : readCount) + ",状态: 0-删除 1-正常 2-草稿:" + (status == null ? "空" : status) + ",创建时间:" + (createdAt == null ? "空" : DateUtils.format(createdAt, DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern())) + ",更新时间:" + (updatedAt == null ? "空" : DateUtils.format(updatedAt, DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern()));
	}
}
package com.yunnancommon.entity.query;

import java.util.Date;

/**
 * @Description:通知消息表查询对象
 * @auther:group2
 * @date:2025/10/22
 */
public class NoticeInfoQuery extends BaseQuery{
	/**
	 * 通知ID
	 */
	private Long noticeId;

	/**
	 * 通知标题
	 */
	private String title;

	private String titleFuzzy;

	/**
	 * 通知内容
	 */
	private String content;

	private String contentFuzzy;

	/**
	 * 附件路径
	 */
	private String attachment;

	private String attachmentFuzzy;

	/**
	 * 附件原名
	 */
	private String attachmentName;

	private String attachmentNameFuzzy;

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

	private String publisherFuzzy;

	/**
	 * 发布时间
	 */
	private Date publishTime;

	private String publishTimeStart;

	private String publishTimeEnd;

	/**
	 * 生效开始时间
	 */
	private Date startTime;

	private String startTimeStart;

	private String startTimeEnd;

	/**
	 * 生效结束时间
	 */
	private Date endTime;

	private String endTimeStart;

	private String endTimeEnd;

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
	private Date createdAt;

	private String createdAtStart;

	private String createdAtEnd;

	/**
	 * 更新时间
	 */
	private Date updatedAt;

	private String updatedAtStart;

	private String updatedAtEnd;

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

	public String getTitleFuzzy() {
		return titleFuzzy;
	}

	public void setTitleFuzzy(String titleFuzzy) {
		this.titleFuzzy = titleFuzzy;
	}

	public String getContentFuzzy() {
		return contentFuzzy;
	}

	public void setContentFuzzy(String contentFuzzy) {
		this.contentFuzzy = contentFuzzy;
	}

	public String getAttachmentFuzzy() {
		return attachmentFuzzy;
	}

	public void setAttachmentFuzzy(String attachmentFuzzy) {
		this.attachmentFuzzy = attachmentFuzzy;
	}

	public String getAttachmentNameFuzzy() {
		return attachmentNameFuzzy;
	}

	public void setAttachmentNameFuzzy(String attachmentNameFuzzy) {
		this.attachmentNameFuzzy = attachmentNameFuzzy;
	}

	public String getPublisherFuzzy() {
		return publisherFuzzy;
	}

	public void setPublisherFuzzy(String publisherFuzzy) {
		this.publisherFuzzy = publisherFuzzy;
	}

	public String getPublishTimeStart() {
		return publishTimeStart;
	}

	public void setPublishTimeStart(String publishTimeStart) {
		this.publishTimeStart = publishTimeStart;
	}

	public String getPublishTimeEnd() {
		return publishTimeEnd;
	}

	public void setPublishTimeEnd(String publishTimeEnd) {
		this.publishTimeEnd = publishTimeEnd;
	}

	public String getStartTimeStart() {
		return startTimeStart;
	}

	public void setStartTimeStart(String startTimeStart) {
		this.startTimeStart = startTimeStart;
	}

	public String getStartTimeEnd() {
		return startTimeEnd;
	}

	public void setStartTimeEnd(String startTimeEnd) {
		this.startTimeEnd = startTimeEnd;
	}

	public String getEndTimeStart() {
		return endTimeStart;
	}

	public void setEndTimeStart(String endTimeStart) {
		this.endTimeStart = endTimeStart;
	}

	public String getEndTimeEnd() {
		return endTimeEnd;
	}

	public void setEndTimeEnd(String endTimeEnd) {
		this.endTimeEnd = endTimeEnd;
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
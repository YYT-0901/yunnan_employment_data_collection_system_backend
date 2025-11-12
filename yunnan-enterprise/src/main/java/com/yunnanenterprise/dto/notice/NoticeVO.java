package com.yunnanenterprise.dto.notice;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.util.Date;

/**
 * 通知信息 VO（View Object）
 * 
 * 这个对象专门用于返回给前端
 */
public class NoticeVO implements Serializable {
    
    /**
     * 通知 ID
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
     * 附件原始文件名
     */
    private String attachmentName;
    
    /**
     * 是否重要：0=普通，1=重要
     */
    private Integer isImportant;
    
    /**
     * 发布人
     */
    private String publisher;
    
    /**
     * 发布时间
     * @JsonFormat 注解用于格式化日期，返回给前端时自动转换为指定格式
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date publishTime;
    
    /**
     * 阅读次数
     */
    private Integer readCount;
    
    /**
     * 是否已读（这个字段需要根据当前用户计算）
     */
    private Boolean isRead;

    // Getter 和 Setter 方法
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

    public Integer getReadCount() {
        return readCount;
    }

    public void setReadCount(Integer readCount) {
        this.readCount = readCount;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }
}
package com.yunnancommon.entity.po;

import java.io.Serializable;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import com.yunnancommon.enums.DateTimePatternEnum;
import com.yunnancommon.utils.DateUtils;

/**
 * @Description:操作日志表
 * @auther:group2
 * @date:2025/10/22
 */
public class LogInfo implements Serializable {
	/**
	 * 日志ID
	 */
	private Long logId;

	/**
	 * 操作用户名
	 */
	private String username;

	/**
	 * 用户类型: 1-企业账号 2-市账号 3-省账号
	 */
	private Integer userType;

	/**
	 * 企业ID
	 */
	private String enterpriseId;

	/**
	 * 操作模块
	 */
	private String operationModule;

	/**
	 * 操作描述
	 */
	private String operationDesc;

	/**
	 * 请求URL
	 */
	private String requestUrl;

	/**
	 * 请求方法
	 */
	private String requestMethod;

	/**
	 * 请求参数
	 */
	private String requestParams;

	/**
	 * 响应状态
	 */
	private Integer responseStatus;

	/**
	 * 执行时间(毫秒)
	 */
	private Long executionTime;

	/**
	 * 错误信息
	 */
	private String errorMessage;

	/**
	 * 操作时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date operationTime;

	public Long getLogId() {
		return logId;
	}

	public void setLogId(Long logId) {
		this.logId = logId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Integer getUserType() {
		return userType;
	}

	public void setUserType(Integer userType) {
		this.userType = userType;
	}

	public String getEnterpriseId() {
		return enterpriseId;
	}

	public void setEnterpriseId(String enterpriseId) {
		this.enterpriseId = enterpriseId;
	}

	public String getOperationModule() {
		return operationModule;
	}

	public void setOperationModule(String operationModule) {
		this.operationModule = operationModule;
	}

	public String getOperationDesc() {
		return operationDesc;
	}

	public void setOperationDesc(String operationDesc) {
		this.operationDesc = operationDesc;
	}

	public String getRequestUrl() {
		return requestUrl;
	}

	public void setRequestUrl(String requestUrl) {
		this.requestUrl = requestUrl;
	}

	public String getRequestMethod() {
		return requestMethod;
	}

	public void setRequestMethod(String requestMethod) {
		this.requestMethod = requestMethod;
	}

	public String getRequestParams() {
		return requestParams;
	}

	public void setRequestParams(String requestParams) {
		this.requestParams = requestParams;
	}

	public Integer getResponseStatus() {
		return responseStatus;
	}

	public void setResponseStatus(Integer responseStatus) {
		this.responseStatus = responseStatus;
	}

	public Long getExecutionTime() {
		return executionTime;
	}

	public void setExecutionTime(Long executionTime) {
		this.executionTime = executionTime;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public Date getOperationTime() {
		return operationTime;
	}

	public void setOperationTime(Date operationTime) {
		this.operationTime = operationTime;
	}

	@Override
	public String toString() {
		return "日志ID:" + (logId == null ? "空" : logId) + ",操作用户名:" + (username == null ? "空" : username) + ",用户类型: 1-企业账号 2-市账号 3-省账号:" + (userType == null ? "空" : userType) + ",企业ID:" + (enterpriseId == null ? "空" : enterpriseId) + ",操作模块:" + (operationModule == null ? "空" : operationModule) + ",操作描述:" + (operationDesc == null ? "空" : operationDesc) + ",请求URL:" + (requestUrl == null ? "空" : requestUrl) + ",请求方法:" + (requestMethod == null ? "空" : requestMethod) + ",请求参数:" + (requestParams == null ? "空" : requestParams) + ",响应状态:" + (responseStatus == null ? "空" : responseStatus) + ",执行时间(毫秒):" + (executionTime == null ? "空" : executionTime) + ",错误信息:" + (errorMessage == null ? "空" : errorMessage) + ",操作时间:" + (operationTime == null ? "空" : DateUtils.format(operationTime, DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern()));
	}
}
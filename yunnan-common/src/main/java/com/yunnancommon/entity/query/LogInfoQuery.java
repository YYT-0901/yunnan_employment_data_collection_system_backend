package com.yunnancommon.entity.query;

import java.util.Date;

/**
 * @Description:操作日志表查询对象
 * @auther:group2
 * @date:2025/10/22
 */
public class LogInfoQuery extends BaseQuery{
	/**
	 * 日志ID
	 */
	private Long logId;

	/**
	 * 操作用户名
	 */
	private String username;

	private String usernameFuzzy;

	/**
	 * 用户类型: 1-企业账号 2-市账号 3-省账号
	 */
	private Integer userType;

	/**
	 * 企业ID
	 */
	private String enterpriseId;

	private String enterpriseIdFuzzy;

	/**
	 * 操作模块
	 */
	private String operationModule;

	private String operationModuleFuzzy;

	/**
	 * 操作描述
	 */
	private String operationDesc;

	private String operationDescFuzzy;

	/**
	 * 请求URL
	 */
	private String requestUrl;

	private String requestUrlFuzzy;

	/**
	 * 请求方法
	 */
	private String requestMethod;

	private String requestMethodFuzzy;

	/**
	 * 请求参数
	 */
	private String requestParams;

	private String requestParamsFuzzy;

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

	private String errorMessageFuzzy;

	/**
	 * 操作时间
	 */
	private Date operationTime;

	private String operationTimeStart;

	private String operationTimeEnd;

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

	public String getUsernameFuzzy() {
		return usernameFuzzy;
	}

	public void setUsernameFuzzy(String usernameFuzzy) {
		this.usernameFuzzy = usernameFuzzy;
	}

	public String getEnterpriseIdFuzzy() {
		return enterpriseIdFuzzy;
	}

	public void setEnterpriseIdFuzzy(String enterpriseIdFuzzy) {
		this.enterpriseIdFuzzy = enterpriseIdFuzzy;
	}

	public String getOperationModuleFuzzy() {
		return operationModuleFuzzy;
	}

	public void setOperationModuleFuzzy(String operationModuleFuzzy) {
		this.operationModuleFuzzy = operationModuleFuzzy;
	}

	public String getOperationDescFuzzy() {
		return operationDescFuzzy;
	}

	public void setOperationDescFuzzy(String operationDescFuzzy) {
		this.operationDescFuzzy = operationDescFuzzy;
	}

	public String getRequestUrlFuzzy() {
		return requestUrlFuzzy;
	}

	public void setRequestUrlFuzzy(String requestUrlFuzzy) {
		this.requestUrlFuzzy = requestUrlFuzzy;
	}

	public String getRequestMethodFuzzy() {
		return requestMethodFuzzy;
	}

	public void setRequestMethodFuzzy(String requestMethodFuzzy) {
		this.requestMethodFuzzy = requestMethodFuzzy;
	}

	public String getRequestParamsFuzzy() {
		return requestParamsFuzzy;
	}

	public void setRequestParamsFuzzy(String requestParamsFuzzy) {
		this.requestParamsFuzzy = requestParamsFuzzy;
	}

	public String getErrorMessageFuzzy() {
		return errorMessageFuzzy;
	}

	public void setErrorMessageFuzzy(String errorMessageFuzzy) {
		this.errorMessageFuzzy = errorMessageFuzzy;
	}

	public String getOperationTimeStart() {
		return operationTimeStart;
	}

	public void setOperationTimeStart(String operationTimeStart) {
		this.operationTimeStart = operationTimeStart;
	}

	public String getOperationTimeEnd() {
		return operationTimeEnd;
	}

	public void setOperationTimeEnd(String operationTimeEnd) {
		this.operationTimeEnd = operationTimeEnd;
	}

}
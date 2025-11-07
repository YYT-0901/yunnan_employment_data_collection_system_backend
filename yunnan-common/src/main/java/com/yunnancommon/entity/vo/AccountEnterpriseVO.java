package com.yunnancommon.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class AccountEnterpriseVO {
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
	 * 企业名称（别名，用于前端显示）
	 */
	private String enterpriseName;

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
	 * 所属性质
	 */
	private Integer nature;

	/**
	 * 所属行业
	 */
	private Integer industry;

	/**
	 * 主要经营业务详情
	 */
	private String industryDesc;

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
}

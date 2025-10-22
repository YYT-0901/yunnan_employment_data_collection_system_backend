package com.yunnancommon.service;


import com.yunnancommon.entity.vo.PaginationResultVO;
import com.yunnancommon.entity.po.AccountInfo;
import com.yunnancommon.entity.query.AccountInfoQuery;

import java.util.List;
/**
 * @Description:账号信息表Service
 * @auther:group2
 * @date:2025/10/22
 */
public interface AccountInfoService {

	/**
	 * 根据条件查询列表
	 */
	List<AccountInfo> findListByParam(AccountInfoQuery query);

	/**
	 * 根据条件查询数量
	 */
	Integer findCountByParam(AccountInfoQuery query);

	/**
	 * 分页查询
	 */
	PaginationResultVO<AccountInfo> findListByPage(AccountInfoQuery query);

	/**
	 * 新增
	 */
	Integer add(AccountInfo bean);

	/**
	 * 批量新增
	 */
	Integer addBatch(List<AccountInfo> listBean);

	/**
	 * 批量新增或修改
	 */
	Integer addOrUpdateBatch(List<AccountInfo> listBean);

	/**
	 * 根据Username查询
	 */
	AccountInfo getAccountInfoByUsername(String username);

	/**
	 * 根据Username更新
	 */
	Integer updateAccountInfoByUsername(AccountInfo bean, String username);

	/**
	 * 根据Username删除
	 */
	Integer deleteAccountInfoByUsername(String username);


}
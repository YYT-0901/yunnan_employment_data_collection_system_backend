package com.yunnancommon.service;


import com.yunnancommon.entity.vo.CreatedAccountVO;
import com.yunnancommon.entity.vo.PaginationResultVO;
import com.yunnancommon.entity.po.EnterpriseInfo;
import com.yunnancommon.entity.query.EnterpriseInfoQuery;

import java.util.List;
/**
 * @Description:企业信息表Service
 * @auther:group2
 * @date:2025/10/22
 */
public interface EnterpriseInfoService {

	/**
	 * 根据条件查询列表
	 */
	List<EnterpriseInfo> findListByParam(EnterpriseInfoQuery query);

	/**
	 * 根据条件查询数量
	 */
	Integer findCountByParam(EnterpriseInfoQuery query);

	/**
	 * 分页查询
	 */
	PaginationResultVO<EnterpriseInfo> findListByPage(EnterpriseInfoQuery query);

	/**
	 * 新增
	 */
	Integer add(EnterpriseInfo bean);

	/**
	 * 批量新增
	 */
	Integer addBatch(List<EnterpriseInfo> listBean);

	/**
	 * 批量新增或修改
	 */
	Integer addOrUpdateBatch(List<EnterpriseInfo> listBean);

	/**
	 * 根据EnterpriseId查询
	 */
	EnterpriseInfo getEnterpriseInfoByEnterpriseId(String enterpriseId);

	/**
	 * 根据EnterpriseId更新
	 */
	Integer updateEnterpriseInfoByEnterpriseId(EnterpriseInfo bean, String enterpriseId);

	/**
	 * 根据EnterpriseId删除
	 */
	Integer deleteEnterpriseInfoByEnterpriseId(String enterpriseId);


    CreatedAccountVO createEnterpriseAccount(EnterpriseInfo enterpriseInfo);

	CreatedAccountVO createCityAccount(Integer cityCode);
}
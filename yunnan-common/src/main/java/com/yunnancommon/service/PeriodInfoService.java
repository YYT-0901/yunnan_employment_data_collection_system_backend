package com.yunnancommon.service;


import com.yunnancommon.entity.vo.PaginationResultVO;
import com.yunnancommon.entity.po.PeriodInfo;
import com.yunnancommon.entity.query.PeriodInfoQuery;
import com.yunnancommon.exception.BusinessException;

import java.util.List;
/**
 * @Description:调查期信息表Service
 * @auther:SOON JIANG BING
 * @date:2025/11/02
 */
public interface PeriodInfoService {

	/**
	 * 根据条件查询列表
	 */
	List<PeriodInfo> findListByParam(PeriodInfoQuery query);

	/**
	 * 根据条件查询数量
	 */
	Integer findCountByParam(PeriodInfoQuery query);

	/**
	 * 分页查询
	 */
	PaginationResultVO<PeriodInfo> findListByPage(PeriodInfoQuery query);

	/**
	 * 新增
	 */
	Integer add(PeriodInfo bean);

	/**
	 * 批量新增
	 */
	Integer addBatch(List<PeriodInfo> listBean);

	/**
	 * 批量新增或修改
	 */
	Integer addOrUpdateBatch(List<PeriodInfo> listBean);

	/**
	 * 根据PeriodId查询
	 */
	PeriodInfo getPeriodInfoByPeriodId(Long periodId);

	/**
	 * 根据PeriodId更新
	 */
	Integer updatePeriodInfoByPeriodId(PeriodInfo bean, Long periodId);

	/**
	 * 根据PeriodId删除
	 */
	Integer deletePeriodInfoByPeriodId(Long periodId) throws BusinessException;

	/**
	 * 根据InvestigateTime查询
	 */
	PeriodInfo getPeriodInfoByInvestigateTime(String investigateTime);

	/**
	 * 根据InvestigateTime更新
	 */
	Integer updatePeriodInfoByInvestigateTime(PeriodInfo bean, String investigateTime);

	/**
	 * 根据InvestigateTime删除
	 */
	Integer deletePeriodInfoByInvestigateTime(String investigateTime);


}
package com.yunnancommon.service;


import com.yunnancommon.entity.vo.CurrentVO;
import com.yunnancommon.entity.vo.EnterpriseReportVO;
import com.yunnancommon.entity.vo.PaginationResultVO;
import com.yunnancommon.entity.po.EnterpriseReportInfo;
import com.yunnancommon.entity.query.EnterpriseReportInfoQuery;

import java.util.List;
/**
 * @Description:企业上报信息表Service
 * @auther:group2
 * @date:2025/10/22
 */
public interface EnterpriseReportInfoService {

	/**
	 * 根据条件查询列表
	 */
	List<EnterpriseReportInfo> findListByParam(EnterpriseReportInfoQuery query);

	/**
	 * 根据条件查询数量
	 */
	Integer findCountByParam(EnterpriseReportInfoQuery query);

	/**
	 * 分页查询
	 */
	PaginationResultVO<EnterpriseReportInfo> findListByPage(EnterpriseReportInfoQuery query);

	/**
	 * 新增
	 */
	Integer add(EnterpriseReportInfo bean);

	/**
	 * 批量新增
	 */
	Integer addBatch(List<EnterpriseReportInfo> listBean);

	/**
	 * 批量新增或修改
	 */
	Integer addOrUpdateBatch(List<EnterpriseReportInfo> listBean);

	/**
	 * 根据EnterpriseIdAndPeriodIdAndReportId查询
	 */
	EnterpriseReportInfo getEnterpriseReportInfoByEnterpriseIdAndPeriodIdAndReportId(String enterpriseId, Long periodId, String reportId);

	/**
	 * 根据EnterpriseIdAndPeriodIdAndReportId更新
	 */
	Integer updateEnterpriseReportInfoByEnterpriseIdAndPeriodIdAndReportId(EnterpriseReportInfo bean, String enterpriseId, Long periodId, String reportId);

	/**
	 * 根据EnterpriseIdAndPeriodIdAndReportId删除
	 */
	Integer deleteEnterpriseReportInfoByEnterpriseIdAndPeriodIdAndReportId(String enterpriseId, Long periodId, String reportId);


	PaginationResultVO<EnterpriseReportVO> findListByPageWithAssociatedEnterpriseName(EnterpriseReportInfoQuery query);

    void getStatisticCount(CurrentVO currentVO);

	void getCityStatisticCount(CurrentVO currentVO, Integer cityCode);
}
package com.yunnancommon.service;


import com.yunnancommon.entity.vo.PaginationResultVO;
import com.yunnancommon.entity.po.ReportInfo;
import com.yunnancommon.entity.query.ReportInfoQuery;
import com.yunnancommon.entity.vo.ReportInfoDetailVO;

import java.util.List;
/**
 * @Description:数据填报信息表Service
 * @auther:group2
 * @date:2025/10/22
 */
public interface ReportInfoService {

	/**
	 * 根据条件查询列表
	 */
	List<ReportInfo> findListByParam(ReportInfoQuery query);

	/**
	 * 根据条件查询数量
	 */
	Integer findCountByParam(ReportInfoQuery query);

	/**
	 * 分页查询
	 */
	PaginationResultVO<ReportInfo> findListByPage(ReportInfoQuery query);

	/**
	 * 新增
	 */
	Integer add(ReportInfo bean);

	/**
	 * 批量新增
	 */
	Integer addBatch(List<ReportInfo> listBean);

	/**
	 * 批量新增或修改
	 */
	Integer addOrUpdateBatch(List<ReportInfo> listBean);

	/**
	 * 根据ReportId查询
	 */
	ReportInfo getReportInfoByReportId(String reportId);

	/**
	 * 根据ReportId更新
	 */
	Integer updateReportInfoByReportId(ReportInfo bean, String reportId);

	/**
	 * 根据ReportId删除
	 */
	Integer deleteReportInfoByReportId(String reportId);

	/**
	 * 根据ReportId查询详情
	 */
	ReportInfoDetailVO getReportInfoDetailByReportId(String reportId);
}
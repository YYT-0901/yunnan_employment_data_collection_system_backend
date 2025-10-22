package com.yunnancommon.service;


import com.yunnancommon.entity.vo.PaginationResultVO;
import com.yunnancommon.entity.po.LogInfo;
import com.yunnancommon.entity.query.LogInfoQuery;

import java.util.List;
/**
 * @Description:操作日志表Service
 * @auther:group2
 * @date:2025/10/22
 */
public interface LogInfoService {

	/**
	 * 根据条件查询列表
	 */
	List<LogInfo> findListByParam(LogInfoQuery query);

	/**
	 * 根据条件查询数量
	 */
	Integer findCountByParam(LogInfoQuery query);

	/**
	 * 分页查询
	 */
	PaginationResultVO<LogInfo> findListByPage(LogInfoQuery query);

	/**
	 * 新增
	 */
	Integer add(LogInfo bean);

	/**
	 * 批量新增
	 */
	Integer addBatch(List<LogInfo> listBean);

	/**
	 * 批量新增或修改
	 */
	Integer addOrUpdateBatch(List<LogInfo> listBean);

	/**
	 * 根据LogId查询
	 */
	LogInfo getLogInfoByLogId(Long logId);

	/**
	 * 根据LogId更新
	 */
	Integer updateLogInfoByLogId(LogInfo bean, Long logId);

	/**
	 * 根据LogId删除
	 */
	Integer deleteLogInfoByLogId(Long logId);


}
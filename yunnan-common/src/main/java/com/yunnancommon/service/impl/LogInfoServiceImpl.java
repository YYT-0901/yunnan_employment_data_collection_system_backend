package com.yunnancommon.service.impl;


import com.yunnancommon.entity.query.SimplePage;
import com.yunnancommon.enums.PageSize;
import com.yunnancommon.entity.vo.PaginationResultVO;
import com.yunnancommon.entity.po.LogInfo;
import com.yunnancommon.entity.query.LogInfoQuery;
import com.yunnancommon.mapper.LogInfoMapper;
import com.yunnancommon.service.LogInfoService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.util.List;
/**
 * @Description:操作日志表ServiceImpl
 * @auther:group2
 * @date:2025/10/22
 */
@Service("logInfoService")
public class LogInfoServiceImpl implements LogInfoService {

	@Resource
	private LogInfoMapper<LogInfo, LogInfoQuery> logInfoMapper;

	/**
	 * 根据条件查询列表
	 */
	@Override
	public List<LogInfo> findListByParam(LogInfoQuery query) {
		return this.logInfoMapper.selectList(query);
	}

	/**
	 * 根据条件查询数量
	 */
	@Override
	public Integer findCountByParam(LogInfoQuery query) {
		return this.logInfoMapper.selectCount(query);
	}

	/**
	 * 分页查询
	 */
	@Override
	public PaginationResultVO<LogInfo> findListByPage(LogInfoQuery query) {
		Integer count = this.findCountByParam(query);
		Integer pageSize = query.getPageSize() == null ? PageSize.SIZE15.getSize(): query.getPageSize();
		SimplePage page = new SimplePage(query.getPageNo(), count, pageSize);
		query.setSimplePage(page);
		List<LogInfo> list = this.findListByParam(query);
		PaginationResultVO<LogInfo> result = new PaginationResultVO<LogInfo>(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
		return result;
	}

	/**
	 * 新增
	 */
	@Override
	public Integer add(LogInfo bean) {
		return this.logInfoMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	@Override
	public Integer addBatch(List<LogInfo> listBean) {
		if(listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.logInfoMapper.insertBatch(listBean);
	}

	/**
	 * 批量新增或修改
	 */
	@Override
	public Integer addOrUpdateBatch(List<LogInfo> listBean) {
		if(listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.logInfoMapper.insertOrUpdateBatch(listBean);
	}

	/**
	 * 根据LogId查询
	 */
	@Override
	public LogInfo getLogInfoByLogId(Long logId) {
		return this.logInfoMapper.selectByLogId(logId);
	}

	/**
	 * 根据LogId更新
	 */
	@Override
	public Integer updateLogInfoByLogId(LogInfo bean, Long logId) {
		return this.logInfoMapper.updateByLogId(bean, logId);
	}

	/**
	 * 根据LogId删除
	 */
	@Override
	public Integer deleteLogInfoByLogId(Long logId) {
		return this.logInfoMapper.deleteByLogId(logId);
	}


}
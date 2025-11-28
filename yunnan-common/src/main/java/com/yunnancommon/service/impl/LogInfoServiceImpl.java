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
		List<LogInfo> list = this.logInfoMapper.selectList(query);
		if (list != null) {
			for (LogInfo logInfo : list) {
				fillEnterpriseIdFromParams(logInfo);
			}
		}
		return list;
	}

	/**
	 * 辅助方法：如果 enterpriseId 为空，尝试从 requestParams 中解析
	 */
	private void fillEnterpriseIdFromParams(LogInfo logInfo) {
		if (logInfo == null) return;
		if (logInfo.getEnterpriseId() == null && logInfo.getRequestParams() != null) {
			String params = logInfo.getRequestParams();
			String prefix = "企业ID：";
			int start = params.indexOf(prefix);
			if (start != -1) {
				int end = params.indexOf("；", start);
				if (end == -1) {
					end = params.length();
				}
				String eid = params.substring(start + prefix.length(), end).trim();
				if (!eid.isEmpty()) {
					logInfo.setEnterpriseId(eid);
				}
			}
		}
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
		LogInfo logInfo = this.logInfoMapper.selectByLogId(logId);
		fillEnterpriseIdFromParams(logInfo);
		return logInfo;
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

	@Override
	public void clearLogs(String endTime) {
		this.logInfoMapper.deleteAll(endTime);
	}


}
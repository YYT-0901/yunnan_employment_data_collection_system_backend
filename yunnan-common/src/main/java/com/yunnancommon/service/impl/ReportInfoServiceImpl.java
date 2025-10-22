package com.yunnancommon.service.impl;


import com.yunnancommon.entity.query.SimplePage;
import com.yunnancommon.enums.PageSize;
import com.yunnancommon.entity.vo.PaginationResultVO;
import com.yunnancommon.entity.po.ReportInfo;
import com.yunnancommon.entity.query.ReportInfoQuery;
import com.yunnancommon.mapper.ReportInfoMapper;
import com.yunnancommon.service.ReportInfoService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.util.List;
/**
 * @Description:数据填报信息表ServiceImpl
 * @auther:group2
 * @date:2025/10/22
 */
@Service("reportInfoService")
public class ReportInfoServiceImpl implements ReportInfoService {

	@Resource
	private ReportInfoMapper<ReportInfo, ReportInfoQuery> reportInfoMapper;

	/**
	 * 根据条件查询列表
	 */
	@Override
	public List<ReportInfo> findListByParam(ReportInfoQuery query) {
		return this.reportInfoMapper.selectList(query);
	}

	/**
	 * 根据条件查询数量
	 */
	@Override
	public Integer findCountByParam(ReportInfoQuery query) {
		return this.reportInfoMapper.selectCount(query);
	}

	/**
	 * 分页查询
	 */
	@Override
	public PaginationResultVO<ReportInfo> findListByPage(ReportInfoQuery query) {
		Integer count = this.findCountByParam(query);
		Integer pageSize = query.getPageSize() == null ? PageSize.SIZE15.getSize(): query.getPageSize();
		SimplePage page = new SimplePage(query.getPageNo(), count, pageSize);
		query.setSimplePage(page);
		List<ReportInfo> list = this.findListByParam(query);
		PaginationResultVO<ReportInfo> result = new PaginationResultVO<ReportInfo>(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
		return result;
	}

	/**
	 * 新增
	 */
	@Override
	public Integer add(ReportInfo bean) {
		return this.reportInfoMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	@Override
	public Integer addBatch(List<ReportInfo> listBean) {
		if(listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.reportInfoMapper.insertBatch(listBean);
	}

	/**
	 * 批量新增或修改
	 */
	@Override
	public Integer addOrUpdateBatch(List<ReportInfo> listBean) {
		if(listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.reportInfoMapper.insertOrUpdateBatch(listBean);
	}

	/**
	 * 根据ReportId查询
	 */
	@Override
	public ReportInfo getReportInfoByReportId(String reportId) {
		return this.reportInfoMapper.selectByReportId(reportId);
	}

	/**
	 * 根据ReportId更新
	 */
	@Override
	public Integer updateReportInfoByReportId(ReportInfo bean, String reportId) {
		return this.reportInfoMapper.updateByReportId(bean, reportId);
	}

	/**
	 * 根据ReportId删除
	 */
	@Override
	public Integer deleteReportInfoByReportId(String reportId) {
		return this.reportInfoMapper.deleteByReportId(reportId);
	}


}
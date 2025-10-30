package com.yunnancommon.service.impl;


import com.yunnancommon.entity.query.SimplePage;
import com.yunnancommon.entity.vo.EnterpriseReportVO;
import com.yunnancommon.enums.PageSize;
import com.yunnancommon.entity.vo.PaginationResultVO;
import com.yunnancommon.entity.po.EnterpriseReportInfo;
import com.yunnancommon.entity.query.EnterpriseReportInfoQuery;
import com.yunnancommon.mapper.EnterpriseReportInfoMapper;
import com.yunnancommon.service.EnterpriseReportInfoService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.util.List;
/**
 * @Description:企业上报信息表ServiceImpl
 * @auther:group2
 * @date:2025/10/22
 */
@Service("enterpriseReportInfoService")
public class EnterpriseReportInfoServiceImpl implements EnterpriseReportInfoService {

	@Resource
	private EnterpriseReportInfoMapper<EnterpriseReportInfo, EnterpriseReportInfoQuery> enterpriseReportInfoMapper;

	/**
	 * 根据条件查询列表
	 */
	@Override
	public List<EnterpriseReportInfo> findListByParam(EnterpriseReportInfoQuery query) {
		return this.enterpriseReportInfoMapper.selectList(query);
	}

	/**
	 * 根据条件查询数量
	 */
	@Override
	public Integer findCountByParam(EnterpriseReportInfoQuery query) {
		return this.enterpriseReportInfoMapper.selectCount(query);
	}

	/**
	 * 分页查询
	 */
	@Override
	public PaginationResultVO<EnterpriseReportInfo> findListByPage(EnterpriseReportInfoQuery query) {
		Integer count = this.findCountByParam(query);
		Integer pageSize = query.getPageSize() == null ? PageSize.SIZE15.getSize(): query.getPageSize();
		SimplePage page = new SimplePage(query.getPageNo(), count, pageSize);
		query.setSimplePage(page);
		List<EnterpriseReportInfo> list = this.findListByParam(query);
		PaginationResultVO<EnterpriseReportInfo> result = new PaginationResultVO<EnterpriseReportInfo>(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
		return result;
	}

	@Override
	public PaginationResultVO<EnterpriseReportVO> findListByPageWithAssociatedEnterpriseName(EnterpriseReportInfoQuery query) {
		Integer count = this.findCountByParam(query);
		Integer pageSize = query.getPageSize() == null ? PageSize.SIZE15.getSize(): query.getPageSize();
		SimplePage page = new SimplePage(query.getPageNo(), count, pageSize);
		query.setSimplePage(page);
		List<EnterpriseReportVO> list = enterpriseReportInfoMapper.selectListWithAssociated(query);
		PaginationResultVO<EnterpriseReportVO> result = new PaginationResultVO<EnterpriseReportVO>(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
		return result;
	}

	/**
	 * 新增
	 */
	@Override
	public Integer add(EnterpriseReportInfo bean) {
		return this.enterpriseReportInfoMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	@Override
	public Integer addBatch(List<EnterpriseReportInfo> listBean) {
		if(listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.enterpriseReportInfoMapper.insertBatch(listBean);
	}

	/**
	 * 批量新增或修改
	 */
	@Override
	public Integer addOrUpdateBatch(List<EnterpriseReportInfo> listBean) {
		if(listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.enterpriseReportInfoMapper.insertOrUpdateBatch(listBean);
	}

	/**
	 * 根据EnterpriseIdAndPeriodIdAndReportId查询
	 */
	@Override
	public EnterpriseReportInfo getEnterpriseReportInfoByEnterpriseIdAndPeriodIdAndReportId(String enterpriseId, Integer periodId, String reportId) {
		return this.enterpriseReportInfoMapper.selectByEnterpriseIdAndPeriodIdAndReportId(enterpriseId, periodId, reportId);
	}

	/**
	 * 根据EnterpriseIdAndPeriodIdAndReportId更新
	 */
	@Override
	public Integer updateEnterpriseReportInfoByEnterpriseIdAndPeriodIdAndReportId(EnterpriseReportInfo bean, String enterpriseId, Integer periodId, String reportId) {
		return this.enterpriseReportInfoMapper.updateByEnterpriseIdAndPeriodIdAndReportId(bean, enterpriseId, periodId, reportId);
	}

	/**
	 * 根据EnterpriseIdAndPeriodIdAndReportId删除
	 */
	@Override
	public Integer deleteEnterpriseReportInfoByEnterpriseIdAndPeriodIdAndReportId(String enterpriseId, Integer periodId, String reportId) {
		return this.enterpriseReportInfoMapper.deleteByEnterpriseIdAndPeriodIdAndReportId(enterpriseId, periodId, reportId);
	}


}
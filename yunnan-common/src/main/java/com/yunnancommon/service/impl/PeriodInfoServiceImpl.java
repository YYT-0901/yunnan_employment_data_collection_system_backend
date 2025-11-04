package com.yunnancommon.service.impl;


import com.yunnancommon.entity.query.SimplePage;
import com.yunnancommon.enums.PageSize;
import com.yunnancommon.entity.vo.PaginationResultVO;
import com.yunnancommon.entity.po.PeriodInfo;
import com.yunnancommon.entity.query.PeriodInfoQuery;
import com.yunnancommon.mapper.PeriodInfoMapper;
import com.yunnancommon.service.PeriodInfoService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.util.List;
/**
 * @Description:调查期信息表ServiceImpl
 * @auther:group2
 * @date:2025/11/04
 */
@Service("periodInfoService")
public class PeriodInfoServiceImpl implements PeriodInfoService {

	@Resource
	private PeriodInfoMapper<PeriodInfo, PeriodInfoQuery> periodInfoMapper;

	/**
	 * 根据条件查询列表
	 */
	@Override
	public List<PeriodInfo> findListByParam(PeriodInfoQuery query) {
		return this.periodInfoMapper.selectList(query);
	}

	/**
	 * 根据条件查询数量
	 */
	@Override
	public Integer findCountByParam(PeriodInfoQuery query) {
		return this.periodInfoMapper.selectCount(query);
	}

	/**
	 * 分页查询
	 */
	@Override
	public PaginationResultVO<PeriodInfo> findListByPage(PeriodInfoQuery query) {
		Integer count = this.findCountByParam(query);
		Integer pageSize = query.getPageSize() == null ? PageSize.SIZE15.getSize(): query.getPageSize();
		SimplePage page = new SimplePage(query.getPageNo(), count, pageSize);
		query.setSimplePage(page);
		List<PeriodInfo> list = this.findListByParam(query);
		PaginationResultVO<PeriodInfo> result = new PaginationResultVO<PeriodInfo>(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
		return result;
	}

	/**
	 * 新增
	 */
	@Override
	public Integer add(PeriodInfo bean) {
		return this.periodInfoMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	@Override
	public Integer addBatch(List<PeriodInfo> listBean) {
		if(listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.periodInfoMapper.insertBatch(listBean);
	}

	/**
	 * 批量新增或修改
	 */
	@Override
	public Integer addOrUpdateBatch(List<PeriodInfo> listBean) {
		if(listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.periodInfoMapper.insertOrUpdateBatch(listBean);
	}

	/**
	 * 根据PeriodId查询
	 */
	@Override
	public PeriodInfo getPeriodInfoByPeriodId(Long periodId) {
		return this.periodInfoMapper.selectByPeriodId(periodId);
	}

	/**
	 * 根据PeriodId更新
	 */
	@Override
	public Integer updatePeriodInfoByPeriodId(PeriodInfo bean, Long periodId) {
		return this.periodInfoMapper.updateByPeriodId(bean, periodId);
	}

	/**
	 * 根据PeriodId删除
	 */
	@Override
	public Integer deletePeriodInfoByPeriodId(Long periodId) {
		return this.periodInfoMapper.deleteByPeriodId(periodId);
	}

	/**
	 * 根据InvestigateTime查询
	 */
	@Override
	public PeriodInfo getPeriodInfoByInvestigateTime(String investigateTime) {
		return this.periodInfoMapper.selectByInvestigateTime(investigateTime);
	}

	/**
	 * 根据InvestigateTime更新
	 */
	@Override
	public Integer updatePeriodInfoByInvestigateTime(PeriodInfo bean, String investigateTime) {
		return this.periodInfoMapper.updateByInvestigateTime(bean, investigateTime);
	}

	/**
	 * 根据InvestigateTime删除
	 */
	@Override
	public Integer deletePeriodInfoByInvestigateTime(String investigateTime) {
		return this.periodInfoMapper.deleteByInvestigateTime(investigateTime);
	}

	/**
	 * 根据参数更新
	 */
	@Override
	public Integer updateByParams (PeriodInfo bean, PeriodInfoQuery query) {
		return this.periodInfoMapper.updateByParams(bean, query);
	}

	/**
	 * 根据参数删除
	 */
	@Override
	public Integer deleteByParam(PeriodInfoQuery query) {
		return this.periodInfoMapper.deleteByParams(query);
	}

}
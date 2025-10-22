package com.yunnancommon.service.impl;


import com.yunnancommon.entity.query.SimplePage;
import com.yunnancommon.enums.PageSize;
import com.yunnancommon.entity.vo.PaginationResultVO;
import com.yunnancommon.entity.po.EnterpriseInfo;
import com.yunnancommon.entity.query.EnterpriseInfoQuery;
import com.yunnancommon.mapper.EnterpriseInfoMapper;
import com.yunnancommon.service.EnterpriseInfoService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.util.List;
/**
 * @Description:企业信息表ServiceImpl
 * @auther:group2
 * @date:2025/10/22
 */
@Service("enterpriseInfoService")
public class EnterpriseInfoServiceImpl implements EnterpriseInfoService {

	@Resource
	private EnterpriseInfoMapper<EnterpriseInfo, EnterpriseInfoQuery> enterpriseInfoMapper;

	/**
	 * 根据条件查询列表
	 */
	@Override
	public List<EnterpriseInfo> findListByParam(EnterpriseInfoQuery query) {
		return this.enterpriseInfoMapper.selectList(query);
	}

	/**
	 * 根据条件查询数量
	 */
	@Override
	public Integer findCountByParam(EnterpriseInfoQuery query) {
		return this.enterpriseInfoMapper.selectCount(query);
	}

	/**
	 * 分页查询
	 */
	@Override
	public PaginationResultVO<EnterpriseInfo> findListByPage(EnterpriseInfoQuery query) {
		Integer count = this.findCountByParam(query);
		Integer pageSize = query.getPageSize() == null ? PageSize.SIZE15.getSize(): query.getPageSize();
		SimplePage page = new SimplePage(query.getPageNo(), count, pageSize);
		query.setSimplePage(page);
		List<EnterpriseInfo> list = this.findListByParam(query);
		PaginationResultVO<EnterpriseInfo> result = new PaginationResultVO<EnterpriseInfo>(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
		return result;
	}

	/**
	 * 新增
	 */
	@Override
	public Integer add(EnterpriseInfo bean) {
		return this.enterpriseInfoMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	@Override
	public Integer addBatch(List<EnterpriseInfo> listBean) {
		if(listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.enterpriseInfoMapper.insertBatch(listBean);
	}

	/**
	 * 批量新增或修改
	 */
	@Override
	public Integer addOrUpdateBatch(List<EnterpriseInfo> listBean) {
		if(listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.enterpriseInfoMapper.insertOrUpdateBatch(listBean);
	}

	/**
	 * 根据EnterpriseId查询
	 */
	@Override
	public EnterpriseInfo getEnterpriseInfoByEnterpriseId(String enterpriseId) {
		return this.enterpriseInfoMapper.selectByEnterpriseId(enterpriseId);
	}

	/**
	 * 根据EnterpriseId更新
	 */
	@Override
	public Integer updateEnterpriseInfoByEnterpriseId(EnterpriseInfo bean, String enterpriseId) {
		return this.enterpriseInfoMapper.updateByEnterpriseId(bean, enterpriseId);
	}

	/**
	 * 根据EnterpriseId删除
	 */
	@Override
	public Integer deleteEnterpriseInfoByEnterpriseId(String enterpriseId) {
		return this.enterpriseInfoMapper.deleteByEnterpriseId(enterpriseId);
	}


}
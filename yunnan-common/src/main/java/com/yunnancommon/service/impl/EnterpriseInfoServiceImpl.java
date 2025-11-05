package com.yunnancommon.service.impl;


import com.yunnancommon.entity.constants.Constants;
import com.yunnancommon.entity.po.AccountInfo;
import com.yunnancommon.entity.po.PeriodInfo;
import com.yunnancommon.entity.query.AccountInfoQuery;
import com.yunnancommon.entity.query.PeriodInfoQuery;
import com.yunnancommon.entity.query.SimplePage;
import com.yunnancommon.entity.vo.CreatedAccountVO;
import com.yunnancommon.enums.AccountStatusEnum;
import com.yunnancommon.enums.AccountTypeEnum;
import com.yunnancommon.enums.EnterpriseStatusEnum;
import com.yunnancommon.enums.PageSize;
import com.yunnancommon.entity.vo.PaginationResultVO;
import com.yunnancommon.entity.po.EnterpriseInfo;
import com.yunnancommon.entity.query.EnterpriseInfoQuery;
import com.yunnancommon.mapper.AccountInfoMapper;
import com.yunnancommon.mapper.EnterpriseInfoMapper;
import com.yunnancommon.mapper.PeriodInfoMapper;
import com.yunnancommon.service.EnterpriseInfoService;
import com.yunnancommon.utils.DateUtils;
import com.yunnancommon.utils.StringTools;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import java.util.Date;
import java.util.List;
/**
 * @Description:企业信息表ServiceImpl
 * @auther:group2
 * @date:2025/11/04
 */
@Service("enterpriseInfoService")
public class EnterpriseInfoServiceImpl implements EnterpriseInfoService {

	@Resource
	private EnterpriseInfoMapper<EnterpriseInfo, EnterpriseInfoQuery> enterpriseInfoMapper;
	@Resource
	private PeriodInfoMapper<PeriodInfo, PeriodInfoQuery> periodInfoMapper;
	@Resource
	private AccountInfoMapper<AccountInfo, AccountInfoQuery> accountInfoMapper;

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

	/**
	 * 根据参数更新
	 */
	@Override
	public Integer updateByParams(EnterpriseInfo bean, EnterpriseInfoQuery query) {
		return this.enterpriseInfoMapper.updateByParams(bean, query);
	}

	/**
	 * 根据参数删除
	 */
	@Override
	public Integer deleteByParams(EnterpriseInfoQuery query) {
		return this.enterpriseInfoMapper.deleteByParams(query);
	}

	@Override
    @Transactional(rollbackFor = Exception.class)
    public CreatedAccountVO createEnterpriseAccount(EnterpriseInfo enterpriseInfo) {
        // 添加企业账号数量
        String investigateTime = DateUtils.format(new Date(), Constants.PATTERN_INVESTIGATE_TIME);
        PeriodInfo periodInfo = this.periodInfoMapper.selectByInvestigateTime(investigateTime);
        
        // 如果调查期不存在，创建一个新的调查期
        if (periodInfo == null) {
            periodInfo = new PeriodInfo();
            periodInfo.setInvestigateTime(investigateTime);
            periodInfo.setEnterpriseCount(Constants.ZERO);
            Date now = new Date();
            periodInfo.setPeriodStartTime(now);
            periodInfo.setPeriodEndTime(now);
            periodInfo.setCreatedAt(now);
            periodInfo.setUpdatedAt(now);
            this.periodInfoMapper.insert(periodInfo);
            // 重新查询获取生成的ID
            periodInfo = this.periodInfoMapper.selectByInvestigateTime(investigateTime);
            if (periodInfo == null) {
                throw new RuntimeException("创建调查期失败，无法继续创建企业账号");
            }
        }
        
        PeriodInfoQuery periodInfoQuery = new PeriodInfoQuery();
        periodInfoQuery.setInvestigateTime(investigateTime);
        periodInfoQuery.setEnterpriseCount(periodInfo.getEnterpriseCount());
        periodInfo.setEnterpriseCount(periodInfo.getEnterpriseCount() != null ? periodInfo.getEnterpriseCount() + Constants.ONE : Constants.ONE);
        Integer i = this.periodInfoMapper.updateByParams(periodInfo, periodInfoQuery);
        if (i == 0) {
            throw new RuntimeException("创建账号失败：更新调查期失败");
        }

        // 创建企业详情
        String enterpriseId = Constants.ENTERPRISE_ID_PREFIX + StringTools.getRandomNumber(20);
        enterpriseInfo.setEnterpriseId(enterpriseId);
        enterpriseInfo.setCreatedAt(new Date());
        enterpriseInfo.setUpdatedAt(new Date());
		if(enterpriseInfo.getRegion() != null) {
			enterpriseInfo.setRegionCode(StringTools.getTopCategoryCode(enterpriseInfo.getRegion(), Constants.REGION));
		}
		if(enterpriseInfo.getNature() != null) {
			enterpriseInfo.setNatureCode(StringTools.getTopCategoryCode(enterpriseInfo.getNature(), Constants.NATURE));
		}
		if(enterpriseInfo.getIndustry() != null) {
			enterpriseInfo.setIndustryCode(StringTools.getTopCategoryCode(enterpriseInfo.getIndustry(), Constants.INDUSTRY));
		}
        enterpriseInfo.setStatus(EnterpriseStatusEnum.CREATED.getCode());
        this.enterpriseInfoMapper.insert(enterpriseInfo);

        // 创建账号
        String username = StringTools.getRandomString(10);
        String password = StringTools.getRandomString(10);

        AccountInfo accountInfo = new AccountInfo();
        accountInfo.setEnterpriseId(enterpriseId);
        accountInfo.setCreatedAt(new Date());
        accountInfo.setStatus(AccountStatusEnum.ENABLE.getCode());
        accountInfo.setUsername(username);
        accountInfo.setPassword(password);
        accountInfo.setType(AccountTypeEnum.ENTERPRISE.getCode());
        this.accountInfoMapper.insert(accountInfo);

        return new CreatedAccountVO(username, password);
    }

    @Override
    public CreatedAccountVO createCityAccount(Integer cityCode) {
        String username = StringTools.getRandomString(10);
        String password = StringTools.getRandomString(10);

        AccountInfo accountInfo = new AccountInfo();
        accountInfo.setCreatedAt(new Date());
        accountInfo.setStatus(AccountStatusEnum.ENABLE.getCode());
        accountInfo.setUsername(username);
        accountInfo.setPassword(password);
        accountInfo.setType(AccountTypeEnum.CITY.getCode());
        accountInfo.setCityCode(cityCode);
        this.accountInfoMapper.insert(accountInfo);

        return new CreatedAccountVO(username, password);
    }

}
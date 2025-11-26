package com.yunnancommon.service.impl;


import com.yunnancommon.component.RedisComponent;
import com.yunnancommon.entity.po.EnterpriseInfo;
import com.yunnancommon.entity.query.EnterpriseInfoQuery;
import com.yunnancommon.entity.query.SimplePage;
import com.yunnancommon.entity.vo.AccountEnterpriseVO;
import com.yunnancommon.entity.vo.TokenInfoVO;
import com.yunnancommon.enums.AccountStatusEnum;
import com.yunnancommon.enums.AccountTypeEnum;
import com.yunnancommon.enums.CityDict;
import com.yunnancommon.enums.PageSize;
import com.yunnancommon.entity.vo.PaginationResultVO;
import com.yunnancommon.entity.po.AccountInfo;
import com.yunnancommon.entity.query.AccountInfoQuery;
import com.yunnancommon.exception.BusinessException;
import com.yunnancommon.mapper.AccountInfoMapper;
import com.yunnancommon.mapper.EnterpriseInfoMapper;
import com.yunnancommon.service.AccountInfoService;
import com.yunnancommon.utils.TokenUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.util.Date;
import java.util.List;

@Service("accountInfoService")
public class AccountInfoServiceImpl implements AccountInfoService {

	@Resource
	private AccountInfoMapper<AccountInfo, AccountInfoQuery> accountInfoMapper;

	@Resource
	private EnterpriseInfoMapper<EnterpriseInfo, EnterpriseInfoQuery> enterpriseInfoMapper;

	@Resource
	private RedisComponent redisComponent;

	/**
	 * 根据条件查询列表
	 */
	@Override
	public List<AccountInfo> findListByParam(AccountInfoQuery query) {
		return this.accountInfoMapper.selectList(query);
	}

	/**
	 * 根据条件查询数量
	 */
	@Override
	public Integer findCountByParam(AccountInfoQuery query) {
		return this.accountInfoMapper.selectCount(query);
	}

	/**
	 * 分页查询
	 */
	@Override
	public PaginationResultVO<AccountInfo> findListByPage(AccountInfoQuery query) {
		Integer count = this.findCountByParam(query);
		Integer pageSize = query.getPageSize() == null ? PageSize.SIZE15.getSize(): query.getPageSize();
		SimplePage page = new SimplePage(query.getPageNo(), count, pageSize);
		query.setSimplePage(page);
		List<AccountInfo> list = this.findListByParam(query);
		PaginationResultVO<AccountInfo> result = new PaginationResultVO<AccountInfo>(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
		return result;
	}

	@Override
	public PaginationResultVO<AccountEnterpriseVO> findListByPageWithAssociated(AccountInfoQuery accountQuery, EnterpriseInfoQuery enterpriseQuery) {
		Integer count = this.findCountWithAssociated(accountQuery, enterpriseQuery);
		Integer pageSize = accountQuery.getPageSize() == null ? PageSize.SIZE15.getSize(): accountQuery.getPageSize();
		SimplePage page = new SimplePage(accountQuery.getPageNo(), count, pageSize);
		accountQuery.setSimplePage(page);
		List<AccountEnterpriseVO> list = this.accountInfoMapper.findListByParamWithAssociatedEnterpriseInfo(accountQuery, enterpriseQuery);
		PaginationResultVO<AccountEnterpriseVO> result = new PaginationResultVO<AccountEnterpriseVO>(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
		return result;
	}

	@Override
	public List<AccountEnterpriseVO> findListWithAssociated(AccountInfoQuery accountQuery, EnterpriseInfoQuery enterpriseQuery) {
		return this.accountInfoMapper.findListByParamWithAssociatedEnterpriseInfo(accountQuery, enterpriseQuery);
	}

	@Override
	public Integer findCountWithAssociated(AccountInfoQuery accountQuery, EnterpriseInfoQuery enterpriseQuery) {
		return this.accountInfoMapper.selectCountWithEnterprise(accountQuery, enterpriseQuery);
	}

	/**
	 * 新增
	 */
	@Override
	public Integer add(AccountInfo bean) {
		return this.accountInfoMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	@Override
	public Integer addBatch(List<AccountInfo> listBean) {
		if(listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.accountInfoMapper.insertBatch(listBean);
	}

	/**
	 * 批量新增或修改
	 */
	@Override
	public Integer addOrUpdateBatch(List<AccountInfo> listBean) {
		if(listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.accountInfoMapper.insertOrUpdateBatch(listBean);
	}

	/**
	 * 根据Username查询
	 */
	@Override
	public AccountInfo getAccountInfoByUsername(String username) {
		return this.accountInfoMapper.selectByUsername(username);
	}

	/**
	 * 根据Username更新
	 */
	@Override
	public Integer updateAccountInfoByUsername(AccountInfo bean, String username) {
		return this.accountInfoMapper.updateByUsername(bean, username);
	}

	/**
	 * 根据Username删除
	 */
	@Override
	public Integer deleteAccountInfoByUsername(String username) {
		return this.accountInfoMapper.deleteByUsername(username);
	}

	@Override
	public TokenInfoVO login(String account, String password) throws BusinessException {
		AccountInfo accountInfo = accountInfoMapper.selectByUsername(account);
		if(accountInfo == null || !accountInfo.getPassword().equals(password)) {
			throw new BusinessException("账号或密码错误");
		}
		if(!AccountTypeEnum.ENTERPRISE.getCode().equals(accountInfo.getType())) {
			throw new BusinessException("账号或密码错误");
		}
		if(AccountStatusEnum.DISABLE.getCode().equals(accountInfo.getStatus())) {
			throw new BusinessException("账号被禁用");
		}

		EnterpriseInfo enterpriseInfo = enterpriseInfoMapper.selectByEnterpriseId(accountInfo.getEnterpriseId());

		String token = TokenUtils.generateToken();
		TokenInfoVO tokenInfoVO = new TokenInfoVO();
		tokenInfoVO.setToken(token);
		tokenInfoVO.setUsername(accountInfo.getUsername());  // 设置用户名
		tokenInfoVO.setEnterpriseInfo(enterpriseInfo);

		redisComponent.saveEnterpriseTokenInfo(tokenInfoVO);

		accountInfo.setLastLoginTime(new Date());
		accountInfoMapper.updateByUsername(accountInfo, account);

		return tokenInfoVO;
	}

	@Override
	public TokenInfoVO cityLogin(String account, String password) throws BusinessException {
		AccountInfo accountInfo = accountInfoMapper.selectByUsername(account);
		if(accountInfo == null || !accountInfo.getPassword().equals(password)) {
			throw new BusinessException("账号或密码错误");
		}
		if(!AccountTypeEnum.CITY.getCode().equals(accountInfo.getType())) {
			throw new BusinessException("账号或密码错误");
		}
		if(AccountStatusEnum.DISABLE.getCode().equals(accountInfo.getStatus())) {
			throw new BusinessException("账号被禁用");
		}

		String token = TokenUtils.generateToken();
		TokenInfoVO tokenInfoVO = new TokenInfoVO();
		tokenInfoVO.setToken(token);
		tokenInfoVO.setUsername(accountInfo.getUsername());
		tokenInfoVO.setCityCode(accountInfo.getCityCode());

		redisComponent.saveCityTokenInfo(tokenInfoVO);

		accountInfo.setLastLoginTime(new Date());
		accountInfoMapper.updateByUsername(accountInfo, account);

		return tokenInfoVO;
	}

}

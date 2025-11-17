package com.yunnanprovince.controller;

import com.yunnancommon.component.RedisComponent;
import com.yunnancommon.annotation.OperationLog;
import com.yunnancommon.controller.ABaseController;
import com.yunnancommon.entity.constants.Constants;
import com.yunnancommon.entity.dto.ChangeStatusDto;
import com.yunnancommon.entity.dto.CreateAccountDto;
import com.yunnancommon.entity.dto.LoginDto;
import com.yunnancommon.entity.po.AccountInfo;
import com.yunnancommon.entity.po.EnterpriseInfo;
import com.yunnancommon.entity.po.PeriodInfo;
import com.yunnancommon.entity.query.AccountInfoQuery;
import com.yunnancommon.entity.query.EnterpriseInfoQuery;
import com.yunnancommon.entity.query.PeriodInfoQuery;
import com.yunnancommon.entity.vo.CreatedAccountVO;
import com.yunnancommon.entity.vo.PaginationResultVO;
import com.yunnancommon.entity.vo.ResponseVO;
import com.yunnancommon.enums.AccountTypeEnum;
import com.yunnancommon.enums.ResponseCodeEnum;
import com.yunnancommon.exception.BusinessException;
import com.yunnancommon.service.AccountInfoService;
import com.yunnancommon.service.EnterpriseInfoService;
import com.yunnancommon.service.PeriodInfoService;
import com.yunnancommon.service.impl.EnterpriseInfoServiceImpl;
import com.yunnancommon.utils.DateUtils;
import com.yunnancommon.utils.TokenUtils;
import com.yunnanprovince.config.AppConfig;
import jakarta.annotation.Resource;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotEmpty;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

import java.util.Date;

@RestController
@RequestMapping("/account")
public class AccountController extends ABaseController {
    @Resource
    private AppConfig appconfig;

    @Resource
    private RedisComponent redisComponent;
    @Resource
    private EnterpriseInfoService enterpriseInfoService;
    @Resource
    private AccountInfoService accountInfoService;
    @Resource
    private PeriodInfoService periodInfoService;

    @PostMapping("/login")
    public ResponseVO login(HttpServletRequest request, HttpServletResponse response, @RequestBody LoginDto loginDto) throws BusinessException {
        try {
            if (!appconfig.getUsername().equals(loginDto.getUsername()) || !appconfig.getPassword().equals(loginDto.getPassword())) {
                throw new BusinessException("账号或密码错误");
            }

            String token = TokenUtils.generateToken();
            saveToken2Cookie(response, token);

            redisComponent.saveProvinceTokenInfo(token);

            return getSuccessResponseVO(token);
        } finally {
            // 清除旧的token
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                String token = null;
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals(Constants.TOKEN_KEY)) {
                        token = cookie.getValue();
                    }
                }
                if (!StringUtils.isEmpty(token)) {
                    redisComponent.cleanProvinceTokenInfo(token);
                }
            }
        }
    }

    @PostMapping("/logout")
    public ResponseVO logout(HttpServletResponse response) {
        cleanToken(response, AccountTypeEnum.PROVINCE);
        return getSuccessResponseVO(null);
    }

    /**
     * 新增账号(企业,市)
     * */
    @PostMapping("/createAccount")
    @OperationLog(module = "enterprise", operation = "创建企业账号")
    public ResponseVO createAccount(@RequestBody CreateAccountDto createAccountDto) throws BusinessException {
        Integer type = createAccountDto.getType();
        if (!AccountTypeEnum.CITY.getCode().equals(type) && !AccountTypeEnum.ENTERPRISE.getCode().equals(type)) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        CreatedAccountVO createdAccountVO = null;
        if (AccountTypeEnum.ENTERPRISE.getCode().equals(type)) {
            createdAccountVO = enterpriseInfoService.createEnterpriseAccount(createAccountDto.getEnterpriseInfo());
        } else {
            createdAccountVO = enterpriseInfoService.createCityAccount(createAccountDto.getCityCode());
        }

        // TODO 发送邮箱到企业人email

        return getSuccessResponseVO(createdAccountVO);
    }

    /**
     * 加载所有企业账号
     */
    @GetMapping("/loadAllEnterpriseAccount")
    public ResponseVO loadAllEnterpriseAccount(AccountInfoQuery query) {
        query.setType(AccountTypeEnum.ENTERPRISE.getCode());
        return getSuccessResponseVO(accountInfoService.findListByPageWithAssociated(query));
    }

    /**
     * 加载所有市账号
     */
    @GetMapping("/loadAllCityAccount")
    public ResponseVO loadAllCityAccount(AccountInfoQuery query) {
        query.setType(AccountTypeEnum.CITY.getCode());
        query.setEnterpriseId(null);
        return getSuccessResponseVO(accountInfoService.findListByPage(query));
    }

    /**
     * 修改账号状态
     */
    @PostMapping("/changeStatus")
    public ResponseVO changeStatus(@RequestBody ChangeStatusDto changeStatusDto) throws BusinessException {
        AccountInfo accountInfo = new AccountInfo();
        accountInfo.setStatus(changeStatusDto.getStatus());
        accountInfoService.updateAccountInfoByUsername(accountInfo,  changeStatusDto.getUsername());
        return getSuccessResponseVO(null);
    }

    /**
     * 加载所有企业列表
     */
    @GetMapping("/loadAllEnterprise")
    public ResponseVO loadAllEnterprise(EnterpriseInfoQuery query) {
        return getSuccessResponseVO(enterpriseInfoService.findListByPage(query));
    }

    /**
     * 根据企业ID获取企业信息
     */
    @GetMapping("/getEnterprise/{enterpriseId}")
    public ResponseVO getEnterprise(@PathVariable String enterpriseId) {
        EnterpriseInfo enterpriseInfo = enterpriseInfoService.getEnterpriseInfoByEnterpriseId(enterpriseId);
        return getSuccessResponseVO(enterpriseInfo);
    }

    /**
     * 更新企业信息
     */
    @PutMapping("/updateEnterprise/{enterpriseId}")
    @OperationLog(module = "enterprise", operation = "更新企业信息")
    public ResponseVO updateEnterprise(@PathVariable String enterpriseId, @RequestBody EnterpriseInfo enterpriseInfo) throws BusinessException {
        enterpriseInfo.setUpdatedAt(new Date());
        // 更新地区、性质、行业的一级分类代码
        if(enterpriseInfo.getRegion() != null) {
            enterpriseInfo.setRegionCode(com.yunnancommon.utils.StringTools.getTopCategoryCode(enterpriseInfo.getRegion(), com.yunnancommon.entity.constants.Constants.REGION));
        }
        if(enterpriseInfo.getNature() != null) {
            enterpriseInfo.setNatureCode(com.yunnancommon.utils.StringTools.getTopCategoryCode(enterpriseInfo.getNature(), com.yunnancommon.entity.constants.Constants.NATURE));
        }
        if(enterpriseInfo.getIndustry() != null) {
            enterpriseInfo.setIndustryCode(com.yunnancommon.utils.StringTools.getTopCategoryCode(enterpriseInfo.getIndustry(), com.yunnancommon.entity.constants.Constants.INDUSTRY));
        }
        Integer result = enterpriseInfoService.updateEnterpriseInfoByEnterpriseId(enterpriseInfo, enterpriseId);
        if (result == 0) {
            throw new BusinessException("更新企业信息失败");
        }
        return getSuccessResponseVO(null);
    }

    /**
     * 删除企业
     */
    @DeleteMapping("/deleteEnterprise/{enterpriseId}")
    @OperationLog(module = "enterprise", operation = "删除企业")
    public ResponseVO deleteEnterprise(@PathVariable String enterpriseId) throws BusinessException {
        Integer result = enterpriseInfoService.deleteEnterpriseInfoByEnterpriseId(enterpriseId);
        if (result == 0) {
            throw new BusinessException("删除企业失败");
        }
        return getSuccessResponseVO(null);
    }

    /**
     * 修改企业状态
     */
    @PostMapping("/changeEnterpriseStatus")
    @OperationLog(module = "enterprise", operation = "修改企业状态")
    public ResponseVO changeEnterpriseStatus(@RequestParam String enterpriseId,
                                             @RequestParam Integer status) throws BusinessException {
        Integer result = enterpriseInfoService.updateStatusByEnterpriseId(enterpriseId, status);
        if (result == 0) {
            throw new BusinessException("非法的企业状态流转或企业不存在");
        }
        return getSuccessResponseVO(null);
    }
}

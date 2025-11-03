package com.yunnanprovince.controller;

import com.yunnancommon.component.RedisComponent;
import com.yunnancommon.controller.ABaseController;
import com.yunnancommon.entity.constants.Constants;
import com.yunnancommon.entity.po.AccountInfo;
import com.yunnancommon.entity.po.EnterpriseInfo;
import com.yunnancommon.entity.query.AccountInfoQuery;
import com.yunnancommon.entity.query.EnterpriseInfoQuery;
import com.yunnancommon.entity.vo.CreatedAccountVO;
import com.yunnancommon.entity.vo.PaginationResultVO;
import com.yunnancommon.entity.vo.ResponseVO;
import com.yunnancommon.enums.AccountTypeEnum;
import com.yunnancommon.enums.ResponseCodeEnum;
import com.yunnancommon.exception.BusinessException;
import com.yunnancommon.service.AccountInfoService;
import com.yunnancommon.service.EnterpriseInfoService;
import com.yunnancommon.service.impl.EnterpriseInfoServiceImpl;
import com.yunnancommon.utils.TokenUtils;
import com.yunnanprovince.config.AppConfig;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotEmpty;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

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

    @PostMapping("/login")
    public ResponseVO login(HttpServletRequest request, HttpServletResponse response, @RequestParam @NotEmpty String username, @RequestParam @NotEmpty String password) throws BusinessException {
        try {
            if (!appconfig.getUsername().equals(username) || !appconfig.getPassword().equals(password)) {
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
    public ResponseVO createAccount(Integer type, EnterpriseInfo enterpriseInfo, Integer cityCode) throws BusinessException {
        if (!AccountTypeEnum.CITY.getCode().equals(type) && !AccountTypeEnum.ENTERPRISE.getCode().equals(type)) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        CreatedAccountVO createdAccountVO = null;
        if (AccountTypeEnum.ENTERPRISE.getCode().equals(type)) {
            createdAccountVO = enterpriseInfoService.createEnterpriseAccount(enterpriseInfo);
        } else {
            createdAccountVO = enterpriseInfoService.createCityAccount(cityCode);
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
    public ResponseVO changeStatus(String username, Integer status) throws BusinessException {
        AccountInfo accountInfo = new AccountInfo();
        accountInfo.setStatus(status);
        accountInfoService.updateAccountInfoByUsername(accountInfo,  username);
        return getSuccessResponseVO(null);
    }
}
package com.yunnanenterprise.controller;

import com.yunnancommon.component.RedisComponent;
import com.yunnancommon.controller.ABaseController;
import com.yunnancommon.entity.constants.Constants;
import com.yunnancommon.entity.vo.ResponseVO;
import com.yunnancommon.entity.vo.TokenInfoVO;
import com.yunnancommon.enums.AccountTypeEnum;
import com.yunnancommon.exception.BusinessException;
import com.yunnancommon.service.AccountInfoService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/account")
public class AccountController extends ABaseController {

    @Resource
    private AccountInfoService accountInfoService;

    @Resource
    private RedisComponent redisComponent;

    @PostMapping("/login")
    public ResponseVO login(HttpServletRequest request, HttpServletResponse response, 
                           @RequestParam("account") String account, 
                           @RequestParam("password") String password) throws BusinessException {
        try {
            TokenInfoVO tokenVO = accountInfoService.login(account, password);
            saveToken2Cookie(response, tokenVO.getToken());
            return getSuccessResponseVO(tokenVO);
        } finally {
            // 清除旧的token
            Cookie[] cookies = request.getCookies();
            if(cookies != null) {
                String token = null;
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals(Constants.TOKEN_KEY)) {
                        token = cookie.getValue();
                    }
                }
                if (!StringUtils.isEmpty(token)) {
                    redisComponent.cleanEnterpriseTokenInfo(token);
                }
            }
        }
    }

    @PostMapping("/logout")
    public ResponseVO logout(HttpServletResponse response) {
        cleanToken(response, AccountTypeEnum.ENTERPRISE);
        return getSuccessResponseVO(null);
    }

    @GetMapping("/getEnterpriseInfo")
    public ResponseVO getEnterpriseInfo(HttpServletRequest request) {
        String token = getTokenFromCookie(request);
        return getSuccessResponseVO(redisComponent.getEnterpriseTokenInfo(token).getEnterpriseInfo());
    }
}
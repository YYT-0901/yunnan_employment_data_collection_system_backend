package com.yunnanprovince.controller;

import com.yunnancommon.component.RedisComponent;
import com.yunnancommon.controller.ABaseController;
import com.yunnancommon.entity.constants.Constants;
import com.yunnancommon.entity.vo.ResponseVO;
import com.yunnancommon.enums.AccountTypeEnum;
import com.yunnancommon.exception.BusinessException;
import com.yunnancommon.utils.TokenUtils;
import com.yunnanprovince.config.AppConfig;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotEmpty;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/account")
public class AccountController extends ABaseController {
    @Resource
    private AppConfig appconfig;

    @Resource
    private RedisComponent redisComponent;

    @PostMapping("/login")
    public ResponseVO login(HttpServletRequest request, HttpServletResponse response, @NotEmpty String username, @NotEmpty String password) throws BusinessException {
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
}

package com.yunnanenterprise.controller;

import com.yunnancommon.component.RedisComponent;
import com.yunnancommon.controller.ABaseController;
import com.yunnancommon.entity.constants.Constants;
import com.yunnancommon.entity.dto.LoginDto;
import com.yunnancommon.entity.vo.ResponseVO;
import com.yunnancommon.entity.vo.TokenInfoVO;
import com.yunnancommon.enums.AccountTypeEnum;
import com.yunnancommon.enums.ResponseCodeEnum;
import com.yunnancommon.exception.BusinessException;
import com.yunnancommon.service.AccountInfoService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/account")
public class AccountController extends ABaseController {

    @Resource
    private AccountInfoService accountInfoService;

    @Resource
    private RedisComponent redisComponent;

    @PostMapping("/login")
    public ResponseVO login(HttpServletRequest request, HttpServletResponse response, @RequestBody LoginDto loginDto) throws BusinessException {
        return doLogin(request, response, loginDto == null ? null : loginDto.getUsername(), loginDto == null ? null : loginDto.getPassword());
    }

    @GetMapping("/login")
    public ResponseVO loginWithQuery(HttpServletRequest request,
                                     HttpServletResponse response,
                                     @RequestParam("username") String username,
                                     @RequestParam("password") String password) throws BusinessException {
        return doLogin(request, response, username, password);
    }

    @PostMapping("/logout")
    public ResponseVO logout(HttpServletResponse response) {
        cleanToken(response, AccountTypeEnum.ENTERPRISE);
        return getSuccessResponseVO(null);
    }

    @GetMapping("/getEnterpriseInfo")
    public ResponseVO getEnterpriseInfo(HttpServletRequest request) throws BusinessException {
        String token = getTokenFromCookie(request);
        if (StringUtils.isEmpty(token)) {
            throw new BusinessException(ResponseCodeEnum.CODE_901);
        }
        TokenInfoVO tokenInfoVO = redisComponent.getEnterpriseTokenInfo(token);
        if (tokenInfoVO == null || tokenInfoVO.getEnterpriseInfo() == null) {
            throw new BusinessException(ResponseCodeEnum.CODE_901);
        }
        return getSuccessResponseVO(tokenInfoVO.getEnterpriseInfo());
    }

    private ResponseVO doLogin(HttpServletRequest request,
                               HttpServletResponse response,
                               String username,
                               String password) throws BusinessException {
        if (StringUtils.isAnyBlank(username, password)) {
            throw new BusinessException("账号或密码不能为空");
        }
        try {
            TokenInfoVO tokenVO = accountInfoService.login(username, password);
            saveToken2Cookie(response, tokenVO.getToken());
            return getSuccessResponseVO(tokenVO);
        } finally {
            cleanOldToken(request);
        }
    }

    private void cleanOldToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return;
        }
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(Constants.TOKEN_KEY)) {
                if (!StringUtils.isEmpty(cookie.getValue())) {
                    redisComponent.cleanEnterpriseTokenInfo(cookie.getValue());
                }
                break;
            }
        }
    }
}

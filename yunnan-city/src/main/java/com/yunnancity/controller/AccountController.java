package com.yunnancity.controller;

import com.yunnancommon.component.RedisComponent;
import com.yunnancommon.controller.ABaseController;
import com.yunnancommon.entity.dto.LoginDto;
import com.yunnancommon.entity.vo.ResponseVO;
import com.yunnancommon.entity.vo.TokenInfoVO;
import com.yunnancommon.enums.AccountTypeEnum;
import com.yunnancommon.exception.BusinessException;
import com.yunnancommon.service.AccountInfoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/account")
public class AccountController extends ABaseController {

    @Resource
    private AccountInfoService accountInfoService;

    @Resource
    private RedisComponent redisComponent;

    @PostMapping("/login")
    public ResponseVO login(HttpServletRequest request, HttpServletResponse response, @RequestBody LoginDto loginDto) throws BusinessException {
        try {
            TokenInfoVO tokenVO = accountInfoService.cityLogin(loginDto.getUsername(), loginDto.getPassword());
            saveToken2Cookie(response, tokenVO.getToken(), AccountTypeEnum.CITY);
            return getSuccessResponseVO(tokenVO);
        } finally {
            String token = getTokenFromCookie(request, AccountTypeEnum.CITY);
            if (!StringUtils.isEmpty(token)) {
                redisComponent.cleanCityTokenInfo(token);
            }
        }
    }

    @PostMapping("/logout")
    public ResponseVO logout(HttpServletResponse response) {
        cleanToken(response, AccountTypeEnum.CITY);
        return getSuccessResponseVO(null);
    }
}

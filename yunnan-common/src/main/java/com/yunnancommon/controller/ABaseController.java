package com.yunnancommon.controller;

import com.yunnancommon.component.RedisComponent;
import com.yunnancommon.entity.constants.Constants;
import com.yunnancommon.enums.AccountTypeEnum;
import com.yunnancommon.enums.ResponseCodeEnum;
import com.yunnancommon.entity.vo.ResponseVO;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;

public class ABaseController {

    @Resource
    private RedisComponent redisComponent;

    protected static final String STATUC_SUCCESS = "success";
    protected static final String STATUC_ERROR = "error";

    protected <T> ResponseVO getSuccessResponseVO(T t) {
        ResponseVO<T> responseVO = new ResponseVO<T>();
        responseVO.setStatus(STATUC_SUCCESS);
        responseVO.setCode(ResponseCodeEnum.CODE_200.getCode());
        responseVO.setInfo(ResponseCodeEnum.CODE_200.getMsg());
        responseVO.setData(t);
        return responseVO;
    }

    protected <T> ResponseVO getErrorResponseVO(T t) {
        ResponseVO<T> responseVO = new ResponseVO<T>();
        responseVO.setStatus(STATUC_ERROR);
        responseVO.setCode(ResponseCodeEnum.CODE_400.getCode());
        responseVO.setInfo(ResponseCodeEnum.CODE_400.getMsg());
        responseVO.setData(t);
        return responseVO;
    }

    protected void saveToken2Cookie(HttpServletResponse response, String token, AccountTypeEnum accountType) {
        Cookie cookie = new Cookie(getCookieName(accountType), token);
        cookie.setMaxAge(Constants.REDIS_KEY_EXPIRES_ONE_DAY / 1000 * 7);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    protected void cleanToken(HttpServletResponse response, AccountTypeEnum accountTypeEnum) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest();
        String token = getTokenFromCookie(request, accountTypeEnum);
        if (token != null) {
            if (accountTypeEnum == AccountTypeEnum.ENTERPRISE) {
                redisComponent.cleanEnterpriseTokenInfo(token);
            } else if (accountTypeEnum == AccountTypeEnum.PROVINCE) {
                redisComponent.cleanProvinceTokenInfo(token);
            } else if (accountTypeEnum == AccountTypeEnum.CITY) {
                redisComponent.cleanCityTokenInfo(token);
            }
        }
        Cookie cookie = new Cookie(getCookieName(accountTypeEnum), "");
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    protected String getTokenFromCookie(HttpServletRequest request, AccountTypeEnum accountTypeEnum) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            String cookieName = getCookieName(accountTypeEnum);
            for (Cookie cookie : cookies) {
                if (cookie.getName().equalsIgnoreCase(cookieName)) {
                    return cookie.getValue();
                }
            }
        }
        
        String token = request.getHeader("token");
        if (token != null && !token.isEmpty()) {
            return token;
        }
        
        String auth = request.getHeader("Authorization");
        if (auth != null && auth.startsWith("Bearer ")) {
            return auth.substring(7);
        }
        
        return null;
    }

    private String getCookieName(AccountTypeEnum accountType) {
        return switch (accountType) {
            case ENTERPRISE -> Constants.TOKEN_KEY_ENTERPRISE;
            case PROVINCE -> Constants.TOKEN_KEY_PROVINCE;
            case CITY -> Constants.TOKEN_KEY_CITY;
        };
    }
}

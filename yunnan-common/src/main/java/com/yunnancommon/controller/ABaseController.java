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

    protected void saveToken2Cookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie(Constants.TOKEN_KEY, token);
        cookie.setMaxAge(Constants.REDIS_KEY_EXPIRES_ONE_DAY / 1000 * 7);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    protected void cleanToken(HttpServletResponse response, AccountTypeEnum accountTypeEnum) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return;
        }
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(Constants.TOKEN_KEY)) {
                if(accountTypeEnum == AccountTypeEnum.ENTERPRISE) {
                    redisComponent.cleanEnterpriseTokenInfo(cookie.getValue());
                } else if(accountTypeEnum == AccountTypeEnum.PROVINCE) {
                    redisComponent.cleanProvinceTokenInfo(cookie.getValue());
                } else if(accountTypeEnum == AccountTypeEnum.CITY) {
                    redisComponent.cleanCityTokenInfo(cookie.getValue());
                }
                cookie.setMaxAge(0);
                cookie.setPath("/");
                response.addCookie(cookie);
                break;
            }
        }
    }

    protected String getTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (cookie.getName().equalsIgnoreCase(Constants.TOKEN_KEY)) {
                return cookie.getValue();
            }
        }
        return null;
    }
}

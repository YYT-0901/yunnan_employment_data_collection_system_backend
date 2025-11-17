package com.yunnancity.interceptor;

import com.yunnancommon.component.RedisComponent;
import com.yunnancommon.entity.constants.Constants;
import com.yunnancommon.entity.vo.TokenInfoVO;
import com.yunnancommon.enums.ResponseCodeEnum;
import com.yunnancommon.exception.BusinessException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AppInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(AppInterceptor.class);

    private final RedisComponent redisComponent;

    public AppInterceptor(RedisComponent redisComponent) {
        this.redisComponent = redisComponent;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 非方法类型直接通过
        if(!(handler instanceof HandlerMethod)) {
            return true;
        }
        // 验证token是否有效
        String token = this.getTokenFromRequest(request);
        if(token == null) {
            throw new BusinessException(ResponseCodeEnum.CODE_901);
        }
        if(StringUtils.isEmpty(token)) {
            throw new BusinessException(ResponseCodeEnum.CODE_901);
        }
        TokenInfoVO cityTokenInfo = redisComponent.getCityTokenInfo(token);
        if(cityTokenInfo == null) {
            throw new BusinessException(ResponseCodeEnum.CODE_901);
        }
        return true;
    }

    private String getTokenFromRequest(HttpServletRequest request) {
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

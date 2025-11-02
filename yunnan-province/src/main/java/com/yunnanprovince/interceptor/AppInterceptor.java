package com.yunnanprovince.interceptor;

import com.yunnancommon.entity.constants.Constants;
import com.yunnancommon.enums.ResponseCodeEnum;
import com.yunnancommon.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;


@Component
public class AppInterceptor implements HandlerInterceptor {

    private final static String URL_ACCOUNT = "/account";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(handler == null) {
            return false;
        }
        if(!(handler instanceof HandlerMethod)) {
            return true;
        }
        // 不拦截登录接口
        if(request.getRequestURI().contains(URL_ACCOUNT)) {
            return true;
        }
        String token = request.getHeader(Constants.TOKEN_ADMIN_HEADER);
        if(StringUtils.isEmpty(token)) {
            throw new BusinessException(ResponseCodeEnum.CODE_901);
        }
        // TODO: 验证token是否有效
        return true;
    }
}

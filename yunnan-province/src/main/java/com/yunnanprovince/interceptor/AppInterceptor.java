package com.yunnanprovince.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yunnancommon.entity.constants.Constants;
import com.yunnancommon.entity.vo.ResponseVO;
import com.yunnancommon.enums.ResponseCodeEnum;
import com.yunnancommon.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import com.yunnancommon.component.RedisComponent;
import jakarta.servlet.http.Cookie;

@Component
public class AppInterceptor implements HandlerInterceptor {

    private final static String URL_ACCOUNT = "/account";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    @jakarta.annotation.Resource
    private RedisComponent redisComponent;

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
            String authHeader = request.getHeader("Authorization");
            if (StringUtils.isNotEmpty(authHeader) && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring("Bearer ".length());
            }
        }
        if(StringUtils.isEmpty(token)) {
            response.resetBuffer();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");

            ResponseVO<String> body = new ResponseVO<>();
            body.setStatus("error");
            body.setCode(ResponseCodeEnum.CODE_901.getCode());
            body.setInfo(ResponseCodeEnum.CODE_901.getMsg());
            body.setData(ResponseCodeEnum.CODE_901.getMsg());

            response.getWriter().write(OBJECT_MAPPER.writeValueAsString(body));
            response.getWriter().flush();
            return false;
        }
        token = redisComponent.getProvinceTokenInfo(token);
        if(token == null) {
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
            if (cookie.getName().equalsIgnoreCase(Constants.TOKEN_KEY_PROVINCE)) {
                return cookie.getValue();
            }
        }
        return null;
    }
}

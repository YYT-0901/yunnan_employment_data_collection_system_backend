package com.yunnanprovince.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yunnancommon.entity.constants.Constants;
import com.yunnancommon.entity.vo.ResponseVO;
import com.yunnancommon.enums.ResponseCodeEnum;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;


@Component
public class AppInterceptor implements HandlerInterceptor {

    private final static String URL_ACCOUNT = "/account";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

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
        // TODO: 验证token是否有效
        return true;
    }
}

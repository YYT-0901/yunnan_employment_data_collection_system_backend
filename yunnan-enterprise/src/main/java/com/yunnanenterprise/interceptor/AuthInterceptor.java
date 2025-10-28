package com.yunnanenterprise.interceptor;

import com.yunnancommon.component.RedisComponent;
import com.yunnancommon.entity.constants.Constants;
import com.yunnancommon.entity.vo.TokenInfoVO;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;

/**
 * 登录验证拦截器
 * 
 * 功能说明：
 * 1. 拦截所有需要登录才能访问的接口
 * 2. 从 Cookie 中获取 token
 * 3. 验证 token 是否有效（是否在 Redis 中存在）
 * 4. 如果未登录，返回 401 错误
 * 
 * 为什么需要这个拦截器？
 * - 统一的登录验证逻辑，不需要在每个 Controller 方法中重复写验证代码
 * - 提高系统安全性，防止未登录用户访问敏感数据
 * - 便于维护，认证逻辑集中在一处
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Resource
    private RedisComponent redisComponent;

    /**
     * 在请求处理之前进行拦截
     * 
     * @param request HTTP 请求
     * @param response HTTP 响应
     * @param handler 处理器
     * @return true 表示继续执行，false 表示拦截请求
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. 从 Cookie 中获取 token
        String token = getTokenFromCookie(request);
        
        // 2. 如果没有 token，说明未登录
        if (StringUtils.isEmpty(token)) {
            sendUnauthorizedResponse(response, "未登录，请先登录");
            return false;
        }
        
        // 3. 从 Redis 中获取 token 信息，验证 token 是否有效
        TokenInfoVO tokenInfo = redisComponent.getEnterpriseTokenInfo(token);
        
        // 4. 如果 token 无效或已过期（Redis 中不存在）
        if (tokenInfo == null) {
            sendUnauthorizedResponse(response, "登录已过期，请重新登录");
            return false;
        }
        
        // 5. token 有效，允许继续访问
        // 可以将用户信息存储到 request 中，供后续使用
        request.setAttribute("enterpriseInfo", tokenInfo.getEnterpriseInfo());
        request.setAttribute("token", token);
        
        return true;
    }
    
    /**
     * 从 Cookie 中获取 token
     * 
     * @param request HTTP 请求
     * @return token 字符串，如果不存在返回 null
     */
    private String getTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        
        for (Cookie cookie : cookies) {
            if (Constants.TOKEN_KEY.equalsIgnoreCase(cookie.getName())) {
                return cookie.getValue();
            }
        }
        
        return null;
    }
    
    /**
     * 发送 401 未授权响应
     * 
     * @param response HTTP 响应
     * @param message 错误消息
     * @throws Exception
     */
    private void sendUnauthorizedResponse(HttpServletResponse response, String message) throws Exception {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
        response.setContentType("application/json;charset=UTF-8");
        
        // 返回 JSON 格式的错误信息
        String jsonResponse = String.format(
            "{\"status\":\"error\",\"code\":401,\"info\":\"%s\",\"data\":null}",
            message
        );
        
        response.getWriter().write(jsonResponse);
        response.getWriter().flush();
    }
}


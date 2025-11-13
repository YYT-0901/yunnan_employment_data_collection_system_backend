package com.yunnanenterprise.config;

import com.yunnanenterprise.interceptor.AuthInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * Web 配置类
 * 
 * 功能说明：
 * 1. 注册拦截器
 * 2. 配置需要拦截的路径
 * 3. 配置不需要拦截的路径（白名单）
 * 
 * 为什么需要这个配置类？
 * - Spring MVC 需要通过配置类来注册自定义的拦截器
 * - 可以灵活配置哪些接口需要登录验证，哪些不需要
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Resource
    private AuthInterceptor authInterceptor;

    /**
     * 添加拦截器配置
     * 
     * @param registry 拦截器注册器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                // 拦截所有 /api/ 开头的接口
                .addPathPatterns("/api/**")
                
                // 排除不需要登录验证的接口（白名单）
                .excludePathPatterns(
                        "/api/account/login",     // 登录接口
                        "/api/account/register",  // 注册接口（如果有）
                        "/api/dictionary/**",     // 字典接口（如果不需要登录）
                        "/api/notice/**",         // 【临时】通知接口（调试用）
                        "/api/report/**",         // 【临时】报表接口（调试用）
                        "/h2-console/**"          // H2 数据库控制台（开发环境）
                );
        
        // 说明：
        // - addPathPatterns: 指定需要拦截的路径模式
        //   ** 表示匹配任意层级的路径
        //   例如：/api/** 会匹配 /api/notice/list, /api/report/save 等
        //
        // - excludePathPatterns: 指定不需要拦截的路径（白名单）
        //   这些路径即使匹配 addPathPatterns，也不会被拦截
        //   通常包括：登录、注册、公开接口等
        //
        // 注意：/api/notice/** 和 /api/report/** 是临时添加的，
        //      用于调试。调试完成后应该删除这两行。
    }
}


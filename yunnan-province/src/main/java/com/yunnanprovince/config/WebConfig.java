package com.yunnanprovince.config;

import com.yunnanprovince.interceptor.AppInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * Web configuration class
 * 
 * Function description:
 * 1. Register interceptors
 * 2. Configure paths to intercept
 * 3. Configure paths that do not need to be intercepted (whitelist)
 * 
 * Why do you need this configuration class?
 * - Spring MVC needs to register custom interceptors through configuration classes
 * - You can flexibly configure which interfaces require login authentication and which do not
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Resource
    private AppInterceptor appInterceptor;

    /**
     * Add interceptor configuration
     * 
     * @param registry Interceptor Registrar
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(appInterceptor)
                // Intercept all /api/ prefixed interfaces
                .addPathPatterns("/**")
                
                // Exclude interfaces that do not require login authentication (whitelist)
                .excludePathPatterns(
                        "/account/login",     // login interface
                        "/account/register"   // registration interface
                );
    }
}
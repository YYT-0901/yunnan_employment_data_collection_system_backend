package com.yunnanprovince.config;

import com.yunnanprovince.interceptor.AppInterceptor;
import jakarta.annotation.Resource;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@Data
public class AppConfig implements WebMvcConfigurer {
    @Value("${admin.username:}")
    private String username;

    @Value("${admin.password:}")
    private String password;

    @Value("${project.xml_folder:./xml}")
    private String xmlFolderPath;  // 添加XML文件父路径字段，默认值为./xml
    @Resource
    private AppInterceptor appInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(appInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/account/login");
    }
}

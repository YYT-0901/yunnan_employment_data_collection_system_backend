package com.yunnanprovince.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class AppConfig {
    @Value("${admin.username:}")
    private String username;

    @Value("${admin.password:}")
    private String password;

    @Value("${project.xml_folder:./xml}")
    private String xmlFolderPath;  // 添加XML文件父路径字段，默认值为./xml
}

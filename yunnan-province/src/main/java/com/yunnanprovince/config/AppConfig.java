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
}

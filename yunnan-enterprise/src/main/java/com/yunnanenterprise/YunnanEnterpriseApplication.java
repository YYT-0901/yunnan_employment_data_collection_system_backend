package com.yunnanenterprise;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.mybatis.spring.annotation.MapperScan;


// 企业端入口：负责启动SpringBoot, 并让他同事扫描enterprise 和 common模块
@SpringBootApplication(scanBasePackages = {"com.yunnanenterprise", "com.yunnancommon"})
@MapperScan("com.yunnancommon.mapper")
public class YunnanEnterpriseApplication {
    public static void main(String[] args){
        SpringApplication.run(YunnanEnterpriseApplication.class, args);
    }
}

package com.yunnanprovince;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.yunnanprovince", "com.yunnancommon"})
@MapperScan("com.yunnancommon.mapper")
public class YunnanProvinceApplication {

	public static void main(String[] args) {
		SpringApplication.run(YunnanProvinceApplication.class, args);
	}

}

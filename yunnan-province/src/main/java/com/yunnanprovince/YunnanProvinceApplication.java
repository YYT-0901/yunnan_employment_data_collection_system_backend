package com.yunnanprovince;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.validation.annotation.Validated;

@SpringBootApplication(scanBasePackages = {"com.yunnanprovince", "com.yunnancommon"})
@MapperScan("com.yunnancommon.mapper")
@Validated
public class YunnanProvinceApplication {

	public static void main(String[] args) {
		SpringApplication.run(YunnanProvinceApplication.class, args);
	}

}

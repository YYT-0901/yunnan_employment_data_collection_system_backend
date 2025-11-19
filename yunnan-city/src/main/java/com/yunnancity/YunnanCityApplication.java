package com.yunnancity;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.validation.annotation.Validated;

@SpringBootApplication(scanBasePackages = {"com.yunnancity", "com.yunnancommon"})
@MapperScan("com.yunnancommon.mapper")
@Validated
public class YunnanCityApplication {

	public static void main(String[] args) {
		SpringApplication.run(YunnanCityApplication.class, args);
	}

}

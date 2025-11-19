package com.yunnanprovince.config;

import com.yunnancommon.service.DataAnalysisService;
import com.yunnancommon.service.DruidQueryService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class TestConfig {
    
    @Bean
    @Primary
    public DataAnalysisService dataAnalysisService() {
        return mock(DataAnalysisService.class);
    }
    
    @Bean
    @Primary  
    public DruidQueryService druidQueryService() {
        return mock(DruidQueryService.class);
    }
}

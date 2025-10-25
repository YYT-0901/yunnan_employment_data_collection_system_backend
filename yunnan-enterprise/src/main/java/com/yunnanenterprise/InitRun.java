package com.yunnanenterprise;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class InitRun implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(InitRun.class);
    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("项目启动成功,开启愉快的开发之旅吧!(卓越2组加油)");
    }
}

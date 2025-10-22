package com.yunnancity;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class InitRun implements ApplicationRunner {
    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("项目启动成功,开启愉快的开发之旅吧!(卓越2组加油)");
    }
}

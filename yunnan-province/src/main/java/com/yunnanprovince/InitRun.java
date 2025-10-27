package com.yunnanprovince;

import com.yunnancommon.redis.RedisUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.SQLException;

@Slf4j
@Component
public class InitRun implements ApplicationRunner {

    @Resource
    private DataSource dataSource;

    @Resource
    private RedisUtils redisUtils;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        try {
            dataSource.getConnection();
            redisUtils.get("test");
            log.info("项目启动成功,开启愉快的开发之旅吧!(卓越2组加油)");
        } catch (SQLException e) {
            log.error("数据库连接失败，请检查数据库配置！");
        } catch (RedisConnectionFailureException e) {
            log.error("Redis连接失败，请检查Redis配置！");
        } catch (Exception e) {
            log.error("服务器启动失败，错误信息:", e);
        }
    }
}

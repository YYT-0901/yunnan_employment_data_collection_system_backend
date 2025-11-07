package com.yunnancommon.component;

import com.yunnancommon.entity.constants.Constants;
import com.yunnancommon.entity.vo.TokenInfoVO;
import com.yunnancommon.redis.RedisUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class RedisComponent {

    @Resource
    private RedisUtils redisUtils;

    public void saveEnterpriseTokenInfo(TokenInfoVO tokenInfoVO) {
        redisUtils.setex(Constants.REDIS_KEY_TOKEN_ENTERPRISE + tokenInfoVO.getToken(), tokenInfoVO, Constants.REDIS_KEY_EXPIRES_ONE_DAY);
    }

    public TokenInfoVO getEnterpriseTokenInfo(String token) {
        return (TokenInfoVO) redisUtils.get(Constants.REDIS_KEY_TOKEN_ENTERPRISE + token);
    }

    public void cleanEnterpriseTokenInfo(String token) {
        redisUtils.delete(Constants.REDIS_KEY_TOKEN_ENTERPRISE + token);
    }

    public void saveProvinceTokenInfo(String token) {
        redisUtils.setex(Constants.REDIS_KEY_TOKEN_PROVINCE + token, token, Constants.REDIS_KEY_EXPIRES_ONE_DAY);
    }

    public String getProvinceTokenInfo(String token) {
        return (String) redisUtils.get(Constants.REDIS_KEY_TOKEN_PROVINCE + token);
    }

    public void cleanProvinceTokenInfo(String token) {
        redisUtils.delete(Constants.REDIS_KEY_TOKEN_PROVINCE + token);
    }

    public void cleanCityTokenInfo(String token) {
        redisUtils.delete(Constants.REDIS_KEY_TOKEN_CITY + token);
    }
}
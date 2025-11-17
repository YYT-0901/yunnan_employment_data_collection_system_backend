package com.yunnancommon.component;

import com.yunnancommon.entity.constants.Constants;
import com.yunnancommon.entity.vo.TokenInfoVO;
import com.yunnancommon.redis.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class RedisComponent {

    @Resource
    private RedisUtils redisUtils;

    private static final long TOKEN_TTL = Constants.REDIS_KEY_EXPIRES_ONE_DAY.longValue();
    private final Map<String, CacheEntry<TokenInfoVO>> enterpriseTokenFallback = new ConcurrentHashMap<>();
    private final Map<String, CacheEntry<String>> provinceTokenFallback = new ConcurrentHashMap<>();
    private final Map<String, CacheEntry<String>> cityTokenFallback = new ConcurrentHashMap<>();

    public void saveEnterpriseTokenInfo(TokenInfoVO tokenInfoVO) {
        String key = Constants.REDIS_KEY_TOKEN_ENTERPRISE + tokenInfoVO.getToken();
        boolean success = false;
        boolean loggedFailure = false;
        try {
            success = redisUtils.setex(key, tokenInfoVO, Constants.REDIS_KEY_EXPIRES_ONE_DAY);
        } catch (Exception e) {
            log.warn("Redis save enterprise token failed, fallback to local cache. key={}", key, e);
            loggedFailure = true;
        }
        if (success) {
            enterpriseTokenFallback.remove(key);
        } else {
            if (!loggedFailure) {
                log.warn("Redis unavailable, enterprise token stored in local cache. key={}", key);
            }
            enterpriseTokenFallback.put(key, new CacheEntry<>(tokenInfoVO, TOKEN_TTL));
        }
    }

    public TokenInfoVO getEnterpriseTokenInfo(String token) {
        String key = Constants.REDIS_KEY_TOKEN_ENTERPRISE + token;
        try {
            TokenInfoVO value = (TokenInfoVO) redisUtils.get(key);
            if (value != null) {
                return value;
            }
        } catch (Exception e) {
            log.warn("Redis read enterprise token failed, fallback to local cache. key={}", key, e);
        }
        return getFromFallback(enterpriseTokenFallback, key);
    }

    public void cleanEnterpriseTokenInfo(String token) {
        String key = Constants.REDIS_KEY_TOKEN_ENTERPRISE + token;
        try {
            redisUtils.delete(key);
        } catch (Exception e) {
            log.warn("Redis clean enterprise token failed. key={}", key, e);
        } finally {
            enterpriseTokenFallback.remove(key);
        }
    }

    public void saveProvinceTokenInfo(String token) {
        String key = Constants.REDIS_KEY_TOKEN_PROVINCE + token;
        boolean success = false;
        boolean loggedFailure = false;
        try {
            success = redisUtils.setex(key, token, Constants.REDIS_KEY_EXPIRES_ONE_DAY);
        } catch (Exception e) {
            log.warn("Redis save province token failed, fallback to local cache. key={}", key, e);
            loggedFailure = true;
        }
        if (success) {
            provinceTokenFallback.remove(key);
        } else {
            if (!loggedFailure) {
                log.warn("Redis unavailable, province token stored in local cache. key={}", key);
            }
            provinceTokenFallback.put(key, new CacheEntry<>(token, TOKEN_TTL));
        }
    }

    public String getProvinceTokenInfo(String token) {
        String key = Constants.REDIS_KEY_TOKEN_PROVINCE + token;
        try {
            return (String) redisUtils.get(key);
        } catch (Exception e) {
            log.warn("Redis read province token failed, fallback to local cache. key={}", key, e);
        }
        return getFromFallback(provinceTokenFallback, key);
    }

    public void cleanProvinceTokenInfo(String token) {
        String key = Constants.REDIS_KEY_TOKEN_PROVINCE + token;
        try {
            redisUtils.delete(key);
        } catch (Exception e) {
            log.warn("Redis clean province token failed. key={}", key, e);
        } finally {
            provinceTokenFallback.remove(key);
        }
    }

    public void cleanCityTokenInfo(String token) {
        String key = Constants.REDIS_KEY_TOKEN_CITY + token;
        try {
            redisUtils.delete(key);
        } catch (Exception e) {
            log.warn("Redis clean city token failed. key={}", key, e);
        } finally {
            cityTokenFallback.remove(key);
        }
    }

    private <T> T getFromFallback(Map<String, CacheEntry<T>> cache, String key) {
        CacheEntry<T> entry = cache.get(key);
        if (entry == null) {
            return null;
        }
        if (entry.isExpired()) {
            cache.remove(key);
            return null;
        }
        return entry.value;
    }

    private static final class CacheEntry<T> {
        private final T value;
        private final long expireAt;

        private CacheEntry(T value, long ttlMillis) {
            this.value = value;
            this.expireAt = System.currentTimeMillis() + ttlMillis;
        }

        private boolean isExpired() {
            return System.currentTimeMillis() > expireAt;
        }
    }
}

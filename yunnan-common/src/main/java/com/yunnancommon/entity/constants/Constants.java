package com.yunnancommon.entity.constants;

public class Constants {
    public static final String TOKEN_ADMIN_HEADER = "X-Token-Admin";
    public static final String TOKEN_KEY = "token";
    public static final Integer REDIS_KEY_EXPIRES_ONE_MIN = 60000;
    public static final Integer REDIS_KEY_EXPIRES_ONE_DAY = REDIS_KEY_EXPIRES_ONE_MIN * 1440;

    public static final String REDIS_KEY_PREFIX = "yunnan:";
    public static final String REDIS_KEY_TOKEN_ENTERPRISE = REDIS_KEY_PREFIX + "token:enterprise:";
    public static final String REDIS_KEY_TOKEN_PROVINCE = REDIS_KEY_PREFIX + "token:province:";
    public static final String REDIS_KEY_TOKEN_CITY = REDIS_KEY_PREFIX + "token:city:";
}

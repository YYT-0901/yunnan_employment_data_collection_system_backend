package com.yunnancommon.enums;

public enum AccountTypeEnum {
    PROVINCE(0, "省级账号"),
    CITY(2, "市级账号"),
    ENTERPRISE(1, "企业账号");

    private Integer code;
    private String name;

    AccountTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}

package com.yunnancommon.enums;

public enum AccountStatusEnum {
    ENABLE(0, "启用"),
    DISABLE(1, "禁用");

    private Integer code;
    private String message;

    AccountStatusEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}

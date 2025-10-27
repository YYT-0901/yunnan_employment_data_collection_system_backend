package com.yunnancommon.enums;

public enum EnterpriseStatusEnum {
    CREATED(0, "创建未备案"),
    FILED(1, "已备案未审核"),
    REFUND(2, "已退回"),
    NORMAL(3, "正常"),
    STOP(4, "停业");

    private Integer code;
    private String name;

    EnterpriseStatusEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static String getNameByCode(Integer code) {
        for (EnterpriseStatusEnum value : values()) {
            if (value.getCode().equals(code)) {
                return value.getName();
            }
        }
        return null;
    }
}

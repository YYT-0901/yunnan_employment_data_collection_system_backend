package com.yunnancommon.enums;

public enum NoticeTypeEnum {
    ALL(1, "全部人可见"),
    COMPANY(2, "企业可见"),
    CITY(3, "市可见"),
    PROVINCE(4, "省可见");

    private final Integer code;
    private final String info;

    NoticeTypeEnum(Integer code, String info) {
        this.code = code;
        this.info = info;
    }

    public Integer getCode() {
        return code;
    }
    public String getInfo() {
        return info;
    }
}

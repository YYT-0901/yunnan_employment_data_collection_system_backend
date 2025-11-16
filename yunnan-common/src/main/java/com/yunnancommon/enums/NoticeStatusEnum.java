package com.yunnancommon.enums;

public enum NoticeStatusEnum {
    DELETED(0, "删除"),
    NORMAL(1, "正常"),
    DRAFT(2, "草稿");

    private final Integer code;
    private final String info;

    NoticeStatusEnum(Integer code, String info) {
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

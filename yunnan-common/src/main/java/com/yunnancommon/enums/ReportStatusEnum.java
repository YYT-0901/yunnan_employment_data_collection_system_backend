package com.yunnancommon.enums;

public enum ReportStatusEnum {
    NOT_SUBMITTED(-1, "未填报"),
    STAGED(0, "已暂存"),
    CITY_AUDITING(1, "待市级审核"),
    PROVINCE_AUDITING(2, "待省级审核"),
    APPROVED(3, "审核通过"),
    ARCHIVED(4, "已归档"),
    REJECTED(5, "已驳回");

    private final Integer code;
    private final String info;

    ReportStatusEnum(Integer code, String info) {
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

package com.yunnancommon.enums;

public enum ReportAuditLevelEnum {
    /**
     * 省级审核
     */
    PROVINCIAL(2, "省级审核"),
    /**
     * 市级审核
     */
    CITY(1, "市级审核");

    private final Integer code;
    private final String desc;

    ReportAuditLevelEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

     public String getDesc() {
        return desc;
    }
}

package com.yunnancommon.enums;

public enum ReportAuditResult {
    /**
     * 审核通过
     */
    APPROVED(1, "通过"),
    /**
     * 审核拒绝
     */
    REJECTED(2, "驳回");

    private final Integer code;
    private final String desc;

    ReportAuditResult(Integer code, String desc) {
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

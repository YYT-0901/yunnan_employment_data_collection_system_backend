package com.yunnanenterprise.enums;

public enum ReportStatusEnum {
    
    NOT_FILLED(-1, "未填报"),
    DRAFT(0, "已暂存"),
    PENDING_CITY_REVIEW(1, "待市级审核"),
    PENDING_PROVINCE_REVIEW(2, "待省级审核"),
    APPROVED(3, "审核通过"),
    ARCHIVED(4, "已归档"),
    REJECTED(5, "驳回");
    
    private final Integer code;
    private final String desc;
    
    ReportStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    /**
     * 根据 code 获取枚举
     * @param code 状态码
     * @return 对应的枚举，如果不存在返回 null
     */
    public static ReportStatusEnum getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (ReportStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
    
    /**
     * 判断该状态是否可以编辑
     * 业务规则：只有"未填报"、"已暂存"、"驳回"状态可以编辑
     */
    public boolean canEdit() {
        return this == NOT_FILLED || this == DRAFT || this == REJECTED;
    }
    
    /**
     * 判断该状态是否可以提交
     * 业务规则：只有"未填报"、"已暂存"、"驳回"状态可以提交
     */
    public boolean canSubmit() {
        return this == NOT_FILLED || this == DRAFT || this == REJECTED;
    }
    
    public Integer getCode() {
        return code;
    }
    
    public String getDesc() {
        return desc;
    }
}
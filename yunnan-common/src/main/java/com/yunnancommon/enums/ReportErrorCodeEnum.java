package com.yunnancommon.enums;

public enum ReportErrorCodeEnum {
    
    // 调查期相关错误 (E-PERIOD-xxx)
    E_PERIOD_001("E-PERIOD-001", "该调查期已存在"),
    E_PERIOD_002("E-PERIOD-002", "开始时间必须早于结束时间"),
    E_PERIOD_003("E-PERIOD-003", "调查期不存在"),
    
    // 窗口时间相关错误 (E-WIN-xxx)
    E_WIN_001("E-WIN-001", "填报尚未开始"),
    E_WIN_002("E-WIN-002", "填报已截止"),
    
    // 权限相关错误 (E-AUTH-xxx)
    E_AUTH_001("E-AUTH-001", "无权操作此报表"),
    E_AUTH_002("E-AUTH-002", "无权查看/审核此报表"),
    E_AUTH_003("E-AUTH-003", "未登录或登录已过期"),
    
    // 状态相关错误 (E-STATUS-xxx)
    E_STATUS_001("E-STATUS-001", "报表已提交或审核中，无法修改"),
    E_STATUS_002("E-STATUS-002", "报表状态不允许提交"),
    E_STATUS_003("E-STATUS-003", "报表状态不是待市级审核"),
    E_STATUS_004("E-STATUS-004", "报表状态不是待省级审核"),
    E_STATUS_005("E-STATUS-005", "只有审核通过的报表才能归档"),
    E_STATUS_006("E-STATUS-006", "报表未被驳回，无需重新提交"),
    
    // 报表相关错误 (E-REPORT-xxx)
    E_REPORT_001("E-REPORT-001", "报表不存在"),
    E_REPORT_002("E-REPORT-002", "报表数据不完整"),
    
    // 数据校验错误 (E-VALIDATE-xxx)
    E_VALIDATE_001("E-VALIDATE-001", "数据校验失败"),
    E_VALIDATE_002("E-VALIDATE-002", "退回原因摘要不能超过255字符"),
    E_VALIDATE_003("E-VALIDATE-003", "建档期就业人数为必填项"),
    E_VALIDATE_004("E-VALIDATE-004", "调查期就业人数为必填项"),
    E_VALIDATE_005("E-VALIDATE-005", "调查期就业人数不能大于建档期就业人数"),
    E_VALIDATE_006("E-VALIDATE-006", "OTHER类型必须填写详细说明");

    private final String code;
    private final String message;

    ReportErrorCodeEnum(String code, String message){
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage(){
        return message;
    }
}

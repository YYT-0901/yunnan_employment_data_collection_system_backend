package com.yunnanenterprise.service.report;

/**
 * 把 YYYY-MM 与 yyyymm 整数互转。
 * 选择 yyyymm 是因为 period 粒度是“月”，int 存储简单且唯一。
 */
public final class PeriodUtils {
    private PeriodUtils() {}

    public static Integer toPeriodId(String yyyyMm) {
        if (yyyyMm == null || yyyyMm.length() != 7 || yyyyMm.charAt(4) != '-') {
            throw new IllegalArgumentException("reporting_period 格式必须是 YYYY-MM");
        }
        String compact = yyyyMm.substring(0, 4) + yyyyMm.substring(5, 7);
        return Integer.parseInt(compact);
    }

    public static String fromPeriodId(Integer periodId) {
        if (periodId == null) return null;
        String s = String.valueOf(periodId);
        if (s.length() != 6) return s;
        return s.substring(0, 4) + "-" + s.substring(4, 6);
    }
}
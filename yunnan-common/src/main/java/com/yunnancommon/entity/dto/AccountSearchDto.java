package com.yunnancommon.entity.dto;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.YearMonth;

/**
 * 查询和导出账号的过滤条件
 */
@Data
public class AccountSearchDto {
    // 文本
    private String username;       // 登录账号（精确）
    private String usernameFuzzy;  // 登录账号（模糊）
    private String unitName;       // 单位名称（精确）
    private String unitNameFuzzy;  // 单位名称（模糊）

    // 枚举/选择
    private Integer userType;      // 用户类型
    private Integer cityCode;      // 所属地市
    private Integer countyCode;    // 所属市县/区县
    private Integer areaCode;      // 所处区域（如有单独维度）
    private Integer dataStatus;    // 数据状态（账号或企业状态）
    private Integer unitNature;    // 单位性质
    private Integer industry;      // 所属行业

    // 日期/期间（互斥）
    private String startDate;      // yyyy-MM-dd
    private String endDate;        // yyyy-MM-dd
    private String statMonth;      // yyyy-MM
    private String statQuarter;    // yyyy-Q1..Q4

    // 分页与排序
    private Integer pageNo = 1;
    private Integer pageSize = 15;
    private String orderBy;

    public void validate() {
        boolean hasRange = StringUtils.isNotBlank(startDate) || StringUtils.isNotBlank(endDate);
        boolean hasMonth = StringUtils.isNotBlank(statMonth);
        boolean hasQuarter = StringUtils.isNotBlank(statQuarter);

        if ((hasRange && hasMonth) || (hasRange && hasQuarter) || (hasMonth && hasQuarter)) {
            throw new IllegalArgumentException("日期范围、统计月份、统计季度互斥，只能选一种");
        }

        if (StringUtils.isNotBlank(startDate) && StringUtils.isNotBlank(endDate)) {
            LocalDate s = LocalDate.parse(startDate);
            LocalDate e = LocalDate.parse(endDate);
            if (e.isBefore(s)) {
                throw new IllegalArgumentException("结束日期不能早于起始日期");
            }
        }

        if (hasMonth) {
            try {
                YearMonth.parse(statMonth);
            } catch (Exception ex) {
                throw new IllegalArgumentException("统计月份格式应为 yyyy-MM");
            }
        }

        if (hasQuarter && !statQuarter.matches("\\d{4}-Q[1-4]")) {
            throw new IllegalArgumentException("统计季度格式应为 yyyy-Q1..Q4");
        }

        if (pageNo == null || pageNo < 1) {
            pageNo = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 15;
        }
        if (pageSize > 200) {
            pageSize = 200;
        }
    }
}

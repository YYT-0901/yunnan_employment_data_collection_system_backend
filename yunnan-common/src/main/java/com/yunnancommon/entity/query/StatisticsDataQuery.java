package com.yunnancommon.entity.query;

import lombok.Data;

import java.util.List;

@Data
public class StatisticsDataQuery {
    private Long periodId;
    private Integer enterpriseRegion;
    private List<Integer> status;
}

package com.yunnancommon.entity.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnalysisQueryDto {
    private List<Long> periodIds;
    private List<Integer> regions;
    private List<Integer> industries;
    private List<Integer> natures;
    private String groupBy;
    // only the status is 3,4 can be statistics
    private List<Integer> statuses;
}

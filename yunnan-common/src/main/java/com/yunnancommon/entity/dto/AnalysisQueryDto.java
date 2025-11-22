package com.yunnancommon.entity.dto;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnalysisQueryDto {
    @NotEmpty(message = "periodIds不能为空")
    private List<Long> periodIds;
    
    private List<Integer> regions;
    private List<Integer> industries;
    private List<Integer> natures;
    
    // groupBy 只能是 region、nature、industry 或空字符串
    @Pattern(regexp = "^(region|nature|industry)?$", message = "groupBy 必须是 region、nature、industry 之一或为空")
    private String groupBy;
    
    // only the status is 4 (archived) can be statistics
    private List<Integer> statuses;
}

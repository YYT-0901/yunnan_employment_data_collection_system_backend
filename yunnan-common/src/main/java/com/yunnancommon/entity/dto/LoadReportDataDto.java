package com.yunnancommon.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoadReportDataDto {
    private Integer page = 1;
    private Integer pageSize = 10;
    private String enterpriseName;
    private Integer industry;
    private Integer nature;
    private Integer periodId;
    private Integer region;
    private Integer status;
}

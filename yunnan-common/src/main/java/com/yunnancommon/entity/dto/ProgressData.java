package com.yunnancommon.entity.dto;

import lombok.Data;

@Data
public class ProgressData {
    private String city;
    private Integer value;
    private Integer total;
    private Double percentage;
}

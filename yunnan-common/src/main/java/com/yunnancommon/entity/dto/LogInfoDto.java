package com.yunnancommon.entity.dto;

import lombok.Data;

import java.util.Date;

@Data
public class LogInfoDto {
    private Integer page;
    private Integer pageSize;
    private String username;
    private Integer userType;
    private String operationModule;
    private String startTime;
    private String endTime;
    private Integer ResponseStatus;
}

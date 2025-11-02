package com.yunnancommon.entity.dto;

import com.yunnancommon.entity.po.EnterpriseInfo;
import lombok.Data;

@Data
public class CreateAccountDto {
    private Integer type;
    private EnterpriseInfo enterpriseInfo;
    private Integer cityCode;
}

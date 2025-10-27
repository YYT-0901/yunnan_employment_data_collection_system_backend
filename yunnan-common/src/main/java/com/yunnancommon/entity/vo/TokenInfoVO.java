package com.yunnancommon.entity.vo;

import com.yunnancommon.entity.po.EnterpriseInfo;
import lombok.Data;

@Data
public class TokenInfoVO {
    private String token;
    private EnterpriseInfo enterpriseInfo;
}

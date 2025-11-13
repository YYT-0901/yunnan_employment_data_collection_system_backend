package com.yunnancommon.entity.vo;

import com.yunnancommon.entity.po.EnterpriseInfo;
import lombok.Data;

/**
 * Token 信息返回对象
 * 用于登录接口返回给前端
 */
@Data
public class TokenInfoVO {
    /**
     * 登录令牌
     */
    private String token;
    
    /**
     * 用户名（账号名）
     */
    private String username;
    
    /**
     * 企业信息
     */
    private EnterpriseInfo enterpriseInfo;
}

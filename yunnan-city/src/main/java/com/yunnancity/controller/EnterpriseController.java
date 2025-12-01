package com.yunnancity.controller;

import com.yunnancommon.component.RedisComponent;
import com.yunnancommon.controller.ABaseController;
import com.yunnancommon.entity.po.EnterpriseInfo;
import com.yunnancommon.entity.query.EnterpriseInfoQuery;
import com.yunnancommon.entity.vo.ResponseVO;
import com.yunnancommon.enums.AccountTypeEnum;
import com.yunnancommon.exception.BusinessException;
import com.yunnancommon.service.EnterpriseInfoService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;

@RestController
@RequestMapping("/account")
public class EnterpriseController extends ABaseController {

    @Resource
    private EnterpriseInfoService enterpriseInfoService;
    @Autowired
    private RedisComponent redisComponent;

    /**
     * 加载所有企业列表
     */
    @GetMapping("/loadAllEnterprise")
    public ResponseVO loadAllEnterprise(HttpServletRequest request, EnterpriseInfoQuery query) {
        // 按市级账号所在城市过滤企业。企业表的region字段是区县编码，regionCode才是市级编码
        query.setRegionCode(redisComponent.getCityTokenInfo(getTokenFromCookie(request, AccountTypeEnum.CITY)).getCityCode());
        return getSuccessResponseVO(enterpriseInfoService.findListByPage(query));
    }

    /**
     * 根据企业ID获取企业信息
     */
    @GetMapping("/getEnterprise/{enterpriseId}")
    public ResponseVO getEnterprise(@PathVariable String enterpriseId) {
        EnterpriseInfo enterpriseInfo = enterpriseInfoService.getEnterpriseInfoByEnterpriseId(enterpriseId);
        return getSuccessResponseVO(enterpriseInfo);
    }
}

package com.yunnancity.controller;

import com.yunnancommon.component.RedisComponent;
import com.yunnancommon.controller.ABaseController;
import com.yunnancommon.entity.vo.ResponseVO;
import com.yunnancommon.entity.vo.TokenInfoVO;
import com.yunnancommon.enums.AccountTypeEnum;
import com.yunnancommon.exception.BusinessException;
import com.yunnancommon.service.OverviewService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/overview")
public class OverviewController extends ABaseController {

    @Resource
    private OverviewService overviewService;
    @Autowired
    private RedisComponent redisComponent;

    @GetMapping("/getStatisticsData")
    public ResponseVO getStatisticsData(HttpServletRequest request) throws BusinessException {
        TokenInfoVO cityTokenInfo = redisComponent.getCityTokenInfo(getTokenFromCookie(request, AccountTypeEnum.CITY));
        return getSuccessResponseVO(overviewService.getCityStatisticsData(cityTokenInfo.getCityCode()));
    }
}

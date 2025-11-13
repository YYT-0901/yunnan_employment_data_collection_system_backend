package com.yunnanprovince.controller;

import com.yunnancommon.controller.ABaseController;
import com.yunnancommon.entity.vo.ResponseVO;
import com.yunnancommon.exception.BusinessException;
import com.yunnancommon.service.OverviewService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/overview")
public class OverviewController extends ABaseController {

    @Resource
    private OverviewService overviewService;

    @GetMapping("/getStatisticsData")
    public ResponseVO getStatisticsData() throws BusinessException {
        return getSuccessResponseVO(overviewService.getStatisticsData());
    }
}

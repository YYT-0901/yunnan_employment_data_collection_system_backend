
package com.yunnanprovince.controller;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yunnancommon.controller.ABaseController;
import com.yunnancommon.entity.dto.AnalysisQueryDto;
import com.yunnancommon.entity.vo.ResponseVO;
import com.yunnancommon.service.DataAnalysisService;

@RestController
@RequestMapping("/dataAnalysis")
public class DataAnalysisController extends ABaseController {

    private static final Logger logger = LoggerFactory.getLogger(DataAnalysisController.class);

    @Resource
    private DataAnalysisService dataAnalysisService;

    // 取样分析API
    @PostMapping("/sampling")
    public ResponseVO sampling(@RequestBody AnalysisQueryDto query) {
        try {
            logger.info("收到取样分析请求，参数：{}", query);
            return getSuccessResponseVO(dataAnalysisService.getSamplingAnalysis(query));
        } catch (Exception e) {
            logger.error("取样分析失败", e);
            return getErrorResponseVO(e.getMessage());
        }
    }

    // 对比分析API
    @PostMapping("/comparison")
    public ResponseVO comparison(@RequestBody AnalysisQueryDto query) {
        try {
            logger.info("收到对比分析请求，参数：{}", query);

            // 参数验证
            if (query.getPeriodIds() == null || query.getPeriodIds().size() != 2) {
                return getErrorResponseVO("对比分析需要选择2个调查期");
            }

            if (query.getGroupBy() == null || query.getGroupBy().isEmpty()) {
                return getErrorResponseVO("对比分析需要指定分组维度");
            }

            return getSuccessResponseVO(dataAnalysisService.getComparisonAnalysis(query));

        } catch (Exception e) {
            logger.error("对比分析失败", e);
            return getErrorResponseVO(e.getMessage());
        }
    }

    // 趋势分析API
    @PostMapping("/trend")
    public ResponseVO trend(@RequestBody AnalysisQueryDto query) {
        try {
            logger.info("收到趋势分析请求，参数：{}", query);
            return getSuccessResponseVO(dataAnalysisService.getTrendAnalysis(query));
        } catch (Exception e) {
            logger.error("趋势分析失败", e);
            return getErrorResponseVO(e.getMessage());
        }
    }
}

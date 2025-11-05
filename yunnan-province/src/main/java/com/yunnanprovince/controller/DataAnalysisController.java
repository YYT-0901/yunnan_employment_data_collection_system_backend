
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
import com.yunnancommon.service.DruidQueryService;

@RestController
@RequestMapping("/dataAnalysis")
public class DataAnalysisController extends ABaseController {

    private static final Logger logger = LoggerFactory.getLogger(DataAnalysisController.class);

    @Resource
    private DataAnalysisService dataAnalysisService;
    
    @Resource
    private DruidQueryService druidQueryService;

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
    
    // ========== Phase 5 新增：多维分析API ==========
    
    /**
     * 多维分析接口（用于 3D 可视化）
     * 
     * POST /api/dataAnalysis/multiDimensional
     * 
     * 请求体示例：
     * {
     *   "periodIds": [1, 2],           // 可选
     *   "regions": [1, 2, 3],          // 可选，一级地区代码
     *   "industries": [1, 3, 5],       // 可选，一级行业代码
     *   "natures": [1, 2]              // 可选，一级性质代码
     * }
     * 
     * 返回数据示例：
     * {
     *   "status": "success",
     *   "data": [
     *     {
     *       "regionCode": 1,
     *       "regionName": "临沧市",
     *       "industryCode": 3,
     *       "industryName": "制造业",
     *       "natureCode": 1,
     *       "natureName": "国有企业",
     *       "enterpriseCount": 150,
     *       "unemploymentRate": 3.2
     *     },
     *     ...
     *   ]
     * }
     */
    @PostMapping("/multiDimensional")
    public ResponseVO multiDimensional(@RequestBody AnalysisQueryDto query) {
        try {
            logger.info("收到多维分析请求，参数：{}", query);
            
            // 调用 DruidQueryService 获取多维数据
            // 注意：这里直接使用 DruidQueryService，与现有的 dataAnalysisService 并存
            return getSuccessResponseVO(druidQueryService.getMultiDimensionalData(
                query.getPeriodIds(),
                query.getRegions(),
                query.getIndustries(),
                query.getNatures()
            ));
            
        } catch (Exception e) {
            logger.error("多维分析失败", e);
            return getErrorResponseVO(e.getMessage());
        }
    }
}

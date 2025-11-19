package com.yunnancity.controller;

import com.yunnancommon.component.RedisComponent;
import com.yunnancommon.controller.ABaseController;
import com.yunnancommon.entity.dto.EnterpriseInfoReportDto;
import com.yunnancommon.entity.dto.LoadReportDataDto;
import com.yunnancommon.entity.po.EnterpriseReportInfo;
import com.yunnancommon.entity.query.EnterpriseReportInfoQuery;
import com.yunnancommon.entity.vo.ReportInfoDetailVO;
import com.yunnancommon.entity.vo.ResponseVO;
import com.yunnancommon.enums.ReportStatusEnum;
import com.yunnancommon.service.EnterpriseReportInfoService;
import com.yunnancommon.service.ReportInfoService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;


/**
 * 省级审核功能控制器
 */
@RestController
@RequestMapping("/audit")
public class AuditController extends ABaseController {

    @Resource
    private EnterpriseReportInfoService enterpriseReportInfoService;
    
    @Resource
    private ReportInfoService reportInfoService;
    @Autowired
    private RedisComponent redisComponent;

    @PostMapping("/loadDataList")
    public ResponseVO loadDataList(HttpServletRequest request, @RequestBody LoadReportDataDto loadReportDataDto)  {
        EnterpriseReportInfoQuery query = new EnterpriseReportInfoQuery();
        query.setEnterpriseRegion(redisComponent.getCityTokenInfo(getTokenFromCookie(request)).getCityCode());
        query.setPageNo(loadReportDataDto.getPage());
        query.setPageSize(loadReportDataDto.getPageSize());
        query.setEnterpriseIndustry(loadReportDataDto.getIndustry());
        query.setEnterpriseNature(loadReportDataDto.getNature());
        query.setPeriodId(loadReportDataDto.getPeriodId());
        query.setEnterpriseNameFuzzy(loadReportDataDto.getEnterpriseName());
        query.setStatus(loadReportDataDto.getStatus());
        return getSuccessResponseVO(enterpriseReportInfoService.findListByPageWithAssociatedEnterpriseName(query));
    }

    @PostMapping("/approve")
    public ResponseVO approve(@RequestBody EnterpriseInfoReportDto enterpriseInfoReportDto) {
        EnterpriseReportInfo enterpriseReportInfo = new EnterpriseReportInfo();
        enterpriseReportInfo.setStatus(ReportStatusEnum.PROVINCE_AUDITING.getCode());
        enterpriseReportInfoService.updateEnterpriseReportInfoByEnterpriseIdAndPeriodIdAndReportId(
                enterpriseReportInfo,
                enterpriseInfoReportDto.getEnterpriseId(),
                enterpriseInfoReportDto.getPeriodId(),
                enterpriseInfoReportDto.getReportId()
        );
        return getSuccessResponseVO(null);
    }

    @PostMapping("/reject")
    public ResponseVO reject(@RequestBody EnterpriseInfoReportDto enterpriseInfoReportDto) {
        EnterpriseReportInfo enterpriseReportInfo = new EnterpriseReportInfo();
        enterpriseReportInfo.setStatus(ReportStatusEnum.REJECTED.getCode());
        enterpriseReportInfo.setReasonReturn(enterpriseInfoReportDto.getRejectReason());
        enterpriseReportInfoService.updateEnterpriseReportInfoByEnterpriseIdAndPeriodIdAndReportId(
                enterpriseReportInfo,
                enterpriseInfoReportDto.getEnterpriseId(),
                enterpriseInfoReportDto.getPeriodId(),
                enterpriseInfoReportDto.getReportId()
        );
        return getSuccessResponseVO(null);
    }

    // ... existing code ...
    @GetMapping("/{reportId}/detail")
    public ResponseVO getReportDetail(@PathVariable("reportId") String reportId) {
        // 使用ReportInfoService获取包含企业名称和调查期时间的报表详情
        ReportInfoDetailVO reportDetail = reportInfoService.getReportInfoDetailByReportId(reportId);
        if (reportDetail == null) {
            return getErrorResponseVO("未找到该报表详情");
        }
        return getSuccessResponseVO(reportDetail);
    }
}
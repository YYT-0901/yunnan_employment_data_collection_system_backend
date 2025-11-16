package com.yunnanprovince.controller;

import com.yunnancommon.component.RedisComponent;
import com.yunnancommon.controller.ABaseController;
import com.yunnancommon.entity.dto.EnterpriseInfoReportDto;
import com.yunnancommon.entity.dto.LoadReportDataDto;
import com.yunnancommon.entity.po.EnterpriseReportInfo;
import com.yunnancommon.entity.po.PeriodInfo;
import com.yunnancommon.entity.po.ReportAuditHistory;
import com.yunnancommon.entity.query.EnterpriseReportInfoQuery;
import com.yunnancommon.entity.query.PeriodInfoQuery;
import com.yunnancommon.entity.vo.ResponseVO;
import com.yunnancommon.enums.ReportAuditLevelEnum;
import com.yunnancommon.enums.ReportAuditResult;
import com.yunnancommon.enums.ReportStatusEnum;
import com.yunnancommon.service.EnterpriseReportInfoService;
import com.yunnancommon.service.PeriodInfoService;
import com.yunnancommon.service.ReportAuditHistoryService;
import com.yunnancommon.service.ReportInfoService;
import com.yunnancommon.entity.vo.ReportInfoDetailVO;

import com.yunnanprovince.config.AppConfig;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.net.http.HttpRequest;
import java.util.List;


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

    @Resource
    private ReportAuditHistoryService reportAuditHistoryService;

    @Resource
    private AppConfig appConfig;

    @Resource
    private PeriodInfoService periodInfoService;


    @PostMapping("/loadDataList")
    public ResponseVO loadDataList(@RequestBody LoadReportDataDto loadReportDataDto) {
        EnterpriseReportInfoQuery query = new EnterpriseReportInfoQuery();
        if(loadReportDataDto.getInvestigateTime() != null) {
            PeriodInfo periodInfo = periodInfoService.getPeriodInfoByInvestigateTime(loadReportDataDto.getInvestigateTime());
            if(periodInfo != null) {
                query.setPeriodId(periodInfo.getPeriodId());
            }
        }
        query.setPageNo(loadReportDataDto.getPage());
        query.setPageSize(loadReportDataDto.getPageSize());
        query.setEnterpriseIndustry(loadReportDataDto.getIndustry());
        query.setEnterpriseNature(loadReportDataDto.getNature());
        query.setEnterpriseRegion(loadReportDataDto.getRegion());
        query.setEnterpriseNameFuzzy(loadReportDataDto.getEnterpriseName());
        query.setStatus(loadReportDataDto.getStatus());
        return getSuccessResponseVO(enterpriseReportInfoService.findListByPageWithAssociatedEnterpriseName(query));
    }

    @PostMapping("/approve")
    public ResponseVO approve(@RequestBody EnterpriseInfoReportDto enterpriseInfoReportDto) {

        EnterpriseReportInfo enterpriseReportInfo = new EnterpriseReportInfo();
        enterpriseReportInfo.setStatus(ReportStatusEnum.APPROVED.getCode());
        enterpriseReportInfoService.updateEnterpriseReportInfoByEnterpriseIdAndPeriodIdAndReportId(
                enterpriseReportInfo,
                enterpriseInfoReportDto.getEnterpriseId(),
                enterpriseInfoReportDto.getPeriodId(),
                enterpriseInfoReportDto.getReportId()
        );

        // 记录审核历史
        ReportAuditHistory auditHistory = new ReportAuditHistory();
        auditHistory.setEnterpriseId(enterpriseInfoReportDto.getEnterpriseId());
        auditHistory.setPeriodId(enterpriseInfoReportDto.getPeriodId());
        auditHistory.setReportId(enterpriseInfoReportDto.getReportId());
        auditHistory.setAuditLevel(ReportAuditLevelEnum.PROVINCIAL.getCode());
        auditHistory.setAuditResult(ReportAuditResult.APPROVED.getCode());
        auditHistory.setAuditor(appConfig.getUsername());
        reportAuditHistoryService.add(auditHistory);

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

        ReportAuditHistory auditHistory = new ReportAuditHistory();
        auditHistory.setEnterpriseId(enterpriseInfoReportDto.getEnterpriseId());
        auditHistory.setPeriodId(enterpriseInfoReportDto.getPeriodId());
        auditHistory.setReportId(enterpriseInfoReportDto.getReportId());
        auditHistory.setAuditLevel(ReportAuditLevelEnum.PROVINCIAL.getCode());
        auditHistory.setAuditResult(ReportAuditResult.REJECTED.getCode());
        auditHistory.setAuditor(appConfig.getUsername());
        reportAuditHistoryService.add(auditHistory);

        return getSuccessResponseVO(null);
    }

    @PostMapping("/upload")
    public ResponseVO upload(@RequestBody EnterpriseInfoReportDto enterpriseInfoReportDto) {
        EnterpriseReportInfo enterpriseReportInfo = new EnterpriseReportInfo();
        enterpriseReportInfo.setStatus(ReportStatusEnum.ARCHIVED.getCode());
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
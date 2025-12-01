package com.yunnancity.controller;

import com.yunnancommon.component.RedisComponent;
import com.yunnancommon.controller.ABaseController;
import com.yunnancommon.entity.dto.EnterpriseInfoReportDto;
import com.yunnancommon.entity.dto.LoadReportDataDto;
import com.yunnancommon.entity.po.EnterpriseReportInfo;
import com.yunnancommon.entity.po.ReportAuditHistory;
import com.yunnancommon.entity.query.EnterpriseReportInfoQuery;
import com.yunnancommon.entity.query.PeriodInfoQuery;
import com.yunnancommon.entity.vo.ReportInfoDetailVO;
import com.yunnancommon.entity.vo.ResponseVO;
import com.yunnancommon.enums.AccountTypeEnum;
import com.yunnancommon.enums.ReportAuditLevelEnum;
import com.yunnancommon.enums.ReportAuditResult;
import com.yunnancommon.enums.ReportStatusEnum;
import com.yunnancommon.service.EnterpriseReportInfoService;
import com.yunnancommon.service.PeriodInfoService;
import com.yunnancommon.service.ReportAuditHistoryService;
import com.yunnancommon.service.ReportInfoService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;


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

    @Resource
    private ReportAuditHistoryService reportAuditHistoryService;

    @Resource
    private PeriodInfoService periodInfoService;

    @PostMapping("/loadDataList")
    public ResponseVO loadDataList(HttpServletRequest request, @RequestBody LoadReportDataDto loadReportDataDto) {
        EnterpriseReportInfoQuery query = new EnterpriseReportInfoQuery();
        String cityToken = getTokenFromCookie(request, AccountTypeEnum.CITY);
        query.setEnterpriseRegion(redisComponent.getCityTokenInfo(cityToken).getCityCode());
        query.setPageNo(loadReportDataDto.getPage());
        query.setPageSize(loadReportDataDto.getPageSize());
        query.setEnterpriseIndustry(loadReportDataDto.getIndustry());
        query.setEnterpriseNature(loadReportDataDto.getNature());
        query.setPeriodId(loadReportDataDto.getPeriodId());
        query.setEnterpriseNameFuzzy(loadReportDataDto.getEnterpriseName());
        query.setStatus(loadReportDataDto.getStatus());
        query.setInvestigateTime(loadReportDataDto.getInvestigateTime());
        return getSuccessResponseVO(enterpriseReportInfoService.findListByPageWithAssociatedEnterpriseName(query));
    }

    @PostMapping("/approve")
    public ResponseVO approve(HttpServletRequest request, @RequestBody EnterpriseInfoReportDto enterpriseInfoReportDto) {
        EnterpriseReportInfo enterpriseReportInfo = new EnterpriseReportInfo();
        enterpriseReportInfo.setStatus(ReportStatusEnum.PROVINCE_AUDITING.getCode());
        enterpriseReportInfo.setUpdatedAt(new Date());
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
        auditHistory.setAuditLevel(ReportAuditLevelEnum.CITY.getCode());
        auditHistory.setAuditor(redisComponent.getCityTokenInfo(getTokenFromCookie(request, AccountTypeEnum.CITY)).getUsername());
        auditHistory.setAuditResult(ReportAuditResult.APPROVED.getCode());
        auditHistory.setAuditTime(new Date());
        reportAuditHistoryService.add(auditHistory);
        return getSuccessResponseVO(null);
    }

    @PostMapping("/reject")
    public ResponseVO reject(HttpServletRequest request, @RequestBody EnterpriseInfoReportDto enterpriseInfoReportDto) {
        EnterpriseReportInfo enterpriseReportInfo = new EnterpriseReportInfo();
        enterpriseReportInfo.setStatus(ReportStatusEnum.REJECTED.getCode());
        enterpriseReportInfo.setReasonReturn(enterpriseInfoReportDto.getRejectReason());
        enterpriseReportInfo.setUpdatedAt(new Date());
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
        auditHistory.setAuditLevel(ReportAuditLevelEnum.CITY.getCode());
        auditHistory.setAuditor(redisComponent.getCityTokenInfo(getTokenFromCookie(request, AccountTypeEnum.CITY)).getUsername());
        auditHistory.setAuditResult(ReportAuditResult.REJECTED.getCode());
        auditHistory.setAuditOpinion(enterpriseInfoReportDto.getRejectReason());
        auditHistory.setAuditTime(new Date());
        reportAuditHistoryService.add(auditHistory);
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

    //获取调查期
    @GetMapping("/period")
    public ResponseVO getPeriod() {
        PeriodInfoQuery query = new PeriodInfoQuery();
        return getSuccessResponseVO(periodInfoService.findListByPage(query));
    }
}

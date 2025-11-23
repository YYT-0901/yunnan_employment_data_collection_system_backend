package com.yunnanprovince.controller;

import com.yunnancommon.controller.ABaseController;
import com.yunnancommon.entity.dto.EnterpriseInfoReportDto;
import com.yunnancommon.entity.dto.LoadReportDataDto;
import com.yunnancommon.entity.po.EnterpriseReportInfo;
import com.yunnancommon.entity.po.PeriodInfo;
import com.yunnancommon.entity.po.ReportAuditHistory;
import com.yunnancommon.entity.po.ReportInfo;
import com.yunnancommon.entity.query.EnterpriseReportInfoQuery;
import com.yunnancommon.entity.query.PeriodInfoQuery;
import com.yunnancommon.entity.vo.ResponseVO;
import com.yunnancommon.entity.vo.XmlReportVO;
import com.yunnancommon.enums.ReportAuditLevelEnum;
import com.yunnancommon.enums.ReportAuditResult;
import com.yunnancommon.enums.ReportStatusEnum;
import com.yunnancommon.mapper.EnterpriseReportInfoMapper;
import com.yunnancommon.service.EnterpriseReportInfoService;
import com.yunnancommon.service.PeriodInfoService;
import com.yunnancommon.service.ReportAuditHistoryService;
import com.yunnancommon.service.ReportInfoService;
import com.yunnancommon.entity.vo.ReportInfoDetailVO;

import com.yunnancommon.utils.XmlUtils;
import com.yunnanprovince.config.AppConfig;
import com.yunnanprovince.config.WebConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


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
    private PeriodInfoService periodInfoService;

    @Resource
    private ReportAuditHistoryService reportAuditHistoryService;

    @Resource
    private AppConfig appConfig;

    @PostMapping("/loadDataList")
    public ResponseVO loadDataList(@RequestBody LoadReportDataDto loadReportDataDto) {
        EnterpriseReportInfoQuery query = new EnterpriseReportInfoQuery();
        query.setPageNo(loadReportDataDto.getPage());
        query.setPageSize(loadReportDataDto.getPageSize());
        query.setEnterpriseIndustry(loadReportDataDto.getIndustry());
        query.setEnterpriseNature(loadReportDataDto.getNature());
        query.setPeriodId(loadReportDataDto.getPeriodId());
        query.setEnterpriseRegion(loadReportDataDto.getRegion());
        query.setEnterpriseNameFuzzy(loadReportDataDto.getEnterpriseName());
        query.setStatus(loadReportDataDto.getStatus());
        query.setInvestigateTime(loadReportDataDto.getInvestigateTime());
        return getSuccessResponseVO(enterpriseReportInfoService.findListByPageWithAssociatedEnterpriseName(query));
    }

    @PostMapping("/approve")
    public ResponseVO approve(@RequestBody EnterpriseInfoReportDto enterpriseInfoReportDto) {
        EnterpriseReportInfo enterpriseReportInfo = new EnterpriseReportInfo();
        enterpriseReportInfo.setStatus(ReportStatusEnum.APPROVED.getCode());
        enterpriseReportInfo.setUpdatedAt(new Date());
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
        auditHistory.setAuditor(appConfig.getUsername());
        auditHistory.setAuditResult(ReportAuditResult.APPROVED.getCode());
        auditHistory.setAuditTime(new Date());
        reportAuditHistoryService.add(auditHistory);
        return getSuccessResponseVO(null);
    }

    @PostMapping("/reject")
    public ResponseVO reject(@RequestBody EnterpriseInfoReportDto enterpriseInfoReportDto) {
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
        auditHistory.setAuditLevel(ReportAuditLevelEnum.PROVINCIAL.getCode());
        auditHistory.setAuditor(appConfig.getUsername());
        auditHistory.setAuditResult(ReportAuditResult.REJECTED.getCode());
        auditHistory.setAuditTime(new Date());
        reportAuditHistoryService.add(auditHistory);
        return getSuccessResponseVO(null);
    }

    @PostMapping("/upload")
    public ResponseVO upload(@RequestBody Map<String, Object> requestData) {
        // 获取items数组
        List<Map<String, Object>> items = (List<Map<String, Object>>) requestData.get("items");
        Long periodId = items.get(0).get("periodId") != null ? Long.valueOf(items.get(0).get("periodId").toString()) : null;
        for (Map<String, Object> item : items) {
            EnterpriseReportInfo enterpriseReportInfo = new EnterpriseReportInfo();
            enterpriseReportInfo.setStatus(ReportStatusEnum.ARCHIVED.getCode());
            enterpriseReportInfo.setUpdatedAt(new Date());

            enterpriseReportInfoService.updateEnterpriseReportInfoByEnterpriseIdAndPeriodIdAndReportId(
                    enterpriseReportInfo,
                    (String) item.get("enterpriseId"),
                    Long.valueOf(item.get("periodId").toString()),
                    (String) item.get("reportId")
            );
        }

//        生成xml数据
        List<XmlReportVO> xmlReportVOList = enterpriseReportInfoService.getEnterpriseReportInfoByStatusAndPeriodId(ReportStatusEnum.ARCHIVED.getCode(), periodId);
        PeriodInfo periodInfo = periodInfoService.getPeriodInfoByPeriodId(periodId);
        String investigateTime = periodInfo.getInvestigateTime();
        //获取enterpriseReportInfo中的reportId获取reportInfo
        XmlUtils.generateXml(xmlReportVOList, investigateTime, appConfig.getXmlFolderPath());

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
package com.yunnanprovince.controller;

import com.yunnancommon.controller.ABaseController;
import com.yunnancommon.entity.dto.EnterpriseInfoReportDto;
import com.yunnancommon.entity.dto.LoadReportDataDto;
import com.yunnancommon.entity.po.EnterpriseReportInfo;
import com.yunnancommon.entity.query.EnterpriseReportInfoQuery;
import com.yunnancommon.entity.vo.ResponseVO;
import com.yunnancommon.enums.ReportStatusEnum;
import com.yunnancommon.service.EnterpriseReportInfoService;

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
}
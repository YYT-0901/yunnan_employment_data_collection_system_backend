package com.yunnancommon.service.impl;

import com.yunnancommon.enums.ReportStatusEnum;
import com.yunnancommon.mapper.EnterpriseAnalysisDataMapper;
import com.yunnancommon.service.EnterpriseAnalysisDataService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service("enterpriseAnalysisDataService")
public class EnterpriseAnalysisDataServiceImpl implements EnterpriseAnalysisDataService {

    @Resource
    private EnterpriseAnalysisDataMapper enterpriseAnalysisDataMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void writeFromReport(String enterpriseId, Long periodId, String reportId) {
        enterpriseAnalysisDataMapper.upsertFromReport(
                enterpriseId,
                periodId,
                reportId,
                ReportStatusEnum.APPROVED.getCode(),
                ReportStatusEnum.ARCHIVED.getCode()
        );
    }
}

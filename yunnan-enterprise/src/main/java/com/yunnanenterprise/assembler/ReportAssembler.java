package com.yunnanenterprise.assembler;

import com.yunnancommon.entity.po.EnterpriseReportInfo;
import com.yunnancommon.entity.po.ReportInfo;
import com.yunnanenterprise.constants.ReportConstants;
import com.yunnanenterprise.dictionary.DictionaryService;
import com.yunnanenterprise.dto.report.ReportCommand;
import com.yunnanenterprise.dto.report.ReportV0;
import com.yunnanenterprise.service.report.PeriodUtils;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * 组装/转换：
 * - ReportCommand -> ReportInfo/EnterpriseReportInfo（落库）
 * - ReportInfo + EnterpriseReportInfo -> ReportV0（返回前端）
 */
@Component
public class ReportAssembler {

    private final DictionaryService dict;

    public ReportAssembler(DictionaryService dict) {
        this.dict = dict;
    }

    public String newReportId() {
        return UUID.randomUUID().toString();
    }

    public ReportInfo toReportInfo(ReportCommand cmd) {
        ReportInfo r = new ReportInfo();
        r.setReportId(cmd.getId()); // 可能为空，服务层会补
        r.setConstructionCount(cmd.getInitialEmployees());
        r.setInvestigationCount(cmd.getCurrentEmployees());
        r.setReductionType(dict.typeCodeToId(cmd.getReductionTypeCode()));
        r.setReason1(dict.causeCodeToId(cmd.getPrimaryReasonCode()));
        r.setReason2(dict.causeCodeToId(cmd.getSecondaryReasonCode()));
        r.setReason3(dict.causeCodeToId(cmd.getTertiaryReasonCode()));

        // “其他”说明的处理：
        // - 减员类型为 OTHER：放到 other_reason
        // - 原因为 OTHER：放到对应 reasonX_desc
        if (ReportConstants.OTHER_CODE.equals(cmd.getReductionTypeCode())) {
            r.setOtherReason(nullIfBlank(cmd.getReductionTypeDesc()));
        } else {
            r.setOtherReason(null);
        }
        r.setReason1Desc(ReportConstants.OTHER_CODE.equals(cmd.getPrimaryReasonCode()) ? nullIfBlank(cmd.getPrimaryReasonDesc()) : null);
        r.setReason2Desc(ReportConstants.OTHER_CODE.equals(cmd.getSecondaryReasonCode()) ? nullIfBlank(cmd.getSecondaryReasonDesc()) : null);
        r.setReason3Desc(ReportConstants.OTHER_CODE.equals(cmd.getTertiaryReasonCode()) ? nullIfBlank(cmd.getTertiaryReasonDesc()) : null);

        return r;
    }

    public EnterpriseReportInfo toEnterpriseReportInfoForDraft(ReportCommand cmd, String reportId, Date now) {
        EnterpriseReportInfo e = new EnterpriseReportInfo();
        e.setEnterpriseId(cmd.getEnterpriseId());
        e.setPeriodId(PeriodUtils.toPeriodId(cmd.getReportingPeriod()));
        e.setReportId(reportId);
        e.setStatus(0); // 0-已暂存
        e.setUpdatedAt(now);
        if (e.getCreatedAt() == null) {
            e.setCreatedAt(now);
        }
        return e;
    }

    public EnterpriseReportInfo toEnterpriseReportInfoForSubmit(ReportCommand cmd, String reportId, Date now) {
        EnterpriseReportInfo e = new EnterpriseReportInfo();
        e.setEnterpriseId(cmd.getEnterpriseId());
        e.setPeriodId(PeriodUtils.toPeriodId(cmd.getReportingPeriod()));
        e.setReportId(reportId);
        e.setStatus(1); // 1-待市级审核
        e.setUpdatedAt(now);
        if (e.getCreatedAt() == null) {
            e.setCreatedAt(now);
        }
        return e;
    }

    public ReportV0 toVO(EnterpriseReportInfo e, ReportInfo r) {
        ReportV0 v = new ReportV0();
        v.setId(r != null ? r.getReportId() : null);
        v.setEnterpriseId(e != null ? e.getEnterpriseId() : null);
        v.setReportingPeriod(e != null ? PeriodUtils.fromPeriodId(Math.toIntExact(e.getPeriodId())) : null);
        v.setStatus(e != null && e.getStatus() != null ? String.valueOf(e.getStatus()) : "");

        if (r != null) {
            v.setInitialEmployees(r.getConstructionCount());
            v.setCurrentEmployees(r.getInvestigationCount());
            v.setReductionTypeCode(dict.typeIdToCode(r.getReductionType()));
            v.setPrimaryReasonCode(dict.causeIdToCode(r.getReason1()));
            v.setSecondaryReasonCode(dict.causeIdToCode(r.getReason2()));
            v.setTertiaryReasonCode(dict.causeIdToCode(r.getReason3()));

            // 其他说明回填
            v.setReductionTypeDesc(r.getOtherReason());
            v.setPrimaryReasonDesc(r.getReason1Desc());
            v.setSecondaryReasonDesc(r.getReason2Desc());
            v.setTertiaryReasonDesc(r.getReason3Desc());
        }

        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        v.setSubmittedAt(e != null && e.getStatus() != null && e.getStatus() >= 1 && e.getCreatedAt() != null
                ? fmt.format(e.getCreatedAt()) : "");
        v.setUpdatedAt(e != null && e.getUpdatedAt() != null ? fmt.format(e.getUpdatedAt()) : "");
        return v;
    }

    private String nullIfBlank(String s) {
        return (s == null || s.trim().isEmpty()) ? null : s.trim();
    }
}
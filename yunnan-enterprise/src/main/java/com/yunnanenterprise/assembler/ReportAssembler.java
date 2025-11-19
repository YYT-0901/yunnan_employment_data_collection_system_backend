package com.yunnanenterprise.assembler;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.yunnancommon.entity.po.EnterpriseReportInfo;
import com.yunnancommon.entity.po.ReportInfo;
import com.yunnanenterprise.constants.ReportConstants;
import com.yunnanenterprise.dictionary.DictionaryService;
import com.yunnanenterprise.dto.report.ReportCommand;
import com.yunnanenterprise.dto.report.ReportV0;
import com.yunnanenterprise.service.report.PeriodUtils;

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

        boolean hasReduction = cmd.getInitialEmployees() != null && cmd.getCurrentEmployees() != null
                && cmd.getCurrentEmployees() < cmd.getInitialEmployees();

        if (!hasReduction) {
            r.setReductionType(null);
            r.setReason1(null);
            r.setReason2(null);
            r.setReason3(null);
            r.setOtherReason(null);
            r.setReason1Desc(null);
            r.setReason2Desc(null);
            r.setReason3Desc(null);
            return r;
        }

        // 有减员：保存减员类型和原因
        r.setReductionType(dict.typeCodeToId(cmd.getReductionTypeCode()));
        r.setReason1(dict.causeCodeToId(cmd.getPrimaryReasonCode()));
        r.setReason2(dict.causeCodeToId(cmd.getSecondaryReasonCode()));
        r.setReason3(dict.causeCodeToId(cmd.getTertiaryReasonCode()));

        // ✅ 关键修复：保存所有原因的说明（不限于"其他"）
        // 问题背景：
        //   - 前端要求：只要选择了原因，说明就是必填的（不管是什么原因）
        //   - 旧逻辑：只保存"其他"原因的说明，导致其他原因的说明被丢弃
        //   - 修复后：保存所有原因的说明，确保数据不丢失
        
        // 1. 减员类型说明：只有选择"其他"类型时才保存到 other_reason 字段
        if (ReportConstants.OTHER_CODE.equals(cmd.getReductionTypeCode())) {
            r.setOtherReason(nullIfBlank(cmd.getReductionTypeDesc()));
        } else {
            r.setOtherReason(null);
        }
        
        // 2. ✅ 所有原因的说明都保存到对应的 reasonX_desc 字段
        //    无论原因是"合同到期"、"正常退休"、"成本上涨"还是"其他"
        //    用户填写的说明都会被保存到数据库
        r.setReason1Desc(nullIfBlank(cmd.getPrimaryReasonDesc()));
        r.setReason2Desc(nullIfBlank(cmd.getSecondaryReasonDesc()));
        r.setReason3Desc(nullIfBlank(cmd.getTertiaryReasonDesc()));

        return r;
    }

    public EnterpriseReportInfo toEnterpriseReportInfoForDraft(ReportCommand cmd, String reportId, Integer periodId, Date now) {
        EnterpriseReportInfo e = new EnterpriseReportInfo();
        e.setEnterpriseId(cmd.getEnterpriseId());
        e.setPeriodId(Long.valueOf(periodId));
        e.setReportId(reportId);
        e.setStatus(0); // 0-已暂存
        e.setUpdatedAt(now);
        if (e.getCreatedAt() == null) {
            e.setCreatedAt(now);
        }
        return e;
    }

    public EnterpriseReportInfo toEnterpriseReportInfoForSubmit(ReportCommand cmd, String reportId, Integer periodId, Date now) {
        EnterpriseReportInfo e = new EnterpriseReportInfo();
        e.setEnterpriseId(cmd.getEnterpriseId());
        e.setPeriodId(Long.valueOf(periodId));
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
        v.setOldId(e.getOldReportId() != null ? e.getOldReportId() : null);
        v.setEnterpriseId(e != null ? e.getEnterpriseId() : null);
        v.setReportingPeriod(e != null ? PeriodUtils.fromPeriodId((long) Math.toIntExact(e.getPeriodId())) : null);
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
                ? fmt.format(e.getCreatedAt())
                : "");
        v.setUpdatedAt(e != null && e.getUpdatedAt() != null ? fmt.format(e.getUpdatedAt()) : "");
        if (e != null) {
            v.setPeriodStartTime(e.getPeriodStartTime() != null ? fmt.format(e.getPeriodStartTime()) : null);
            v.setPeriodEndTime(e.getPeriodEndTime() != null ? fmt.format(e.getPeriodEndTime()) : null);
            v.setReasonReturn(e.getReasonReturn());
        }
        return v;
    }

    private String nullIfBlank(String s) {
        return (s == null || s.trim().isEmpty()) ? null : s.trim();
    }
}

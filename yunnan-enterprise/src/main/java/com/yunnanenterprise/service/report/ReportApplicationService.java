package com.yunnanenterprise.service.report;

import com.yunnancommon.entity.po.EnterpriseReportInfo;
import com.yunnancommon.entity.po.ReportInfo;
import com.yunnancommon.entity.query.EnterpriseReportInfoQuery;
import com.yunnancommon.service.EnterpriseReportInfoService;
import com.yunnancommon.service.ReportInfoService;
import com.yunnanenterprise.assembler.ReportAssembler;
import com.yunnanenterprise.dto.report.ReportCommand;
import com.yunnanenterprise.dto.report.ReportV0;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 应用服务：聚合多个表的操作（enterprise_report_info + report_info）
 */
@Service
@Profile("db")
public class ReportApplicationService {

    private final EnterpriseReportInfoService enterpriseReportInfoService;
    private final ReportInfoService reportInfoService;
    private final ReportAssembler assembler;

    public ReportApplicationService(EnterpriseReportInfoService enterpriseReportInfoService,
                                    ReportInfoService reportInfoService,
                                    ReportAssembler assembler) {
        this.enterpriseReportInfoService = enterpriseReportInfoService;
        this.reportInfoService = reportInfoService;
        this.assembler = assembler;
    }

    public ReportV0 getByEnterpriseAndPeriod(String enterpriseId, String yyyyMm) {
        Integer periodId = PeriodUtils.toPeriodId(yyyyMm);
        EnterpriseReportInfoQuery q = new EnterpriseReportInfoQuery();
        q.setEnterpriseId(enterpriseId);
        q.setPeriodId(periodId);
        q.setOrderBy("updated_at desc");
        q.setPageNo(1);
        q.setPageSize(1);
        List<EnterpriseReportInfo> list = enterpriseReportInfoService.findListByParam(q);
        if (list == null || list.isEmpty()) {
            // 返回空壳，仅带 period 信息
            EnterpriseReportInfo e = new EnterpriseReportInfo();
            e.setEnterpriseId(enterpriseId);
            e.setPeriodId(periodId);
            return assembler.toVO(e, null);
        }
        EnterpriseReportInfo e = list.get(0);
        ReportInfo r = reportInfoService.getReportInfoByReportId(e.getReportId());
        return assembler.toVO(e, r);
    }

    @Transactional
    public void saveDraft(ReportCommand cmd) {
        Date now = new Date();
        Integer periodId = PeriodUtils.toPeriodId(cmd.getReportingPeriod());

        // 查询是否已有记录（该企业+期次）
        EnterpriseReportInfoQuery q = new EnterpriseReportInfoQuery();
        q.setEnterpriseId(cmd.getEnterpriseId());
        q.setPeriodId(periodId);
        q.setOrderBy("updated_at desc");
        q.setPageNo(1);
        q.setPageSize(1);
        List<EnterpriseReportInfo> list = enterpriseReportInfoService.findListByParam(q);

        String reportId;
        if (list != null && !list.isEmpty()) {
            reportId = list.get(0).getReportId();
        } else {
            reportId = assembler.newReportId();
        }

        // 写 report_info
        ReportInfo r = assembler.toReportInfo(cmd);
        r.setReportId(reportId);
        if (reportInfoService.getReportInfoByReportId(reportId) == null) {
            reportInfoService.add(r);
        } else {
            reportInfoService.updateReportInfoByReportId(r, reportId);
        }

        // 写 enterprise_report_info（状态=0）
        EnterpriseReportInfo e = assembler.toEnterpriseReportInfoForDraft(cmd, reportId, now);
        if (list == null || list.isEmpty()) {
            enterpriseReportInfoService.add(e);
        } else {
            enterpriseReportInfoService.updateEnterpriseReportInfoByEnterpriseIdAndPeriodIdAndReportId(
                    e, e.getEnterpriseId(), e.getPeriodId(), reportId);
        }
    }

    @Transactional
    public void submit(ReportCommand cmd) {
        // 先保存草稿，再切换到提交状态，保证数据一致
        saveDraft(cmd);
        Date now = new Date();
        Integer periodId = PeriodUtils.toPeriodId(cmd.getReportingPeriod());

        // 再次拿最新记录
        EnterpriseReportInfoQuery q = new EnterpriseReportInfoQuery();
        q.setEnterpriseId(cmd.getEnterpriseId());
        q.setPeriodId(periodId);
        q.setOrderBy("updated_at desc");
        q.setPageNo(1);
        q.setPageSize(1);
        List<EnterpriseReportInfo> list = enterpriseReportInfoService.findListByParam(q);
        if (list == null || list.isEmpty()) return;

        EnterpriseReportInfo latest = list.get(0);
        EnterpriseReportInfo submit = assembler.toEnterpriseReportInfoForSubmit(cmd, latest.getReportId(), now);
        enterpriseReportInfoService.updateEnterpriseReportInfoByEnterpriseIdAndPeriodIdAndReportId(
                submit, submit.getEnterpriseId(), submit.getPeriodId(), submit.getReportId());
    }

    /**
     * 获取企业的所有报表列表
     * @param enterpriseId 企业ID
     * @param pageNo 页码
     * @param pageSize 每页数量
     * @return 报表列表
     */
    public List<ReportV0> getReportList(String enterpriseId, Integer pageNo, Integer pageSize) {
        EnterpriseReportInfoQuery q = new EnterpriseReportInfoQuery();
        q.setEnterpriseId(enterpriseId);
        q.setOrderBy("updated_at desc");  // 按更新时间倒序
        q.setPageNo(pageNo);
        q.setPageSize(pageSize);
        
        List<EnterpriseReportInfo> list = enterpriseReportInfoService.findListByParam(q);
        
        if (list == null || list.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 转换为 VO 对象
        List<ReportV0> result = new ArrayList<>();
        for (EnterpriseReportInfo e : list) {
            ReportInfo r = reportInfoService.getReportInfoByReportId(e.getReportId());
            result.add(assembler.toVO(e, r));
        }
        
        return result;
    }
}

package com.yunnanenterprise.service.report;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.yunnanenterprise.dictionary.DictionaryService;
import com.yunnancommon.service.EnterpriseInfoService;
import com.yunnancommon.entity.po.EnterpriseInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.binding.BindingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yunnancommon.entity.po.EnterpriseReportInfo;
import com.yunnancommon.entity.po.PeriodInfo;
import com.yunnancommon.entity.po.ReportInfo;
import com.yunnancommon.entity.query.EnterpriseReportInfoQuery;
import com.yunnancommon.entity.query.PeriodInfoQuery;
import com.yunnancommon.exception.BusinessException;
import com.yunnancommon.redis.RedisUtils;
import com.yunnancommon.service.EnterpriseReportInfoService;
import com.yunnancommon.service.PeriodInfoService;
import com.yunnancommon.service.ReportInfoService;
import com.yunnancommon.entity.po.ReportAuditHistory;
import com.yunnanenterprise.assembler.ReportAssembler;
import com.yunnanenterprise.dto.report.ReportCommand;
import com.yunnanenterprise.dto.report.ReportV0;
import org.apache.ibatis.binding.BindingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.yunnanenterprise.enums.ReportStatusEnum;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;
import com.yunnancommon.service.ReportAuditHistoryService;
import com.yunnancommon.entity.query.ReportAuditHistoryQuery;
import java.text.SimpleDateFormat;

import javax.annotation.Resource;
import java.util.*;

/**
 * 应用服务：聚合多个表的操作（enterprise_report_info + report_info）
 */
@Service
public class ReportApplicationService {

    private static final Logger log = LoggerFactory.getLogger(ReportApplicationService.class);

    private final EnterpriseReportInfoService enterpriseReportInfoService;
    private final ReportInfoService reportInfoService;
    private final ReportAssembler assembler;

    @Resource
    private DictionaryService dictionaryService;

    @Resource
    private ReportAuditHistoryService reportAuditHistoryService;

    @Resource
    private ReportInfoService reportInfoCommonService;

    @Resource
    private PeriodInfoService periodInfoService;

    @Resource
    private EnterpriseInfoService enterpriseInfoService;

    @Resource
    private RedisUtils redisUtils;

    public ReportApplicationService(EnterpriseReportInfoService enterpriseReportInfoService,
            ReportInfoService reportInfoService,
            ReportAssembler assembler) {
        this.enterpriseReportInfoService = enterpriseReportInfoService;
        this.reportInfoService = reportInfoService;
        this.assembler = assembler;
    }

    /**
     * 辅助方法：通过 investigate_time 获取真实的 period_id
     * 
     * @param investigateTime 调查期标识（如 "2025-01"）
     * @return 数据库中的真实 period_id（自增ID）
     * @throws BusinessException 如果调查期不存在
     */
    private Integer getPeriodIdByInvestigateTime(String investigateTime) throws BusinessException {
        // 查询所有调查期（由于PeriodInfoQuery没有investigateTime字段，所以查询所有然后过滤）
        PeriodInfoQuery periodQuery = new PeriodInfoQuery();
        periodQuery.setPageNo(1);
        periodQuery.setPageSize(1000); // 假设不会有超过1000个调查期
        List<PeriodInfo> allPeriods = periodInfoService.findListByParam(periodQuery);

        // 过滤出匹配的调查期
        if (allPeriods != null) {
            for (PeriodInfo period : allPeriods) {
                if (investigateTime.equals(period.getInvestigateTime())) {
                    // 找到了，返回 period_id（Long转Integer）
                    return period.getPeriodId().intValue();
                }
            }
        }

        // 没找到，抛出异常
        throw new BusinessException("调查期 " + investigateTime + " 不存在");
    }

    /**
     * 根据企业ID和调查期获取报表数据
     * 
     * @param enterpriseId 企业ID（如 "TEST_ENT_002"）
     * @param yyyyMm       调查期（如 "2025-01"）
     * @return 报表视图对象，包含所有报表数据和锁定状态
     * @throws BusinessException 如果调查期不存在
     */
    public ReportV0 getByEnterpriseAndPeriod(String enterpriseId, String yyyyMm) throws BusinessException {
        System.out.println("===== getByEnterpriseAndPeriod START =====");
        System.out.println("enterpriseId: " + enterpriseId + ", period: " + yyyyMm);

        // ✅ 通过 investigate_time 获取真实的 period_id
        Integer periodId = getPeriodIdByInvestigateTime(yyyyMm);
        System.out.println("Real periodId: " + periodId + " for investigate_time: " + yyyyMm);

        // 查询该企业在该调查期的报表数据
        EnterpriseReportInfoQuery q = new EnterpriseReportInfoQuery();
        q.setEnterpriseId(enterpriseId);
        q.setPeriodId(Long.valueOf(periodId)); // 使用真实的 period_id
        q.setOrderBy("updated_at desc"); // 按更新时间倒序，获取最新版本
        q.setPageNo(1);
        q.setPageSize(1);
        List<EnterpriseReportInfo> list = enterpriseReportInfoService.findListByParam(q);

        // 情况1：没有找到报表 -> 新建报表
        if (list == null || list.isEmpty()) {
            System.out.println("No existing report found, creating new empty VO");

            // ⚠️ 只在新建报表时查询上一期数据（用于自动填充建档期就业人数）
            Integer previousEmployeeCount = getPreviousPeriodEmployeeCount(enterpriseId, periodId);
            boolean hasHistory = (previousEmployeeCount != null);

            System.out.println("Has history: " + hasHistory);
            if (hasHistory) {
                System.out.println("Previous employee count: " + previousEmployeeCount);
            }

            // 返回空壳，仅带 period 信息
            EnterpriseReportInfo e = new EnterpriseReportInfo();
            e.setEnterpriseId(enterpriseId);
            e.setPeriodId(Long.valueOf(periodId));

            ReportV0 vo = assembler.toVO(e, null);
            vo.setReportingPeriod(yyyyMm);
            applyEditabilityFlags(vo, e);
            applyLatestAuditSnapshot(vo, null, null);

            // 如果有历史数据，自动填充建档期就业人数并锁定
            if (hasHistory) {
                vo.setInitialEmployees(previousEmployeeCount);
                vo.setIsInitialEmployeesLocked(true);
                System.out.println("New report: Set initial_employees: " + previousEmployeeCount + ", locked: true");
            } else {
                vo.setIsInitialEmployeesLocked(false);
                System.out.println("New report: No history, locked: false");
            }

            // 新建报表：调查期人数可编辑
            vo.setIsCurrentEmployeesLocked(false);

            System.out.println("Final VO (new report):");
            System.out.println("  - initial_employees: " + vo.getInitialEmployees() + ", locked: "
                    + vo.getIsInitialEmployeesLocked());
            System.out.println("  - current_employees: can edit, locked: " + vo.getIsCurrentEmployeesLocked());
            System.out.println("===== getByEnterpriseAndPeriod END =====");
            return vo;
        }

        System.out.println("Found existing report - entering edit mode");
        EnterpriseReportInfo e = list.get(0);
        System.out.println("Existing report_id: " + e.getReportId());
        System.out.println("Existing report enterprise_id: " + e.getEnterpriseId()
                + ", period_id: " + e.getPeriodId());
        ReportInfo r = reportInfoService.getReportInfoByReportId(e.getReportId());
        ReportV0 vo = assembler.toVO(e, r);
        vo.setReportingPeriod(yyyyMm);
        applyEditabilityFlags(vo, e);

        Map<String, ReportAuditHistory[]> snapshot = loadLatestAuditSnapshots(
                Collections.singletonList(e.getReportId()));
        ReportAuditHistory[] pair = snapshot.get(e.getReportId());
        applyLatestAuditSnapshot(vo, pair != null ? pair[0] : null, pair != null ? pair[1] : null);

        System.out.println("Existing report - initial_employees: " + vo.getInitialEmployees()
                + ", current_employees: " + vo.getCurrentEmployees()
                + ", status: " + vo.getStatus());

        // ⚠️ 关键逻辑：已有报表时，直接返回数据库中的数据，不做任何自动填充
        // 报送期次、建档期人数、调查期人数 都应该锁定

        // 建档期人数：如果已填写就锁定
        if (vo.getInitialEmployees() != null && vo.getInitialEmployees() > 0) {
            vo.setIsInitialEmployeesLocked(true);
        } else {
            vo.setIsInitialEmployeesLocked(false);
        }

        // 调查期人数：已有报表时始终锁定（无论是暂存、提交、驳回等任何状态）
        // vo.setIsCurrentEmployeesLocked(true);

        Integer status = e.getStatus();

        boolean isEditableStatus = (status != null && (status == 0 || status == 5));

        boolean isLatestVersion = !hasNewerVersion(e.getEnterpriseId(), Math.toIntExact(e.getPeriodId()),
                e.getReportId());

        if (isEditableStatus && isLatestVersion) {
            vo.setIsCurrentEmployeesLocked(false);
            vo.setIsInitialEmployeesLocked(true);

            System.out.println(">> Unlocking fields for Status " + status);
        } else {
            vo.setIsCurrentEmployeesLocked(true);
            if (vo.getInitialEmployees() != null && vo.getInitialEmployees() > 0) {
                vo.setIsInitialEmployeesLocked(true);
            } else {
                vo.setIsInitialEmployeesLocked(false);
            }
        }

        System.out.println("Final VO (existing report):");
        System.out.println(
                "  - initial_employees: " + vo.getInitialEmployees() + ", locked: " + vo.getIsInitialEmployeesLocked());
        System.out.println(
                "  - current_employees: " + vo.getCurrentEmployees() + ", locked: " + vo.getIsCurrentEmployeesLocked());
        System.out.println("===== getByEnterpriseAndPeriod END =====");
        return vo;
    }

    /**
     * 获取上一期的调查期就业人数
     * 
     * @param enterpriseId    企业ID
     * @param currentPeriodId 当前调查期ID
     * @return 上一期的调查期就业人数，如果不存在返回null
     */
    private Integer getPreviousPeriodEmployeeCount(String enterpriseId, Integer currentPeriodId) {
        System.out.println("===== start to query the history data =====");
        System.out.println("enterpreiseID: " + enterpriseId);
        System.out.println("currentPeriodID: " + currentPeriodId);
        // 查询该企业所有已审核通过（status=3）或已归档（status=4）的报表
        EnterpriseReportInfoQuery query = new EnterpriseReportInfoQuery();
        query.setEnterpriseId(enterpriseId);
        query.setOrderBy("period_id DESC"); // 按调查期倒序
        query.setPageNo(1);
        query.setPageSize(100); // 获取最近的报表

        List<EnterpriseReportInfo> historyList = enterpriseReportInfoService.findListByParam(query);

        System.out.println("number of the query history data:" + (historyList != null ? historyList.size() : 0));

        if (historyList == null || historyList.isEmpty()) {
            System.out.println("didn't find any history data");
            return null;
        }

        // 找到小于当前period_id的最大period_id记录（即上一期）
        EnterpriseReportInfo previousReport = null;
        for (EnterpriseReportInfo report : historyList) {
            System.out.println("checked report - period_id" + report.getPeriodId()
                    + ", report_id:" + report.getReportId()
                    + ", status:" + report.getStatus());
            // 只考虑已归档（status=4）的记录
            if (report.getStatus() != null && report.getStatus() == 4) {
                if (report.getPeriodId() < currentPeriodId) {
                    if (previousReport == null || report.getPeriodId() > previousReport.getPeriodId()) {
                        previousReport = report;
                        System.out.println("find the previous report - period_id" + report.getPeriodId());
                    }
                }
            }
        }

        if (previousReport == null) {
            System.out.println("没有找到符合条件的上一期报表（需要status=4，且period_id < " + currentPeriodId + "）");
            return null;
        }

        System.out.println("最终选定的上一期报表 - period_id: " + previousReport.getPeriodId() + ", report_id: "
                + previousReport.getReportId());

        // 获取上一期的调查期就业人数
        ReportInfo previousReportInfo = reportInfoService.getReportInfoByReportId(previousReport.getReportId());
        if (previousReportInfo != null && previousReportInfo.getInvestigationCount() != null) {
            System.out.println("上一期调查期就业人数: " + previousReportInfo.getInvestigationCount());
            System.out.println("===== 查询历史数据结束 =====");
            return previousReportInfo.getInvestigationCount();
        }
        System.out.println("上一期报表数据为空或没有调查期就业人数");
        System.out.println("===== 查询历史数据结束 =====");
        return null;
    }

    private boolean hasNewerVersion(String enterpriseId, Integer periodId, String reportId) {
        if (reportId == null) {
            return false;
        }
        EnterpriseReportInfoQuery query = new EnterpriseReportInfoQuery();
        query.setEnterpriseId(enterpriseId);
        query.setPeriodId(Long.valueOf(periodId));
        query.setOldReportId(reportId);
        query.setPageNo(1);
        query.setPageSize(1);
        List<EnterpriseReportInfo> newerList = enterpriseReportInfoService.findListByParam(query);
        return newerList != null && !newerList.isEmpty();
    }

    private void applyEditabilityFlags(ReportV0 vo, EnterpriseReportInfo entity) {
        if (vo == null) {
            return;
        }

        if (entity == null) {
            vo.setEditable(true);
            vo.setCanResubmit(false);
            return;
        }

        Integer status = entity.getStatus();
        ReportStatusEnum statusEnum = ReportStatusEnum.getByCode(status);
        boolean editable = (statusEnum == null) || statusEnum.canEdit();

        if (editable && hasNewerVersion(entity.getEnterpriseId(), Math.toIntExact(entity.getPeriodId()),
                entity.getReportId())) {
            editable = false;
        }

        vo.setEditable(editable);
        vo.setCanResubmit(editable && status != null && status == 5);
    }

    private String resolveAuditLevelName(Integer level) {
        if (level == null) {
            return null;
        }
        switch (level) {
            case 1:
                return "市级审核";
            case 2:
                return "省级审核";
            default:
                return "";
        }
    }

    private String resolveAuditResultName(Integer result) {
        if (result == null) {
            return null;
        }
        return result == 1 ? "通过" : (result == 2 ? "驳回" : "");
    }

    private void applyLatestAuditSnapshot(ReportV0 vo, ReportAuditHistory latest, ReportAuditHistory latestReject) {
        if (vo == null) {
            return;
        }

        ReportAuditHistory source = latestReject != null ? latestReject : latest;
        if (source == null) {
            vo.setLatestAuditLevel(null);
            vo.setLatestAuditLevelName(null);
            vo.setLatestAuditResult(null);
            vo.setLatestAuditResultName(null);
            vo.setLatestAuditOpinion(null);
            vo.setLatestAuditTime(null);
            return;
        }

        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        vo.setLatestAuditLevel(source.getAuditLevel());
        vo.setLatestAuditLevelName(resolveAuditLevelName(source.getAuditLevel()));
        vo.setLatestAuditResult(source.getAuditResult());
        vo.setLatestAuditResultName(resolveAuditResultName(source.getAuditResult()));
        vo.setLatestAuditOpinion(source.getAuditOpinion());
        vo.setLatestAuditTime(source.getAuditTime() != null ? fmt.format(source.getAuditTime()) : null);

        if (latestReject == null && latest != null && latest.getAuditResult() != null && latest.getAuditResult() == 2) {
            // 如果最新记录本身就是驳回，保持已有设置
            return;
        }

        if (latestReject != null && latestReject != source) {
            // 如果使用最新审核信息（非驳回），确保驳回信息也同步
            vo.setLatestAuditResult(latestReject.getAuditResult());
            vo.setLatestAuditResultName(resolveAuditResultName(latestReject.getAuditResult()));
            vo.setLatestAuditOpinion(latestReject.getAuditOpinion());
            vo.setLatestAuditTime(latestReject.getAuditTime() != null ? fmt.format(latestReject.getAuditTime()) : null);
            vo.setLatestAuditLevel(latestReject.getAuditLevel());
            vo.setLatestAuditLevelName(resolveAuditLevelName(latestReject.getAuditLevel()));
        }
    }

    private Map<String, ReportAuditHistory[]> loadLatestAuditSnapshots(List<String> reportIds) {
        Map<String, ReportAuditHistory[]> snapshots = new HashMap<>();
        if (reportIds == null || reportIds.isEmpty()) {
            return snapshots;
        }

        for (String reportId : reportIds) {
            if (reportId == null) {
                continue;
            }
            ReportAuditHistoryQuery query = new ReportAuditHistoryQuery();
            query.setReportId(reportId);
            query.setOrderBy("audit_time DESC");
            query.setPageNo(1);
            query.setPageSize(20);

            List<ReportAuditHistory> historyList;
            try {
                historyList = reportAuditHistoryService.findListByParam(query);
            } catch (BindingException ex) {
                log.warn("ReportAuditHistoryMapper 未加载，跳过审核历史快照填充", ex);
                return Collections.emptyMap();
            } catch (Exception ex) {
                log.warn("查询审核历史失败，跳过快照填充 reportId={}", reportId, ex);
                continue;
            }
            if (historyList == null || historyList.isEmpty()) {
                continue;
            }

            ReportAuditHistory latest = historyList.get(0);
            ReportAuditHistory latestReject = null;
            for (ReportAuditHistory history : historyList) {
                if (history != null && history.getAuditResult() != null && history.getAuditResult() == 2) {
                    latestReject = history;
                    break;
                }
            }

            snapshots.put(reportId, new ReportAuditHistory[] { latest, latestReject });
        }

        return snapshots;
    }

    /**
     * 保存草稿（暂存）
     * 
     * @param cmd 报表命令对象，包含用户填写的所有数据
     * @throws BusinessException 如果调查期不存在
     */
    @Transactional
    public void saveDraft(ReportCommand cmd) throws BusinessException {
        Date now = new Date();

        // ✅ 通过 investigate_time 获取真实的 period_id
        Integer periodId = getPeriodIdByInvestigateTime(cmd.getReportingPeriod());

        // 查询该企业在该调查期是否已有报表记录
        EnterpriseReportInfoQuery q = new EnterpriseReportInfoQuery();
        q.setEnterpriseId(cmd.getEnterpriseId());
        q.setPeriodId(Long.valueOf(periodId));
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
        EnterpriseReportInfo e = assembler.toEnterpriseReportInfoForDraft(cmd, reportId, periodId, now);
        populateExtendedInfo(e);

        if (list == null || list.isEmpty()) {
            enterpriseReportInfoService.add(e);
        } else {
            enterpriseReportInfoService.updateEnterpriseReportInfoByEnterpriseIdAndPeriodIdAndReportId(
                    e, e.getEnterpriseId(), e.getPeriodId(), reportId);
        }
    }

    /**
     * 提交报表（不带幂等键的简单版本）
     * 
     * @param cmd 报表命令对象
     * @throws BusinessException 如果调查期不存在
     */
    @Transactional
    public void submit(ReportCommand cmd) throws BusinessException {
        // 先保存草稿，再切换到提交状态，保证数据一致
        saveDraft(cmd);
        Date now = new Date();

        // ✅ 通过 investigate_time 获取真实的 period_id
        Integer periodId = getPeriodIdByInvestigateTime(cmd.getReportingPeriod());

        // 再次拿最新记录
        EnterpriseReportInfoQuery q = new EnterpriseReportInfoQuery();
        q.setEnterpriseId(cmd.getEnterpriseId());
        q.setPeriodId(Long.valueOf(periodId));
        q.setOrderBy("updated_at desc");
        q.setPageNo(1);
        q.setPageSize(1);
        List<EnterpriseReportInfo> list = enterpriseReportInfoService.findListByParam(q);
        if (list == null || list.isEmpty())
            return;

        // 更新状态为"待市级审核"（status=1）
        EnterpriseReportInfo latest = list.get(0);
        EnterpriseReportInfo submit = assembler.toEnterpriseReportInfoForSubmit(cmd, latest.getReportId(), periodId,
                now);
        populateExtendedInfo(submit);

        enterpriseReportInfoService.updateEnterpriseReportInfoByEnterpriseIdAndPeriodIdAndReportId(
                submit, submit.getEnterpriseId(), submit.getPeriodId(), submit.getReportId());
    }

    /**
     * 获取企业的所有报表列表
     * 
     * @param enterpriseId 企业ID
     * @param pageNo       页码
     * @param pageSize     每页数量
     * @return 报表列表
     */
    public List<ReportV0> getReportList(String enterpriseId, Integer pageNo, Integer pageSize) {
        List<EnterpriseReportInfo> list = enterpriseReportInfoService.findLatestByEnterprise(enterpriseId, pageNo,
                pageSize);

        if (list == null || list.isEmpty()) {
            return new ArrayList<>();
        }

        // 若缺少时间范围信息，补充 period_info 的起止时间
        Map<Integer, PeriodInfo> periodInfoMap = new HashMap<>();
        for (EnterpriseReportInfo e : list) {
            if (e == null) {
                continue;
            }
            Integer periodId = Math.toIntExact(e.getPeriodId());
            if (periodId != null) {
                periodInfoMap.computeIfAbsent(periodId,
                        id -> periodInfoService.getPeriodInfoByPeriodId(Long.valueOf(id)));
            }
        }
        for (EnterpriseReportInfo e : list) {
            if (e == null) {
                continue;
            }
            PeriodInfo periodInfo = periodInfoMap.get(e.getPeriodId().intValue());
            if (periodInfo != null) {
                System.out.println("periodInfo: " + periodInfo.getInvestigateTime());
                if (e.getPeriodStartTime() == null) {
                    e.setPeriodStartTime(periodInfo.getPeriodStartTime());
                }
                if (e.getPeriodEndTime() == null) {
                    e.setPeriodEndTime(periodInfo.getPeriodEndTime());
                }
            }
        }

        // 转换为 VO 对象
        List<ReportV0> result = new ArrayList<>();
        for (EnterpriseReportInfo e : list) {
            ReportInfo r = reportInfoService.getReportInfoByReportId(e.getReportId());
            ReportV0 vo = assembler.toVO(e, r);
            PeriodInfo periodInfo = periodInfoMap.get(e.getPeriodId().intValue());
            if (periodInfo != null && periodInfo.getInvestigateTime() != null) {
                vo.setReportingPeriod(periodInfo.getInvestigateTime());
            }
            applyEditabilityFlags(vo, e);
            result.add(vo);
        }

        List<String> reportIds = new ArrayList<>();
        for (ReportV0 vo : result) {
            if (vo != null && vo.getId() != null) {
                reportIds.add(vo.getId());
            }
        }

        Map<String, ReportAuditHistory[]> snapshots = loadLatestAuditSnapshots(reportIds);
        for (ReportV0 vo : result) {
            if (vo == null) {
                continue;
            }
            ReportAuditHistory[] pair = vo.getId() != null ? snapshots.get(vo.getId()) : null;
            applyLatestAuditSnapshot(vo, pair != null ? pair[0] : null, pair != null ? pair[1] : null);
        }

        return result;
    }

    public List<ReportV0> getReportHistory(String enterpriseId, String reportingPeriod) throws BusinessException {
        if (StringUtils.isBlank(reportingPeriod)) {
            throw new BusinessException("reporting_period 不能为空");
        }
        Integer periodId = getPeriodIdByInvestigateTime(reportingPeriod);
        List<EnterpriseReportInfo> list = enterpriseReportInfoService.findHistoryByEnterpriseAndPeriod(enterpriseId,
                Long.valueOf(periodId));
        if (list == null || list.isEmpty()) {
            return new ArrayList<>();
        }

        PeriodInfo periodInfo = periodInfoService.getPeriodInfoByPeriodId(Long.valueOf(periodId));
        List<ReportV0> result = new ArrayList<>();
        for (EnterpriseReportInfo e : list) {
            if (e == null) {
                continue;
            }
            if (periodInfo != null) {
                if (e.getPeriodStartTime() == null) {
                    e.setPeriodStartTime(periodInfo.getPeriodStartTime());
                }
                if (e.getPeriodEndTime() == null) {
                    e.setPeriodEndTime(periodInfo.getPeriodEndTime());
                }
            }
            ReportInfo r = reportInfoService.getReportInfoByReportId(e.getReportId());
            ReportV0 vo = assembler.toVO(e, r);
            vo.setReportingPeriod(reportingPeriod);
            applyEditabilityFlags(vo, e);
            result.add(vo);
        }

        List<String> reportIds = new ArrayList<>();
        for (ReportV0 vo : result) {
            if (vo != null && vo.getId() != null) {
                reportIds.add(vo.getId());
            }
        }
        Map<String, ReportAuditHistory[]> snapshots = loadLatestAuditSnapshots(reportIds);
        for (ReportV0 vo : result) {
            if (vo == null) {
                continue;
            }
            ReportAuditHistory[] pair = vo.getId() != null ? snapshots.get(vo.getId()) : null;
            applyLatestAuditSnapshot(vo, pair != null ? pair[0] : null, pair != null ? pair[1] : null);
        }
        return result;
    }

    /**
     * 获取当前可填报的调查期列表
     * 
     * 业务逻辑：
     * 1. 查询 period_info，条件：NOW() BETWEEN period_start_time AND period_end_time
     * 2. 返回所有符合条件的调查期（可能有多个，如有重叠窗口）
     * 3. 前端会显示列表让用户选择
     * 
     * @return Map包含periods列表
     */
    public Map<String, Object> getCurrentPeriods() {
        Date now = new Date();

        // 查询进行中的调查期
        System.out.println("===== getCurrentPeriods DEBUG message =====");
        System.out.println("current time:" + now);

        PeriodInfoQuery query = new PeriodInfoQuery();
        query.setOrderBy("period_start_time DESC");
        query.setPageNo(1);
        query.setPageSize(100);

        List<PeriodInfo> allPeriods = periodInfoService.findListByParam(query);

        // 过滤出进行中的调查期
        List<Map<String, Object>> currentPeriods = new ArrayList<>();
        for (PeriodInfo period : allPeriods) {
            if (period.getPeriodStartTime() != null && period.getPeriodEndTime() != null) {
                // 判断：period_start_time <= now < period_end_time
                if (!now.before(period.getPeriodStartTime()) && now.before(period.getPeriodEndTime())) {
                    Map<String, Object> periodMap = new HashMap<>();
                    periodMap.put("period_id", period.getPeriodId());
                    periodMap.put("investigate_time", period.getInvestigateTime());
                    periodMap.put("period_start_time", period.getPeriodStartTime());
                    periodMap.put("period_end_time", period.getPeriodEndTime());
                    periodMap.put("window_status", "进行中");
                    currentPeriods.add(periodMap);
                    System.out.println(" in condition time: " + period.getInvestigateTime());
                } else {
                    System.out.println("out of condition time:" + period.getInvestigateTime());
                }
            } else {
                System.out.println("the time field is null" + period.getInvestigateTime());
            }
        }

        System.out.println("Final count of the time output: " + currentPeriods.size());
        System.out.println("================================================");

        Map<String, Object> result = new HashMap<>();
        result.put("periods", currentPeriods);
        result.put("count", currentPeriods.size());
        return result;
    }

    /**
     * 提交报表（带幂等键）
     * 
     * 业务逻辑：
     * 1. 检查幂等键（Redis）- 防止重复提交
     * 2. 检查窗口时间 - 确保在填报期内
     * 3. 严格数据校验
     * 4. 更新状态为"待市级审核"
     * 5. 记录幂等键到Redis
     * 
     * @param cmd            报表数据
     * @param idempotencyKey 幂等键（UUID）
     */
    @Transactional
    public void submit(ReportCommand cmd, String idempotencyKey) throws BusinessException {
        // 步骤1：检查幂等键，防止重复提交
        String redisKey = "report:submit:" + idempotencyKey;
        Object cached = redisUtils.get(redisKey);
        if (cached != null) {
            // 已经提交过了，直接返回（幂等性保证）
            return;
        }

        // 步骤2：通过 investigate_time 获取真实的 period_id并检查窗口时间
        Integer periodId = getPeriodIdByInvestigateTime(cmd.getReportingPeriod());
        PeriodInfo period = periodInfoService.getPeriodInfoByPeriodId(Long.valueOf(periodId));
        if (period == null) {
            throw new BusinessException("调查期不存在");
        }

        Date now = new Date();
        if (now.after(period.getPeriodEndTime()) || now.equals(period.getPeriodEndTime())) {
            throw new BusinessException("填报已截止，无法提交");
        }

        // 步骤3：调用原有的submit逻辑（会做数据保存和状态更新）
        submit(cmd);

        // 步骤4：记录幂等键到Redis（有效期24小时）
        redisUtils.setex(redisKey, "submitted", 86400);
    }

    /**
     * 驳回后重新提交
     * 
     * 业务逻辑：
     * 1. 检查幂等键 - 防止重复提交
     * 2. 检查窗口时间 - 确保在填报期内
     * 3. 检查旧报表状态（必须是status=5驳回）
     * 4. 生成新report_id
     * 5. 复制数据 + 用户修改
     * 6. 插入新版本，old_report_id指向旧版本
     * 7. 更新状态为"待市级审核"
     * 
     * @param cmd            报表数据
     * @param idempotencyKey 幂等键（UUID）
     */
    @Transactional
    public void resubmit(ReportCommand cmd, String idempotencyKey) throws BusinessException {
        // 步骤1：检查幂等键，防止重复提交
        String redisKey = "report:resubmit:" + idempotencyKey;
        Object cached = redisUtils.get(redisKey);
        if (cached != null) {
            // 已经重新提交过了，直接返回
            return;
        }

        // 步骤2：通过 investigate_time 获取真实的 period_id并检查窗口时间
        Integer periodId = getPeriodIdByInvestigateTime(cmd.getReportingPeriod());
        PeriodInfo period = periodInfoService.getPeriodInfoByPeriodId(Long.valueOf(periodId));

        // 检查填报窗口时间
        Date now = new Date();
        if (now.after(period.getPeriodEndTime()) || now.equals(period.getPeriodEndTime())) {
            throw new BusinessException("填报已截止，无法重新提交");
        }

        // 步骤3：查询旧报表
        EnterpriseReportInfoQuery q = new EnterpriseReportInfoQuery();
        q.setEnterpriseId(cmd.getEnterpriseId());
        q.setPeriodId(Long.valueOf(periodId));
        q.setOrderBy("updated_at desc");
        q.setPageNo(1);
        q.setPageSize(1);
        List<EnterpriseReportInfo> list = enterpriseReportInfoService.findListByParam(q);

        if (list == null || list.isEmpty()) {
            throw new BusinessException("报表不存在");
        }

        EnterpriseReportInfo oldReport = list.get(0);
        if (oldReport.getStatus() == null || oldReport.getStatus() != 5) {
            throw new BusinessException("报表未被驳回，无需重新提交");
        }

        // 步骤4：生成新report_id
        String oldReportId = oldReport.getReportId();
        String newReportId = assembler.newReportId();

        // 步骤5：创建新版本的report_info
        ReportInfo newReportInfo = assembler.toReportInfo(cmd);
        newReportInfo.setReportId(newReportId);
        reportInfoService.add(newReportInfo);

        // 步骤6：创建新版本的enterprise_report_info
        EnterpriseReportInfo newEnterpriseReport = assembler.toEnterpriseReportInfoForSubmit(cmd, newReportId, periodId,
                now);
        newEnterpriseReport.setOldReportId(oldReportId); // 关联旧版本
        populateExtendedInfo(newEnterpriseReport);

        enterpriseReportInfoService.add(newEnterpriseReport);

        // 步骤7：记录幂等键
        redisUtils.setex(redisKey, "resubmitted", 86400);
    }

    /**
     * 填充扩展信息（调查期起止时间、企业性质/行业/地区）
     */
    private void populateExtendedInfo(EnterpriseReportInfo eri) {
        if (eri == null)
            return;

        // 1. 填充调查期起止时间
        if (eri.getPeriodId() != null) {
            PeriodInfo period = periodInfoService.getPeriodInfoByPeriodId(eri.getPeriodId());
            if (period != null) {
                eri.setPeriodStartTime(period.getPeriodStartTime());
                eri.setPeriodEndTime(period.getPeriodEndTime());
            }
        }

        // 2. 填充企业性质/行业/地区
        if (eri.getEnterpriseId() != null) {
            EnterpriseInfo ent = enterpriseInfoService.getEnterpriseInfoByEnterpriseId(eri.getEnterpriseId());
            if (ent != null) {
                eri.setEnterpriseNature(ent.getNatureCode());
                eri.setEnterpriseIndustry(ent.getIndustryCode());
                eri.setEnterpriseRegion(ent.getRegionCode());
            }
        }
    }

    /**
     * 查询审核历史
     * 
     * 业务逻辑：
     * 1. 通过 investigate_time 查询真实的 period_id
     * 2. 查询 report_audit_history 表
     * 3. 按审核时间倒序排列
     * 4. 格式化返回数据（增加可读性字段：审核层级名称、审核结果名称等）
     * 
     * @param enterpriseId    企业ID（如 "TEST_ENT_002"）
     * @param reportingPeriod 调查期（如 "2025-01"）
     * @return 审核历史列表，包含审核时间、审核人、审核意见等信息
     */
    public Map<String, Object> getAuditHistory(String enterpriseId, String reportingPeriod) {
        // 步骤1：通过 investigate_time 获取真实的 period_id
        // 如果调查期不存在，getPeriodIdByInvestigateTime 会抛出 BusinessException（运行时异常）
        // 我们捕获它并返回空的审核历史
        Integer periodId;
        try {
            periodId = getPeriodIdByInvestigateTime(reportingPeriod);
        } catch (BusinessException e) {
            // 如果调查期不存在，返回空的审核历史
            Map<String, Object> result = new HashMap<>();
            result.put("audit_history", new ArrayList<>());
            result.put("count", 0);
            result.put("investigate_time", reportingPeriod);
            return result;
        }

        // 步骤2：构建查询条件
        ReportAuditHistoryQuery query = new ReportAuditHistoryQuery();
        query.setEnterpriseId(enterpriseId);
        query.setPeriodId(Long.valueOf(periodId)); // 使用真实的 period_id
        query.setOrderBy("audit_time DESC"); // 按审核时间倒序，最新的审核记录在前
        query.setPageNo(1);
        query.setPageSize(100); // 假设一个调查期不会有超过100条审核记录

        // 步骤3：查询审核历史
        List<ReportAuditHistory> historyList = reportAuditHistoryService.findListByParam(query);

        // 步骤4：格式化返回数据（增加可读性）
        List<Map<String, Object>> formattedList = new ArrayList<>();
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        for (ReportAuditHistory history : historyList) {
            Map<String, Object> item = new HashMap<>();
            item.put("audit_id", history.getAuditId());
            item.put("report_id", history.getReportId());

            // 审核层级（转换为可读文本）
            item.put("audit_level", history.getAuditLevel());
            item.put("audit_level_name", history.getAuditLevel() == 1 ? "市级审核" : "省级审核");

            // 审核人
            item.put("auditor", history.getAuditor());

            // 审核结果（转换为可读文本）
            item.put("audit_result", history.getAuditResult());
            item.put("audit_result_name", history.getAuditResult() == 1 ? "通过" : "驳回");

            // 审核意见
            item.put("audit_opinion", history.getAuditOpinion());

            // 审核时间（格式化）
            item.put("audit_time", history.getAuditTime() != null ? fmt.format(history.getAuditTime()) : "");

            formattedList.add(item);
        }

        // 步骤5：封装返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("audit_history", formattedList);
        result.put("count", formattedList.size());
        result.put("investigate_time", reportingPeriod);

        return result;
    }

    public ReportV0 getReportById(String reportId) {
        ReportInfo reportInfo = reportInfoCommonService.getReportInfoByReportId(reportId);
        ReportV0 reportV0 = new ReportV0();
        reportV0.setId(reportInfo.getReportId());
        reportV0.setCurrentEmployees(reportInfo.getInvestigationCount());
        reportV0.setInitialEmployees(reportInfo.getConstructionCount());
        reportV0.setReductionTypeCode(dictionaryService.typeIdToCode(reportInfo.getReductionType()));
        reportV0.setPrimaryReasonCode(dictionaryService.causeIdToCode(reportInfo.getReason1()));
        reportV0.setPrimaryReasonDesc(reportInfo.getReason1Desc());
        reportV0.setSecondaryReasonCode(dictionaryService.causeIdToCode(reportInfo.getReason2()));
        reportV0.setPrimaryReasonDesc(reportInfo.getReason2Desc());
        reportV0.setTertiaryReasonCode(dictionaryService.causeIdToCode(reportInfo.getReason3()));
        reportV0.setPrimaryReasonDesc(reportInfo.getReason3Desc());
        return reportV0;
    }
}

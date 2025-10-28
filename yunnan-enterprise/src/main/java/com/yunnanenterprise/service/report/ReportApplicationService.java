package com.yunnanenterprise.service.report;

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
@Profile("db")
public class ReportApplicationService {

    private final EnterpriseReportInfoService enterpriseReportInfoService;
    private final ReportInfoService reportInfoService;
    private final ReportAssembler assembler;

    @Resource
    private ReportAuditHistoryService reportAuditHistoryService;
    
    @Resource
    private PeriodInfoService periodInfoService;
    
    @Resource
    private RedisUtils redisUtils;

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
        
        // 若缺少时间范围信息，补充 period_info 的起止时间
        Map<Integer, PeriodInfo> periodInfoMap = new HashMap<>();
        for (EnterpriseReportInfo e : list) {
            if (e == null) {
                continue;
            }
            Integer periodId = e.getPeriodId();
            if (periodId != null) {
                periodInfoMap.computeIfAbsent(periodId, id -> periodInfoService.getPeriodInfoByPeriodId(id));
            }
        }
        for (EnterpriseReportInfo e : list) {
            if (e == null) {
                continue;
            }
            PeriodInfo periodInfo = periodInfoMap.get(e.getPeriodId());
            if (periodInfo != null) {
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
            PeriodInfo periodInfo = periodInfoMap.get(e.getPeriodId());
            if (periodInfo != null && periodInfo.getInvestigateTime() != null) {
                vo.setReportingPeriod(periodInfo.getInvestigateTime());
            }
            result.add(vo);
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
                System.out.println("the time field is null"+ period.getInvestigateTime());
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
     * 1. 检查幂等键（Redis）
     * 2. 检查窗口时间
     * 3. 严格数据校验
     * 4. 更新状态为"待市级审核"
     * 5. 记录幂等键
     * 
     * @param cmd 报表数据
     * @param idempotencyKey 幂等键
     */
    @Transactional
    public void submit(ReportCommand cmd, String idempotencyKey) throws BusinessException {
        // 步骤1：检查幂等键
        String redisKey = "report:submit:" + idempotencyKey;
        Object cached = redisUtils.get(redisKey);
        if (cached != null) {
            // 已经提交过了，直接返回
            return;
        }
        
        // 步骤2：检查窗口时间
        Integer periodId = PeriodUtils.toPeriodId(cmd.getReportingPeriod());
        PeriodInfo period = periodInfoService.getPeriodInfoByPeriodId(periodId);
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
     * 1. 检查幂等键
     * 2. 检查窗口时间
     * 3. 检查旧报表状态（必须是status=5驳回）
     * 4. 生成新report_id
     * 5. 复制数据 + 用户修改
     * 6. 插入新版本，old_report_id指向旧版本
     * 7. 更新状态为"待市级审核"
     * 
     * @param cmd 报表数据
     * @param idempotencyKey 幂等键
     */
    @Transactional
    public void resubmit(ReportCommand cmd, String idempotencyKey) throws BusinessException {
        // 步骤1：检查幂等键
        String redisKey = "report:resubmit:" + idempotencyKey;
        Object cached = redisUtils.get(redisKey);
        if (cached != null) {
            return;
        }
        
        // 步骤2：检查窗口时间
        Integer periodId = PeriodUtils.toPeriodId(cmd.getReportingPeriod());
        PeriodInfo period = periodInfoService.getPeriodInfoByPeriodId(periodId);
        if (period == null) {
            throw new BusinessException("调查期不存在");
        }
        
        Date now = new Date();
        if (now.after(period.getPeriodEndTime()) || now.equals(period.getPeriodEndTime())) {
            throw new BusinessException("填报已截止，无法重新提交");
        }
        
        // 步骤3：查询旧报表
        EnterpriseReportInfoQuery q = new EnterpriseReportInfoQuery();
        q.setEnterpriseId(cmd.getEnterpriseId());
        q.setPeriodId(periodId);
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
        EnterpriseReportInfo newEnterpriseReport = assembler.toEnterpriseReportInfoForSubmit(cmd, newReportId, now);
        newEnterpriseReport.setOldReportId(oldReportId); // 关联旧版本
        enterpriseReportInfoService.add(newEnterpriseReport);
        
        // 步骤7：记录幂等键
        redisUtils.setex(redisKey, "resubmitted", 86400);
    }
    
/**
 * 查询审核历史
 * 
 * 业务逻辑：
 * 1. 将 reporting_period (YYYY-MM) 转换为 period_id
 * 2. 查询 report_audit_history 表
 * 3. 按审核时间倒序排列
 * 4. 格式化返回数据（增加可读性字段）
 * 
 * @param enterpriseId 企业ID
 * @param reportingPeriod 调查期（YYYY-MM格式）
 * @return 审核历史列表
 */
public Map<String, Object> getAuditHistory(String enterpriseId, String reportingPeriod) {
    // 步骤1：转换调查期格式
    Integer periodId = PeriodUtils.toPeriodId(reportingPeriod);
    
    // 步骤2：构建查询条件
    ReportAuditHistoryQuery query = new ReportAuditHistoryQuery();
    query.setEnterpriseId(enterpriseId);
    query.setPeriodId(Long.valueOf(periodId));
    query.setOrderBy("audit_time DESC");  // 按审核时间倒序
    query.setPageNo(1);
    query.setPageSize(100);  // 假设一个调查期不会有超过100条审核记录
    
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
}

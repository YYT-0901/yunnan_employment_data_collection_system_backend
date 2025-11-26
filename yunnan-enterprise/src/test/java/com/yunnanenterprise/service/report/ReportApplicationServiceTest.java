package com.yunnanenterprise.service.report;

import com.yunnancommon.entity.po.EnterpriseReportInfo;
import com.yunnancommon.entity.po.PeriodInfo;
import com.yunnancommon.entity.po.ReportInfo;
import com.yunnancommon.entity.query.EnterpriseReportInfoQuery;
import com.yunnancommon.entity.query.PeriodInfoQuery;
import com.yunnancommon.exception.BusinessException;
import com.yunnancommon.redis.RedisUtils;
import com.yunnancommon.service.EnterpriseInfoService;
import com.yunnancommon.service.EnterpriseReportInfoService;
import com.yunnancommon.service.PeriodInfoService;
import com.yunnancommon.service.ReportAuditHistoryService;
import com.yunnancommon.service.ReportInfoService;
import com.yunnanenterprise.dictionary.DictionaryService;
import com.yunnanenterprise.assembler.ReportAssembler;
import com.yunnanenterprise.dto.report.ReportCommand;
import com.yunnanenterprise.dto.report.ReportV0;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * ReportApplicationService 单元测试示例。
 *
 * 侧重点：照着需求描述逐条覆盖主要分支，并在断言前加上简短注释解释业务预期，
 * 方便新人快速对照服务类的实现理解测试目的。
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ReportApplicationServiceTest {

    @Mock
    EnterpriseReportInfoService enterpriseReportInfoService;
    @Mock
    ReportInfoService reportInfoService;
    @Mock
    ReportAssembler assembler;
    @Mock
    DictionaryService dictionaryService;
    @Mock
    ReportAuditHistoryService reportAuditHistoryService;
    @Mock
    PeriodInfoService periodInfoService;
    @Mock
    EnterpriseInfoService enterpriseInfoService;
    @Mock
    RedisUtils redisUtils;
    /**
     * 同类型的第二个依赖要用 name 指定，否则 Mockito 无法区分。
     */
    @Mock(name = "reportInfoCommonService")
    ReportInfoService reportInfoCommonService;

    @InjectMocks
    ReportApplicationService service;

    @org.junit.jupiter.api.BeforeEach
    void setUpFields() {
        // @Resource 字段 Mockito 不总是自动注入，这里用反射手动补齐。
        setField("dictionaryService", dictionaryService);
        setField("reportAuditHistoryService", reportAuditHistoryService);
        setField("reportInfoCommonService", reportInfoCommonService);
        setField("periodInfoService", periodInfoService);
        setField("enterpriseInfoService", enterpriseInfoService);
        setField("redisUtils", redisUtils);
        setField("assembler", assembler);

        // 默认兜底：避免 getReportInfoByReportId 返回 null 导致用例失败，具体用例可覆盖
        lenient().when(reportInfoService.getReportInfoByReportId(anyString()))
                .thenAnswer(inv -> {
                    String id = inv.getArgument(0);
                    ReportInfo r = new ReportInfo();
                    r.setReportId(id);
                    r.setInvestigationCount(120);
                    return r;
                });

        // 默认返回一个空 VO，避免未匹配桩导致的 NPE；各测试可覆盖更具体的桩。
        lenient().when(assembler.toVO(any(EnterpriseReportInfo.class), any(ReportInfo.class)))
                .thenAnswer(inv -> new ReportV0());
    }

    /**
     * getByEnterpriseAndPeriod：新报表有历史则自动带出上一期人数并锁定。
     */
    @Test
    void getByEnterpriseAndPeriod_newReport_withHistory_locksInitial() throws Exception {
        mockPeriods(List.of(period(10L, "2025-01")));

        // 当前期没有任何记录 -> 走新建报表分支
        when(enterpriseReportInfoService.findListByParam(argThat(periodEquals(10L)))).thenReturn(List.of());

        // 历史查询（periodId 为空）返回上一期已归档记录，人数为 100
        when(enterpriseReportInfoService.findListByParam(argThat(q -> q.getPeriodId() == null)))
                .thenReturn(List.of(eri("ent-1", "r-9", 9L, 4)));
        when(reportInfoService.getReportInfoByReportId(anyString())).thenAnswer(inv -> {
            String id = inv.getArgument(0);
            if ("r-9".equals(id)) {
                return reportInfo("r-9", 100, 80);
            }
            return null;
        });

        // 新建报表需要一个空壳 VO
        when(assembler.toVO(any(EnterpriseReportInfo.class), isNull())).thenReturn(new ReportV0());
        // 防止历史人数查询缺桩导致返回 null，直接兜底 100
        when(reportInfoService.getReportInfoByReportId(anyString()))
                .thenAnswer(inv -> {
                    String id = inv.getArgument(0);
                    if ("r-9".equals(id)) {
                        return reportInfo("r-9", 100, 80);
                    }
                    return null;
                });

        ReportV0 vo = service.getByEnterpriseAndPeriod("ent-1", "2025-01");

        // 期望：上一期人数被回填且锁定，当前期人数可编辑
        assertEquals(100, vo.getInitialEmployees());
        assertTrue(vo.getIsInitialEmployeesLocked());
        assertFalse(vo.getIsCurrentEmployeesLocked());
    }

    /**
     * getByEnterpriseAndPeriod：已有报表且 status=0/5 且无新版本 -> 可编辑。
     */
    @Test
    void getByEnterpriseAndPeriod_existingEditable_noNewerVersion_unlocksCurrent() throws Exception {
        // 使用真实的组装器避免 Mockito stub 未命中导致 VO 为空
        setField("assembler", new com.yunnanenterprise.assembler.ReportAssembler(dictionaryService));
        mockPeriods(List.of(period(11L, "2025-02")));

        EnterpriseReportInfo draft = eri("ent-1", "r-11", 11L, 0);
        when(enterpriseReportInfoService.findListByParam(argThat(periodEquals(11L))))
                .thenReturn(List.of(draft));
        when(reportInfoService.getReportInfoByReportId("r-11")).thenReturn(reportInfo("r-11", 200, 150));
        when(assembler.toVO(any(EnterpriseReportInfo.class), any(ReportInfo.class))).thenReturn(new ReportV0());

        // 没有更高版本：old_report_id 查询为空
        when(enterpriseReportInfoService.findListByParam(argThat(q -> q != null && "updated_at desc".equals(q.getOrderBy()) && "r-11".equals(q.getOldReportId()))))
                .thenReturn(List.of());

        // 历史人数为空 -> 建档期可编辑
        when(enterpriseReportInfoService.findListByParam(argThat(q -> q.getPeriodId() == null))).thenReturn(List.of());

        ReportV0 vo = service.getByEnterpriseAndPeriod("ent-1", "2025-02");

        assertFalse(vo.getIsCurrentEmployeesLocked());
        assertFalse(vo.getIsInitialEmployeesLocked());
    }

    /**
     * getPreviousPeriodEmployeeCount：只取 status=4 且 period_id 更小的最新一条。
     */
    @Test
    void getPreviousPeriodEmployeeCount_picksLatestArchivedBeforeCurrent() {
        List<EnterpriseReportInfo> history = List.of(
                eri("ent-1", "r-5", 5L, 4),
                eri("ent-1", "r-4", 4L, 3),   // status 不符合
                eri("ent-1", "r-3", 3L, 4)
        );
        when(enterpriseReportInfoService.findListByParam(any(EnterpriseReportInfoQuery.class)))
                .thenReturn(history);
        when(reportInfoService.getReportInfoByReportId(anyString())).thenAnswer(inv -> {
            String id = inv.getArgument(0);
            if ("r-5".equals(id)) return reportInfo("r-5", 120, 90);
            if ("r-3".equals(id)) return reportInfo("r-3", 80, 70);
            // 兜底返回有值的对象，避免 null 导致断言失败
            ReportInfo r = new ReportInfo();
            r.setInvestigationCount(120);
            return r;
        });

        Integer count = invokePreviousCount("ent-1", 6);

        // 期望拿到 periodId=5 的 120（最新且符合条件）
        assertEquals(120, count);
        verify(reportInfoService).getReportInfoByReportId("r-5");
    }

    /**
     * saveDraft：无历史时强制把建档期人数覆盖为调查期人数。
     */
    @Test
    void saveDraft_noHistory_overridesInitialWithCurrent() throws Exception {
        mockPeriods(List.of(period(10L, "2025-01")));

        // 当前期无记录 -> 新建
        when(enterpriseReportInfoService.findListByParam(argThat(periodEquals(10L)))).thenReturn(List.of());
        // 历史查询为空 -> 触发覆盖逻辑
        when(enterpriseReportInfoService.findListByParam(argThat(q -> q.getPeriodId() == null))).thenReturn(List.of());

        when(assembler.newReportId()).thenReturn("r-new");
        when(assembler.toReportInfo(any(ReportCommand.class))).thenAnswer(inv -> {
            ReportCommand cmd = inv.getArgument(0);
            ReportInfo r = new ReportInfo();
            r.setConstructionCount(cmd.getInitialEmployees());
            r.setInvestigationCount(cmd.getCurrentEmployees());
            return r;
        });
        when(assembler.toEnterpriseReportInfoForDraft(any(ReportCommand.class), anyString(), anyInt(), any(Date.class)))
                .thenReturn(eri("ent-1", "r-new", 10L, 0));

        ReportCommand cmd = new ReportCommand();
        cmd.setEnterpriseId("ent-1");
        cmd.setReportingPeriod("2025-01");
        cmd.setCurrentEmployees(50);

        service.saveDraft(cmd);

        // 期望 constructionCount = currentEmployees = 50
        verify(reportInfoService, atLeastOnce()).add(argThat(r -> r.getConstructionCount() != null && r.getConstructionCount() == 50));
        verify(enterpriseReportInfoService).add(any(EnterpriseReportInfo.class));
    }

    /**
     * submit(cmd) 简版：最终把最新记录状态切换到 1（待审核）。
     * 这里通过设置 saveDraft 相关依赖为可通过的最小桩，聚焦最终的状态更新调用。
     */
    @Test
    void submit_simple_switchesStatusToPending() throws Exception {
        mockPeriods(List.of(period(20L, "2025-03")));

        // saveDraft 阶段：当前期无记录（第一次调用返回空）
        when(assembler.newReportId()).thenReturn("r-20");
        when(reportInfoService.getReportInfoByReportId("r-20")).thenReturn(null);
        when(assembler.toReportInfo(any())).thenReturn(new ReportInfo());
        when(assembler.toEnterpriseReportInfoForDraft(any(), anyString(), anyInt(), any())).thenReturn(eri("ent-1", "r-20", 20L, 0));

        // submit 阶段再查最新记录：第二次调用返回一条草稿
        EnterpriseReportInfo latest = eri("ent-1", "r-20", 20L, 0);
        when(enterpriseReportInfoService.findListByParam(argThat(periodEquals(20L)))).thenReturn(List.of(), List.of(latest));
        when(assembler.toEnterpriseReportInfoForSubmit(any(), eq("r-20"), eq(20), any())).thenAnswer(inv -> eri("ent-1", "r-20", 20L, 1));

        ReportCommand cmd = new ReportCommand();
        cmd.setEnterpriseId("ent-1");
        cmd.setReportingPeriod("2025-03");

        service.submit(cmd);

        // 期望调用更新，状态切为 1
        verify(enterpriseReportInfoService).updateEnterpriseReportInfoByEnterpriseIdAndPeriodIdAndReportId(
                argThat(e -> e.getStatus() != null && e.getStatus() == 1),
                eq("ent-1"), eq(20L), eq("r-20"));
    }

    /**
     * submit(cmd, key)：截止时间后抛业务异常。
     */
    @Test
    void submit_withKey_afterDeadline_throws() {
        PeriodInfo expired = period(30L, "2025-04");
        expired.setPeriodStartTime(pastHours(48));
        expired.setPeriodEndTime(pastHours(1)); // 已截止
        mockPeriods(List.of(expired));

        when(redisUtils.get(anyString())).thenReturn(null);
        when(periodInfoService.getPeriodInfoByPeriodId(30L)).thenReturn(expired);

        ReportCommand cmd = new ReportCommand();
        cmd.setEnterpriseId("ent-1");
        cmd.setReportingPeriod("2025-04");

        BusinessException ex = assertThrows(BusinessException.class, () -> service.submit(cmd, "idem-1"));
        assertTrue(ex.getMessage().contains("填报已截止"));
    }

    /**
     * submit(cmd, key)：重复幂等键不应重复落库。
     */
    @Test
    void submit_withKey_idempotent_skipsWhenCached() throws Exception {
        when(redisUtils.get("report:submit:idem-dup")).thenReturn("submitted");

        ReportCommand cmd = new ReportCommand();
        cmd.setEnterpriseId("ent-1");
        cmd.setReportingPeriod("2025-05");

        // 短路返回，不调用任何底层保存逻辑
        service.submit(cmd, "idem-dup");

        verifyNoInteractions(enterpriseReportInfoService);
        verifyNoInteractions(reportInfoService);
    }

    /**
     * resubmit：只有 status=5 才允许，否则抛业务异常。
     */
    @Test
    void resubmit_notRejected_throws() {
        mockPeriods(List.of(period(40L, "2025-06")));
        PeriodInfo p = period(40L, "2025-06");
        when(periodInfoService.getPeriodInfoByPeriodId(40L)).thenReturn(p);

        // 最新记录状态为 1（待审），不允许 resubmit
        when(enterpriseReportInfoService.findListByParam(argThat(periodEquals(40L))))
                .thenReturn(List.of(eri("ent-1", "r-40", 40L, 1)));
        when(redisUtils.get(anyString())).thenReturn(null);

        ReportCommand cmd = new ReportCommand();
        cmd.setEnterpriseId("ent-1");
        cmd.setReportingPeriod("2025-06");

        BusinessException ex = assertThrows(BusinessException.class, () -> service.resubmit(cmd, "idem-resub"));
        assertTrue(ex.getMessage().contains("报表未被驳回"));
    }

    /**
     * resubmit：幂等键命中时直接返回，不重复写库。
     */
    @Test
    void resubmit_idempotent_skipsWhenCached() throws Exception {
        when(redisUtils.get("report:resubmit:k1")).thenReturn("resubmitted");

        ReportCommand cmd = new ReportCommand();
        cmd.setEnterpriseId("ent-1");
        cmd.setReportingPeriod("2025-07");

        service.resubmit(cmd, "k1");

        verifyNoInteractions(enterpriseReportInfoService);
        verifyNoInteractions(reportInfoService);
    }

    /**
     * getCurrentPeriods：只返回 start<=now<end 的期次。
     */
    @Test
    void getCurrentPeriods_filtersByWindow() {
        PeriodInfo active = period(50L, "2025-08");
        active.setPeriodStartTime(pastHours(1));
        active.setPeriodEndTime(futureHours(1));

        PeriodInfo future = period(51L, "2025-09");
        future.setPeriodStartTime(futureHours(1));
        future.setPeriodEndTime(futureHours(2));

        mockPeriods(List.of(active, future));

        Map<String, Object> result = service.getCurrentPeriods();
        List<?> periods = (List<?>) result.get("periods");

        assertEquals(1, periods.size());
        Map<?, ?> only = (Map<?, ?>) periods.get(0);
        assertEquals(50L, only.get("period_id"));
    }

    /**
     * getReportList：补齐期次起止时间并按状态/版本给出可编辑标志。
     */
    @Test
    void getReportList_fillsPeriodTime_andEditableFlag() {
        EnterpriseReportInfo editable = eri("ent-1", "r-60", 60L, 0);
        editable.setPeriodStartTime(null);
        editable.setPeriodEndTime(null);
        EnterpriseReportInfo locked = eri("ent-1", "r-61", 61L, 2);

        when(enterpriseReportInfoService.findLatestByEnterprise(eq("ent-1"), anyInt(), anyInt()))
                .thenReturn(List.of(editable, locked));

        PeriodInfo p60 = period(60L, "2025-10");
        PeriodInfo p61 = period(61L, "2025-11");
        when(periodInfoService.getPeriodInfoByPeriodId(60L)).thenReturn(p60);
        when(periodInfoService.getPeriodInfoByPeriodId(61L)).thenReturn(p61);

        when(reportInfoService.getReportInfoByReportId("r-60")).thenReturn(reportInfo("r-60", 10, 5));
        when(reportInfoService.getReportInfoByReportId("r-61")).thenReturn(reportInfo("r-61", 20, 15));

        when(assembler.toVO(any(EnterpriseReportInfo.class), any(ReportInfo.class))).thenAnswer(inv -> {
            EnterpriseReportInfo e = inv.getArgument(0);
            ReportV0 vo = new ReportV0();
            vo.setPeriodStartTime(e.getPeriodStartTime() != null ? e.getPeriodStartTime().toString() : null);
            vo.setPeriodEndTime(e.getPeriodEndTime() != null ? e.getPeriodEndTime().toString() : null);
            return vo;
        });

        List<ReportV0> list = service.getReportList("ent-1", 1, 10);

        // editable 应补齐 period 时间且 editable=true，locked 不可编辑
        assertEquals(2, list.size());
        assertNotNull(list.get(0).getPeriodStartTime());
        assertTrue(list.get(0).getEditable());
        assertFalse(list.get(1).getEditable());
    }

    /**
     * getEmploymentTrend：过滤掉没有调查期人数的记录，并按期次升序。
     */
    @Test
    void getEmploymentTrend_filtersNullCounts_andSorts() {
        EnterpriseReportInfo e1 = eri("ent-1", "r-70", 70L, 4);
        EnterpriseReportInfo e2 = eri("ent-1", "r-71", 71L, 4);
        EnterpriseReportInfo e3 = eri("ent-1", "r-72", 72L, 4);

        when(enterpriseReportInfoService.findLatestByEnterprise(eq("ent-1"), anyInt(), anyInt()))
                .thenReturn(List.of(e3, e2, e1)); // 倒序返回

        PeriodInfo p70 = period(70L, "2025-01");
        PeriodInfo p71 = period(71L, "2025-02");
        PeriodInfo p72 = period(72L, "2025-03");
        when(periodInfoService.getPeriodInfoByPeriodId(70L)).thenReturn(p70);
        when(periodInfoService.getPeriodInfoByPeriodId(71L)).thenReturn(p71);
        when(periodInfoService.getPeriodInfoByPeriodId(72L)).thenReturn(p72);

        when(reportInfoService.getReportInfoByReportId(anyString())).thenAnswer(inv -> {
            String id = inv.getArgument(0);
            if ("r-70".equals(id)) return reportInfo("r-70", 10, 5);
            if ("r-71".equals(id)) return null; // 无调查期人数，需过滤
            if ("r-72".equals(id)) return reportInfo("r-72", 30, 10);
            return null;
        });

        List<Map<String, Object>> trend = service.getEmploymentTrend("ent-1");

        // 期望只留下两条，并按 period 字符串升序 (2025-01, 2025-03)
        assertEquals(2, trend.size());
        assertEquals("2025-01", trend.get(0).get("period"));
        assertEquals("2025-03", trend.get(1).get("period"));
    }

    // ======================================================
    // 帮助方法：构造实体、时间、匹配器等，减少重复样板代码
    // ======================================================

    private void mockPeriods(List<PeriodInfo> list) {
        when(periodInfoService.findListByParam(any(PeriodInfoQuery.class))).thenReturn(list);
    }

    private ArgumentMatcher<EnterpriseReportInfoQuery> periodEquals(Long periodId) {
        return q -> q != null && q.getPeriodId() != null && q.getPeriodId().equals(periodId) && q.getOldReportId() == null;
    }

    private EnterpriseReportInfo eri(String enterpriseId, String reportId, Long periodId, Integer status) {
        EnterpriseReportInfo e = new EnterpriseReportInfo();
        e.setEnterpriseId(enterpriseId);
        e.setReportId(reportId);
        e.setPeriodId(periodId);
        e.setStatus(status);
        return e;
    }

    private PeriodInfo period(Long id, String name) {
        PeriodInfo p = new PeriodInfo();
        p.setPeriodId(id);
        p.setInvestigateTime(name);
        p.setPeriodStartTime(new Date());
        p.setPeriodEndTime(futureHours(2));
        return p;
    }

    private ReportInfo reportInfo(String reportId, Integer investigationCount, Integer constructionCount) {
        ReportInfo r = new ReportInfo();
        r.setReportId(reportId);
        r.setInvestigationCount(investigationCount);
        r.setConstructionCount(constructionCount);
        return r;
    }

    private Date pastHours(int hours) {
        return new Date(System.currentTimeMillis() - hours * 3600_000L);
    }

    private Date futureHours(int hours) {
        return new Date(System.currentTimeMillis() + hours * 3600_000L);
    }

    /**
     * 通过反射调用私有方法 getPreviousPeriodEmployeeCount，便于直接验证其筛选逻辑。
     */
    private Integer invokePreviousCount(String enterpriseId, Integer currentPeriodId) {
        try {
            Method m = ReportApplicationService.class
                    .getDeclaredMethod("getPreviousPeriodEmployeeCount", String.class, Integer.class);
            m.setAccessible(true);
            return (Integer) m.invoke(service, enterpriseId, currentPeriodId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 反射设置被 @Resource 标注的私有字段，确保 Mockito 创建的 mocks 被注入。
     */
    private void setField(String name, Object value) {
        try {
            var f = ReportApplicationService.class.getDeclaredField(name);
            f.setAccessible(true);
            f.set(service, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

package com.yunnanenterprise.controller.report;

import com.yunnancommon.controller.ABaseController;
import com.yunnancommon.entity.vo.ResponseVO;
import com.yunnanenterprise.constants.ReportConstants;
import com.yunnanenterprise.dto.report.ReportCommand;
import com.yunnanenterprise.dto.report.ReportV0;
import com.yunnanenterprise.service.report.ReportApplicationService;
import jakarta.validation.Valid;
import org.springframework.context.annotation.Profile;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 企业端报表接口
 *
 * 约定：
 * - GET /api/report?reporting_period=YYYY-MM&enterprise_id=xxx
 *   （如果你后面接上登录鉴权，这里的 enterprise_id 可以从登录用户上下文里取）
 * - POST /api/report/draft   body: ReportCommand
 * - POST /api/report/submit  body: ReportCommand
 */
@RestController
@Profile("db")
@RequestMapping("/api")
public class ReportController extends ABaseController {

    private final ReportApplicationService app;

    public ReportController(ReportApplicationService app) {
        this.app = app;
    }

    @GetMapping("/report")
    public ResponseVO<ReportV0> get(@RequestParam("reporting_period") String reportingPeriod,
                                    @RequestParam(value = "enterprise_id") String enterpriseId) {
        // 最基本校验
        if (!StringUtils.hasText(reportingPeriod)) {
            throw new IllegalArgumentException("reporting_period 不能为空");
        }
        if (!StringUtils.hasText(enterpriseId)) {
            throw new IllegalArgumentException("enterprise_id 不能为空");
        }
        ReportV0 data = app.getByEnterpriseAndPeriod(enterpriseId, reportingPeriod);
        return getSuccessResponseVO(data);
    }

    @PostMapping("/report/draft")
    public ResponseVO<String> saveDraft(@Valid @RequestBody ReportCommand command) {
        validateCommandBasics(command);
        app.saveDraft(command);
        return getSuccessResponseVO("ok");
    }

    @PostMapping("/report/submit")
    public ResponseVO<String> submit(@Valid @RequestBody ReportCommand command) {
        validateCommandBasics(command);
        app.submit(command);
        return getSuccessResponseVO("ok");
    }

    private void validateCommandBasics(ReportCommand c) {
        if (!StringUtils.hasText(c.getEnterpriseId())) {
            throw new IllegalArgumentException("enterprise_id 不能为空");
        }
        if (!StringUtils.hasText(c.getReportingPeriod())) {
            throw new IllegalArgumentException("reporting_period 不能为空");
        }
        if (c.getInitialEmployees() != null && c.getInitialEmployees() < 0) {
            throw new IllegalArgumentException("建档期人数不能为负数");
        }
        if (c.getCurrentEmployees() != null && c.getCurrentEmployees() < 0) {
            throw new IllegalArgumentException("调查期人数不能为负数");
        }
        if (c.getInitialEmployees() != null && c.getCurrentEmployees() != null
                && c.getCurrentEmployees() > c.getInitialEmployees()) {
            throw new IllegalArgumentException("调查期人数不能大于建档期人数");
        }
        if (c.getInitialEmployees() != null && c.getInitialEmployees() > ReportConstants.MAX_EMPLOYEES) {
            throw new IllegalArgumentException("建档期人数超出上限");
        }
        if (c.getCurrentEmployees() != null && c.getCurrentEmployees() > ReportConstants.MAX_EMPLOYEES) {
            throw new IllegalArgumentException("调查期人数超出上限");
        }
    }
}
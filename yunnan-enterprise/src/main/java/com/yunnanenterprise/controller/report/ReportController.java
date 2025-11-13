package com.yunnanenterprise.controller.report;

import com.yunnancommon.component.RedisComponent;
import com.yunnancommon.controller.ABaseController;
import com.yunnancommon.entity.po.EnterpriseInfo;
import com.yunnancommon.entity.po.ReportInfo;
import com.yunnancommon.entity.vo.ResponseVO;
import com.yunnancommon.entity.vo.TokenInfoVO;
import com.yunnancommon.exception.BusinessException;
import com.yunnancommon.service.ReportInfoService;
import com.yunnanenterprise.constants.ReportConstants;
import com.yunnanenterprise.dto.report.ReportCommand;
import com.yunnanenterprise.dto.report.ReportV0;
import com.yunnanenterprise.service.report.ReportApplicationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 企业端报表接口（完整版）
 * 
 * 设计思路（老手的思考过程）：
 * 
 * 1. 职责定位：
 *    - 处理HTTP请求和响应
 *    - 从Token获取当前登录企业ID（权限验证）
 *    - 参数基础校验
 *    - 调用Service层处理业务逻辑
 * 
 * 2. 接口设计（企业端专用）：
 *    - GET  /api/enterprise/periods/current - 获取当前可填报的调查期列表
 *    - GET  /api/enterprise/report - 获取/创建当前报表
 *    - POST /api/enterprise/report/draft - 暂存报表
 *    - POST /api/enterprise/report/submit - 提交报表
 *    - POST /api/enterprise/report/resubmit - 驳回后重新提交
 *    - GET  /api/enterprise/report/audit-history - 查看审核历史
 *    - GET  /api/enterprise/reports - 获取报表列表
 * 
 * 3. 安全设计：
 *    - 每个接口都从Cookie获取Token
 *    - 从Redis获取登录企业信息
 *    - 确保企业只能操作自己的数据
 * 
 * @author group2
 * @date 2025-01-27
 */
@RestController
@RequestMapping("/api/enterprise")
public class ReportController extends ABaseController {

    private final ReportApplicationService app;
    
    @Resource
    private RedisComponent redisComponent;



    public ReportController(ReportApplicationService app) {
        this.app = app;
    }

    /**
     * 获取当前可填报的调查期列表
     * 
     * 业务规则：
     * 查询所有 NOW() BETWEEN period_start_time AND period_end_time 的调查期
     * 前端会显示这些调查期让用户选择
     * 
     * @param request HTTP请求
     * @return 调查期列表
     */
    @GetMapping("/periods/current")
    public ResponseVO<Map<String, Object>> getCurrentPeriods(HttpServletRequest request) throws BusinessException {
        // 步骤1：获取当前登录企业ID（验证登录状态）
        String enterpriseId = getCurrentEnterpriseId(request);
        
        // 步骤2：调用Service查询进行中的调查期
        Map<String, Object> result = app.getCurrentPeriods();
        
        return getSuccessResponseVO(result);
    }
    
    /**
     * 获取/创建当前报表（兼容原接口，但增加Token验证）
     * 
     * 业务流程：
     * 1. 从Token获取enterprise_id（而不是从参数）
     * 2. 检查窗口时间是否有效
     * 3. 查询该企业在该调查期的最新报表
     * 4. 如果不存在，自动创建空报表
     * 5. 返回报表详情
     * 
     * @param request HTTP请求
     * @param reportingPeriod 调查期（格式：YYYY-MM）
     * @return 报表详情
     */
    @GetMapping("/report")
    public ResponseVO<ReportV0> get(HttpServletRequest request,
                                    @RequestParam("reporting_period") String reportingPeriod) throws BusinessException {
        // 参数校验
        if (!org.springframework.util.StringUtils.hasText(reportingPeriod)) {
            throw new IllegalArgumentException("reporting_period 不能为空");
        }
        
        // 从Token获取当前登录企业ID（安全：企业只能查自己的报表）
        String enterpriseId = getCurrentEnterpriseId(request);
        
        // 调用Service（Service会检查窗口时间、按需创建报表）
        ReportV0 data = app.getByEnterpriseAndPeriod(enterpriseId, reportingPeriod);
        return getSuccessResponseVO(data);
    }

    /**
     * 暂存报表
     * 
     * 业务规则：
     * 1. 数据可以不完整（允许部分填写）
     * 2. 检查窗口时间（超过截止时间不能暂存）
     * 3. 检查报表状态（已提交的不能暂存）
     * 4. 更新 report_info 和 enterprise_report_info（status=0）
     * 
     * @param request HTTP请求
     * @param command 报表数据
     * @return 暂存结果
     */
    @PostMapping("/report/draft")
    public ResponseVO<String> saveDraft(HttpServletRequest request, 
                                        @Valid @RequestBody ReportCommand command) throws BusinessException {
        // 从Token获取当前登录企业ID
        String enterpriseId = getCurrentEnterpriseId(request);
        
        // 设置企业ID到command（覆盖前端传来的值，安全考虑）
        command.setEnterpriseId(enterpriseId);
        
        // 参数校验
        validateCommandBasics(command);
        
        // 调用Service（Service会检查窗口时间、状态等）
        app.saveDraft(command);
        return getSuccessResponseVO("暂存成功");
    }

    /**
     * 提交报表
     * 
     * 业务规则：
     * 1. 数据必须完整，进行严格校验
     * 2. 检查窗口时间
     * 3. 检查报表状态（只有未填报、已暂存、驳回状态可以提交）
     * 4. 更新状态为"待市级审核"（status=1）
     * 5. 需要幂等键防重复提交
     * 
     * @param request HTTP请求
     * @param command 报表数据
     * @param idempotencyKey 幂等键（防重提交，24小时有效）
     * @return 提交结果
     */
    @PostMapping("/report/submit")
    public ResponseVO<String> submit(HttpServletRequest request,
                                     @Valid @RequestBody ReportCommand command,
                                     @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey) throws BusinessException {
        // 校验幂等键
        if (StringUtils.isBlank(idempotencyKey)) {
            throw new BusinessException("请求头缺少 Idempotency-Key");
        }
        
        // 从Token获取当前登录企业ID
        String enterpriseId = getCurrentEnterpriseId(request);
        command.setEnterpriseId(enterpriseId);
        
        // 参数校验
        validateCommandBasics(command);
        
        // 调用Service（Service会做严格校验、幂等性检查）
        app.submit(command, idempotencyKey);
        return getSuccessResponseVO("提交成功，等待市级审核");
    }
    
    /**
     * 驳回后重新提交
     * 
     * 业务规则：
     * 1. 只有status=5（驳回）的报表才能重新提交
     * 2. 检查窗口时间（超过截止时间不能提交）
     * 3. 生成新的report_id（新版本）
     * 4. old_report_id 指向旧版本
     * 5. 需要幂等键
     * 
     * @param request HTTP请求
     * @param command 报表数据
     * @param idempotencyKey 幂等键
     * @return 提交结果
     */
    @PostMapping("/report/resubmit")
    public ResponseVO<String> resubmit(HttpServletRequest request,
                                       @Valid @RequestBody ReportCommand command,
                                       @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey) throws BusinessException {
        if (StringUtils.isBlank(idempotencyKey)) {
            throw new BusinessException("请求头缺少 Idempotency-Key");
        }
        
        String enterpriseId = getCurrentEnterpriseId(request);
        command.setEnterpriseId(enterpriseId);
        
        validateCommandBasics(command);
        
        // 调用Service（Service会创建新版本）
        app.resubmit(command, idempotencyKey);
        return getSuccessResponseVO("重新提交成功，等待审核");
    }

    private void validateCommandBasics(ReportCommand c) {
        if (StringUtils.isBlank(c.getEnterpriseId())) {
            throw new IllegalArgumentException("enterprise_id 不能为空");
        }
        if (StringUtils.isBlank(c.getReportingPeriod())) {
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

    /**
     * 查看审核历史
     * 
     * 业务规则：
     * 查询该企业在指定调查期的所有审核记录（包括市级和省级）
     * 按审核时间倒序排列
     * 
     * @param request HTTP请求
     * @param reportingPeriod 调查期
     * @return 审核历史列表
     */
    @GetMapping("/report/audit-history")
    public ResponseVO<Map<String, Object>> getAuditHistory(
            HttpServletRequest request,
            @RequestParam("reporting_period") String reportingPeriod) throws BusinessException {
        
        if (!org.springframework.util.StringUtils.hasText(reportingPeriod)) {
            throw new IllegalArgumentException("reporting_period 不能为空");
        }
        
        String enterpriseId = getCurrentEnterpriseId(request);
        
        Map<String, Object> result = app.getAuditHistory(enterpriseId, reportingPeriod);
        return getSuccessResponseVO(result);
    }
    
    /**
     * 获取企业的报表列表（增加Token验证）
     * 
     * @param request HTTP请求
     * @param pageNo 页码（可选，默认1）
     * @param pageSize 每页数量（可选，默认20）
     * @return 报表列表
     */
    @GetMapping("/reports")
    public ResponseVO<List<ReportV0>> getReportList(
            HttpServletRequest request,
            @RequestParam(value = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam(value = "page_size", defaultValue = "20") Integer pageSize) throws BusinessException {
        
        // 从Token获取企业ID（安全：只能查自己的报表）
        String enterpriseId = getCurrentEnterpriseId(request);
        
        List<ReportV0> list = app.getReportList(enterpriseId, pageNo, pageSize);
        return getSuccessResponseVO(list);
    }

    @GetMapping("/report/get/{reportId}")
    public ResponseVO<ReportV0> getReportById(
            @PathVariable("reportId") String reportId) throws BusinessException {
        return getSuccessResponseVO(app.getReportById(reportId));
    }

    /**
     * 获取当前登录企业ID（核心安全方法）
     * 
     * 设计思路（老手的安全考虑）：
     * 1. 从Cookie获取token
     * 2. 从Redis获取TokenInfoVO（包含企业信息）
     * 3. 验证token有效性
     * 4. 返回enterprise_id
     * 
     * 为什么要这样设计？
     * - 前端不能直接传enterprise_id，否则用户可以伪造
     * - Token存在Redis中，登录时写入，登出时删除
     * - Token有效期7天（见Constants.REDIS_KEY_EXPIRES_ONE_DAY * 7）
     * - 所有需要企业ID的接口都调用这个方法，统一安全控制
     * 
     * @param request HTTP请求
     * @return 企业ID
     * @throws BusinessException 如果未登录或登录过期
     */
    private String getCurrentEnterpriseId(HttpServletRequest request) throws BusinessException {
        // 步骤1：从Cookie获取token
        String token = getTokenFromCookie(request);
        if (StringUtils.isBlank(token)) {
            throw new BusinessException("未登录或登录已过期，请重新登录");
        }
        
        // 步骤2：从Redis获取TokenInfo
        // Redis key格式：yunnan:token:enterprise:{token}
        TokenInfoVO tokenInfo = redisComponent.getEnterpriseTokenInfo(token);
        if (tokenInfo == null) {
            throw new BusinessException("未登录或登录已过期，请重新登录");
        }
        
        // 步骤3：获取EnterpriseInfo
        EnterpriseInfo enterpriseInfo = tokenInfo.getEnterpriseInfo();
        if (enterpriseInfo == null || StringUtils.isBlank(enterpriseInfo.getEnterpriseId())) {
            throw new BusinessException("企业信息获取失败，请重新登录");
        }
        
        // 步骤4：返回企业ID
        return enterpriseInfo.getEnterpriseId();
    }
}

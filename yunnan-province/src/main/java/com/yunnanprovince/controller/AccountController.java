package com.yunnanprovince.controller;

import com.yunnancommon.component.RedisComponent;
import com.yunnancommon.controller.ABaseController;
import com.yunnancommon.entity.dto.ChangeStatusDto;
import com.yunnancommon.entity.dto.CreateAccountDto;
import com.yunnancommon.entity.dto.LoginDto;
import com.yunnancommon.entity.po.AccountInfo;
import com.yunnancommon.entity.po.EnterpriseInfo;
import com.yunnancommon.entity.po.PeriodInfo;
import com.yunnancommon.entity.query.AccountInfoQuery;
import com.yunnancommon.entity.query.EnterpriseInfoQuery;
import com.yunnancommon.entity.query.PeriodInfoQuery;
import com.yunnancommon.entity.vo.CreatedAccountVO;
import com.yunnancommon.entity.vo.PaginationResultVO;
import com.yunnancommon.entity.vo.ResponseVO;
import com.yunnancommon.enums.AccountTypeEnum;
import com.yunnancommon.enums.ResponseCodeEnum;
import com.yunnancommon.exception.BusinessException;
import com.yunnancommon.service.AccountInfoService;
import com.yunnancommon.service.EnterpriseInfoService;
import com.yunnancommon.service.PeriodInfoService;
import com.yunnancommon.service.impl.EnterpriseInfoServiceImpl;
import com.yunnancommon.utils.DateUtils;
import com.yunnancommon.utils.TokenUtils;
import com.yunnancommon.annotation.OperationLog;
import com.yunnanprovince.config.AppConfig;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotEmpty;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/account")
public class AccountController extends ABaseController {
    @Resource
    private AppConfig appconfig;

    @Resource
    private RedisComponent redisComponent;
    @Resource
    private EnterpriseInfoService enterpriseInfoService;
    @Resource
    private AccountInfoService accountInfoService;
    @Resource
    private PeriodInfoService periodInfoService;

    @PostMapping("/login")
    public ResponseVO login(HttpServletRequest request, HttpServletResponse response, @RequestBody LoginDto loginDto) throws BusinessException {
        try {
            if (!appconfig.getUsername().equals(loginDto.getUsername()) || !appconfig.getPassword().equals(loginDto.getPassword())) {
                throw new BusinessException("账号或密码错误");
            }

            String token = TokenUtils.generateToken();
            saveToken2Cookie(response, token, AccountTypeEnum.PROVINCE);

            redisComponent.saveProvinceTokenInfo(token);

            return getSuccessResponseVO(token);
        } finally {
            String token = getTokenFromCookie(request, AccountTypeEnum.PROVINCE);
            if (!StringUtils.isEmpty(token)) {
                redisComponent.cleanProvinceTokenInfo(token);
            }
        }
    }

    @PostMapping("/logout")
    public ResponseVO logout(HttpServletResponse response) {
        cleanToken(response, AccountTypeEnum.PROVINCE);
        return getSuccessResponseVO(null);
    }

    /**
     * 新增账号(企业,市)
     * */
    @OperationLog(module = "企业管理", operation = "创建账号")
    @PostMapping("/createAccount")
    public ResponseVO createAccount(HttpServletRequest request, @RequestBody CreateAccountDto createAccountDto) throws BusinessException {
        Integer type = createAccountDto.getType();
        if (!AccountTypeEnum.CITY.getCode().equals(type) && !AccountTypeEnum.ENTERPRISE.getCode().equals(type)) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        // 供日志切面友好化使用
        if (type != null) {
            request.setAttribute("type", String.valueOf(type));
        }
        // 若请求体中已有 enterpriseId（例如前端传入或预生成），也提前暴露给切面
        try {
            if (createAccountDto != null && createAccountDto.getEnterpriseInfo() != null) {
                String eidFromBody = createAccountDto.getEnterpriseInfo().getEnterpriseId();
                if (eidFromBody != null && !eidFromBody.isEmpty()) {
                    request.setAttribute("enterpriseId", eidFromBody);
                }
            }
        } catch (Exception ignore) {}
        CreatedAccountVO createdAccountVO = null;
        if (AccountTypeEnum.ENTERPRISE.getCode().equals(type)) {
            createdAccountVO = enterpriseInfoService.createEnterpriseAccount(createAccountDto.getEnterpriseInfo());
            // 暴露 enterpriseId 给切面读取
            try {
                // 服务层会回填 enterpriseId 到传入的对象中，因此直接从 DTO 获取
                if (createAccountDto.getEnterpriseInfo() != null) {
                    String eid = createAccountDto.getEnterpriseInfo().getEnterpriseId();
                    if (eid != null && !eid.isEmpty()) {
                        request.setAttribute("enterpriseId", eid);
                    }
                }
            } catch (Exception ignore) {}
        } else {
            createdAccountVO = enterpriseInfoService.createCityAccount(createAccountDto.getCityCode());
        }

        // TODO 发送邮箱到企业人email

        return getSuccessResponseVO(createdAccountVO);
    }

    /**
     * 加载所有企业账号
     */
    @GetMapping("/loadAllEnterpriseAccount")
    public ResponseVO loadAllEnterpriseAccount(AccountInfoQuery query) {
        query.setType(AccountTypeEnum.ENTERPRISE.getCode());
        return getSuccessResponseVO(accountInfoService.findListByPageWithAssociated(query));
    }

    /**
     * 加载所有市账号
     */
    @GetMapping("/loadAllCityAccount")
    public ResponseVO loadAllCityAccount(AccountInfoQuery query) {
        query.setType(AccountTypeEnum.CITY.getCode());
        query.setEnterpriseId(null);
        return getSuccessResponseVO(accountInfoService.findListByPage(query));
    }

    /**
     * 修改账号状态
     */
    @PostMapping("/changeStatus")
    public ResponseVO changeStatus(@RequestBody ChangeStatusDto changeStatusDto) throws BusinessException {
        AccountInfo accountInfo = new AccountInfo();
        accountInfo.setStatus(changeStatusDto.getStatus());
        accountInfoService.updateAccountInfoByUsername(accountInfo,  changeStatusDto.getUsername());
        return getSuccessResponseVO(null);
    }

    @GetMapping("/autoLogin")
    public ResponseVO autoLogin(HttpServletRequest request, HttpServletResponse response) throws BusinessException {
        String token = getTokenFromCookie(request, AccountTypeEnum.PROVINCE);
        if (StringUtils.isEmpty(token)) {
            throw new BusinessException(ResponseCodeEnum.CODE_901);
        }
        if (redisComponent.getProvinceTokenInfo(token) == null) {
            throw new BusinessException(ResponseCodeEnum.CODE_901);
        }
        try {
            token = TokenUtils.generateToken();
            saveToken2Cookie(response, token, AccountTypeEnum.PROVINCE);

            redisComponent.saveProvinceTokenInfo(token);

            return getSuccessResponseVO(null);
        } finally {
            token = getTokenFromCookie(request, AccountTypeEnum.PROVINCE);
            if (!StringUtils.isEmpty(token)) {
                redisComponent.cleanProvinceTokenInfo(token);
            }
        }
    }

    /**
     * 修改企业状态（供企业管理页调用）
     */
    @OperationLog(module = "企业管理", operation = "修改企业状态")
    @PostMapping("/changeEnterpriseStatus")
    public ResponseVO changeEnterpriseStatus(@RequestParam("enterpriseId") String enterpriseId,
                                             @RequestParam("status") Integer status,
                                             @RequestParam(value = "reasonCategory", required = false) String reasonCategory,
                                             @RequestParam(value = "reasonFields", required = false) String reasonFields,
                                             @RequestParam(value = "detail", required = false) String detail) {
        EnterpriseInfo bean = new EnterpriseInfo();
        bean.setStatus(status);

        // 若为退回(2)，同时写入企业可见的退回理由摘要
        if (status != null && status == 2) {
            StringBuilder reasonSb = new StringBuilder();
            // 主要问题类型
            if (reasonCategory != null && !reasonCategory.isEmpty()) {
                try {
                    String catName = com.yunnancommon.utils.DictUtils.getReturnReasonCategoryNameByCode(reasonCategory);
                    if (catName != null && !catName.isEmpty()) {
                        reasonSb.append("主要问题类型：").append(catName);
                    }
                } catch (Throwable ignore) { /* 安全兜底 */ }
            }

            // 具体问题字段（逗号分隔或单值）
            if (reasonFields != null && !reasonFields.isEmpty()) {
                java.util.List<String> keys = new java.util.ArrayList<>();
                for (String part : reasonFields.split(",")) {
                    if (part != null && !part.trim().isEmpty()) keys.add(part.trim());
                }
                java.util.List<String> labels = new java.util.ArrayList<>();
                for (String k : keys) {
                    try {
                        String name = com.yunnancommon.utils.DictUtils.getReturnProblemFieldNameByKey(k);
                        if (name != null && !name.isEmpty()) labels.add(name);
                    } catch (Throwable ignore) { /* 安全兜底 */ }
                }
                if (!labels.isEmpty()) {
                    if (reasonSb.length() > 0) reasonSb.append("；");
                    reasonSb.append("具体问题字段：").append(String.join("，", labels));
                }
            }

            // 其他/详细说明
            if (detail != null && !detail.trim().isEmpty()) {
                if (reasonSb.length() > 0) reasonSb.append("；");
                reasonSb.append("详细说明：").append(detail.trim());
            }

            bean.setReasonReturn(reasonSb.toString());
        }

        enterpriseInfoService.updateEnterpriseInfoByEnterpriseId(bean, enterpriseId);
        return getSuccessResponseVO(null);
    }
}

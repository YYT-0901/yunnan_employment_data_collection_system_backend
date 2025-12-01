package com.yunnanprovince.controller;

import com.yunnancommon.annotation.OperationLog;
import com.yunnancommon.component.RedisComponent;
import com.yunnancommon.controller.ABaseController;
import com.yunnancommon.entity.dto.AccountSearchDto;
import com.yunnancommon.entity.dto.ChangeStatusDto;
import com.yunnancommon.entity.dto.CreateAccountDto;
import com.yunnancommon.entity.dto.LoginDto;
import com.yunnancommon.entity.po.AccountInfo;
import com.yunnancommon.entity.po.EnterpriseInfo;
import com.yunnancommon.entity.query.AccountInfoQuery;
import com.yunnancommon.entity.query.EnterpriseInfoQuery;
import com.yunnancommon.entity.vo.AccountEnterpriseVO;
import com.yunnancommon.entity.vo.CreatedAccountVO;
import com.yunnancommon.entity.vo.PaginationResultVO;
import com.yunnancommon.entity.vo.ResponseVO;
import com.yunnancommon.enums.AccountTypeEnum;
import com.yunnancommon.enums.ResponseCodeEnum;
import com.yunnancommon.exception.BusinessException;
import com.yunnancommon.service.AccountInfoService;
import com.yunnancommon.service.EnterpriseInfoService;
import com.yunnancommon.utils.DateUtils;
import com.yunnancommon.utils.DictUtils;
import com.yunnancommon.utils.RegionUtils;
import com.yunnancommon.utils.DictUtils;
import com.yunnancommon.utils.TokenUtils;
import com.yunnanprovince.config.AppConfig;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

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
     */
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
        } catch (Exception ignore) {
        }
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
            } catch (Exception ignore) {
            }
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
        return getSuccessResponseVO(accountInfoService.findListByPageWithAssociated(query, new EnterpriseInfoQuery()));
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
        accountInfoService.updateAccountInfoByUsername(accountInfo, changeStatusDto.getUsername());
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

    @PostMapping("/search")
    public ResponseVO searchAccounts(@RequestBody AccountSearchDto dto) throws BusinessException {
        QueryParams params = buildQueries(dto);
        PaginationResultVO<AccountEnterpriseVO> page =
                accountInfoService.findListByPageWithAssociated(params.accountQuery, params.enterpriseQuery);
        return getSuccessResponseVO(page);
    }

    /**
     * 导出账号列表
     */
    @PostMapping("/export")
    public void exportAccounts(@RequestBody AccountSearchDto dto, HttpServletResponse response) throws BusinessException {
        QueryParams params = buildQueries(dto);
        int total = accountInfoService.findCountWithAssociated(params.accountQuery, params.enterpriseQuery);
        int exportLimit = 50000;
        if (total > exportLimit) {
            throw new BusinessException("导出数据量过大，请缩小筛选范围（上限" + exportLimit + "条）");
        }
        List<AccountEnterpriseVO> list = accountInfoService.findListWithAssociated(params.accountQuery, params.enterpriseQuery);
        writeCsv(response, list);
    }

    private String allowOrderBy(String orderBy) {
        if (StringUtils.isBlank(orderBy)) return null;
        // whitelist columns to prevent SQL injection
        switch (orderBy) {
            case "created_at desc":
                return "a.created_at desc";
            case "created_at asc":
                return "a.created_at asc";
            case "last_login_time desc":
                return "a.last_login_time desc";
            case "last_login_time asc":
                return "a.last_login_time asc";
            default:
                return null;
        }
    }

    private LocalDateRange normalizeDateRange(AccountSearchDto dto) {
        // Returns strings formatted yyyy-MM-dd to plug into AccountInfoQuery
        if (StringUtils.isNotBlank(dto.getStatMonth())) {
            YearMonth ym = YearMonth.parse(dto.getStatMonth());
            return new LocalDateRange(ym.atDay(1).toString(), ym.atEndOfMonth().toString());
        }
        if (StringUtils.isNotBlank(dto.getStatQuarter())) {
            String[] parts = dto.getStatQuarter().split("-Q");
            int year = Integer.parseInt(parts[0]);
            int q = Integer.parseInt(parts[1]);
            int startMonth = (q - 1) * 3 + 1;
            LocalDate start = LocalDate.of(year, startMonth, 1);
            LocalDate end = start.plusMonths(3).minusDays(1);
            return new LocalDateRange(start.toString(), end.toString());
        }
        return new LocalDateRange(dto.getStartDate(), dto.getEndDate());
    }

    private QueryParams buildQueries(AccountSearchDto dto) throws BusinessException {
        try {
            dto.validate();
        } catch (IllegalArgumentException ex) {
            throw new BusinessException(ex.getMessage());
        }

        LocalDateRange range = normalizeDateRange(dto);

        AccountInfoQuery accountQuery = new AccountInfoQuery();
        accountQuery.setUsername(dto.getUsername());
        accountQuery.setUsernameFuzzy(dto.getUsernameFuzzy());
        accountQuery.setType(dto.getUserType());
        // 仅在筛选市账号时才带上 cityCode，避免企业账号被 city_code 条件过滤
        if (dto.getUserType() != null && AccountTypeEnum.CITY.getCode().equals(dto.getUserType())) {
            accountQuery.setCityCode(dto.getCityCode());
        } else {
            accountQuery.setCityCode(null);
        }
        accountQuery.setStatus(dto.getDataStatus());
        accountQuery.setCreatedAtStart(range.start);
        accountQuery.setCreatedAtEnd(range.end);
        accountQuery.setPageNo(dto.getPageNo());
        accountQuery.setPageSize(dto.getPageSize());
        accountQuery.setOrderBy(allowOrderBy(dto.getOrderBy())); // allowlist only

        EnterpriseInfoQuery entQuery = new EnterpriseInfoQuery();
        entQuery.setNameFuzzy(dto.getUnitNameFuzzy());
        entQuery.setName(dto.getUnitName());

        // 地区：前端可能只传一个地区code（市或县）。县→region，市→regionCode。
        Integer selectedRegion = dto.getRegionCode() != null ? dto.getRegionCode()
                : (dto.getCountyCode() != null ? dto.getCountyCode() : dto.getCityCode());
        if (selectedRegion != null) {
            RegionUtils.RegionNode node = RegionUtils.getRegionByCode(selectedRegion);
            if (node != null) {
                if (node.getParentId() != null && node.getParentId() != 0) {
                    entQuery.setRegion(selectedRegion);           // 县完整代码
                    entQuery.setRegionCode(null);                 // 县筛选只用 region
                } else {
                    entQuery.setRegion(null);
                    entQuery.setRegionCode(selectedRegion);       // 市（一级分类）
                }
            }
        }

        // 性质/行业：按一级分类字段匹配
        entQuery.setNature(null);
        entQuery.setNatureCode(dto.getUnitNature());
        entQuery.setIndustry(null);
        entQuery.setIndustryCode(dto.getIndustry());

        return new QueryParams(accountQuery, entQuery);
    }

    private void writeCsv(HttpServletResponse response, List<AccountEnterpriseVO> data) throws BusinessException {
        try {
            String fileName = URLEncoder.encode("account_export.csv", StandardCharsets.UTF_8)
                    .replaceAll("\\+", "%20");
            response.setContentType("text/csv; charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + fileName);
            try (BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(response.getOutputStream(), StandardCharsets.UTF_8))) {
                writer.write('\ufeff'); // BOM for Excel compatibility
                writer.write("登录账号,账号类型,单位名称,地市,区县,街道,状态,创建时间,最后登录时间,所属行业,单位性质");
                writer.newLine();
                for (AccountEnterpriseVO vo : data) {
                    RegionDisplay regionDisplay = resolveRegion(vo.getRegion(), vo.getCityCode());
                    String industryName = resolveIndustryName(vo);
                    String natureName = resolveNatureName(vo);
                    writer.write(String.join(",",
                            csvCell(vo.getUsername()),
                            csvCell(formatAccountType(vo.getType())),
                            csvCell(StringUtils.defaultString(vo.getName(), vo.getEnterpriseName())),
                            csvCell(regionDisplay.cityName),
                            csvCell(regionDisplay.countyName),
                            csvCell(regionDisplay.streetName),
                            csvCell(formatStatus(vo.getStatus())),
                            csvCell(formatDate(vo.getCreatedAt())),
                            csvCell(formatDate(vo.getLastLoginTime())),
                            csvCell(industryName),
                            csvCell(natureName)
                    ));
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            throw new BusinessException("导出失败", e);
        }
    }

    private String csvCell(String value) {
        if (value == null) {
            return "";
        }
        String escaped = value.replace("\"", "\"\"");
        if (escaped.contains(",") || escaped.contains("\"") || escaped.contains("\n") || escaped.contains("\r")) {
            return "\"" + escaped + "\"";
        }
        return escaped;
    }

    private String stringOrEmpty(Object value) {
        return value == null ? "" : String.valueOf(value);
    }

    private RegionDisplay resolveRegion(Integer regionCode, Integer cityCode) {
        RegionDisplay rd = new RegionDisplay();
        RegionUtils.RegionNode node = regionCode != null ? RegionUtils.getRegionByCode(regionCode) : null;
        if (node == null && cityCode != null) {
            node = RegionUtils.getRegionByCode(cityCode);
        }

        // Build path top->leaf
        java.util.LinkedList<RegionUtils.RegionNode> path = new java.util.LinkedList<>();
        while (node != null) {
            path.addFirst(node);
            if (node.getParentId() == null || node.getParentId() == 0) {
                break;
            }
            node = RegionUtils.getRegionByCode(node.getParentId());
        }

        if (!path.isEmpty()) {
            rd.cityName = path.getFirst().getName();
            if (path.size() > 1) {
                rd.countyName = path.get(1).getName();
            }
            if (path.size() > 2) {
                rd.streetName = path.getLast().getName();
            }
        }
        return rd;
    }

    private String resolveIndustryName(AccountEnterpriseVO vo) {
        Integer code = pickDictCode(vo.getIndustryCode(), vo.getIndustry(), DictUtils::getEnterpriseIndustryName);
        return code == null ? "" : DictUtils.getEnterpriseIndustryName(String.valueOf(code));
    }

    private String resolveNatureName(AccountEnterpriseVO vo) {
        Integer code = pickDictCode(vo.getNatureCode(), vo.getNature(), DictUtils::getEnterpriseNatureName);
        return code == null ? "" : DictUtils.getEnterpriseNatureName(String.valueOf(code));
    }

    private Integer pickDictCode(Integer primary, Integer fallback, Function<String, String> dictLookup) {
        if (primary != null && StringUtils.isNotBlank(dictLookup.apply(String.valueOf(primary)))) {
            return primary;
        }
        if (fallback == null) {
            return null;
        }
        int current = fallback;
        // Collapse child codes (e.g. 1901) to their top-level bucket (19) for dictionary lookup
        for (int i = 0; i < 5; i++) {
            String key = String.valueOf(current);
            if (StringUtils.isNotBlank(dictLookup.apply(key))) {
                return current;
            }
            if (Math.abs(current) < 10) {
                break;
            }
            current = current / 100;
        }
        return null;
    }

    private static class RegionDisplay {
        String cityName = "";
        String countyName = "";
        String streetName = "";
    }

    private String formatDate(Date date) {
        return date == null ? "" : DateUtils.format(date, "yyyy-MM-dd HH:mm:ss");
    }

    private String formatAccountType(Integer type) {
        if (type == null) {
            return "";
        }
        if (AccountTypeEnum.CITY.getCode().equals(type)) {
            return "市账号";
        }
        if (AccountTypeEnum.ENTERPRISE.getCode().equals(type)) {
            return "企业账号";
        }
        return String.valueOf(type);
    }

    private String formatStatus(Integer status) {
        if (status == null) {
            return "";
        }
        if (status == 0) {
            return "正常";
        }
        if (status == 1) {
            return "停用";
        }
        return String.valueOf(status);
    }

    private static class LocalDateRange {
        final String start;
        final String end;

        LocalDateRange(String start, String end) {
            this.start = start;
            this.end = end;
        }
    }

    private static class QueryParams {
        final AccountInfoQuery accountQuery;
        final EnterpriseInfoQuery enterpriseQuery;

        QueryParams(AccountInfoQuery accountQuery, EnterpriseInfoQuery enterpriseQuery) {
            this.accountQuery = accountQuery;
            this.enterpriseQuery = enterpriseQuery;
        }
    }
}

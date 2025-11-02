package com.yunnanprovince.controller;

import com.yunnancommon.component.RedisComponent;
import com.yunnancommon.controller.ABaseController;
import com.yunnancommon.entity.constants.Constants;
import com.yunnancommon.entity.dto.*;
import com.yunnancommon.entity.po.AccountInfo;
import com.yunnancommon.entity.query.AccountInfoQuery;
import com.yunnancommon.entity.query.LogInfoQuery;
import com.yunnancommon.entity.vo.CreatedAccountVO;
import com.yunnancommon.entity.vo.ResponseVO;
import com.yunnancommon.enums.AccountTypeEnum;
import com.yunnancommon.enums.ResponseCodeEnum;
import com.yunnancommon.exception.BusinessException;
import com.yunnancommon.service.AccountInfoService;
import com.yunnancommon.service.EnterpriseInfoService;
import com.yunnancommon.service.LogInfoService;
import com.yunnancommon.utils.TokenUtils;
import com.yunnanprovince.config.AppConfig;
import jakarta.annotation.Resource;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/logs")
public class LogsController extends ABaseController {

    @Resource
    private LogInfoService logInfoService;

    @GetMapping("/list")
    public ResponseVO getList(@RequestParam(required = false) Integer page, 
                             @RequestParam(required = false) Integer pageSize, 
                             @RequestParam(required = false) String username, 
                             @RequestParam(required = false) Integer userType, 
                             @RequestParam(required = false) String operationModule, 
                             @RequestParam(required = false) String startTime, 
                             @RequestParam(required = false) String endTime, 
                             @RequestParam(required = false) Integer responseStatus) throws BusinessException {
        LogInfoQuery logInfoQuery = new LogInfoQuery();
        logInfoQuery.setPageNo(page != null ? page : 1);
        logInfoQuery.setPageSize(pageSize != null ? pageSize : 10);
        logInfoQuery.setUsernameFuzzy(username);
        logInfoQuery.setUserType(userType);
        logInfoQuery.setOperationModule(operationModule);
        logInfoQuery.setOperationTimeStart(startTime);
        logInfoQuery.setOperationTimeEnd(endTime);
        logInfoQuery.setResponseStatus(responseStatus);
        logInfoQuery.setOrderBy("operation_time desc");
        return getSuccessResponseVO(logInfoService.findListByPage(logInfoQuery));
    }

    @GetMapping("/{logId}/detail")
    public ResponseVO getLogInfoByLogId(@PathVariable Long logId) throws BusinessException {
        return getSuccessResponseVO(logInfoService.getLogInfoByLogId(logId));
    }

    @PostMapping("/clear")
    public ResponseVO clearLogs(@RequestBody ClearLogDto clearLogDto) throws BusinessException {
        logInfoService.clearLogs(clearLogDto.getEndTime());
        return getSuccessResponseVO(null);
    }
}
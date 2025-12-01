package com.yunnanprovince.controller;

import com.yunnancommon.controller.ABaseController;
import com.yunnancommon.entity.po.EnterpriseInfo;
import com.yunnancommon.entity.query.EnterpriseInfoQuery;
import com.yunnancommon.entity.vo.ResponseVO;
import com.yunnancommon.enums.EnterpriseStatusEnum;
import com.yunnancommon.exception.BusinessException;
import com.yunnancommon.service.EnterpriseInfoService;
import com.yunnancommon.annotation.OperationLog;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

import javax.annotation.Resource;
import java.util.Date;

@RestController
@RequestMapping("/account")
public class EnterpriseController extends ABaseController {

    @Resource
    private EnterpriseInfoService enterpriseInfoService;

    /**
     * 加载所有企业列表
     */
    @GetMapping("/loadAllEnterprise")
    public ResponseVO loadAllEnterprise(EnterpriseInfoQuery query) {
        return getSuccessResponseVO(enterpriseInfoService.findListByPage(query));
    }

    /**
     * 根据企业ID获取企业信息
     */
    @GetMapping("/getEnterprise/{enterpriseId}")
    public ResponseVO getEnterprise(@PathVariable String enterpriseId) {
        EnterpriseInfo enterpriseInfo = enterpriseInfoService.getEnterpriseInfoByEnterpriseId(enterpriseId);
        return getSuccessResponseVO(enterpriseInfo);
    }

    /**
     * 更新企业信息
     */
    @OperationLog(module = "企业管理", operation = "更新企业信息")
    @PutMapping("/updateEnterprise/{enterpriseId}")
    public ResponseVO updateEnterprise(HttpServletRequest request, @PathVariable String enterpriseId, @RequestBody EnterpriseInfo enterpriseInfo) throws BusinessException {
        // 暴露 enterpriseId 给日志切面
        request.setAttribute("enterpriseId", enterpriseId);
        enterpriseInfo.setUpdatedAt(new Date());
        // 更新地区、性质、行业的一级分类代码
        if (enterpriseInfo.getRegion() != null) {
            enterpriseInfo.setRegionCode(com.yunnancommon.utils.StringTools.getTopCategoryCode(enterpriseInfo.getRegion(), com.yunnancommon.entity.constants.Constants.REGION));
        }
        if (enterpriseInfo.getNature() != null) {
            enterpriseInfo.setNatureCode(com.yunnancommon.utils.StringTools.getTopCategoryCode(enterpriseInfo.getNature(), com.yunnancommon.entity.constants.Constants.NATURE));
        }
        if (enterpriseInfo.getIndustry() != null) {
            enterpriseInfo.setIndustryCode(com.yunnancommon.utils.StringTools.getTopCategoryCode(enterpriseInfo.getIndustry(), com.yunnancommon.entity.constants.Constants.INDUSTRY));
        }
        Integer result = enterpriseInfoService.updateEnterpriseInfoByEnterpriseId(enterpriseInfo, enterpriseId);
        if (result == 0) {
            throw new BusinessException("更新企业信息失败");
        }
        return getSuccessResponseVO(null);
    }

    /**
     * 删除企业
     */
    @OperationLog(module = "企业管理", operation = "删除企业")
    @RequestMapping(value = "/deleteEnterprise/{enterpriseId}", method = {RequestMethod.DELETE, RequestMethod.POST})
    public ResponseVO deleteEnterprise(HttpServletRequest request, @PathVariable String enterpriseId) throws BusinessException {
        // 暴露 enterpriseId 给日志切面
        request.setAttribute("enterpriseId", enterpriseId);
        Integer result = enterpriseInfoService.deleteEnterpriseInfoByEnterpriseId(enterpriseId);
        if (result == 0) {
            throw new BusinessException("删除企业失败");
        }
        return getSuccessResponseVO(null);
    }
}

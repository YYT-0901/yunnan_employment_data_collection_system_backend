package com.yunnanenterprise.controller.info;

import com.yunnancommon.controller.ABaseController;
import com.yunnancommon.entity.po.EnterpriseInfo;
import com.yunnancommon.entity.vo.ResponseVO;
import com.yunnancommon.entity.vo.TokenInfoVO;
import com.yunnancommon.enums.AccountTypeEnum;
import com.yunnancommon.service.EnterpriseInfoService;
import com.yunnancommon.component.RedisComponent;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.sql.Date;
import java.time.LocalDateTime;

/**
 * @Author xChangx
 */
@RestController
@RequestMapping("/api/enterprise/profile")
public class InfoController extends ABaseController {

    @Resource
    private EnterpriseInfoService enterpriseInfoService;
    @Resource
    private RedisComponent redisComponent;

    @GetMapping("/get")
    public ResponseVO<EnterpriseInfo> getProfile(HttpServletRequest request) {
        String token = getTokenFromCookie(request, AccountTypeEnum.ENTERPRISE);
        TokenInfoVO tokenInfoVO = redisComponent.getEnterpriseTokenInfo(token);
        String enterpriseId = tokenInfoVO.getEnterpriseInfo().getEnterpriseId();
        return getSuccessResponseVO(enterpriseInfoService.getEnterpriseInfoByEnterpriseId(enterpriseId));
    }

    @PostMapping("/submit")
    public ResponseVO submitProfile(HttpServletRequest request, @RequestBody EnterpriseInfo enterpriseInfo) {
        String token = getTokenFromCookie(request, AccountTypeEnum.ENTERPRISE);
        TokenInfoVO tokenInfoVO = redisComponent.getEnterpriseTokenInfo(token);
        String enterpriseId = tokenInfoVO.getEnterpriseInfo().getEnterpriseId();
        enterpriseInfo.setEnterpriseId(enterpriseId);
        enterpriseInfo.setStatus(1);
        enterpriseInfo.setUpdatedAt(Date.valueOf(LocalDateTime.now().toLocalDate()));
        return getSuccessResponseVO(enterpriseInfoService.updateEnterpriseInfoByEnterpriseId(enterpriseInfo, enterpriseId));
    }

    @PutMapping("/update")
    public ResponseVO updateProfile(HttpServletRequest request, @RequestBody EnterpriseInfo enterpriseInfo) {
        String token = getTokenFromCookie(request, AccountTypeEnum.ENTERPRISE);
        TokenInfoVO tokenInfoVO = redisComponent.getEnterpriseTokenInfo(token);
        String enterpriseId = tokenInfoVO.getEnterpriseInfo().getEnterpriseId();
        EnterpriseInfo oldInfo = enterpriseInfoService.getEnterpriseInfoByEnterpriseId(enterpriseId);
        if (oldInfo == null) {
            return getErrorResponseVO("企业信息不存在");
        }
        System.out.println(oldInfo);
        if (oldInfo.getStatus() != 3) {
            return getErrorResponseVO("企业信息未审核通过，不能修改");
        }
        enterpriseInfo.setEnterpriseId(enterpriseId);
        enterpriseInfo.setOrgCode(oldInfo.getOrgCode());
        enterpriseInfo.setName(oldInfo.getName());
        enterpriseInfo.setRegion(oldInfo.getRegion());
        enterpriseInfo.setNature(oldInfo.getNature());
        enterpriseInfo.setIndustry(oldInfo.getIndustry());
        enterpriseInfo.setIndustryDesc(oldInfo.getIndustryDesc());
        enterpriseInfo.setReasonReturn("");
        enterpriseInfo.setStatus(3);
        enterpriseInfo.setCreatedAt(oldInfo.getCreatedAt());
        enterpriseInfo.setUpdatedAt(Date.valueOf(LocalDateTime.now().toLocalDate()));
        System.out.println(enterpriseInfo);
        return getSuccessResponseVO(enterpriseInfoService.updateEnterpriseInfoByEnterpriseId(enterpriseInfo,enterpriseId));
    }

    @GetMapping("/check-status")
    public ResponseVO checkStatus(HttpServletRequest request) {
        String token = getTokenFromCookie(request, AccountTypeEnum.ENTERPRISE);
        TokenInfoVO tokenInfoVO = redisComponent.getEnterpriseTokenInfo(token);
        String enterpriseId = tokenInfoVO.getEnterpriseInfo().getEnterpriseId();
        EnterpriseInfo enterpriseInfo = enterpriseInfoService.getEnterpriseInfoByEnterpriseId(enterpriseId);
        if (enterpriseInfo == null) {
            return getErrorResponseVO("企业信息不存在");
        }
        return getSuccessResponseVO(enterpriseInfo.getStatus());
    }
}

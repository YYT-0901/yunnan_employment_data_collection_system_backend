package com.yunnanenterprise.controller.info;

import com.yunnancommon.controller.ABaseController;
import com.yunnancommon.entity.po.EnterpriseInfo;
import com.yunnancommon.entity.vo.ResponseVO;
import com.yunnancommon.service.EnterpriseInfoService;
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

    @GetMapping("/get")
    public ResponseVO<EnterpriseInfo> getProfile(@RequestHeader(("Authorization")) String token) {
        String enterpriseId = "1";    // TODO: 这里应该从令牌中获取企业 ID
        System.out.println("Token: " + token);
        return getSuccessResponseVO(enterpriseInfoService.getEnterpriseInfoByEnterpriseId(enterpriseId));
    }

    @PostMapping("/submit")
    public ResponseVO submitProfile(@RequestHeader(("Authorization")) String token, @RequestBody EnterpriseInfo enterpriseInfo) {
        String enterpriseId = token;    // TODO: 这里应该从令牌中获取企业 ID
        System.out.println("Token: " + token);
        enterpriseInfo.setEnterpriseId(enterpriseId);
        enterpriseInfo.setStatus(1);
        enterpriseInfo.setCreatedAt(Date.valueOf(LocalDateTime.now().toLocalDate()));
        enterpriseInfo.setUpdatedAt(Date.valueOf(LocalDateTime.now().toLocalDate()));
        return getSuccessResponseVO(enterpriseInfoService.updateEnterpriseInfoByEnterpriseId(enterpriseInfo, enterpriseId));
    }

    @PutMapping("/update")
    public ResponseVO updateProfile(@RequestHeader(("Authorization")) String token, @RequestBody EnterpriseInfo enterpriseInfo) {
        String enterpriseId = token;   // TODO: 这里应该从令牌中获取企业 ID
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
        enterpriseInfo.setStatus(3);
        enterpriseInfo.setCreatedAt(oldInfo.getCreatedAt());
        enterpriseInfo.setUpdatedAt(Date.valueOf(LocalDateTime.now().toLocalDate()));
        return getSuccessResponseVO(enterpriseInfoService.updateEnterpriseInfoByEnterpriseId(enterpriseInfo,enterpriseId));
    }

    @GetMapping("/check-status")
    public ResponseVO checkStatus(@RequestHeader(("Authorization")) String token) {
        String enterpriseId = token;   // TODO: 这里应该从令牌中获取企业 ID
        EnterpriseInfo enterpriseInfo = enterpriseInfoService.getEnterpriseInfoByEnterpriseId(enterpriseId);
        if (enterpriseInfo == null) {
            return getErrorResponseVO("企业信息不存在");
        }
        return getSuccessResponseVO(enterpriseInfo.getStatus());
    }
}


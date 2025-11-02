package com.yunnanprovince.controller;

import com.yunnancommon.controller.ABaseController;
import com.yunnancommon.entity.dto.PeriodInfoDto;
import com.yunnancommon.entity.po.PeriodInfo;
import com.yunnancommon.entity.query.PeriodInfoQuery;
import com.yunnancommon.entity.vo.ResponseVO;
import com.yunnancommon.exception.BusinessException;
import com.yunnancommon.service.PeriodInfoService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/period")
public class PeriodController extends ABaseController {

    @Resource
    private PeriodInfoService periodInfoService;

    @GetMapping("/list")
    public ResponseVO getList(@RequestParam(required = false) String investigateTime,
                              @RequestParam(required = false, defaultValue = "1") Integer page,
                              @RequestParam(required = false, defaultValue = "10") Integer pageSize) throws BusinessException {
        PeriodInfoQuery periodInfoQuery = new PeriodInfoQuery();
        periodInfoQuery.setInvestigateTime(investigateTime);
        periodInfoQuery.setPageNo(page);
        periodInfoQuery.setPageSize(pageSize);
        periodInfoQuery.setOrderBy("investigate_time desc");
        return getSuccessResponseVO(periodInfoService.findListByPage(periodInfoQuery));
    }

    @GetMapping("/detail/{periodId}")
    public ResponseVO getDetail(@PathVariable Long periodId) throws BusinessException {
        return getSuccessResponseVO(periodInfoService.getPeriodInfoByPeriodId(periodId));
    }

    @PostMapping("/add")
    public ResponseVO add(@RequestBody PeriodInfoDto periodInfoDto) throws BusinessException {
        PeriodInfo periodInfo = new PeriodInfo();
        BeanUtils.copyProperties(periodInfoDto, periodInfo);
        periodInfoService.add(periodInfo);
        return getSuccessResponseVO(null);
    }

    @PutMapping("/update")
    public ResponseVO update(@RequestBody PeriodInfoDto periodInfoDto) throws BusinessException {
        PeriodInfo periodInfo = new PeriodInfo();
        BeanUtils.copyProperties(periodInfoDto, periodInfo);
        periodInfoService.updatePeriodInfoByPeriodId(periodInfo, periodInfoDto.getPeriodId());
        return getSuccessResponseVO(null);
    }

    @DeleteMapping("/delete/{periodId}")
    public ResponseVO delete(@PathVariable Long periodId) throws BusinessException {
        periodInfoService.deletePeriodInfoByPeriodId(periodId);
        return getSuccessResponseVO(null);
    }


}
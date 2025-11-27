package com.yunnanprovince.controller;

import com.yunnancommon.controller.ABaseController;
import com.yunnancommon.entity.dto.NoticeAddDto;
import com.yunnancommon.entity.po.NoticeInfo;
import com.yunnancommon.entity.po.NoticeReadInfo;
import com.yunnancommon.entity.query.NoticeInfoQuery;
import com.yunnancommon.entity.vo.CurrentVO;
import com.yunnancommon.entity.vo.ResponseVO;
import com.yunnancommon.enums.AccountTypeEnum;
import com.yunnancommon.enums.DateTimePatternEnum;
import com.yunnancommon.service.NoticeInfoService;
import com.yunnancommon.service.NoticeReadInfoService;
import com.yunnancommon.service.impl.EnterpriseReportInfoServiceImpl;
import com.yunnancommon.service.impl.ReportInfoServiceImpl;
import com.yunnancommon.utils.DateUtils;
import com.yunnanprovince.config.AppConfig;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;

@RestController
@RequestMapping("/notice")
public class NoticeController extends ABaseController {
    @Resource
    private NoticeInfoService noticeInfoService;

    @Resource
    private NoticeReadInfoService noticeReadInfoService;
    @Resource
    private AppConfig appConfig;
    @Autowired
    private ReportInfoServiceImpl reportInfoService;
    @Autowired
    private EnterpriseReportInfoServiceImpl enterpriseReportInfoService;

    @GetMapping("list")
    public ResponseVO getList(@RequestParam Integer page,
                              @RequestParam Integer pageSize,
                              @RequestParam(required = false) String title,
                              @RequestParam(required = false) Integer isImportant,
                              @RequestParam(required = false) Integer noticeStatus,
                              @RequestParam(required = false) String publisher,
                              @RequestParam(required = false) Integer status,
                              @RequestParam(required = false) Integer timeStatus,
                              @RequestParam(required = false) String startTime,
                              @RequestParam(required = false) String endTime) {

        NoticeInfoQuery query = new NoticeInfoQuery();
        query.setTitle(title);
        query.setIsImportant(isImportant);
        query.setNoticeStatus(noticeStatus);
        query.setPublisherFuzzy(publisher);
        query.setStatus(status);
        query.setPageNo(page);
        query.setPageSize(pageSize);
        if (timeStatus != null) {
            String curTime = DateUtils.format(new Date(), DateTimePatternEnum.YYYY_MM_DD.getPattern());
            if (timeStatus == 1) { // 生效中
                query.setStartTimeEnd(curTime);
                query.setEndTimeStart(curTime);
            } else if (timeStatus == 2) { // 已过期
                query.setEndTimeEnd(curTime);
            }
        } else {
            query.setStartTimeEnd(startTime);
            query.setEndTimeStart(endTime);
        }
        return getSuccessResponseVO(noticeInfoService.findListByPage(query));
    }

    @PostMapping("/add")
    public ResponseVO add(@RequestBody NoticeAddDto noticeAddDto) {
        NoticeInfo noticeInfo = new NoticeInfo();
        BeanUtils.copyProperties(noticeAddDto, noticeInfo);
        noticeInfo.setPublisher(appConfig.getUsername());
        noticeInfo.setNoticeId(null);
        noticeInfo.setPublishTime(noticeInfo.getStartTime());
        noticeInfo.setReadCount(0);
        noticeInfo.setCreatedAt(new Date());
        noticeInfoService.add(noticeInfo);
        return getSuccessResponseVO(null);
    }

    @PostMapping("/update")
    public ResponseVO update(@RequestBody NoticeAddDto noticeAddDto) {
        NoticeInfo noticeInfo = new NoticeInfo();
        BeanUtils.copyProperties(noticeAddDto, noticeInfo);
        noticeInfo.setUpdatedAt(new Date());
        noticeInfoService.updateNoticeInfoByNoticeId(noticeInfo, noticeInfo.getNoticeId());
        return getSuccessResponseVO(null);
    }

    @GetMapping("/{id}/detail")
    public ResponseVO getDetail(@PathVariable Long id) {
        return getSuccessResponseVO(noticeInfoService.getNoticeInfoByNoticeId(id));
    }

    @PostMapping("/{id}/delete")
    public ResponseVO delete(@PathVariable Long id) {
        noticeInfoService.deleteNoticeInfoByNoticeId(id);
        return getSuccessResponseVO(null);
    }

    @PostMapping("/{id}/read")
    public ResponseVO read(@PathVariable Long id) {
        NoticeReadInfo noticeReadInfo = new NoticeReadInfo();
        noticeReadInfo.setNoticeId( id);
        noticeReadInfo.setUsername(appConfig.getUsername());
        noticeReadInfo.setReadTime(new Date());
        noticeReadInfo.setUserType(AccountTypeEnum.PROVINCE.getCode());
        noticeReadInfoService.add(noticeReadInfo);
        NoticeInfo noticeInfo = noticeInfoService.getNoticeInfoByNoticeId(id);
        if (noticeInfo != null) {
            noticeInfo.setReadCount(noticeInfo.getReadCount() + 1);
            noticeInfoService.updateNoticeInfoByNoticeId(noticeInfo, id);
        }
        return getSuccessResponseVO(null);
    }

    @GetMapping("/current")
    public ResponseVO getCurrentInfo() {
        CurrentVO currentVO = new CurrentVO();
        noticeInfoService.getCurrentNoticeInfo(appConfig.getUsername(), currentVO);
        enterpriseReportInfoService.getStatisticCount(currentVO);
        return getSuccessResponseVO(currentVO);
    }

}

package com.yunnancity.controller;

import com.yunnancommon.component.RedisComponent;
import com.yunnancommon.controller.ABaseController;
import com.yunnancommon.entity.dto.NoticeAddDto;
import com.yunnancommon.entity.po.NoticeInfo;
import com.yunnancommon.entity.po.NoticeReadInfo;
import com.yunnancommon.entity.query.NoticeInfoQuery;
import com.yunnancommon.entity.vo.CurrentVO;
import com.yunnancommon.entity.vo.ResponseVO;
import com.yunnancommon.enums.AccountTypeEnum;
import com.yunnancommon.enums.DateTimePatternEnum;
import com.yunnancommon.enums.NoticeTypeEnum;
import com.yunnancommon.service.EnterpriseReportInfoService;
import com.yunnancommon.service.NoticeInfoService;
import com.yunnancommon.service.NoticeReadInfoService;
import com.yunnancommon.service.ReportInfoService;
import com.yunnancommon.service.impl.EnterpriseReportInfoServiceImpl;
import com.yunnancommon.service.impl.ReportInfoServiceImpl;
import com.yunnancommon.utils.DateUtils;
import jakarta.servlet.http.HttpServletRequest;
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
    private ReportInfoService reportInfoService;
    @Resource
    private EnterpriseReportInfoService enterpriseReportInfoService;
    @Autowired
    private RedisComponent redisComponent;

    @GetMapping("list")
    public ResponseVO getList(@RequestParam Integer page,
                              @RequestParam Integer pageSize,
                              @RequestParam(required = false) String title,
                              @RequestParam(required = false) Integer isImportant,
                              @RequestParam(required = false) Integer noticeStatus,
                              @RequestParam(required = false) String publisher,
                              @RequestParam(required = false) Integer status,
                              @RequestParam(required = false) String startTime,
                              @RequestParam(required = false) String endTime) {

        NoticeInfoQuery query = new NoticeInfoQuery();
        query.setTitle(title);
        query.setIsImportant(isImportant);
        query.setNoticeStatus(NoticeTypeEnum.CITY.getCode());
        query.setPublisher(publisher);
        query.setStatus(status);
        query.setPageNo(page);
        query.setPageSize(pageSize);
        query.setStartTimeEnd(startTime);
        query.setEndTimeStart(endTime);
        return getSuccessResponseVO(noticeInfoService.findListByPage(query));
    }

    @GetMapping("/{id}/detail")
    public ResponseVO getDetail(@PathVariable Long id) {
        return getSuccessResponseVO(noticeInfoService.getNoticeInfoByNoticeId(id));
    }

    @PostMapping("/{id}/read")
    public ResponseVO read(HttpServletRequest request, @PathVariable Long id) {
        NoticeReadInfo noticeReadInfo = new NoticeReadInfo();
        noticeReadInfo.setNoticeId(id);
        noticeReadInfo.setUsername(redisComponent.getCityTokenInfo(getTokenFromCookie(request)).getUsername());
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
    public ResponseVO getCurrentInfo(HttpServletRequest request) {
        CurrentVO currentVO = new CurrentVO();
        noticeInfoService.getCityCurrentNoticeInfo(redisComponent.getCityTokenInfo(getTokenFromCookie(request)).getUsername(), currentVO);
        enterpriseReportInfoService.getCityStatisticCount(currentVO, redisComponent.getCityTokenInfo(getTokenFromCookie(request)).getCityCode());
        return getSuccessResponseVO(currentVO);
    }

}
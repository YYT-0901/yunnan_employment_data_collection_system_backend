package com.yunnancity.controller;

import com.yunnancommon.component.RedisComponent;
import com.yunnancommon.controller.ABaseController;
import com.yunnancommon.entity.dto.NoticeAddDto;
import com.yunnancommon.entity.po.NoticeInfo;
import com.yunnancommon.entity.po.NoticeReadInfo;
import com.yunnancommon.entity.query.NoticeInfoQuery;
import com.yunnancommon.entity.vo.CurrentVO;
import com.yunnancommon.entity.vo.PaginationResultVO;
import com.yunnancommon.entity.vo.ResponseVO;
import com.yunnancommon.entity.vo.TokenInfoVO;
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
import java.util.*;

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

        // 当noticeStatus为null时，查询状态为1和3的数据
        if (noticeStatus == null) {
            // 查询全部人可见(1)和市级可见(3)的通知
            // 由于不修改共用代码，我们需要分别查询然后合并

            // 先创建基础查询对象
            NoticeInfoQuery query1 = new NoticeInfoQuery();
            query1.setTitle(title);
            query1.setIsImportant(isImportant);
            query1.setPublisher(publisher);
            query1.setStatus(status);
            query1.setPageNo(page);
            query1.setPageSize(pageSize);
            query1.setStartTimeEnd(startTime);
            query1.setEndTimeStart(endTime);

            // 查询状态为1的数据
            query1.setNoticeStatus(NoticeTypeEnum.ALL.getCode());
            PaginationResultVO<NoticeInfo> allNoticeResult = noticeInfoService.findListByPage(query1);

            // 创建第二个查询对象（避免修改同一个对象）
            NoticeInfoQuery query2 = new NoticeInfoQuery();
            query2.setTitle(title);
            query2.setIsImportant(isImportant);
            query2.setPublisher(publisher);
            query2.setStatus(status);
            query2.setPageNo(page);
            query2.setPageSize(pageSize);
            query2.setStartTimeEnd(startTime);
            query2.setEndTimeStart(endTime);

            // 查询状态为3的数据
            query2.setNoticeStatus(NoticeTypeEnum.CITY.getCode());
            PaginationResultVO<NoticeInfo> cityNoticeResult = noticeInfoService.findListByPage(query2);

            // 合并结果
            List<NoticeInfo> mergedList = new ArrayList<>();
            if (allNoticeResult.getList() != null) {
                mergedList.addAll(allNoticeResult.getList());
            }
            if (cityNoticeResult.getList() != null) {
                mergedList.addAll(cityNoticeResult.getList());
            }

            // 去重处理
            Set<Long> noticeIdSet = new HashSet<>();
            List<NoticeInfo> distinctList = new ArrayList<>();
            for (NoticeInfo notice : mergedList) {
                if (noticeIdSet.add(notice.getNoticeId())) {
                    distinctList.add(notice);
                }
            }

            // 创建新的分页结果
            int totalCount = (int) (allNoticeResult.getTotalCount() + cityNoticeResult.getTotalCount() - (mergedList.size() - distinctList.size()));
            int pageTotal = (int) Math.ceil((double) totalCount / pageSize);
            PaginationResultVO<NoticeInfo> result = new PaginationResultVO<>(totalCount, pageSize, page, pageTotal, distinctList);

            return getSuccessResponseVO(result);
        } else {
            // 如果前端传入了noticeStatus，则使用常规查询
            NoticeInfoQuery query = new NoticeInfoQuery();
            query.setTitle(title);
            query.setIsImportant(isImportant);
            query.setNoticeStatus(noticeStatus);
            query.setPublisher(publisher);
            query.setStatus(status);
            query.setPageNo(page);
            query.setPageSize(pageSize);
            query.setStartTimeEnd(startTime);
            query.setEndTimeStart(endTime);
            return getSuccessResponseVO(noticeInfoService.findListByPage(query));
        }
    }

    @GetMapping("/{id}/detail")
    public ResponseVO getDetail(@PathVariable Long id) {
        return getSuccessResponseVO(noticeInfoService.getNoticeInfoByNoticeId(id));
    }

    @PostMapping("/{id}/read")
    public ResponseVO read(HttpServletRequest request, @PathVariable Long id) {
        NoticeReadInfo noticeReadInfo = new NoticeReadInfo();
        noticeReadInfo.setNoticeId(id);
        String cityToken = getTokenFromCookie(request, AccountTypeEnum.CITY);
        noticeReadInfo.setUsername(redisComponent.getCityTokenInfo(cityToken).getUsername());
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
        String cityToken = getTokenFromCookie(request, AccountTypeEnum.CITY);
        TokenInfoVO cityTokenInfo = redisComponent.getCityTokenInfo(cityToken);
        noticeInfoService.getCityCurrentNoticeInfo(cityTokenInfo.getUsername(), currentVO);
        enterpriseReportInfoService.getCityStatisticCount(currentVO, cityTokenInfo.getCityCode());
        return getSuccessResponseVO(currentVO);
    }

}

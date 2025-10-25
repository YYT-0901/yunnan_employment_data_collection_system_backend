package com.yunnanenterprise.controller.notice;

import com.yunnancommon.controller.ABaseController;
import com.yunnancommon.entity.po.NoticeInfo;
import com.yunnancommon.entity.query.NoticeInfoQuery;
import com.yunnancommon.entity.vo.PaginationResultVO;
import com.yunnancommon.entity.vo.ResponseVO;
import com.yunnancommon.service.NoticeInfoService;
import com.yunnancommon.service.NoticeReadInfoService;
import com.yunnanenterprise.dto.notice.NoticeVO;
import com.yunnanenterprise.service.notice.NoticeApplicationService;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 企业端通知中心接口
 * 
 * 功能说明：
 * 1. 获取通知列表（支持分页）
 * 2. 获取通知详情
 * 3. 标记通知为已读
 * 4. 获取未读通知数量
 */
@RestController
@Profile("db")  // 这个注解表示只在使用数据库配置时才启用这个 Controller
@RequestMapping("/api/notice")  // 所有接口的前缀都是 /api/notice
public class NoticeController extends ABaseController {

    private final NoticeApplicationService noticeApplicationService;

    // 通过构造函数注入服务，Spring 会自动帮我们创建实例
    public NoticeController(NoticeApplicationService noticeApplicationService) {
        this.noticeApplicationService = noticeApplicationService;
    }

    /**
     * 获取通知列表（分页）
     * 
     * 为什么需要分页？
     * - 如果通知有几千条，一次性加载会很慢，用户体验不好
     * - 分页可以每次只加载 10-20 条，加载速度快
     * 
     * @param pageNo 页码，默认第 1 页
     * @param pageSize 每页数量，默认 10 条
     * @param enterpriseId 企业 ID（后面接入登录后可以从 session 中获取）
     * @return 通知列表和分页信息
     */
    @GetMapping("/list")
    public ResponseVO<PaginationResultVO<NoticeVO>> getNoticeList(
            @RequestParam(value = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam(value = "page_size", defaultValue = "10") Integer pageSize,
            @RequestParam("enterprise_id") String enterpriseId) {
        
        // 调用业务层，获取企业可见的通知列表
        PaginationResultVO<NoticeVO> result = noticeApplicationService.getNoticeList(
                enterpriseId, pageNo, pageSize);
        
        // 返回成功响应
        return getSuccessResponseVO(result);
    }

    /**
     * 获取通知详情
     * 
     * 为什么要单独有个详情接口？
     * - 列表只显示标题和摘要，节省流量
     * - 详情才显示完整内容、附件等信息
     * - 点击查看详情时才调用这个接口
     * 
     * @param noticeId 通知 ID
     * @param username 当前用户名（用于记录已读）
     * @param enterpriseId 企业 ID
     * @return 通知详情
     */
    @GetMapping("/{noticeId}")
    public ResponseVO<NoticeVO> getNoticeDetail(
            @PathVariable("noticeId") Long noticeId,
            @RequestParam("username") String username,
            @RequestParam("enterprise_id") String enterpriseId) {
        
        // 获取通知详情，并自动标记为已读
        NoticeVO notice = noticeApplicationService.getNoticeDetail(
                noticeId, username, enterpriseId);
        
        return getSuccessResponseVO(notice);
    }

    /**
     * 获取未读通知数量
     * 
     * 为什么需要这个接口？
     * - 在导航栏显示红色数字提醒，告诉用户有多少未读通知
     * - 就像微信右上角的小红点
     * 
     * @param username 用户名
     * @return 未读通知数量
     */
    @GetMapping("/unread/count")
    public ResponseVO<Integer> getUnreadCount(
            @RequestParam("username") String username) {
        
        Integer count = noticeApplicationService.getUnreadCount(username);
        return getSuccessResponseVO(count);
    }
}
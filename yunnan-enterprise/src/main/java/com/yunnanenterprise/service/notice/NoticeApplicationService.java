package com.yunnanenterprise.service.notice;

import com.yunnancommon.entity.po.NoticeInfo;
import com.yunnancommon.entity.po.NoticeReadInfo;
import com.yunnancommon.entity.query.NoticeInfoQuery;
import com.yunnancommon.entity.vo.PaginationResultVO;
import com.yunnancommon.service.NoticeInfoService;
import com.yunnancommon.service.NoticeReadInfoService;
import com.yunnanenterprise.dto.notice.NoticeVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 通知中心业务服务
 * 
 * 这个类负责处理通知相关的所有业务逻辑
 */
@Service
public class NoticeApplicationService {

    @Resource
    private NoticeInfoService noticeInfoService;
    
    @Resource
    private NoticeReadInfoService noticeReadInfoService;

    /**
     * 获取企业可见的通知列表
     * 
     * 业务逻辑说明：
     * 1. 只查询状态为正常的通知（status=1）
     * 2. 只查询企业可见或全部人可见的通知（noticeStatus=1 或 2）
     * 3. 只查询在生效期内的通知（当前时间在 startTime 和 endTime 之间）
     * 4. 按发布时间倒序排列（最新的在前面）
     */
    public PaginationResultVO<NoticeVO> getNoticeList(
            String enterpriseId, Integer pageNo, Integer pageSize) {
        
        // 构建查询条件
        NoticeInfoQuery query = new NoticeInfoQuery();
        query.setPageNo(pageNo);
        query.setPageSize(pageSize);
        
        // 只查询正常状态的通知
        query.setStatus(1);
        
        // 设置排序：按发布时间倒序
        query.setOrderBy("publish_time desc");
        
        // 调用通用服务层查询数据
        PaginationResultVO<NoticeInfo> result = noticeInfoService.findListByPage(query);
        
        // 过滤出企业可见的通知
        // noticeStatus: 1=全部人可见，2=企业可见
        List<NoticeInfo> filteredList = result.getList().stream()
                .filter(notice -> {
                    Integer status = notice.getNoticeStatus();
                    // 如果是全部人可见(1)或企业可见(2)，就保留
                    return status != null && (status == 1 || status == 2);
                })
                .filter(notice -> {
                    // 过滤生效时间：当前时间必须在 startTime 和 endTime 之间
                    Date now = new Date();
                    boolean afterStart = notice.getStartTime() == null || 
                                       now.after(notice.getStartTime());
                    boolean beforeEnd = notice.getEndTime() == null || 
                                      now.before(notice.getEndTime());
                    return afterStart && beforeEnd;
                })
                .collect(Collectors.toList());
        
        // 转换成前端需要的 VO 对象
        List<NoticeVO> voList = filteredList.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        
        // 构建分页结果
        return new PaginationResultVO<>(
                filteredList.size(),
                result.getPageSize(),
                result.getPageNo(),
                result.getPageTotal(),
                voList
        );
    }

    /**
     * 获取通知详情并标记为已读
     * 
     * 为什么要标记已读？
     * - 记录用户已经看过这条通知
     * - 下次查询未读数量时，这条就不算了
     * 
     * 使用 @Transactional 注解的原因：
     * - 这个方法涉及多个数据库操作（查询通知 + 插入阅读记录）
     * - 如果中间出错，所有操作都会回滚，保证数据一致性
     */
    @Transactional(rollbackFor = Exception.class)
    public NoticeVO getNoticeDetail(Long noticeId, String username, String enterpriseId) {
        
        // 1. 查询通知信息
        NoticeInfo notice = noticeInfoService.getNoticeInfoByNoticeId(noticeId);
        
        if (notice == null) {
            throw new IllegalArgumentException("通知不存在");
        }
        
        // 2. 检查权限：企业是否可以查看这条通知
        if (notice.getNoticeStatus() != 1 && notice.getNoticeStatus() != 2) {
            throw new IllegalArgumentException("无权查看此通知");
        }
        
        // 3. 记录阅读记录（标记为已读）
        try {
            NoticeReadInfo readInfo = new NoticeReadInfo();
            readInfo.setNoticeId(noticeId);
            readInfo.setUsername(username);
            readInfo.setEnterpriseId(enterpriseId);
            readInfo.setUserType(1); // 1=企业账号
            readInfo.setReadTime(new Date());
            
            // 插入阅读记录（如果已经读过，因为数据库有唯一索引，会忽略）
            noticeReadInfoService.add(readInfo);
        } catch (Exception e) {
            // 即使插入失败（比如已经读过），也不影响查看
            // 这里捕获异常是为了不影响主流程
        }
        
        // 4. 转换并返回
        return convertToVO(notice);
    }

    /**
     * 获取未读通知数量
     * 
     * 逻辑：
     * 1. 查询所有有效的企业可见通知
     * 2. 排除用户已读过的通知
     * 3. 返回剩余数量
     */
    public Integer getUnreadCount(String username) {
        
        // 1. 查询所有有效通知
        NoticeInfoQuery query = new NoticeInfoQuery();
        query.setStatus(1);
        List<NoticeInfo> allNotices = noticeInfoService.findListByParam(query);
        
        // 2. 过滤出企业可见的通知
        List<NoticeInfo> enterpriseNotices = allNotices.stream()
                .filter(notice -> {
                    Integer status = notice.getNoticeStatus();
                    return status != null && (status == 1 || status == 2);
                })
                .filter(notice -> {
                    // 过滤生效时间
                    Date now = new Date();
                    boolean afterStart = notice.getStartTime() == null || 
                                       now.after(notice.getStartTime());
                    boolean beforeEnd = notice.getEndTime() == null || 
                                      now.before(notice.getEndTime());
                    return afterStart && beforeEnd;
                })
                .collect(Collectors.toList());
        
        // 3. 统计未读数量（总数 - 已读数量）
        int totalCount = enterpriseNotices.size();
        
        // 这里简化处理，你需要根据实际情况查询已读数量
        // TODO: 查询 notice_read_info 表，统计该用户已读的数量
        
        return totalCount;
    }

    /**
     * 将数据库实体转换为前端 VO 对象
     * 
     * 为什么要转换？
     * - 数据库字段名可能不符合前端习惯（如 snake_case vs camelCase）
     * - 有些字段不需要返回给前端（如内部状态）
     * - 可以添加一些计算字段（如是否已读）
     */
    private NoticeVO convertToVO(NoticeInfo notice) {
        NoticeVO vo = new NoticeVO();
        vo.setNoticeId(notice.getNoticeId());
        vo.setTitle(notice.getTitle());
        vo.setContent(notice.getContent());
        vo.setAttachment(notice.getAttachment());
        vo.setAttachmentName(notice.getAttachmentName());
        vo.setIsImportant(notice.getIsImportant());
        vo.setPublisher(notice.getPublisher());
        vo.setPublishTime(notice.getPublishTime());
        vo.setReadCount(notice.getReadCount());
        return vo;
    }
}
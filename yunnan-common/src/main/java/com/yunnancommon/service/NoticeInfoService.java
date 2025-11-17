package com.yunnancommon.service;


import com.yunnancommon.entity.vo.CurrentVO;
import com.yunnancommon.entity.vo.PaginationResultVO;
import com.yunnancommon.entity.po.NoticeInfo;
import com.yunnancommon.entity.query.NoticeInfoQuery;

import java.util.List;

/**
 * @Description:通知消息表Service
 * @auther:group2
 * @date:2025/10/22
 */
public interface NoticeInfoService {

    /**
     * 根据条件查询列表
     */
    List<NoticeInfo> findListByParam(NoticeInfoQuery query);

    /**
     * 根据条件查询数量
     */
    Integer findCountByParam(NoticeInfoQuery query);

    /**
     * 分页查询
     */
    PaginationResultVO<NoticeInfo> findListByPage(NoticeInfoQuery query);

    /**
     * 新增
     */
    Integer add(NoticeInfo bean);

    /**
     * 批量新增
     */
    Integer addBatch(List<NoticeInfo> listBean);

    /**
     * 批量新增或修改
     */
    Integer addOrUpdateBatch(List<NoticeInfo> listBean);

    /**
     * 根据NoticeId查询
     */
    NoticeInfo getNoticeInfoByNoticeId(Long noticeId);

    /**
     * 根据NoticeId更新
     */
    Integer updateNoticeInfoByNoticeId(NoticeInfo bean, Long noticeId);

    /**
     * 根据NoticeId删除
     */
    Integer deleteNoticeInfoByNoticeId(Long noticeId);


    void getCurrentNoticeInfo(String username, CurrentVO currentVO);
    void getCityCurrentNoticeInfo(String username, CurrentVO currentVO);
}
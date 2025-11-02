package com.yunnancommon.service;


import com.yunnancommon.entity.vo.PaginationResultVO;
import com.yunnancommon.entity.po.NoticeReadInfo;
import com.yunnancommon.entity.query.NoticeReadInfoQuery;

import java.util.List;
/**
 * @Description:通知阅读记录表Service
 * @auther:group2
 * @date:2025/10/22
 */
public interface NoticeReadInfoService {

	/**
	 * 根据条件查询列表
	 */
	List<NoticeReadInfo> findListByParam(NoticeReadInfoQuery query);

	/**
	 * 根据条件查询数量
	 */
	Integer findCountByParam(NoticeReadInfoQuery query);

	/**
	 * 分页查询
	 */
	PaginationResultVO<NoticeReadInfo> findListByPage(NoticeReadInfoQuery query);

	/**
	 * 新增
	 */
	Integer add(NoticeReadInfo bean);

	/**
	 * 批量新增
	 */
	Integer addBatch(List<NoticeReadInfo> listBean);

	/**
	 * 批量新增或修改
	 */
	Integer addOrUpdateBatch(List<NoticeReadInfo> listBean);

	/**
	 * 根据Id查询
	 */
	NoticeReadInfo getNoticeReadInfoById(Long id);

	/**
	 * 根据Id更新
	 */
	Integer updateNoticeReadInfoById(NoticeReadInfo bean, Long id);

	/**
	 * 根据Id删除
	 */
	Integer deleteNoticeReadInfoById(Long id);

	/**
	 * 根据NoticeIdAndUsername查询
	 */
	NoticeReadInfo getNoticeReadInfoByNoticeIdAndUsername(Long noticeId, String username);

	/**
	 * 根据NoticeIdAndUsername更新
	 */
	Integer updateNoticeReadInfoByNoticeIdAndUsername(NoticeReadInfo bean, Long noticeId, String username);

	/**
	 * 根据NoticeIdAndUsername删除
	 */
	Integer deleteNoticeReadInfoByNoticeIdAndUsername(Long noticeId, String username);


}
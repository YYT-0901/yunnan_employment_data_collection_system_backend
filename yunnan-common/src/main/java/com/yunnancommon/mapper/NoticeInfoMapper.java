package com.yunnancommon.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
/**
 * @Description:通知消息表Mapper
 * @auther:group2
 * @date:2025/10/22
 */

@Mapper
public interface NoticeInfoMapper<T, P> extends BaseMapper {
	/**
	 * 根据NoticeId查询
	 */
	T selectByNoticeId(@Param("noticeId") Long noticeId);

	/**
	 * 根据NoticeId更新
	 */
	Integer updateByNoticeId(@Param("bean") T t, @Param("noticeId") Long noticeId);

	/**
	 * 根据NoticeId删除
	 */
	Integer deleteByNoticeId(@Param("noticeId") Long noticeId);


}
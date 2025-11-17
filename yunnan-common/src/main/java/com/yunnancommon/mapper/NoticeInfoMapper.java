package com.yunnancommon.mapper;

import com.yunnancommon.entity.dto.NoticeInfoDto;
import com.yunnancommon.entity.query.NoticeInfoQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

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

	/**
	 * 根据用户名查询未读通知数量
	 */
	Integer countNoReadNoticeInfoByUsername(@Param("curTime") String curTime, @Param("username") String username, @Param("noticeStatus") Integer noticeStatus);

	/**
	 * 查询通知列表并判断是否已读
	 */
	List<NoticeInfoDto> selectListWithReadStatus(@Param("curTime") String curTime, @Param("username") String username, @Param("noticeStatus") Integer noticeStatus);

}
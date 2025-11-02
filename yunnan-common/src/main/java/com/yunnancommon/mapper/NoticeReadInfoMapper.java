package com.yunnancommon.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
/**
 * @Description:通知阅读记录表Mapper
 * @auther:group2
 * @date:2025/10/22
 */

@Mapper
public interface NoticeReadInfoMapper<T, P> extends BaseMapper {
	/**
	 * 根据Id查询
	 */
	T selectById(@Param("id") Long id);

	/**
	 * 根据Id更新
	 */
	Integer updateById(@Param("bean") T t, @Param("id") Long id);

	/**
	 * 根据Id删除
	 */
	Integer deleteById(@Param("id") Long id);

	/**
	 * 根据NoticeIdAndUsername查询
	 */
	T selectByNoticeIdAndUsername(@Param("noticeId") Long noticeId, @Param("username") String username);

	/**
	 * 根据NoticeIdAndUsername更新
	 */
	Integer updateByNoticeIdAndUsername(@Param("bean") T t, @Param("noticeId") Long noticeId, @Param("username") String username);

	/**
	 * 根据NoticeIdAndUsername删除
	 */
	Integer deleteByNoticeIdAndUsername(@Param("noticeId") Long noticeId, @Param("username") String username);


}
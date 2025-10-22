package com.yunnancommon.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
/**
 * @Description:操作日志表Mapper
 * @auther:group2
 * @date:2025/10/22
 */

@Mapper
public interface LogInfoMapper<T, P> extends BaseMapper {
	/**
	 * 根据LogId查询
	 */
	T selectByLogId(@Param("logId") Long logId);

	/**
	 * 根据LogId更新
	 */
	Integer updateByLogId(@Param("bean") T t, @Param("logId") Long logId);

	/**
	 * 根据LogId删除
	 */
	Integer deleteByLogId(@Param("logId") Long logId);


}
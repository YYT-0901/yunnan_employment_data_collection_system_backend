package com.yunnancommon.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
/**
 * @Description:调查期信息表Mapper
 * @auther:group2
 * @date:2025/10/22
 */

@Mapper
public interface PeriodInfoMapper<T, P> extends BaseMapper {
	/**
	 * 根据PeriodId查询
	 */
	T selectByPeriodId(@Param("periodId") Integer periodId);

	/**
	 * 根据PeriodId更新
	 */
	Integer updateByPeriodId(@Param("bean") T t, @Param("periodId") Integer periodId);

	/**
	 * 根据PeriodId删除
	 */
	Integer deleteByPeriodId(@Param("periodId") Integer periodId);


}
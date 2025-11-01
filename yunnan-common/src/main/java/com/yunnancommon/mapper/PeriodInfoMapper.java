package com.yunnancommon.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
/**
 * @Description:调查期信息表Mapper
 * @auther:SOON JIANG BING
 * @date:2025/11/02
 */

@Mapper
public interface PeriodInfoMapper<T, P> extends BaseMapper {
	/**
	 * 根据PeriodId查询
	 */
	T selectByPeriodId(@Param("periodId") Long periodId);

	/**
	 * 根据PeriodId更新
	 */
	Integer updateByPeriodId(@Param("bean") T t, @Param("periodId") Long periodId);

	/**
	 * 根据PeriodId删除
	 */
	Integer deleteByPeriodId(@Param("periodId") Long periodId);

	/**
	 * 根据InvestigateTime查询
	 */
	T selectByInvestigateTime(@Param("investigateTime") String investigateTime);

	/**
	 * 根据InvestigateTime更新
	 */
	Integer updateByInvestigateTime(@Param("bean") T t, @Param("investigateTime") String investigateTime);

	/**
	 * 根据InvestigateTime删除
	 */
	Integer deleteByInvestigateTime(@Param("investigateTime") String investigateTime);


}
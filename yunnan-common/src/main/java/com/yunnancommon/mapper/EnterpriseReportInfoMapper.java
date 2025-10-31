package com.yunnancommon.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
/**
 * @Description:企业上报信息表Mapper
 * @auther:group2
 * @date:2025/10/22
 */

@Mapper
public interface EnterpriseReportInfoMapper<T, P> extends BaseMapper {
	/**
	 * 根据EnterpriseIdAndPeriodIdAndReportId查询
	 */
	T selectByEnterpriseIdAndPeriodIdAndReportId(@Param("enterpriseId") String enterpriseId, @Param("periodId") Integer periodId, @Param("reportId") String reportId);

	/**
	 * 根据EnterpriseIdAndPeriodIdAndReportId更新
	 */
	Integer updateByEnterpriseIdAndPeriodIdAndReportId(@Param("bean") T t, @Param("enterpriseId") String enterpriseId, @Param("periodId") Integer periodId, @Param("reportId") String reportId);

	/**
	 * 根据EnterpriseIdAndPeriodIdAndReportId删除
	 */
	Integer deleteByEnterpriseIdAndPeriodIdAndReportId(@Param("enterpriseId") String enterpriseId, @Param("periodId") Integer periodId, @Param("reportId") String reportId);


}
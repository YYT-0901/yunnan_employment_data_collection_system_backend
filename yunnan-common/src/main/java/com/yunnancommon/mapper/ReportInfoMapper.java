package com.yunnancommon.mapper;

import com.yunnancommon.entity.vo.ReportInfoDetailVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
/**
 * @Description:数据填报信息表Mapper
 * @auther:group2
 * @date:2025/10/22
 */

@Mapper
public interface ReportInfoMapper<T, P> extends BaseMapper {
	/**
	 * 根据ReportId查询
	 */
	T selectByReportId(@Param("reportId") String reportId);

	/**
	 * 根据ReportId更新
	 */
	Integer updateByReportId(@Param("bean") T t, @Param("reportId") String reportId);

	/**
	 * 根据ReportId删除
	 */
	Integer deleteByReportId(@Param("reportId") String reportId);

    
	/**
	 * 根据ReportId查询详情，包含企业名称和调查期时间
	 */
	ReportInfoDetailVO selectDetailByReportId(@Param("reportId") String reportId);
}
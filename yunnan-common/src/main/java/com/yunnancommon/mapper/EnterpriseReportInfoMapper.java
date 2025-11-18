package com.yunnancommon.mapper;

import java.util.List;
import java.util.Map;
import com.yunnancommon.entity.dto.AnalysisQueryDto;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.yunnancommon.entity.vo.EnterpriseReportVO;

@Mapper
public interface EnterpriseReportInfoMapper<T, P> extends BaseMapper {
	/**
	 * 根据EnterpriseIdAndPeriodIdAndReportId查询
	 */
	T selectByEnterpriseIdAndPeriodIdAndReportId(@Param("enterpriseId") String enterpriseId,
			@Param("periodId") Long periodId, @Param("reportId") String reportId);

	/**
	 * 根据EnterpriseIdAndPeriodIdAndReportId更新
	 */
	Integer updateByEnterpriseIdAndPeriodIdAndReportId(@Param("bean") T t, @Param("enterpriseId") String enterpriseId,
			@Param("periodId") Long periodId, @Param("reportId") String reportId);

	/**
	 * 根据EnterpriseIdAndPeriodIdAndReportId删除
	 */
	Integer deleteByEnterpriseIdAndPeriodIdAndReportId(@Param("enterpriseId") String enterpriseId,
			@Param("periodId") Long periodId, @Param("reportId") String reportId);

	List<EnterpriseReportVO> selectListWithAssociated(@Param("query") P p);


	Integer selectCountWithAssociated(@Param("query") P p);

	/**
	 * 取样分析 - 按地区统计企业数量
	 */
	List<Map<String, Object>> selectSamplingData(@Param("query") AnalysisQueryDto query);

	/**
	 * 对比分析 - 通用分析查询（支持动态分组）
	 */
	List<Map<String, Object>> selectAnalysisData(@Param("query") AnalysisQueryDto query);

	/**
	 * 趋势分析 - 按时间序列查询
	 */
	List<Map<String, Object>> selectTrendData(@Param("query") AnalysisQueryDto query);

	List<T> selectLatestByEnterprise(@Param("enterpriseId") String enterpriseId,
			@Param("offset") Integer offset, @Param("pageSize") Integer pageSize);

	List<T> selectHistoryByEnterpriseAndPeriod(@Param("enterpriseId") String enterpriseId,
			@Param("periodId") Long periodId);
}

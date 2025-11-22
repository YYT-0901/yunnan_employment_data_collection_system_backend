package com.yunnancommon.mapper;

import java.util.List;
import java.util.Map;
import com.yunnancommon.entity.dto.AnalysisQueryDto;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.yunnancommon.entity.vo.EnterpriseReportVO;

import com.yunnancommon.entity.po.EnterpriseReportInfo;
import com.yunnancommon.entity.query.EnterpriseReportInfoQuery;

@Mapper
public interface EnterpriseReportInfoMapper extends BaseMapper<EnterpriseReportInfo, EnterpriseReportInfoQuery> {
	/**
	 * 根据EnterpriseIdAndPeriodIdAndReportId查询
	 */
	EnterpriseReportInfo selectByEnterpriseIdAndPeriodIdAndReportId(@Param("enterpriseId") String enterpriseId,
			@Param("periodId") Long periodId, @Param("reportId") String reportId);

	/**
	 * 根据EnterpriseIdAndPeriodIdAndReportId更新
	 */
	Integer updateByEnterpriseIdAndPeriodIdAndReportId(@Param("bean") EnterpriseReportInfo t,
			@Param("enterpriseId") String enterpriseId,
			@Param("periodId") Long periodId, @Param("reportId") String reportId);

	/**
	 * 根据EnterpriseIdAndPeriodIdAndReportId删除
	 */
	Integer deleteByEnterpriseIdAndPeriodIdAndReportId(@Param("enterpriseId") String enterpriseId,
			@Param("periodId") Long periodId, @Param("reportId") String reportId);

	@Override
	List<EnterpriseReportInfo> selectList(@Param("query") EnterpriseReportInfoQuery query);

	@Override
	Integer selectCount(@Param("query") EnterpriseReportInfoQuery query);

	List<EnterpriseReportVO> selectListWithAssociated(@Param("query") EnterpriseReportInfoQuery p);

	Integer selectCountWithAssociated(@Param("query") EnterpriseReportInfoQuery p);

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

	List<EnterpriseReportInfo> selectLatestByEnterprise(@Param("enterpriseId") String enterpriseId,
			@Param("offset") Integer offset, @Param("pageSize") Integer pageSize);

	List<EnterpriseReportInfo> selectHistoryByEnterpriseAndPeriod(@Param("enterpriseId") String enterpriseId,
			@Param("periodId") Long periodId);
}

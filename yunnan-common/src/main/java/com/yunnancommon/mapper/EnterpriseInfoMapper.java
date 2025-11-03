package com.yunnancommon.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.yunnancommon.entity.dto.AnalysisQueryDto;

/**
 * @Description:企业信息表Mapper
 * @auther:group2
 * @date:2025/10/22
 */

@Mapper
public interface EnterpriseInfoMapper<T, P> extends BaseMapper {
	/**
	 * 根据EnterpriseId查询
	 */
	T selectByEnterpriseId(@Param("enterpriseId") String enterpriseId);

	/**
	 * 根据EnterpriseId更新
	 */
	Integer updateByEnterpriseId(@Param("bean") T t, @Param("enterpriseId") String enterpriseId);

	/**
	 * 根据EnterpriseId删除
	 */
	Integer deleteByEnterpriseId(@Param("enterpriseId") String enterpriseId);

	List<Map<String, Object>> selectAnalysisData(@Param("query") AnalysisQueryDto query);

	List<Map<String, Object>> selectSamplingData(@Param("query") AnalysisQueryDto query);

	List<Map<String, Object>> selectTrendData(@Param("query") AnalysisQueryDto query);

}

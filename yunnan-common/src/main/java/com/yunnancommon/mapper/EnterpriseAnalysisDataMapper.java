package com.yunnancommon.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface EnterpriseAnalysisDataMapper {

    /**
     * 将审核通过/归档的报表数据汇总写入 enterprise_analysis_data
     */
    int upsertFromReport(@Param("enterpriseId") String enterpriseId,
                         @Param("periodId") Long periodId,
                         @Param("reportId") String reportId,
                         @Param("approvedStatus") Integer approvedStatus,
                         @Param("archivedStatus") Integer archivedStatus);
}

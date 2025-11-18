package com.yunnancommon.mapper;

import com.yunnancommon.entity.dto.DistributionData;
import com.yunnancommon.entity.dto.OverviewStatisticsDataDto;
import com.yunnancommon.entity.query.StatisticsDataQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OverviewMapper {

    List<OverviewStatisticsDataDto> getStatisticList(@Param("query") StatisticsDataQuery statisticsDataQuery);

    List<DistributionData> getDistributionDataList(@Param("groupBy") String groupBy);

    List<DistributionData> getCityDistributionDataList(@Param("groupBy") String groupBy, @Param("regionCode") Integer regionCode);
}

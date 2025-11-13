package com.yunnancommon.entity.vo;

import com.yunnancommon.entity.dto.DistributionData;
import com.yunnancommon.entity.dto.ProgressData;
import lombok.Data;

import java.util.List;

@Data
public class StatisticsDataVO {
    /*
    * 企业总数
    * */
    private Integer enterpriseTotal;

    /*
    * 本月新企业总数
    * */
    private Integer newEnterpriseTotal;

    /*
    * 本期上报
    * */
    private Integer currentReportCount;

    /*
    * 本期建档总数
    * */
    private Integer constructionCount;

    /*
    * 本期调查总数
    * */
    private Integer investigationCount;

    /*
    * 本月岗位变化数
    * */
    private Integer positionChanges;

    /*
    * 进度数据
    * */
    private List<ProgressData> progressDataList;

    private List<DistributionData> regionDistributionDataList;
    private List<DistributionData> industryDistributionDataList;
    private List<DistributionData> natureDistributionDataList;

}

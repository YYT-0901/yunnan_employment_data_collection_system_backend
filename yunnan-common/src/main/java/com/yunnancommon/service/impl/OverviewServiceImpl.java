package com.yunnancommon.service.impl;

import com.yunnancommon.entity.constants.Constants;
import com.yunnancommon.entity.dto.DistributionData;
import com.yunnancommon.entity.dto.OverviewStatisticsDataDto;
import com.yunnancommon.entity.dto.ProgressData;
import com.yunnancommon.entity.po.EnterpriseInfo;
import com.yunnancommon.entity.po.EnterpriseReportInfo;
import com.yunnancommon.entity.po.PeriodInfo;
import com.yunnancommon.entity.query.EnterpriseInfoQuery;
import com.yunnancommon.entity.query.EnterpriseReportInfoQuery;
import com.yunnancommon.entity.query.PeriodInfoQuery;
import com.yunnancommon.entity.query.StatisticsDataQuery;
import com.yunnancommon.entity.vo.StatisticsDataVO;
import com.yunnancommon.enums.CityDict;
import com.yunnancommon.enums.IndustryDict;
import com.yunnancommon.enums.NatureDict;
import com.yunnancommon.enums.ReportStatusEnum;
import com.yunnancommon.exception.BusinessException;
import com.yunnancommon.mapper.EnterpriseInfoMapper;
import com.yunnancommon.mapper.EnterpriseReportInfoMapper;
import com.yunnancommon.mapper.OverviewMapper;
import com.yunnancommon.mapper.PeriodInfoMapper;
import com.yunnancommon.service.OverviewService;
import com.yunnancommon.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

@Service
@Slf4j
public class OverviewServiceImpl implements OverviewService {

    @Resource
    private OverviewMapper overviewMapper;
    @Resource
    private PeriodInfoMapper<PeriodInfo, PeriodInfoQuery> periodInfoMapper;
    @Resource
    private EnterpriseReportInfoMapper<EnterpriseReportInfo, EnterpriseReportInfoQuery> enterpriseReportInfoMapper;
    @Resource
    private EnterpriseInfoMapper<EnterpriseInfo, EnterpriseInfoQuery> enterpriseInfoMapper;

    @Override
    public StatisticsDataVO getStatisticsData() throws BusinessException {
        StatisticsDataVO statisticsDataVO = new StatisticsDataVO();

        statisticsDataVO.setEnterpriseTotal(enterpriseInfoMapper.selectCount(new EnterpriseInfoQuery()));

        // 获取当前时间信息 格式为(yyyy-MM)
        String investigateTime = DateUtils.format(new Date(), Constants.PATTERN_INVESTIGATE_TIME);
        PeriodInfo periodInfo = periodInfoMapper.selectByInvestigateTime(investigateTime);
        if (periodInfo == null) {
            throw new BusinessException("本月调查期不存在");
        }
        statisticsDataVO.setNewEnterpriseTotal(periodInfo.getEnterpriseCount());

        EnterpriseReportInfoQuery enterpriseReportInfoQuery = new EnterpriseReportInfoQuery();
        enterpriseReportInfoQuery.setPeriodId(periodInfo.getPeriodId());
        statisticsDataVO.setCurrentReportCount(enterpriseReportInfoMapper.selectCount(enterpriseReportInfoQuery));

        StatisticsDataQuery statisticsDataQuery = new StatisticsDataQuery();
        statisticsDataQuery.setPeriodId(periodInfo.getPeriodId());
        statisticsDataQuery.setStatus(List.of(ReportStatusEnum.ARCHIVED.getCode(), ReportStatusEnum.APPROVED.getCode(), ReportStatusEnum.CITY_AUDITING.getCode(), ReportStatusEnum.PROVINCE_AUDITING.getCode()));
        List<OverviewStatisticsDataDto> statisticList = overviewMapper.getStatisticList(statisticsDataQuery);

        Integer constructionCount = statisticList.stream().mapToInt(item -> item.getConstructionCount() != null ? item.getConstructionCount() : 0).sum();
        Integer investigationCount = statisticList.stream().mapToInt(item -> item.getInvestigationCount() != null ? item.getInvestigationCount() : 0).sum();
        statisticsDataVO.setConstructionCount(constructionCount);
        statisticsDataVO.setInvestigationCount(investigationCount);
        statisticsDataVO.setPositionChanges(investigationCount - constructionCount);

        // 本月各地市上报进度数据
        List<ProgressData> progressDataList = new ArrayList<>();
        for (CityDict city : CityDict.values()) {
            ProgressData progressData = new ProgressData();
            progressData.setCity(city.getName());
            EnterpriseInfoQuery enterpriseInfoQuery = new EnterpriseInfoQuery();
            enterpriseInfoQuery.setRegionCode(city.getCode());
            Integer totalCount = enterpriseInfoMapper.selectCount(enterpriseInfoQuery);
            progressData.setTotal(totalCount);
            Integer currentCount = (int) statisticList.stream().filter(item -> city.getCode().equals(item.getEnterpriseRegion())).count();
            progressData.setValue(currentCount);

            // 防止 totalCount 为 0 导致除零异常
            if (totalCount != 0) {
                progressData.setPercentage(currentCount * 100.0 / totalCount);
            } else {
                progressData.setPercentage(0.0);
            }

            progressDataList.add(progressData);
        }
        statisticsDataVO.setProgressDataList(progressDataList);

        // 各地市分布数据
        processDistributionData(
                "region_code",
                CityDict::getNameByCode,
                statisticsDataVO::setRegionDistributionDataList
        );

        processDistributionData(
                "nature_code",
                NatureDict::getNameByCode,
                statisticsDataVO::setNatureDistributionDataList  // 修正：使用正确的setter
        );

        processDistributionData(
                "industry_code",
                IndustryDict::getNameByCode,
                statisticsDataVO::setIndustryDistributionDataList
        );
        return statisticsDataVO;
    }

    private void processDistributionData(
            String field,
            Function<Integer, String> nameMapper,
            Consumer<List<DistributionData>> setter) {

        List<DistributionData> dataList = overviewMapper.getDistributionDataList(field);
        dataList.forEach(item -> {
            Integer code = Integer.valueOf(item.getCode());
            item.setName(nameMapper.apply(code));
        });
        setter.accept(dataList);
    }
}

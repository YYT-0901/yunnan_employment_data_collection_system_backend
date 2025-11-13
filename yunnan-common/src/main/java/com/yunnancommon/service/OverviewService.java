package com.yunnancommon.service;

import com.yunnancommon.entity.vo.StatisticsDataVO;
import com.yunnancommon.exception.BusinessException;

public interface OverviewService {
    StatisticsDataVO getStatisticsData() throws BusinessException;
}

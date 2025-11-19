package com.yunnancommon.service;

import java.util.List;

import com.yunnancommon.entity.dto.AnalysisQueryDto;
import com.yunnancommon.entity.vo.AnalysisResultVO;
import com.yunnancommon.entity.vo.SamplingResultVO;

public interface DataAnalysisService {

    List<SamplingResultVO> getSamplingAnalysis(AnalysisQueryDto query);

    List<AnalysisResultVO> getComparisonAnalysis(AnalysisQueryDto query);

    List<AnalysisResultVO> getTrendAnalysis(AnalysisQueryDto query);

}

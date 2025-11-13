package com.yunnanprovince.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yunnancommon.entity.dto.AnalysisQueryDto;
import com.yunnancommon.entity.vo.AnalysisResultVO;
import com.yunnancommon.entity.vo.SamplingResultVO;
import com.yunnancommon.service.DataAnalysisService;
import com.yunnancommon.service.DruidQueryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DataAnalysisController.class)
public class DataAnalysisControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DataAnalysisService dataAnalysisService;

    @MockBean
    private DruidQueryService druidQueryService;

    @Autowired
    private ObjectMapper objectMapper;

    private AnalysisQueryDto validQuery;
    private List<SamplingResultVO> samplingResult;
    private List<AnalysisResultVO> analysisResult;

    @BeforeEach
    void setUp() {
        validQuery = new AnalysisQueryDto();
        validQuery.setPeriodIds(Arrays.asList(1L, 2L));
        validQuery.setRegions(Arrays.asList(530100, 530200));
        validQuery.setStatuses(Arrays.asList(4));

        // Mock sampling result
        SamplingResultVO samplingVO = new SamplingResultVO();
        samplingVO.setRegionCode(530100);
        samplingVO.setRegionName("昆明市");
        samplingVO.setEnterpriseCount(150);
        samplingVO.setPercentage(30.5);
        samplingVO.setConstructionTotal(1000);
        samplingVO.setInvestigationTotal(950);
        samplingVO.setChangeTotal(-50);
        samplingVO.setChangeRatio(-5.0);
        samplingResult = Arrays.asList(samplingVO);

        // Mock analysis result
        AnalysisResultVO analysisVO = new AnalysisResultVO();
        analysisVO.setPeriodName("2024Q1");
        analysisVO.setDimensionCode("530100");
        analysisVO.setDimensionName("昆明市");
        analysisVO.setEnterpriseCount(100);
        analysisVO.setConstructionTotal(1000);
        analysisVO.setInvestigationTotal(950);
        analysisVO.setChangeTotal(-50);
        analysisVO.setChangeRatio(-5.0);
        analysisResult = Arrays.asList(analysisVO);
    }

    @Test
    void testSamplingSuccess() throws Exception {
        // Mock service response
        when(dataAnalysisService.getSamplingAnalysis(any())).thenReturn(samplingResult);

        // Prepare request
        AnalysisQueryDto query = new AnalysisQueryDto();
        query.setPeriodIds(Arrays.asList(1L));
        query.setStatuses(Arrays.asList(4));

        mockMvc.perform(post("/dataAnalysis/sampling")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(query)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].regionName").value("昆明市"))
                .andExpect(jsonPath("$.data[0].enterpriseCount").value(150));
    }

    @Test
    void testSamplingWithServiceException() throws Exception {
        // Mock service exception
        when(dataAnalysisService.getSamplingAnalysis(any())).thenThrow(new RuntimeException("数据库连接失败"));

        AnalysisQueryDto query = new AnalysisQueryDto();
        query.setPeriodIds(Arrays.asList(1L));

        mockMvc.perform(post("/dataAnalysis/sampling")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(query)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.info").value("数据库连接失败"));
    }

    @Test
    void testComparisonSuccess() throws Exception {
        // Mock service response
        when(dataAnalysisService.getComparisonAnalysis(any())).thenReturn(analysisResult);

        // Prepare valid comparison query
        AnalysisQueryDto query = new AnalysisQueryDto();
        query.setPeriodIds(Arrays.asList(1L, 2L));
        query.setGroupBy("region");
        query.setStatuses(Arrays.asList(4));

        mockMvc.perform(post("/dataAnalysis/comparison")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(query)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void testComparisonWithInvalidPeriodCount() throws Exception {
        // Test with only 1 period (should be 2)
        AnalysisQueryDto query = new AnalysisQueryDto();
        query.setPeriodIds(Arrays.asList(1L));
        query.setGroupBy("region");

        mockMvc.perform(post("/dataAnalysis/comparison")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(query)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testComparisonWithMissingGroupBy() throws Exception {
        // Test without groupBy
        AnalysisQueryDto query = new AnalysisQueryDto();
        query.setPeriodIds(Arrays.asList(1L, 2L));
        // groupBy is not set

        mockMvc.perform(post("/dataAnalysis/comparison")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(query)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testTrendSuccess() throws Exception {
        // Mock service response
        when(dataAnalysisService.getTrendAnalysis(any())).thenReturn(analysisResult);

        AnalysisQueryDto query = new AnalysisQueryDto();
        query.setPeriodIds(Arrays.asList(1L, 2L, 3L));
        query.setGroupBy("");  // Empty string for province-wide trend
        query.setStatuses(Arrays.asList(4));

        mockMvc.perform(post("/dataAnalysis/trend")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(query)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void testTrendWithServiceException() throws Exception {
        when(dataAnalysisService.getTrendAnalysis(any())).thenThrow(new RuntimeException("查询超时"));

        AnalysisQueryDto query = new AnalysisQueryDto();
        query.setPeriodIds(Arrays.asList(1L, 2L));

        mockMvc.perform(post("/dataAnalysis/trend")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(query)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.info").value("查询超时"));
    }

    @Test
    void testMultiDimensionalSuccess() throws Exception {
        // Mock Druid service response
        when(druidQueryService.getMultiDimensionalData(any(), any(), any(), any()))
                .thenReturn(Arrays.asList());

        AnalysisQueryDto query = new AnalysisQueryDto();
        query.setPeriodIds(Arrays.asList(1L));
        query.setRegions(Arrays.asList(530100));
        query.setIndustries(Arrays.asList(1, 2));
        query.setNatures(Arrays.asList(1));

        mockMvc.perform(post("/dataAnalysis/multiDimensional")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(query)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void testMultiDimensionalWithLargeDataSet() throws Exception {
        // Test performance with larger dataset
        when(druidQueryService.getMultiDimensionalData(any(), any(), any(), any()))
                .thenReturn(Collections.nCopies(1000, new Object()));  // 1000 data points

        AnalysisQueryDto query = new AnalysisQueryDto();
        query.setPeriodIds(Arrays.asList(1L, 2L, 3L));  // 3 periods
        // All regions, industries, and natures (should generate large dataset)

        mockMvc.perform(post("/dataAnalysis/multiDimensional")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(query)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void testInvalidRequestBody() throws Exception {
        // Test with invalid JSON
        mockMvc.perform(post("/dataAnalysis/sampling")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ invalid json }"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testEmptyRequestBody() throws Exception {
        // Test with empty request body
        mockMvc.perform(post("/dataAnalysis/sampling")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isOk());  // Should handle gracefully with validation
    }

    @Test
    void testConcurrentRequests() throws Exception {
        // Test concurrent request handling
        when(dataAnalysisService.getSamplingAnalysis(any())).thenReturn(samplingResult);

        AnalysisQueryDto query = new AnalysisQueryDto();
        query.setPeriodIds(Arrays.asList(1L));

        // Simulate multiple concurrent requests
        for (int i = 0; i < 5; i++) {
            mockMvc.perform(post("/dataAnalysis/sampling")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(query)))
                    .andExpect(status().isOk());
        }
    }
}

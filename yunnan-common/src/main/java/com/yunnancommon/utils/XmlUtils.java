package com.yunnancommon.utils;

import com.yunnancommon.entity.vo.XmlReportVO;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class XmlUtils {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 生成云南省就业数据汇总XML报告
     * @param xmlReportVOList 企业报告数据列表
     * @param investigateTime 调查时间
     * @param outputPath 输出文件路径（可选，为null时仅返回XML字符串）
     * @return 生成的XML字符串
     */
    public static String  generateXml(List<XmlReportVO> xmlReportVOList, String investigateTime, String outputPath) {
        try {
            // 创建文档对象
            Document document = DocumentHelper.createDocument();
            // 创建根元素
            Element yunnanReport = document.addElement("yunnan_report");

            // 1. 创建header部分
            createHeaderSection(yunnanReport, investigateTime);

            // 2. 创建汇总信息部分
            createSummarySection(yunnanReport, xmlReportVOList);

            // 3. 创建市级数据列表部分
            createCityDataSection(yunnanReport, xmlReportVOList);

            // 4. 创建企业上报数据部分
            createEnterpriseDataSection(yunnanReport, xmlReportVOList);

            // 格式化输出
            OutputFormat format = OutputFormat.createPrettyPrint();
            format.setEncoding("UTF-8");

            // 如果指定了输出路径，保存到文件
            if (outputPath != null && !outputPath.isEmpty()) {
                XMLWriter fileWriter = null;
                try {
                    // 添加自动生成文件名的逻辑
                    String filePath = outputPath;

                    // 检查是否只是目录路径（以/或\结尾）
                    if (filePath.endsWith("/") || filePath.endsWith("\\")) {
                        // 生成文件名：省份_调查期_年月日_时分秒.xml
                        String province = "云南省";
                        // 清理调查期字符串，移除不能作为文件名的字符
                        String safeInvestigateTime = investigateTime.replaceAll("[/\\:*?\"<>|]", "-");
                        String fileName = String.format("%s_%s.xml", province, safeInvestigateTime);
                        filePath += fileName;
                    }

                    // 确保使用正确的文件分隔符
                    filePath = filePath.replace('/', File.separatorChar);

                    // 确保目录存在
                    File file = new File(filePath);
                    File parentDir = file.getParentFile();
                    if (parentDir != null && !parentDir.exists()) {
                        parentDir.mkdirs();
                    }

                    fileWriter = new XMLWriter(new FileWriter(filePath), format);
                    fileWriter.write(document);
                    System.out.println("XML已保存到: " + filePath);
                } catch (IOException e) {
                    System.err.println("保存XML文件失败: " + e.getMessage());
                    throw new RuntimeException("保存XML文件失败: " + e.getMessage(), e);
                } finally {
                    if (fileWriter != null) {
                        try {
                            fileWriter.close();
                        } catch (IOException e) {
                            // 忽略关闭时的异常
                        }
                    }
                }
            }

            // 返回XML字符串
            StringWriter stringWriter = new StringWriter();
            XMLWriter xmlWriter = null;
            try {
                xmlWriter = new XMLWriter(stringWriter, format);
                xmlWriter.write(document);
                return stringWriter.toString();
            } catch (IOException e) {
                throw new RuntimeException("生成XML字符串失败: " + e.getMessage(), e);
            } finally {
                if (xmlWriter != null) {
                    try {
                        xmlWriter.close();
                    } catch (IOException e) {
                        // 忽略关闭时的异常
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("生成XML报告失败: " + e.getMessage(), e);
        }
    }

    /**
     * 返回xml字符串
     * @param xmlReportVOList
     * @param investigateTime
     * @return
     */
    public static String generateXml(List<XmlReportVO> xmlReportVOList, String investigateTime) {
        return generateXml(xmlReportVOList, investigateTime, null);
    }

    /**
     * 创建header部分
     */
    private static void createHeaderSection(Element parent, String investigateTime) {
        Element header = parent.addElement("header");

        // 省份名称
        header.addElement("province_name").setText("云南省");

        // 上报时间
        header.addElement("report_time").setText(DATE_FORMAT.format(new Date()));

        // 调查期
        header.addElement("investigate_period").setText(investigateTime);
    }

    /**
     * 创建汇总信息部分
     */
    private static void createSummarySection(Element parent, List<XmlReportVO> dataList) {
        Element summary = parent.addElement("summary_info");

        // 企业总数
        summary.addElement("total_enterprise_count").setText(String.valueOf(dataList.size()));

        // 总就业人数（这里假设constructionCount代表就业人数）
        int totalEmployment = dataList.stream()
                .mapToInt(report -> report.getConstructionCount() != null ? report.getConstructionCount() : 0)
                .sum();
        summary.addElement("total_employment_count").setText(String.valueOf(totalEmployment));

        // 总调查期人数（这里假设investigationCount代表调查人数）
        int totalInvestigation = dataList.stream()
                .mapToInt(report -> report.getInvestigationCount() != null ? report.getInvestigationCount() : 0)
                .sum();
        summary.addElement("total_investigation_count").setText(String.valueOf(totalInvestigation));

        // 市级涉及单位数量（通过enterpriseRegion去重统计）
        long cityCount = dataList.stream()
                .filter(report -> report.getEnterpriseRegion() != null)
                .map(report -> report.getEnterpriseRegion())
                .distinct()
                .count();
        summary.addElement("involved_city_count").setText(String.valueOf(cityCount));
    }

    /**
     * 创建市级数据列表部分
     */
    private static void createCityDataSection(Element parent, List<XmlReportVO> dataList) {
        Element cityDataList = parent.addElement("city_data_list");

        // 按市级分组
        Map<Integer, List<XmlReportVO>> cityGroupedData = dataList.stream()
                .filter(report -> report.getEnterpriseRegion() != null)
                .collect(Collectors.groupingBy(XmlReportVO::getEnterpriseRegion));

        // 为每个市级创建数据
        for (Map.Entry<Integer, List<XmlReportVO>> entry : cityGroupedData.entrySet()) {
            Integer cityCode = entry.getKey();
            List<XmlReportVO> cityReports = entry.getValue();

            Element cityData = cityDataList.addElement("city_data");

            // 市级名称（这里简化处理，实际可能需要一个城市代码到名称的映射）
            cityData.addElement("city_name").setText(DictUtils.getCityName(String.valueOf(cityCode)));

            // 该市级的企业数量
            cityData.addElement("enterprise_count").setText(String.valueOf(cityReports.size()));

            // 该市级的总就业人数
            int cityEmployment = cityReports.stream()
                    .mapToInt(report -> report.getConstructionCount() != null ? report.getConstructionCount() : 0)
                    .sum();
            cityData.addElement("total_employment").setText(String.valueOf(cityEmployment));

            // 该市级的总调查人数
            int cityInvestigation = cityReports.stream()
                    .mapToInt(report -> report.getInvestigationCount() != null ? report.getInvestigationCount() : 0)
                    .sum();
            cityData.addElement("total_investigation").setText(String.valueOf(cityInvestigation));
        }
    }

    /**
     * 创建企业上报数据部分
     */
    private static void createEnterpriseDataSection(Element parent, List<XmlReportVO> dataList) {
        Element enterpriseDataList = parent.addElement("enterprise_data_list");

        // 按市级分组
        Map<Integer, List<XmlReportVO>> cityGroupedData = dataList.stream()
                .filter(report -> report.getEnterpriseRegion() != null)
                .collect(Collectors.groupingBy(XmlReportVO::getEnterpriseRegion));

        // 为每个市级创建企业数据列表
        for (Map.Entry<Integer, List<XmlReportVO>> entry : cityGroupedData.entrySet()) {
            Integer cityCode = entry.getKey();
            List<XmlReportVO> cityReports = entry.getValue();

            Element cityEnterpriseGroup = enterpriseDataList.addElement("city_enterprise_group");
            cityEnterpriseGroup.addElement("city_name").setText(DictUtils.getCityName(String.valueOf(cityCode)));

            // 为每个企业创建数据
            for (XmlReportVO report : cityReports) {
                Element enterpriseData = cityEnterpriseGroup.addElement("enterprise_data");

                // 企业基本信息
                enterpriseData.addElement("enterprise_name").setText(report.getEnterpriseName() != null ? report.getEnterpriseName() : "");

                // 就业和调查数据
                enterpriseData.addElement("construction_count").setText(report.getConstructionCount() != null ? report.getConstructionCount().toString() : "0");
                enterpriseData.addElement("investigation_count").setText(report.getInvestigationCount() != null ? report.getInvestigationCount().toString() : "0");

                // 其他数据
                String enterpriseNatureName = report.getEnterpriseNature() != null ?
                        DictUtils.getEnterpriseNatureName(String.valueOf(report.getEnterpriseNature())) : "";
                enterpriseData.addElement("enterprise_nature").setText(enterpriseNatureName);

                String industryName = report.getEnterpriseIndustry() != null ?
                        DictUtils.getEnterpriseIndustryName(String.valueOf(report.getEnterpriseIndustry())) : "";
                enterpriseData.addElement("enterprise_industry").setText(industryName);

                // 减少类型和原因
                if (report.getReductionType() != null) {
                    String reductionTypeName = DictUtils.getReductionTypeName(report.getReductionType());
                    enterpriseData.addElement("reduction_type").setText(reductionTypeName);

                    if (report.getReason1() != null) {
                        String reason1Name = DictUtils.getReductionReasonName(report.getReason1());
                        enterpriseData.addElement("reason1").setText(reason1Name);
                        if (report.getReason1Desc() != null) {
                            enterpriseData.addElement("reason1_desc").setText(report.getReason1Desc());
                        }
                    }

                    if (report.getReason2() != null) {
                        String reason2Name = DictUtils.getReductionReasonName(report.getReason2());
                        enterpriseData.addElement("reason2").setText(reason2Name);
                        if (report.getReason2Desc() != null) {
                            enterpriseData.addElement("reason2_desc").setText(report.getReason2Desc());
                        }
                    }

                    if (report.getReason3() != null) {
                        String reason3Name = DictUtils.getReductionReasonName(report.getReason3());
                        enterpriseData.addElement("reason3").setText(reason3Name);
                        if (report.getReason3Desc() != null) {
                            enterpriseData.addElement("reason3_desc").setText(report.getReason3Desc());
                        }
                    }

                    if (report.getOtherReason() != null) {
                        enterpriseData.addElement("other_reason").setText(report.getOtherReason());
                    }
                }
            }
        }
    }
    /**
     * 内部使用的StringWriter类
     */
    private static class StringWriter extends java.io.StringWriter {
        @Override
        public String toString() {
            return getBuffer().toString();
        }
    }
}

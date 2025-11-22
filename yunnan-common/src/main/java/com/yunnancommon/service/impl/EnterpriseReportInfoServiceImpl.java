package com.yunnancommon.service.impl;

import com.yunnancommon.entity.po.EnterpriseInfo;
import com.yunnancommon.entity.query.EnterpriseInfoQuery;
import com.yunnancommon.entity.query.SimplePage;
import com.yunnancommon.entity.vo.CurrentVO;
import com.yunnancommon.entity.vo.EnterpriseReportVO;
import com.yunnancommon.entity.vo.XmlReportVO;
import com.yunnancommon.enums.EnterpriseStatusEnum;
import com.yunnancommon.enums.PageSize;
import com.yunnancommon.entity.vo.PaginationResultVO;
import com.yunnancommon.entity.po.EnterpriseReportInfo;
import com.yunnancommon.entity.query.EnterpriseReportInfoQuery;
import com.yunnancommon.enums.ReportStatusEnum;
import com.yunnancommon.mapper.EnterpriseInfoMapper;
import com.yunnancommon.mapper.EnterpriseReportInfoMapper;
import com.yunnancommon.service.EnterpriseReportInfoService;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import java.util.List;

@Service("enterpriseReportInfoService")
public class EnterpriseReportInfoServiceImpl implements EnterpriseReportInfoService {

    @Resource
    private EnterpriseReportInfoMapper enterpriseReportInfoMapper;

    @Resource
    private EnterpriseInfoMapper<EnterpriseInfo, EnterpriseReportInfoQuery> enterpriseInfoMapper;

    /**
     * 根据条件查询列表
     */
    @Override
    public List<EnterpriseReportInfo> findListByParam(EnterpriseReportInfoQuery query) {
        return this.enterpriseReportInfoMapper.selectList(query);
    }

    /**
     * 根据条件查询数量
     */
    @Override
    public Integer findCountByParam(EnterpriseReportInfoQuery query) {
        return this.enterpriseReportInfoMapper.selectCount(query);
    }

    /**
     * 分页查询
     */
    @Override
    public PaginationResultVO<EnterpriseReportInfo> findListByPage(EnterpriseReportInfoQuery query) {
        Integer count = this.findCountByParam(query);
        Integer pageSize = query.getPageSize() == null ? PageSize.SIZE15.getSize() : query.getPageSize();
        SimplePage page = new SimplePage(query.getPageNo(), count, pageSize);
        query.setSimplePage(page);
        List<EnterpriseReportInfo> list = this.findListByParam(query);
        PaginationResultVO<EnterpriseReportInfo> result = new PaginationResultVO<EnterpriseReportInfo>(count,
                page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
        return result;
    }

    @Override
    public PaginationResultVO<EnterpriseReportVO> findListByPageWithAssociatedEnterpriseName(
            EnterpriseReportInfoQuery query) {
        Integer count = enterpriseReportInfoMapper.selectCountWithAssociated(query);
        Integer pageSize = query.getPageSize() == null ? PageSize.SIZE15.getSize() : query.getPageSize();
        SimplePage page = new SimplePage(query.getPageNo(), count, pageSize);
        query.setSimplePage(page);
        List<EnterpriseReportVO> list = enterpriseReportInfoMapper.selectListWithAssociated(query);
        PaginationResultVO<EnterpriseReportVO> result = new PaginationResultVO<EnterpriseReportVO>(count,
                page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
        return result;
    }

    /**
     * 新增
     */
    @Override
    public Integer add(EnterpriseReportInfo bean) {
        return this.enterpriseReportInfoMapper.insert(bean);
    }

    /**
     * 批量新增
     */
    @Override
    public Integer addBatch(List<EnterpriseReportInfo> listBean) {
        if (listBean == null || listBean.isEmpty()) {
            return 0;
        }
        return this.enterpriseReportInfoMapper.insertBatch(listBean);
    }

    /**
     * 批量新增或修改
     */
    @Override
    public Integer addOrUpdateBatch(List<EnterpriseReportInfo> listBean) {
        if (listBean == null || listBean.isEmpty()) {
            return 0;
        }
        return this.enterpriseReportInfoMapper.insertOrUpdateBatch(listBean);
    }

    /**
     * 根据EnterpriseIdAndPeriodIdAndReportId查询
     */
    @Override
    public EnterpriseReportInfo getEnterpriseReportInfoByEnterpriseIdAndPeriodIdAndReportId(String enterpriseId,
            Long periodId, String reportId) {
        return this.enterpriseReportInfoMapper.selectByEnterpriseIdAndPeriodIdAndReportId(enterpriseId, periodId,
                reportId);
    }

    /**
     * 根据EnterpriseIdAndPeriodIdAndReportId更新
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer updateEnterpriseReportInfoByEnterpriseIdAndPeriodIdAndReportId(EnterpriseReportInfo bean,
            String enterpriseId, Long periodId, String reportId) {
        return this.enterpriseReportInfoMapper.updateByEnterpriseIdAndPeriodIdAndReportId(bean, enterpriseId, periodId,
                reportId);
    }

    /**
     * 根据EnterpriseIdAndPeriodIdAndReportId删除
     */
    @Override
    public Integer deleteEnterpriseReportInfoByEnterpriseIdAndPeriodIdAndReportId(String enterpriseId, Long periodId,
            String reportId) {
        return this.enterpriseReportInfoMapper.deleteByEnterpriseIdAndPeriodIdAndReportId(enterpriseId, periodId,
                reportId);
    }

    @Override
    public void getStatisticCount(CurrentVO currentVO) {
        EnterpriseReportInfoQuery query = new EnterpriseReportInfoQuery();
        query.setStatus(ReportStatusEnum.PROVINCE_AUDITING.getCode());
        currentVO.setAuditDataCount(this.enterpriseReportInfoMapper.selectCount(query));

        query.setStatus(ReportStatusEnum.APPROVED.getCode());
        currentVO.setUploadDataCount(this.enterpriseReportInfoMapper.selectCount(query));

        EnterpriseInfoQuery enterpriseInfoQuery = new EnterpriseInfoQuery();
        enterpriseInfoQuery.setStatus(EnterpriseStatusEnum.FILED.getCode());
        currentVO.setAuditEnterpriseCount(this.enterpriseInfoMapper.selectCount(enterpriseInfoQuery));
    }

    @Override
    public void getCityStatisticCount(CurrentVO currentVO, Integer cityCode) {
        EnterpriseReportInfoQuery query = new EnterpriseReportInfoQuery();
        query.setStatus(ReportStatusEnum.CITY_AUDITING.getCode());
        query.setEnterpriseRegion(cityCode);
        currentVO.setAuditDataCount(this.enterpriseReportInfoMapper.selectCount(query));
    }

    @Override
    public List<XmlReportVO> getEnterpriseReportInfoByStatusAndPeriodId(Integer status, Long periodId) {
        return this.enterpriseReportInfoMapper.selectListWithAssociatedEnterpriseNameAndReportInfo(status, periodId);
    }
}
    public List<EnterpriseReportInfo> findLatestByEnterprise(String enterpriseId, Integer pageNo, Integer pageSize) {
        if (pageNo == null || pageNo < 1) {
            pageNo = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = PageSize.SIZE15.getSize();
        }
        int offset = (pageNo - 1) * pageSize;
        return this.enterpriseReportInfoMapper.selectLatestByEnterprise(enterpriseId, offset, pageSize);
    }

    @Override
    public List<EnterpriseReportInfo> findHistoryByEnterpriseAndPeriod(String enterpriseId, Long periodId) {
        return this.enterpriseReportInfoMapper.selectHistoryByEnterpriseAndPeriod(enterpriseId, periodId);
    }
}

package com.yunnancommon.service.impl;

import com.yunnancommon.entity.query.SimplePage;
import com.yunnancommon.enums.PageSize;
import com.yunnancommon.entity.vo.PaginationResultVO;
import com.yunnancommon.entity.po.ReportAuditHistory;
import com.yunnancommon.entity.query.ReportAuditHistoryQuery;
import com.yunnancommon.mapper.ReportAuditHistoryMapper;
import com.yunnancommon.service.ReportAuditHistoryService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 审核历史表 ServiceImpl
 * 
 * 实现思路（老手的标准套路）：
 * 1. 注入 Mapper
 * 2. 实现接口的所有方法
 * 3. 每个方法调用对应的 Mapper 方法
 * 4. 分页查询需要计算分页参数
 * 
 * @author group2
 * @date 2025-01-27
 */
@Service("reportAuditHistoryService")
public class ReportAuditHistoryServiceImpl implements ReportAuditHistoryService {
    
    @Resource
    private ReportAuditHistoryMapper<ReportAuditHistory, ReportAuditHistoryQuery> reportAuditHistoryMapper;
    
    /**
     * 根据条件查询列表
     * 
     * 思路：直接调用 Mapper 的 selectList 方法
     */
    @Override
    public List<ReportAuditHistory> findListByParam(ReportAuditHistoryQuery query) {
        return this.reportAuditHistoryMapper.selectList(query);
    }
    
    /**
     * 根据条件查询数量
     */
    @Override
    public Integer findCountByParam(ReportAuditHistoryQuery query) {
        return this.reportAuditHistoryMapper.selectCount(query);
    }
    
    /**
     * 分页查询
     * 
     * 思路：
     * 1. 先查总数
     * 2. 创建SimplePage对象（计算偏移量）
     * 3. 查询当前页数据
     * 4. 封装成PaginationResultVO返回
     */
    @Override
    public PaginationResultVO<ReportAuditHistory> findListByPage(ReportAuditHistoryQuery query) {
        // 查询总数
        Integer count = this.findCountByParam(query);
        
        // 获取每页大小（没有设置则用默认值15）
        Integer pageSize = query.getPageSize() == null ? PageSize.SIZE15.getSize() : query.getPageSize();
        
        // 创建分页对象（会自动计算偏移量）
        SimplePage page = new SimplePage(query.getPageNo(), count, pageSize);
        query.setSimplePage(page);
        
        // 查询当前页数据
        List<ReportAuditHistory> list = this.findListByParam(query);
        
        // 封装返回结果
        PaginationResultVO<ReportAuditHistory> result = new PaginationResultVO<>(
            count, 
            page.getPageSize(), 
            page.getPageNo(), 
            page.getPageTotal(), 
            list
        );
        return result;
    }
    
    /**
     * 新增
     * 
     * 思路：调用 Mapper 的 insert 方法
     * 注意：useGeneratedKeys=true 会自动回填 auditId
     */
    @Override
    public Integer add(ReportAuditHistory bean) {
        return this.reportAuditHistoryMapper.insert(bean);
    }
    
    /**
     * 批量新增
     */
    @Override
    public Integer addBatch(List<ReportAuditHistory> listBean) {
        if (listBean == null || listBean.isEmpty()) {
            return 0;
        }
        return this.reportAuditHistoryMapper.insertBatch(listBean);
    }
    
    /**
     * 批量新增或修改
     */
    @Override
    public Integer addOrUpdateBatch(List<ReportAuditHistory> listBean) {
        if (listBean == null || listBean.isEmpty()) {
            return 0;
        }
        return this.reportAuditHistoryMapper.insertOrUpdateBatch(listBean);
    }
    
    /**
     * 根据AuditId查询
     */
    @Override
    public ReportAuditHistory getReportAuditHistoryByAuditId(Long auditId) {
        return this.reportAuditHistoryMapper.selectByAuditId(auditId);
    }
    
    /**
     * 根据AuditId更新
     */
    @Override
    public Integer updateReportAuditHistoryByAuditId(ReportAuditHistory bean, Long auditId) {
        return this.reportAuditHistoryMapper.updateByAuditId(bean, auditId);
    }
    
    /**
     * 根据AuditId删除
     */
    @Override
    public Integer deleteReportAuditHistoryByAuditId(Long auditId) {
        return this.reportAuditHistoryMapper.deleteByAuditId(auditId);
    }
}
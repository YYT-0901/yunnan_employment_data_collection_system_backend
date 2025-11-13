package com.yunnancommon.service;

import com.yunnancommon.entity.vo.PaginationResultVO;
import com.yunnancommon.entity.po.ReportAuditHistory;
import com.yunnancommon.entity.query.ReportAuditHistoryQuery;

import java.util.List;

public interface ReportAuditHistoryService {
    
    /**
     * 根据条件查询列表
     * 
     * 使用场景：
     * - 企业查看自己的审核历史
     * - 市级/省级查看审核记录
     */
    List<ReportAuditHistory> findListByParam(ReportAuditHistoryQuery query);
    
    /**
     * 根据条件查询数量
     */
    Integer findCountByParam(ReportAuditHistoryQuery query);
    
    /**
     * 分页查询
     */
    PaginationResultVO<ReportAuditHistory> findListByPage(ReportAuditHistoryQuery query);
    
    /**
     * 新增审核记录
     * 
     * 使用场景：
     * - 市级/省级审核时插入记录
     */
    Integer add(ReportAuditHistory bean);
    
    /**
     * 批量新增
     */
    Integer addBatch(List<ReportAuditHistory> listBean);
    
    /**
     * 批量新增或修改
     */
    Integer addOrUpdateBatch(List<ReportAuditHistory> listBean);
    
    /**
     * 根据AuditId查询
     */
    ReportAuditHistory getReportAuditHistoryByAuditId(Long auditId);
    
    /**
     * 根据AuditId更新
     */
    Integer updateReportAuditHistoryByAuditId(ReportAuditHistory bean, Long auditId);
    
    /**
     * 根据AuditId删除
     */
    Integer deleteReportAuditHistoryByAuditId(Long auditId);
}
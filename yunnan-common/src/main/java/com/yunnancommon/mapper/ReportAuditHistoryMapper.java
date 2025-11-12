package com.yunnancommon.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 审核历史表 Mapper
 * 
 * 设计思路：
 * 1. 继承BaseMapper获得通用的CRUD方法
 * 2. 定义特定的查询方法（如根据auditId查询）
 * 
 * @author group2
 * @date 2025-01-27
 */
@Mapper
public interface ReportAuditHistoryMapper<T, P> extends BaseMapper {
    
    /**
     * 根据AuditId查询
     */
    T selectByAuditId(@Param("auditId") Long auditId);
    
    /**
     * 根据AuditId更新
     */
    Integer updateByAuditId(@Param("bean") T t, @Param("auditId") Long auditId);
    
    /**
     * 根据AuditId删除
     */
    Integer deleteByAuditId(@Param("auditId") Long auditId);
}
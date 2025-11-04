package com.yunnancommon.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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
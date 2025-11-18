package com.yunnancommon.entity.vo;

import com.yunnancommon.entity.dto.NoticeInfoDto;
import lombok.Data;

import java.util.List;

@Data
public class CurrentVO {
    private List<NoticeInfoDto> noticeInfoList;
    private Integer noReadCount;
    private Integer auditEnterpriseCount;
    private Integer auditDataCount;
    private Integer uploadDataCount;
}

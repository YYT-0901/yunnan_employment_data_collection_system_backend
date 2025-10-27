package com.yunnancommon.controller;

import com.yunnancommon.enums.ResponseCodeEnum;
import com.yunnancommon.entity.vo.ResponseVO;
public class ABaseController {
    protected static final String STATUC_SUCCESS = "success";
    protected static final String STATUC_ERROR = "error";

    protected <T> ResponseVO getSuccessResponseVO(T t) {
        ResponseVO<T> responseVO = new ResponseVO<T>();
        responseVO.setStatus(STATUC_SUCCESS);
        responseVO.setCode(ResponseCodeEnum.CODE_200.getCode());
        responseVO.setInfo(ResponseCodeEnum.CODE_200.getMsg());
        responseVO.setData(t);
        return responseVO;
    }

    protected <T> ResponseVO getErrorResponseVO(T t) {
        ResponseVO<T> responseVO = new ResponseVO<T>();
        responseVO.setStatus(STATUC_ERROR);
        responseVO.setCode(ResponseCodeEnum.CODE_400.getCode());
        responseVO.setInfo(ResponseCodeEnum.CODE_400.getMsg());
        responseVO.setData(t);
        return responseVO;
    }
}

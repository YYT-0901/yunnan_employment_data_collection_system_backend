package com.yunnanprovince.exception;

import com.yunnancommon.controller.ABaseController;
import com.yunnancommon.entity.vo.ResponseVO;
import com.yunnancommon.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * 全局异常处理器
 * 用于统一处理参数校验失败、业务异常等
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ABaseController {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理参数校验失败异常 (@Valid 注解触发)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseVO handleValidationException(MethodArgumentNotValidException ex) {
        // 提取所有字段的校验错误信息
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        
        logger.warn("参数校验失败: {}", errorMessage);
        return getErrorResponseVO(errorMessage);
    }

    /**
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseVO handleBusinessException(BusinessException ex) {
        logger.warn("业务异常: {}", ex.getMessage());
        return getErrorResponseVO(ex.getMessage());
    }

    /**
     * 处理其他未捕获的异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseVO handleGenericException(Exception ex) {
        logger.error("系统异常", ex);
        // 生产环境不暴露详细错误信息
        return getErrorResponseVO("系统内部错误，请联系管理员");
    }
}
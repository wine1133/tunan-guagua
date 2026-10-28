package com.tunan.guagua.common.exception;

import com.tunan.guagua.common.api.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ApiResponse<Void> handleBusiness(BusinessException exception) {
        return ApiResponse.fail(exception.getCode(), exception.getMessage());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public ApiResponse<Void> handleValidation(Exception exception) {
        String message = "请求参数不正确";
        if (exception instanceof MethodArgumentNotValidException validException
                && validException.getBindingResult().getFieldError() != null) {
            message = validException.getBindingResult().getFieldError().getDefaultMessage();
        } else if (exception instanceof BindException bindException
                && bindException.getBindingResult().getFieldError() != null) {
            message = bindException.getBindingResult().getFieldError().getDefaultMessage();
        }
        return ApiResponse.fail(400, message);
    }

    @ExceptionHandler(Exception.class)
    public ApiResponse<Void> handleException(Exception exception) {
        log.error("Unhandled request exception", exception);
        return ApiResponse.fail(500, "服务开小差了，请稍后重试");
    }
}

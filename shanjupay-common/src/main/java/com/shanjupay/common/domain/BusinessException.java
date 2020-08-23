package com.shanjupay.common.domain;

/**
 * @author : xsh
 * @create : 2020-08-24 - 1:10
 * @describe: 自定义异常类型
 */
public class BusinessException extends RuntimeException {
    //错误代码
    private ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        super();
        this.errorCode = errorCode;
    }

    public BusinessException() {
        super();
    }

    public void setErrorCode(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
